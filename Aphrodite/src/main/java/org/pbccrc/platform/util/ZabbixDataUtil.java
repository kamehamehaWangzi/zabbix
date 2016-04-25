package org.pbccrc.platform.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pbccrc.platform.api.ApiFactory;
import org.pbccrc.platform.api.zabbix.Request;
import org.pbccrc.platform.api.zabbix.RequestBuilder;
import org.pbccrc.platform.cmdb.dao.HostDao;
import org.pbccrc.platform.cmdb.dao.TaskDao;
import org.pbccrc.platform.model.GraphModel;
import org.pbccrc.platform.model.Series;
import org.pbccrc.platform.model.ZabbixDataModel;
import org.pbccrc.platform.monitor.biz.IGraphBiz;
import org.pbccrc.platform.vo.HostVO;
import org.pbccrc.platform.vo.TaskDataVO;
import org.pbccrc.platform.vo.TaskVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Service
public class ZabbixDataUtil {
	
	@Autowired
	TaskDao taskDao;
	
	@Autowired
	HostDao hostDao;
	
	@Autowired
	IGraphBiz graphBiz;
	
	@Autowired
	ApiFactory apiFactory;
	
	public static final String TYPE_CPU = "CPU";
	public static final String TYPE_DISK = "DISK";
	public static final String TYPE_NET = "NET";
	public static final String TYPE_MEMORY = "MEMORY";
	
	/**
	 * CPU 
	 */
	private static enum CPU {
		
		CPU_USER_TIME("system.cpu.util[,user]"), 
		CPU_SYSTEM_TIME("system.cpu.util[,system]"), 
		CPU_IOWAIT_TIME("system.cpu.util[,iowait]"), 
		CPU_IDLE_TIME("system.cpu.util[,idle]");
		
		private String value;

		public String getValue() {
			return this.value;
		}

		private CPU(String value) {
			this.value = value;
		}
	}
	
	/**
	 * 磁盘读写
	 */
	private static enum DISK_IO {
		
		VFS_DEV_READ("vfs.dev.read[,bytes]"), 
		VFS_DEV_WRITE("vfs.dev.write[,bytes]");
		
		private String value;

		public String getValue() {
			return this.value;
		}

		private DISK_IO(String value) {
			this.value = value;
		}
	}
	
	/**
	 * 磁盘大小 
	 */
	private static enum DISK_SIZE {
		
		DISK_SIZE_FREE("vfs.fs.size[/,free]"), 
		DISK_SIZE_TOTAL("vfs.fs.size[/,total]");
		
		private String value;

		public String getValue() {
			return this.value;
		}

		private DISK_SIZE(String value) {
			this.value = value;
		}
	}
	
	/**
	 * 网卡
	 * in : 网卡入口流量
	 * out: 网卡出口流量 
	 */
	private static enum NET {
		
		NET_IN("net.if.in"), 
		NET_OUT("net.if.out");
		
		private String value;

		public String getValue() {
			return this.value;
		}

		private NET(String value) {
			this.value = value;
		}
	}
	
	/**
	 * 内存
	 * total: 总内存
	 * available: 可用内存
	 * used: 已用内存 
	 */
	private static enum MEMORY {
		
		MEMORY_TOTAL("vm.memory.size[total]"), 
		MEMORY_AVAILABLE("vm.memory.size[available]"), 
		MEMORY_USED("vm.memory.size[used]");
		
		private String value;

		public String getValue() {
			return this.value;
		}

		private MEMORY(String value) {
			this.value = value;
		}
	}
	
	/**
	 * 交换分区
	 * in:磁盘交换到内存
	 * out:内存到磁盘 
	 */
	private static enum SWAP {
		
		SWAP_IN("system.swap.in"), 
		SWAP_OUT("system.swap.out");
		
		private String value;

		public String getValue() {
			return this.value;
		}

		private SWAP(String value) {
			this.value = value;
		}
	}
	
	public Map<String, List<ZabbixDataModel>> getZabbixDataMap(TaskDataVO vo) {
		
		Map<String, List<ZabbixDataModel>> map = new HashMap<String, List<ZabbixDataModel>>();
		
		List<ZabbixDataModel> cpulist = getZabbixData(vo, TYPE_CPU);
		List<ZabbixDataModel> disklist = getZabbixData(vo, TYPE_DISK);
		List<ZabbixDataModel> netlist = getZabbixData(vo, TYPE_NET);
		List<ZabbixDataModel> memorylist = getZabbixData(vo, TYPE_MEMORY);
		
		map.put(TYPE_CPU, cpulist);
		map.put(TYPE_DISK, disklist);
		map.put(TYPE_NET, netlist);
		map.put(TYPE_MEMORY, memorylist);
		
		return map;
	} 
	
	// get zabbixData by parameter
	public JSONArray getZabbixDataByType(String type, JSONArray keyItems){
		
		JSONArray jsonArray = new JSONArray();
		
		if(keyItems != null && !keyItems.isEmpty()) {
			
			for(int i = 0; i < keyItems.size(); i++) {
				
				JSONObject keyItem = keyItems.getJSONObject(i);
				String key = keyItem.getString("key_");
				
				switch (type) {
					case TYPE_CPU:
						
						if (CPU.CPU_USER_TIME.getValue().equals(key) ||
							CPU.CPU_SYSTEM_TIME.getValue().equals(key) ||
							CPU.CPU_IOWAIT_TIME.getValue().equals(key) ||
							CPU.CPU_IDLE_TIME.getValue().equals(key) ) {
							jsonArray.add(keyItem);
						}
						break;
						
					case TYPE_DISK:
						
						if (DISK_IO.VFS_DEV_READ.getValue().equals(key) ||
							DISK_IO.VFS_DEV_WRITE.getValue().equals(key) ||
							DISK_SIZE.DISK_SIZE_FREE.getValue().equals(key) ||
							DISK_SIZE.DISK_SIZE_TOTAL.getValue().equals(key) ) {
							jsonArray.add(keyItem);
						}
						break;
			
					case TYPE_NET:
						
						if (NET.NET_IN.getValue().equals(key) ||
							NET.NET_OUT.getValue().equals(key) ) {
							jsonArray.add(keyItem);
						}
						break;
			
					case TYPE_MEMORY:
						
						if (MEMORY.MEMORY_TOTAL.getValue().equals(key) ||
							MEMORY.MEMORY_AVAILABLE.getValue().equals(key) ||
							MEMORY.MEMORY_USED.getValue().equals(key) ||
							SWAP.SWAP_IN.getValue().equals(key) ||
							SWAP.SWAP_OUT.getValue().equals(key) ) {
							jsonArray.add(keyItem);
						}
						break;
			
					default:
						break;
				}
			}
		}
		
		return jsonArray;
	}
	
	// getKeyItems by zabbix hostID
	public JSONArray getKeyItems(Integer zabbixHostId){
		
		Request request = RequestBuilder.newBuilder()
				.paramEntry("output", "extend")
				.paramEntry("hostids", zabbixHostId)
				.paramEntry("sortfield", "name")
				.method("item.get")
				.build();
		JSONObject returnObj = apiFactory.zabbix().call(request);
		
		JSONArray keyItems = new JSONArray();
		if(returnObj != null && !returnObj.isEmpty() && !returnObj.getJSONArray("result").isEmpty()) {
			keyItems.addAll(returnObj.getJSONArray("result"));
		}
		
		return keyItems;
	}
	
	
	// get zabbixData
	public List<ZabbixDataModel> getZabbixData(TaskDataVO vo, String type){
		
		List<ZabbixDataModel> list = new ArrayList<ZabbixDataModel>();
		
		// defalut param
		Integer defaultDateRange = 1;
		Integer scaler = null;
		String graphType = Constant.GRAPH_TYPE.area.getValue();
		
		TaskVO task = taskDao.queryByTaskId(vo.getTaskId().toString());
		
		JSONArray hosts = JSON.parseArray(task.getHosts());
		
		if(hosts != null && !hosts.isEmpty()) {
		
			// loop host
			for(int i = 0; i < hosts.size(); i++) {
				
				ZabbixDataModel zabbixData = new ZabbixDataModel();
				
				HostVO host = hostDao.queryById(hosts.getString(i));
				
				JSONArray keyItems = getKeyItems(host.getZabbixHostid());
				keyItems = getZabbixDataByType(type, keyItems);
				
				zabbixData.setHostId(host.getId());
				
				// init data
				GraphModel graphData = new GraphModel();
				List<String> legend = new ArrayList<String>(); 
				List<String> xAxis = new ArrayList<String>(); 
				List<Series> yAxis = new ArrayList<Series>(); 
				
				List<GraphModel> graphList = new ArrayList<GraphModel>();
				
				JSONObject historyX = null;
				if(keyItems != null && !keyItems.isEmpty()) {
					
					//获取Y轴的值
					for(int j=0; j<keyItems.size(); j++) {
						
						JSONObject itemY = keyItems.getJSONObject(j);
						Integer valueType = itemY.getInteger("value_type");
						
						JSONObject historyY = graphBiz.queryHistoryByItem(itemY.getString("itemid"), valueType, vo.getStartTime(), vo.getEndTime(), defaultDateRange);
						historyX = historyY;
						
						//得到数据结果集
						JSONArray resultY = historyY.getJSONArray("result");
						
						Series series = new Series();
						
						//将result结果集合中的value数据存到values链表中
						List<String> values = new ArrayList<String>();
						for(int m=0; m<resultY.size(); m++) {
							String value_key = resultY.getJSONObject(m).containsKey("value_avg") ? "value_avg" : "value";
							String value = null;
							if(scaler != null) {
								value = String.valueOf(resultY.getJSONObject(m).getDouble(value_key) / scaler);
							} else {
								value = String.valueOf(resultY.getJSONObject(m).get(value_key));
							}
							values.add(value);
						}
						
						String itemName = itemY.getString("name");
						
						//itemName的格式化转换
						if(itemName.indexOf("$") != -1) {
							Pattern pattern = Pattern.compile("\\[.*\\]");
							Matcher match = pattern.matcher(itemY.getString("key_"));
							if(match.find()) {
								String[] matchStr = match.group().replaceAll("\\[|\\]", "").split(",");
								
								Pattern patternIndex = Pattern.compile("\\$.d*");
								Matcher matchIndex = patternIndex.matcher(itemName);
								
								if(matchIndex.find()) {
									Integer index = Integer.parseInt(matchIndex.group().replaceAll("\\$", "").trim());
									itemName = itemName.replaceAll("\\$" + index, matchStr[index - 1]);
								}
							}
						}
						
						legend.add(itemName);
						series.setName(itemName);
						
						// echarts's area == line
						if(Constant.GRAPH_TYPE.area.getValue().equals(graphType)) {
							series.setType(Constant.GRAPH_TYPE.line.getValue());
							series.setItemStyle(JSONObject.parseObject("{normal: {areaStyle: {type: 'default'}}}"));
						} else {
							series.setType(graphType);
							series.setItemStyle(JSONObject.parseObject("{normal: {lineStyle: {type: 'solid'}}}"));
						}
						
						//将获取到的值存入到yAxis的series中，待传到前台显示
						series.setData(values);
						yAxis.add(series);
					}
					
					
					//获取X轴的坐标值
					JSONArray resultX = historyX.getJSONArray("result");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					
					for(int n=0; n<resultX.size(); n++) {
						JSONObject obj = resultX.getJSONObject(n);
						
						String clock = sdf.format(new Date(Long.parseLong(obj.getString("clock")) * 1000L));
						xAxis.add(clock);
					}
					
				}
				
				graphData.setLegend(legend);
				graphData.setxAxis(xAxis);
				graphData.setyAxis(yAxis);
				
				graphList.add(graphData);
				
				zabbixData.setGraphList(graphList);
				list.add(zabbixData);
			}
		}
		
		return list;
	}
	
	// save to file
	public void saveZabbixData(ZabbixDataModel zabbixDataModel, File file) throws Exception {
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(zabbixDataModel);
		oos.flush();
		oos.close();
		fos.close();
	}
	
	// load from file
	public ZabbixDataModel loadZabbixData(File file) throws Exception {
		ZabbixDataModel zabbixDataModel = null;
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		zabbixDataModel = (ZabbixDataModel) ois.readObject();
		ois.close();
		fis.close();
		return zabbixDataModel;
	}

}
