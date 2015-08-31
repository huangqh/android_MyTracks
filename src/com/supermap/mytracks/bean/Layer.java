package com.supermap.mytracks.bean;
import java.util.List;

public class Layer {

    private Rectangle2D bounds = null;
    private String datasourceName = null;
    private int id = 0;
    private String identifier = null;
    private boolean isVisible = true;
    private String layerType = null;
    private int mapId ;
    private List<Marker> markers = null;
    private String name;
    private List<Feature> features = null;
    private int opacty = 1;
    private double[] scales = null;
//    private Style style = null;
    private List<String> subLayers;
    private String title = null;
    private String type = null;
    private String url = null;
    private int zindex = 0;
    
    public Rectangle2D getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle2D bounds) {
        this.bounds = bounds;
    }

    public String getDatasourceName() {
        return datasourceName;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean isIsVisible() {
        return isVisible;
    }

    public void setIsVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public String getLayerType() {
        return layerType;
    }

    public void setLayerType(String layerType) {
        this.layerType = layerType;
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public List<Marker> getMarkers() {
        return markers;
    }

    public void setMarkers(List<Marker> markers) {
        this.markers = markers;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public int getOpacty() {
        return opacty;
    }

    public void setOpacty(int opacty) {
        this.opacty = opacty;
    }

    public double[] getScales() {
        return scales;
    }

    public void setScales(double[] scales) {
        this.scales = scales;
    }

//    public Style getStyle() {
//        return style;
//    }
//
//    public void setStyle(Style style) {
//        this.style = style;
//    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getZindex() {
        return zindex;
    }

    public void setZindex(int zindex) {
        this.zindex = zindex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSubLayers() {
        return subLayers;
    }

    public void setSubLayers(List<String> subLayers) {
        this.subLayers = subLayers;
    }

}
