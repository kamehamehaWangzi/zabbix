package org.pbccrc.platform.vo;

public class ProjectVO { 
	
	private Integer id;
	private String name;
	private String hosts;
	private String description;
	
	
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
	public String getHosts() {
		return hosts;
	}
	public void setHosts(String hosts) {
		this.hosts = hosts;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}


	@Override
	public String toString() {
		return "ProjectVO [id=" + id + ", name=" + name + ", hosts="
				+ hosts + ", description=" + description + "]";
	}
	
	
}
