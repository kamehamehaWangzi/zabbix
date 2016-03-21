package org.pbccrc.platform.api.zabbix;

import com.alibaba.fastjson.JSONObject;

public interface ZabbixApi {

	public void init();

	public void destory();

	public void setAuth(String auth);
	
	public String apiVersion();

	public JSONObject call(Request request);
	
	public JSONObject customCall(Request request, JSONObject params);

	public boolean login(String user, String password);
	
	public String auth(String user, String password);
	
}
