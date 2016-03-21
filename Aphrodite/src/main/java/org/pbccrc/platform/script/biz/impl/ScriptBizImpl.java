package org.pbccrc.platform.script.biz.impl;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.pbccrc.platform.api.saltstack.SaltRequest;
import org.pbccrc.platform.cmdb.dao.ScriptDao;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.script.biz.IScriptBiz;
import org.pbccrc.platform.util.Constant;
import org.pbccrc.platform.util.FTPClientUtil;
import org.pbccrc.platform.util.FileUtils;
import org.pbccrc.platform.vo.ScriptVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

@Service
public class ScriptBizImpl implements IScriptBiz {

	private static final Logger log = Logger.getLogger(ScriptBizImpl.class);

	@Autowired
	private ScriptDao scriptDao;

	@Autowired
	FTPClientUtil ftpClientUtil;

	public String getScriptSuffix(ScriptVO vo){
		String suffix="";
		if(vo.getType().equals("shell")){
			suffix=Constant.SCRIPT_TYPE.shell_sh.getName();
		}else if(vo.getType().equals("python")){
			suffix=Constant.SCRIPT_TYPE.python_py.getName();
		}
		return suffix;
	}
	@Override
	public int updateShellScript(ScriptVO vo) {
		
		delFtpScript(vo.getId().toString());
		
		createAndUplodScript(vo);
		
		vo.setPath("scripts/admin/" + vo.getName() +this.getScriptSuffix(vo));
		
		scriptDao.updateScript(vo);
		
		return 0;
	}

	@Override
	public void insertShellScript(SaltRequest request, JSONObject response) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<ScriptVO> queryScripts(ScriptVO vo,Pagination pagination) {
		return scriptDao.queryAll(vo,pagination);
	}

	@Override
	public void deleteScript(String scriptIds) {
		if (scriptIds != null) {
			for (String id : scriptIds.split(",")) {
				if (id != null && id.trim().length() > 0) {

					delFtpScript(id);
					scriptDao.deleteScript(id);

				}
			}
		}

	}

	@Override
	public void addScript(JSONObject script) {
		String suffix =""; 
		ScriptVO vo = new ScriptVO();
		vo.setName(script.getString("name"));
		vo.setDescription(script.getString("description"));
		vo.setContent(script.getString("content"));
		vo.setType(script.getString("type"));
		
		suffix=this.getScriptSuffix(vo);
		
		vo.setPath("scripts/admin/" + script.getString("name") + suffix);
		vo.setStatus(0);

		scriptDao.insertScript(vo);
		createAndUplodScript(vo);
	}

	public void createAndUplodScript(ScriptVO vo) {
		
		boolean uploadflag = false;
		String suffix =this.getScriptSuffix(vo);
		String propath = this.getProPath();
		String upload = propath + Constant.SCRIPT_FILEPATH.ftp_localpath.getName();

		String filepath = upload + "admin/" + vo.getName() + suffix;
		try {
			File uploadfile= this.createScriptFile(upload + "admin", vo.getName() + suffix,
					vo.getContent());
			
				uploadflag = uploadScriptFile(filepath);

				if (uploadflag == false) {
					log.debug(" upload script file fail");
				}
				uploadfile.delete();

		} catch (Exception e) {
			throw new RuntimeException(
					"create script file or upload script file exception: " + e);
		}
	}

	@Override
	public File createScriptFile(String filedir, String filename,
			String fileContent) throws Exception {

		File filedirs = new File(filedir);
		if (!filedirs.exists() && !filedirs.isDirectory()) {
			filedirs.mkdirs();
		}
		File file = new File(filedir + "/" + filename);
		/*if (file.exists()) {
			log.debug("The new file " + file.getAbsolutePath()
					+ " already exists!");
			file.delete();
			file.createNewFile();
		} else {*/
			FileUtils.writeFile(fileContent, file);
		
			log.debug("create script file " + file.getAbsolutePath()
					+ " success");
		//}

		return file;
	}

	@Override
	public boolean uploadScriptFile(String filepath) {

		boolean flag = false;

		log.debug("upload file begin...");
		try {
			ftpClientUtil.uploadFile(Constant.SCRIPT_FILEPATH.ftp_path.getName() + "admin",
					new File(filepath));
			flag = true;
		} catch (Exception e) {

			e.printStackTrace();
		}
		log.debug("upload file end...");
		return flag;
	}

	public void delFtpScript(String id) {

		log.debug("delete ftp scirpt begin ...");

		ScriptVO vo = scriptDao.queryById(id);

		try {
			ftpClientUtil.delRemoteFile(Constant.SCRIPT_FILEPATH.ftp_path.getName() + "admin",
					vo.getName() + this.getScriptSuffix(vo));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("delete ftp script file exception: " + e);
		}
		log.debug("delete ftp scirpt end ...");
	}

	@Override
	public ScriptVO queryScriptById(String id) {
		
		return scriptDao.queryById(id);
	}
	@Override
	public String getProPath() {
		String curpath = this.getClass().getClassLoader().getResource("/")
				.getPath();
		String propath = curpath.substring(0, curpath.indexOf("WEB-INF"));
		return propath;
	}
	@Override
	public boolean dealUploadScriptFile(JSONObject obj, File uploadfile) {

		String filecontent=FileUtils.readToString(uploadfile);
		obj.put("name", uploadfile.getName().substring(0, uploadfile.getName().indexOf(".")));
		
		if(!"".equals(filecontent)&&filecontent!=null){
			log.debug("upload script file "+uploadfile.getName()+" content is "+filecontent);
			
			obj.put("content", filecontent);
			
		}
		this.addScript(obj);
		
		uploadfile.delete();
		
		return true;
	}
	@Override
	public File downloadScript(ScriptVO vo)  {
		log.debug("create download script begin ...");
		
		String suffix =this.getScriptSuffix(vo);
		String propath = this.getProPath();
		String downpath = propath + Constant.SCRIPT_FILEPATH.download_localptah.getName();
		System.out.println(downpath);
		File downloadfile=null;
		try {
			 downloadfile = this.createScriptFile(downpath + "admin", vo.getName() + suffix,
					vo.getContent());
		} catch (Exception e) {
			throw new RuntimeException(
					"create script file exception: " + e);
		}
		log.debug("create download script end ...");
		return downloadfile;
	}

}