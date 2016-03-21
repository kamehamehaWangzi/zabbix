package org.pbccrc.platform.monitor.biz.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.pbccrc.platform.api.ApiFactory;
import org.pbccrc.platform.api.zabbix.Request;
import org.pbccrc.platform.api.zabbix.RequestBuilder;
import org.pbccrc.platform.cmdb.dao.HostDao;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.monitor.biz.IMachineBiz;
import org.pbccrc.platform.util.Constant;
import org.pbccrc.platform.vo.HostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


@Service
public class MachineBizImpl implements IMachineBiz {
	
	private static final Logger log = Logger.getLogger(MachineBizImpl.class);

	@Autowired
	private ApiFactory apiFactory;
	
	@Autowired
	private HostDao hostDao;
	
	public List<HostVO> queryHosts(HostVO vo, Pagination pagination) {
		return hostDao.queryAll(vo, pagination);
	}
	
	public HostVO queryHostById(String id) {
		return hostDao.queryById(id);
	}
	
	public HostVO queryHostByHostid(String id) {
		return hostDao.queryByHostid(id);
	}
	
	public List<HostVO> queryHostsByApp(Map<String, Object> param, Pagination pagination) {
		return hostDao.queryHostsByApp(param, pagination);
	}
	
	public List<String> queryHostsByIds(String ids) {
		List<String> hosts = new ArrayList<String>();
		
		if(ids != null && ids.trim().length() > 0) {
			for(String id : ids.split(",")) {
				if(id != null && id.trim().length() > 0) {
					HostVO vo = hostDao.queryById(id);
					hosts.add(vo.getName());
				}
			}
		}
		
		return hosts;
	}
	
	public List<String> queryHostsByIds(JSONArray ids) {
		List<String> hosts = new ArrayList<String>();
		
		if(ids != null && !ids.isEmpty()) {
			for(int i=0; i<ids.size(); i++) {
				HostVO vo = hostDao.queryById(ids.getString(i));
				hosts.add(vo.getName());
			}
		}
		
		return hosts;
	}
	
	public List<HostVO> queryHostVOsByIds(JSONArray ids) {
		List<HostVO> hosts = new ArrayList<HostVO>();
		
		if(ids != null && !ids.isEmpty()) {
			for(int i=0; i<ids.size(); i++) {
				HostVO vo = hostDao.queryById(ids.getString(i));
				hosts.add(vo);
			}
		}
		
		return hosts;
	}
	
	public List<HostVO> queryHostsExceptApp(String appId) {
		return hostDao.queryHostsExceptApp(appId);
	}
	
	public List<HostVO> queryHosts() {
		
		return hostDao.queryHosts();
	}
	
	public JSONObject getHostByIdFromZabbix(Integer hostId) {
		JSONArray output = new JSONArray();
		output.add("hostid");
		output.add("status");
		
		JSONObject filter = new JSONObject();
		filter.put("hostid", hostId);
		
		RequestBuilder builder = RequestBuilder.newBuilder()
				.paramEntry("output", output)
				.paramEntry("filter", filter);
		
		Request request = builder.method("host.get").build();
		
		log.debug(String.format("getHostById call zabbix host.get, request: %s", request.toString()));
		
		return apiFactory.zabbix().call(request);
	}
	
	public JSONObject listMachine(String param) {
		JSONArray output = new JSONArray();
		output.add("hostid");
		output.add("name");
		output.add("host");
		output.add("status");
		
		JSONObject searchInventory = new JSONObject();
		searchInventory.put("type", "host");
		
		JSONArray interfaces = new JSONArray();
		interfaces.add("interfaceid");
		interfaces.add("ip");
		
		RequestBuilder builder = RequestBuilder.newBuilder()
				.paramEntry("output", output)
				.paramEntry("searchInventory", searchInventory)
				.paramEntry("selectInterfaces", interfaces);
		
		if(param != null && param.trim().length() > 0) {
			JSONObject filter = new JSONObject();
			filter.put("host", param.trim());
			builder.paramEntry("search", filter);
		}
		
		Request request = builder.method("host.get").build();
		
		log.debug(String.format("listMachine call zabbix host.get, request: %s", request.toString()));
		
		return apiFactory.zabbix().call(request);
	}
	
	public JSONObject getMachineInfo(String hostId) {
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
		inventory.add("type");
		inventory.add("os");
		inventory.add("os_short");
		inventory.add("vendor");
		
		Request request = RequestBuilder.newBuilder()
				.paramEntry("output", output)
				.paramEntry("selectInterfaces", interfaces)
				.paramEntry("selectInventory", inventory)
				.paramEntry("hostids", hostId)
				.method("host.get")
				.build();
		
		log.debug(String.format("getMachineInfo call zabbix host.get, request: %s", request.toString()));
		
		return apiFactory.zabbix().call(request);
	}
	
	public void addHost(JSONObject host) {
		HostVO vo = new HostVO();
		JSONArray groupArray = host.getJSONArray("groupids");
		vo.setZabbix_groupids(groupArray);
		vo.setGroups(groupArray.toString());
		
		vo.setName(host.getString("name"));
		vo.setType(host.getString("type"));
		vo.setIp1(host.getString("ip"));
		
		String osVersion = host.getString("osVersion");
		if(osVersion != null) {
			if(osVersion.toLowerCase().indexOf("aix") != -1) {
				vo.setOsType(Constant.OS_TYPE.Aix.getName());
			} else if(osVersion.toLowerCase().indexOf("windows") != -1) {
				vo.setOsType(Constant.OS_TYPE.Windows.getName());
			} else {
				vo.setOsType(Constant.OS_TYPE.Linux.getName());
			}
			vo.setIsMonitor(Integer.valueOf(1));
		}
		
		vo.setOsVersion(host.getString("osVersion"));
		vo.setSn(host.getString("sn"));
		vo.setDescription(host.getString("description"));
		JSONArray templateArray = host.getJSONArray("templates");
		vo.setZabbix_templates(templateArray);
		vo.setTeamplates(templateArray.toJSONString());
		
		
		JSONObject result = addHostToZabbix(vo);
		
		if(result != null && !result.getJSONObject("result").isEmpty()) {
			Integer zabbixHostId = Integer.parseInt(result.getJSONObject("result").getJSONArray("hostids").getString(0));
			vo.setZabbixHostid(zabbixHostId);
			
			hostDao.insertHost(vo);
		}
		
	}
	
	public void deleteHost(String hostId) {
		HostVO vo = hostDao.queryByHostid(hostId);
		if(vo != null) {
			hostDao.deleteHostAppByHostId(String.valueOf(vo.getId()));
			hostDao.deleteHost(String.valueOf(vo.getId()));
		}
	}
	
	private JSONObject addHostToZabbix(HostVO vo) {
		JSONArray groups = vo.getZabbix_groupids();
		
		JSONArray interfaces = new JSONArray();
		JSONObject inter = new JSONObject();
		inter.put("type", 1);
		inter.put("main", 1);
		inter.put("useip", 1);
		inter.put("dns", "");
		inter.put("ip", vo.getIp1());
		inter.put("port", "10050");
		interfaces.add(inter);
		
		
		RequestBuilder requestBuilder = RequestBuilder.newBuilder()
				.paramEntry("host", vo.getName())
				.paramEntry("groups", groups)
				.paramEntry("interfaces", interfaces)
				.method("host.create");
		
		if(vo.getZabbix_templates() != null && !vo.getZabbix_templates().isEmpty()) {
			requestBuilder.paramEntry("templates", vo.getZabbix_templates());
		}
		
		Request request = requestBuilder.build();
		
		log.debug(String.format("addHostToZabbix call zabbix host.create, request: %s", request.toString()));
		
		return apiFactory.zabbix().call(request);
	}
	
	public JSONObject deleteHostFromZabbix(String hostIds) {
		JSONArray ids = new JSONArray();
		for(String id : hostIds.split(",")) {
			if(id != null && id.trim().length() > 0) {
				ids.add(id);
			}
		}
		
		JSONObject params = new JSONObject();
		params.put("params", ids);
		
		Request request = RequestBuilder.newBuilder()
				.method("host.delete")
				.build();
		
		log.debug(String.format("deleteHostFromZabbix call zabbix host.delete, request: %s, params: %s", request.toString(), params.toJSONString()));
		
		return apiFactory.zabbix().customCall(request, params);
	}
	
	public JSONObject executeScript(String hostId, String scriptId) {
		if(scriptId == null) {
			return null;
		}
		
		Request request = RequestBuilder.newBuilder()
				.paramEntry("scriptid", scriptId)
				.paramEntry("hostid", hostId)
				.method("script.execute")
				.build();
		
		log.debug(String.format("executeScript call zabbix script.execute, request: %s", request.toString()));
		
		return apiFactory.zabbix().call(request);
	}
	
	public JSONObject queryAllGroup() {
		JSONArray output = new JSONArray();
		output.add("name");
		output.add("groupid");
		
		RequestBuilder builder = RequestBuilder.newBuilder()
				.paramEntry("output", output)
				.method("hostgroup.get");
		
		Request request = builder.build();
		
		log.debug(String.format("queryAllGroup call zabbix hostgroup.get, request: %s", request.toString()));
		
		return apiFactory.zabbix().call(request);
	}
	
	public JSONObject queryGroup(String name) {
		JSONArray output = new JSONArray();
		output.add("name");
		output.add("groupid");
		
		RequestBuilder builder = RequestBuilder.newBuilder()
				.paramEntry("output", output)
				.method("hostgroup.get");
		
		if(name != null && name.trim().length() > 0) {
			JSONObject search = new JSONObject();
			search.put("name", name);
			
			builder.paramEntry("search", search);
		}
		
		Request request = builder.build();
		
		log.debug(String.format("queryGroup call zabbix hostgroup.get, request: %s", request.toString()));
		
		return apiFactory.zabbix().call(request);
	}
	
	public JSONObject queryTemplates(JSONArray groupIds) {
		JSONArray output = new JSONArray();
		output.add("name");
		output.add("templateid");
		output.add("status");
		
		Request request = null;
		RequestBuilder builder = RequestBuilder.newBuilder()
				.paramEntry("output", output)
				.method("template.get");
		
		if(groupIds != null && !groupIds.isEmpty()) {
			builder.paramEntry("groupids", groupIds);
		}
		
		request = builder.build();
		
		log.debug(String.format("queryTemplates call zabbix template.get, request: %s", request.toString()));
		
		return apiFactory.zabbix().call(request);
	}
	
	public JSONObject updateHostTemplate(String hostId, JSONArray templates) {
		if(templates == null || templates.isEmpty()) {
			return null;
		}
		
		JSONArray templateArray = new JSONArray();
		JSONObject temp = null;
		
		JSONObject currentTemplates = this.getCurrentHostTemplate(hostId);
		if(currentTemplates != null && currentTemplates.containsKey("result")) {
			JSONObject hostObj = currentTemplates.getJSONArray("result").getJSONObject(0);
			if(hostObj != null && hostObj.containsKey("parentTemplates") 
					&& !hostObj.getJSONArray("parentTemplates").isEmpty() && hostObj.getJSONArray("parentTemplates").size() > 0) {
				JSONArray currTempArray = hostObj.getJSONArray("parentTemplates");
				
				for(Iterator<Object> it = currTempArray.iterator(); it.hasNext();) {
					JSONObject currTemp = (JSONObject) it.next();
					
					temp = new JSONObject();
					temp.put("templateid", currTemp.getString("templateid"));
					templateArray.add(temp);
				}
			}
			
			for(int i=0; i<templates.size(); i++) {
				temp = new JSONObject();
				temp.put("templateid", templates.getString(i));
				
				templateArray.add(temp);
			}
			
			Request request = RequestBuilder.newBuilder()
					.paramEntry("hostid", hostId)
					.paramEntry("templates", templateArray)
					.method("host.update")
					.build();
			
			log.debug(String.format("updateHostTemplate call zabbix host.update, request: %s", request.toString()));
			
			return apiFactory.zabbix().call(request);
		}
		
		return null;
	}
	
	public JSONObject getCurrentHostTemplate(String hostId) {
		JSONArray selectParentTemplates = new JSONArray();
		selectParentTemplates.add("templateid");
		selectParentTemplates.add("name");
		
		Request request = RequestBuilder.newBuilder()
				.paramEntry("hostids", hostId)
				.paramEntry("selectParentTemplates", selectParentTemplates)
				.method("host.get")
				.build();
		
		log.debug(String.format("getCurrentHostTemplate call zabbix host.get, request: %s", request.toString()));
		
		return apiFactory.zabbix().call(request);
	}
	
}
