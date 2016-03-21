package org.pbccrc.platform.project.biz;

import java.util.List;

import org.pbccrc.platform.model.Pagination;
import org.pbccrc.platform.vo.HostVO;
import org.pbccrc.platform.vo.ProjectVO;

import com.alibaba.fastjson.JSONObject;

public interface IProjectBiz {
	
	List<HostVO> queryAllHost();
	
	List<HostVO> queryAllHost(String projectId);
	
	List<ProjectVO> queryAllProject();
	
	void addProject(JSONObject project);
	
	void deleteProject(String projectId);
	
	List<ProjectVO> queryProjects(ProjectVO vo, Pagination pagination);
	
	ProjectVO queryProjectById(String projectId);
}
