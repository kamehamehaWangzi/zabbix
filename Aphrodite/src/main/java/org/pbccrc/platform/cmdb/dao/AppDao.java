package org.pbccrc.platform.cmdb.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.vo.AppVO;
import org.pbccrc.platform.vo.HostAppVO;
import org.springframework.stereotype.Repository;

@Repository
public class AppDao extends AbstractMyBatisDao {
	
	public List<AppVO> queryAll(AppVO vo, Pagination pagination) {
		return this.getSqlSession().selectList("dao.AppDao.queryAll", vo, new RowBounds(pagination.getOffset(), pagination.getPageSize()));
	}
	
	public AppVO queryById(String id) {
		return this.getSqlSession().selectOne("dao.AppDao.queryById", id);
	}
	
	public List<AppVO> queryAppsByHost(String id) {
		return this.getSqlSession().selectList("dao.AppDao.queryAppsByHost", id);
	}
	
	public List<HostAppVO> queryHostAppByAppId(Integer appId) {
		return this.getSqlSession().selectList("dao.HostAppDao.queryByAppId", appId);
	}
	
	public void insertApp(AppVO vo) {
		this.getSqlSession().insert("dao.AppDao.insertApp", vo);
	}
	
	public void deleteApp(String id) {
		this.getSqlSession().delete("dao.AppDao.deleteApp", id);
	}
	
	public void deleteHostAppByAppId(String id) {
		this.getSqlSession().delete("dao.HostAppDao.deleteByAppId", id);
	}
	
	public void insertAppToHost(Map<String, String> params) {
		this.getSqlSession().insert("dao.HostAppDao.insertHostApp", params);
	}
	
	public void deleteAppFromHost(Map<String, String> params) {
		this.getSqlSession().insert("dao.HostAppDao.deleteByHostAndApp", params);
	}

}
