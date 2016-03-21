package org.pbccrc.platform.project.biz;

import java.util.List;

import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.vo.TaskVO;

import com.alibaba.fastjson.JSONObject;

public interface ITaskBiz {
	
	List<TaskVO> queryTasks(TaskVO vo, Pagination pagination);
	
	void addTask(JSONObject task);
	
	void deleteTask(String taskId);
	
	void deleteTaskByProject(String projectId);
	
}
