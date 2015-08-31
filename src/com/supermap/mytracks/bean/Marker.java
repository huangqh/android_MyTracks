package com.supermap.mytracks.bean;

import java.io.File;

public class Marker {
    private Attributes attributes = null;
    private Geometry geometry = null;
    private int height = 0;
    private int width = 0;
    private String icon = null;
    private String id = null;
    
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
    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }

    public String getBigIcon() {
        // 获得大尺寸标注图标，url从http://..../pictures/a.png -----> http://..../pictures/big/a.png
        int position = icon.lastIndexOf(File.separator);
        String bigIcon = icon.substring(0, position) + File.separator + "big" + icon.substring(position, icon.length());
        return bigIcon;
    }

    public String getIconPressed() {
        // 获得大尺寸标注的按下图标，url从http://..../pictures/a.png -----> http://..../pictures/big/a.png
        int position = icon.lastIndexOf(File.separator);
        String bigIcon = icon.substring(0, position) + File.separator + "big" + File.separator + "pressed" + icon.substring(position, icon.length());
        return bigIcon;
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
    
}
