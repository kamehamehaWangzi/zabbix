package org.pbccrc.platform.script.biz;

import java.util.List;

import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.vo.ScriptTmpVO;

import com.alibaba.fastjson.JSONObject;


public interface IScriptTmpBiz {
	

	List<ScriptTmpVO> queryScriptTmpsPage(ScriptTmpVO vo,Pagination pagination);
	List<ScriptTmpVO> queryScriptTmps(ScriptTmpVO vo);
	public void deleteTemplate(String tmplateIds);
	public void addTmp(JSONObject tmp);
	public ScriptTmpVO queryTmpInfo(String id);
	public void updateTmp(JSONObject tmp);
	public ScriptTmpVO splitVariable(String id);
	public void createScript(JSONObject tmp);
}
