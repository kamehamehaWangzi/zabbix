package org.pbccrc.platform.elk.rest;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.pbccrc.platform.elk.biz.IELKBiz;
import org.pbccrc.platform.monitor.biz.IMachineBiz;
import org.pbccrc.platform.vo.HostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Path("/elk")
public class ELKRest {
	private static final Logger log = Logger.getLogger(ELKRest.class);

	@Autowired
	private IELKBiz elkBiz;
	
	@Autowired
	@Qualifier("machineBizImpl")
	private IMachineBiz machineBiz;
	
	@Path("/log")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listLog(@QueryParam("id") String id, @QueryParam("app") String app, @QueryParam("type") String type, 
			@QueryParam("param") String param, @QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate) {
		
		Map<String, Object> paramMap = null;
		if(param != null && param.trim().length() > 0) {
			paramMap = new HashMap<String, Object>();
			
			JSONObject json = JSON.parseObject(param);
			log.debug(String.format("listLog param: %s", json));
			
			for(String key : json.keySet()) {
				paramMap.put(key, json.get(key));
			}
		}
		
		HostVO host = machineBiz.queryHostByHostid(id);
		if(host == null) {
			throw new RuntimeException(String.format("can not find HOST info by id: %s", id));
		}
		
		String index = String.format("%s-%s-%s-*", app, type, host.getIp1());
		JSONObject result = elkBiz.getLogListByIndex(index, paramMap, startDate, endDate);
		
		log.debug(String.format("listLog response: %s", result));
		
		return Response.ok(result).build();
	}
}
