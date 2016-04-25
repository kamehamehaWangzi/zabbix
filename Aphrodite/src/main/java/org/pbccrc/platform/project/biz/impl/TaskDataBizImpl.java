package org.pbccrc.platform.project.biz.impl;

import java.util.List;
import java.util.Map;

import org.pbccrc.platform.cmdb.dao.TaskDataDao;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.model.ZabbixDataModel;
import org.pbccrc.platform.project.biz.ITaskDataBiz;
import org.pbccrc.platform.util.ZabbixDataUtil;
import org.pbccrc.platform.vo.TaskDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

@Service
public class TaskDataBizImpl implements ITaskDataBiz{
	
	@Autowired
	private TaskDataDao taskDataDao;
	
	@Autowired
	private ZabbixDataUtil zabbixDataUtil;

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
		
		for (int i = 0; i < map.size(); i++) {
			
		}
	}
	
	public void deleteTaskData(String taskDataId) {
		TaskDataVO vo = taskDataDao.queryByTaskDataId(taskDataId);
		if(vo != null) {
			taskDataDao.deleteTaskData(String.valueOf(vo.getId()));
		}
	}

}
