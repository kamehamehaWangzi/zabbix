package org.pbccrc.platform.elk.biz.impl;

import java.util.Map;

import org.pbccrc.platform.api.ApiFactory;
import org.pbccrc.platform.elk.biz.IELKBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

@Service
public class ELKBizImpl implements IELKBiz {

	@Autowired
	private ApiFactory apiFactory;
	
	@Override
	public JSONObject getLogListByIndex(String index, Map<String, Object> param, String startDate, String endDate) {
		
		return apiFactory.elk().search(index, param, startDate, endDate);
	}

}
