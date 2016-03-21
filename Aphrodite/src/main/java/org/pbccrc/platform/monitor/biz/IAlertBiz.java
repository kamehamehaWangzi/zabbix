package org.pbccrc.platform.monitor.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface IAlertBiz {

	JSONObject listTriggerStatus(String param);

	JSONObject getTemplateList(String param); 
	
	JSONObject getTriggerList(String hostid); 
	
	JSONObject getActionList(String param);
	
	JSONObject getTriggerListBySearch(String param,String templateid);

	JSONArray getItemList(String templateid);
	
	Integer addAction(JSONObject action);
	
	JSONObject getHostList();

}
