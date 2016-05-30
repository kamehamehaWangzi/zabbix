package org.pbccrc.platform.project.biz.impl;
		
import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.pbccrc.platform.cmdb.dao.MonitorDataDao;
import org.pbccrc.platform.cmdb.dao.TaskDao;
import org.pbccrc.platform.cmdb.dao.TaskDataDao;
import org.pbccrc.platform.model.GraphModel;
import org.pbccrc.platform.model.Series;
import org.pbccrc.platform.model.ZabbixDataModel;
import org.pbccrc.platform.project.biz.IMonitorDataBiz;
import org.pbccrc.platform.util.ZabbixDataUtil;
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
	private ZabbixDataUtil zabbixDataUtil;
	
	public String loadHostMonitorData(String taskDataId) {
		//查询显示表格主机
		TaskDataVO taskVO = taskDataDao.queryByTaskDataId(taskDataId);
		TaskVO task = taskDao.queryByTaskId(taskVO.getTaskId().toString());
		JSONArray hostArray = JSON.parseArray(task.getHosts());
		
		//生成一个待压缩的文件夹downfile，待返回链接供前端下载
		String basePath = request.getContextPath();
		basePath = basePath+"\\"+taskVO.getTaskId();
		
		//循环主机，每个主机生成一个文件夹downfile/hostIP
		if (hostArray != null && !hostArray.isEmpty()) {
			Map<String, String> paramMap =  new HashMap<String,String>();
			paramMap.put("taskDataId", taskDataId);
			for(int i = 0; i < hostArray.size(); i++){
				paramMap.put("hostId", hostArray.getString(i));
				List<MonitorDataVO> resultData = monitorDataDao.selectMonitorDataList(paramMap);
				//每个Item一个图,1按照固定Item处理，每次item都得取循环一次，2循环Item处理
				
				for(MonitorDataVO vo : resultData){
					System.out.println(vo.getMonitorType()+" -> "+vo.getItemName());
					//从文件系统取数据
					File dataFile = new File(vo.getPath());
					ZabbixDataModel dataModel = null;
					try {
						dataModel = zabbixDataUtil.loadZabbixData(dataFile);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					if(dataModel==null) continue;
					
					GraphModel graphModel = dataModel.getGraph();
//					private List<String> legend;
//					private List<String> xAxis;
//					private List<Series> yAxis;
					
					//JFreeCharts生成图片
					String series1 = "First"; 
					String series2 = "Second"; 
					String series3 = "Third"; 
					String type1 = "Type 1"; 
					String type2 = "Type 2"; 
					String type3 = "Type 3"; 
					String type4 = "Type 4"; 
					String type5 = "Type 5"; 
					String type6 = "Type 6"; 
					String type7 = "Type 7"; 
					String type8 = "Type 8"; 
					DefaultCategoryDataset defaultcategorydataset = new DefaultCategoryDataset(); 
					defaultcategorydataset.addValue(1.0D, series1, type1); 
					defaultcategorydataset.addValue(4D, series1, type2); 
					defaultcategorydataset.addValue(3D, series1, type3); 
					defaultcategorydataset.addValue(5D, series1, type4); 
					defaultcategorydataset.addValue(5D, series1, type5); 
					defaultcategorydataset.addValue(7D, series1, type6); 
					defaultcategorydataset.addValue(7D, series1, type7); 
					defaultcategorydataset.addValue(8D, series1, type8); 

					defaultcategorydataset.addValue(5D, series2, type1); 
					defaultcategorydataset.addValue(7D, series2, type2); 
					defaultcategorydataset.addValue(6D, series2, type3); 
					defaultcategorydataset.addValue(8D, series2, type4); 
					defaultcategorydataset.addValue(4D, series2, type5); 
					defaultcategorydataset.addValue(4D, series2, type6); 
					defaultcategorydataset.addValue(2D, series2, type7); 
					defaultcategorydataset.addValue(1.0D, series2, type8); 

					defaultcategorydataset.addValue(4D, series3, type1); 
					defaultcategorydataset.addValue(3D, series3, type2); 
					defaultcategorydataset.addValue(2D, series3, type3); 
					defaultcategorydataset.addValue(3D, series3, type4); 
					defaultcategorydataset.addValue(6D, series3, type5); 
					defaultcategorydataset.addValue(3D, series3, type6); 
					defaultcategorydataset.addValue(4D, series3, type7); 
					defaultcategorydataset.addValue(3D, series3, type8); 
					
					JFreeChart jfreechart = ChartFactory.createLineChart("Line Chart Demo 1", 
							"Type", 
							"Value", 
							defaultcategorydataset, 
							PlotOrientation.VERTICAL, 
							true, 
							true, 
							false); 
							jfreechart.setBackgroundPaint(Color.white); 
							CategoryPlot categoryplot = (CategoryPlot)jfreechart.getPlot(); 
							categoryplot.setBackgroundPaint(Color.lightGray); 
							categoryplot.setRangeGridlinePaint(Color.white); 
							NumberAxis numberaxis = (NumberAxis)categoryplot.getRangeAxis(); 
							numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); 
							numberaxis.setAutoRangeIncludesZero(true); 
					
				}
				
				System.out.println();
			}
		}
				//压缩downfile，生成downfile.zip，返回地址链接给前端
		return "";
	}

}
