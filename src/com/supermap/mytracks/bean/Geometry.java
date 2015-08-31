package com.supermap.mytracks.bean;

public class Geometry {
    private Point2D[] points = null;
    private String type = null;
    
    public Point2D[] getPoints() {
        return points;
    }
    public void setPoints(Point2D[] points) {
        this.points = points;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
