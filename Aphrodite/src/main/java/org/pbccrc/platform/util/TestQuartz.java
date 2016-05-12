package org.pbccrc.platform.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestQuartz {

	public static void main(String[] args) {
		
		ApplicationContext acx = new ClassPathXmlApplicationContext("classpath:conf/spring/applicationContext-quartz.xml");
		
		//如果配置文件中将Quartz监听器的lazy-init设置为false,则不用实例化
		//acx.getBean("registerQuartz");
		System.out.println("test End……");
	
	}
}
