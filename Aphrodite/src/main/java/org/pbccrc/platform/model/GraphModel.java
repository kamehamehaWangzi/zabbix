package org.pbccrc.platform.model;

import java.util.List;

public class GraphModel {
	
	private List<String> legend;
	private List<String> xAxis;
	private List<Series> yAxis;
	
	public List<String> getLegend() {
		return legend;
	}
	public void setLegend(List<String> legend) {
		this.legend = legend;
	}
	public List<String> getxAxis() {
		return xAxis;
	}
	public void setxAxis(List<String> xAxis) {
		this.xAxis = xAxis;
	}
	public List<Series> getyAxis() {
		return yAxis;
	}
	public void setyAxis(List<Series> yAxis) {
		this.yAxis = yAxis;
	}
	
	@Override
	public String toString() {
		return "GraphModel [legend=" + legend + ", xAxis=" + xAxis + ", yAxis="
				+ yAxis + "]";
	}
	
}
