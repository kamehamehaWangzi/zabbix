package org.pbccrc.platform.project.biz;

public interface IMonitorDataBiz {

	/**
	 * 根据监控任务id,收集各主机的监控数据信息，生成响应的图表集合文件，返回文件地址
	 * @param taskDataId
	 * @return
	 */
	String loadHostMonitorData(String taskDataId);

	/**
	 * 根据监控任务id,收集各监控项的监控数据信息，生成响应的图表集合文件，返回文件地址
	 * @param taskDataId
	 * @return
	 */
	String loadItemMonitorData(String taskDataId);
	
}
