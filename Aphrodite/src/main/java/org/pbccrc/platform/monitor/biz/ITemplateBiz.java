package org.pbccrc.platform.monitor.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface ITemplateBiz {
	JSONObject getHostGroup();
	
	JSONObject getTemplateList(String groupid);
	
	JSONObject getTemplate(String templateid);
	
	JSONObject getHostList(String templateid,String param);
	
	JSONObject getHostByGroupid(String groupid);
	
	void addHosts(JSONObject host,String templateid);
}
