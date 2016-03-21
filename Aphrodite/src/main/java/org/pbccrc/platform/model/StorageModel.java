package org.pbccrc.platform.model;



public class StorageModel {
	/* 磁盘域ID*/
	private String d_domainId;
	/* 磁盘域名称*/
	private String d_domainName;
	/* 磁盘域健康状态*/
	private String d_domainHealthStat;
	/* 控制器cpu信息*/
	private String c_cpuInfo;
	/* 控制器角色*/
	private String c_role;	
	
	public String getD_domainId() {
		return d_domainId;
	}

	public void setD_domainId(String d_domainId) {
		this.d_domainId = d_domainId;
	}

	public String getD_domainName() {
		return d_domainName;
	}

	public void setD_domainName(String d_domainName) {
		this.d_domainName = d_domainName;
	}

	public String getD_domainHealthStat() {
		return d_domainHealthStat;
	}

	public void setD_domainHealthStat(String d_domainHealthStat) {
		this.d_domainHealthStat = d_domainHealthStat;
	}

	public String getC_cpuInfo() {
		return c_cpuInfo;
	}

	public void setC_cpuInfo(String c_cpuInfo) {
		this.c_cpuInfo = c_cpuInfo;
	}

	public String getC_role() {
		return c_role;
	}

	public void setC_role(String c_role) {
		this.c_role = c_role;
	}
	
	
	
}
