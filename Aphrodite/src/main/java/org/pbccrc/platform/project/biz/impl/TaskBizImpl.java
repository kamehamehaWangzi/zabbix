package org.pbccrc.platform.project.biz.impl;

import java.util.List;

import org.pbccrc.platform.cmdb.dao.TaskDao;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.project.biz.ITaskBiz;
import org.pbccrc.platform.vo.TaskVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Service
public class TaskBizImpl implements ITaskBiz{
	
	@Autowired
	private TaskDao taskDao;

	public List<TaskVO> queryTasks(TaskVO vo, Pagination pagination) {
		return taskDao.queryAll(vo, pagination);
	}
	

	public void addTask(JSONObject task) {
		
		TaskVO vo = new TaskVO();
		vo.setName(task.getString("name"));
		vo.setProject(task.getString("project"));
		vo.setDescription(task.getString("description"));
		JSONArray hostArray = task.getJSONArray("hosts");
		if (null != hostArray) {
			vo.setHosts(hostArray.toString());
		}
		
		taskDao.insertTask(vo);
	}
	
	public void deleteTask(String taskId) {
		TaskVO vo = taskDao.queryByTaskId(taskId);
		if(vo != null) {
			taskDao.deleteTask(String.valueOf(vo.getId()));
		}
	}
	
	public void deleteTaskByProject(String projectId) {
		taskDao.deleteTaskByProject(projectId);
	}

}
