package org.pbccrc.platform.monitor.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.pbccrc.platform.monitor.biz.IWarningBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alibaba.fastjson.JSONObject;

@Path("/warning")
public class WarningRest {
	
	@Autowired
	@Qualifier("warningBizImpl")
	private IWarningBiz warningBiz;
	
	@GET
	@Path("/scanningWarning")
	@Produces(MediaType.APPLICATION_JSON)
	public Response scanningWarning() throws Exception {
		
		JSONObject response = warningBiz.scanningWarning();
		
		return Response.ok(response).build();
	}

}
