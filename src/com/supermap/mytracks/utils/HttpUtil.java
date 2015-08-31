package com.supermap.mytracks.utils;

import java.util.Iterator;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.supermap.mytracks.common.Params;

/**
 * <p>
 * 请求处理工具类
 * </p>
 * @author ${huangqh}
 * @version ${Version}
 * @since 1.0.0
 * 
 */
public class HttpUtil {

    /**
     * 得到Cookie
     *  @param headers
     */
    public static String getCookie(Header[] headers, String name) {
        return getCookie(headers,name,"");
    }
    
    /**
     * 得到Cookie
     *
     * @param headers
     * @param name cookie名
     * @param exceptValue 排除的值
     */
    public static String getCookie(Header[] headers, String name, String exceptValue) {
        String cookie = null;
        if (headers != null) {
            //Header[] headers = httpResponse.getHeaders("Set-Cookie");
            //String headerstr = headers.toString();
            if (headers == null)
                return null;
            for (int i = 0; i < headers.length; i++) {
                String cookieValue = headers[i].getValue();
                if (cookieValue.contains(name)) {
                    String[] cookievalues = cookieValue.split(";");
                    for (int j = 0; j < cookievalues.length; j++) {
                        String[] keyPair = cookievalues[j].split("=");
                        String key = keyPair[0].trim();
                        if (name.equals(key)) {
                             String value = keyPair.length > 1 ? keyPair[1].trim() : "";
                             if (!value.equals(exceptValue)){
                                 cookie = cookievalues[j];
                                 break;
                             }
                        }
                    }
                }
            }
        }
        
        return cookie;
    }

    /**
     * <p>
     * 发送请求，并把结果返回给callBack
     * </p>
     * @param method 请求方法
     * @param path 请求的url
     * @param queryParams 请求参数
     * @param callBack 接收返回结果的RequestCallBack对象
     * @since 1.0.0
     */
    public static <T> void sendRequest(HttpRequest.HttpMethod method, String path, Map<String, String> queryParams, RequestCallBack<T> callBack) {
        sendRequest(method, path, queryParams, null, callBack);
    }

    /**
     * <p>
     * 发送请求，并把结果返回给callBack
     * </p>
     * @param method 请求方法
     * @param path 请求的url
     * @param queryParams 请求参数
     * @param bodyParams 请求体内容键值对
     * @param callBack 接收返回结果的RequestCallBack对象 
     * @since 1.0.0
     */
    public static <T> void sendRequest(HttpRequest.HttpMethod method, String path, Map<String, String> queryParams, Map<String, String> bodyParams,
            RequestCallBack<T> callBack) {
        HttpUtils http = new HttpUtils();
        http.configCurrentHttpCacheExpiry(500 * 1);// 设置缓存时间，此处基本不做缓存
        RequestParams requestParams = new RequestParams();
        if (queryParams != null) {
            Iterator<String> it = queryParams.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next().toString();
                requestParams.addQueryStringParameter(key, queryParams.get(key));
            }
        }
        if (bodyParams != null) {
            Iterator<String> it = bodyParams.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next().toString();
                requestParams.addBodyParameter(key, bodyParams.get(key));
            }
        }

        if (Params.cookie_Jessionid != null) {
            requestParams.addHeader("Cookie", Params.cookie_Jessionid);
        }
        http.send(method, path, requestParams, callBack);
    }
      
    public static <T> void sendRequestByEntity(HttpRequest.HttpMethod method, String path, Map<String, String> queryParams, HttpEntity bodyEntity,
            RequestCallBack<T> callBack) {
        HttpUtils http = new HttpUtils();
        http.configCurrentHttpCacheExpiry(500 * 1);
        RequestParams requestParams = new RequestParams();
        if (queryParams != null) {
            Iterator<String> it = queryParams.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next().toString();
                requestParams.addQueryStringParameter(key, queryParams.get(key));
            }
        }
        if (bodyEntity != null) {
            requestParams.setBodyEntity(bodyEntity);
        }

        if (Params.cookie_Jessionid != null) {
            requestParams.addHeader("Cookie", Params.cookie_Jessionid);
        }
        http.send(method, path, requestParams, callBack);
    }
 
}
