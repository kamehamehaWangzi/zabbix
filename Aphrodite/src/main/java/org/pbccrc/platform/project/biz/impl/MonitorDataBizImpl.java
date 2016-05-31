package org.pbccrc.platform.project.biz.impl;

import java.awt.Color;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.LegendGraphic;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.general.DatasetUtilities;
import org.pbccrc.platform.cmdb.dao.HostDao;
import org.pbccrc.platform.cmdb.dao.MonitorDataDao;
import org.pbccrc.platform.cmdb.dao.TaskDao;
import org.pbccrc.platform.cmdb.dao.TaskDataDao;
import org.pbccrc.platform.model.GraphModel;
import org.pbccrc.platform.model.Series;
import org.pbccrc.platform.model.ZabbixDataModel;
import org.pbccrc.platform.project.biz.IMonitorDataBiz;
import org.pbccrc.platform.util.Constant;
import org.pbccrc.platform.util.ZabbixDataUtil;
import org.pbccrc.platform.util.ZipUtil;
import org.pbccrc.platform.vo.HostAppVO;
import org.pbccrc.platform.vo.HostVO;
import org.pbccrc.platform.vo.MonitorDataVO;
import org.pbccrc.platform.vo.TaskDataVO;
import org.pbccrc.platform.vo.TaskVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

@Service
public class MonitorDataBizImpl implements IMonitorDataBiz {

	@Autowired
	private TaskDataDao taskDataDao;

	@Autowired
	private TaskDao taskDao;

	@Autowired
	private MonitorDataDao monitorDataDao;

	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private HostDao hostDao;

	@Autowired
	private ZabbixDataUtil zabbixDataUtil;

	public String loadHostMonitorData(String taskDataId) {
		// 查询显示表格主机
		TaskDataVO taskVO = taskDataDao.queryByTaskDataId(taskDataId);
		TaskVO task = taskDao.queryByTaskId(taskVO.getTaskId().toString());
		JSONArray hostArray = JSON.parseArray(task.getHosts());

		// 生成一个待压缩的文件夹downfile，待返回链接供前端下载
		String basePath = request.getSession().getServletContext().getRealPath(Constant.ZABBIX_MONITOR_DATA_DOWNLOAD_PATH);
		basePath = basePath + "\\taskData_" + taskDataId;

		// 循环主机，每个主机生成一个文件夹downfile/hostIP
		if (hostArray != null && !hostArray.isEmpty()) {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("taskDataId", taskDataId);
			for (int i = 0; i < hostArray.size(); i++) {
				paramMap.put("hostId", hostArray.getString(i));
				List<MonitorDataVO> resultData = monitorDataDao.selectMonitorDataList(paramMap);
				// 每个Item一个图,1按照固定Item处理，每次item都得取循环一次，2循环Item处理
				HostVO host = hostDao.queryById(hostArray.getString(i));
				String path = basePath + "\\" + host.getIp1();
				
				for (MonitorDataVO vo : resultData) {
					System.out.println(vo.getMonitorType() + " -> " + vo.getItemName());
					
					// 从文件系统取数据
					File dataFile = new File(vo.getPath());
					ZabbixDataModel dataModel = null;
					try {
						dataModel = zabbixDataUtil.loadZabbixData(dataFile);
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (dataModel == null)
						continue;

					// JFreeCharts生成图片,新建数据源
					CategoryDataset defaultcategorydataset = new DefaultCategoryDataset();
					// 数据源赋值
					GraphModel graphModel = dataModel.getGraph();
					List<String> legend = graphModel.getLegend();
					String[] legendStr = legend.toArray(new String[legend.size()]);
					List<String> xaxis = graphModel.getxAxis();
					String[] xaxisStr = xaxis.toArray(new String[xaxis.size()]);
					List<Series> yaxis = graphModel.getyAxis();
					double[][] data = new double[yaxis.size()][yaxis.get(0).getData().size()];
					for (int yindex = 0; yindex < yaxis.size(); yindex++) {
						for (int dataIndex = 0; dataIndex < yaxis.get(yindex).getData().size(); dataIndex++) {
							data[yindex][dataIndex] = Double.parseDouble(yaxis.get(yindex).getData().get(dataIndex));
						}
					}
					//监控时间轴，最好不要以秒为单位，因为X可能出现相同的X值，报错……
					for(int t = 1; t<xaxisStr.length ;t++){
							xaxisStr[t] = xaxisStr[t]+(t-1);
					}
					defaultcategorydataset = DatasetUtilities.createCategoryDataset(legendStr, xaxisStr, data);

					String range = xaxisStr!=null&&xaxisStr.length>0 ? xaxisStr[0]+" - "+xaxisStr[xaxisStr.length-1] : "-";
					JFreeChart jfreechart = ChartFactory.createLineChart(
							vo.getMonitorType() + " -> " + vo.getItemName(),range , "Value", defaultcategorydataset,
							PlotOrientation.VERTICAL, true, true, false);
					jfreechart.setBackgroundPaint(Color.white);
					
					CategoryPlot plot = (CategoryPlot) jfreechart.getPlot();
					plot.setBackgroundPaint(Color.white);
					plot.setDomainGridlinesVisible(true);  //设置背景网格线是否可见
					plot.setDomainGridlinePaint(Color.BLACK); //设置背景网格线颜色
					plot.setRangeGridlinePaint(Color.GRAY);
					plot.setNoDataMessage("没有数据");//没有数据时显示的文字说明。 

					 // 数据轴属性部分
                    NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
                    rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
                    rangeAxis.setAutoRangeIncludesZero(true); //自动生成
                    rangeAxis.setUpperMargin(0.20);
                    rangeAxis.setLabelAngle(Math.PI / 2.0); 
                    rangeAxis.setAutoRange(true);
                    
                    // 数据渲染部分 主要是对折线做操作
                    LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
                    renderer.setBaseItemLabelsVisible(true);
                    renderer.setSeriesPaint(0, Color.black);    //设置折线的颜色
                    renderer.setBaseShapesFilled(true);
                    renderer.setBaseItemLabelsVisible(true);     
                    renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());  
					// x轴转向45度
					plot.setBackgroundPaint(Color.WHITE);
					plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
					// 渲染 曲线上有点的效果
					LineAndShapeRenderer lsr = new LineAndShapeRenderer();
					// 设置消除字体的锯齿渲染(解决中文问题)
					jfreechart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
					plot.setRenderer(lsr);
					
					try {
						String filePath = path + "\\" + vo.getMonitorType() + "\\";
						File file = new File(filePath);
						if(!file.exists()){
							file.mkdirs();
						}
						ChartUtilities.saveChartAsPNG(new File(filePath+vo.getItemName()+".png"), jfreechart, 1200, 800);
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}

			System.out.println();
		}
		// 压缩downfile，生成downfile.zip，返回地址链接给前端
		File zipSourceFile = new File(basePath);
		String zipEndFile = zipSourceFile.getParentFile().getAbsolutePath()+"//taskData_" + taskDataId+".zip";
//		ZipOutputStream zipOutput = new ZipOutputStream(zipEndFile);
		ZipUtil.zipFile(basePath, zipSourceFile.getParentFile().getAbsolutePath()+"//taskData_" + taskDataId+".zip");
		return "taskData_" + taskDataId+".zip";
	}

}
