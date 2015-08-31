
package com.supermap.mytracks.bean;

import java.io.Serializable;


public class AuthorizeSetting implements Serializable{
    private static final long serialVersionUID = -402352286937175351L;
    private String aliasName;
    private int entityId;
   	private String entityName;
   	private String entityType;
   	private String permissionType;

 	public String getEntityName(){
		return this.entityName;
	}
	public void setEntityName(String entityName){
		this.entityName = entityName;
	}
 	public String getEntityType(){
		return this.entityType;
	}
	public void setEntityType(String entityType){
		this.entityType = entityType;
	}
 	public String getPermissionType(){
		return this.permissionType;
	}
	public void setPermissionType(String permissionType){
		this.permissionType = permissionType;
	}
    public int getEntityId() {
        return entityId;
    }
    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }
    public String getAliasName() {
        return aliasName;
    }
    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }
	
}
