package org.pbccrc.platform.identity.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.pbccrc.platform.model.UserModel;
import org.pbccrc.platform.util.Constant;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Path("/identity")
public class LoginRest {
	
	private static final Logger log = Logger.getLogger(LoginRest.class);
	
	@Context
	private HttpServletRequest request;
	
	@Path("/login")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response userLogin(String user) {
		JSONObject userInfo = JSON.parseObject(user);
		
		log.debug(String.format("User '%s' login.", userInfo.getString("name")));
		
		//TODO user ldap auth
		
		UserModel userModel = new UserModel();
		userModel.setName(userInfo.getString("name"));
		request.getSession().setAttribute(Constant.SESSION_USER_KEY, userModel);
		
		return Response.ok(200).build();
	}
	
	@Path("/logout")
	@POST
	public Response userLogout() {
		
		request.getSession().invalidate();
		
		return Response.ok(200).build();
	}
	
	@Path("/info")
	@GET
	public Response userInfo() {
		UserModel userModel = new UserModel();
		Object obj = request.getSession().getAttribute(Constant.SESSION_USER_KEY);
		if(obj != null) {
			userModel = (UserModel) obj;
		}
		
		return Response.ok(userModel).build();
	}
	
}
