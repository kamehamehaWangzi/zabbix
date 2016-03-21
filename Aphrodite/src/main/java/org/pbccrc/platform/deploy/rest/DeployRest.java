package org.pbccrc.platform.deploy.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.pbccrc.platform.deploy.biz.IDeployBiz;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.vo.SaltJobVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;

@Path("/deploy")
public class DeployRest {
	
	private static final Logger log = Logger.getLogger(DeployRest.class);
	
	@Autowired
	@Qualifier("deployBizImpl")
	private IDeployBiz deployBiz;
	
	
	@Path("/service")
	@POST
	public Response serviceStatus(@QueryParam("ids") String ids, @QueryParam("app") String app, @QueryParam("cmd") String cmd) {
		List<String> hostIds = new ArrayList<String>();
		if(ids != null && ids.trim().length() > 0) {
			for(String id : ids.split(",")) {
				if(id != null && id.trim().length() > 0) {
					hostIds.add(id);
				}
			}
		}
		
		log.debug(String.format("execute service %s %s, on %s", app, cmd, hostIds));
		
		String serviceName = null;
		String fullCmd = null;
		
		switch (app) {
			case "mysql":
				serviceName = "mysql";
				fullCmd = String.format("/etc/init.d/%s %s", serviceName, cmd);
				break;
			default:
				serviceName = app;
		}
		
		JSONObject result = deployBiz.executeCmd(hostIds, fullCmd);
		
		if(result != null && !result.getJSONArray("return").isEmpty()) {
			if(!result.getJSONArray("return").getJSONObject(0).isEmpty())
				return Response.ok(200).build();
		}
		
		return Response.ok(result).build();
	}
	
	@Path("/job")
	@GET
	public Response listJob(@QueryParam("param") String param, @QueryParam("currentPage") int currentPage, @QueryParam("pageSize") int pageSize) {
		SaltJobVO vo = new SaltJobVO();
		if(param != null && param.trim().length() > 0) {
			vo.setJobId(param.trim());
			vo.setHostName(param.trim());
			vo.setTitle(param.trim());
			vo.setArg(param.trim());
		}
		
		Pagination pagination = new Pagination();
		pagination.setCurrentPage(currentPage);
		pagination.setPageSize(pageSize);
		
		List<SaltJobVO> jobs = deployBiz.querySaltJob(vo, pagination);
		
		pagination.setTotalCount(((Page<SaltJobVO>) jobs).getTotal());
		pagination.setResult(jobs);
		
		return Response.ok(pagination).build();
	}
	
	@Path("/job/{id}/result")
	@GET
	public Response getJob(@PathParam("id") String id) {
		SaltJobVO job = deployBiz.querySaltJobById(id);
		if(job != null && job.getReturnValue() != null) {
			return Response.ok(JSONObject.parse(job.getReturnValue())).build();
		}
		
		return Response.noContent().build();
	}
	
	@Path("/job")
	@PUT
	public Response executeJob() {
		deployBiz.executeQuartzJob();
		
		return Response.ok(200).build();
	}

}
