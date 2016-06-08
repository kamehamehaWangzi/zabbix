package org.pbccrc.platform.cmdb.dao;

import java.util.List;
import java.util.Map;

import org.pbccrc.platform.vo.MonitorDataVO;
import org.springframework.stereotype.Repository;

@Repository  
public class MonitorDataDao extends AbstractMyBatisDao{

	public int insertMonitorData(MonitorDataVO vo){
		return this.getSqlSession().insert("dao.MonitorDataDao.insertMonitorData", vo);
	}
	//selectMonitorData
	public List<MonitorDataVO> selectMonitorDataList(Map<String,String> paramMap){
		return this.getSqlSession().selectList("dao.MonitorDataDao.selectMonitorData", paramMap);
	}	
	
	/**
	 * @param taskDataId
	 * @return
	 */
	public List<String> queryItemNameByTaskDataId(String taskDataId){
		return this.getSqlSession().selectList("dao.MonitorDataDao.queryItemNameByTaskDataId", taskDataId);
	}
	
	public int cleanSavedMonitorDataByTaskDataId(String taskDataId){
		return this.getSqlSession().delete("dao.MonitorDataDao.deleteDataByTaskDataId", taskDataId);
	}
	
}
