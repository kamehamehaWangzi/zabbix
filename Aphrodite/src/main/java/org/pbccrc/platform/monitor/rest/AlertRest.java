package org.pbccrc.platform.monitor.rest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.pbccrc.platform.monitor.biz.IAlertBiz;
import org.pbccrc.platform.vo.ActionVO;
import org.pbccrc.platform.vo.HostVO;
import org.pbccrc.platform.vo.TemplateVO;
import org.pbccrc.platform.vo.TriggerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Path("/alert")
public class AlertRest {
	
	private static final Logger log = Logger.getLogger(AlertRest.class);
	
	@Autowired
	@Qualifier("alertBizImpl")
	private IAlertBiz alertBiz;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listTriggerStatus(@QueryParam("param") String param) {
		List<TriggerVO> result = new ArrayList<TriggerVO>();
		JSONObject response = alertBiz.listTriggerStatus(param);
		
		System.out.println(response);
		
		if(response != null && !response.getJSONArray("result").isEmpty()) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			JSONArray array = response.getJSONArray("result");
			for(int i=0; i<array.size(); i++) {
				JSONObject obj = array.getJSONObject(i);
				TriggerVO vo = new TriggerVO();
				
				vo.setTriggerId(obj.getString("triggerid"));
				vo.setHostId(obj.getJSONArray("hosts").getJSONObject(0).getString("hostid"));
				vo.setHostName(obj.getJSONArray("hosts").getJSONObject(0).getString("name"));
				vo.setDescription(obj.getString("description"));
				vo.setLastChange(sdf.format(new Date(Long.parseLong(obj.getString("lastchange")) * 1000L)));
				vo.setPriority(obj.getString("priority"));
				
				result.add(vo);
			}
		}
		
		log.debug(String.format("listTriggerStatus, %s", result));
		
		return Response.ok(result).build();
	}	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/template")
	public Response getTemplateList(@QueryParam("param") String param) {
		List<TemplateVO> result = new ArrayList<TemplateVO>();
		JSONObject response = alertBiz.getTemplateList(param);
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
		log.debug(String.format("listTempaltes, %s", result));
		return Response.ok(result).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/host")
	public Response getHostList() {
		List<HostVO> result = new ArrayList<HostVO>();
		JSONObject response = alertBiz.getHostList();
		if(response !=null && !response.getJSONArray("result").isEmpty()){
			JSONArray array = response.getJSONArray("result");
			for(int i=0;i<array.size();i++){
				JSONObject obj = array.getJSONObject(i);
				HostVO vo =new HostVO();
				vo.setZabbixHostid(Integer.valueOf(obj.getString("hostid")));
				vo.setName(obj.getString("name"));
				result.add(vo);
			}
		}
		log.debug(String.format("HostList, %s", result));
		return Response.ok(result).build();
	}	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/trigger")
	public Response getTriggerListByHostid(@QueryParam("hostid") String hostid) {
		List<TriggerVO> result = new ArrayList<TriggerVO>();
		JSONObject response = alertBiz.getTriggerList(hostid);
		if(response !=null && !response.getJSONArray("result").isEmpty()){
			JSONArray array = response.getJSONArray("result");
			for(int i=0;i<array.size();i++){
				JSONObject obj = array.getJSONObject(i);
				TriggerVO vo =new TriggerVO();
				vo.setValue(obj.getString("value"));
				vo.setStatus(obj.getString("status"));
				vo.setDescription(obj.getString("description"));
				result.add(vo);
			}
		}
		log.debug(String.format("TriggerList, %s", result));
		return Response.ok(result).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/action")
	public Response getActionList(@QueryParam("param") String param) {
		List<ActionVO> result = new ArrayList<ActionVO>();
		JSONObject response = alertBiz.getActionList(param);
		if(response !=null && !response.getJSONArray("result").isEmpty()){
			JSONArray array = response.getJSONArray("result");
			for(int i=0;i<array.size();i++){
				JSONObject obj = array.getJSONObject(i);
				ActionVO vo =new ActionVO();
				vo.setName(obj.getString("name"));
				vo.setStatus(obj.getString("status"));
				result.add(vo);
			}
		}
		log.debug(String.format("ActionList, %s", result));
		return Response.ok(result).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/search")
	public Response getTriggerListBySearch(@QueryParam("param") String param,@QueryParam("templateid") String templateid) {
		List<TriggerVO> result = new ArrayList<TriggerVO>();
		JSONObject response = alertBiz.getTriggerListBySearch(param,templateid);
		if(response !=null && !response.getJSONArray("result").isEmpty()){
			JSONArray array = response.getJSONArray("result");
			for(int i=0;i<array.size();i++){
				JSONObject obj = array.getJSONObject(i);
				TriggerVO vo =new TriggerVO();
				vo.setValue(obj.getString("value"));
				vo.setDescription(obj.getString("description"));
				result.add(vo);
			}
		}
		log.debug(String.format("TriggerList, %s", result));
		return Response.ok(result).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/item")
	public JSONArray getTemplateItems(@QueryParam("name") String templateid){
		JSONArray items =alertBiz.getItemList(templateid);
		log.debug(String.format("getTemplateItems, %s", items));
		return items;
	}
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/create")
	public Response addAction(@QueryParam("action") String action) {
		JSONObject actionInfo = JSON.parseObject(action);
		Integer actionid = alertBiz.addAction(actionInfo);		
		return Response.ok(actionid).build();
	}

}
