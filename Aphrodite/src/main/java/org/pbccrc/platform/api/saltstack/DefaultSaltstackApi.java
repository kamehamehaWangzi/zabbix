package org.pbccrc.platform.api.saltstack;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.pbccrc.platform.util.Constant;
import org.pbccrc.platform.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

@Component
public class DefaultSaltstackApi implements SaltstackApi {
	
	private static final Logger logger = Logger.getLogger(DefaultSaltstackApi.class);
	
	private Client client;
	
	private String token;
	
	@Autowired
	private JerseyClientWithSSL jerseyClient;
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public void init() {
		if(this.client == null) {
			try {
				this.client = jerseyClient.initClient();
			} catch (KeyManagementException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}

	}
	
	@Override
	public boolean login(String user, String password) {
		MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
		formData.add("username", user);
		formData.add("password", password);
		formData.add("eauth", "pam");

		Response response = client.target(Constant.SALTSTACK_API_URL)
				.path("login")
				.request(MediaType.APPLICATION_JSON_TYPE)
				.header("Accept", MediaType.APPLICATION_JSON)
				.header("Content-Type", MediaType.APPLICATION_JSON)
				.post(Entity.form(formData));
		
		logger.debug(String.format("Saltstack api login, response: %s", response));

		if(response.getStatus() == 200) {
			JSONObject result = response.readEntity(JSONObject.class);
			if(result != null && !result.getJSONArray("return").isEmpty()) {
				this.token = result.getJSONArray("return").getJSONObject(0).getString("token");
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String auth(String user, String password) {
		if(login(user, password)) {
			return this.token;
		}
		
		return null;
	}

	@Override
	public JSONObject call(SaltRequest request) {
		JSONObject result = null;
		
		MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
		formData.add("client", request.getClient());
		formData.add("expr_form", request.getExprForm());
		formData.add("tgt", Utils.convertListToStringWithoutBracket(request.getTgt()));
		formData.add("fun", request.getFun());
		formData.add("arg", request.getArg());
		
		// for cmd.script
		if(request.getParam() != null) {
			formData.add("arg", request.getParam());
		}
		
		Response response = client.target(Constant.SALTSTACK_API_URL)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.header("Accept", MediaType.APPLICATION_JSON)
				.header("Content-Type", MediaType.APPLICATION_JSON)
				.header("X-Auth-Token", this.token)
				.post(Entity.form(formData));
		
		logger.debug(String.format("Saltstack api call, response: %s", response));
		
		if(response.getStatus() == 200) {
			result = response.readEntity(JSONObject.class);
			logger.debug(String.format("Saltstack response entity: %s", result));
		}
		
		return result;
	}
	
	@Override
	public JSONObject job(String jobId) {
		JSONObject result = null;
		
		Response response = client.target(Constant.SALTSTACK_API_URL)
				.path(String.format("/jobs/%s", jobId))
				.request(MediaType.APPLICATION_JSON_TYPE)
				.header("Accept", MediaType.APPLICATION_JSON)
				.header("Content-Type", MediaType.APPLICATION_JSON)
				.header("X-Auth-Token", this.token)
				.get();
		
		logger.debug(String.format("Saltstack api job, response: %s", response));
		
		if(response.getStatus() == 200) {
			result = response.readEntity(JSONObject.class);
			logger.debug(String.format("Saltstack response entity: %s", result));
		}
		
		return result;
	}
	
}
