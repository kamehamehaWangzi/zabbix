package org.pbccrc.platform.cmdb.dao;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.vo.TaskVO;
import org.springframework.stereotype.Repository;

@Repository
public class TaskDao extends AbstractMyBatisDao {
	
	public List<TaskVO> queryAll(TaskVO vo, Pagination pagination) {
		return this.getSqlSession().selectList("dao.TaskDao.queryAll", vo, new RowBounds(pagination.getOffset(), pagination.getPageSize()));
	}
	
	public TaskVO queryByTaskId(String id) {
		return this.getSqlSession().selectOne("dao.TaskDao.queryByTaskId", id);
	}
	
	public void insertTask(TaskVO vo) {
		this.getSqlSession().insert("dao.TaskDao.insertTask", vo);
	}
	
	public void deleteTask(String id) {
		this.getSqlSession().delete("dao.TaskDao.deleteTask", id);
	}
	
	public void deleteTaskByProject(String projectId) {
		this.getSqlSession().delete("dao.TaskDao.deleteTaskByProject", projectId);
	}

}
