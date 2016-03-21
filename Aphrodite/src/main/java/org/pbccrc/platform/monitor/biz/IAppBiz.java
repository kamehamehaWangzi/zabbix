package org.pbccrc.platform.monitor.biz;

import java.util.List;

import org.pbccrc.platform.model.AppModel;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.vo.AppVO;
import org.pbccrc.platform.vo.HostAppVO;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface IAppBiz {
	
	List<AppVO> queryApps(AppVO vo, Pagination pagination);
	
	AppVO queryAppById(String id);
	
	List<AppVO> queryAppsByHost(String id);
	
	JSONObject listApp(String param);
	
	JSONObject getAppInfo(String hostId);
	
	AppModel buildApp(JSONObject obj);
	
	String getAppStatus(String hostId, String key);
	
	String getAppVersion(String hostId, String key);
	
	void addApp(JSONObject app);
	
	void deleteApp(String id);
	
	List<HostAppVO> queryHostAppByAppId(Integer appId);
	
	void addAppToHost(String appId, JSONArray hostIds);
	
	void deleteAppFromHost(String appId, String hostIds);
	
}
