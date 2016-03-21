package org.pbccrc.platform.monitor.biz.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.pbccrc.platform.api.ApiFactory;
import org.pbccrc.platform.api.zabbix.Request;
import org.pbccrc.platform.api.zabbix.RequestBuilder;
import org.pbccrc.platform.monitor.biz.IGraphBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Service
public class GraphBizImpl implements IGraphBiz {
	
	private static final Logger log = Logger.getLogger(GraphBizImpl.class);

	@Autowired
	private ApiFactory apiFactory;

	@Override
	public JSONObject queryItemsByHostKey(String hostId, String key, Boolean likeSearch) {
		JSONArray output = new JSONArray();
		output.add("itemid");
		output.add("name");
		output.add("value_type");
		output.add("lastvalue");
		output.add("lastclock");
		output.add("status");
		output.add("key_");
		output.add("hostid");
		output.add("units");
		
		RequestBuilder requestBuilder = RequestBuilder.newBuilder()
				.paramEntry("output", output)
				.paramEntry("hostids", hostId)
				.paramEntry("sortfield", "name")
				.method("item.get");
		
		if(likeSearch) {
			JSONObject search = new JSONObject();
			search.put("key_", key);
			
			requestBuilder.paramEntry("search", search);
		} else {
			JSONObject filter = new JSONObject();
			filter.put("key_", key);
			
			requestBuilder.paramEntry("filter", filter);
		}
		
		Request request = requestBuilder.build();
		
		log.debug(String.format("call zabbix item.get, request: %s", request.toString()));

		return apiFactory.zabbix().call(request);
	}
	
	@Override
	public JSONObject queryHistoryByItem(String itemId, Integer type, String startDate, String endDate, Integer defaultDateRange) {
		Long start = null;
		Long end = null;
		if(startDate == null || startDate.trim().length() == 0 || endDate == null || endDate.trim().length() == 0) {
			start = new Date().getTime() / 1000L - (defaultDateRange * 3600);
			end = new Date().getTime() / 1000L;
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			try {
				start = sdf.parse(startDate).getTime() / 1000L;
				end = sdf.parse(endDate).getTime() / 1000L;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		String method = "history.get";
		
		RequestBuilder requestBuilder = RequestBuilder.newBuilder()
				.paramEntry("output", "extend")
				.paramEntry("itemids", itemId)
				.paramEntry("history", type)
				.paramEntry("sortfield", "clock")
				.paramEntry("time_from", start)
				.paramEntry("time_till", end);
		
		if((end - start) > (3600 * 24)) {
			method = "trend.get";
		}
		
		Request request = requestBuilder.method(method).build();
		
		log.debug(String.format("call zabbix history.get, request: %s", request.toString()));
		
		return apiFactory.zabbix().call(request);
	}
	
}
