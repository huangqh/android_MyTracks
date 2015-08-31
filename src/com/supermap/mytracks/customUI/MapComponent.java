package com.supermap.mytracks.customUI;

import java.util.ArrayList;
import java.util.List;
import com.supermap.data.GeoLine;
import com.supermap.data.GeoPoint;
import com.supermap.data.Geometry;
import com.supermap.data.Point2Ds;
import com.supermap.data.Rectangle2D;
import com.supermap.mapping.MapView;
import com.supermap.mapping.TrackingLayer;
import com.supermap.mytracks.R;
import com.supermap.mytracks.activity.CreateMapActivity;

import android.view.View;
import android.view.View.OnClickListener;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

/**
 * <p>
 * 地图交互相关界面布局类，如地图所需的布局以及缩放按钮和全幅按钮的控件的响应
 * </p>
 * @author ${huangqh}
 * @version ${Version}
 * @since 1.0.0
 * 
 */
public class MapComponent extends RelativeLayout implements OnClickListener{
    private Context context = null;
    private static MapComponent instance = null;
    private MapView mapview = null;
//    private UserLocationFound userLocationFound = null;
    private List<com.supermap.data.Point2D> points;
    public MapComponent(Context context) {
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.mapcomponent, this);
        mapview=(MapView)this.findViewById(R.id.mapview);
        this.findViewById(R.id.mapview).setOnClickListener(this);
//        this.findViewById(R.id.ibtn_mapview_gps).setOnClickListener(this);
        this.findViewById(R.id.ibtn_mapview_zoomin).setOnClickListener(this);
        this.findViewById(R.id.ibtn_mapview_zoomout).setOnClickListener(this);
        this.findViewById(R.id.ibtn_mapview_fullscreen).setOnClickListener(this);
//        this.userLocationFound = new UserLocationFound(context, mapview);
    }

    private MapComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private MapComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    
    public static MapComponent getInstance(Context context){
        if (instance == null){
            instance = new MapComponent(context);
        }
        instance.context = context;
        
        return instance;
    }
    
    public MapView getMapView(){
        return this.mapview;
    }

//    public void removeGPSUpdate(){
//        this.userLocationFound.removeUpdate();
//    }
    
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.ibtn_mapview_zoomin:
            mapview.getMapControl().getMap().zoom(2);
            mapview.getMapControl().getMap().refresh();
            break;
        case R.id.ibtn_mapview_zoomout:
            mapview.getMapControl().getMap().zoom(0.5);
            mapview.getMapControl().getMap().refresh();
            break;
//        case R.id.ibtn_mapview_gps:
//            userLocationFound.showGPS();
//            break;
        case R.id.ibtn_mapview_fullscreen:
            Rectangle2D rect = getViewBounds();
            if (rect != null) {
                // 根据地图矢量和标注图层的bounds作为可见范围进行全幅
                mapview.getMapControl().getMap().setViewBounds(rect);
                Rectangle2D getRect = mapview.getMapControl().getMap().getViewBounds();
                // 如果imobile返回的viewBounds的比设置进去的viewBounds小，那么上升一级viewBounds,即缩小地图即可
                if ((getRect.getBottom() > rect.getBottom() && getRect.getTop() < rect.getTop())
                        || (getRect.getLeft() > rect.getLeft() && getRect.getRight() < rect.getRight())) {
                    mapview.getMapControl().getMap().zoom(0.5);
                }
            } else {
                mapview.getMapControl().getMap().viewEntire();
            }
            mapview.getMapControl().getMap().refresh();
            break;
        case R.id.mapview:
            break;
        default:
            break;
        }
       if (context instanceof CreateMapActivity) {
            ((CreateMapActivity) context).refreshMarkerView();
        }
        
    }
    
    /**
     * <p>
     * 根据地图矢量和标注图层的bounds计算可见范围进行全幅
     * </p>
     * @return
     * @since 1.0.0
     */
    private Rectangle2D getViewBounds() {
        TrackingLayer tl = mapview.getMapControl().getMap().getTrackingLayer();
        int count = tl.getCount();
        Rectangle2D rect = null;
        List<Rectangle2D> list = new ArrayList<Rectangle2D>();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                Geometry geo = tl.get(i);
                if (geo != null) {
                    list.add(geo.getBounds());
                }
            }
        }
        if (points != null && points.size() > 0) {
            if (points.size() == 1) {
                GeoPoint point = new GeoPoint(points.get(0).getX(), points.get(0).getY());
                list.add(point.getBounds());
            } else {
                com.supermap.data.Point2D[] ps = new com.supermap.data.Point2D[points.size()];
                ps = points.toArray(ps);
                Point2Ds point2Ds = new Point2Ds(ps);
                GeoLine line = new GeoLine(point2Ds);
                list.add(line.getBounds());
            }
        }
        if (list.size() > 0) {
            double left = list.get(0).getLeft();
            double right = list.get(0).getRight();
            double bottom = list.get(0).getBottom();
            double top = list.get(0).getTop();
            for (int i = 1; i < list.size(); i++) {
                Rectangle2D r = list.get(i);
                if (r.getLeft() < left) {
                    left = r.getLeft();
                }
                if (r.getBottom() < bottom) {
                    bottom = r.getBottom();
                }
                if (r.getRight() > right) {
                    right = r.getRight();
                }
                if (r.getTop() > top) {
                    top = r.getTop();
                }
            }
            rect = new Rectangle2D(left, bottom, right, top);
        }
        return rect;
    }

    /**
     * <p>
     * 设置地图标注图层所有的点坐标数据集合，用于计算标注图层的bounds
     * </p>
     * @param points
     * @since 1.0.0
     */
    public void setDatas(List<com.supermap.data.Point2D> points) {
        this.points = points;
    }
}
