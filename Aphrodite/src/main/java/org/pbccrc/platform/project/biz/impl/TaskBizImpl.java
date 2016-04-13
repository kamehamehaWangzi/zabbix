package org.pbccrc.platform.project.biz.impl;

import java.util.ArrayList;
import java.util.List;

import org.pbccrc.platform.cmdb.dao.TaskDao;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.project.biz.ITaskBiz;
import org.pbccrc.platform.util.StringUtils;
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
	
	@Override
	public TaskVO queryTaskById(String id) {
		return taskDao.queryByTaskId(id);
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

	/**
	 * 将JSon对象封装成java对象，传给Dao层进行更新
	 * zhp  2015.3.30
	 */
	@Override
	public void modifyTask(JSONObject taskInfo) {
		TaskVO vo = new TaskVO();
		vo.setId(Integer.parseInt(taskInfo.getString("id")));
		vo.setName(taskInfo.getString("name"));
		vo.setProject(taskInfo.getString("project"));
		vo.setDescription(taskInfo.getString("description"));
		vo.setHosts(taskInfo.getString("hosts"));
		
		taskDao.modifyTask(vo);
	}
	
	public List<TaskVO> getAllTasks(String taskId) {
		List<TaskVO> list = null;
		
		if (StringUtils.isNull(taskId)) {
			list = taskDao.queryTasks();
		}
		
		list = new ArrayList<TaskVO>();
		list.add(taskDao.queryByTaskId(taskId));
		
		return list;
	}
}
