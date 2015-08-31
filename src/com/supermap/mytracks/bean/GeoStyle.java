package com.supermap.mytracks.bean;

public class GeoStyle {
    // 填充颜色，点面需要
    private String fillColor = null;
    // 填充透明度，点面需要
    private float fillOpacity = 1.0f;
    // 点半径，点需要
    private int pointRadius = 6;
    // 轮廓颜色，点线面需要
    private String strokeColor = null;
    // 轮廓线样式，线面需要
    private String strokeDashstyle = null;
    // 轮廓线端点，线面需要
    private String strokeLinecap = null;
    // 轮廓透明度，点线需要
    private float strokeOpacity = 1.0f;
    // 轮廓线粗细，点线面需要
    private int strokeWidth = 3;

    public String getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(String strokeColor) {
        this.strokeColor = strokeColor;
    }

    public String getStrokeDashstyle() {
        return strokeDashstyle;
    }

    public void setStrokeDashstyle(String strokeDashstyle) {
        this.strokeDashstyle = strokeDashstyle;
    }

    public String getStrokeLinecap() {
        return strokeLinecap;
    }

    public void setStrokeLinecap(String strokeLinecap) {
        this.strokeLinecap = strokeLinecap;
    }

    public float getStrokeOpacity() {
        return strokeOpacity;
    }

    public void setStrokeOpacity(float strokeOpacity) {
        this.strokeOpacity = strokeOpacity;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public String getFillColor() {
        return fillColor;
    }

    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }

    public float getFillOpacity() {
        return fillOpacity;
    }

    public void setFillOpacity(float fillOpacity) {
        this.fillOpacity = fillOpacity;
    }

    public int getPointRadius() {
        return pointRadius;
    }

    public void setPointRadius(int pointRadius) {
        this.pointRadius = pointRadius;
    }

}
