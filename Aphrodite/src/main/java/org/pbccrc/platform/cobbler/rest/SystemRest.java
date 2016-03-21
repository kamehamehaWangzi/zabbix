package org.pbccrc.platform.cobbler.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.pbccrc.platform.cobbler.biz.ISystemBiz;
import org.pbccrc.platform.vo.CobblerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Path("/cobbler_system")
public class SystemRest {
	
	private static final Logger log = Logger.getLogger(SystemRest.class);
	
	@Autowired
	@Qualifier("systemBizImpl")
	private ISystemBiz systemBiz;
	
	@Path("/list")
	@GET
	public Response listSystem(@QueryParam("param") String param) {
		
		List<CobblerVO> vos = null;
		try {
			vos = systemBiz.querySystem(param);
		} catch (XmlRpcException e) {
			log.debug("query cobbler system exception :"+e.getMessage());
			return Response.ok("exception:"+e.getMessage()).build();
		}
		return Response.ok(vos).build();
	}
	
	@Path("/delSystem")
	@PUT
	public Response removeSystem(@QueryParam("Ids") String Ids) {
		
		try {
			systemBiz.deleteSystem(Ids);
		} catch (XmlRpcException e) {
			log.debug("delete system exception :"+e.getMessage());
			return Response.ok("exception:"+e.getMessage()).build();		}

		return Response.ok(200).build();
	}
	
	@Path("/loadprofile")
	@GET
	public Response loadprofile() {
		
		List<String> profiles = null;
		try {
			profiles = systemBiz.loadProfiles();
		} catch (XmlRpcException e) {
			log.debug("query cobbler profiles exception :"+e.getMessage());
			return Response.ok("exception:"+e.getMessage()).build();
		}
		return Response.ok(profiles).build();
	}

	@Path("/add")
	@POST
	public Response add(@QueryParam("systems") String systems) {
		log.debug("add cobbler system "+systems);
		JSONObject systemInfo = JSON.parseObject(systems);
		
		try {
			systemBiz.addSystem(systemInfo);
		} catch (XmlRpcException e) {
			log.debug("add system exception :"+e.getMessage());
			return Response.ok("exception:"+e.getMessage()).build();		
			}

		return Response.ok(200).build();
	}

	@Path("/list/{id}/info")
	@GET
	public Response getSystenInfo(@PathParam("id") String id) {
		log.debug("cobbler system uid "+id);
		CobblerVO vo;
		try {
			vo = systemBiz.querySystemById(id);
		} catch (XmlRpcException e) {
			log.debug("find system exception :"+e.getMessage());
			return Response.ok("exception:"+e.getMessage()).build();		
		}
		if (vo != null) {
			return Response.ok(vo).build();
		}

		return Response.noContent().build();
	}
	@Path("/deploy")
	@PUT
	public Response deploySystem(@QueryParam("Ids") String Ids) {
		
		try {
			systemBiz.deploySystem(Ids);
		} catch (XmlRpcException e) {
			log.debug("deploy system exception :"+e.getMessage());
			return Response.ok("exception:"+e.getMessage()).build();		}

		return Response.ok(200).build();
	}
	@Path("/salt")
	@PUT
	public Response installSaltMinion(@QueryParam("Ids") String Ids) {
		
		try {
			systemBiz.installSaltMinion(Ids);
		} catch (XmlRpcException e) {
			log.debug("install salt-minion exception :"+e.getMessage());
			return Response.ok("exception:"+e.getMessage()).build();		}

		return Response.ok(200).build();
	}

}
