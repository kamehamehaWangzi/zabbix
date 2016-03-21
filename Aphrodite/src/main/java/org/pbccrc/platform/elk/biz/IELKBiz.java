package org.pbccrc.platform.elk.biz;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public interface IELKBiz {
	
	JSONObject getLogListByIndex(String index, Map<String, Object> param, String startDate, String endDate);
	
}
