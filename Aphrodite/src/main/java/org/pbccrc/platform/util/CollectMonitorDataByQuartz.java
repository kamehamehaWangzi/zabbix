package org.pbccrc.platform.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.pbccrc.platform.cmdb.dao.TaskDataDao;
import org.pbccrc.platform.project.biz.ITaskDataBiz;
import org.pbccrc.platform.vo.TaskDataVO;
import org.springframework.beans.factory.annotation.Autowired;

public class CollectMonitorDataByQuartz {
	
	@Autowired
	private TaskDataDao taskDataDao;
	
	@Autowired
	private ITaskDataBiz taskDataBiz;
	
	public CollectMonitorDataByQuartz() {
		System.out.println("Quartz 初始化了 ");
	}

	public void execute(){
		
		//根据当前时间，获取待取数据的任务
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String endTime = sf.format(new Date());
		List<TaskDataVO> resultList = null;
		try{
			resultList = taskDataDao.queryTaskDataByTime(endTime);
		//对每个任务进行持久化
		for(TaskDataVO e : resultList){
			System.out.println(e.toString());
			String path = Constant.ZABBIX_MONITOR_DATA_PATH;
			String bathPath = this.getClass().getResource("/").getFile().toString();
			bathPath = bathPath.replace("/WEB-INF/classes/", path);
			System.out.println(bathPath);
			taskDataBiz.saveTaskDataMonitor2DB(e.getId().toString(), bathPath);
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
