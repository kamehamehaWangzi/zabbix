package org.pbccrc.platform.monitor.biz.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.pbccrc.platform.api.ApiFactory;
import org.pbccrc.platform.api.zabbix.Request;
import org.pbccrc.platform.api.zabbix.RequestBuilder;
import org.pbccrc.platform.cmdb.dao.AppDao;
import org.pbccrc.platform.model.AppModel;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.monitor.biz.IAppBiz;
import org.pbccrc.platform.util.Constant;
import org.pbccrc.platform.vo.AppVO;
import org.pbccrc.platform.vo.HostAppVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Service
public class AppBizImpl implements IAppBiz {
	
	private static final Logger log = Logger.getLogger(AppBizImpl.class);
	
	@Autowired
	private ApiFactory apiFactory;
	
	@Autowired
	private AppDao appDao;
	
	public List<AppVO> queryApps(AppVO vo, Pagination pagination) {
		return appDao.queryAll(vo, pagination);
	}
	
	public AppVO queryAppById(String id) {
		return appDao.queryById(id);
	}
	
	public List<AppVO> queryAppsByHost(String id) {
		return appDao.queryAppsByHost(id);
	}
	
	public JSONObject listApp(String param) {
		JSONArray output = new JSONArray();
		output.add("hostid");
		output.add("name");
		output.add("host");
		output.add("status");
		
		JSONObject searchInventory = new JSONObject();
		searchInventory.put("type", "app");
		
		JSONArray interfaces = new JSONArray();
		interfaces.add("interfaceid");
		interfaces.add("ip");
		
		JSONArray inventorys = new JSONArray();
		inventorys.add("type");
		inventorys.add("type_full");
		
		RequestBuilder builder = RequestBuilder.newBuilder()
				.paramEntry("output", output)
				.paramEntry("searchInventory", searchInventory)
				.paramEntry("selectInterfaces", interfaces)
				.paramEntry("selectInventory", inventorys);
		
		if(param != null && param.trim().length() > 0) {
			JSONObject filter = new JSONObject();
			filter.put("name", param.trim());
			builder.paramEntry("search", filter);
		}
		
		Request request = builder.method("host.get").build();
		
		log.debug(String.format("listApp call zabbix host.get, request: %s", request.toString()));
		
		return apiFactory.zabbix().call(request);
	}
	
	public JSONObject getAppInfo(String hostId) {
		JSONArray output = new JSONArray();
		output.add("hostid");
		output.add("name");
		output.add("host");
		output.add("status");
		
		JSONArray interfaces = new JSONArray();
		interfaces.add("interfaceid");
		interfaces.add("ip");
		
		JSONArray inventory = new JSONArray();
		inventory.add("name");
		inventory.add("os");
		inventory.add("os_short");
		inventory.add("vendor");
		inventory.add("type");
		inventory.add("type_full");
		
		Request request = RequestBuilder.newBuilder()
				.paramEntry("output", output)
				.paramEntry("selectInterfaces", interfaces)
				.paramEntry("selectInventory", inventory)
				.paramEntry("hostids", hostId)
				.method("host.get")
				.build();
		
		log.debug(String.format("getAppInfo call zabbix host.get, request: %s", request.toString()));
		
		return apiFactory.zabbix().call(request);
	}
	
	public AppModel buildApp(JSONObject obj) {
		if(obj == null || obj.isEmpty()) {
			return null;
		}
		
		String hostId = obj.getString("hostid");
		String type = obj.getJSONObject("inventory").getString("type_full");
		
		AppModel app = new AppModel();
		app.setHostid(obj.getString("hostid"));
		app.setName(obj.getString("name"));
		app.setType(type);
		
		if(Constant.APP_TYPE.mysql.getName().equals(type)) {
			app.setStatus(this.getAppStatus(hostId, Constant.APP_TYPE.mysql.getStatus()));
			app.setVersion(this.getAppVersion(hostId, Constant.APP_TYPE.mysql.getVersion()));
		} else if(Constant.APP_TYPE.oracle.getName().equals(type)) {
			app.setStatus(this.getAppStatus(hostId, Constant.APP_TYPE.oracle.getStatus()));
			app.setVersion(this.getAppVersion(hostId, Constant.APP_TYPE.oracle.getVersion()));
		} else if(Constant.APP_TYPE.tomcat.getName().equals(type)) {
			app.setStatus(this.getAppStatus(hostId, Constant.APP_TYPE.tomcat.getStatus()));
			app.setVersion(this.getAppVersion(hostId, Constant.APP_TYPE.tomcat.getVersion()));
		} else if(Constant.APP_TYPE.weblogic.getName().equals(type)) {
			app.setStatus(this.getAppStatus(hostId, Constant.APP_TYPE.weblogic.getStatus()));
			app.setVersion(this.getAppVersion(hostId, Constant.APP_TYPE.weblogic.getVersion()));
		}
		
		return app;
	}
	
	public String getAppStatus(String hostId, String key) {
		JSONArray output = new JSONArray();
		output.add("itemid");
		output.add("name");
		output.add("key_");
		output.add("hostid");
		output.add("lastvalue");
		
		JSONObject search = new JSONObject();
		search.put("key_", key);
		
		Request request = RequestBuilder.newBuilder()
				.paramEntry("output", output)
				.paramEntry("search", search)
				.paramEntry("hostids", hostId)
				.method("item.get")
				.build();
		
		log.debug(String.format("getAppStatus call zabbix item.get, request: %s", request.toString()));
		
		JSONObject result = apiFactory.zabbix().call(request);
		
		if(result != null && !result.getJSONArray("result").isEmpty()) {
			log.debug(String.format("getAppStatus call zabbix item.get, response: %s", result.toString()));
			
			return result.getJSONArray("result").getJSONObject(0).getString("lastvalue");
		}
		
		return String.valueOf(0);
	}
	
	public String getAppVersion(String hostId, String key) {
		JSONArray output = new JSONArray();
		output.add("itemid");
		output.add("name");
		output.add("key_");
		output.add("hostid");
		output.add("lastvalue");
		
		JSONObject search = new JSONObject();
		search.put("key_", key);
		
		Request request = RequestBuilder.newBuilder()
				.paramEntry("output", output)
				.paramEntry("search", search)
				.paramEntry("hostids", hostId)
				.method("item.get")
				.build();
		
		log.debug(String.format("getAppVersion call zabbix item.get, request: %s", request.toString()));
		
		JSONObject result = apiFactory.zabbix().call(request);
		
		if(result != null && !result.getJSONArray("result").isEmpty()) {
			log.debug(String.format("getAppVersion call zabbix item.get, response: %s", result.toString()));
			
			return result.getJSONArray("result").getJSONObject(0).getString("lastvalue");
		}
		
		return null;
	}
	
	public void addApp(JSONObject app) {
		AppVO vo = new AppVO();
		vo.setName(app.getString("name"));
		vo.setType(app.getString("type"));
		vo.setVersion(app.getString("version"));
		vo.setDescription(app.getString("description"));
		
		appDao.insertApp(vo);
	}
	
	public void deleteApp(String id) {
		appDao.deleteHostAppByAppId(id);
		appDao.deleteApp(id);
	}
	
	public List<HostAppVO> queryHostAppByAppId(Integer appId) {
		return appDao.queryHostAppByAppId(appId);
	}
	
	public void addAppToHost(String appId, JSONArray hostIds) {
		if(hostIds != null) {
			for(int i=0; i<hostIds.size(); i++) {
				Map<String, String> params = new HashMap<String, String>();
				params.put("hostId", hostIds.getString(i));
				params.put("appId", appId);
				
				appDao.insertAppToHost(params);
			}
		}
	}
	
	public void deleteAppFromHost(String appId, String hostIds) {
		if(hostIds != null) {
			for(String id : hostIds.split(",")) {
				Map<String, String> params = new HashMap<String, String>();
				params.put("hostId", id);
				params.put("appId", appId);
				
				appDao.deleteAppFromHost(params);
			}
		}
	}
	
}
