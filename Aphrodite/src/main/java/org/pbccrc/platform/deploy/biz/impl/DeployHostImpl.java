package org.pbccrc.platform.deploy.biz.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.pbccrc.platform.cmdb.dao.HostAgentDao;
import org.pbccrc.platform.deploy.biz.IDeployHostBiz;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.util.DeployAgentUtil;
import org.pbccrc.platform.vo.HostAgentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

@Service
public class DeployHostImpl implements IDeployHostBiz {

	@Autowired
	private HostAgentDao hostAgentDao;
	
	@Autowired
	private DeployAgentUtil deployAgentUtil;
	
	/**
	 * 远程部署agent
	 */
	@Override
	public int deployRemoteMachine(JSONObject hostInfo) {
		
		HostAgentVO vo = new HostAgentVO();
		vo.setAgentIp(hostInfo.getString("ip"));
		vo.setAgentName(hostInfo.getString("name"));
		vo.setAgentPwd(hostInfo.getString("password"));
		vo.setOsType(hostInfo.getString("osVersion"));
		vo.setRemark(hostInfo.getString("description"));
		//1.远程部署
		System.out.println("正在部署……");
		deployAgentUtil.deployAgent(vo.getAgentIp(), vo.getAgentName(), vo.getAgentPwd());
		
		//2.记录数据库
		
		System.out.println(vo.toString());
		
		int insertResult = hostAgentDao.insertHostAgent(vo);
		System.out.println(insertResult);
		
		return 0;
	}

	@Override
	public List<HostAgentVO> queryHostAgents(HostAgentVO vo, Pagination pagination) {
		return hostAgentDao.queryAll(vo, pagination);
	}

	@Override
	public int deleteDeployHostAgent(String ids) {
		String[] param = ids.split(",");
		List<String> paramList = Arrays.asList(param);
		return hostAgentDao.deletAgent(paramList);
	}

}
