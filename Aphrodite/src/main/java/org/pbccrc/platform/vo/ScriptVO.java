package org.pbccrc.platform.vo;

import java.sql.Timestamp;

/**
 * @author lixianyang
 *
 */
public class ScriptVO {
	
	private Integer id;
	private String name;
	private String path;
	private String description;
	private Integer status;
	private Timestamp createtime;
	private String content;
	private String type;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Timestamp getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Timestamp createtime) {
		this.createtime = createtime;
	}
	
	
	@Override
	public String toString() {
		return "ShellScriptVO [id=" + id + ", name=" + name + ", path=" + path
				+ ", description=" + description + ", status=" + status
				+ ", createtime=" + createtime + ", content= "+content+", type="+type+"]";
	}
}
