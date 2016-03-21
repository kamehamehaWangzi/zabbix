package org.pbccrc.platform.vo;

public class TaskVO { 
	
	private Integer id;
	private String name;
	private String project;
	private String hosts;
	
	private String description;
	
	private String projectName;
	
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
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
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
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}


	@Override
	public String toString() {
		return "ProjectVO [id=" + id + ", name=" + name + ", project="
				+ project + "hosts=" + hosts + ", description=" + description + "]";
	}
	
	
}
