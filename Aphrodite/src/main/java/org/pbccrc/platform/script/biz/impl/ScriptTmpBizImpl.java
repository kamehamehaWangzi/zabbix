package org.pbccrc.platform.script.biz.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.pbccrc.platform.cmdb.dao.ScriptTmpDao;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.script.biz.IScriptBiz;
import org.pbccrc.platform.script.biz.IScriptTmpBiz;
import org.pbccrc.platform.util.Constant;
import org.pbccrc.platform.util.StringUtils;
import org.pbccrc.platform.vo.ScriptTmpVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Service
public class ScriptTmpBizImpl implements IScriptTmpBiz {

	private static final Logger log = Logger.getLogger(ScriptTmpBizImpl.class);

	@Autowired
	ScriptTmpDao scriptTmpDao;

	@Autowired
	@Qualifier("scriptBizImpl")
	private IScriptBiz scriptBiz;

	@Override
	public List<ScriptTmpVO> queryScriptTmpsPage(ScriptTmpVO vo,Pagination pagination) {

		return scriptTmpDao.queryAllPage(vo,pagination);
	}
	@Override
	public List<ScriptTmpVO> queryScriptTmps(ScriptTmpVO vo) {

		return scriptTmpDao.queryAll(vo);
	}

	@Override
	public void deleteTemplate(String tmplateIds) {
		if (tmplateIds != null) {
			for (String id : tmplateIds.split(",")) {
				if (id != null && id.trim().length() > 0) {

					scriptTmpDao.deleteTemplate(id);

				}
			}
		}

	}

	@Override
	public void addTmp(JSONObject tmp) {
		ScriptTmpVO vo = new ScriptTmpVO();
		vo.setName(tmp.getString("name"));
		vo.setDescription(tmp.getString("description"));
		vo.setContent(tmp.getString("content"));
		vo.setOs(tmp.getString("os"));
		vo.setType(tmp.getString("type"));
		vo.setStatus(0);
		scriptTmpDao.insertTemplate(vo);

	}

	@Override
	public ScriptTmpVO queryTmpInfo(String id) {
		return scriptTmpDao.queryTmpInfo(id);
	}

	@Override
	public void updateTmp(JSONObject tmp) {
		ScriptTmpVO vo = new ScriptTmpVO();
		vo.setId(tmp.getInteger("id"));
		vo.setName(tmp.getString("name"));
		vo.setDescription(tmp.getString("description"));
		vo.setContent(tmp.getString("content"));
		vo.setOs(tmp.getString("os"));
		vo.setType(tmp.getString("type"));

		scriptTmpDao.updateTemplate(vo);
	}

	public ScriptTmpVO splitVariable(String id) {
		ScriptTmpVO vo = scriptTmpDao.queryTmpInfo(id);

		List<String> params = new ArrayList<String>();

		if (!"".equals(vo) && vo.getContent() != null) {

			StringUtils.splitParams(params, Constant.SHELLTEMPLATE_PREFFIX,
					Constant.SHELLTEMPLATE_SUFFIX, vo.getContent());

			log.debug("shell template :" + vo.getName() + " contains variable "
					+ params.toString());
		}
		vo.setVariable(params);

		return vo;
	}

	@Override
	public void createScript(JSONObject tmp) {

		String content = "";
		JSONArray paramvals = null;
		StringBuilder preffix = new StringBuilder();
		StringBuilder suffix = new StringBuilder();

		for (int i = 0; i < Constant.SHELLTEMPLATE_PREFFIX.length(); i++) {
			preffix.append("\\").append(
					Constant.SHELLTEMPLATE_PREFFIX.charAt(i));
			suffix.append("\\").append(Constant.SHELLTEMPLATE_SUFFIX.charAt(i));
		}
		content = tmp.getString("content");

		if (tmp.containsKey("paramvals")) {
			paramvals = tmp.getJSONArray("paramvals");
		}

		if (paramvals != null && paramvals.size() > 0) {
			String param = "";
			for (int i = 0; i < paramvals.size(); i++) {
				param = paramvals.getString(i);
			
				content = content.replaceAll(
						preffix.toString()
								+ param.substring(0, param.indexOf("^^"))
								+ suffix.toString(),
						param.substring(param.indexOf("^^") + 2));
			}

		}
		log.debug("generate new shell script is: " + content);
		JSONObject script = new JSONObject();
		script.put("name", tmp.getString("name"));
		script.put("content", content);
		script.put("type", tmp.getString("type"));
		log.debug("generate shell script by template...");
		scriptBiz.addScript(script);

	}

}