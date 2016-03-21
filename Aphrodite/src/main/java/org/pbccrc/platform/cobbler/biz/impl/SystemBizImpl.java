package org.pbccrc.platform.cobbler.biz.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.pbccrc.platform.api.ApiFactory;
import org.pbccrc.platform.cobbler.biz.ISystemBiz;
import org.pbccrc.platform.deploy.biz.IDeployBiz;
import org.pbccrc.platform.util.Constant;
import org.pbccrc.platform.vo.CobblerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

@Service
public class SystemBizImpl implements ISystemBiz {
	
	private static final Logger log = Logger.getLogger(SystemBizImpl.class);

	@Autowired
	private ApiFactory apiFactory;

	@Autowired
	@Qualifier("deployBizImpl")
	private IDeployBiz deployBiz;
	
	@Override
	public List<CobblerVO> querySystem(String param) throws XmlRpcException {
		log.debug("query cobbler system...");
		List<CobblerVO> list=new ArrayList<CobblerVO>();
		String cobbler="";
		Object[] objs=null;
		HashMap<String, String> paramap=null;
		if(!"".equals(param)&&param!=null){
			paramap=new HashMap<String, String>();
			paramap.put("name", "*"+param+"*");
			//paramap.put("profile", "*"+param+"*");
			objs=apiFactory.cobbler().executeRetObj("find_system", new Object[]{paramap,"True"});
		}else{
			objs=apiFactory.cobbler().executeRetObj("get_systems", new Object[]{});
		}
		
		
		for(Object obj:objs){
			cobbler=obj.toString();
			
			log.debug("query cobbler systems is:"+cobbler);
			CobblerVO vo=(CobblerVO) this.setProperty(CobblerVO.class,cobbler);
			
			if(!"".equals(vo.getName())&&vo.getName()!=null){
				list.add(vo);
			}
			
		}
		
		return list;
	}
	

	
	public <T> Object setProperty(Class<T> clazz, String str){
		
		Field[] fields;
		Object obj=null;
		String temp="";
		
		try {
			fields=clazz.getDeclaredFields();
			obj=clazz.newInstance();
			
			for(Field field:fields){
				field.setAccessible(true);
				if(str.indexOf(field.getName())!=-1){
					temp=str;
					if(field.getName().equals("interfaces")){
						temp=temp.substring(temp.indexOf(field.getName())+field.getName().length()+2);
						temp=temp.substring(0, temp.indexOf("="));
					}else if(field.getName().equals("name")){
						temp=temp.substring(temp.lastIndexOf(field.getName())+field.getName().length()+1);
						temp=temp.substring(0, temp.indexOf(","));
					}else{
						temp=temp.substring(temp.indexOf(field.getName())+field.getName().length()+1);
						temp=temp.substring(0, temp.indexOf(","));

					}
					
					log.debug("field "+field.getName()+" value is "+temp);
					field.set(obj, temp);
				}
		
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return obj;
	}



	@Override
	public void deleteSystem(String ids) throws XmlRpcException {
		Object[] objs=null;
		HashMap<String, String> paramap=null;
		if (ids != null) {
			for (String id : ids.split(",")) {
				paramap=null;
				objs=null;
				if (id != null && id.trim().length() > 0) {
					paramap=new HashMap<String, String>();
					paramap.put("uid", id);
					objs=apiFactory.cobbler().executeRetObj("find_system",new Object[]{paramap});
					
					if(objs!=null&&objs.length>0){
						
						apiFactory.cobbler().execute("remove_system", new Object[]{objs[0],apiFactory.cobbler().getToken()});
						apiFactory.cobbler().sync();
						log.debug("delete system uid is "+id+" name is "+objs[0]);
					}
					
				}
			}
		}
	}



	@Override
	public List<String> loadProfiles() throws XmlRpcException {
		Object[] objs=null;
		List<String> profiles=null;
		HashMap<String, String> paramap=new HashMap<String, String>();
		paramap.put("name", "*");
		objs=apiFactory.cobbler().executeRetObj("find_profile",new Object[]{paramap});
		if(objs!=null&&objs.length>0){
			profiles=new ArrayList<String>();
			for(Object obj:objs){
				profiles.add((String) obj);
				log.debug("load cobbler profile "+obj);
			}
			
		}
		
		return profiles;
	}



	@Override
	public void addSystem(JSONObject systems) throws XmlRpcException {
		
		String system_id=null;
		
		system_id=apiFactory.cobbler().executeRetStr("new_system", new Object[]{apiFactory.cobbler().getToken()});
		log.debug("add cobbler system begin ,system_id "+system_id);
		this.dealSystem(systems,system_id);
		apiFactory.cobbler().execute("save_system", new Object[]{system_id,apiFactory.cobbler().getToken()});
		apiFactory.cobbler().sync();
		log.debug("add cobbler system end ,system_id "+system_id);
	}
	
	public void dealSystem(JSONObject systems,String system_id) throws XmlRpcException{
		HashMap<String, String> intermap=new HashMap<String, String>();
		if(systems.getString("name")!=null){
			apiFactory.cobbler().execute("modify_system", new Object[]{system_id,"name",systems.getString("name"),apiFactory.cobbler().getToken()});
		}
		if(systems.getString("profile")!=null){
		apiFactory.cobbler().execute("modify_system", new Object[]{system_id,"profile",systems.getString("profile"),apiFactory.cobbler().getToken()});
		}
		if(systems.getString("status")!=null){
		apiFactory.cobbler().execute("modify_system", new Object[]{system_id,"status",systems.getString("status"),apiFactory.cobbler().getToken()});
		}
		if(systems.getString("hostname")!=null){
		apiFactory.cobbler().execute("modify_system", new Object[]{system_id,"hostname",systems.getString("hostname"),apiFactory.cobbler().getToken()});
		}
		if(systems.getString("gateway")!=null){
		apiFactory.cobbler().execute("modify_system", new Object[]{system_id,"gateway",systems.getString("gateway"),apiFactory.cobbler().getToken()});
		}
		if(systems.getString("power_type")!=null){
		apiFactory.cobbler().execute("modify_system", new Object[]{system_id,"power_type",systems.getString("power_type"),apiFactory.cobbler().getToken()});
		}
		if(systems.getString("power_user")!=null){
		apiFactory.cobbler().execute("modify_system", new Object[]{system_id,"power_user",systems.getString("power_user"),apiFactory.cobbler().getToken()});
		}
		if(systems.getString("power_pass")!=null){
		apiFactory.cobbler().execute("modify_system", new Object[]{system_id,"power_pass",systems.getString("power_pass"),apiFactory.cobbler().getToken()});
		}
		if(systems.getString("power_address")!=null){
		apiFactory.cobbler().execute("modify_system", new Object[]{system_id,"power_address",systems.getString("power_address"),apiFactory.cobbler().getToken()});
		}
		if(systems.getString("mac_address")!=null){
		intermap.put("macaddress-"+systems.getString("interfaces"),systems.getString("mac_address"));
		}
		if(systems.getString("ip_address")!=null){
		intermap.put("ipaddress-"+systems.getString("interfaces"),systems.getString("ip_address"));
		}
		if(systems.getString("dns_name")!=null){
		intermap.put("dnsname-"+systems.getString("interfaces"),systems.getString("dns_name"));
		}
		
		apiFactory.cobbler().execute("modify_system", new Object[]{system_id,"modify_interface",intermap,apiFactory.cobbler().getToken()});
	}



	@Override
	public CobblerVO querySystemById(String id) throws XmlRpcException {
		CobblerVO vo=null;
		Object[] objs=null;
		HashMap<String, String> param =new HashMap<String, String>();
		param.put("uid",id);
		if(!"".equals(id)&&id!=null){
			objs=apiFactory.cobbler().executeRetObj("find_system",new Object[]{param,"True"});
			if(objs!=null&&objs.length>0){
				log.debug("find cobbler system by id "+id+"--"+objs[0].toString());
				vo=(CobblerVO) this.setProperty(CobblerVO.class,objs[0].toString());
			}
		}
		return vo;
	}



	@Override
	public void deploySystem(String ids) throws XmlRpcException {
		log.debug("deploy cobbler system begin ...");
		CobblerVO vo=null;
		Object[] objs=null;
		HashMap<String, String> param=null;
		String bootdev=null;
		String reboot=null;
		String pass=null;
		apiFactory.cobbler().sync();
		if (ids != null) {
			for (String id : ids.split(",")) {
				param=null;
				objs=null;
				bootdev=null;
				reboot=null;
				if (id != null && id.trim().length() > 0) {
					param=new HashMap<String, String>();
					param.put("uid", id);
					objs=apiFactory.cobbler().executeRetObj("find_system",new Object[]{param,"True"});
					
					if(objs!=null&&objs.length>0){
						vo=(CobblerVO) this.setProperty(CobblerVO.class,objs[0].toString());
						pass=vo.getPower_pass();
						pass=pass.replaceAll("\"", "\\\"");
						
						bootdev="ipmitool -H "+vo.getPower_address()+" -U "+vo.getPower_user()+" -P \""+pass+"\" chassis bootdev pxe";
						deployBiz.executeCmd(Constant.SALTSTACK_COBBLER, bootdev);
						reboot="ipmitool -H "+vo.getPower_address()+" -U "+vo.getPower_user()+" -P \""+pass+"\" chassis power reset";
						deployBiz.executeCmd(Constant.SALTSTACK_COBBLER, reboot);
					}
					
				}
			}
		}
		log.debug("deploy cobbler system end ...");
	}



	@Override
	public void installSaltMinion(String ids) throws XmlRpcException {
		
		log.debug("install salt-minion begin...");
		
		CobblerVO vo=null;
		Object[] objs=null;
		HashMap<String, String> param=null;
		
		if (ids != null) {
			for (String id : ids.split(",")) {
				param=null;
				objs=null;
			
				if (id != null && id.trim().length() > 0) {
					param=new HashMap<String, String>();
					param.put("uid", id);
					objs=apiFactory.cobbler().executeRetObj("find_system",new Object[]{param,"True"});
					
					if(objs!=null&&objs.length>0){
						vo=(CobblerVO) this.setProperty(CobblerVO.class,objs[0].toString());
						executeSaltCmd(vo.getIp_address(),vo.getHostname(),Constant.COBBLER_OS_USER,Constant.COBBLER_OS_PASSWORD);
					}
					
				}
			}
		}
		log.debug("install salt-minion end...");
	}
	
	public synchronized  void executeSaltCmd(String ip,String hostname,String user,String pwd){
		
		String cmd=null;
		String cmdinit=null;
		cmd=" if [ `grep -c "+hostname+" /etc/salt/roster` -eq 0 ]; then echo '"+hostname+":\n  host: "+ip+"\n  user: "+user+"\n  passwd: "+pwd+"'>>/etc/salt/roster; fi";
		log.debug("add salt-ssh roster :"+cmd);
		deployBiz.executeCmd(Constant.SALTSTACK_MASTER, cmd);
		cmdinit="salt-ssh -L "+hostname+" state.sls repo/yum/init";
		//deployBiz.executeCmd(Constant.SALTSTACK_MASTER, cmd);
		log.debug("add salt-ssh repo :"+cmdinit);
		cmd=cmdinit+" ; sed -i '6c nodename: "+hostname+"' /srv/salt/config/hosts.sls; salt-ssh -L "+hostname+" state.sls saltstack/minion/init";
		deployBiz.executeCmdAsync(Constant.SALTSTACK_MASTER, cmd);
		log.debug("install salt-minion by salt-ssh  :"+cmd);
	}
}