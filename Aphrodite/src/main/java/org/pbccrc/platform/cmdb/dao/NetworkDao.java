package org.pbccrc.platform.cmdb.dao;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.vo.NetworkVO;
import org.springframework.stereotype.Repository;

@Repository
public class NetworkDao extends AbstractMyBatisDao {
	
	public List<NetworkVO> queryAll(NetworkVO vo, Pagination pagination) {
		return this.getSqlSession().selectList("dao.NetworkDao.queryAll", vo, new RowBounds(pagination.getOffset(), pagination.getPageSize()));
	}	
	
	public NetworkVO queryById(String id) {
		return this.getSqlSession().selectOne("dao.NetworkDao.queryById", id);
	}
	
	public void insertNetwork(NetworkVO vo) {
		this.getSqlSession().insert("dao.NetworkDao.insertNetwork", vo);
	}
	
	public NetworkVO queryByHostid(String id) {
		return this.getSqlSession().selectOne("dao.NetworkDao.queryByHostId", id);
	}
	
	public void deleteHost(String id) {
		this.getSqlSession().delete("dao.NetworkDao.deleteHost", id);
	}
}
