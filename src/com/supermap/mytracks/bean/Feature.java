package com.supermap.mytracks.bean;

public class Feature {
    private Attributes attributes = null;
    private Geometry geometry = null;
    private String icon = null;
    private String id = null;
    private String name = null;
    private boolean visiable = false;
    private String style = null;
    
    public Attributes getAttributes() {
        return attributes;
    }
    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }
    public Geometry getGeometry() {
        return geometry;
    }
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isVisiable() {
        return visiable;
    }
    public void setVisiable(boolean visiable) {
        this.visiable = visiable;
    }
    public String getStyle() {
        return style;
    }
    public void setStyle(String style) {
        this.style = style;
    }

}
