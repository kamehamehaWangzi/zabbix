package org.pbccrc.platform.api.saltstack;

import java.util.List;

import org.pbccrc.platform.util.Constant;

public class SaltRequest {
	
	private String client;
	private String exprForm = "list";
	private List<String> tgt;
	private String fun;
	private String arg;
	private String param;
	
	public void setClient(String client) {
		this.client = client;
	}
	public String getClient() {
		if(client == null || client.trim().length() == 0) {
			client = Constant.SALT_CLIENT.sync.getName();
		}
		return client;
	}
	public String getExprForm() {
		return exprForm;
	}
	public void setExprForm(String exprForm) {
		this.exprForm = exprForm;
	}
	public List<String> getTgt() {
		return tgt;
	}
	public void setTgt(List<String> tgt) {
		this.tgt = tgt;
	}
	public String getFun() {
		return fun;
	}
	public void setFun(String fun) {
		this.fun = fun;
	}
	public String getArg() {
		return arg;
	}
	public void setArg(String arg) {
		this.arg = arg;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	
	@Override
	public String toString() {
		return "SaltRequest [client=" + client + ", exprForm=" + exprForm
				+ ", tgt=" + tgt + ", fun=" + fun + ", arg=" + arg + ", param="
				+ param + "]";
	}
	
}
