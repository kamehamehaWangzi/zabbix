package org.pbccrc.platform.monitor.biz;

import com.alibaba.fastjson.JSONObject;

public interface IGraphBiz {
	
	JSONObject queryItemsByHostKey(String hostId, String key, Boolean likeSearch);
	
	JSONObject queryHistoryByItem(String itemId, Integer type, String startDate, String endDate, Integer defaultDateRange);
	
}
