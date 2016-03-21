package org.pbccrc.platform.vo;

public class TriggerVO {
	
	private String triggerId;
	private String hostId;
	private String hostName;
	private String description;
	private String lastChange;
	private String priority;
	private String value; 
	private String status;	
	private String functionid;
	private String itemid;
	private String function;
	private String parameter;
	private String triggerid;
	private String comments;
	private String url;
	private String error;
	private String type;
	private String state;
	private String flags;
	private String templateid;
	
	public String getFunctionid() {
		return functionid;
	}
	public void setFunctionid(String functionid) {
		this.functionid = functionid;
	}
	public String getItemid() {
		return itemid;
	}
	public void setItemid(String itemid) {
		this.itemid = itemid;
	}
	public String getFunction() {
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}
	public String getParameter() {
		return parameter;
	}
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	public String getTriggerid() {
		return triggerid;
	}
	public void setTriggerid(String triggerid) {
		this.triggerid = triggerid;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getFlags() {
		return flags;
	}
	public void setFlags(String flags) {
		this.flags = flags;
	}
	public String getTemplateid() {
		return templateid;
	}
	public void setTemplateid(String templateid) {
		this.templateid = templateid;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTriggerId() {
		return triggerId;
	}
	public void setTriggerId(String triggerId) {
		this.triggerId = triggerId;
	}
	public String getHostId() {
		return hostId;
	}
	public void setHostId(String hostId) {
		this.hostId = hostId;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLastChange() {
		return lastChange;
	}
	public void setLastChange(String lastChange) {
		this.lastChange = lastChange;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	
	@Override
	public String toString() {
		return "TriggerVO [triggerId=" + triggerId + ", hostId=" + hostId
				+ ", hostName=" + hostName + ", description=" + description
				+ ", lastChange=" + lastChange + ", priority=" + priority + "]";
	}

}
