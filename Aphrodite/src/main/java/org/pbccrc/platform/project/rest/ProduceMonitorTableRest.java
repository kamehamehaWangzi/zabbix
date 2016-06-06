package org.pbccrc.platform.project.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;
import org.pbccrc.platform.project.biz.IMonitorDataBiz;
import org.pbccrc.platform.project.biz.ITaskBiz;
import org.pbccrc.platform.project.biz.ITaskDataBiz;
import org.pbccrc.platform.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import com.alibaba.fastjson.JSONObject;

@Path("/produceTable")
@Produces(MediaType.APPLICATION_JSON)
public class ProduceMonitorTableRest {

	private static final Logger log = Logger.getLogger(ProduceMonitorTableRest.class);
	
	@Autowired
	@Qualifier("taskBizImpl") 
	private ITaskBiz taskBiz;
	
	@Autowired
	@Qualifier("taskDataBizImpl")
	private ITaskDataBiz taskDataBiz;
	
	@Autowired
	@Qualifier("monitorDataBizImpl")
	private IMonitorDataBiz monitorDataBiz;// = new MonitorDataBizImpl()
	
	@Path("/getHostListReport")
	@GET
	public Response getHostListReport(@QueryParam("task_id")String id){
		log.debug(String.format(" Get the monitor data id %s", id));
		//返回文件下载地址
		String filePath = monitorDataBiz.loadHostMonitorData(id);
		String bathPath = Constant.ZABBIX_MONITOR_DATA_DOWNLOAD_PATH;
		JSONObject result = new JSONObject();
		result.put("filePath", "/Aphrodite"+bathPath+filePath);
		return Response.ok(result).build();
	}
	
	@Path("/getItemListReport")
	@GET
	public Response getItemListReport(@QueryParam("task_id")String id){
		log.debug(String.format(" Get the Item monitror data by taskDataId %s", id));	
		String filePath = monitorDataBiz.loadItemMonitorData(id);
		String bathPath = Constant.ZABBIX_MONITOR_DATA_DOWNLOAD_PATH;
		
		JSONObject result = new JSONObject();
		result.put("filePath", "/Aphrodite"+bathPath+filePath);
		return Response.ok(result).build();
	}
	
	@Path("/export2Excel")
	@GET
	public Response export2Excel(@QueryParam("task_id")String id){
		String bathPath = Constant.ZABBIX_MONITOR_DATA_EXPORT_PATH;
		String filePath = monitorDataBiz.export2Excel(id);
		JSONObject result = new JSONObject();
		result.put("filePath", "/Aphrodite" + bathPath + filePath);
		return Response.ok(result).build();
	}
}
