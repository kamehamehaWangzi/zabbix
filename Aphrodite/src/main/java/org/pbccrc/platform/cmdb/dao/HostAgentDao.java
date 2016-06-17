package org.pbccrc.platform.cmdb.dao;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.vo.HostAgentVO;
import org.springframework.stereotype.Repository;

@Repository
public class HostAgentDao extends AbstractMyBatisDao {

	public int insertHostAgent(HostAgentVO hostAgentVO){
		return this.getSqlSession().insert("dao.HostAgentDao.insertHostAgent", hostAgentVO);
	}

	public List<HostAgentVO> queryAll(HostAgentVO vo, Pagination pagination) {
		return this.getSqlSession().selectList("dao.HostAgentDao.queryAll", vo, new RowBounds(pagination.getOffset(),pagination.getPageSize()));
	}

	public int deletAgent(List<String> ids) {
		return this.getSqlSession().delete("dao.HostAgentDao.deleteHostAgent", ids);
	}
}
