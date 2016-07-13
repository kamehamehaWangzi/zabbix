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
	 * @return 
	 */
	@Override
	public int deployRemoteMachine(JSONObject hostInfo) {
		
		HostAgentVO vo = new HostAgentVO();
		vo.setAgentIp(hostInfo.getString("ip"));
		vo.setAgentName(hostInfo.getString("name"));
		vo.setAgentPwd(hostInfo.getString("password"));
		vo.setAgentPort(hostInfo.getString("agentPort"));
		vo.setOsType(hostInfo.getString("osVersion"));
		vo.setRemark(hostInfo.getString("description"));
		
		//验证端口是否被占用 ，必要时可以 取消注释
//		int testResult = deployAgentUtil.testIsUserdPort(vo.getAgentIp(), vo.getAgentName(), vo.getAgentPwd(),vo.getAgentPort());
//		if(testResult==-1){
//			return -1; //用户名或密码错误
//		}
//		if(testResult == 1){
//			return 3;//端口占用
//		}
		
		List<String> isExistDeployed = hostAgentDao.queryHostByIp(vo.getAgentIp());
		
		if(isExistDeployed != null && isExistDeployed.size()>0){
			return 4; //机器已经部署
		}
		
		//1.远程部署
		System.out.println("正在部署……");
		int deployResult = deployAgentUtil.deployAgent(vo.getAgentIp(), vo.getAgentName(), vo.getAgentPwd());
		
		//2.记录数据库
		
		System.out.println(vo.toString());
		if(deployResult==1){
			int insertResult = hostAgentDao.insertHostAgent(vo);
			System.out.println(insertResult);
		}
		
		return deployResult;
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

	@Override
	public String obtainDeployProgress() {
		return deployAgentUtil.getCommonStr().toString();
	}

}
