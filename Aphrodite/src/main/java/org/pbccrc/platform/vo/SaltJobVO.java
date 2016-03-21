package org.pbccrc.platform.vo;

import java.sql.Timestamp;

public class SaltJobVO {
	
	private Integer id;
	private String jobId;
	private String title;
	private String hostName;
	private String fun;
	private String returnValue;
	private String arg;
	private String param;
	private String status;
	private Timestamp deployTime;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getFun() {
		return fun;
	}
	public void setFun(String fun) {
		this.fun = fun;
	}
	public String getReturnValue() {
		return returnValue;
	}
	public void setReturnValue(String returnValue) {
		this.returnValue = returnValue;
	}
	public String getArg() {
		return arg;
	}
	public void setArg(String arg) {
		this.arg = arg;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Timestamp getDeployTime() {
		return deployTime;
	}
	public void setDeployTime(Timestamp deployTime) {
		this.deployTime = deployTime;
	}
	
	@Override
	public String toString() {
		return "SaltJobVO [id=" + id + ", jobId=" + jobId + ", title=" + title
				+ ", hostName=" + hostName + ", fun=" + fun + ", returnValue="
				+ returnValue + ", arg=" + arg + ", param=" + param
				+ ", status=" + status + ", deployTime=" + deployTime + "]";
	}
	
}
