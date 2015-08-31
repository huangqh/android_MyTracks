package com.supermap.mytracks.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import android.text.TextUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.supermap.mytracks.bean.Content;
import com.supermap.mytracks.bean.Layer;
import com.supermap.mytracks.bean.Maps;
import com.supermap.mytracks.bean.RequestParam;
import com.supermap.mytracks.bean.RequestParamType;
import com.supermap.mytracks.common.Params;
import com.supermap.mytracks.common.URLs;

/**
 * <p>
 * 根据条件发送请求获取所有满足条件的地图
 * </p>
 * @author ${huangqh}
 * @version ${Version}
 * @since 1.0.0
 * 
 */
public class MapsActive {
    public static final int MESSAGE_FAILURE_NO_NETWORK = -3;
    public static final int MESSAGE_NO_NEXTPAGE = 2;
    private RequestCallBack<String> requestCallBack;
    private int totalPage;
    private int total;
    private int pageSize;
    private RequestParam param = null;
    // 必传参数
    private String path;

    public MapsActive(){
    }

    /**
     * <p>
     * 请求的地址
     * </p>
     * @param path
     * @since 1.0.0
     */
    public void setPath(String path) {
        this.path = path;
    }
    
    public RequestParam getParam() {
        return param;
    }

    /**
     * <p>
     * 请求参数
     * </p>
     * @param param
     * @since 1.0.0
     */
    public void setParam(RequestParam param) {
        this.param = param;
    }
    
    /**
     * <p>
     * 转换请求参数的封装
     * </p>
     * @param param
     * @return
     * @since 1.0.0
     */
    private RequestParams getParams(RequestParam param) {
        
        RequestParams requestParams = new RequestParams();
        if (Params.cookie_Jessionid != null) {
            requestParams.addHeader("Cookie", Params.cookie_Jessionid);
//            requestParams.addHeader("Cookie", true);
        }
        if(param == null){
            return requestParams;
        }
        if (!TextUtils.isEmpty(param.getOrderField())){
            JSONObject orderByJSON = new JSONObject();
            orderByJSON.put(RequestParamType.OderBy.ORDERFIELD,param.getOrderField());
            if (!TextUtils.isEmpty(param.getOrderType())){
                orderByJSON.put(RequestParamType.OderBy.ORDERTYPE, param.getOrderType());
            }
            JSONArray orderByArray = new JSONArray();
            orderByArray.add(orderByJSON);
            requestParams.addQueryStringParameter(RequestParamType.ORDERBY,orderByArray.toJSONString());
        }
        
        if (param.getCurrentPage() != -1){
            requestParams.addQueryStringParameter(RequestParamType.CURRENTPAGE, String.valueOf(param.getCurrentPage()));
        }
        
        if (param.getPageSize() != -1){
            requestParams.addQueryStringParameter(RequestParamType.PAGESIZE, String.valueOf(param.getPageSize()));
        }
        
        if (!TextUtils.isEmpty(param.getTag())){
            requestParams.addQueryStringParameter(RequestParamType.TAGS, param.getTag());
        }
        
        if (!TextUtils.isEmpty(param.getJoinTypes())){
            requestParams.addQueryStringParameter("joinTypes", param.getJoinTypes());
        }
        
        if (!TextUtils.isEmpty(param.getStartTime())){
            requestParams.addQueryStringParameter(RequestParamType.STARTTIME, param.getStartTime());
        }
        
        if (!TextUtils.isEmpty(param.getEndTime())){
            requestParams.addQueryStringParameter(RequestParamType.ENDTIME, param.getEndTime());
        }
        
        if (!TextUtils.isEmpty(param.getUpdateStart())){
            requestParams.addQueryStringParameter(RequestParamType.UPDATESTART, param.getUpdateStart());
        }
        
        if (!TextUtils.isEmpty(param.getUpdateEnd())){
            requestParams.addQueryStringParameter(RequestParamType.UPDATEEND, param.getUpdateEnd());
        }
        if (!TextUtils.isEmpty(param.getVisitStart())){
            requestParams.addQueryStringParameter(RequestParamType.VISITSTART, param.getVisitStart());
        }
        
        if (!TextUtils.isEmpty(param.getVisitEnd())){
            requestParams.addQueryStringParameter(RequestParamType.VISITEND, param.getVisitEnd());
        }
        
        if (!TextUtils.isEmpty(param.getKeyword())){
            requestParams.addQueryStringParameter(RequestParamType.KEYWORDS, param.getKeyword());
        }
        
        if (!TextUtils.isEmpty(param.getUserNames())){
            requestParams.addQueryStringParameter(RequestParamType.USERNAMES, param.getUserNames());
        }
        
        if (!TextUtils.isEmpty(param.getMapStatus())){
            requestParams.addQueryStringParameter(RequestParamType.MAPSTATUS, param.getMapStatus());
        }
        
        if (param.isSuggest()){
            requestParams.addQueryStringParameter(RequestParamType.SUGGEST, "true");
        }
        
        if (!TextUtils.isEmpty(param.getSourceTypes())){
            requestParams.addQueryStringParameter(RequestParamType.SOURCETYPES, param.getSourceTypes());
        }
        
        if (param.getEpsgCode()!=-1){
            requestParams.addQueryStringParameter(RequestParamType.EPSGCODE, String.valueOf(param.getEpsgCode()));
        }
        
        if (param.isUnique()){
            requestParams.addQueryStringParameter(RequestParamType.UNIQUE, "true");
        }
        
        // 添加上 MapStatusType.PUBLISHED 条件，未登录情况下也能查询出私有地图？
        // params.put(MapsRequestParamsList.MAPSTATUS, MapStatusType.PUBLISHED);
        
        return requestParams;
    }

    /**
     * <p>
     * 设置请求完成的回调处理类
     * </p>
     * @param requestCallBack
     * @since 1.0.0
     */
    public void setRequestCallBack(RequestCallBack<String> requestCallBack) {
        this.requestCallBack = requestCallBack;
    }
    
    /**
     * <p>
     * 发送请求
     * </p>
     * @since 1.0.0
     */
    public void active() {
        HttpUtils http = new HttpUtils();
        http.configCurrentHttpCacheExpiry(500 * 1);

        RequestParams requestParams = this.getParams(this.param);
        http.send(HttpRequest.HttpMethod.GET, URLs.getUrl(this.path), requestParams, this.requestCallBack);
    }
    
    /**
     * <p>
     * 解析结果，提取出所有的地图
     * </p>
     * @param json
     * @return
     * @since 1.0.0
     */
    public List<Content> getMaps(String json){
        Maps maps = JSON.parseObject(json, Maps.class);
        if (maps == null) {
            return null;
        }
        this.totalPage =  maps.getTotalPage();
        this.total = maps.getTotal();
        this.pageSize = maps.getPageSize();

        // 转化json数据为Content，并刷新地图列表View
        List<Content> appendlist = new ArrayList<Content>();
        if (maps.getContent() != null && maps.getContent().size() > 0){
            for (int i=0;i<maps.getContent().size();i++){
                Content mapItem = maps.getContent().get(i);
                if(!this.isWMSService(mapItem)){
                    appendlist.add(mapItem);
                }
            }
        }
        
        return appendlist;
    }
    
    /**
     * 判断是否为WMS服务
     * @param content
     * @return
     */
    public boolean isWMSService(Content content) {
        if (content == null) {
            return false;
        }
        List<Layer> layers = content.getLayers();
        if (layers != null) {
            for (Layer layer : layers) {
                if (layer.getUrl() != null && layer.getUrl().indexOf("wms") != -1) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * <p>
     * 分页请求的总页数
     * </p>
     * @return
     * @since 1.0.0
     */
    public int getTotalPage(){
        return totalPage;
    }
    /**
     * <p>
     * 当前请求返回地图的总个数
     * </p>
     * @return
     * @since 1.0.0
     */
    public int getTotal(){        
        return total;
    }
    /**
     * <p>
     * 分页请求的每一页的大小即条目
     * </p>
     * @return
     * @since 1.0.0
     */
    public int getPageSize(){        
        return pageSize;
    }
}
