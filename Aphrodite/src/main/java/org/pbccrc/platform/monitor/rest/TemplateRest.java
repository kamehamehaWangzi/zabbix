package org.pbccrc.platform.monitor.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.pbccrc.platform.monitor.biz.ITemplateBiz;
import org.pbccrc.platform.vo.HostGroupVO;
import org.pbccrc.platform.vo.HostVO;
import org.pbccrc.platform.vo.TemplateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Path("/template")
public class TemplateRest {
	
	private static final Logger log = Logger.getLogger(AlertRest.class);
	
	@Autowired
	@Qualifier("templateBizImpl")
	private ITemplateBiz templateBiz;			
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/hostgroup")
	public Response getHostGroup() {
		List<HostGroupVO> result =new ArrayList<HostGroupVO>();
		JSONObject response = templateBiz.getHostGroup();
		if(response !=null && response.getJSONArray("result") !=null && !response.getJSONArray("result").isEmpty()){
			JSONArray array = response.getJSONArray("result");
			for(int i=0;i<array.size();i++){
				JSONObject obj = array.getJSONObject(i);
				HostGroupVO vo =new HostGroupVO();
				vo.setGroupid(obj.getString("groupid"));
				vo.setName(obj.getString("name"));
				vo.setInternal(obj.getString("internal"));
				result.add(vo);
			}
		}
		
		System.out.println(result);
		
		return Response.ok(result).build();
	}	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/templatelist")
	public Response getTemplateList(@QueryParam("groupid") String groupid) {
		List<TemplateVO> result = new ArrayList<TemplateVO>();
		JSONObject response = templateBiz.getTemplateList(groupid);
		if(response !=null && !response.getJSONArray("result").isEmpty()){
			JSONArray array = response.getJSONArray("result");
			for(int i=0;i<array.size();i++){
				JSONObject obj = array.getJSONObject(i);
				TemplateVO vo =new TemplateVO();
				vo.setName(obj.getString("name"));
				vo.setHost(obj.getString("host"));
				vo.setTemplateid(obj.getString("templateid"));
				vo.setDescription(obj.getString("description"));
				result.add(vo);
			}
		}
		log.debug(String.format("getTemplateList, %s", result));
		return Response.ok(result).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)	
	public Response getTemplate(@QueryParam("templateid") String templateid) {
		JSONObject response = templateBiz.getTemplate(templateid);
		TemplateVO vo =new TemplateVO();
		if(response !=null && !response.getJSONArray("result").isEmpty()){
			JSONArray array = response.getJSONArray("result");			
			JSONObject obj = array.getJSONObject(0);			
			vo.setName(obj.getString("name"));
			vo.setHost(obj.getString("host"));
			vo.setTemplateid(obj.getString("templateid"));
			vo.setDescription(obj.getString("description"));							
		}
		log.debug(String.format("Tempalte, %s", vo));
		return Response.ok(vo).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/host")
	public Response getHostList(@QueryParam("templateid") String templateid,@QueryParam("param") String param) {
		List<HostVO> result = new ArrayList<HostVO>();
		JSONObject response = templateBiz.getHostList(templateid,param);
		if(response !=null && !response.getJSONArray("result").isEmpty()){
			JSONArray array = response.getJSONArray("result");
			for(int i=0;i<array.size();i++){
				JSONObject obj = array.getJSONObject(i);
				HostVO vo =new HostVO();
				vo.setName(obj.getString("host"));
				vo.setStatus(Integer.parseInt(obj.getString("status")));
				vo.setZabbixHostid(Integer.parseInt(obj.getString("hostid")));
				result.add(vo);
			}
		}
		log.debug(String.format("getHostList, %s", result));
		return Response.ok(result).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/list")
	public Response getHostByGroupid(@QueryParam("groupid") String groupid) {
		JSONObject response = templateBiz.getHostByGroupid(groupid);		
		log.debug(String.format("getHostByGroupid, %s", response));
		return Response.ok(response).build();
	}
	
	@POST
	public Response addHost(@QueryParam("host") String host,@QueryParam("templateid") String templateid) {
		JSONObject hostInfo = JSON.parseObject(host);
		templateBiz.addHosts(hostInfo,templateid);		
		return Response.ok(200).build();
	}
}
