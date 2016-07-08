package org.pbccrc.platform.deploy.rest;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.pbccrc.platform.deploy.biz.IDeployHostBiz;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.vo.HostAgentVO;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;

@Path("/deployAgent")
public class DeployAgentRest {

	@Autowired
	private IDeployHostBiz deployHostBiz;
	
	@GET
	public Response listAgent(@QueryParam("param")String param,
			@QueryParam("currentPage")int currentPage,
			@QueryParam("pageSize")int pageSize){
		HostAgentVO vo = new HostAgentVO();
		if(param != null && param.trim().length()>0){
			vo.setAgentIp(param);
			vo.setOsType(param);
			vo.setRemark(param);
		}
		
		Pagination pagination = new Pagination();
		pagination.setCurrentPage(currentPage);
		pagination.setPageSize(pageSize);
		
		List<HostAgentVO> hosts = deployHostBiz.queryHostAgents(vo,pagination);
		
		pagination.setResult(hosts);
		pagination.setTotalCount(((Page<HostAgentVO>) hosts).getTotal());
		
		return Response.ok(pagination).build();
	}
	
	@POST
	public Response addAgent(@QueryParam("agent")String agent){
		JSONObject agentInfo = JSONObject.parseObject(agent);
		int result = deployHostBiz.deployRemoteMachine(agentInfo);
		System.out.println(agent);
		return Response.ok(result).build();
	}
	
	@GET
	@Path("/deployProgress")
	public Response deployProgress(){
		String commonStr = deployHostBiz.obtainDeployProgress();
		return Response.ok(commonStr).build();
	}
	
	@DELETE
	public Response deleteAgent(@QueryParam("ids")String ids){
		if(ids != null && ids.trim().length() > 0){
			deployHostBiz.deleteDeployHostAgent(ids);
		}
		return Response.ok(200).build();
	}
	
}
