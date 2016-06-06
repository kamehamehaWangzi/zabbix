package org.pbccrc.platform.project.biz.impl;
		
import java.awt.Color;
import java.awt.RenderingHints;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.pbccrc.platform.cmdb.dao.HostDao;
import org.pbccrc.platform.cmdb.dao.MonitorDataDao;
import org.pbccrc.platform.cmdb.dao.TaskDao;
import org.pbccrc.platform.cmdb.dao.TaskDataDao;
import org.pbccrc.platform.model.GraphModel;
import org.pbccrc.platform.model.Series;
import org.pbccrc.platform.model.ZabbixDataModel;
import org.pbccrc.platform.project.biz.IMonitorDataBiz;
import org.pbccrc.platform.project.biz.ITaskDataBiz;
import org.pbccrc.platform.util.Constant;
import org.pbccrc.platform.util.ZabbixDataUtil;
import org.pbccrc.platform.util.ZipUtil;
import org.pbccrc.platform.vo.HostVO;
import org.pbccrc.platform.vo.MonitorDataVO;
import org.pbccrc.platform.vo.TaskDataVO;
import org.pbccrc.platform.vo.TaskVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

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
	
	@Autowired
	private HostDao hostDao;
	
	@Autowired
	private ITaskDataBiz taskDataBiz;
	
	public String loadHostMonitorData(String taskDataId) {
		// 查询显示表格主机
		TaskDataVO taskVO = taskDataDao.queryByTaskDataId(taskDataId);
		TaskVO task = taskDao.queryByTaskId(taskVO.getTaskId().toString());
		// 获得该任务所关联的主机Id集合
		JSONArray hostArray = JSON.parseArray(task.getHosts());

		// 生成一个待压缩的文件夹downfile，待返回链接供前端下载
		String basePath = request.getSession().getServletContext().getRealPath(Constant.ZABBIX_MONITOR_DATA_DOWNLOAD_PATH);
		basePath = basePath + "\\taskData_hosts_ " + taskDataId;

		if (hostArray != null && !hostArray.isEmpty()) {
			//配置查询条件，查询监控数据
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("taskDataId", taskDataId);
			
			// 循环主机，每个主机生成一个文件夹downfile/hostIP
			for (int i = 0; i < hostArray.size(); i++) {
				//按照主机id，进行查询，获取监控数据集合
				paramMap.put("hostId", hostArray.getString(i));
				List<MonitorDataVO> resultData = monitorDataDao.selectMonitorDataList(paramMap);
				
				//拼接文件存储路径
				HostVO host = hostDao.queryById(hostArray.getString(i));
				String path = basePath + "\\" + host.getIp1();
				
				// 每个Item一个图, 1. 按照固定Item处理，每次item都得取循环一次; 2.循环Item处理
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
						xaxisStr[t] = xaxisStr[t]+(t-1)%10;
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
					for(int x = 0;x<xaxisStr.length;x++){
						if(x%10 == 0){
							plot.getDomainAxis().setTickLabelPaint(xaxisStr[x],Color.black);
						}else{
							plot.getDomainAxis().setTickLabelPaint(xaxisStr[x],Color.white);
						}
					}
					
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
		String zipEndFilePath = zipSourceFile.getParentFile().getAbsolutePath()+"//taskData_host_" + taskDataId+".zip";
		ZipUtil.zipFile(basePath, zipEndFilePath);
		return "taskData_host_" + taskDataId+".zip";
	}

	@Override
	public String loadItemMonitorData(String taskDataId) {

		// 1按照任务号，在监控数据表中获取已经监控的监控项集合
		List<String> itemNameList = monitorDataDao.queryItemNameByTaskDataId(taskDataId);
		
		// 生成一个待压缩的文件夹downfile，待返回链接供前端下载
		String basePath = request.getSession().getServletContext().getRealPath(Constant.ZABBIX_MONITOR_DATA_DOWNLOAD_PATH);
		basePath = basePath + "\\taskData_items_" + taskDataId;
		
		// 配置查询条件
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("taskDataId", taskDataId);

		// 2对监控项进行循环遍历
		if (itemNameList != null && itemNameList.size() > 0) {
			// 2.1按照任务号，和当前的监控项，获取监控数据集合（主要是面向不同的主机）
			for (String itemName : itemNameList) {
				paramMap.put("itemName", itemName);
				List<MonitorDataVO> monitorList = monitorDataDao.selectMonitorDataList(paramMap);
//				String path = basePath + "\\" + itemName;
				
				try {
					// 2.2创建数据集
					GraphModel dataModel = zabbixDataUtil.loadZabbixData(new File(monitorList.get(0).getPath()))
							.getGraph();
					// 2.3循环每个监控数据（不同主机）的数据对象，封装到数据集合中
					for (int i = 1; i < monitorList.size(); i++) {
						// 获取数据对象 dataModel
						GraphModel dataModelTemp = zabbixDataUtil.loadZabbixData(new File(monitorList.get(i).getPath()))
								.getGraph();
						if (dataModelTemp == null)
							continue;
						dataModel.getLegend().addAll(dataModelTemp.getLegend());
						dataModel.getyAxis().addAll(dataModelTemp.getyAxis());

					}
					// 数据源赋值
					CategoryDataset defaultcategorydataset = new DefaultCategoryDataset();
					
					List<String> legend = dataModel.getLegend();
					
					String[] legendStr = legend.toArray(new String[legend.size()]);
					List<String> xaxis = dataModel.getxAxis();
					String[] xaxisStr = xaxis.toArray(new String[xaxis.size()]);
					List<Series> yaxis = dataModel.getyAxis();
					int length = yaxis.get(0).getData().size();
					double[][] data = new double[yaxis.size()][length];
					for (int yindex = 0; yindex < yaxis.size(); yindex++) {
						length = length<yaxis.get(yindex).getData().size()?length:yaxis.get(yindex).getData().size();
						for (int dataIndex = 0; dataIndex < length; dataIndex++) {
							data[yindex][dataIndex] = Double.parseDouble(yaxis.get(yindex).getData().get(dataIndex));
						}
					}
					
					// 监控时间轴，最好不要以秒为单位，因为X可能出现相同的X值，报错……
					for (int t = 1; t < xaxisStr.length; t++) {
						xaxisStr[t] = xaxisStr[t] + (t-1)%10;
					}
					for (int t=1; t<legendStr.length; t++){
						legendStr[t] = legendStr[t] +"_"+ (t-1)%10;
					}
					
					// 2.4生成图片
					defaultcategorydataset = DatasetUtilities.createCategoryDataset(legendStr, xaxisStr, data);

					String range = xaxisStr!=null && xaxisStr.length>0 ? xaxisStr[0]+" - "+xaxisStr[xaxisStr.length-1] : "-";
					JFreeChart jfreechart = ChartFactory.createLineChart(
							itemName,
							range , "Value", defaultcategorydataset,
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

					for(int x = 0;x<xaxisStr.length;x++){
						if(x%10 == 0){
							plot.getDomainAxis().setTickLabelPaint(xaxisStr[x],Color.black);
						}else{
							plot.getDomainAxis().setTickLabelPaint(xaxisStr[x],Color.white);
						}
					}
					
					// 渲染 曲线上有点的效果
					LineAndShapeRenderer lsr = new LineAndShapeRenderer();
					// 设置消除字体的锯齿渲染(解决中文问题)
					jfreechart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
					plot.setRenderer(lsr);
					
					try {
						String filePath = basePath + "\\";
						File file = new File(filePath);
						if(!file.exists()){
							file.mkdirs();
						}
						ChartUtilities.saveChartAsPNG(new File(filePath+itemName
						+".png"), jfreechart, 1000, 800);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		// 3对文件打包zip 压缩downfile，生成downfile.zip，返回地址链接给前端
		File zipSourceFile = new File(basePath);
		String zipEndFilePath = zipSourceFile.getParentFile().getAbsolutePath()+"//taskData_item_" + taskDataId+".zip";
		ZipUtil.zipFile(basePath, zipEndFilePath);
		return "taskData_item_" + taskDataId+".zip";
	}
	
	@SuppressWarnings("deprecation")
	public String export2Excel(String taskDataId){
		
		String exportPath = "";
		
		String title = "导出excel数据";
		
		String[] headers1 = new String[]{
				"服务器地址", "CPU使用情况", "", "", "",
				"磁盘使用情况", "", "", "",
				"网络使用情况", "", "内存使用情况", ""};
		String[] headers2 = new String[]{
				"服务器地址", "用户进程", "系统进程", "I/O等待", "空闲状态", 
				"Disk Read KB/s", "Disk Write KB/s", "Disk Read r/s", "Disk Write w/s",
				"en0-reads kB/s", "en0-writes KB/s", "swaptotal MB", "Memfree MB"};
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(title);
		// cell width
		sheet.setDefaultColumnWidth((short) 20);
		
		// title style
		HSSFCellStyle titleStyle = workbook.createCellStyle();
		titleStyle.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
		titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		titleStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		titleStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		titleStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		titleStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		// title font
		HSSFFont titleFont = workbook.createFont();
		titleFont.setColor(HSSFColor.VIOLET.index);
		titleFont.setFontHeightInPoints((short) 12);
		titleFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		titleStyle.setFont(titleFont);
		
		// data style
		HSSFCellStyle dataStyle = workbook.createCellStyle();
		dataStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
		dataStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		dataStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		dataStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		dataStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		dataStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		dataStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		dataStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		dataStyle.setWrapText(true);
		// data font
		HSSFFont dataFont = workbook.createFont();
		dataFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		dataFont.setColor(HSSFColor.BLUE.index);
		dataStyle.setFont(dataFont);
		
		// title line
		HSSFRow row = sheet.createRow(0);
		for (short i = 0; i < headers1.length; i++) {
			HSSFCell cell = row.createCell(i);
			cell.setCellStyle(titleStyle);
			HSSFRichTextString text = new HSSFRichTextString(headers1[i]);
			cell.setCellValue(text);
		}
		
		row = sheet.createRow(1);
		for (short i = 0; i < headers2.length; i++) {
			HSSFCell cell = row.createCell(i);
			cell.setCellStyle(titleStyle);
			HSSFRichTextString text = new HSSFRichTextString(headers2[i]);
			cell.setCellValue(text);
		}
		
		// merge title
		sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 4));
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 5, 8));
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 9, 10));
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 11, 12));
		
		// data line
		JSONArray jsonArray = taskDataBiz.showTaskMonitorData(taskDataId);
		
		int index = 1;
		
		for (int i = 0; i < jsonArray.size(); i++) {
			row = sheet.createRow(++index);
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			
			JSONArray netList = jsonObject.getJSONArray("netParam");
			JSONArray diskList = jsonObject.getJSONArray("diskParam");
			
			String host = String.valueOf(jsonObject.get("hostIP"));
			String cpuUser = String.valueOf(jsonObject.get("CPU_user_time"));
			String cpuSys = String.valueOf(jsonObject.get("CPU_system_time"));
			String cpuIOWait = String.valueOf(jsonObject.get("CPU_iowait_time"));
			String cpuIde = String.valueOf(jsonObject.get("CPU_idle_time"));
			String swap = String.valueOf(jsonObject.get("Free_swap_space"));
			String memory = String.valueOf(jsonObject.get("Available_memory"));
			
			// host
			HSSFCell cell = row.createCell(0);
			cell.setCellValue(host);
			cell.setCellStyle(dataStyle);
			
			// cpu user time
			cell = row.createCell(1);
			cell.setCellValue(cpuUser);
			cell.setCellStyle(dataStyle);
			
			// cpu system time
			cell = row.createCell(2);
			cell.setCellValue(cpuSys);
			cell.setCellStyle(dataStyle);
			
			// cpu IO wait time
			cell = row.createCell(3);
			cell.setCellValue(cpuIOWait);
			cell.setCellStyle(dataStyle);
			
			// cpu ide
			cell = row.createCell(4);
			cell.setCellValue(cpuIde);
			cell.setCellStyle(dataStyle);
			
			// disk read kb/s
			cell = row.createCell(5);
			StringBuilder cellValue = new StringBuilder();
			for (int j = 0; j < diskList.size(); j++) {
				String disk = String.valueOf(diskList.get(j));
				String key = "IO_dev_read_on_" + disk;
				String value = String.valueOf(jsonObject.get(key));
				cellValue.append(disk + " : " + value);
				if (j != diskList.size() - 1) {
					cellValue.append("\n");
				}
			}
			cell.setCellValue(cellValue.toString());
			cell.setCellStyle(dataStyle);
			
			// disk write kb/s
			cell = row.createCell(6);
			cellValue = new StringBuilder();
			for (int j = 0; j < diskList.size(); j++) {
				String disk = String.valueOf(diskList.get(j));
				String key = "IO_dev_write_on_" + disk;
				String value = String.valueOf(jsonObject.get(key));
				cellValue.append(disk + " : " + value);
				if (j != diskList.size() - 1) {
					cellValue.append("\n");
				}
			}
			cell.setCellValue(cellValue.toString());
			cell.setCellStyle(dataStyle);
			
			// disk read r/s
			cell = row.createCell(7);
			cellValue = new StringBuilder();
			for (int j = 0; j < diskList.size(); j++) {
				String disk = String.valueOf(diskList.get(j));
				String key = "IO_rps_on_" + disk;
				String value = String.valueOf(jsonObject.get(key));
				cellValue.append(disk + " : " + value);
				if (j != diskList.size() - 1) {
					cellValue.append("\n");
				}
			}
			cell.setCellValue(cellValue.toString());
			cell.setCellStyle(dataStyle);
			
			// disk write w/s
			cell = row.createCell(8);
			cellValue = new StringBuilder();
			for (int j = 0; j < diskList.size(); j++) {
				String disk = String.valueOf(diskList.get(j));
				String key = "IO_wps_on_" + disk;
				String value = String.valueOf(jsonObject.get(key));
				cellValue.append(disk + " : " + value);
				if (j != diskList.size() - 1) {
					cellValue.append("\n");
				}
			}
			cell.setCellValue(cellValue.toString());
			cell.setCellStyle(dataStyle);
			
			// net read
			cell = row.createCell(9);
			cellValue = new StringBuilder();
			for (int j = 0; j < netList.size(); j++) {
				String net = String.valueOf(netList.get(j));
				String key = "Incoming_network_traffic_on_" + net;
				String value = String.valueOf(jsonObject.get(key));
				cellValue.append(net + " : " + value);
				if (j != netList.size() - 1) {
					cellValue.append("\n");
				}
			}
			cell.setCellValue(cellValue.toString());
			cell.setCellStyle(dataStyle);
			
			// net write
			cell = row.createCell(10);
			cellValue = new StringBuilder();
			for (int j = 0; j < netList.size(); j++) {
				String net = String.valueOf(netList.get(j));
				String key = "Outgoing_network_traffic_on_" + net;
				String value = String.valueOf(jsonObject.get(key));
				cellValue.append(net + " : " + value);
				if (j != netList.size() - 1) {
					cellValue.append("\n");
				}
			}
			cell.setCellValue(cellValue.toString());
			cell.setCellStyle(dataStyle);
			
			// swap
			cell = row.createCell(11);
			cell.setCellValue(swap);
			cell.setCellStyle(dataStyle);
			
			// memory
			cell = row.createCell(12);
			cell.setCellValue(memory);
			cell.setCellStyle(dataStyle);
		}
		
		// IO operation
		OutputStream os = null;
		// 生成一个待压缩的文件夹downfile，待返回链接供前端下载
		String basePath = request.getSession().getServletContext().getRealPath(Constant.ZABBIX_MONITOR_DATA_EXPORT_PATH);
		basePath = basePath + "\\taskData_hosts_ " + taskDataId;
		try {
			File file = new File(basePath);
			if(!file.exists()){
				try{
					file.mkdirs();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			exportPath = basePath + System.currentTimeMillis() + ".xlsx";
			os = new FileOutputStream(exportPath);
			workbook.write(os);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		// 3对文件打包zip 压缩downfile，生成downfile.zip，返回地址链接给前端
		File zipSourceFile = new File(exportPath);
		String zipEndFilePath = zipSourceFile.getParentFile().getAbsolutePath()+"//taskData_excel_" + taskDataId+".zip";
		ZipUtil.zipFile(exportPath, zipEndFilePath);
		return "taskData_excel_" + taskDataId+".zip";
	}

}