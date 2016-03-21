package org.pbccrc.platform.vo;

public class TemplateVO { 
	private String host;
	private String name;
	private String templateid;
	private String description;
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTemplateid() {
		return templateid;
	}
	public void setTemplateid(String templateid) {
		this.templateid = templateid;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String toString() {
		return "TemplateVO [host=" + host + ", name=" + name + ", templateid="
				+ templateid + ", description=" + description + "]";
	}
	
	
}
