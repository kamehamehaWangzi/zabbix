package org.pbccrc.platform.deploy.biz.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.pbccrc.platform.api.ApiFactory;
import org.pbccrc.platform.api.saltstack.SaltRequest;
import org.pbccrc.platform.cmdb.dao.AppDao;
import org.pbccrc.platform.cmdb.dao.HostDao;
import org.pbccrc.platform.cmdb.dao.DeployDao;
import org.pbccrc.platform.deploy.biz.IDeployBiz;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.util.Constant;
import org.pbccrc.platform.vo.AppVO;
import org.pbccrc.platform.vo.SaltJobVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Service
public class DeployBizImpl implements IDeployBiz {
	
	private static final Logger log = Logger.getLogger(DeployBizImpl.class);

	@Autowired
	private ApiFactory apiFactory;
	
	@Autowired
	private HostDao hostDao;
	
	@Autowired
	private AppDao appDao;
	
	@Autowired
	private DeployDao deployDao;
	
	public JSONObject deployApp(List<String> hosts, String app) {
		SaltRequest request = new SaltRequest();
		request.setClient(Constant.SALT_CLIENT.async.getName());
		request.setTgt(hosts);
		request.setFun(Constant.SALT_FUN.state_sls.getName());
		request.setArg(app);
		
		log.debug(String.format("deployApp, request: %s", request.toString()));
		
		JSONObject response = apiFactory.saltstack().call(request);
		
		log.debug(String.format("deployApp, response: %s", response));
		
		this.insertSaltJob(request, response);
		log.debug("deployApp, insert job");
		
		return response;
	}
	
	public JSONObject executeCmd(String host, String cmd) {
		List<String> hosts = new ArrayList<String>();
		hosts.add(host);
		
		return executeCmd(hosts, cmd);
	}
	
	public JSONObject executeCmd(List<String> hosts, String cmd) {
		SaltRequest request = new SaltRequest();
		request.setClient(Constant.SALT_CLIENT.sync.getName());
		request.setTgt(hosts);
		request.setFun(Constant.SALT_FUN.cmd.getName());
		request.setArg(cmd);
		
		log.debug(String.format("executeCmd, request: %s", request.toString()));
		
		JSONObject response = apiFactory.saltstack().call(request);
		
		log.debug(String.format("executeCmd, response: %s", response));
		
		return response;
	}
	public JSONObject executeCmdAsync(String host, String cmd) {
		List<String> hosts = new ArrayList<String>();
		hosts.add(host);
		
		return executeCmdAsync(hosts, cmd);
	}
	
	
	public JSONObject executeCmdAsync(List<String> hosts, String cmd) {
		SaltRequest request = new SaltRequest();
		request.setClient(Constant.SALT_CLIENT.async.getName());
		request.setTgt(hosts);
		request.setFun(Constant.SALT_FUN.cmd.getName());
		request.setArg(cmd);
		
		log.debug(String.format("executeCmd, request: %s", request.toString()));
		
		JSONObject response = apiFactory.saltstack().call(request);
		
		log.debug(String.format("executeCmd, response: %s", response));
		
		this.insertSaltJob(request, response);
		log.debug("executeCmd, insert job");
		
		return response;
	}
	
	public JSONObject executeScript(List<String> hosts, String script, String param) {
		SaltRequest request = new SaltRequest();
		request.setClient(Constant.SALT_CLIENT.async.getName());
		request.setTgt(hosts);
		request.setFun(Constant.SALT_FUN.script.getName());
		request.setArg(String.format("salt://%s", script));
		request.setParam(param);
		
		log.debug(String.format("executeScript, request: %s", request.toString()));
		
		JSONObject response = apiFactory.saltstack().call(request);
		
		log.debug(String.format("executeScript, response: %s", response));
		
		this.insertSaltJob(request, response);
		log.debug("executeScript, insert job");
		
		return response;
	}
	
	public Boolean isServiceAlive(String appId, String hostId) {
		AppVO app = appDao.queryById(appId);
		
		String cmd = null;
		String template = null;
		
		switch (app.getType()) {
			case "mysql":
				cmd = "/etc/init.d/mysql status";
				template = "MySQL running";
				break;
			case "oracle":
				break;
			case "tomcat":
				break;
			case "weblogic":
				break;
		}
		
		if(cmd == null) {
			return false;
		}
		
		JSONObject response = executeCmd(hostId, cmd);
		if(response != null && !response.getJSONArray("return").isEmpty()) {
			JSONObject value = response.getJSONArray("return").getJSONObject(0);
			String result = value.getString(hostId);
			
			if(result != null && result.indexOf(template) != -1) {
				return true;
			} else {
				return false;
			}
		}
		
		return false;
	}
	
	public List<SaltJobVO> querySaltJob(SaltJobVO vo, Pagination pagination) {
		return deployDao.queryAll(vo, pagination);
	}
	
	public SaltJobVO querySaltJobById(String id) {
		return deployDao.queryById(id);
	}
	
	public SaltJobVO queryByJobIdAndHostName(String jobId, String hostName) {
		SaltJobVO vo = new SaltJobVO();
		vo.setJobId(jobId);
		vo.setHostName(hostName);
		
		return deployDao.queryByJobIdAndHostName(vo);
	}
	
	public List<SaltJobVO> queryNewJob() {
		return deployDao.queryNewJob();
	}
	
	public int updateJob(SaltJobVO vo) {
		return deployDao.updateJob(vo);
	}
	
	public JSONObject callJobById(String jobId) {
		return apiFactory.saltstack().job(jobId);
	}
	
	public void insertSaltJob(SaltRequest request, JSONObject response) {
		if(response != null && response.containsKey("return")) {
			JSONObject result = response.getJSONArray("return").getJSONObject(0);
			
			if(result != null && !result.isEmpty()) {
				JSONArray hosts = result.getJSONArray("minions");
				
				for(int i=0; i<hosts.size(); i++) {
					SaltJobVO vo = new SaltJobVO();
					vo.setJobId(result.getString("jid"));
					vo.setHostName(hosts.getString(i));
					vo.setFun(request.getFun());
					vo.setArg(request.getArg());
					vo.setParam(request.getParam());
					
					deployDao.insertJob(vo);
				}
			}
		}
	}
	
	@Scheduled(cron="0 0/10 * * * ?")
	public void executeQuartzJob() {
		log.debug("quartz job starting......");
		
		List<SaltJobVO> newJobs = this.queryNewJob();
		if(newJobs != null && newJobs.size() > 0)  {
			log.debug(String.format("find %s new jobs", newJobs.size()));
			
			int count = 0;
			for(SaltJobVO aJob : newJobs) {
				if(aJob.getArg() == null || aJob.getArg().trim().length() == 0) {
					log.error(String.format("Job(%s) failed, arg is NULL", aJob.getJobId()));
					
					aJob.setStatus(Constant.JOB_STATUS.fail.name());
					this.updateJob(aJob);
					return;
				}
				
				String fun = aJob.getFun();
				JSONObject job = apiFactory.saltstackWithoutSession().job(aJob.getJobId());
				
				log.debug(String.format("call saltstack job response: %s", job));
				
				SaltJobVO vo = new SaltJobVO();
				vo.setJobId(aJob.getJobId());
				
				if(job != null && job.containsKey("info")) {
					JSONArray jobReturn = job.getJSONArray("info");
					
					if(jobReturn != null && !jobReturn.isEmpty() && !jobReturn.getJSONObject(0).isEmpty()) {
						JSONArray hosts = jobReturn.getJSONObject(0).getJSONArray("Minions");
						
						JSONObject result = jobReturn.getJSONObject(0).getJSONObject("Result");
						if(hosts != null && !hosts.isEmpty() && result != null && !result.isEmpty()) {
							for(int i=0; i<hosts.size(); i++) {
								String host = hosts.getString(i);
								String status = Constant.JOB_STATUS.success.name();
								
								if(result.containsKey(host)) {
									JSONObject modules =null;
									if(Constant.SALT_FUN.cmd.getName().equals(fun)){
										modules= result.getJSONObject(host);
									}else{
										modules= result.getJSONObject(host).getJSONObject("return");
									}
									
									
									if(Constant.SALT_FUN.state_high.getName().equals(fun) 
											|| Constant.SALT_FUN.state_sls.getName().equals(fun)) {
										
										if(modules != null && !modules.isEmpty()) {
											for(String moduleKey : modules.keySet()) {
												JSONObject moduleValue = modules.getJSONObject(moduleKey);
												
												if(!moduleValue.getBoolean("result")) {
													status = Constant.JOB_STATUS.fail.name();
													break;
												}
											}
										}
									} else if (Constant.SALT_FUN.script.getName().equals(fun)) {
										if(modules.getIntValue("retcode") != 0) {
											status = Constant.JOB_STATUS.fail.name();
										}
									} else if(Constant.SALT_FUN.cmd.getName().equals(fun)){
										String ret=modules.toJSONString();
										if(ret.contains("Result: False")){
											status = Constant.JOB_STATUS.fail.name();
										}
									}else {
										log.error(String.format("Job(%s) failed, unsupport saltstack fun: %s", aJob.getJobId(), fun)); 
										status = Constant.JOB_STATUS.fail.name();
									}
									
									vo.setHostName(host);
									
									SaltJobVO saltJob = this.queryByJobIdAndHostName(aJob.getJobId(), host);
									if(saltJob != null) {
										saltJob.setReturnValue(modules.toJSONString());
										saltJob.setStatus(status);
										this.updateJob(saltJob);
										
										count++;
									}
								}
							}
						}
					}
				}
			}
			
			log.debug(String.format("update %s job result", count));
		} else {
			log.debug("find 0 new jobs");
		}
		
		log.debug("quartz job stopped.");
	}
	
}
