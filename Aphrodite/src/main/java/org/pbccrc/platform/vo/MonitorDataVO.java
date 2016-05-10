package org.pbccrc.platform.vo;

public class MonitorDataVO {

	private String id;
	private String taskDataId;
	private String hostId;
	private String monitorType; //1-cpu; 2-disk; 3-net; 4-memory
	private String itemName;
	private String path;
	
	public MonitorDataVO(){}
	
	public MonitorDataVO(String taskDataId, String hostId, String monitorType, String path) {
		super();
		this.taskDataId = taskDataId;
		this.hostId = hostId;
		this.monitorType = monitorType;
		this.path = path;
	}
	
	public MonitorDataVO(String taskDataId, String hostId, String monitorType, String itemName,
			String path) {
		super();
		this.taskDataId = taskDataId;
		this.hostId = hostId;
		this.monitorType = monitorType;
		this.itemName = itemName;
		this.path = path;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTaskDataId() {
		return taskDataId;
	}
	public void setTaskDataid(String taskDataId) {
		this.taskDataId = taskDataId;
	}
	public String getHostId() {
		return hostId;
	}
	public void setHostId(String hostId) {
		this.hostId = hostId;
	}
	public String getMonitorType() {
		return monitorType;
	}
	public void setMonitorType(String monitorType) {
		this.monitorType = monitorType;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
	
	
	
}
