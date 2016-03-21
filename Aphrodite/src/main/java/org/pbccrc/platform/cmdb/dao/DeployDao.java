package org.pbccrc.platform.cmdb.dao;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.vo.SaltJobVO;
import org.springframework.stereotype.Repository;

@Repository
public class DeployDao extends AbstractMyBatisDao {

	public List<SaltJobVO> queryAll(SaltJobVO vo, Pagination pagination) {
		return this.getSqlSession().selectList("dao.DeployDao.queryAll", vo, new RowBounds(pagination.getOffset(), pagination.getPageSize()));
	}
	
	public SaltJobVO queryById(String id) {
		return this.getSqlSession().selectOne("dao.DeployDao.queryJobById", id);
	}
	
	public SaltJobVO queryByJobIdAndHostName(SaltJobVO vo) {
		List<SaltJobVO> vos = this.getSqlSession().selectList("dao.DeployDao.queryByJobIdAndHostName", vo);
		if(vos != null && vos.size() > 0) {
			return vos.get(0);
		}
		
		return null; 
	}
	
	public List<SaltJobVO> queryNewJob() {
		return this.getSqlSession().selectList("dao.DeployDao.queryDistinctJob");
	}
	
	public void insertJob(SaltJobVO vo) {
		this.getSqlSession().insert("dao.DeployDao.insertJob", vo);
	}
	
	public int updateJob(SaltJobVO vo) {
		return this.getSqlSession().update("dao.DeployDao.updateJob", vo);
	}
	
}
