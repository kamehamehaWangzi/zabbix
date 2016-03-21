package org.pbccrc.platform.api.saltstack;

import com.alibaba.fastjson.JSONObject;

public interface SaltstackApi {
	
	public void init();
	
	public String getToken();
	
	public void setToken(String token);
	
	public boolean login(String user, String password);
	
	public String auth(String user, String password);

	public JSONObject call(SaltRequest request);
	
	public JSONObject job(String jobId);
	
}
