package org.pbccrc.platform.monitor.rest;

import java.util.ArrayList;
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
import org.pbccrc.platform.deploy.biz.IDeployBiz;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.monitor.biz.IAppBiz;
import org.pbccrc.platform.monitor.biz.IMachineBiz;
import org.pbccrc.platform.vo.AppVO;
import org.pbccrc.platform.vo.HostAppVO;
import org.pbccrc.platform.vo.HostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;

@Path("/app")
public class AppRest {
	
	private static final Logger log = Logger.getLogger(AppRest.class);

	@Autowired
	@Qualifier("appBizImpl")
	private IAppBiz appBiz;
	
	@Autowired
	@Qualifier("machineBizImpl")
	private IMachineBiz machineBiz;
	
	@Autowired
	@Qualifier("deployBizImpl")
	private IDeployBiz deplpyBiz;
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listApp(@QueryParam("param") String param, @QueryParam("currentPage") int currentPage, @QueryParam("pageSize") int pageSize) {
		AppVO vo = new AppVO();
		if(param != null && param.trim().length() > 0) {
			vo.setName(param.trim());
			vo.setType(param.trim());
		}
		
		Pagination pagination = new Pagination();
		pagination.setCurrentPage(currentPage);
		pagination.setPageSize(pageSize);
		
		List<AppVO> apps = appBiz.queryApps(vo, pagination);
		
		pagination.setTotalCount(((Page<AppVO>) apps).getTotal());
		
		if(apps != null) {
			for(AppVO app : apps) {
				List<HostAppVO> hostApp = appBiz.queryHostAppByAppId(app.getId());
				if(hostApp != null) {
					app.setDeployCount(hostApp.size());
				}
			}
		}
		
		pagination.setResult(apps);
		
		log.debug(String.format("listApp, %s", apps));
		
		return Response.ok(pagination).build();
	}
	
	@Path("/info")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAppDetail(@QueryParam("appId") String appId) {
		AppVO app = appBiz.queryAppById(appId);
		
		log.debug(String.format("getAppDetail, %s", app));
		
		return Response.ok(app).build();
	}
	
	@Path("/{id}/host")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getHostApp(@PathParam("id") String id) {
		List<AppVO> apps = appBiz.queryAppsByHost(id);
		
		log.debug(String.format("getHostApp, %s", apps));
		
		return Response.ok(apps).build();
	}
	
	@POST
	public Response addApp(@QueryParam("app") String app) {
		JSONObject appInfo = JSON.parseObject(app);
		appBiz.addApp(appInfo);
		
		return Response.ok(200).build();
	}
	
	@DELETE
	public Response deleteApp(@QueryParam("ids") String ids) {
		if(ids != null && ids.trim().length() > 0) {
			for(String id : ids.split(",")) {
				if(id != null && id.trim().length() > 0) {
					appBiz.deleteApp(id.trim());
				}
			}
		}
		
		return Response.ok(200).build();
	}
	
	@Path("/host")
	@POST
	public Response deployApp(@QueryParam("param") String param) {
		JSONObject paramInfo = JSON.parseObject(param);
		JSONArray hostIds = paramInfo.getJSONArray("hosts");
		
		appBiz.addAppToHost(paramInfo.getString("appId"), hostIds);
		
		List<HostVO> hostVOs = machineBiz.queryHostVOsByIds(hostIds);
		List<String> hosts = new ArrayList<String>();
		for(HostVO vo : hostVOs) {
			hosts.add(vo.getName());
		}
		JSONObject response = deplpyBiz.deployApp(hosts, paramInfo.getString("appType"));
		
		for(HostVO vo : hostVOs) {
			machineBiz.updateHostTemplate(String.valueOf(vo.getZabbixHostid()), paramInfo.getJSONArray("templates"));
		}
		
		log.debug(String.format("deployApp response: %s", response));
		
		return Response.ok(200).build();
	}
	
	@Path("/host")
	@DELETE
	public Response removeApp(@QueryParam("appId") String appId, @QueryParam("hostIds") String hostIds) {
		appBiz.deleteAppFromHost(appId, hostIds);
		
		return Response.ok(200).build();
	}
	
}
