package org.pbccrc.platform.util;

import org.pbccrc.platform.api.zabbix.DefaultZabbixApi;
import org.pbccrc.platform.api.zabbix.Request;
import org.pbccrc.platform.api.zabbix.RequestBuilder;
import org.pbccrc.platform.api.zabbix.ZabbixApi;

import com.alibaba.fastjson.JSONObject;

public class Test extends Thread{

	public static void main(String[] args) {
		
		ZabbixApi zabbixApi = new DefaultZabbixApi();
		zabbixApi.init();
		
		String auth = zabbixApi.auth(Constant.ZABBIX_USERNAME, Constant.ZABBIX_PASSWORD);
		
		RequestBuilder requestBuilder = RequestBuilder.newBuilder().auth(auth)
				.paramEntry("output", "extend")
				.paramEntry("hostids", 10107)
				.paramEntry("sortfield", "name")
				.method("item.get");
		
		Request request = requestBuilder.build();
		
		JSONObject json = zabbixApi.call(request);
		
		System.out.println(json);
		
	}
	
}
