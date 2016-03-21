package org.pbccrc.platform.util;

import java.util.HashMap;
import java.util.Map;

public class Constant {

	public static final String SESSION_USER_KEY = "CURRENT_USER";

	public static final String ZABBIX_API_URL = "http://192.168.24.129/zabbix/api_jsonrpc.php";
//	public static final String ZABBIX_API_URL = "http://10.128.142.243/zabbix/api_jsonrpc.php";
	// public static final String ZABBIX_API_URL = "http://10.128.147.42/zabbix/api_jsonrpc.php";
	public static final String ZABBIX_USERNAME = "Admin";
	public static final String ZABBIX_PASSWORD = "zabbix";

	public static final String SALTSTACK_API_URL = "https://10.128.142.239:8000/";
	public static final String SALTSTACK_USERNAME = "admin";
	public static final String SALTSTACK_PASSWORD = "123456";
	public static final String SHELLFILE_ENCODE = "utf-8";
	public static final String SALTSTACK_MASTER = "salt-master";

	public static final String SALT_FTP_HOST = "10.128.142.239";
	public static final String SALT_FTP_USERNAME = "ftp-salt";
	public static final String SALT_FTP_PASSWD = "ftp-salt";
	public static final String SHELLTEMPLATE_PREFFIX= "{{";
	public static final String SHELLTEMPLATE_SUFFIX = "}}";
	
	public static final String COBBLER_API_URL = "http://10.128.142.132/cobbler_api";
	public static final String COBBLER_USERNAME = "cobbler";
	public static final String COBBLER_PASSWORD = "cobbler";
	public static final String SALTSTACK_COBBLER = "cobbler_node1";
	public static final String COBBLER_OS_USER = "root";
	public static final String COBBLER_OS_PASSWORD = "root";
	public static Map<String, Integer> ELASTICSEARCH_ADDRESS = new HashMap<String, Integer>() {
		
		private static final long serialVersionUID = 1L;

		{
			put("10.128.142.90", 9300);
		}
	};

	public static enum GRAPH_TYPE {
		line("line"), pie("pie"), area("area"), bar("bar");

		private String value;

		public String getValue() {
			return this.value;
		}

		private GRAPH_TYPE(String value) {
			this.value = value;
		}
	}

	public static enum VALUE_TYPE {

		float_type(0), string_type(1), log_type(2), integer_type(3), text_type(4);

		private Integer value;

		public Integer getValue() {
			return this.value;
		}

		private VALUE_TYPE(Integer value) {
			this.value = value;
		}
	}

	public static enum APP_TYPE {
		mysql("mysql", "mysql.ping", "mysql.version"), 
		oracle("oracle", "alive", "dbversion"), 
		tomcat("tomcat", "", "jmx[\"Catalina:type=Server\",serverInfo]"), 
		weblogic("weblogic", "", "jmx[\"java.lang:type=Runtime\",VmVersion]");

		private String name;
		private String status;
		private String version;

		public String getName() {
			return this.name;
		}

		public String getStatus() {
			return status;
		}

		public String getVersion() {
			return version;
		}

		private APP_TYPE(String name, String status, String version) {
			this.name = name;
			this.status = status;
			this.version = version;
		}
	}

	public static enum OS_TYPE {

		Linux("linux"), Aix("aix"), Windows("windows");

		private String name;

		public String getName() {
			return name;
		}

		private OS_TYPE(String name) {
			this.name = name;
		}

	}

	public static enum SALT_CLIENT {

		sync("local"), async("local_async");

		private String name;

		public String getName() {
			return name;
		}

		private SALT_CLIENT(String name) {
			this.name = name;
		}

	}

	public static enum SALT_FUN {

		state_high("state.highstate"), state_sls("state.sls"), cmd("cmd.run"), script("cmd.script");

		private String name;

		public String getName() {
			return name;
		}

		private SALT_FUN(String name) {
			this.name = name;
		}

	}

	public static enum JOB_STATUS {

		wait, success, fail, mix;

	}

	public static enum SCRIPT_TYPE {

		python_py(".py"),shell_sh(".sh"), state_sls(".sls");

		private String name;

		public String getName() {
			return name;
		}

		private SCRIPT_TYPE(String name) {
			this.name = name;
		}

	}
	
	public static enum SCRIPT_FILEPATH {

		ftp_localpath("/upload/scripts/"),ftp_path("/srv/salt/scripts/"), upload_localpath("/upload_local/scripts/"),download_localptah("/download_local/scripts/");

		private String name;

		public String getName() {
			return name;
		}

		private SCRIPT_FILEPATH(String name) {
			this.name = name;
		}

	}
}
