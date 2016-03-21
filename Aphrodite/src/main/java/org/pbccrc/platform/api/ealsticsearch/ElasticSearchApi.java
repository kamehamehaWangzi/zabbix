package org.pbccrc.platform.api.ealsticsearch;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public interface ElasticSearchApi {
	
	public int searchTotalNum(String index);
	
	public JSONObject search(String index);
	
	public JSONObject search(String index, Map<String, Object> param);
	
	public JSONObject search(String index, Map<String, Object> param, String startDate, String endDate);
	
}
