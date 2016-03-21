package org.pbccrc.platform.deploy.biz;

import java.util.List;

import org.pbccrc.platform.api.saltstack.SaltRequest;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.vo.SaltJobVO;

import com.alibaba.fastjson.JSONObject;

public interface IDeployBiz {

	JSONObject deployApp(List<String> hosts, String app);
	
	JSONObject executeCmd(String host, String cmd);
	
	JSONObject executeCmd(List<String> hosts, String cmd);
	
    JSONObject executeCmdAsync(String host, String cmd);
	
	JSONObject executeCmdAsync(List<String> hosts, String cmd);
	
	JSONObject executeScript(List<String> hosts, String script, String param);
	
	Boolean isServiceAlive(String appId, String hostId);
	
	List<SaltJobVO> querySaltJob(SaltJobVO vo, Pagination pagination);
	
	SaltJobVO querySaltJobById(String id);
	
	SaltJobVO queryByJobIdAndHostName(String jobId, String hostName);
	
	List<SaltJobVO> queryNewJob();
	
	int updateJob(SaltJobVO vo);
	
	JSONObject callJobById(String jobId);
	
	void insertSaltJob(SaltRequest request, JSONObject response);
	
	void executeQuartzJob();
}
