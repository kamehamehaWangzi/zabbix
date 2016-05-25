package org.pbccrc.platform.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

@Service
public class ZabbixOperator {
	
	private final String DISK_NAME = "{#DISK_NAME}";
	
	private final String LINUX_CMD = "cd / && ./etc/zabbix/monitor_scripts/disk_scan.sh";
	
	@Autowired
	private ZabbixDataUtil zabbixDataUtil;
	
	/**
	 * connect to server by ssh
	 * execute command read disk info
	 * */
	public List<String> getDiskInfoBySSH(String host, String user, String password) {
		
		List<String> diskList = new ArrayList<String>();

		try {
			Connection conn = new Connection(host);

			conn.connect();

			boolean isAuthenticated = conn.authenticateWithPassword(user, password);

			if (isAuthenticated == false) {
				throw new IOException("Authentication failed.");
			}

			Session session = conn.openSession();

			session.execCommand(LINUX_CMD);
			
			InputStream stdout = new StreamGobbler(session.getStdout());

			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

			StringBuilder sb = new StringBuilder();

			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				sb.append(line);
			}

			session.close();
			conn.close();
			br.close();
			
			JSONObject jsonObject = JSONObject.parseObject(sb.toString());
			JSONArray data = jsonObject.getJSONArray("data");
			
			for (int i = 0;i < data.size(); i++) {
				diskList.add(String.valueOf(data.getJSONObject(i).get(DISK_NAME)));
			}	
			
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(2);
		}
		
		return diskList;
	}
	

	/** get disk info from zabbix item */
	public List<String> getDiskInfoByItem(Integer zabbixHostId) {
		
		JSONArray keyItems = zabbixDataUtil.getKeyItems(zabbixHostId);
		
		List<String> diskList = new ArrayList<String>();
		
		String searchkey = "io.util";
		
		for(int i = 0; i < keyItems.size(); i++) {
			
			JSONObject keyItem = keyItems.getJSONObject(i);
			
			String key = keyItem.getString("key_");
			
			if(key.contains(searchkey)) {
				String disk = key.substring(key.indexOf("[") + 1, key.length() - 1);
				diskList.add(disk);
			}
		}
		
		return diskList;
	}
	
	/** get net info from zabbix item */
	public List<String> getNetInfoByItem(Integer zabbixHostId) {
		
		JSONArray keyItems = zabbixDataUtil.getKeyItems(zabbixHostId);
		
		List<String> netList = new ArrayList<String>();
		
		String searchkey = "net.if.out";
		
		for(int i = 0; i < keyItems.size(); i++) {
			
			JSONObject keyItem = keyItems.getJSONObject(i);
			
			String key = keyItem.getString("key_");
			
			if(key.contains(searchkey)) {
				String net = key.substring(key.indexOf("[") + 1, key.length() - 1);
				netList.add(net);
			}
		}
		
		return netList;
	}

}
