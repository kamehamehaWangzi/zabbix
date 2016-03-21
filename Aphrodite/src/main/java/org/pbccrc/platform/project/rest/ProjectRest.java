package org.pbccrc.platform.project.rest;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.monitor.biz.IMachineBiz;
import org.pbccrc.platform.project.biz.IProjectBiz;
import org.pbccrc.platform.util.StringUtils;
import org.pbccrc.platform.vo.HostVO;
import org.pbccrc.platform.vo.ProjectVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;

@Path("/project")
@Produces(MediaType.APPLICATION_JSON)
public class ProjectRest {
	
	private static final Logger log = Logger.getLogger(ProjectRest.class);
	
	@Autowired
	@Qualifier("projectBizImpl")
	private IProjectBiz projectBiz;
	
	@Autowired
	@Qualifier("machineBizImpl")
	private IMachineBiz machineBiz;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listProject(@QueryParam("param") String param, @QueryParam("currentPage") int currentPage, @QueryParam("pageSize") int pageSize) {
		ProjectVO vo = new ProjectVO();
		if(param != null && param.trim().length() > 0) {
			vo.setName(param.trim());
		}
		
		Pagination pagination = new Pagination();
		pagination.setCurrentPage(currentPage);
		pagination.setPageSize(pageSize);
		
		List<ProjectVO> projects = projectBiz.queryProjects(vo, pagination);
		
		pagination.setTotalCount(((Page<ProjectVO>) projects).getTotal());
		
		pagination.setResult(projects);
		
		log.debug(String.format("listProject, %s", projects));
		
		return Response.ok(pagination).build();
	}
	
	@Path("/getHost")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listHost() {
		List<HostVO> hostList = projectBiz.queryAllHost();
		return Response.ok(JSON.toJSONString(hostList)).build();
	}
	
	@POST
	public Response addProject(@QueryParam("project") String project) {
		JSONObject projectInfo = JSON.parseObject(project);
		projectBiz.addProject(projectInfo);
		
		return Response.ok(200).build();
	}
	
	@DELETE
	public Response deleteProject(@QueryParam("ids") String ids) {
		if(ids != null && ids.trim().length() > 0) {
		
			for(String id : ids.split(",")) {
				projectBiz.deleteProject(id);
			}
		}
		
		return Response.ok(200).build();
	}
	
	@Path("/{id}/info")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProjectDetail(@PathParam("id") String id) {
		ProjectVO project = projectBiz.queryProjectById(id);
		
		log.debug(String.format("getProjectDetail, %s", project));
		
		return Response.ok(project).build();
	}
	
	@Path("/{id}/host")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProjectHostDetail(@PathParam("id") String id) {
		ProjectVO project = projectBiz.queryProjectById(id);
		
		String hosts = project.getHosts();
		
		if(StringUtils.isNull(hosts)) {
			return Response.ok("").build();
		}
		
		List<String> hostList = machineBiz.queryHostsByIds(JSON.parseArray(hosts));
		
		return Response.ok(hostList).build();
	}
	
	
}
