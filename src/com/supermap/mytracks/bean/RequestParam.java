package com.supermap.mytracks.bean;

import java.util.List;

public class RequestParam {
    private int pageSize = -1;
    private String orderField = null;
    private String orderType = null;
    private int currentPage = -1; 
    private String tag = null;
    private String joinTypes = null;
    private String startTime = null;
    private String endTime = null;
    private String updateStart = null;
    private String updateEnd = null;
    private String visitStart = null;
    private String visitEnd = null;
    private String keyword = null;
    private String userNames = null;
    private String mapStatus = "PUBLISHED"; //"SAVED"
    private boolean suggest = false;
    private String sourceTypes;//根据地图来源类型过滤,包括：SUPERMAP_REST,MAPVIEWER,WMS,WMTS
    private int epsgCode=-1;
    private boolean unique = false;
    public String getUserNames() {
        return userNames;
    }

    public void setUserNames(String userNames) {
        this.userNames = userNames;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getTag() {
        return tag;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    
    public String getUpdateStart() {
        return updateStart;
    }

    public void setUpdateStart(String updateStart) {
        this.updateStart = updateStart;
    }

    public String getUpdateEnd() {
        return updateEnd;
    }

    public void setUpdateEnd(String updateEnd) {
        this.updateEnd = updateEnd;
    }

    public String getVisitStart() {
        return visitStart;
    }

    public void setVisitStart(String visitStart) {
        this.visitStart = visitStart;
    }

    public String getVisitEnd() {
        return visitEnd;
    }

    public void setVisitEnd(String visitEnd) {
        this.visitEnd = visitEnd;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getOrderField() {
        return orderField;
    }

    public void setOrderField(String orderField) {
        this.orderField = orderField;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void setCurrentPage(int page) {
        this.currentPage = page;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    
    public String getJoinTypes() {
        return joinTypes;
    }

    public void setJoinTypes(String joinTypes) {
        this.joinTypes = joinTypes;
    }

    public String getMapStatus() {
        return mapStatus;
    }

    public void setMapStatus(String mapStatus) {
        this.mapStatus = mapStatus;
    }

    public boolean isSuggest() {
        return suggest;
    }

    public void setSuggest(boolean suggest) {
        this.suggest = suggest;
    }

    public String getSourceTypes() {
        return sourceTypes;
    }

    public void setSourceTypes(String sourceTypes) {
        this.sourceTypes = sourceTypes;
    }

    public int getEpsgCode() {
        return epsgCode;
    }

    public void setEpsgCode(int epsgCode) {
        this.epsgCode = epsgCode;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }
    
}
