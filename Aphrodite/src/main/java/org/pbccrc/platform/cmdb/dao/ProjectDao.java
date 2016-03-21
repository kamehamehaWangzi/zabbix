package org.pbccrc.platform.cmdb.dao;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.vo.ProjectVO;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectDao extends AbstractMyBatisDao {
	
	public void insertProject(ProjectVO vo) {
		this.getSqlSession().insert("dao.ProjectDao.insertProject", vo);
	}
	
	public List<ProjectVO> queryAll(ProjectVO vo, Pagination pagination) {
		return this.getSqlSession().selectList("dao.ProjectDao.queryAll", vo, new RowBounds(pagination.getOffset(), pagination.getPageSize()));
	}
	
	public ProjectVO queryByProjectId(String id) {
		return this.getSqlSession().selectOne("dao.ProjectDao.queryByProjectId", id);
	}
	
	public void deleteProject(String id) {
		this.getSqlSession().delete("dao.ProjectDao.deleteProject", id);
	}
	
	public List<ProjectVO> queryProjects() {
		return this.getSqlSession().selectList("dao.ProjectDao.queryProjects");
	}

}
