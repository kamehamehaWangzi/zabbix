package org.pbccrc.platform.monitor.biz;

import com.alibaba.fastjson.JSONObject;

public interface IGraphBiz {
	
	/**
	 * key 待查询的item, 根据key值反馈出对应的item
	 * @param hostId
	 * @param key
	 * @param likeSearch
	 * @return
	 */
	JSONObject queryItemsByHostKey(String hostId, String key, Boolean likeSearch);
	
	/**
	 * 根据item的Id返回历史数据
	 * @param itemId
	 * @param type
	 * @param startDate
	 * @param endDate
	 * @param defaultDateRange
	 * @return
	 */
	JSONObject queryHistoryByItem(String itemId, Integer type, String startDate, String endDate, Integer defaultDateRange);
	
}
