package org.pbccrc.platform.monitor.biz;

import java.util.List;
import java.util.Map;

import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.vo.HostVO;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface IMachineBiz {
	
	List<HostVO> queryHosts(HostVO vo, Pagination pagination);
	
	HostVO queryHostById(String id);
	
	HostVO queryHostByHostid(String id);
	
	List<HostVO> queryHostsByApp(Map<String, Object> param, Pagination pagination);
	
	List<HostVO> queryHostsExceptApp(String appId);
	
	List<HostVO> queryHosts();
	
	List<String> queryHostsByIds(String ids);
	
	List<String> queryHostsByIds(JSONArray ids);
	
	List<HostVO> queryHostVOsByIds(JSONArray ids);
	
	JSONObject getHostByIdFromZabbix(Integer hostId);
	
	JSONObject listMachine(String param);
	
	JSONObject getMachineInfo(String hostId);
	
	void addHost(JSONObject host);
	
	void deleteHost(String hostId);
	
	JSONObject deleteHostFromZabbix(String hostIds);
	
	JSONObject executeScript(String hostId, String scriptId);
	
	JSONObject queryAllGroup();
	
	JSONObject queryGroup(String name);
	
	JSONObject queryTemplates(JSONArray groupIds);
	
	JSONObject updateHostTemplate(String hostId, JSONArray templates);

	JSONObject getCurrentHostTemplate(String hostId);
	
}
