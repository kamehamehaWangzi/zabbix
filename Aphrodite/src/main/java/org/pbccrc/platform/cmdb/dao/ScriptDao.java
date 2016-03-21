package org.pbccrc.platform.cmdb.dao;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.vo.ScriptVO;
import org.springframework.stereotype.Repository;

@Repository
public class ScriptDao extends AbstractMyBatisDao {

	public List<ScriptVO> queryAll(ScriptVO vo,Pagination pagination) {
		return this.getSqlSession().selectList("dao.ScriptDao.queryAll",vo, new RowBounds(pagination.getOffset(), pagination.getPageSize()));
	}
	
	public void deleteScript(String id) {
		this.getSqlSession().insert("dao.ScriptDao.deleteScript", id);
	}

	public void insertScript(ScriptVO vo) {
		this.getSqlSession().insert("dao.ScriptDao.insertScript", vo);
	}
	
	public ScriptVO queryById(String id) {
		return this.getSqlSession().selectOne("dao.ScriptDao.queryById", id);
	}
	
	public int updateScript(ScriptVO vo) {
		return this.getSqlSession().update("dao.ScriptDao.updateScript", vo);
	}
}
