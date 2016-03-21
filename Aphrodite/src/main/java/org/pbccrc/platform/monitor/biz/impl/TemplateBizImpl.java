package org.pbccrc.platform.monitor.biz.impl;

import org.apache.log4j.Logger;
import org.pbccrc.platform.api.ApiFactory;
import org.pbccrc.platform.api.zabbix.Request;
import org.pbccrc.platform.api.zabbix.RequestBuilder;
import org.pbccrc.platform.monitor.biz.ITemplateBiz;
import org.pbccrc.platform.vo.HostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Service
public class TemplateBizImpl implements ITemplateBiz {
	
	private static final Logger log = Logger.getLogger(AlertBizImpl.class);
	
	@Autowired
	private ApiFactory apiFactory;
	@Override
	public JSONObject getHostGroup() {
		JSONArray output =new JSONArray();
		output.add("groupid");
		output.add("name");
		output.add("internal");
		Request request = RequestBuilder.newBuilder()
				.paramEntry("output", output)
				.method("hostgroup.get")
				.build();
		log.debug(String.format("getHostGroup call zabbix hostgroup.get, request: %s", request.toString()));
		return apiFactory.zabbix().call(request);
	}
	@Override
	public JSONObject getTemplateList(String groupid) {	
		JSONArray output = new JSONArray();
		output.add("templateid");
		output.add("name");	
		output.add("host");	
		output.add("description");	
		RequestBuilder builder = RequestBuilder.newBuilder()
				.paramEntry("output", output)
				.method("template.get");
		if(groupid != null && groupid.trim().length() > 0) {		
			builder.paramEntry("groupids", groupid.trim());
		}
		Request request =builder.build();
		log.debug(String.format("getTemplateList call zabbix template.get, request: %s", request.toString()));			
		return apiFactory.zabbix().call(request);
	}
	@Override
	public JSONObject getTemplate(String templateid) {
		JSONArray output = new JSONArray();
		output.add("templateid");
		output.add("name");	
		output.add("host");	
		output.add("description");	
		RequestBuilder builder = RequestBuilder.newBuilder()
				.paramEntry("output", output)
				.method("template.get");
		if(templateid != null && templateid.trim().length() > 0) {		
			builder.paramEntry("templateids", templateid.trim());
		}
		Request request =builder.build();
		log.debug(String.format("getTemplate call zabbix template.get, request: %s", request.toString()));			
		return apiFactory.zabbix().call(request);
	}
	@Override
	public JSONObject getHostList(String templateid,String param) {
		JSONArray output = new JSONArray();	
		output.add("hostid");
		output.add("status");
		output.add("host");
		RequestBuilder builder = RequestBuilder.newBuilder()
				.paramEntry("output", output)			
				.method("host.get");
		JSONObject search =new JSONObject();		
		if(templateid != null && templateid.trim().length() > 0) {
			builder.paramEntry("templateids", templateid.trim());
		}
		if(param != null && param.trim().length() > 0){
			search.put("host", param.trim());
			builder.paramEntry("search", search);
		}
		Request request =builder.build();
		log.debug(String.format("getHostList call zabbix host.get, request: %s", request.toString()));			
		return apiFactory.zabbix().call(request);
	}
	@Override
	public JSONObject getHostByGroupid(String groupid) {
		JSONArray output = new JSONArray();	
		output.add("hostid");
		output.add("status");
		output.add("host");
		RequestBuilder builder = RequestBuilder.newBuilder()
				.paramEntry("output", output)			
				.method("host.get");
		if(groupid != null && groupid.trim().length() > 0){
			builder.paramEntry("groupids", groupid.trim());
		}
		Request request =builder.build();
		log.debug(String.format("getHostByGroupid call zabbix host.get, request: %s", request.toString()));			
		return apiFactory.zabbix().call(request);
	}
	@Override
	public void addHosts(JSONObject host,String templateid) {
		JSONArray hostids =host.getJSONArray("hostids");
		for(int i=0;i<hostids.size();i++){
			String hostid = (String) hostids.get(i);
			JSONArray output = new JSONArray();
			JSONArray selectParentTemplates = new JSONArray();
			output.add("hostid");			
			selectParentTemplates.add("templateid");
			RequestBuilder builder = RequestBuilder.newBuilder()
					.paramEntry("output", output)
					.paramEntry("selectParentTemplates", selectParentTemplates)
					.method("host.get");
			if(hostid != null && hostid.trim().length() > 0){
				builder.paramEntry("hostids", hostid.trim());
			}
			Request request =builder.build();
			JSONObject response = apiFactory.zabbix().call(request);
			if(response !=null && !response.getJSONArray("result").isEmpty()){
				JSONArray ja = response.getJSONArray("result")
						.getJSONObject(0)
						.getJSONArray("parentTemplates");
				JSONArray templateids =new JSONArray();
				for(int j =0;j<ja.size();j++){
					templateids.add(ja.getJSONObject(j).getString("templateid"));
				}
				templateids.add(templateid);
				JSONArray templates = new JSONArray();
				for(int k =0;k<templateids.size();k++){
					JSONObject obj = new JSONObject();
					obj.put("templateid", templateids.get(k));
					templates.add(obj);
				}
				Request req =RequestBuilder.newBuilder()
						.paramEntry("templates", templates)
						.paramEntry("hostid", hostid)
						.method("host.update")
						.build();
				JSONObject res =apiFactory.zabbix().call(req);
				if(res !=null && !res.getJSONObject("result").isEmpty()){				
					System.out.println(res.getJSONObject("result"));
				}
								
				
			}
		}
		
	}
}
