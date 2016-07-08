package org.pbccrc.platform.deploy.biz;

import java.util.List;

import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.vo.HostAgentVO;

import com.alibaba.fastjson.JSONObject;

public interface IDeployHostBiz {

	public int deployRemoteMachine(JSONObject hostInfo);

	public List<HostAgentVO> queryHostAgents(HostAgentVO vo, Pagination pagination);

	public int deleteDeployHostAgent(String ids);

	public String obtainDeployProgress();
}
