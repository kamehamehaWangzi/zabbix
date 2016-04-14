package org.pbccrc.platform.project.biz.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.pbccrc.platform.api.ApiFactory;
import org.pbccrc.platform.api.zabbix.Request;
import org.pbccrc.platform.api.zabbix.RequestBuilder;
import org.pbccrc.platform.cmdb.dao.HostDao;
import org.pbccrc.platform.cmdb.dao.TaskDao;
import org.pbccrc.platform.cmdb.dao.TaskDataDao;
import org.pbccrc.platform.model.GraphModel;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.model.Series;
import org.pbccrc.platform.project.biz.ITaskDataBiz;
import org.pbccrc.platform.vo.HostVO;
import org.pbccrc.platform.vo.TaskDataVO;
import org.pbccrc.platform.vo.TaskVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Service
public class TaskDataBizImpl implements ITaskDataBiz{
	
	private static final Logger log = Logger.getLogger(TaskDataBizImpl.class);
	
	@Autowired
	private TaskDataDao taskDataDao;
	
	@Autowired
	private TaskDao taskDao;
	
	@Autowired
	private HostDao hostDao;
	
	@Autowired
	private ApiFactory apiFactory;

	public List<TaskDataVO> queryTaskDatas(TaskDataVO vo, Pagination pagination) {
		return taskDataDao.queryAll(vo, pagination);
	}
	
	public void addTaskData(JSONObject taskData) {
		
		TaskDataVO vo = new TaskDataVO();
		vo.setTaskId(taskData.getInteger("taskId"));
		vo.setStartTime(taskData.getString("startTime"));
		vo.setEndTime(taskData.getString("endTime"));
		
		taskDataDao.insertTaskData(vo);
	}
	
	// get zabbixData
	private List<GraphModel> getZabbixData(TaskDataVO vo){
		
		TaskVO task = taskDao.queryByTaskId(vo.getTaskId().toString());
		
		JSONArray hosts = JSON.parseArray(task.getHosts());
		
		List<GraphModel> list = new ArrayList<GraphModel>();
		
		if(hosts != null && !hosts.isEmpty()) {
		
			// loop host
			for(int i = 0; i < hosts.size(); i++) {
				
				HostVO host = hostDao.queryById(hosts.getString(i));
				
				// get item from zabbix host
				Request request = RequestBuilder.newBuilder()
						.paramEntry("output", "extend")
						.paramEntry("hostids", host.getZabbixHostid())
						.paramEntry("sortfield", "name")
						.method("item.get")
						.build();
				JSONObject returnObj = apiFactory.zabbix().call(request);
				
				JSONArray keyItems = new JSONArray();
				if(returnObj != null && !returnObj.isEmpty() && !returnObj.getJSONArray("result").isEmpty()) {
					keyItems.addAll(returnObj.getJSONArray("result"));
				}
				
			}
		}
		
		
		return list;
	}
	
	public void deleteTaskData(String taskDataId) {
		TaskDataVO vo = taskDataDao.queryByTaskDataId(taskDataId);
		if(vo != null) {
			taskDataDao.deleteTaskData(String.valueOf(vo.getId()));
		}
	}

}
