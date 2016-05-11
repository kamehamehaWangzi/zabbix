package org.pbccrc.platform.project.biz;

import java.util.List;

import org.pbccrc.platform.model.GraphModel;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.vo.TaskDataVO;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface ITaskDataBiz {
	
	List<TaskDataVO> queryTaskDatas(TaskDataVO vo, Pagination pagination);
	
	void addTaskData(JSONObject taskData);
	
	void deleteTaskData(String taskDataId);
	
	/**
	 * 监控步骤1：持久化监控数据，根据监控数据id，将监控数据存入文件系统和数据库
	 * */
	public int saveTaskDataMonitor2DB(String id, String path);
	
	/**
	 * 监控步骤2：表格展示监控数据
	 * @param taskDataId
	 * @return
	 */
	public JSONArray showTaskMonitorData(String taskDataId);
	
	/**
	 * 监控步骤3：监控数据的图形化
	 * @param taskData_id
	 * @param hostId
	 * @param type
	 * @return
	 */
	public GraphModel showDetaiGraph(String taskData_id,String hostId,String type);
}
