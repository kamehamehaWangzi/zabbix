package org.pbccrc.platform.script.biz;

import java.io.File;
import java.util.List;

import org.pbccrc.platform.api.saltstack.SaltRequest;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.vo.ScriptVO;

import com.alibaba.fastjson.JSONObject;

public interface IScriptBiz {
	

	List<ScriptVO> queryScripts(ScriptVO vo,Pagination pagination);
	int updateShellScript(ScriptVO vo);
	void insertShellScript(SaltRequest request, JSONObject response);
	public void deleteScript(String scriptIds);
	void addScript(JSONObject script);
	public File createScriptFile(String filepath,String filename,String fileContent) throws Exception;
	public boolean uploadScriptFile(String filepath);
	public void createAndUplodScript(ScriptVO vo);
	public ScriptVO queryScriptById(String id);
	public String getProPath();
	public boolean dealUploadScriptFile(JSONObject obj,File uploadfile);
	
	public File downloadScript(ScriptVO vo);
}
