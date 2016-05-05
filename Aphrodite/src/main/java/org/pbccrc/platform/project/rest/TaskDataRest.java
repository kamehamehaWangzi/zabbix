package org.pbccrc.platform.project.rest;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.pbccrc.platform.model.GraphModel;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.model.ZabbixDataModel;
import org.pbccrc.platform.project.biz.ITaskBiz;
import org.pbccrc.platform.project.biz.ITaskDataBiz;
import org.pbccrc.platform.project.biz.impl.TaskBizImpl;
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
	 * 获取任务监控数据，在前台表格显示
	 * */
	@Path("/detailMonitor")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMonitorData(@QueryParam("taskId")String taskId,
			@QueryParam("startTime") String startTime,
			@QueryParam("endTime")String endTime){
		System.out.println("AAAAAAA");
		log.debug(String.format(" Get the monitor data %s,%s,%s", taskId,startTime,endTime));
		JSONArray resultArray = taskDataBiz.getTaskDataMonitorData(taskId,startTime,endTime);
		return Response.ok(resultArray).build();
	}
	
	/**
	 * 获取显示图表的数据
	 * */
	@Path("/detailMonitorTrend")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDetailMonitorTrend(@QueryParam("key") String key){
		
		//获取到的监控数据 ZabbixDataModel
		//ZabbixDataModel 主机 时间 GraphModel(包含4大类监控数据)
		ZabbixDataModel monitorData = new ZabbixDataModel();
		
		//转换类型
		List<GraphModel> monitorDataList = monitorData.getGraphList();
		
		//通过循环获取指定类型
		GraphModel resultGraph = null;
		for(int i = 0; i < monitorDataList.size(); i++){
			if(monitorDataList.get(i).getLegend().equals("")){
				resultGraph = monitorDataList.get(i);
				break;
			}
		}
		
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
