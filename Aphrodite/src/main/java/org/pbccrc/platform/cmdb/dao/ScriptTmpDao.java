package org.pbccrc.platform.cmdb.dao;

import java.util.List;






import org.apache.ibatis.session.RowBounds;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.vo.ScriptTmpVO;
import org.pbccrc.platform.vo.ScriptVO;
import org.springframework.stereotype.Repository;

@Repository
public class ScriptTmpDao extends AbstractMyBatisDao {

	public List<ScriptTmpVO> queryAllPage(ScriptTmpVO vo,Pagination pagination) {
		return this.getSqlSession().selectList("dao.ScriptTmpDao.queryAll", vo,new RowBounds(pagination.getOffset(), pagination.getPageSize()));
	}
	public List<ScriptTmpVO> queryAll(ScriptTmpVO vo) {
		return this.getSqlSession().selectList("dao.ScriptTmpDao.queryAll", vo);
	}
	
	
	public void deleteTemplate(String id) {
		this.getSqlSession().insert("dao.ScriptTmpDao.deleteTemplate", id);
		
	}
	
	public void insertTemplate(ScriptTmpVO vo){
		 this.getSqlSession().insert("dao.ScriptTmpDao.insertTemplate", vo);
	}
	
	public ScriptTmpVO queryTmpInfo(String id){
		 return this.getSqlSession().selectOne("dao.ScriptTmpDao.queryTmpInfo", id);
	}
	
	
	public void updateTemplate(ScriptTmpVO vo) {
		this.getSqlSession().update("dao.ScriptTmpDao.updateTemplate", vo);
		
	}
}
