package org.pbccrc.platform.monitor.biz.impl;

import java.util.Date;

import org.apache.log4j.Logger;
import org.pbccrc.platform.api.ApiFactory;
import org.pbccrc.platform.api.zabbix.Request;
import org.pbccrc.platform.api.zabbix.RequestBuilder;
import org.pbccrc.platform.monitor.biz.IWarningBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Service
public class WarningBizImpl implements IWarningBiz {
	
	private static final Logger log = Logger.getLogger(WarningBizImpl.class);
	
	@Autowired
	private ApiFactory apiFactory;
	
	public JSONObject scanningWarning() throws Exception {
		
		long time = (new Date().getTime() - 2 * 60 * 1000) / 1000;
		
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
				.paramEntry("lastChangeSince", time)
				.method("trigger.get");
		
		
		builder.paramEntry("filter", filter);
		
		Request request = builder.build();
		
		log.debug(String.format("scanningWarning call zabbix trigger.get, request: %s", request.toString()));
		
		return apiFactory.zabbix().call(request);
	}
	
}
