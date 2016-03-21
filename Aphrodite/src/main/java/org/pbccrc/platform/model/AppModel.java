package org.pbccrc.platform.model;

public class AppModel {
	
	private String hostid;
	private String name;
	private String type;
	private String version;
	private String status;
	
	public String getHostid() {
		return hostid;
	}
	public void setHostid(String hostid) {
		this.hostid = hostid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getStatus() {
		if(status == null) {
			return String.valueOf(0);
		}
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return "AppModel [hostid=" + hostid + ", name=" + name + ", type=" + type
				+ ", version=" + version + ", status=" + status + "]";
	}

}
