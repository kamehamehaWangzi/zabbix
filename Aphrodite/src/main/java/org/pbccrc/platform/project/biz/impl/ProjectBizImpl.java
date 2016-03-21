package org.pbccrc.platform.project.biz.impl;

import java.util.ArrayList;
import java.util.List;

import org.pbccrc.platform.cmdb.dao.HostDao;
import org.pbccrc.platform.cmdb.dao.ProjectDao;
import org.pbccrc.platform.cmdb.dao.TaskDao;
import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.project.biz.IProjectBiz;
import org.pbccrc.platform.util.StringUtils;
import org.pbccrc.platform.vo.HostVO;
import org.pbccrc.platform.vo.ProjectVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Service
public class ProjectBizImpl implements IProjectBiz{
	
	@Autowired
	private HostDao hostDao;
	
	@Autowired
	private ProjectDao projectDao;
	
	@Autowired
	private TaskDao taskDao;

	public List<HostVO> queryAllHost() {
		
		return hostDao.queryHosts();
	}
	
	public List<HostVO> queryAllHost(String projectId) {
		
		List<HostVO> list = new ArrayList<HostVO>();
		
		ProjectVO project = queryProjectById(projectId);
		
		if (null != project && !StringUtils.isNull(project.getHosts())) {
			
			String hosts = project.getHosts();
			
			JSONArray jsonArray = JSON.parseArray(hosts);
			String[] ids = new String[jsonArray.size()];
			for (int i = 0 ; i < jsonArray.size(); i++) {
				ids[i] = jsonArray.getString(i);
			}
			
			list = hostDao.queryHostsByIds(ids);
		}
		
		return list;
	}
	
	public List<ProjectVO> queryAllProject() {
		
		return projectDao.queryProjects();
	}
	
	public List<ProjectVO> queryProjects(ProjectVO vo, Pagination pagination) {
		return projectDao.queryAll(vo, pagination);
	}
	
	public void addProject(JSONObject project) {
		
		ProjectVO vo = new ProjectVO();
		JSONArray hostArray = project.getJSONArray("hosts");
		if (null != hostArray) {
			vo.setHosts(hostArray.toString());
		}
		vo.setName(project.getString("name"));
		vo.setDescription(project.getString("description"));
		
		projectDao.insertProject(vo);
	}
	
	public ProjectVO queryProjectById(String projectId){
		return projectDao.queryByProjectId(projectId);
	}
	
	public void deleteProject(String projectId) {
		ProjectVO vo = projectDao.queryByProjectId(projectId);
		if(vo != null) {
			projectDao.deleteProject(String.valueOf(vo.getId()));
			taskDao.deleteTaskByProject(String.valueOf(vo.getId()));
		}
	}

}
