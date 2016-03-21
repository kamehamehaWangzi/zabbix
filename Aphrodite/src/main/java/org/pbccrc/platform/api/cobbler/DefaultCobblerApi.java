package org.pbccrc.platform.api.cobbler;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.pbccrc.platform.util.Constant;
import org.springframework.stereotype.Component;

@Component
public class DefaultCobblerApi implements CobblerApi {
	
	private static final Logger logger = Logger.getLogger(DefaultCobblerApi.class);
	
	private XmlRpcClient client;
	
	private String token;
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public void init() {
		if(this.client == null) {
			
				 XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
				 try {
					config.setServerURL(new URL(Constant.COBBLER_API_URL));
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				  XmlRpcClient client = new XmlRpcClient();
				  client.setConfig(config);
				this.client = client;
		}

	}
	
	@Override
	public boolean login(String user, String password) throws XmlRpcException {
		Object[] params = new Object[]{user,password};
		String token_tmp = (String) client.execute("login", params);
		
		logger.debug(String.format("cobbler api login, token is: %s", token_tmp));

		if(!"".equals(token_tmp)&&token_tmp!=null) {
			
			this.token=token_tmp;
				return true;
		}
		
		return false;
	}
	
	@Override
	public void execute(String method, Object[] objs) throws XmlRpcException {
		boolean flag=false;
		
		flag=(boolean)client.execute(method, objs);
		
		logger.debug(String.format("cobbler api execute %s method is %s", method,flag));
	}

	@Override
	public String executeRetStr(String method, Object[] objs)
			throws XmlRpcException {
		String ret="";
		
		ret=(String)client.execute(method, objs);
		logger.debug(String.format("cobbler api execute %s method is %s", method,ret));
		return ret;
	}

	@Override
	public Object[] executeRetObj(String method, Object[] objs)
			throws XmlRpcException {
		Object[] rets=null;
		
		rets=(Object[])client.execute(method, objs);
		for(Object obj:rets){
			logger.debug(String.format("cobbler api execute %s method is %s", method,obj));
		}
		
		return rets; 
	}

	@Override
	public void sync() throws XmlRpcException {
		// TODO Auto-generated method stub
		this.execute("sync", new Object[]{this.getToken()});
	}
	
}
