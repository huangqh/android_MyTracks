package com.supermap.mytracks.bean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.supermap.mytracks.utils.CommonUtil;

/**
 * 访问地图列表URL，返回的json中content标签包含了所有地图详细的信息。
 */
public class Content{
   	private String authorizeSetting;
   	private String center;
   	private String centerString;
   	private String controls;
   	private String controlsString;
   	private long createTime;
   	private String description;
   	private int epsgCode;
   	private String extent;
   	private String extentString;
   	private int id;
   	private boolean isDefaultBottomMap;
    private List<Layer> layers;
   	private String level;
   	private String nickname;
   	private String sourceType;
   	private String status;
   	private List<String> tags;
   	private String thumbnail;
   	private String title;
   	private String units;
   	private long updateTime;
   	private String userName;
   	private int visitCount;

 	public String getAuthorizeSetting(){
		return this.authorizeSetting;
	}
	public void setAuthorizeSetting(String authorizeSetting){
		this.authorizeSetting = authorizeSetting;
	}
 	public String getCenter(){
		return this.center;
	}
	public void setCenter(String center){
		this.center = center;
	}
 	public String getCenterString(){
		return this.centerString;
	}
	public void setCenterString(String centerString){
		this.centerString = centerString;
	}
 	public String getControls(){
		return this.controls;
	}
	public void setControls(String controls){
		this.controls = controls;
	}
 	public String getControlsString(){
		return this.controlsString;
	}
	public void setControlsString(String controlsString){
		this.controlsString = controlsString;
	}
 	public String getCreateTime(){
		return CommonUtil.getDate(this.createTime) ;
	}
    public long getCreateTimeLong(){
        return this.createTime;
    }
	public void setCreateTime(long createTime){
		this.createTime = createTime;
	}
 	public String getDescription(){
		return this.description;
	}
	public void setDescription(String description){
		this.description = description;
	}
 	public int getEpsgCode(){
		return this.epsgCode;
	}
	public void setEpsgCode(int epsgCode){
		this.epsgCode = epsgCode;
	}
 	public String getExtent(){
		return this.extent;
	}
	public void setExtent(String extent){
		this.extent = extent;
	}
 	public String getExtentString(){
		return this.extentString;
	}
	public void setExtentString(String extentString){
		this.extentString = extentString;
	}
 	public int getId(){
		return this.id;
	}
	public void setId(int id){
		this.id = id;
	}
 	public boolean getIsDefaultBottomMap(){
		return this.isDefaultBottomMap;
	}
	public void setIsDefaultBottomMap(boolean isDefaultBottomMap){
		this.isDefaultBottomMap = isDefaultBottomMap;
	}

    public List<Layer> getLayers() {
        return this.layers;
    }

    public void setLayers(List<Layer> layers) {
        this.layers = layers;
    }

    public String getLevel(){
		return this.level;
	}

    public void setLevel(String level) {
        this.level = level;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getSourceType(){
		return this.sourceType;
	}
	public void setSourceType(String sourceType){
		this.sourceType = sourceType;
	}
 	public String getStatus(){
		return this.status;
	}
	public void setStatus(String status){
		this.status = status;
	}
 	public List getTags(){
		return this.tags;
	}
	public void setTags(List tags){
		this.tags = tags;
	}
 	public String getThumbnail(){
		return this.thumbnail;
	}
	public void setThumbnail(String thumbnail){
		this.thumbnail = thumbnail;
	}
 	public String getTitle(){
		return this.title;
	}
	public void setTitle(String title){
		this.title = title;
	}
 	public String getUnits(){
		return this.units;
	}
	public void setUnits(String units){
		this.units = units;
	}
 	public long getUpdateTimeLong(){
 	    return this.updateTime;
	}
    public String getUpdateTime(){
        return CommonUtil.getDate(this.updateTime);
    }
	public void setUpdateTime(long updateTime){
		this.updateTime = updateTime;
	}
 	public String getUserName(){
		return this.userName;
	}
	public void setUserName(String userName){
		this.userName = userName;
	}
 	public int getVisitCount(){
		return this.visitCount;
	}
	public void setVisitCount(int visitCount){
		this.visitCount = visitCount;
	}
}
