package org.pbccrc.platform.model;

import java.io.Serializable;
import java.util.List;

public class ZabbixDataModel implements Serializable{
	
	/** serialVersionUID */
	private static final long serialVersionUID = -8618181845092513172L;

	private int taskDataId;

	private int hostId;
	
	private GraphModel graph; //按监控item类型进行的存储
	
	@Deprecated  //按照单个item进行存储，暂时废弃
	private List<GraphModel> graphList;
	
	public int getTaskDataId() {
		return taskDataId;
	}

	public void setTaskDataId(int taskDataId) {
		this.taskDataId = taskDataId;
	}

	public int getHostId() {
		return hostId;
	}

	public void setHostId(int hostId) {
		this.hostId = hostId;
	}

	public List<GraphModel> getGraphList() {
		return graphList;
	}

	public void setGraphList(List<GraphModel> graphList) {
		this.graphList = graphList;
	}

	public GraphModel getGraph() {
		return graph;
	}

	public void setGraph(GraphModel graph) {
		this.graph = graph;
	}
	
	
}
