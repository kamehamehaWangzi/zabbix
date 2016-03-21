package org.pbccrc.platform.script.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.script.biz.IScriptTmpBiz;
import org.pbccrc.platform.vo.ScriptTmpVO;
import org.pbccrc.platform.vo.ScriptVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;

@Path("/script_template")
public class ScriptTmpRest {
	private static final Logger log = Logger.getLogger(ScriptTmpRest.class);

	@Autowired
	@Qualifier("scriptTmpBizImpl")
	private IScriptTmpBiz templateBiz;

	@Path("/list")
	@GET
	public Response listTemplate(@QueryParam("param") String param,@QueryParam("currentPage") int currentPage, @QueryParam("pageSize") int pageSize) {
		ScriptTmpVO vo = new ScriptTmpVO();
		if (param != null && param.trim().length() > 0) {
			vo.setName(param.trim());
		}
		Pagination pagination = new Pagination();
		pagination.setCurrentPage(currentPage);
		pagination.setPageSize(pageSize);
		
		List<ScriptTmpVO> vos = templateBiz.queryScriptTmpsPage(vo,pagination);
		pagination.setTotalCount(((Page<ScriptTmpVO>) vos).getTotal());
		pagination.setResult(vos);
		return Response.ok(pagination).build();
	}

	@Path("/delTmplate")
	@PUT
	public Response removeTemplate(@QueryParam("tmplateIds") String tmplateIds) {
		templateBiz.deleteTemplate(tmplateIds);

		return Response.ok(200).build();
	}

	@Path("/add")
	@POST
	public Response addScript(@QueryParam("tmps") String tmp) {

		JSONObject tmpInfo = JSON.parseObject(tmp);

		ScriptTmpVO vo = new ScriptTmpVO();
		if (tmpInfo.getString("name") != null
				&& !"".equals(tmpInfo.getString("name"))) {
			vo.setName(tmpInfo.getString("name"));

			List<ScriptTmpVO> vos = templateBiz.queryScriptTmps(vo);

			if (vos.size() > 0 && vos != null) {
				return Response.ok(10).build();
			}
		}
		templateBiz.addTmp(tmpInfo);

		return Response.ok(200).build();
	}

	@Path("/info")
	@GET
	public Response queryInfo(@QueryParam("id") String id) {

		ScriptTmpVO tmp = templateBiz.queryTmpInfo(id);
		return Response.ok(tmp).build();
	}

	@Path("/modify")
	@PUT
	public Response modTemplate(@QueryParam("tmps") String tmps) {
		JSONObject tmpInfo = JSON.parseObject(tmps);

		ScriptTmpVO vo = new ScriptTmpVO();
		if (tmpInfo.getString("name") != null
				&& !"".equals(tmpInfo.getString("name"))) {
			vo.setName(tmpInfo.getString("name"));
			vo.setId(tmpInfo.getInteger("id"));

			List<ScriptTmpVO> vos = templateBiz.queryScriptTmps(vo);

			if (vos.size() > 0 && vos != null) {
				for (ScriptTmpVO tmpvo : vos) {
					if (tmpvo.getId() != vo.getId()) {
						return Response.ok(10).build();

					}
				}

			}
		}

		templateBiz.updateTmp(tmpInfo);

		return Response.ok(200).build();
	}

	@Path("/variable")
	@GET
	public Response organizeVariable(@QueryParam("id") String id) {

		ScriptTmpVO tmp = templateBiz.splitVariable(id);

		return Response.ok(tmp).build();
	}
	
	@Path("/generate")
	@PUT
	public Response generateScript(@QueryParam("tmp") String tmp) {
		JSONObject tmpInfo = JSON.parseObject(tmp);
		
		templateBiz.createScript(tmpInfo);
		
		return Response.ok(200).build();
	}
}