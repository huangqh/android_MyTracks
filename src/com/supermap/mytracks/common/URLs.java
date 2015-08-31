package com.supermap.mytracks.common;
import java.io.File;

/**
 * <p>
 * 请求地址封装类
 * </p>
 * @author ${huangqh}
 * @version ${Version}
 * @since 1.0.0
 * 
 */
public class URLs {
    public static String DOMAIN_ICLOUD = "www.isupermap.com";//超图云的地址
    private static String mapviewer="/apps/viewer";

    public static String getURL() {
            return "http://" + DOMAIN_ICLOUD;
    }

    /**
     * <p>
     * 相对于超图云跟地址的相对路径，以“/”开始
     * </p>
     * @param path
     * @return
     * @since 1.0.0
     */
    public static String getUrl(String path){
        String url = getURL() + path + ".json";
        return url;
    }

    /**
     * <p>
     * 访问具体地图的地址
     * </p>
     * @param id
     * @return
     * @since 1.0.0
     */
    public static String getMapViewerURL(String id) {
        return getURL() + mapviewer + File.separator+ id;
    }
}
