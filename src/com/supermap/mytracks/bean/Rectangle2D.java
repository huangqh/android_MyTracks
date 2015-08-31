package com.supermap.mytracks.bean;

import java.io.Serializable;
import java.math.BigDecimal;



/**
 * <p>
 * 二维矩形类。
 * </p>
 *
 * <p>
 * 该类主要用来描述地图的地理坐标范围。
 * </p>
 *
 * @author ${Author}
 * @version ${Version}
 */
public class Rectangle2D implements Serializable {
    private static final long serialVersionUID = 1L;
    public double left;
    public double right;
    public double top;
    public double bottom;
    public Point2D leftBottom;
    public Point2D rightTop;


    public Rectangle2D() {
        this.leftBottom = new Point2D();
        this.rightTop = new Point2D();
    }


    public Rectangle2D(Rectangle2D rect2D) {
        if (rect2D == null) {
            throw new IllegalArgumentException(Rectangle2D.class.getName()+" constructor.argument.null");
        }

        if (rect2D.leftBottom == null || rect2D.rightTop == null) {
            throw new IllegalArgumentException("Rectangle2D.constructor.rect2D.illegal");
        }

        
        this.leftBottom = new Point2D(rect2D.leftBottom);
        this.rightTop = new Point2D(rect2D.rightTop);
        this.left = rect2D.left;
        this.right = rect2D.right;
        this.top = rect2D.top;
        this.bottom = rect2D.bottom;
    }


    public Rectangle2D(Point2D leftBottom, Point2D rightTop) {
        if (leftBottom == null || rightTop == null) {
        	throw new IllegalArgumentException(Rectangle2D.class.getName()+" constructor.argument.null");
        }

        this.leftBottom = new Point2D(leftBottom);
        this.rightTop = new Point2D(rightTop);
        this.left = leftBottom.x;
        this.bottom = leftBottom.y;
        this.right = rightTop.x;
        this.top = rightTop.y;
    }

   
    public Rectangle2D(double left, double bottom, double right, double top) {
        this.leftBottom = new Point2D(left, bottom);
        this.rightTop = new Point2D(right, top);
        this.left = left;
        this.bottom = bottom;
        this.right = right;
        this.top = top;
    }


    public double width() {
        double width = 0.0;

        if (this.rightTop != null && this.leftBottom != null) {
            BigDecimal b1 = new BigDecimal(Double.toString(this.rightTop.x));
            BigDecimal b2 = new BigDecimal(Double.toString(this.leftBottom.x));
            width = b1.subtract(b2).doubleValue();
        }

        return width;
    }

    public double height() {
        double height = 0.0;

        if (this.rightTop != null && this.leftBottom != null) {
            BigDecimal b1 = new BigDecimal(Double.toString(this.rightTop.y));
            BigDecimal b2 = new BigDecimal(Double.toString(this.leftBottom.y));
            height = b1.subtract(b2).doubleValue();
        }

        return height;
    }



    public Point2D center() {
        return new Point2D((this.left + this.right) / 2, (this.bottom + this.top) / 2);
    }

    /**
     * 获取矩形范围的左下角 x 坐标
     *
     * @return 矩形范围的左下角 x 坐标
     */
    public double getLeft() {
        return this.left;
    }

    /**
     * 设置矩形范围的左下角 x 坐标
     *
     * @param left 左下角 x 坐标
     */
    public void setLeft(double left) {
        if(this.leftBottom == null) {
            this.leftBottom = new Point2D(this.left,this.bottom);
        }
        this.left = left;
    }

    /**
     * 获取矩形范围的右上角 x 坐标
     *
     * @return 矩形范围的右上角 x 坐标
     */
    public double getRight() {
        return this.right;
    }

    /**
     * 设置矩形范围的右上角 x 坐标。
     *
     * @param right 右上角 x 坐标。
     */
    public void setRight(double right) {
        if(this.rightTop == null) {
            this.rightTop = new Point2D(this.right,this.top);
        }
        this.right = right;

    }

    /**
     * 设置矩形范围的右上角 y 坐标。
     *
     * @param top 右上角 y 坐标。
     */
    public void setTop(double top) {
        if(this.rightTop == null) {
            this.rightTop = new Point2D(this.right,this.top);
        }
        this.top = top;

    }

    /**
     * 获取矩形范围的右上角 y 坐标。
     *
     * @return 右上角 y 坐标。
     */
    public double getTop() {
        return this.top;
    }

    /**
     * 设置矩形范围的左下角 y 坐标
     *
     * @param bottom 左下角 y 坐标
     */
    public void setBottom(double bottom) {
        if(this.leftBottom == null) {
            this.leftBottom = new Point2D(this.left,this.bottom);
        }
        this.bottom = bottom;
    }

    /**
     * 设置矩形范围的左下角 y 坐标。
     *
     * @return 左下角 y 坐标。
     */
    public double getBottom() {
        return this.bottom;
    }
    
    public Point2D getLeftBottom(){
        if (this.leftBottom == null){
            this.leftBottom = new Point2D(this.left,this.bottom);
        }
        return this.leftBottom;
    }
    
    public void setLeftBottom(Point2D leftBottom){
        this.left = leftBottom.x;
        this.bottom = leftBottom.y;
        if (this.leftBottom == null){
            this.leftBottom = new Point2D(this.left,this.bottom);  
        } else {
            this.leftBottom.x = leftBottom.x;
            this.leftBottom.y = leftBottom.y;
        }
        
    }
    
    public Point2D getRightTop(){
        if (this.rightTop == null){
            this.rightTop = new Point2D(this.right,this.top);
        }
        return this.rightTop;
    }
    
    public void setRightTop(Point2D rightTop){
        this.right = rightTop.x;
        this.top = rightTop.y;
        if (this.rightTop == null){
            this.rightTop = new Point2D(this.right,this.top);  
        } else {
            this.rightTop.x = rightTop.x;
            this.rightTop.y = rightTop.y;
        }
        
    }
}