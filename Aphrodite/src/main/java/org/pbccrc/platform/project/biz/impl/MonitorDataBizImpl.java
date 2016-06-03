package org.pbccrc.platform.project.biz.impl;
		
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
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
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.pbccrc.platform.cmdb.dao.MonitorDataDao;
import org.pbccrc.platform.cmdb.dao.TaskDao;
import org.pbccrc.platform.cmdb.dao.TaskDataDao;
import org.pbccrc.platform.model.GraphModel;
import org.pbccrc.platform.model.ZabbixDataModel;
import org.pbccrc.platform.project.biz.IMonitorDataBiz;
import org.pbccrc.platform.project.biz.ITaskDataBiz;
import org.pbccrc.platform.util.ZabbixDataUtil;
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
	private ITaskDataBiz taskDataBiz;
	
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
	
	@SuppressWarnings("deprecation")
	public String export2Excel(String taskDataId, String path){
		
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
		try {
			path = path + "\\" + taskDataId + "\\";    											
			File file = new File(path);
			if(!file.exists()){
				try{
					file.mkdirs();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			exportPath = path + System.currentTimeMillis() + ".xlsx";
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
		
		return exportPath;
	}

}
