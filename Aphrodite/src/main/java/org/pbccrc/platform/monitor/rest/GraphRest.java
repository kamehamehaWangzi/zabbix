package org.pbccrc.platform.monitor.rest;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.pbccrc.platform.model.GraphModel;
import org.pbccrc.platform.model.Series;
import org.pbccrc.platform.monitor.biz.IGraphBiz;
import org.pbccrc.platform.util.Constant;
import org.pbccrc.platform.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Path("/graph")
@Produces(MediaType.APPLICATION_JSON)
public class GraphRest {
	
	private static final Logger log = Logger.getLogger(GraphRest.class);
	
	@Autowired
	@Qualifier("graphBizImpl")
	private IGraphBiz graphBiz;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGraphData(@QueryParam("id") String id, //������ hostId
			@QueryParam("key") String key, //��ѯ���� �� ['system.cpu.util[,user]', 'system.cpu.util[,nice]']
			@QueryParam("likeSearch") Boolean likeSearch, 
			@QueryParam("graphType") String graphType, //ͼ������
			@QueryParam("scaler") Integer scaler, @QueryParam("startDate") String startDate, 
			@QueryParam("endDate") String endDate, @QueryParam("defaultDateRange") Integer defaultDateRange) {
		
		JSONArray keys = JSONArray.parseArray(key); //��ѯ�ַ���ͨ��JSonת��Ϊ����
		JSONArray keyItems = new JSONArray();
		
		for(int i=0; i<keys.size(); i++) {
			JSONObject items = graphBiz.queryItemsByHostKey(id, keys.getString(i), likeSearch);
			if(items != null && !items.isEmpty() && !items.getJSONArray("result").isEmpty()) {
				keyItems.addAll(items.getJSONArray("result"));
			}
		}
		
		log.debug(String.format("keys: %s", keyItems));
		
		//���÷�������ı���
		GraphModel graphData = new GraphModel();
		List<String> legend = new ArrayList<String>(); //����
		List<String> xAxis = new ArrayList<String>(); //X������
		List<Series> yAxis = new ArrayList<Series>(); //Y��valueֵ
		
		//ѭ�������õ���item���ϣ���ȡÿ��item����ʷ����
		JSONObject historyX = null;
		if(keyItems != null && !keyItems.isEmpty()) {
			
			//��ȡY���ֵ
			for(int i=0; i<keyItems.size(); i++) {
				
				JSONObject itemY = keyItems.getJSONObject(i);
				Integer valueType = itemY.getInteger("value_type");
				
				JSONObject historyY = graphBiz.queryHistoryByItem(itemY.getString("itemid"), valueType, startDate, endDate, defaultDateRange);
				historyX = historyY;
				
				log.debug(String.format("history: %s", historyY));
				
				//�õ����ݽ����
				JSONArray resultY = historyY.getJSONArray("result");
				
				Series series = new Series();
				
				//��result��������е�value���ݴ浽values������
				List<String> values = new ArrayList<String>();
				for(int j=0; j<resultY.size(); j++) {
					String value_key = resultY.getJSONObject(j).containsKey("value_avg") ? "value_avg" : "value";
					String value = null;
					if(scaler != null) {
						value = String.valueOf(resultY.getJSONObject(j).getDouble(value_key) / scaler);
					} else {
						value = String.valueOf(resultY.getJSONObject(j).get(value_key));
					}
					values.add(value);
				}
				
				String itemName = itemY.getString("name");
				
				//itemName�ĸ�ʽ��ת��
				if(itemName.indexOf("$") != -1) {
					Pattern pattern = Pattern.compile("\\[.*\\]");
					Matcher match = pattern.matcher(itemY.getString("key_"));
					if(match.find()) {
						String[] matchStr = match.group().replaceAll("\\[|\\]", "").split(",");
						
						Pattern patternIndex = Pattern.compile("\\$.d*");
						Matcher matchIndex = patternIndex.matcher(itemName);
						
						if(matchIndex.find()) {
							Integer index = Integer.parseInt(matchIndex.group().replaceAll("\\$", "").trim());
							itemName = itemName.replaceAll("\\$" + index, matchStr[index - 1]);
						}
					}
				}
				
				legend.add(itemName);
				series.setName(itemName);
				
				// echarts's area == line
				if(Constant.GRAPH_TYPE.area.getValue().equals(graphType)) {
					series.setType(Constant.GRAPH_TYPE.line.getValue());
					series.setItemStyle(JSONObject.parseObject("{normal: {areaStyle: {type: 'default'}}}"));
				} else {
					series.setType(graphType);
					series.setItemStyle(JSONObject.parseObject("{normal: {lineStyle: {type: 'solid'}}}"));
				}
				
				//����ȡ����ֵ���뵽yAxis��series�У�������ǰ̨��ʾ
				series.setData(values);
				yAxis.add(series);
			}
			
			
			//��ȡX�������ֵ
			JSONArray resultX = historyX.getJSONArray("result");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			
			for(int i=0; i<resultX.size(); i++) {
				JSONObject obj = resultX.getJSONObject(i);
				
				String clock = sdf.format(new Date(Long.parseLong(obj.getString("clock")) * 1000L));
				xAxis.add(clock);
			}
			
		}
		
		graphData.setLegend(legend);
		graphData.setxAxis(xAxis);
		graphData.setyAxis(yAxis);
		
		log.debug(graphData);
		
		return Response.ok(graphData).build();
	}
	@Path("/value")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getKeyData(@QueryParam("id") String id, @QueryParam("key") String key) {
		if(key == null || key.trim().length() == 0) {
			return Response.noContent().build();
		}
		JSONArray keys = JSONArray.parseArray(key);
		JSONArray keyItems = new JSONArray();
		
		for(int i=0; i<keys.size(); i++) {
			JSONObject items = graphBiz.queryItemsByHostKey(id, keys.getString(i), false);
			if(items != null && !items.isEmpty() && !items.getJSONArray("result").isEmpty()) {

				keyItems.addAll(items.getJSONArray("result"));
			}
		}
		
		return Response.ok(keyItems).build();
	}
	
	
	@Path("/type_value")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getKeyDataByType(@QueryParam("id") String id, @QueryParam("key") String key,@QueryParam("type") String type) {
		if(key == null || key.trim().length() == 0) {
			return Response.noContent().build();
		}
		JSONArray keys = JSONArray.parseArray(key);
		JSONArray keyItems = new JSONArray();
		
		for(int i=0; i<keys.size(); i++) {
			JSONObject items = graphBiz.queryItemsByHostKey(id, keys.getString(i), false);
			if(items != null && !items.isEmpty() && !items.getJSONArray("result").isEmpty()) {
				
				if( type!=null && type.length()!=0 && type.equals("loadbalance")&&keys.getString(i).equals("netopti.vs.name")){
					
					JSONObject	obj=items.getJSONArray("result").getJSONObject(0);
					String str=obj.getString("lastvalue");
					
					byte [] b=FileUtils.hexStringToBytes(StringUtils.trimAllWhitespace(str).trim());
					if(b.length>0){
							try {
								obj.put("lastvalue",new String(b,Constant.SHELLFILE_ENCODE));
								
							} catch (UnsupportedEncodingException e) {
								log.debug("vs name convert exception "+e.getMessage());
								e.printStackTrace();
							}
							keyItems.add(obj);
					}
				
				}else{
				keyItems.addAll(items.getJSONArray("result"));
				}
			}
		}
		
		return Response.ok(keyItems).build();
	}
	

}
