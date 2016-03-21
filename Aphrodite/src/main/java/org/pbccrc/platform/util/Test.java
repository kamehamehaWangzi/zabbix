package org.pbccrc.platform.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

public class Test {

	public static void main(String[] args) {
		String hosts = "[\"1\",\"3\"]";
		JSONArray jsonArray = JSON.parseArray(hosts); 
		 Object[] objects = jsonArray.toArray();
		 for (Object object : objects) {
			 System.out.println(object);
		 }
		 
	}
}
