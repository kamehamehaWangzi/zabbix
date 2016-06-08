package org.pbccrc.platform.project.biz.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pbccrc.platform.cmdb.dao.HostDao;
import org.pbccrc.platform.cmdb.dao.MonitorDataDao;
import org.pbccrc.platform.cmdb.dao.TaskDao;
import org.pbccrc.platform.cmdb.dao.TaskDataDao;
import org.pbccrc.platform.model.GraphModel;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.model.Series;
import org.pbccrc.platform.model.ZabbixDataModel;
import org.pbccrc.platform.project.biz.ITaskDataBiz;
import org.pbccrc.platform.util.ZabbixDataUtil;
import org.pbccrc.platform.util.ZabbixOperator;
import org.pbccrc.platform.vo.HostVO;
import org.pbccrc.platform.vo.MonitorDataVO;
import org.pbccrc.platform.vo.TaskDataVO;
import org.pbccrc.platform.vo.TaskVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Service
public class TaskDataBizImpl implements ITaskDataBiz{
	
	@Autowired
	private TaskDataDao taskDataDao;
	
	@Autowired
	TaskDao taskDao;
	
	@Autowired
	MonitorDataDao monitorDataDao;
	
	@Autowired
	HostDao hostDao;
	
	@Autowired
	private ZabbixDataUtil zabbixDataUtil;
	
	@Autowired
	private ZabbixOperator zabbixOperator;
	
	public static final String TYPE_CPU = "CPU";
	public static final String TYPE_DISK = "DISK";
	public static final String TYPE_DISK_CNT = "DISK_CNT";
	public static final String TYPE_NET = "NET";
	public static final String TYPE_MEMORY = "MEMORY";

	public List<TaskDataVO> queryTaskDatas(TaskDataVO vo, Pagination pagination) {
		return taskDataDao.queryAll(vo, pagination);
	}
	
	public void addTaskData(JSONObject taskData) {
		
		TaskDataVO vo = new TaskDataVO();
		vo.setTaskId(taskData.getInteger("taskId"));
		vo.setStartTime(taskData.getString("startTime"));
		vo.setEndTime(taskData.getString("endTime"));
//		vo.setPath(taskData.getString("path"));
		
		taskDataDao.insertTaskData(vo);
		
	}
	
	
	/**
	 * （定时）获取任务的监控数据，保存到数据库和文件系统持久化
	 * 2016.5.6 zhp
	 * */
	public int saveTaskDataMonitor2DB(String id, String path){
		
		int result = 0;
		//根据id在taskData表中获取taskData
		TaskDataVO taskVO = taskDataDao.queryByTaskDataId(id);
		
		int resultCPU = zabbixDataUtil.obtainZabbixData(taskVO, TYPE_CPU, path, null);
		int resultDISK = zabbixDataUtil.obtainZabbixData(taskVO, TYPE_DISK, path, 1024);
		int resultDISKCnt = zabbixDataUtil.obtainZabbixData(taskVO, TYPE_DISK_CNT, path, null);
		int resultNET = zabbixDataUtil.obtainZabbixData(taskVO, TYPE_NET, path, 1024);
		int resultMEMORY = zabbixDataUtil.obtainZabbixData(taskVO, TYPE_MEMORY, path, 1024*1024*1024);
		
		result = resultCPU & resultDISK & resultDISKCnt 
				& resultNET & resultMEMORY ;

		//将任务状态置为“已获取”为1
		taskVO.setPath("1");
		result = taskDataDao.updateTaskData(taskVO);
		
		return result;
	}

	public int saveAllTaskDataMonitor2DB(String taskId,String path){
		
		int result = 0;
		//获取所有的已经执行完毕的任务Id
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String endTime = sf.format(new Date());
		
		Map<String,String> paramMap = new HashMap<String,String>();
		paramMap.put("taskId", taskId);
		paramMap.put("endTime", endTime);
		
		List<TaskDataVO> taskDataIdList = taskDataDao.queryTaskDataByTime(paramMap);
		
		if(taskDataIdList!=null && taskDataIdList.size()>0){
			//循环遍历所有任务
			for(TaskDataVO task : taskDataIdList){
				
				System.out.println(task.getTaskId());
				int resultCPU = zabbixDataUtil.obtainZabbixData(task, TYPE_CPU, path,null);
				int resultDISK = zabbixDataUtil.obtainZabbixData(task, TYPE_DISK, path, 1024);
				int resultDISKCnt = zabbixDataUtil.obtainZabbixData(task, TYPE_DISK_CNT, path, null);
				int resultNET = zabbixDataUtil.obtainZabbixData(task, TYPE_NET, path,1024);
				int resultMEMORY = zabbixDataUtil.obtainZabbixData(task, TYPE_MEMORY, path,1024*1024*1024);
				
				result = resultCPU & resultDISK & resultDISKCnt 
						& resultNET & resultMEMORY ;
				
				//将任务状态置为“已获取”为1
				if(result==1){
					task.setPath("1");
					result = taskDataDao.updateTaskData(task);
				}
			}
		}
		
		return result;
	}


	
	/**
	 * 根据任务ID,读取监控数据文件，反馈监控数据
	 * 2016.5.6 zhp
	 */
	public JSONArray showTaskMonitorData(String taskDataId){
		//按照主机为单元展示结果
		JSONArray hostResult = new JSONArray();
		//根据id在taskData表中获取taskData
		TaskDataVO taskVO = taskDataDao.queryByTaskDataId(taskDataId);
		//根据 taskId 获取关联的 主机host
		TaskVO task = taskDao.queryByTaskId(taskVO.getTaskId().toString());
		
		JSONArray hostArray = JSONArray.parseArray(task.getHosts());
		// TODO
//		String startTime = taskVO.getStartTime();
//		String endTime = taskVO.getEndTime();
		
		//循环获得主机数据
		for(int i = 0;i<hostArray.size();i++){
			
			HostVO host = hostDao.queryById(hostArray.getString(i));
			//查询监控数据
			//根据taskDataId ,主机号, 获取监控数据文件 public List<MonitorDataVO> selectMonitorDataList(Map<String,String> paramMap)
			Map<String,String> paramMap = new HashMap<String, String>();
			paramMap.put("taskDataId", taskDataId);
			paramMap.put("hostId", host.getId().toString());
			List<MonitorDataVO> dataList= monitorDataDao.selectMonitorDataList(paramMap);
			
			//封装单机host的监控结果
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("hostIP", host.getIp1());
			map.put("hostId", host.getId().toString());
			
			List<String> netList = zabbixOperator.getNetInfoByItem(host.getZabbixHostid());
			map.put("netParam", netList);
			
			List<String> diskList = zabbixOperator.getDiskInfoByItem(host.getZabbixHostid());
			map.put("diskParam", diskList);
			
			//根据监控类型封装到不同的对象中
			for(MonitorDataVO monitor : dataList){
				try {
					File file = new File(monitor.getPath());
					ZabbixDataModel tempList = zabbixDataUtil.loadZabbixData(file);
					Map<String,String> tempObject = getMaxMinData(tempList.getGraph());
					map.putAll(tempObject);;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			hostResult.add(map);
		}
		return hostResult;
	}
	
	/**
	 * 获取监控文件，监控数据表的数据图形化
	 * 2016.5.6 zhp
	 * @return
	 */
	public GraphModel showDetaiGraph(String taskData_id,String hostId,String type){
		
		GraphModel resultModel = new GraphModel();
		
		Map<String,String> paramMap = new HashMap<String,String>();
		paramMap.put("taskDataId", taskData_id);
		paramMap.put("itemName", type);
		
		if(!hostId.equals("all")){
			paramMap.put("hostId", hostId);
		}
			
		List<MonitorDataVO> dataList = monitorDataDao.selectMonitorDataList(paramMap);
		try {
			resultModel = zabbixDataUtil.loadZabbixData(new File(dataList.get(0).getPath())).getGraph();
			resultModel.getyAxis().get(0).setName(resultModel.getLegend().get(0));
			for(int i=1; i<dataList.size();i++){
				
				GraphModel tempModel = zabbixDataUtil.loadZabbixData(new File(dataList.get(i).getPath())).getGraph();
				resultModel.getLegend().addAll(tempModel.getLegend());//设置曲线主题
				tempModel.getyAxis().get(0).setName(tempModel.getLegend().get(0));//设置曲线名称
				resultModel.getyAxis().addAll(tempModel.getyAxis());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultModel;
	}
	
	/**
	 * 获取监控数据的罪值
	 * @param graphModel
	 * @return
	 */
	public Map<String,String> getMaxMinData(GraphModel graphModel){
		Map<String,String> resultJsonObject = new HashMap<String,String>();
		if(graphModel != null ){
			List<Series> seriesTemp = graphModel.getyAxis();
			for(Series e : seriesTemp){
				if(e.getData()==null || e.getData().size()==0){
					continue;
				}
				float max = Float.parseFloat(e.getData().get(0));
				float min = Float.parseFloat(e.getData().get(0));
				float avg = Float.parseFloat(e.getData().get(0));
				for(int k = 1; k<e.getData().size(); k++){
					float currentOne = Float.parseFloat(e.getData().get(k));
					max = currentOne>max?currentOne:max;
					min = currentOne<min?currentOne:min;
					avg += currentOne;
				}
				avg = avg / e.getData().size();
//				System.out.println(avg);
				resultJsonObject.put(e.getName().replace(' ', '_'), (float)(Math.round(max*10))/10 +"/"+(float)(Math.round(min*10))/10+"/"+(float)(Math.round(avg*10))/10);
			}
		}
		return resultJsonObject;
	}
	
	public void deleteTaskData(String taskDataId) {
		TaskDataVO vo = taskDataDao.queryByTaskDataId(taskDataId);
		if(vo != null) {
			int result = taskDataDao.deleteTaskData(String.valueOf(vo.getId()));
			//清空monitorData表中已经持久化的数据
			if(result==1){
				result = monitorDataDao.cleanSavedMonitorDataByTaskDataId(taskDataId);
			}
		}
	}

	@Override
	public int modifyTaskDataTime(String taskDataId, int startOrEnd) {
		
		int result = 0;

		TaskDataVO vo = new TaskDataVO();
		vo.setId(Integer.parseInt(taskDataId));
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String currentTime = sf.format(new Date());
		
		if(startOrEnd==0){
			vo.setPath("0");
			vo.setStartTime(currentTime);
			vo.setEndTime(currentTime);
			result = taskDataDao.updateTaskData(vo);

			//清空monitorData表中已经持久化的数据
			if(result==1){
				result = monitorDataDao.cleanSavedMonitorDataByTaskDataId(taskDataId);
			}
			
		}else if(startOrEnd==1){
			vo.setEndTime(currentTime);
			result = taskDataDao.updateTaskData(vo);
		}
		
		return result;
	}

}
