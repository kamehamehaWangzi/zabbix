package org.pbccrc.platform.cmdb.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.vo.TaskDataVO;
import org.springframework.stereotype.Repository;

@Repository
public class TaskDataDao extends AbstractMyBatisDao {
	
	public List<TaskDataVO> queryAll(TaskDataVO vo, Pagination pagination) {
		return this.getSqlSession().selectList("dao.TaskDataDao.queryAll", vo, new RowBounds(pagination.getOffset(), pagination.getPageSize()));
	}
	
	public void insertTaskData(TaskDataVO vo) {
		this.getSqlSession().insert("dao.TaskDataDao.insertTaskData", vo);
	}
	
	public int updateTaskData(TaskDataVO vo) {
		return this.getSqlSession().update("dao.TaskDataDao.updateTaskData",vo);
	}
	
	public void deleteTaskData(String id) {
		this.getSqlSession().delete("dao.TaskDataDao.deleteTaskData", id);
	}
	
	public TaskDataVO queryByTaskDataId(String id) {
		return this.getSqlSession().selectOne("dao.TaskDataDao.queryByTaskDataId", id);
	}
	
	public List<TaskDataVO> queryTaskDataByTime(Map<String,String> paramMap){
		return this.getSqlSession().selectList("dao.TaskDataDao.queryTaskDataByEndTime",paramMap);
	}
	
}
