
package com.supermap.mytracks.bean;

import java.util.List;

public class Maps{
   	private List<Content> content;
   	private int currentPage;
   	private int pageSize;
   	private int total;
   	private int totalPage;

 	public List<Content> getContent(){
		return this.content;
	}
	public void setContent(List<Content> content){
		this.content = content;
	}
 	public int getCurrentPage(){
		return this.currentPage;
	}
	public void setCurrentPage(int currentPage){
		this.currentPage = currentPage;
	}
 	public int getPageSize(){
		return this.pageSize;
	}
	public void setPageSize(int pageSize){
		this.pageSize = pageSize;
	}
 	public int getTotal(){
		return this.total;
	}
	public void setTotal(int total){
		this.total = total;
	}
 	public int getTotalPage(){
		return this.totalPage;
	}
	public void setTotalPage(int totalPage){
		this.totalPage = totalPage;
	}
}
