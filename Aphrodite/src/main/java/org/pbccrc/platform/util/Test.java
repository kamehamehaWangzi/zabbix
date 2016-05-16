package org.pbccrc.platform.util;

import org.pbccrc.platform.api.zabbix.DefaultZabbixApi;
import org.pbccrc.platform.api.zabbix.Request;
import org.pbccrc.platform.api.zabbix.RequestBuilder;
import org.pbccrc.platform.api.zabbix.ZabbixApi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class Test extends Thread{

	public static void main(String[] args) {
		
		ZabbixApi zabbixApi = new DefaultZabbixApi();
		zabbixApi.init();
		
		String auth = zabbixApi.auth(Constant.ZABBIX_USERNAME, Constant.ZABBIX_PASSWORD);
		
		JSONArray interfaces = new JSONArray();
		JSONObject inter = new JSONObject();
		inter.put("type", 1);
//		inter.put("main", 1);
//		inter.put("useip", 1);
//		inter.put("dns", "");
		inter.put("ip", "192.168.62.111");
//		inter.put("port", "10050");
		interfaces.add(inter);
		
		RequestBuilder requestBuilder = RequestBuilder.newBuilder().auth(auth)
				.paramEntry("hostid", 10112)
				.paramEntry("groups", JSONArray.parse("[\"8\"]"))
				.paramEntry("templates", JSONArray.parse("[\"10081\"]"))
				.paramEntry("interfaces", interfaces)
				.method("host.update");
		
		Request request = requestBuilder.build();
		
		JSONObject json = zabbixApi.call(request);
		
		System.out.println(json);
		
	}
	
}
