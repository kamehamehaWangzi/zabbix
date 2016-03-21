package org.pbccrc.platform.monitor.biz.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.pbccrc.platform.api.ApiFactory;
import org.pbccrc.platform.api.zabbix.Request;
import org.pbccrc.platform.api.zabbix.RequestBuilder;
import org.pbccrc.platform.cmdb.dao.NetworkDao;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.model.PortModel;
import org.pbccrc.platform.model.StorageModel;
import org.pbccrc.platform.monitor.biz.INetworkBiz;
import org.pbccrc.platform.vo.NetworkVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Service
public class NetworkBizImpl implements INetworkBiz {
	
	private static final Logger log = Logger.getLogger(MachineBizImpl.class);

	@Autowired
	private ApiFactory apiFactory;
	
	@Autowired
	private NetworkDao networkDao;

	@Override
	public List<NetworkVO> queryNetworks(NetworkVO vo, Pagination pagination) {
		return networkDao.queryAll(vo, pagination);
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
	@Override
	public NetworkVO queryHostById(String id) {
		return networkDao.queryById(id);
	}
	@Override
	public JSONArray getkeys(String hostId) {
		JSONArray output = new JSONArray();
		output.add("key_");
		JSONArray hostids = new JSONArray();
		hostids.add(hostId);	
		JSONObject search = new JSONObject();
		search.put("key_", "ifIndex");
		Request request = RequestBuilder.newBuilder()
				.paramEntry("output", output)
				.paramEntry("hostids", hostids)
				.paramEntry("search", search)
				.method("item.get")
				.build();
		JSONObject items = apiFactory.zabbix().call(request);
		JSONArray itemKeys = new JSONArray();
		JSONArray result = items.getJSONArray("result");		
		for (int i=0;i<result.size();i++) {
			JSONObject jsonObj = result.getJSONObject(i);		
			String key =jsonObj.getString("key_");
			itemKeys.add(key.substring(key.indexOf("[")));			
		}						
		return itemKeys;
	}
	@Override
	public List<PortModel> queryItems(String hostId, String[] array) {
		JSONArray output = new JSONArray();
		output.add("itemid");		
		output.add("key_");
		JSONObject search = new JSONObject();
		List<PortModel> pms =new ArrayList<PortModel>();
		PortModel pm =new PortModel();
		for(int i=0;i<array.length;i++){
			search.put("key_", array[i]);
			Request request =RequestBuilder.newBuilder()
						.paramEntry("output", output)
						.paramEntry("hostids", hostId)
						.paramEntry("search", search)
						.method("item.get")
						.build();
			JSONObject items =apiFactory.zabbix().call(request);
			JSONArray result =items.getJSONArray("result");
			JSONObject jsonobj =result.getJSONObject(0);			
			String itemid =jsonobj.getString("itemid");
			String key =jsonobj.getString("key_").substring(0, jsonobj.getString("key_").indexOf("["));
			Request req;
			if(array[i].contains("ifAlias")||array[i].contains("ifName")){
				req =RequestBuilder.newBuilder()
						.paramEntry("hostids", hostId)
						.paramEntry("itemids", itemid)
						.paramEntry("history", 1)
						.paramEntry("limit", 1)
						.method("history.get")
						.build();
			}else{
				req =RequestBuilder.newBuilder()
					.paramEntry("hostids", hostId)
					.paramEntry("itemids", itemid)
					.paramEntry("sortorder", "DESC")
					.paramEntry("limit", 1)
					.method("history.get")
					.build();				
			}
			JSONObject res =apiFactory.zabbix().call(req);
			JSONArray resData =res.getJSONArray("result");
			JSONObject jo =resData.getJSONObject(0);
			String value =jo.getString("value");
			switch(key){
			case "ifIndex" :
				pm.setIfIndex(value);
				break;
			case "ifAlias" :
				pm.setIfAlias(value);
				break;
			case "ifType" :
				pm.setIfType(value);
				break;
			case "ifName" :
				pm.setIfName(value);
				break;
			case "ifLastChange" :
				pm.setIfLastChange(value);
				break;
			}
		}
		System.out.println("ifIndex:"+pm.getIfIndex()+"  ifAlias:"+pm.getIfAlias()+"  ifType:"+pm.getIfType()+"  ifName:"+pm.getIfName()+"  ifLastChange:"+pm.getIfLastChange());
		pms.add(pm);
		return pms;		
	}
	
	public List<StorageModel> getDiskValue(String hostId, String[] array){
		JSONArray output =new JSONArray();
		output.add("itemid");
		output.add("key_");
		JSONObject search = new JSONObject();		
		search.put("key_", array[0]);
		Request request =RequestBuilder.newBuilder()
				.paramEntry("output", output)
				.paramEntry("hostids", hostId)
				.paramEntry("search", search)
				.method("item.get")
				.build();
		JSONObject items =apiFactory.zabbix().call(request);
		JSONArray result =items.getJSONArray("result");
		System.out.println("&&&&&"+result);
		List<StorageModel> dms =new ArrayList<StorageModel>();		
		int index =result.size();
		for(int i=1;i<=index;i++){
			StorageModel dm =new StorageModel();
			for(int j=0;j<array.length;j++){								
				search.put("key_", array[j]+i);			
				request =RequestBuilder.newBuilder()
						.paramEntry("output", output)
						.paramEntry("hostids", hostId)
						.paramEntry("search", search)
						.method("item.get")
						.build();
				items =apiFactory.zabbix().call(request);
				result =items.getJSONArray("result");				
				JSONObject jsonobj =result.getJSONObject(0);			
				String itemid =jsonobj.getString("itemid");
				String key =jsonobj.getString("key_").substring(0, jsonobj.getString("key_").length()-1);								
				Request req;
				if(key.contains("stor.domain.health.status")||key.contains("stor.controller.role"))
				{
					req =RequestBuilder.newBuilder()
							.paramEntry("hostids", hostId)
							.paramEntry("itemids", itemid)
							.paramEntry("sortorder", "DESC")
							.paramEntry("limit", 1)
							.method("history.get")
							.build();					
				}else{
					req =RequestBuilder.newBuilder()
							.paramEntry("hostids", hostId)
							.paramEntry("itemids", itemid)
							.paramEntry("history", 1)
							.paramEntry("limit", 1)
							.method("history.get")
							.build();				
				}
				JSONObject res =apiFactory.zabbix().call(req);				
				JSONArray resData =res.getJSONArray("result");
				JSONObject jo =resData.getJSONObject(0);
				String value =jo.getString("value");
				switch(key){
				case "stor.domain.id":
					dm.setD_domainId(value);					
					break;
				case "stor.domain.name":
					dm.setD_domainName(value);					
					break;
				case "stor.domain.health.status":
					dm.setD_domainHealthStat(value);					
					break;
				case "stor.controller.cpu":
					dm.setC_cpuInfo(value);					
					break;
				case "stor.controller.role":
					dm.setC_role(value);					
					break;
				}								
			}
			dms.add(dm);
		}
		return dms;
	}
	@Override
	public void addNetwork(JSONObject network) {
		NetworkVO vo = new NetworkVO();
		vo.setName(network.getString("name"));
		vo.setType(network.getString("type"));
		vo.setIp(network.getString("ip"));					
		vo.setSn(network.getString("sn"));
		vo.setDescription(network.getString("description"));
		vo.setTemplates(network.getJSONArray("templates"));
		vo.setIsMonitor(Integer.valueOf(1));
		JSONObject result = addNetworkToZabbix(vo);
		
		if(result != null && !result.getJSONObject("result").isEmpty()) {
			Integer zabbixHostId = Integer.parseInt(result.getJSONObject("result").getJSONArray("hostids").getString(0));
			vo.setZabbixHostid(zabbixHostId);
			
			networkDao.insertNetwork(vo);
		}	
	}
	private JSONObject addNetworkToZabbix(NetworkVO vo) {
		JSONArray groups = new JSONArray();
		JSONObject group = new JSONObject();
		group.put("groupid", "59");
		groups.add(group);
		
		JSONArray interfaces = new JSONArray();
		JSONObject inter = new JSONObject();
		inter.put("type", 2);
		inter.put("main", 1);
		inter.put("useip", 1);
		inter.put("dns", "");
		inter.put("ip", vo.getIp());
		inter.put("port", "161");
		interfaces.add(inter);
		
		RequestBuilder requestBuilder = RequestBuilder.newBuilder()
				.paramEntry("host", vo.getName())
				.paramEntry("groups", groups)
				.paramEntry("interfaces", interfaces)
				.method("host.create");
		
		if(vo.getTemplates() != null && !vo.getTemplates().isEmpty()) {
			requestBuilder.paramEntry("templates", vo.getTemplates());
		}
		
		Request request = requestBuilder.build();
		
		log.debug(String.format("addNetworkToZabbix call zabbix host.create, request: %s", request.toString()));
		
		return apiFactory.zabbix().call(request);
	}
	@Override
	public void deleteNetwork(String hostId) {
		NetworkVO vo = networkDao.queryByHostid(hostId);
		if(vo != null) {
			networkDao.deleteHost(String.valueOf(vo.getId()));
		}	
	}
	@Override
	public JSONObject deleteNetworkFromZabbix(String hostIds) {
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
		
		log.debug(String.format("deleteNetworkFromZabbix call zabbix host.delete, request: %s, params: %s", request.toString(), params.toJSONString()));
		
		return apiFactory.zabbix().customCall(request, params);
	}
	
}
