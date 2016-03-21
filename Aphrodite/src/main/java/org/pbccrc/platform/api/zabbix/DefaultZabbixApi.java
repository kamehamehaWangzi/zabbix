package org.pbccrc.platform.api.zabbix;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.pbccrc.platform.util.Constant;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Component
public class DefaultZabbixApi implements ZabbixApi {
	
	private static final Logger logger = Logger.getLogger(DefaultZabbixApi.class);

	CloseableHttpClient httpClient;

	URI uri;

	String auth;
	
	public void setAuth(String auth) {
		this.auth = auth;
	}

	public DefaultZabbixApi() {
		super();
		
		try {
			uri = new URI(Constant.ZABBIX_API_URL.trim());
		} catch (URISyntaxException e) {
			throw new RuntimeException("url invalid", e);
		}
		
	}

	public DefaultZabbixApi(String url) {
		try {
			uri = new URI(url.trim());
		} catch (URISyntaxException e) {
			throw new RuntimeException("url invalid", e);
		}
	}

	public DefaultZabbixApi(URI uri) {
		this.uri = uri;
	}

	public DefaultZabbixApi(String url, CloseableHttpClient httpClient) {
		this(url);
		this.httpClient = httpClient;
	}

	public DefaultZabbixApi(URI uri, CloseableHttpClient httpClient) {
		this(uri);
		this.httpClient = httpClient;
	}

	@Override
	public void init() {
		if (httpClient == null) {
			httpClient = HttpClients.custom().build();
		}
	}

	@Override
	public void destory() {
		if (httpClient != null) {
			try {
				httpClient.close();
			} catch (Exception e) {
				logger.error("close httpclient error!", e);
			}
		}
	}

	@Override
	public boolean login(String user, String password) {
		Request request = RequestBuilder.newBuilder().paramEntry("user", user)
				.paramEntry("password", password).method("user.login").build();
		JSONObject response = call(request);
		String auth = response.getString("result");
		if (auth != null && !auth.isEmpty()) {
			this.auth = auth;
			return true;
		}
		return false;
	}
	
	@Override
	public String auth(String user, String password) {
		Request request = RequestBuilder.newBuilder().paramEntry("user", user)
				.paramEntry("password", password).method("user.login").build();
		JSONObject response = call(request);
		String auth = response.getString("result");
		if (auth != null && !auth.isEmpty()) {
			this.auth = auth;
			return auth;
		}
		return null;
	}

	@Override
	public String apiVersion() {
		Request request = RequestBuilder.newBuilder().method("apiinfo.version")
				.build();
		JSONObject response = call(request);
		return response.getString("result");
	}

	public boolean hostExists(String name) {
		Request request = RequestBuilder.newBuilder().method("host.exists")
				.paramEntry("name", name).build();
		JSONObject response = call(request);
		return response.getBooleanValue("result");
	}

	public String hostCreate(String host, String groupId) {
		JSONArray groups = new JSONArray();
		JSONObject group = new JSONObject();
		group.put("groupid", groupId);
		groups.add(group);
		Request request = RequestBuilder.newBuilder().method("host.create")
				.paramEntry("host", host).paramEntry("groups", groups).build();
		JSONObject response = call(request);
		return response.getJSONObject("result").getJSONArray("hostids")
				.getString(0);
	}

	public boolean hostgroupExists(String name) {
		Request request = RequestBuilder.newBuilder()
				.method("hostgroup.exists").paramEntry("name", name).build();
		JSONObject response = call(request);
		return response.getBooleanValue("result");
	}

	/**
	 * 
	 * @param name
	 * @return groupId
	 */
	public String hostgroupCreate(String name) {
		Request request = RequestBuilder.newBuilder()
				.method("hostgroup.create").paramEntry("name", name).build();
		JSONObject response = call(request);
		return response.getJSONObject("result").getJSONArray("groupids")
				.getString(0);
	}

	@Override
	public JSONObject call(Request request) {
		if (request.getAuth() == null && !"user.login".equals(request.getMethod())) {
			request.setAuth(auth);
		}

		try {
			HttpUriRequest httpRequest = org.apache.http.client.methods.RequestBuilder
					.post().setUri(uri)
					.addHeader("Content-Type", "application/json")
					.setEntity(new StringEntity(JSON.toJSONString(request)))
					.build();
			CloseableHttpResponse response = httpClient.execute(httpRequest);
			HttpEntity entity = response.getEntity();
			byte[] data = EntityUtils.toByteArray(entity);
			return (JSONObject) JSON.parse(data);
		} catch (IOException e) {
			throw new RuntimeException("DefaultZabbixApi call exception!", e);
		}
	}
	
	@Override
	public JSONObject customCall(Request request, JSONObject params) {
		if (request.getAuth() == null && !"user.login".equals(request.getMethod())) {
			request.setAuth(auth);
		}
		
		String jsonStr = JSON.toJSONString(request);
		if(params != null && !params.isEmpty() && params.containsKey("params")) {
			JSONObject req = (JSONObject) JSONObject.toJSON(request);
			req.put("params", params.get("params"));
			
			jsonStr = JSON.toJSONString(req);
		}
		
		try {
			HttpUriRequest httpRequest = org.apache.http.client.methods.RequestBuilder
					.post().setUri(uri)
					.addHeader("Content-Type", "application/json")
					.setEntity(new StringEntity(jsonStr))
					.build();
			CloseableHttpResponse response = httpClient.execute(httpRequest);
			HttpEntity entity = response.getEntity();
			byte[] data = EntityUtils.toByteArray(entity);
			return (JSONObject) JSON.parse(data);
		} catch (IOException e) {
			throw new RuntimeException("DefaultZabbixApi call exception!", e);
		}
	}

}
