package com.supermap.mytracks.bean;
import java.util.List;

public class Map{
   	private List<AuthorizeSetting> authorizeSetting = null;
   	private Point2D center = null;
   	private List controls = null;
   	private long createTime = 0;
   	private String description = null;
   	private int epsgCode = 3857;
   	private Rectangle2D extent = null;
   	private String extentString = null;
   	private int id = -1;
   	private boolean isDefaultBottomMap;
   	private List<Layer> layers = null;
   	private int level = -1;
   	private String nickname;
   	private String sourceType = null;
   	private String status = null;
   	private List<String> tags = null;
   	private String thumbnail = null;
   	private String title = null;
   	private String units = null;
   	private long updateTime = 0;
   	private String userName = null;
   	private int visitCount = 0;
   	private double scale;
   	private double resolution;
 	public List<AuthorizeSetting> getAuthorizeSetting(){
		return this.authorizeSetting;
	}
	public void setAuthorizeSetting(List<AuthorizeSetting> authorizeSetting){
		this.authorizeSetting = authorizeSetting;
	}
 	public Point2D getCenter(){
		return this.center;
	}
	public void setCenter(Point2D center){
		this.center = center;
	}

 	public List getControls(){
		return this.controls;
	}
	public void setControls(List controls){
		this.controls = controls;
	}
 	public long getCreateTime(){
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
 	public Rectangle2D getExtent(){
		return this.extent;
	}
	public void setExtent(Rectangle2D extent){
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
 	public List<Layer> getLayers(){
		return this.layers;
	}
	public void setLayers(List<Layer> layers){
		this.layers = layers;
	}
 	public int getLevel(){
		return this.level;
	}
	public void setLevel(int level){
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
 	public List<String> getTags(){
		return this.tags;
	}
	public void setTags(List<String> tags){
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
 	public long getUpdateTime(){
		return this.updateTime;
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
    public double getScale() {
        return scale;
    }
    public void setScale(double scale) {
        this.scale = scale;
    }
    public double getResolution() {
        return resolution;
    }
    public void setResolution(double resolution) {
        this.resolution = resolution;
    }
	
}
