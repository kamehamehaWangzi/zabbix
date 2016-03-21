package org.pbccrc.platform.vo;

import java.util.List;

import org.pbccrc.platform.util.Constant;

public class CobblerVO {
	
	private String uid;
	private String profile;
	private String name;
	private String status;
	private String hostname;
	private String gateway;
	private String interfaces;
	private String mac_address;
	private String ip_address;
	private String dns_name;
	private String power_type;
	private String power_user;
	private String power_pass;
	private String power_address;
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getGateway() {
		return gateway;
	}
	public void setGateway(String gateway) {
		this.gateway = gateway;
	}
	public String getInterfaces() {
		return interfaces;
	}
	public void setInterfaces(String interfaces) {
		this.interfaces = interfaces;
	}
	public String getMac_address() {
		return mac_address;
	}
	public void setMac_address(String mac_address) {
		this.mac_address = mac_address;
	}
	public String getIp_address() {
		return ip_address;
	}
	public void setIp_address(String ip_address) {
		this.ip_address = ip_address;
	}
	public String getDns_name() {
		return dns_name;
	}
	public void setDns_name(String dns_name) {
		this.dns_name = dns_name;
	}
	public String getPower_type() {
		return power_type;
	}
	public void setPower_type(String power_type) {
		this.power_type = power_type;
	}
	public String getPower_user() {
		return power_user;
	}
	public void setPower_user(String power_user) {
		this.power_user = power_user;
	}
	public String getPower_pass() {
		return power_pass;
	}
	public void setPower_pass(String power_pass) {
		this.power_pass = power_pass;
	}
	public String getPower_address() {
		return power_address;
	}
	public void setPower_address(String power_address) {
		this.power_address = power_address;
	}

	
	@Override
	public String toString() {
		return "CobblerRequest [uid=" + uid + ", profile=" + profile
				+ ", name=" + name + ", status=" + status + ", hostname="
				+ hostname + ", gateway=" + gateway + ", interfaces="
				+ interfaces + ", macaddress=" + mac_address + ", ipaddress="
				+ ip_address + ", dnsname=" + dns_name + ", power_type="
				+ power_type + ", power_user=" + power_user + ", power_pass="
				+ power_pass + ", power_address=" + power_address + "]";
	}
}
