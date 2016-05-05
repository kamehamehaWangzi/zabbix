package org.pbccrc.platform.project.biz.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pbccrc.platform.cmdb.dao.HostDao;
import org.pbccrc.platform.cmdb.dao.TaskDao;
import org.pbccrc.platform.cmdb.dao.TaskDataDao;
import org.pbccrc.platform.model.GraphModel;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.model.Series;
import org.pbccrc.platform.model.ZabbixDataModel;
import org.pbccrc.platform.project.biz.ITaskDataBiz;
import org.pbccrc.platform.util.ZabbixDataUtil;
import org.pbccrc.platform.vo.HostVO;
import org.pbccrc.platform.vo.TaskDataVO;
import org.pbccrc.platform.vo.TaskVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Service
public class TaskDataBizImpl implements ITaskDataBiz{
	
	@Autowired
	private TaskDataDao taskDataDao;
	
	@Autowired
	TaskDao taskDao;
	
	@Autowired
	HostDao hostDao;
	
	@Autowired
	private ZabbixDataUtil zabbixDataUtil;
	
	public static final String TYPE_CPU = "CPU";
	public static final String TYPE_DISK = "DISK";
	public static final String TYPE_NET = "NET";
	public static final String TYPE_MEMORY = "MEMORY";

	public List<TaskDataVO> queryTaskDatas(TaskDataVO vo, Pagination pagination) {
		return taskDataDao.queryAll(vo, pagination);
	}
	
	public void addTaskData(JSONObject taskData) {
		
		TaskDataVO vo = new TaskDataVO();
		vo.setTaskId(taskData.getInteger("taskId"));
		vo.setStartTime(taskData.getString("startTime"));
		vo.setEndTime(taskData.getString("endTime"));
//		vo.setPath(taskData.getString("path"));
		
//		taskDataDao.insertTaskData(vo);
		
		Map<String, List<ZabbixDataModel>> map = zabbixDataUtil.getZabbixDataMap(vo);
		
//		map.put(TYPE_CPU, cpulist);
//		map.put(TYPE_DISK, disklist);
//		map.put(TYPE_NET, netlist);
//		map.put(TYPE_MEMORY, memorylist);
		
//		private int taskDataId;
//		private int hostId;
//		private List<GraphModel> graphList;
//
//		private List<String> legend;
//		private List<String> xAxis;
//		private List<Series> yAxis;
		
		for (int i = 0; i < map.size(); i++) {
//			map.get("TYPE_CPU").get(i).getHostId();
//			map.get("TYPE_CPU").get(i).getTaskDataId();
//			map.get("TYPE_CPU").get(i).getGraphList().get(0).
		}
	}
	
	/**
	 * new 2016.4.29 zhp
	 * @param taskData
	 */
	public JSONArray getTaskDataMonitorData(String taskId,String startTime, String endTime){
		
		//根据 taskId 获取关联的 主机host
		TaskVO task = taskDao.queryByTaskId(taskId);
		JSONArray hostArray = JSONArray.parseArray(task.getHosts());
		
		JSONArray hostResult = new JSONArray();
		
		//循环 按照主机号，监控项，开始时间，截止时间查出监控信息
		for(int i = 0; i<hostArray.size(); i++){
			
			HostVO host = hostDao.queryById(hostArray.getString(i));
			
			Map<String, JSONObject> map = new HashMap<String, JSONObject>();
			
			ZabbixDataModel cpulist = zabbixDataUtil.getZabbixDataByHost(host, TYPE_CPU, startTime, endTime);
			ZabbixDataModel disklist = zabbixDataUtil.getZabbixDataByHost(host, TYPE_DISK, startTime, endTime);
			ZabbixDataModel netlist = zabbixDataUtil.getZabbixDataByHost(host, TYPE_NET, startTime, endTime);
			ZabbixDataModel memorylist = zabbixDataUtil.getZabbixDataByHost(host, TYPE_MEMORY, startTime, endTime);
			
			JSONObject cpuJsonObject = getMaxMinData(cpulist.getGraphList());
			JSONObject diskJsonObject = getMaxMinData(disklist.getGraphList());
			JSONObject netJsonObject = getMaxMinData(netlist.getGraphList());
			JSONObject memoryJsonObject = getMaxMinData(memorylist.getGraphList());
			
			JSONObject hostIP = new JSONObject();
			hostIP.put("value", host.getIp1());
			map.put("hostIP", hostIP);
			map.put(TYPE_CPU, cpuJsonObject);
			map.put(TYPE_DISK, diskJsonObject);
			map.put(TYPE_NET, netJsonObject);
			map.put(TYPE_MEMORY, memoryJsonObject);
			
			hostResult.add(map);
		}
		
		//各个主机监控信put into 结果集合resultMap，方便前台直接显示
		return hostResult;
	}
	
	public JSONObject getMaxMinData(List<GraphModel> graphModel){
		JSONObject resultJsonObject = new JSONObject();
		for(int j = 0;j<graphModel.size();j++){
			List<Series> seriesTemp = graphModel.get(j).getyAxis();
			for(Series e : seriesTemp){
				if(e.getData()==null || e.getData().size()==0){
					continue;
				}
				float max = Float.parseFloat(e.getData().get(0));
				float min = Float.parseFloat(e.getData().get(0));
				float avg = 0.0f;
				for(int k = 1; k<e.getData().size(); k++){
					float currentOne = Float.parseFloat(e.getData().get(k));
					max = currentOne>max?currentOne:max;
					min = currentOne<min?currentOne:min;
					avg += currentOne;
				}
				avg = avg / e.getData().size();
				System.out.println(avg);
				resultJsonObject.put(e.getName().replace(' ', '_'), max+"/"+min+"/"+avg);
			}
		}
		return resultJsonObject;
	}
	
	public void deleteTaskData(String taskDataId) {
		TaskDataVO vo = taskDataDao.queryByTaskDataId(taskDataId);
		if(vo != null) {
			taskDataDao.deleteTaskData(String.valueOf(vo.getId()));
		}
	}

}
