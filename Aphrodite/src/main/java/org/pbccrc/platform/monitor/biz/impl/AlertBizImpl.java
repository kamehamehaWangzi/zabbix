package org.pbccrc.platform.monitor.biz.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.pbccrc.platform.api.ApiFactory;
import org.pbccrc.platform.api.zabbix.Request;
import org.pbccrc.platform.api.zabbix.RequestBuilder;
import org.pbccrc.platform.monitor.biz.IAlertBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Service
public class AlertBizImpl implements IAlertBiz {
	
	private static final Logger log = Logger.getLogger(AlertBizImpl.class);
	
	@Autowired
	private ApiFactory apiFactory;

	
	public JSONObject listTriggerStatus(String param) {
		JSONArray output = new JSONArray();
		output.add("triggerid");
		output.add("description");
		output.add("priority");
		output.add("lastchange");
		
		JSONObject filter = new JSONObject();
		filter.put("value", 1);
		
		JSONArray selectHosts = new JSONArray();
		selectHosts.add("name");
		selectHosts.add("hostid");
		
		JSONArray sortfield = new JSONArray();
		sortfield.add("priority");
		sortfield.add("lastchange");
		
		RequestBuilder builder = RequestBuilder.newBuilder()
				.paramEntry("output", output)
				.paramEntry("selectHosts", selectHosts)
				.paramEntry("expandComment", true)
				.paramEntry("expandDescription", true)
				.paramEntry("sortfield", sortfield)
				.paramEntry("sortorder", "DESC")
				.method("trigger.get");
		
		if(param != null && param.trim().length() > 0) {
			filter.put("host", param.trim());
		}
		
		builder.paramEntry("filter", filter);
		
		Request request = builder.build();
		
		
		log.debug(String.format("listTriggerStatus call zabbix trigger.get, request: %s", request.toString()));
		
		return apiFactory.zabbix().call(request);
	} 
	public JSONObject getTemplateList(String param){
		JSONArray output = new JSONArray();
		output.add("templateid");
		output.add("name");	
		output.add("host");	
		output.add("description");	
		RequestBuilder builder = RequestBuilder.newBuilder()
				.paramEntry("output", output)			
				.method("template.get");
		JSONObject search = new JSONObject();
		if(param != null && param.trim().length() > 0) {
			search.put("host", param.trim());
			builder.paramEntry("search", search);
		}
		Request request =builder.build();
		log.debug(String.format("getTemplateList call zabbix template.get, request: %s", request.toString()));			
		return apiFactory.zabbix().call(request);
	}
	public JSONArray getItemList(String templateid){ 
		JSONArray output = new JSONArray();
		output.add("itemid");
		output.add("key_");	
		output.add("name");				
		Request request = RequestBuilder.newBuilder()
				.paramEntry("output", output)
				.paramEntry("templateids",templateid)
				.method("item.get")
				.build();
		JSONObject response =apiFactory.zabbix().call(request);
		JSONArray result = response.getJSONArray("result");
		JSONArray items = new JSONArray();
		for(int i=0;i<result.size();i++){
			JSONObject obj = result.getJSONObject(i);
			items.add(obj.getString("key_"));
		}
		return items;
	}
	@Override
	public Integer addAction(JSONObject action) {
		
		RequestBuilder builder =RequestBuilder.newBuilder()
                .paramEntry("name",action.getString("name"))
                .paramEntry("eventsource","0")//默认事件由trigger产生
                .paramEntry("status", "0")//默认action为有效的
                .paramEntry("esc_period", action.getString("time"))
                .paramEntry("def_shortdata", action.getString("title"))
                .paramEntry("def_longdata", action.getString("content"))				                       
                .method("action.create")
                ;
		//封装action的条件			
		JSONObject jo1 =new JSONObject();
		JSONObject jo2 =new JSONObject();
		JSONObject jo3 =new JSONObject();
		JSONObject jo4 =new JSONObject();
		JSONObject jo5 =new JSONObject();
		jo1.put("conditiontype", "3");//条件默认选择触发器名称匹配
		jo1.put("operator", "2");//操作符默认选择like
		jo1.put("value", action.getString("value"));//触发器名名称
		jo1.put("formulaid", "A");
		jo2.put("conditiontype", "5");//条件默认选择触发器值匹配
		jo2.put("operator", "0");//操作符默认选择=
		jo2.put("value", "1");//默认为PROBLEM	
		jo2.put("formulaid", "B");
		JSONArray conditions =new JSONArray();
		JSONArray operations =new JSONArray();
		JSONArray opmessage_grp =new JSONArray();		 		
		conditions.add(jo1);
		conditions.add(jo2);
		JSONObject filter=new JSONObject();
		filter.put("evaltype", "3");
		filter.put("conditions", conditions);
		filter.put("formula", "A and B");
		builder.paramEntry("filter", filter);
		jo3.put("usrgrpid", "7"); 
		opmessage_grp.add(jo3);
		jo4.put("default_msg", "1");
		jo4.put("mediatypeid", action.getString("mediatypeid"));
		jo5.put("operationtype", "0");
		jo5.put("esc_period", "0");
		jo5.put("esc_step_from", action.getString("stepfrom"));
		jo5.put("esc_step_to", action.getString("stepto"));
		jo5.put("evaltype", action.getString("evaltype"));
		jo5.put("opmessage_grp", opmessage_grp);
		jo5.put("opmessage", jo4);
		operations.add(jo5);
		builder.paramEntry("operations", operations);
		Request request = builder.build(); 	
		JSONObject response =apiFactory.zabbix().call(request);	
		Integer actionid = null;
		if(response != null && !response.getJSONObject("result").isEmpty()) {
			actionid = Integer.parseInt(response.getJSONObject("result").getJSONArray("actionids").getString(0));
		}
		return actionid;
	}
	@Override
	public JSONObject getTriggerList(String hostid) {
		JSONArray output = new JSONArray();	
		output.add("value");
		output.add("status");
		output.add("description");
		RequestBuilder builder = RequestBuilder.newBuilder()
				.paramEntry("output", output)
				.paramEntry("expandComment", true)
				.paramEntry("expandDescription", true)				
				.method("trigger.get");
		if(hostid != null && hostid.trim().length() > 0) {
			builder.paramEntry("hostids", hostid.trim());
		}
		Request request =builder.build();
		log.debug(String.format("getTriggerList call zabbix trigger.get, request: %s", request.toString()));			
		return apiFactory.zabbix().call(request);		
	}
	@Override
	public JSONObject getHostList() {
		JSONArray output = new JSONArray();	
		output.add("hostid");
		output.add("name");
		Request request =RequestBuilder.newBuilder()
				         .paramEntry("output", output)
				         .method("host.get")
				         .build();
		log.debug(String.format("getHostList call zabbix host.get, request: %s", request.toString()));			
		return apiFactory.zabbix().call(request);
	}
	@Override
	public JSONObject getTriggerListBySearch(String param,String templateid) {
		JSONArray output = new JSONArray();	
		output.add("value");	
		output.add("description");
		JSONObject search =new JSONObject();
		RequestBuilder builder = RequestBuilder.newBuilder()
				.paramEntry("output", output)
				.paramEntry("expandComment", true)
				.paramEntry("expandDescription", true)
				.paramEntry("templateids", templateid)
				.method("trigger.get");
		if(param != null && param.trim().length() > 0) {
			search.put("description", param.trim());
			builder.paramEntry("search", search);
		}
		Request request =builder.build();
		log.debug(String.format("getTriggerList call zabbix trigger.get, request: %s", request.toString()));			
		return apiFactory.zabbix().call(request);	
	}
	@Override
	public JSONObject getActionList(String param) {
		JSONArray output = new JSONArray();	
		output.add("name");	
		output.add("status");
		JSONObject search =new JSONObject();
		JSONObject filter =new JSONObject();
		filter.put("eventsource", "0");
		RequestBuilder builder = RequestBuilder.newBuilder()
				.paramEntry("output", output)
				.paramEntry("filter", filter)
				.method("action.get");
		if(param != null && param.trim().length() > 0) {
			search.put("name", param.trim());
			builder.paramEntry("search", search);
		}
		Request request =builder.build();
		log.debug(String.format("getActionList call zabbix trigger.get, request: %s", request.toString()));			
		return apiFactory.zabbix().call(request);	
	}
	
}
