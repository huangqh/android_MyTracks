package com.supermap.mytracks.bean;

import java.util.List;

/**
 * <p>
 * 创建地图所需的web图层的基本信息封装类，适用于底图和web图层
 * </p>
 * @author ${huangqh}
 * @version ${Version}
 * @since 1.0.0
 * 
 */
public class WebLayer {
    private Rectangle2D bounds;
    private String url;
    private List<String> subLayers;
    private String thumbnail;
    private String title;
    private int id = -1;
    private int epsgCode = -1;
    public WebLayer(WebLayer wl) {
        this.bounds = wl.getBounds();
        this.url = wl.getUrl();
        this.subLayers = wl.getSubLayers();
        this.thumbnail = wl.getThumbnail();
        this.title = wl.getTitle();
        this.id = wl.getId();
        this.epsgCode = wl.getEpsgCode();
    }

    public WebLayer() {
        super();
    }

    public Rectangle2D getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle2D bounds) {
        this.bounds = bounds;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getSubLayers() {
        return subLayers;
    }

    public void setSubLayers(List<String> subLayers) {
        this.subLayers = subLayers;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEpsgCode() {
        return epsgCode;
    }

    public void setEpsgCode(int epsgCode) {
        this.epsgCode = epsgCode;
    }

}
