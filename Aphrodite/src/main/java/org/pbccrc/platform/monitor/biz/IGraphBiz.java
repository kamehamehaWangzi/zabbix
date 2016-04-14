package org.pbccrc.platform.monitor.biz;

import com.alibaba.fastjson.JSONObject;

public interface IGraphBiz {
	
	/**
	 * key ����ѯ��item, ����keyֵ��������Ӧ��item
	 * @param hostId
	 * @param key
	 * @param likeSearch
	 * @return
	 */
	JSONObject queryItemsByHostKey(String hostId, String key, Boolean likeSearch);
	
	/**
	 * ����item��Id������ʷ����
	 * @param itemId
	 * @param type
	 * @param startDate
	 * @param endDate
	 * @param defaultDateRange
	 * @return
	 */
	JSONObject queryHistoryByItem(String itemId, Integer type, String startDate, String endDate, Integer defaultDateRange);
	
}
