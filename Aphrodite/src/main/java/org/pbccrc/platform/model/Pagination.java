package org.pbccrc.platform.model;

import java.util.List;

public class Pagination {
	
	private int currentPage;
	private int pageSize;
	private long totalCount;
	private List<?> result;
	
	public Pagination() {
		super();
	}
	
	public Pagination(int currentPage, int pageSize) {
		super();
		this.currentPage = currentPage;
		this.pageSize = pageSize;
	}
	
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public long getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
	public List<?> getResult() {
		return result;
	}

	public void setResult(List<?> result) {
		this.result = result;
	}

	public int getOffset() {
		return (currentPage - 1) * pageSize ;
	}

	@Override
	public String toString() {
		return "Pagination [currentPage=" + currentPage + ", pageSize="
				+ pageSize + ", totalCount=" + totalCount + ", result="
				+ result + "]";
	}

}
