package org.pbccrc.platform.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.pbccrc.platform.api.cobbler.CobblerApi;
import org.pbccrc.platform.api.ealsticsearch.ElasticSearchApi;
import org.pbccrc.platform.api.saltstack.SaltstackApi;
import org.pbccrc.platform.api.zabbix.ZabbixApi;
import org.pbccrc.platform.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApiFactory {
	
	private static final Logger log = Logger.getLogger(ApiFactory.class);
	
	@Autowired
    private HttpServletRequest request;
	
	@Autowired
	private ZabbixApi zabbixApi;
	
	@Autowired
	private SaltstackApi saltstackApi;
	
	@Autowired
	private ElasticSearchApi elasticSearchApi;
	
	@Autowired
	private CobblerApi cobblerApi;

	
	public ZabbixApi zabbix() {
		zabbixApi.init();
		
		HttpSession session = request.getSession();
		String auth = null;
		
		if(session.getAttribute("ZABBIX_AUTH") == null) {
			log.debug("ZABBIX_AUTH not in session, create...");
			
			auth = zabbixApi.auth(Constant.ZABBIX_USERNAME, Constant.ZABBIX_PASSWORD);
			session.setAttribute("ZABBIX_AUTH", auth);
			
			log.debug(String.format("ZABBIX_AUTH saved to session, '%s'", auth));
		} else {
			auth = String.valueOf(session.getAttribute("ZABBIX_AUTH"));
			
			log.debug(String.format("ZABBIX_AUTH find in session, '%s'", auth));
		}
		
		zabbixApi.setAuth(auth);
		
		return zabbixApi;
	}
	
	public SaltstackApi saltstack() {
		saltstackApi.init();
		
		HttpSession session = request.getSession();
		String token = null;
		
		if(session.getAttribute("SALTSTACK_TOKEN") == null) {
			log.debug("SALTSTACK_TOKEN not in session, create...");
			
			token = saltstackApi.auth(Constant.SALTSTACK_USERNAME, Constant.SALTSTACK_PASSWORD);
			session.setAttribute("SALTSTACK_TOKEN", token);
			
			log.debug(String.format("SALTSTACK_TOKEN saved to session, '%s'", token));
		} else {
			token = String.valueOf(session.getAttribute("SALTSTACK_TOKEN"));
			
			log.debug(String.format("SALTSTACK_TOKEN find in session, '%s'", token));
		}
		
		saltstackApi.setToken(token);
		
		return saltstackApi;
	}
	
	public SaltstackApi saltstackWithoutSession() {
		saltstackApi.init();
		
		log.debug("SALTSTACK_TOKEN created for quartz job.");
		
		String token = saltstackApi.getToken();
		if(token == null || token.trim().length() == 0) {
			token = saltstackApi.auth(Constant.SALTSTACK_USERNAME, Constant.SALTSTACK_PASSWORD);
		}
		
		saltstackApi.setToken(token);
		
		return saltstackApi;
	}
	
	public ElasticSearchApi elk() {
		return elasticSearchApi;
	}
	
	
	public CobblerApi cobbler() throws XmlRpcException {
		cobblerApi.init();
		
		HttpSession session = request.getSession();
		String token = null;
		boolean login=false;
		
		if(session.getAttribute("COBBLER_TOKEN") == null) {
			log.debug("COBBLER_TOKEN not in session, create...");
			login=cobblerApi.login(Constant.COBBLER_USERNAME, Constant.COBBLER_PASSWORD);
			if(login){
				token=cobblerApi.getToken();
			}
			session.setAttribute("COBBLER_TOKEN", token);
			
			log.debug(String.format("COBBLER_TOKEN saved to session, '%s'", token));
		} else {
			token = String.valueOf(session.getAttribute("COBBLER_TOKEN"));
			
			log.debug(String.format("COBBLER_TOKEN find in session, '%s'", token));
		}
		
		cobblerApi.setToken(token);
		
		return cobblerApi;
	}
}
