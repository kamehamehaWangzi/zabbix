package org.pbccrc.platform.vo;

import com.alibaba.fastjson.JSONArray;

public class HostVO {
	
	private Integer id;
	/** group */
	private String groups;
	/** zabbix-groupid(用于调用zabbix api) */
	private JSONArray zabbix_groupids;
	/**zabbix hostid*/
	private Integer zabbixHostid;
	/**host name*/
	private String name;
	/**主机类型, physical/virtual*/
	private String type;
	/**操作系统类型, linux/window/aix*/
	private String osType;
	/**操作系统版本*/
	private String osVersion;
	/**ip1*/
	private String ip1;
	/**ip2*/
	private String ip2;
	/**ip3*/
	private String ip3;
	/**ip4*/
	private String ip4;
	/**供应商*/
	private String vendor;
	/**机器型号*/
	private String model;
	/**cpu型号*/
	private String cpu;
	/**磁盘型号*/
	private String disk;
	/**内存型号*/
	private String memory;
	/**条码*/
	private String sn;
	/**描述*/
	private String description;
	/**是否监控本机*/
	private Integer isMonitor;
	/**主机状态*/
	private Integer status;
	/** 监控模板 */
	private String teamplates;
	/** zabbix-template(用于调用zabbix api) */
	private JSONArray zabbix_templates;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getGroups() {
		return groups;
	}
	public void setGroups(String groups) {
		this.groups = groups;
	}
	public JSONArray getZabbix_groupids() {
		return zabbix_groupids;
	}
	public void setZabbix_groupids(JSONArray zabbix_groupids) {
		this.zabbix_groupids = zabbix_groupids;
	}
	public Integer getZabbixHostid() {
		return zabbixHostid;
	}
	public void setZabbixHostid(Integer zabbixHostid) {
		this.zabbixHostid = zabbixHostid;
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
	public String getOsType() {
		return osType;
	}
	public void setOsType(String osType) {
		this.osType = osType;
	}
	public String getOsVersion() {
		return osVersion;
	}
	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}
	public String getIp1() {
		return ip1;
	}
	public void setIp1(String ip1) {
		this.ip1 = ip1;
	}
	public String getIp2() {
		return ip2;
	}
	public void setIp2(String ip2) {
		this.ip2 = ip2;
	}
	public String getIp3() {
		return ip3;
	}
	public void setIp3(String ip3) {
		this.ip3 = ip3;
	}
	public String getIp4() {
		return ip4;
	}
	public void setIp4(String ip4) {
		this.ip4 = ip4;
	}
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getCpu() {
		return cpu;
	}
	public void setCpu(String cpu) {
		this.cpu = cpu;
	}
	public String getDisk() {
		return disk;
	}
	public void setDisk(String disk) {
		this.disk = disk;
	}
	public String getMemory() {
		return memory;
	}
	public void setMemory(String memory) {
		this.memory = memory;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getIsMonitor() {
		return isMonitor;
	}
	public void setIsMonitor(Integer isMonitor) {
		this.isMonitor = isMonitor;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getTeamplates() {
		return teamplates;
	}
	public void setTeamplates(String teamplates) {
		this.teamplates = teamplates;
	}
	public JSONArray getZabbix_templates() {
		return zabbix_templates;
	}
	public void setZabbix_templates(JSONArray zabbix_templates) {
		this.zabbix_templates = zabbix_templates;
	}
	
	@Override
	public String toString() {
		return "HostVO [id=" + id + ", groupids=" + zabbix_groupids + ", zabbixHostid=" + zabbixHostid
				+ ", name=" + name + ", type=" + type + ", osType=" + osType
				+ ", osVersion=" + osVersion + ", ip1=" + ip1 + ", ip2=" + ip2
				+ ", ip3=" + ip3 + ", ip4=" + ip4 + ", vendor=" + vendor
				+ ", model=" + model + ", cpu=" + cpu + ", disk=" + disk
				+ ", memory=" + memory + ", sn=" + sn + ", description="
				+ description + ", isMonitor=" + isMonitor + ", status="
				+ status + ", templates=" + zabbix_templates + "]";
	}
	
}
