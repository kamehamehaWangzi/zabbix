package org.pbccrc.platform.vo;

public class AppVO {
	
	private Integer id;
	private String name;
	private String type;
	private String version;
	private String description;
	private String status;
	private Integer deployCount;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getDeployCount() {
		return deployCount;
	}
	public void setDeployCount(Integer deployCount) {
		this.deployCount = deployCount;
	}
	
	@Override
	public String toString() {
		return "AppVO [id=" + id + ", name=" + name + ", type=" + type
				+ ", version=" + version + ", description=" + description
				+ ", status=" + status + ", deployCount=" + deployCount + "]";
	}
	
	
}
