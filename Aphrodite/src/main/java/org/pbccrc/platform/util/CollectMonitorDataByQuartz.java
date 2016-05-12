package org.pbccrc.platform.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.stereotype.Repository;

@Repository
public class CollectMonitorDataByQuartz {
	
	public CollectMonitorDataByQuartz() {
		System.out.println("Quartz 初始化了 ");
	}

	public void execute(){
		//设置定时任务
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sf.format(new Date());
		System.out.println("quartz startingAAAA………………………………"+date);
		
	}
}
