package org.pbccrc.platform.cmdb.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.vo.HostVO;
import org.springframework.stereotype.Repository;

@Repository
public class HostDao extends AbstractMyBatisDao {
	
	public List<HostVO> queryAll(HostVO vo, Pagination pagination) {
		return this.getSqlSession().selectList("dao.HostDao.queryAll", vo, new RowBounds(pagination.getOffset(), pagination.getPageSize()));
	}
	
	public HostVO queryById(String id) {
		return this.getSqlSession().selectOne("dao.HostDao.queryById", id);
	}
	
	public HostVO queryByHostid(String id) {
		return this.getSqlSession().selectOne("dao.HostDao.queryByHostId", id);
	}
	
	public List<HostVO> queryHostsByApp(Map<String, Object> param, Pagination pagination) {
		return this.getSqlSession().selectList("dao.HostDao.queryHostsByApp", param, new RowBounds(pagination.getOffset(), pagination.getPageSize()));
	}
	
	public List<HostVO> queryHostsExceptApp(String appId) {
		return this.getSqlSession().selectList("dao.HostDao.queryHostsExceptApp", appId);
	}
	
	public List<HostVO> queryHosts() {
		return this.getSqlSession().selectList("dao.HostDao.queryHosts");
	}
	
	public List<HostVO> queryHostsByIds(String[] ids) {
		return this.getSqlSession().selectList("dao.HostDao.queryHostsByIds", ids);
	}
	
	public void insertHost(HostVO vo) {
		this.getSqlSession().insert("dao.HostDao.insertHost", vo);
	}
	
	public void deleteHost(String id) {
		this.getSqlSession().delete("dao.HostDao.deleteHost", id);
	}
	
	public void deleteHostByZabbixHostId(String hostId) {
		this.getSqlSession().delete("dao.HostDao.deleteHostByZabbixHostId", hostId);
	}
	
	public void deleteHostAppByHostId(String id) {
		this.getSqlSession().delete("dao.HostAppDao.deleteByHostId", id);
	}

}
