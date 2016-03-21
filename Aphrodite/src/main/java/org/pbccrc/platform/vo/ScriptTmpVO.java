package org.pbccrc.platform.vo;

import java.sql.Timestamp;
import java.util.List;

public class ScriptTmpVO {

	private Integer id;
	private String name;
	private String content;
	private String description;
	private String os;
	private Timestamp createtime;
	private Integer status;
	private String type;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	private List<String> variable;
	
	public List<String> getVariable() {
		return variable;
	}
	public void setVariable(List<String> variable) {
		this.variable = variable;
	}
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public Timestamp getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Timestamp createtime) {
		this.createtime = createtime;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "ScriptTemplateVO [id=" + id + ", name=" + name + ", content="
				+ content + ", description=" + description + ", os=" + os
				+ ", createtime=" + createtime + ", status=" + status + ", type="+type+"]";
	}
	
	
}
