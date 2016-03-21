package org.pbccrc.platform.script.rest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.pbccrc.platform.deploy.biz.IDeployBiz;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.monitor.biz.IMachineBiz;
import org.pbccrc.platform.script.biz.IScriptBiz;
import org.pbccrc.platform.util.Constant;
import org.pbccrc.platform.util.FileUtils;
import org.pbccrc.platform.vo.SaltJobVO;
import org.pbccrc.platform.vo.ScriptVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;

@Path("/script")
public class ScriptRest {

	private static final Logger log = Logger.getLogger(ScriptRest.class);
	/*
	 * public ScriptRest(){ register(MultiPartFeature.class); }
	 */

	@Autowired
	@Qualifier("deployBizImpl")
	private IDeployBiz deployBiz;

	@Autowired
	@Qualifier("scriptBizImpl")
	private IScriptBiz scriptBiz;

	@Autowired
	@Qualifier("machineBizImpl")
	private IMachineBiz machineBiz;

	@Path("/list")
	@GET
	public Response listScript(@QueryParam("param") String param,@QueryParam("currentPage") int currentPage, @QueryParam("pageSize") int pageSize) {
		ScriptVO vo = new ScriptVO();
		if (param != null && param.trim().length() > 0) {
			vo.setName(param.trim());
		}
		Pagination pagination = new Pagination();
		pagination.setCurrentPage(currentPage);
		pagination.setPageSize(pageSize);
		
		List<ScriptVO> vos = scriptBiz.queryScripts(vo,pagination);
		pagination.setTotalCount(((Page<ScriptVO>) vos).getTotal());
		pagination.setResult(vos);
		return Response.ok(pagination).build();
	}

	@Path("/service")
	@POST
	public Response serviceStatus(@QueryParam("hostIds") String ids,
			@QueryParam("scriptpath") String scriptpath) {

		List<String> hosts = machineBiz.queryHostsByIds(ids);

		log.debug(String.format("execute shellscript %s, on %s", scriptpath,
				hosts));

		JSONObject result = deployBiz.executeScript(hosts, scriptpath, "");

		if (result != null && !result.getJSONArray("return").isEmpty()) {
			if (!result.getJSONArray("return").getJSONObject(0).isEmpty())
				return Response.ok(200).build();
		}

		return Response.ok(result).build();
	}

	@Path("/delScript")
	@PUT
	public Response removeScript(@QueryParam("scriptIds") String scriptIds) {
		scriptBiz.deleteScript(scriptIds);

		return Response.ok(200).build();
	}

	@Path("/add")
	@POST
	public Response addScript(@QueryParam("script") String script) {

		JSONObject ScriptInfo = JSON.parseObject(script);

		scriptBiz.addScript(ScriptInfo);

		return Response.ok(200).build();
	}

	@Path("/list/{id}/info")
	@GET
	public Response getScriptInfo(@PathParam("id") String id) {
		ScriptVO vo = scriptBiz.queryScriptById(id);
		if (vo != null) {
			return Response.ok(vo).build();
		}

		return Response.noContent().build();
	}

	@Path("/modify")
	@PUT
	public Response modifyScript(@QueryParam("scripts") String scripts) {
		JSONObject scriptInfo = JSON.parseObject(scripts);
		if (scriptInfo != null) {
			ScriptVO vo = new ScriptVO();
			vo.setId(scriptInfo.getInteger("id"));
			vo.setPath(scriptInfo.getString("path"));
			vo.setDescription(scriptInfo.getString("description"));
			vo.setContent(scriptInfo.getString("content"));
			vo.setName(scriptInfo.getString("name"));
			vo.setType(scriptInfo.getString("type"));

			scriptBiz.updateShellScript(vo);
			return Response.ok(200).build();
		}

		return Response.noContent().build();
	}

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadScript(FormDataMultiPart form,
			@Context HttpServletResponse response,
			@QueryParam("scripts") String scripts)
			throws UnsupportedEncodingException {
		
		log.debug("upload script file begin ...");
		
		JSONObject scriptInfo = JSON.parseObject(scripts);

		FormDataBodyPart filePart = form.getField("file");

		/*
		 * FormDataBodyPart desc = form.getField("description");
		 * desc.getValue(String.class)
		 */

		InputStream fileInputStream = filePart.getValueAs(InputStream.class);

		FormDataContentDisposition formDataContentDisposition = filePart
				.getFormDataContentDisposition();

		String source = formDataContentDisposition.getFileName();
		String fileName = new String(source.getBytes("ISO8859-1"),
				Constant.SHELLFILE_ENCODE);

		String filedir = scriptBiz.getProPath()
				+ Constant.SCRIPT_FILEPATH.upload_localpath.getName()
				+ "admin/";
		File filedirs = new File(filedir);
		if (!filedirs.exists() && !filedirs.isDirectory()) {
			filedirs.mkdirs();
		}
		String filePath = filedir + fileName;
		File file = new File(filePath);

		try {

		FileUtils.saveFile(fileInputStream, file);

		scriptBiz.dealUploadScriptFile(scriptInfo, file);

		} catch (Exception ex) {
			log.debug(ex.getMessage());
		}
		log.debug("upload script file end ...");
		
		return Response.ok(200).build();
	}

	@GET
	@Path("/download")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response download(@QueryParam("id") String id,
			@Context HttpServletResponse response)
			throws UnsupportedEncodingException {

		if (!"".equals(id) && id != null) {
			log.debug("download script file begin ...");
			
			ScriptVO vo = scriptBiz.queryScriptById(id);

			File downloadfile = null;

			downloadfile = scriptBiz.downloadScript(vo);

			if (!downloadfile.exists()) {
				log.debug(" create download script file "+vo.getName()+" fail...");
				throw new WebApplicationException(404);
			}
			String mt = new MimetypesFileTypeMap().getContentType(downloadfile);

			ResponseBuilder builder = Response.ok(downloadfile, mt);
			builder.header("Content-disposition", "attachment;filename="
					+ URLEncoder.encode(downloadfile.getName(), "UTF-8"));
			builder.header("ragma", "No-cache").header("Cache-Control",
					"no-cache");
			builder.encoding("utf-8");
			// downloadfile.delete();
			log.debug("download script file end ...");
			
			return builder.build();
		}

		return Response.noContent().build();

	}
}
