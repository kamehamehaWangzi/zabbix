package org.pbccrc.platform.project.biz;

import java.util.List;

import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.vo.TaskDataVO;

import com.alibaba.fastjson.JSONObject;

public interface ITaskDataBiz {
	
	List<TaskDataVO> queryTaskDatas(TaskDataVO vo, Pagination pagination);
	
	void addTaskData(JSONObject taskData);
	
	void deleteTaskData(String taskDataId);
}
