package com.supermap.mytracks.bean;
import java.io.Serializable;

/**
 * <p>
 * 图层控制所需的图层基本信息类，便于图层控制
 * </p>
 * @author ${huangqh}
 * @version ${Version}
 * @since 1.0.0
 * 
 */
public class LayerControlBean implements Serializable {
    private static final long serialVersionUID = -1208651154121804446L;
    private Rectangle2D bounds;
    private String layerType;
    private String layerName;
    private boolean visible = true;

    public LayerControlBean(Rectangle2D bounds, String layerType, String layerName, boolean visible) {
        this.bounds = bounds;
        this.layerType = layerType;
        this.layerName = layerName;
        this.visible = visible;
    }

    public LayerControlBean() {
        super();
    }

    public LayerControlBean(LayerControlBean bean) {
        this.bounds = bean.getBounds();
        this.layerType = bean.getLayerType();
        this.layerName = bean.getLayerName();
        this.visible = bean.isVisible();
    }

    public Rectangle2D getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle2D bounds) {
        this.bounds = bounds;
    }

    public String getLayerType() {
        return layerType;
    }

    public void setLayerType(String layerType) {
        this.layerType = layerType;
    }

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

}
