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
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.project.biz.IProjectBiz;
import org.pbccrc.platform.project.biz.ITaskBiz;
import org.pbccrc.platform.vo.HostVO;
import org.pbccrc.platform.vo.ProjectVO;
import org.pbccrc.platform.vo.TaskVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;

@Path("/task")
@Produces(MediaType.APPLICATION_JSON)
public class TaskRest {
	
	private static final Logger log = Logger.getLogger(TaskRest.class);
	
	@Autowired
	@Qualifier("projectBizImpl")
	private IProjectBiz projectBiz;
	
	@Autowired
	@Qualifier("taskBizImpl")
	private ITaskBiz taskBiz;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listTask(@QueryParam("param") String param, @QueryParam("currentPage") int currentPage, @QueryParam("pageSize") int pageSize) {
		TaskVO vo = new TaskVO();
		if(param != null && param.trim().length() > 0) {
			vo.setName(param.trim());
		}
		
		Pagination pagination = new Pagination();
		pagination.setCurrentPage(currentPage);
		pagination.setPageSize(pageSize);
		
		List<TaskVO> tasks = taskBiz.queryTasks(vo, pagination);
		
		pagination.setTotalCount(((Page<TaskVO>) tasks).getTotal());
		
		pagination.setResult(tasks);
		
		log.debug(String.format("listTask, %s", tasks));
		
		return Response.ok(pagination).build();
	}
	
	
	@Path("/getProject")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listProject() {
		List<ProjectVO> hostList = projectBiz.queryAllProject();
		return Response.ok(JSON.toJSONString(hostList)).build();
	}
	
	@Path("/getHost")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listHost(@QueryParam("projectId") String projectId) {
		List<HostVO> hostList = projectBiz.queryAllHost(projectId);
		return Response.ok(JSON.toJSONString(hostList)).build();
	}
	
	@POST
	public Response addTask(@QueryParam("task") String task) {
		JSONObject taskInfo = JSON.parseObject(task);
		taskBiz.addTask(taskInfo);
		
		return Response.ok(200).build();
	}
	
	@DELETE
	public Response deleteTask(@QueryParam("ids") String ids) {
		if(ids != null && ids.trim().length() > 0) {
		
			for(String id : ids.split(",")) {
				taskBiz.deleteTask(id);
			}
		}
		
		return Response.ok(200).build();
	}
	
}
