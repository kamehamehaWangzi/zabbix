package org.pbccrc.platform.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pbccrc.platform.api.ApiFactory;
import org.pbccrc.platform.api.zabbix.DefaultZabbixApi;
import org.pbccrc.platform.api.zabbix.Request;
import org.pbccrc.platform.api.zabbix.RequestBuilder;
import org.pbccrc.platform.api.zabbix.ZabbixApi;
import org.pbccrc.platform.cmdb.dao.HostDao;
import org.pbccrc.platform.cmdb.dao.MonitorDataDao;
import org.pbccrc.platform.cmdb.dao.TaskDao;
import org.pbccrc.platform.cmdb.dao.TaskDataDao;
import org.pbccrc.platform.model.GraphModel;
import org.pbccrc.platform.model.Series;
import org.pbccrc.platform.model.ZabbixDataModel;
import org.pbccrc.platform.monitor.biz.IGraphBiz;
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
public class ZabbixDataUtil {
	
	@Autowired
	TaskDao taskDao;
	
	@Autowired
	TaskDataDao taskDataDao;
	
	@Autowired
	MonitorDataDao monitorDataDao;
	
	@Autowired
	HostDao hostDao;
	
	@Autowired
	IGraphBiz graphBiz;
	
	@Autowired
	ApiFactory apiFactory;
	
	@Autowired
	ZabbixOperator zabbixOperator;
	
	public static final String TYPE_CPU = "CPU";
	public static final String TYPE_DISK = "DISK";
	public static final String TYPE_DISK_CNT = "DISK_CNT";
	public static final String TYPE_NET = "NET";
	public static final String TYPE_MEMORY = "MEMORY";
	
	public static final int DISK_CNT = 1;
	public static final int DISK_SIZE = 2;
	
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
	 * 内存
	 * total: 总内存
	 * available: 可用内存
	 * used: 已用内存 
	 */
	private static enum MEMORY {
		
		MEMORY_TOTAL("vm.memory.size[total]"), 
		MEMORY_AVAILABLE("vm.memory.size[available]");
		
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
		
		SWAP_IN("system.swap.size[,free]"), 
		SWAP_OUT("system.swap.size[,total]");
		
		private String value;

		public String getValue() {
			return this.value;
		}

		private SWAP(String value) {
			this.value = value;
		}
	}
	
	/** 磁盘读写
	 *  type : 1:次数 2：大小
	 *  */
	private List<String> getDiskIOKeyList(int type) {

		List<String> keyList = new ArrayList<String>();
		
		if (DISK_CNT == type) {
			keyList.add("io.rps[*]");
			keyList.add("io.wps[*]");
		} else if (DISK_SIZE == type) {
			keyList.add("vfs.dev.read[*,sps]");
			keyList.add("vfs.dev.write[*,sps]");
		} else {
			// TODO
			// to be extended
		}
		
		return keyList;
	}
	
	/**
	 * 网卡
	 * in : 网卡入口流量
	 * out: 网卡出口流量 
	 */
	private List<String> getNetKeyList(){
		
		List<String> keyList = new ArrayList<String>();
		keyList.add("net.if.in[*]");
		keyList.add("net.if.out[*]");
		
		return keyList;
	}
	
	/** 磁盘读写 */
	private List<String> getDiskIOItemList(Integer zabbixHostId, int type) {
		
		List<String> itemList = new ArrayList<String>();
		
		List<String> diskList = zabbixOperator.getDiskInfoByItem(zabbixHostId);
		
		List<String> keyList = getDiskIOKeyList(type);
		
		for (int i = 0; i < diskList.size(); i++) {
			
			String disk = diskList.get(i);
			
			for (int j = 0; j < keyList.size(); j++) {
				
				String key = keyList.get(j);
				
				itemList.add(key.replace("*", disk));
			}
		}
		
		return itemList;
	}
	
	/**
	 * 网卡
	 */
	private List<String> getNetItemList(Integer zabbixHostId) {
		
		List<String> itemList = new ArrayList<String>();
		
		List<String> netList = zabbixOperator.getNetInfoByItem(zabbixHostId);
		
		List<String> keyList = getNetKeyList();
		
		for (int i = 0; i < netList.size(); i++) {
			
			String disk = netList.get(i);
			
			for (int j = 0; j < keyList.size(); j++) {
				
				String key = keyList.get(j);
				
				itemList.add(key.replace("*", disk));
			}
		}
		
		return itemList;
	}
	
	// get zabbixData by parameter, 使用中，用于对item的分类 2016.05.10
	public JSONArray getZabbixDataByType(Integer zabbixHostId, String type, JSONArray keyItems){
		
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
						
						List<String> diskIOList = getDiskIOItemList(zabbixHostId, DISK_SIZE);
						
						for (int j = 0; j < diskIOList.size(); j++) {
							if (diskIOList.get(j).equals(key) ) {
								jsonArray.add(keyItem);
							}
						}
						
						break;
						
					case TYPE_DISK_CNT:
						
						List<String> diskIOCntList = getDiskIOItemList(zabbixHostId, DISK_CNT);
						
						for (int j = 0; j < diskIOCntList.size(); j++) {
							if (diskIOCntList.get(j).equals(key) ) {
								jsonArray.add(keyItem);
							}
						}
						
						break;
			
					case TYPE_NET:
						
						List<String> netList = getNetItemList(zabbixHostId);
						
						for (int j = 0; j < netList.size(); j++) {
							if (netList.get(j).equals(key) ) {
								jsonArray.add(keyItem);
							}
						}
						
						break;
			
					case TYPE_MEMORY:
						
						if (MEMORY.MEMORY_TOTAL.getValue().equals(key) ||
							MEMORY.MEMORY_AVAILABLE.getValue().equals(key) ||
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
		
		ZabbixApi zabbixApi = new DefaultZabbixApi();
		zabbixApi.init();
		
		String auth = zabbixApi.auth(Constant.ZABBIX_USERNAME, Constant.ZABBIX_PASSWORD);
		
		Request request = RequestBuilder.newBuilder().auth(auth)
				.paramEntry("output", "extend")
				.paramEntry("hostids", zabbixHostId)
				.paramEntry("sortfield", "name")
				.method("item.get")
				.build();
		
//		JSONObject returnObj = apiFactory.zabbix().call(request);
		JSONObject returnObj = zabbixApi.call(request);
		
		JSONArray keyItems = new JSONArray();
		if(returnObj != null && !returnObj.isEmpty() && !returnObj.getJSONArray("result").isEmpty()) {
			keyItems.addAll(returnObj.getJSONArray("result"));
		}
		
		return keyItems;
	}
	
	
	/**
	 * get zabbixData 按照任务ID和监控类型(CPU,DISK……)进行监控数据的持久化
	 * @param vo
	 * @param type
	 * @param path
	 * @return
	 * 2015.05.09 修改持久化过程中添加，监控itemName作为存储字段
	 */
	public int obtainZabbixData(TaskDataVO vo, String type, String path, Integer scaler){
		
		int result = 0;
		
		// defalut param
		Integer defaultDateRange = 1;
		String graphType = Constant.GRAPH_TYPE.area.getValue();
		
		TaskVO task = taskDao.queryByTaskId(vo.getTaskId().toString());
		//获取任务涉及到的主机
		JSONArray hosts = JSON.parseArray(task.getHosts());
		//监控数据文件持久化的根地址
		String basePath = path + vo.getTaskId() + "\\" + vo.getId() + "\\";    											//需要做参数化，还需要确认文件是否存在
		File baseFile = new File(basePath);
		if(!baseFile.exists()){
			try{
				baseFile.mkdirs();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if (hosts != null && !hosts.isEmpty()) {
			// loop host
			for (int i = 0; i < hosts.size(); i++) {

				ZabbixDataModel zabbixData = new ZabbixDataModel();// 任务监控数据对象
				HostVO host = hostDao.queryById(hosts.getString(i));

				// 获取当前host 监控的所有的keyItems
				JSONArray keyItems = getKeyItems(host.getZabbixHostid());
				// 根据监控类型type获取监控item
				keyItems = getZabbixDataByType(host.getZabbixHostid(), type, keyItems);
				zabbixData.setHostId(host.getId());

				JSONObject historyX = null;
				if (keyItems != null && !keyItems.isEmpty()) {
					// 获取Y轴的值
					for (int j = 0; j < keyItems.size(); j++) {
						// init data
						GraphModel graphData = new GraphModel(); // 初始化，每个item对应一个graphData
						List<String> legend = new ArrayList<String>();
						List<String> xAxis = new ArrayList<String>();
						List<Series> yAxis = new ArrayList<Series>();
						
						JSONObject itemY = keyItems.getJSONObject(j);
						Integer valueType = itemY.getInteger("value_type");

						JSONObject historyY = graphBiz.queryHistoryByItem(itemY.getString("itemid"), valueType,
								vo.getStartTime(), vo.getEndTime(), defaultDateRange);
						historyX = historyY;

						// 得到数据结果集
						JSONArray resultY = historyY.getJSONArray("result");
						Series series = new Series();
						// 将result结果集合中的value数据存到values链表中
						List<String> values = new ArrayList<String>();
						for (int m = 0; m < resultY.size(); m++) {
							String value_key = resultY.getJSONObject(m).containsKey("value_avg") ? "value_avg" : "value";
							String value = null;
							if (scaler != null) {
								value = String.valueOf(resultY.getJSONObject(m).getDouble(value_key) / scaler);
							} else {
								value = String.valueOf(resultY.getJSONObject(m).get(value_key));
							}
							values.add(value);
						}

						String itemName = itemY.getString("name");
						// itemName的格式化转换
						if (itemName.indexOf("$") != -1) {
							Pattern pattern = Pattern.compile("\\[.*\\]");
							Matcher match = pattern.matcher(itemY.getString("key_"));
							if (match.find()) {
								String[] matchStr = match.group().replaceAll("\\[|\\]", "").split(",");
								Pattern patternIndex = Pattern.compile("\\$.d*");
								Matcher matchIndex = patternIndex.matcher(itemName);
								if (matchIndex.find()) {
									Integer index = Integer.parseInt(matchIndex.group().replaceAll("\\$", "").trim());
									itemName = itemName.replaceAll("\\$" + index, matchStr[index - 1]).replace("/", "");
								}
							}
						}

						legend.add(host.getIp1());
						series.setName(itemName);
						// echarts's area == line
						if (Constant.GRAPH_TYPE.area.getValue().equals(graphType)) {
							series.setType(Constant.GRAPH_TYPE.line.getValue());
							series.setItemStyle(JSONObject.parseObject("{normal: {areaStyle: {type: 'default'}}}"));
						} else {
							series.setType(graphType);
							series.setItemStyle(JSONObject.parseObject("{normal: {lineStyle: {type: 'solid'}}}"));
						}
						// 将获取到的值存入到yAxis的series中，待传到前台显示
						series.setData(values);
						yAxis.add(series);
						// 获取X轴的坐标值
						JSONArray resultX = historyX.getJSONArray("result");
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

						for (int n = 0; n < resultX.size(); n++) {
							JSONObject obj = resultX.getJSONObject(n);
							String clock = sdf.format(new Date(Long.parseLong(obj.getString("clock")) * 1000L));
							xAxis.add(clock);
						}

						graphData.setLegend(legend);
						graphData.setxAxis(xAxis);
						graphData.setyAxis(yAxis);
						zabbixData.setGraph(graphData);

						String filePath = basePath + "host_" + hosts.getString(i) + "_" + itemName.replace(' ', '_') + ".data";
						// 数据持久化到文件系统
						// eg.E://monitorData//task_12//host_12_CPU.data
						File saveFile = new File(filePath);
						try {
							saveZabbixData(zabbixData, saveFile);
						} catch (Exception e) {
							e.printStackTrace();
						}
						MonitorDataVO monitorData = new MonitorDataVO(vo.getId().toString(), host.getId().toString(),
								type, itemName.replace(' ', '_'), filePath);
						result = monitorDataDao.insertMonitorData(monitorData);
					}
				}
			}
		}
		
		return result;
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
