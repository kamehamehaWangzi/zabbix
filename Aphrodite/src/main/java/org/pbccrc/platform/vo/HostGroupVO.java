package org.pbccrc.platform.vo;

public class HostGroupVO { 
	/*主机群组ID*/
	private String groupid;
	/*主机群组名称*/
	private String name;
	/*是否为系统内置*/
	private String internal;
	
	public String getGroupid() {
		return groupid;
	}
	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getInternal() {
		return internal;
	}
	public void setInternal(String internal) {
		this.internal = internal;
	}
	@Override
	public String toString() {
		return "HostGroupVO [groupid=" + groupid + ", name=" + name
				+ ", internal=" + internal + "]";
	}
	
	
}
