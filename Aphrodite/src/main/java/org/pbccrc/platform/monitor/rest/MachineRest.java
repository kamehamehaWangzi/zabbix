package org.pbccrc.platform.monitor.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.pbccrc.platform.deploy.biz.IDeployBiz;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.monitor.biz.IMachineBiz;
import org.pbccrc.platform.vo.HostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;

@Path("/machine")
@Produces(MediaType.APPLICATION_JSON)
public class MachineRest {
	
	private static final Logger log = Logger.getLogger(MachineRest.class);
	
	@Autowired
	@Qualifier("machineBizImpl")
	private IMachineBiz machineBiz;
	
	@Autowired
	@Qualifier("deployBizImpl")
	private IDeployBiz deployBiz;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listMachine(@QueryParam("param") String param, @QueryParam("currentPage") int currentPage, @QueryParam("pageSize") int pageSize) {
		HostVO vo = new HostVO();
		vo.setIsMonitor(Integer.valueOf(1));
		if(param != null && param.trim().length() > 0) {
			vo.setName(param.trim());
			vo.setIp1(param.trim());
		}
		
		Pagination pagination = new Pagination();
		pagination.setCurrentPage(currentPage);
		pagination.setPageSize(pageSize);
		
		List<HostVO> hosts = machineBiz.queryHosts(vo, pagination);
		
		pagination.setTotalCount(((Page<HostVO>) hosts).getTotal());
		
		if(hosts != null) {
			for(HostVO host: hosts) {
				if(host.getZabbixHostid() != null) {
					JSONObject obj = machineBiz.getHostByIdFromZabbix(host.getZabbixHostid());
					if(obj != null && !obj.getJSONArray("result").isEmpty()) {
						host.setStatus(obj.getJSONArray("result").getJSONObject(0).getInteger("status"));
					}
				}
			}
		}
		
		pagination.setResult(hosts);
		
		log.debug(String.format("listMachine, %s", hosts));
		
		return Response.ok(pagination).build();
	}
	
	@Path("/{id}/info")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMachineDetail(@PathParam("id") String id) {
		HostVO host = machineBiz.queryHostById(id);
		
		log.debug(String.format("getMachineDetail, %s", host));
		
		return Response.ok(host).build();
	}
	
	@Path("/{id}/app")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAppMachine(@PathParam("id") String id, @QueryParam("nameOrIp") String nameOrIp, @QueryParam("currentPage") int currentPage, @QueryParam("pageSize") int pageSize) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("appId", id);
		if(nameOrIp != null && nameOrIp.trim().length() > 0) {
			param.put("name", nameOrIp.trim());
			param.put("ip1", nameOrIp.trim());
		}
		
		Pagination pagination = new Pagination();
		pagination.setCurrentPage(currentPage);
		pagination.setPageSize(pageSize);
		
		List<HostVO> hosts = machineBiz.queryHostsByApp(param, pagination);
		
		pagination.setTotalCount(((Page<HostVO>) hosts).getTotal());
		
		if(hosts != null) {
			for(HostVO vo : hosts) {
				if(deployBiz.isServiceAlive(id, vo.getName())) {
					vo.setStatus(Integer.valueOf(1));
				} else {
					vo.setStatus(Integer.valueOf(0));
				}
			}
		}
		
		pagination.setResult(hosts);
		
		log.debug(String.format("getAppMachine, %s", hosts));
		
		return Response.ok(pagination).build();
	}
	
	@POST
	public Response addHost(@QueryParam("host") String host) {
		JSONObject hostInfo = JSON.parseObject(host);
		machineBiz.addHost(hostInfo);
		
		return Response.ok(200).build();
	}
	
	@DELETE
	public Response deleteHost(@QueryParam("ids") String ids) {
		if(ids != null && ids.trim().length() > 0) {
			JSONObject response = machineBiz.deleteHostFromZabbix(ids);
			
			log.debug(String.format("deleteHost, response: %s", response));
			if(response != null && response.containsKey("result") && !response.getJSONObject("result").isEmpty()) {
				JSONArray hostIds = response.getJSONObject("result").getJSONArray("hostids");
				
				for(int i=0; i<hostIds.size(); i++) {
					machineBiz.deleteHost(hostIds.getString(i));
				}
			}
		}
		
		return Response.ok(200).build();
	}
	
	@Path("/except")
	@GET
	public Response loadHostsExceptApp(@QueryParam("appId") String appId) {
		List<HostVO> hosts = machineBiz.queryHostsExceptApp(appId);
		
		return Response.ok(hosts).build();
	}
	
	@Path("/hosts")
	@GET
	public Response loadHosts() {
		List<HostVO> hosts = machineBiz.queryHosts();
		
		return Response.ok(hosts).build();
	}
	
	@Path("/script")
	@POST
	public Response executeScript(@QueryParam("hostId") String hostId, @QueryParam("script") String script) {
		String scriptId = null;
		if("ping".equals(script)) {
			scriptId = "1";
		} else if ("traceroute".equals(script)) {
			scriptId = "2";
		} else if ("detectOperatingSystem".equals(script)) {
			scriptId = "3";
		}
		
		if(scriptId == null) {
			return Response.ok("fail, script action error.").build();
		} else {
			JSONObject result = machineBiz.executeScript(hostId, scriptId);
			
			log.debug(String.format("executeScript, %s: %s", script, result));
			
			return Response.ok(result.getJSONObject("result")).build();
		}
		
	}
	
	@Path("/groupAll")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listGroup() {
		JSONObject groups = machineBiz.queryAllGroup();
		return Response.ok(groups).build();
	}
	
	@Path("/group")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listGroup(@QueryParam("name") String name) {
		JSONObject groups = machineBiz.queryGroup(name);
		return Response.ok(groups).build();
	}
	
	@Path("/template")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listTemplate(@QueryParam("group") String group) {
		JSONObject groups = machineBiz.queryGroup(group);
		JSONArray groupIds = new JSONArray();
		
		if(groups != null && !groups.isEmpty() && groups.containsKey("result") && !groups.getJSONArray("result").isEmpty()) {
			JSONArray groupArray = groups.getJSONArray("result");
			
			for(int i=0; i<groupArray.size(); i++) {
				groupIds.add(groupArray.getJSONObject(i).getString("groupid"));
			}
		}
		
		JSONObject temps = machineBiz.queryTemplates(groupIds);
		
		return Response.ok(temps).build();
	}
	
	
}
