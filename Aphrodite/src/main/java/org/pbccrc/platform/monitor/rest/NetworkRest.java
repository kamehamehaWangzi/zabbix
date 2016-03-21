package org.pbccrc.platform.monitor.rest;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.model.PortModel;
import org.pbccrc.platform.model.StorageModel;
import org.pbccrc.platform.monitor.biz.INetworkBiz;
import org.pbccrc.platform.vo.NetworkVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;

@Path("/network")
@Produces(MediaType.APPLICATION_JSON)
public class NetworkRest { 
	private static final  Logger log =Logger.getLogger(NetworkRest.class);
	@Autowired
	@Qualifier("networkBizImpl")
	private INetworkBiz networkBiz;	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listNetwork(@QueryParam("param") String param, @QueryParam("currentPage") int currentPage, @QueryParam("pageSize") int pageSize) {
		NetworkVO vo =new NetworkVO();
		vo.setIsMonitor(Integer.valueOf(1));
		if(param != null && param.trim().length() > 0) {
			vo.setName(param.trim());
			vo.setIp(param.trim());
		}
		
		Pagination pagination = new Pagination();
		pagination.setCurrentPage(currentPage);
		pagination.setPageSize(pageSize);
		
		List<NetworkVO> hosts = networkBiz.queryNetworks(vo, pagination);
		
		pagination.setTotalCount(((Page<NetworkVO>) hosts).getTotal());
		
		if(hosts != null) {
			for(NetworkVO host: hosts) {
				if(host.getZabbixHostid() != null) {
					JSONObject obj = networkBiz.getHostByIdFromZabbix(host.getZabbixHostid());
					if(obj != null && !obj.getJSONArray("result").isEmpty()) {
						
						log.debug(String.format("listNetwork, %s", obj));
						
						host.setStatus(obj.getJSONArray("result").getJSONObject(0).getInteger("status"));
						
					}
				}
			}
		}
		
		pagination.setResult(hosts);
		
		log.debug(String.format("listNetwork, %s", hosts));
		
		return Response.ok(pagination).build();
	}
	
	@POST
	public Response addHost(@QueryParam("network") String network) {
		JSONObject networkInfo = JSON.parseObject(network);
		networkBiz.addNetwork(networkInfo);
		
		return Response.ok(200).build();
	}
	
	@DELETE
	public Response deleteHost(@QueryParam("ids") String ids) {
		if(ids != null && ids.trim().length() > 0) {
			JSONObject response = networkBiz.deleteNetworkFromZabbix(ids);
			
			log.debug(String.format("deleteHost, response: %s", response));
			if(response != null && response.containsKey("result") && !response.getJSONObject("result").isEmpty()) {
				JSONArray hostIds = response.getJSONObject("result").getJSONArray("hostids");
				
				for(int i=0; i<hostIds.size(); i++) {
					networkBiz.deleteNetwork(hostIds.getString(i));
				}
			}
		}
		
		return Response.ok(200).build();
	}
	@Path("/{id}/info")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNetworkDetail(@PathParam("id") String id) {
		NetworkVO host = networkBiz.queryHostById(id);
		
		log.debug(String.format("getNetworkDetail, %s", host));
		
		return Response.ok(host).build();
	}
	
	@Path("/{hostId}")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray getOctetsKeys(@PathParam("hostId") String hostId) {
		JSONArray itemKeys =networkBiz.getkeys(hostId);
		
		log.debug(String.format("ifIndex, %s", itemKeys));
		
		return itemKeys;
	}
	@Path("/port")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getValue(@QueryParam("params") String params,@QueryParam("hostid") String hostId){
		String[] array =params.split(",");
		List<PortModel> port =networkBiz.queryItems(hostId,array);
		return Response.ok(port).build();
	}
	
	@Path("/storage")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDiskValue(@QueryParam("params") String params,@QueryParam("hostid") String hostId){
		String[] array =params.split(",");
		List<StorageModel> disk =networkBiz.getDiskValue(hostId,array);
		return Response.ok(disk).build();
	}
}
