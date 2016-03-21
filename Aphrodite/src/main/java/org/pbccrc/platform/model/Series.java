package org.pbccrc.platform.model;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

public class Series {
	
	private String name;
	private String type;
	private Boolean smooth = true;
	private JSONObject itemStyle;
	private List<String> data;
	
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
	public Boolean getSmooth() {
		return smooth;
	}
	public void setSmooth(Boolean smooth) {
		this.smooth = smooth;
	}
	public JSONObject getItemStyle() {
		return itemStyle;
	}
	public void setItemStyle(JSONObject itemStyle) {
		this.itemStyle = itemStyle;
	}
	public List<String> getData() {
		return data;
	}
	public void setData(List<String> data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		return "Series [name=" + name + ", type=" + type + ", smooth=" + smooth
				+ ", itemStyle=" + itemStyle + ", data=" + data + "]";
	}
	
}
