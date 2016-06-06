package org.pbccrc.platform.project.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.pbccrc.platform.model.GraphModel;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.project.biz.ITaskBiz;
import org.pbccrc.platform.project.biz.ITaskDataBiz;
import org.pbccrc.platform.util.Constant;
import org.pbccrc.platform.vo.TaskDataVO;
import org.pbccrc.platform.vo.TaskVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;

@Path("/taskData")
@Produces(MediaType.APPLICATION_JSON)
public class TaskDataRest {
	
	private static final Logger log = Logger.getLogger(TaskDataRest.class);
	
	@Autowired
	@Qualifier("taskBizImpl")
	private ITaskBiz taskBiz;
	
	@Autowired
	@Qualifier("taskDataBizImpl")
	private ITaskDataBiz taskDataBiz;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listTaskData(@QueryParam("taskName") String taskName, @QueryParam("taskId") String taskId, @QueryParam("currentPage") int currentPage, @QueryParam("pageSize") int pageSize) {
		TaskDataVO vo = new TaskDataVO();
		if(taskName != null && taskName.trim().length() > 0) {
			vo.setTaskName(taskName.trim());
		}
		if(taskId != null && taskId.trim().length() > 0) {
			vo.setTaskId(Integer.parseInt(taskId));
		}
		
		Pagination pagination = new Pagination();
		pagination.setCurrentPage(currentPage);
		pagination.setPageSize(pageSize);
		
		List<TaskDataVO> taskDatas = taskDataBiz.queryTaskDatas(vo, pagination);
		
		pagination.setTotalCount(((Page<TaskDataVO>) taskDatas).getTotal());
		
		pagination.setResult(taskDatas);
		
		log.debug(String.format("listTaskData, %s", taskDatas));
		
		return Response.ok(pagination).build();
	}
	
	@Path("/getTask")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listHost(@QueryParam("taskId") String taskId) {
		List<TaskVO> taskList = taskBiz.getAllTasks(taskId);
		return Response.ok(JSON.toJSONString(taskList)).build();
	}
	
	@POST
	public Response addTaskData(@QueryParam("taskData") String taskData) {
		JSONObject taskDataInfo = JSON.parseObject(taskData);
		taskDataBiz.addTaskData(taskDataInfo);
		
		return Response.ok(200).build();
	}
	

	/**
	 * 触发，将任务数据存储到DB
	 * @return
	 */
	@Path("/obtainTaskMonitor2DB")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public int obtainData2DB(@QueryParam("task_id")String task_id,@QueryParam("id")String id, @Context HttpServletRequest request){
		int result = 0;
		String path = request.getSession().getServletContext().getRealPath(Constant.ZABBIX_MONITOR_DATA_PATH);
		if(task_id != null){
			result = taskDataBiz.saveTaskDataMonitor2DB(task_id, path);
		}else if(id!=null){
			System.out.println("All");
			//设置批量持久化监控数据,已经结束的，尚未收集数据的任务
			result = taskDataBiz.saveAllTaskDataMonitor2DB(id,path);
		}
		return result;
		
	}
	
	/**
	 * 获取任务监控数据，在前台表格显示
	 * */
	@Path("/detailMonitor")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMonitorData(@QueryParam("task_id")String id){

		JSONArray resultArray = taskDataBiz.showTaskMonitorData(id);
		log.debug(String.format(" Get the monitor data id %s", id));
		//查询显示表格
		return Response.ok(resultArray).build();
	}
	
	/**
	 * 获取显示图表的数据
	 * */
	@Path("/detailMonitorTrend")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDetailMonitorTrend(@QueryParam("taskData_id") String taskData_id,
			@QueryParam("key") String key, //查询类型 例 ['system.cpu.util[,user]', 'system.cpu.util[,nice]']
			@QueryParam("likeSearch") Boolean likeSearch, 
			@QueryParam("graphType") String graphType, //图表类型
			@QueryParam("hostId") String hostId,
			@QueryParam("scaler") Integer scaler,
			@QueryParam("defaultDateRange") Integer defaultDateRange){
		
		//获取到的监控数据 ZabbixDataModel
		//ZabbixDataModel 主机 时间 GraphModel(包含4大类监控数据)
		GraphModel resultGraph = taskDataBiz.showDetaiGraph(taskData_id,hostId,key);
		
		//导出结果		
		return Response.ok(resultGraph).build();
	}
	
	@DELETE
	public Response deleteTaskData(@QueryParam("ids") String ids) {
		if(ids != null && ids.trim().length() > 0) {
		
			for(String id : ids.split(",")) {
				taskDataBiz.deleteTaskData(id);
			}
		}
		
		return Response.ok(200).build();
	}

}
