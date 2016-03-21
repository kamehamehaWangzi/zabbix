package org.pbccrc.platform.vo;

public class HostAppVO {
	
	private Integer id;
	private Integer hostId;
	private Integer appId;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getHostId() {
		return hostId;
	}
	public void setHostId(Integer hostId) {
		this.hostId = hostId;
	}
	public Integer getAppId() {
		return appId;
	}
	public void setAppId(Integer appId) {
		this.appId = appId;
	}
	
	@Override
	public String toString() {
		return "HostAppVO [id=" + id + ", hostId=" + hostId + ", appId="
				+ appId + "]";
	}
	
}
