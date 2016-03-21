package org.pbccrc.platform.vo;

import com.alibaba.fastjson.JSONArray;

public class NetworkVO {
	private Integer id;
	/**zabbix hostid*/
	private Integer zabbixHostid;
	/**设备名*/
	private String name;
	/**设备类型, physical/virtual*/
	private String type;
	/**操作系统类型, linux/window/aix*/
	private String osType;
	/**操作系统版本*/
	private String osVersion;
	/**ip1*/
	private String ip;
	/**供应商*/
	private String vendor;
	/**位置 */
	private String location;
	/**设备型号*/
	private String model;	
	/**内存型号*/
	private String memory;
	/**序列号*/
	private String sn;
	/**描述*/
	private String description;
	/**是否监控本机*/
	private Integer isMonitor;
	/**主机状态*/
	private Integer status;
	/**监控模板*/
	private JSONArray templates;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
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
	
	public JSONArray getTemplates() {
		return templates;
	}
	public void setTemplates(JSONArray templates) {
		this.templates = templates;
	}
	@Override
	public String toString() {
		return "NetworkVO [id=" + id + ", zabbixHostid=" + zabbixHostid
				+ ", name=" + name + ", type=" + type + ", osType=" + osType
				+ ", osVersion=" + osVersion + ", ip=" + ip + ", vendor="
				+ vendor + ", model=" + model + ", memory=" + memory + ", sn="
				+ sn + ", description=" + description + ", isMonitor="
				+ isMonitor + ", status=" + status + "]";
	}

}
