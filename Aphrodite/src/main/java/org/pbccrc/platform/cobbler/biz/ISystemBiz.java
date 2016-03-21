package org.pbccrc.platform.cobbler.biz;

import java.util.List;

import org.apache.xmlrpc.XmlRpcException;
import org.pbccrc.platform.vo.CobblerVO;

import com.alibaba.fastjson.JSONObject;

public interface ISystemBiz {

	
	List<CobblerVO> querySystem(String param) throws XmlRpcException;
	
	void deleteSystem(String ids) throws XmlRpcException;
	
	List<String> loadProfiles() throws XmlRpcException;
	
	void addSystem(JSONObject systems) throws XmlRpcException;
	
	CobblerVO querySystemById(String id) throws XmlRpcException;
	
	void deploySystem(String ids) throws XmlRpcException;
	void installSaltMinion(String ids) throws XmlRpcException;
}
