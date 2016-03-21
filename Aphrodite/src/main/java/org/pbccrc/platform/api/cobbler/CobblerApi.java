package org.pbccrc.platform.api.cobbler;

import org.apache.xmlrpc.XmlRpcException;

public interface CobblerApi {
	
	public void init();
	
	public String getToken();
	
	public void setToken(String token);
	
	public boolean login(String user, String password) throws XmlRpcException;
	
	public void execute(String method,Object[] objs) throws XmlRpcException;
	
	public String executeRetStr(String method,Object[] objs) throws XmlRpcException;

	public Object[] executeRetObj(String method,Object[] objs) throws XmlRpcException;
	
	public void sync() throws XmlRpcException;
}
