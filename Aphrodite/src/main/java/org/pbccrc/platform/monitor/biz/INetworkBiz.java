package org.pbccrc.platform.monitor.biz;

import java.util.List;

import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.model.PortModel;
import org.pbccrc.platform.model.StorageModel;
import org.pbccrc.platform.vo.NetworkVO;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface  INetworkBiz {
	
	List<NetworkVO> queryNetworks(NetworkVO vo, Pagination pagination);
	
	JSONObject getHostByIdFromZabbix(Integer hostId);
	
	NetworkVO queryHostById(String id);
	
	JSONArray getkeys(String hostId); 
	
	List<PortModel> queryItems(String hostId,String[] array);
	
	void addNetwork(JSONObject network);
	
    void deleteNetwork(String hostId);
	
	JSONObject deleteNetworkFromZabbix(String hostIds);
	
	List<StorageModel> getDiskValue(String hostId,String[] array);
}
