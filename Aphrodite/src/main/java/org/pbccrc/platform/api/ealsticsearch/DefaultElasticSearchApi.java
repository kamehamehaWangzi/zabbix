package org.pbccrc.platform.api.ealsticsearch;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.pbccrc.platform.util.Constant;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Component
public class DefaultElasticSearchApi implements ElasticSearchApi {

	private static final Logger logger = Logger.getLogger(DefaultElasticSearchApi.class);

	private Map<String, Integer> esHostsMap;
	private Client client;

	public DefaultElasticSearchApi() {
		this.esHostsMap = Constant.ELASTICSEARCH_ADDRESS;
		init();
	}

	public DefaultElasticSearchApi(Map<String, Integer> esHostsMap) {
		this.esHostsMap = esHostsMap;
		init();
	}

	public void init() {
		logger.debug("ElasticSearch Transport Client init.");
		
		try {
			client = TransportClient.builder().build();
			Iterator<String> iter = esHostsMap.keySet().iterator();
			while (iter.hasNext()) {
				String host = iter.next();
				int port = esHostsMap.get(host);
				((TransportClient) client).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void finallize() {
		logger.debug("ElasticSearch Transport Client close.");
		
		client.close();
	}

	public int searchTotalNum(String index) {
		JSONObject hits = this.search(index);
		return hits.getInteger("total");
	}

	public JSONObject search(String index) {
		return this.search(index, null, 0, 50, null, null);
	}
	
	public JSONObject search(String index, Map<String, Object> param) {
		return this.search(index, param, 0, 50, null, null);
	}
	
	public JSONObject search(String index, Map<String, Object> param, String startDate, String endDate) {
		return this.search(index, param, 0, 50, startDate, endDate);
	}

	public JSONObject search(String index, Map<String, Object> param, int from, int size, String startDate, String endDate) {
		SearchRequestBuilder builder = client.prepareSearch(index);
		
		if(param != null) {
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			for(String key : param.keySet()) {
				boolQueryBuilder.must(QueryBuilders.matchQuery(key, param.get(key)));
			}
			
			builder.setQuery(boolQueryBuilder);
		}
		
		if(startDate != null && startDate.trim().length() > 0 && endDate != null && endDate.trim().length() > 0) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			try {
				builder.setPostFilter(QueryBuilders.rangeQuery("@timestamp").from(format.parse(startDate)).to(format.parse(endDate)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		SearchResponse response = builder.setFrom(from).setSize(size).addSort("@timestamp", SortOrder.DESC).setExplain(true).execute().actionGet();
		if(response != null) {
			JSONObject result = JSON.parseObject(response.toString());
			if(result.containsKey("hits")) {
				return result.getJSONObject("hits");
			}
		}
		
		return null;
	}

}
