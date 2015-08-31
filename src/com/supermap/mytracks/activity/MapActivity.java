package com.supermap.mytracks.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import android.content.Intent;
import android.view.*;
import android.view.View.OnTouchListener;
import android.widget.*;
import com.umeng.socialize.sso.UMSsoHandler;
import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.supermap.data.Color;
import com.supermap.data.Dataset;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.GeoLine;
import com.supermap.data.GeoPoint;
import com.supermap.data.GeoRegion;
import com.supermap.data.Point;
import com.supermap.data.Point2Ds;
import com.supermap.data.Size2D;
import com.supermap.data.Workspace;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.ViewGroup.LayoutParams;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.TrackingLayer;
import com.supermap.mytracks.R;
import com.supermap.mytracks.adapter.LayerAdapter;
import com.supermap.mytracks.bean.Feature;
import com.supermap.mytracks.bean.GeoStyle;
import com.supermap.mytracks.bean.Geometry;
import com.supermap.mytracks.bean.GeometryType;
import com.supermap.mytracks.bean.Layer;
import com.supermap.mytracks.bean.LayerControlBean;
import com.supermap.mytracks.bean.LayerType;
import com.supermap.mytracks.bean.Marker;
import com.supermap.mytracks.bean.Point2D;
import com.supermap.mytracks.bean.Rectangle2D;
import com.supermap.mytracks.common.URLs;
import com.supermap.mytracks.customUI.CustomProgressDialog;
import com.supermap.mytracks.customUI.MapComponent;
import com.supermap.mytracks.share.SharePopupWindow;
import com.supermap.mytracks.utils.DialogUtils;
import com.supermap.mytracks.utils.DisplayUtil;
import com.supermap.mytracks.utils.HttpExceptionHandler;
import com.supermap.mytracks.utils.MapsActive;

/**
 * <p>
 * 浏览地图
 * </p>
 * @author ${huangqh}
 * @version ${Version}
 * @since 1.0.0
 * 
 */
public class MapActivity extends BaseActivity {
    public static final int OPENMAP = 100000000;// 随意的
    public static final String VIEW_MARKER = "markerView";
    private RequestCallBack<String> mapCallBack = null;// 访问地图成功后的回调对象
    private FrameLayout parent = null;
    private com.supermap.mytracks.bean.Map map = null;
    private String path = null;
    private MapComponent component = null;
    private MapView mapview = null;
    private MapControl mapControl = null;
    private CustomProgressDialog dialog = null;
    private MapsActive mapActive = null;
    private BitmapUtils bitmapUtils = null;
    @ViewInject(R.id.tv_map_title)
    private TextView tv_map_title;
    @ViewInject(R.id.ibtn_map_layercontrol)
    private ImageView ibtn_map_layercontrol;
    private PopupWindow layerWindow = null;
    private List<LayerControlBean> layers = new ArrayList<LayerControlBean>();
    private View layersView = null;
    private Map<String, CallOut> calloutAdded = new HashMap<String, CallOut>();
    private List<com.supermap.data.Point2D> calloutPoints = new ArrayList<com.supermap.data.Point2D>();
    private View bg_gray; // 灰色背景，当弹出分享面板是显示
    private View marker_detail = null;
    private LayerAdapter layerAdapter = null;

    private Map<String, Marker> markerMap = new HashMap<String, Marker>();
    private SharePopupWindow sharePopupWindow = null;
    private TextView marker_title = null;
    private TextView marker_desc = null;
    private boolean initView = false;
    private ImageView preMarker; // 上一个被选中的markerView
    private String preIcon; // 上一个被选中的marker
    private List<CallOut> popu_marker = new ArrayList<CallOut>();

    private CallOut popu_callout = null;
    private Point2D point_marker = null;
    private int marker_height;
    // 为了显示和隐藏web图层的时候方便
    private Map<String, com.supermap.mapping.Layer> mobileLayers = new HashMap<String, com.supermap.mapping.Layer>();
    // 记录当前被点击的标注
    private String markerClickedKey;
    // key是marker图层名，value是图层中所有marker的标签名，便于控制图层的显示和隐藏以及删除图层
    private Map<String, List<String>> markerKeyMap = new HashMap<String, List<String>>();
    // 保存地图中所有的矢量图层
    private List<Layer> featureLayers = new ArrayList<Layer>();
    // 保存地图中所有的矢量图层的可见性
    private Map<String, Boolean> featureLayerVisibleMap = new HashMap<String, Boolean>();

    /**
     * <p>
     * 访问地图成功后的回调类
     * </p>
     * @author ${huangqh}
     * @version ${Version}
     * @since 1.0.0
     * 
     */
    class GetMapCallBack extends RequestCallBack<String> {

        @Override
        public void onFailure(HttpException error, String arg1) {
            HttpExceptionHandler.handleExption(error, true, context);
            DialogUtils.stopProgressDialog(dialog);
        }

        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            String result = responseInfo.result;
            map = JSON.parseObject(result, com.supermap.mytracks.bean.Map.class);
            if (map != null) {
                tv_map_title.setText(map.getTitle());
                openMap(map);
            }
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case 2:
                // 开关图层
                if (msg.getData() != null && (msg.getData().getSerializable("layerBean") instanceof LayerControlBean)) {
                    layerClick((LayerControlBean) msg.getData().getSerializable("layerBean"));
                }
                break;
            case 3:// 初始化环境完成后，初始化视图
                initView();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        context = this;
        Bundle bundle = this.getIntent().getExtras();
        this.path = bundle.getString("path");
        this.parent = new FrameLayout(this);
        this.setContentView(this.parent);
        dialog = CustomProgressDialog.createDialog(this);
        this.dialog.setContentView(R.layout.progress_custom);
        dialog = DialogUtils.startProgressDialog(this, dialog, "正在请求...");
        this.initView();
    }

    @OnClick(R.id.iv_map_back)
    private void onClickBack(View v) {
        finish();
    }

    /**
     * <p>
     * 初始化视图
     * </p>
     * @since 1.0.0
     */
    private void initView() {
        if (!this.initView) {
            this.component = MapComponent.getInstance(this);
            if (component.getParent() != null) {
                FrameLayout p = (FrameLayout) component.getParent();
                p.removeView(component);
            }
            parent.addView(component);
            this.mapview = component.getMapView();
            this.mapControl = this.mapview.getMapControl();
            // LinearLayout.inflate(this, R.layout.mapview_topbtns, parent);
            LinearLayout.inflate(this, R.layout.map_top, parent);

            // 增加灰色背景，当弹出分享面板时使用
            bg_gray = new View(context);
            LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            bg_gray.setLayoutParams(params);
            bg_gray.setBackgroundColor(getResources().getColor(R.color.bg_half_transparent));
            bg_gray.setVisibility(View.GONE);
            parent.addView(bg_gray);

            ViewUtils.inject(this, parent);
            this.initView = true;
            mapCallBack = new GetMapCallBack();
            this.mapActive = new MapsActive();
            this.mapActive.setPath(this.path);
            this.mapActive.setRequestCallBack(mapCallBack);
            this.mapActive.active();
        }

        this.mapview.getMapControl().setOnTouchListener(new OnTouchListener() {
            float preX = 0;
            float preY = 0;
            double distance = DisplayUtil.getPxFromDp(context, 2);
            double maxMoveDistance = -1;

            @Override
            public boolean onTouch(View arg0, MotionEvent ev) {
                switch (ev.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    refreshMarkerView();
                    if (maxMoveDistance < Math.pow(distance, 2)) {
                        CallOut callout = mapview.getCallOut(VIEW_MARKER);
                        if (callout != null && callout.isShown()) {
                            getMarkerBitmapUtils().display(preMarker, preIcon);
                            mapview.removeCallOut(VIEW_MARKER);
                        }
                    }
                    break;
                case MotionEvent.ACTION_DOWN:
                    preX = ev.getRawX();
                    preY = ev.getRawY();
                    maxMoveDistance = -1;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (maxMoveDistance < Math.pow(distance, 2)) {
                        double dis = Math.pow((ev.getRawX() - preX), 2) + Math.pow((ev.getRawY() - preY), 2);
                        if (maxMoveDistance < dis) {
                            maxMoveDistance = dis;
                        }
                    }
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_CANCEL:
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    refreshMarkerView();
                    break;
                }
                return false;
            }
        });

    }

    /**
     * <p>
     * 控制分享弹出框的显示和隐藏
     * </p>
     * @param v
     * @since 1.0.0
     */
    @OnClick(R.id.iv_map_share)
    private void onClick_Share(View v) {
        if (map == null) {
            Toast.makeText(context, getString(R.string.map_share_wait), Toast.LENGTH_SHORT).show();
            return;
        }
        if (sharePopupWindow == null) {
            sharePopupWindow = new SharePopupWindow(this);
            sharePopupWindow.setMapThumbnailURL(map.getThumbnail());
            sharePopupWindow.setMapURL(URLs.getMapViewerURL(String.valueOf(map.getId())));
            sharePopupWindow.setMapDes(map.getDescription());
            sharePopupWindow.setMapTitle(map.getTitle());

            sharePopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    // 主要是为了隐藏分享面板后，让灰色背景也消失掉
                    hideSharePopupWindow();
                }
            });
        }
        if (sharePopupWindow.isShowing()) {
            hideSharePopupWindow();
        } else {
            showSharePopupWindow();
        }
    }

    /**
     * <p>
     * 弹出分享选项弹出框
     * </p>
     * @since 1.0.0
     */
    private void showSharePopupWindow() {
        if (bg_gray != null) {
            bg_gray.setVisibility(View.VISIBLE);
        }
        // 显示分享面板
        if (sharePopupWindow != null && !sharePopupWindow.isShowing()) {
            sharePopupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        }
    }

    /**
     * <p>
     * 隐藏分享选项弹出框
     * </p>
     * @since 1.0.0
     */
    private void hideSharePopupWindow() {
        // 隐藏分享面板
        if (sharePopupWindow != null && sharePopupWindow.isShowing()) {
            sharePopupWindow.dismiss();
        }
        // 隐藏灰色背景
        if (bg_gray != null) {
            bg_gray.setVisibility(View.GONE);
        }
    }

    /**
     * <p>
     * UMeng相关授权成功后所需的回调实现
     * </p>
     * @param requestCode
     * @param resultCode
     * @param data
     * @since 1.0.0
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 响应分享的回调
        /**使用SSO授权必须添加如下代码 */
        if (sharePopupWindow != null) {
            UMSsoHandler ssoHandler = sharePopupWindow.getUmengShareController().getConfig().getSsoHandler(requestCode);
            if (ssoHandler != null) {
                ssoHandler.authorizeCallBack(requestCode, resultCode, data);
            }
        }
    }

    /**
     * <p>
     * 通过获取到的地图信息可视化地图
     * </p>
     * @param restMap
     * @since 1.0.0
     */
    public void openMap(com.supermap.mytracks.bean.Map restMap) {
        com.supermap.mapping.Map map = this.mapControl.getMap();
        map.setMapDPI(DisplayUtil.getDPI(this));
        Workspace wokspace = new Workspace();
        map.setWorkspace(wokspace);
        map.getTrackingLayer().clear();
        calloutPoints.clear();
        // 保证多个图层叠加的顺序是正序加载，倒序会导致叠加上下层次错误
        for (int i = 0; i < restMap.getLayers().size(); i++) {
            Layer layer = restMap.getLayers().get(i);
            this.addLayer(wokspace, layer);
        }
        // 地图根据矢量和标注图层的bounds作为可见范围进行全幅。
        component.setDatas(calloutPoints);
        // 解决打开地图时，默认比例尺与范围应该与缩略图保持一致
        Rectangle2D rec2D = restMap.getExtent();
        if (rec2D != null) {
            LogUtils.d("地图默认范围是：l:" + rec2D.left + ",b:" + rec2D.bottom + ",r:" + rec2D.right + ",t:" + rec2D.top);
            map.setViewBounds(new com.supermap.data.Rectangle2D(rec2D.left, rec2D.bottom, rec2D.right, rec2D.top));
        } else {
            map.viewEntire();
        }
        // map.setAntialias(true);//设置反走样，抗锯齿
        DialogUtils.stopProgressDialog(this.dialog);
        this.initLayerView();
    }

    /**
     * <p>
     * 可视化地图中的具体图层
     * </p>
     * @param workspace
     * @param layer
     * @since 1.0.0
     */
    private void addLayer(Workspace workspace, Layer layer) {
        DatasourceConnectionInfo dsInfo = new DatasourceConnectionInfo();
        String url = layer.getUrl();
        // 矢量图层的可视化
        if (LayerType.FEATURE_LAYER.equals(layer.getLayerType())) {
            if (layer.getFeatures() != null) {
                LayerControlBean lcb = new LayerControlBean(layer.getBounds(), LayerType.FEATURE_LAYER, layer.getTitle(), layer.isIsVisible());
                if (!this.layers.contains(lcb)) {
                    this.layers.add(lcb);
                }
                featureLayers.add(layer);
                featureLayerVisibleMap.put(layer.getTitle(), layer.isIsVisible());
                if (layer.isIsVisible()) {// 可见才绘制
                    drawFeatureLayer(layer);
                }
            }
            return;
        }
        // 标注图层的可视化
        if (LayerType.MARKER_LAYER.equals(layer.getLayerType())) {
            if (layer.getMarkers() != null) {
                LayerControlBean lcb = new LayerControlBean(null, LayerType.MARKER_LAYER, layer.getTitle(), layer.isIsVisible());
                if (!this.layers.contains(lcb)) {
                    this.layers.add(lcb);
                }
                for (int i = 0; i < layer.getMarkers().size(); i++) {
                    Marker marker = layer.getMarkers().get(i);
                    if (marker != null) {
                        Geometry geometry = marker.getGeometry();
                        if (GeometryType.POINT.equals(geometry.getType())) {
                            Point2D point = geometry.getPoints()[0];
                            String icon = marker.getBigIcon();
                            calloutPoints.add(new com.supermap.data.Point2D(point.x, point.y));
                            View calloutLayout = LinearLayout.inflate(this, R.layout.mapviewactivity_callout, null);
                            ViewUtils.inject(this, calloutLayout);
                            ImageView btnMarker = (ImageView) calloutLayout.findViewById(R.id.btn_mapview_marker);

                            String markserKey = String.valueOf(btnMarker.hashCode());
                            storeMakerKey(layer.getTitle(), markserKey);
                            markerMap.put(markserKey, marker);
                            // String id = String.valueOf(geometry.getID());
                            // btnSelected.setTag(id);
                            // 详细信息弹出框
                            // btnSelected.setOnClickListener(detailClickListener);
                            CallOut callout = new CallOut(this);
                            callout.setContentView(calloutLayout); // 设置显示内容
                            callout.setCustomize(true); // 设置自定义背景图片
                            callout.setLocation(point.x, point.y);
                            // callout.setTag(id);
                            this.mapview.addCallout(callout, markserKey);
                            this.calloutAdded.put(markserKey, callout);
                            this.getMarkerBitmapUtils().display(btnMarker, icon);
                            if (!layer.isIsVisible()) {
                                callout.setVisibility(View.GONE);
                            }
                            // this.markers.add(callout);
                        }
                    }
                }
            }
            return;
        }
     // 底图和web图层的可视化
        String type = layer.getType();
        if (!TextUtils.isEmpty(type)) {
            LayerControlBean lcb = new LayerControlBean(layer.getBounds(), layer.getLayerType(), layer.getTitle(), layer.isIsVisible());
            if (layer.getLayerType() == null && layer.getZindex() == 0) {
                lcb.setLayerType(LayerType.BASE_LAYER);
            }
            this.layers.add(lcb);
            if ("SUPERMAP_REST".equals(type)) {
                dsInfo.setEngineType(EngineType.Rest);
            } else {
                dsInfo.setEngineType(EngineType.OGC);
                String layertype = this.getLayerType(url);
                dsInfo.setDriver(layertype);
            }
            dsInfo.setServer(url);
            dsInfo.setAlias(layer.getTitle());// 保证多个rest图层的别名不一样，解决别名一样发生崩溃
            Datasource ds = workspace.getDatasources().open(dsInfo);
            if (ds != null) {
                Dataset dt = ds.getDatasets().get(0);
                com.supermap.mapping.Layer lay = this.mapControl.getMap().getLayers().add(dt, true);
                if (!layer.isIsVisible()) {
                    lay.setVisible(false);
                }
                mobileLayers.put(layer.getTitle(), lay);
                this.mapControl.getMap().refresh();
            }
        }
    }

    /**
     * <p>
     * 真正绘制矢量
     * </p>
     * @param layer
     * @since 1.0.0
     */
    private void drawFeatureLayer(Layer layer) {
        for (int i = 0; i < layer.getFeatures().size(); i++) {
            Feature feature = layer.getFeatures().get(i);
            if (feature != null) {
                Geometry geometry = feature.getGeometry();
                com.supermap.data.GeoStyle style = null;
                if (geometry != null) {
                    String styleString = feature.getStyle();
                    String type = geometry.getType();
                    if (!TextUtils.isEmpty(styleString)) {
                        style = this.getStyle(styleString, type);
                    }
                    if (GeometryType.POINT.equals(type)) {
                        Point2D[] points = geometry.getPoints();
                        if (points != null) {
                            this.drawPoint(points, style);
                        }
                    } else if (GeometryType.LINE.equals(type)) {
                        Point2D[] points = geometry.getPoints();
                        if (points != null) {
                            this.drawLine(points, style);
                        }
                    } else if (GeometryType.REGION.equals(type) || "polygon".equals(type)) {
                        Point2D[] points = geometry.getPoints();
                        if (points != null) {
                            this.drawRegion(points, style);
                        }
                    }
                }
            }
        }
    }

    /**
     * <p>
     * 存储标注图层与其所有的marker
     * </p>
     * @param layerName
     * @param markserKey
     * @since 1.0.0
     */
    private void storeMakerKey(String layerName, String markserKey) {
        if (markerKeyMap == null) {
            markerKeyMap = new HashMap<String, List<String>>();
        }
        if (layerName != null) {
            if (markerKeyMap.containsKey(layerName)) {
                List<String> list = markerKeyMap.get(layerName);
                if (list != null) {
                    list.add(markserKey);
                } else {
                    list = new ArrayList<String>();
                    list.add(markserKey);
                    markerKeyMap.put(layerName, list);
                }
            } else {
                List<String> list = new ArrayList<String>();
                list.add(markserKey);
                markerKeyMap.put(layerName, list);
            }
        }
    }

    /**
     * <p>
     * 点击marker触发的事件
     * </p>
     * @param view
     * @since 1.0.0
     */
    @OnClick(R.id.btn_mapview_marker)
    private void markerClick(View view) {
        this.mapview.removeCallOut(VIEW_MARKER);
        markerClickedKey = String.valueOf(view.hashCode());
        Marker marker = markerMap.get(markerClickedKey);
        if (marker == null) {
            return;
        }
        if (preMarker != null) {
            this.getMarkerBitmapUtils().display(preMarker, preIcon);
        }
        preMarker = (ImageView) view;
        preIcon = marker.getBigIcon();
        this.getMarkerPressedBitmapUtils().display(view, marker.getIconPressed());
        if (this.marker_detail == null) {
            this.marker_detail = LinearLayout.inflate(this, R.layout.marker_detail, null);
            marker_title = (TextView) marker_detail.findViewById(R.id.marker_title);
            marker_desc = (TextView) marker_detail.findViewById(R.id.marker_desc);
            // 设置textView可以滚动
            marker_desc.setMovementMethod(ScrollingMovementMethod.getInstance());
        }

        point_marker = marker.getGeometry().getPoints()[0];
        Point p = this.mapview.getMapControl().getMap().mapToPixel(new com.supermap.data.Point2D(point_marker.getX(), point_marker.getY()));
        this.marker_height = view.getMeasuredHeight();
        p.setY((int) (p.getY() - this.marker_height));
        com.supermap.data.Point2D point1 = this.mapview.getMapControl().getMap().pixelToMap(p);

        if (TextUtils.isEmpty(marker.getAttributes().getTitle())) {
            marker_title.setText("暂无描述");
        } else {
            marker_title.setText(marker.getAttributes().getTitle());
        }

        if (TextUtils.isEmpty(marker.getAttributes().getDescription())) {
            marker_desc.setText("暂无描述");
        } else {
            marker_desc.setText(marker.getAttributes().getDescription());
        }
        if (this.popu_callout == null) {
            popu_callout = new CallOut(this);
            popu_callout.setContentView(marker_detail);
            popu_callout.setCustomize(true);

            this.popu_marker.add(popu_callout);
        }
        popu_callout.setLocation(point1.getX(), point1.getY());
        popu_callout.setVisibility(View.VISIBLE);
        this.mapview.addCallout(popu_callout, VIEW_MARKER);

    }

    /**
     * <p>
     * 响应图层控制弹出框的显示和隐藏
     * </p>
     * @param view
     * @since 1.0.0
     */
    @OnClick(R.id.ibtn_map_layercontrol)
    private void layerCtrlClick(View view) {
        if (this.layerWindow == null) {
            if (this.layersView == null) {
                return;
            }
            this.layerWindow = new PopupWindow(this.layersView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            // popuWindow必须设置背景才能在点击外部时消失，否则只能手动调用dismiss方法
            this.layerWindow.setBackgroundDrawable(new ColorDrawable(0x00ffffff));
            this.layerWindow.setAnimationStyle(android.R.style.Animation_Dialog);
            this.layerWindow.setFocusable(true);
            this.layerWindow.setTouchable(true);
        }
        if (this.layerWindow.isShowing()) {
            this.layerWindow.dismiss();
        } else {
            this.layerWindow.showAtLocation(this.mapview, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        }
    }

    /**
     * <p>
     * 初始化图层控制弹出框视图
     * </p>
     * @since 1.0.0
     */
    private void initLayerView() {
        if (this.layers == null || this.layers.size() == 0) {
            return;
        }
        this.layersView = FrameLayout.inflate(this, R.layout.map_layercontrol, null);
        ListView layersListView = (ListView) layersView.findViewById(R.id.lv_map_layers);
        layerAdapter = new LayerAdapter(this);
        layerAdapter.setLayers(this.layers);
        layerAdapter.setHandler(handler);
        layersListView.setAdapter(layerAdapter);

        // this.mapview.getMapControl().getMap().viewEntire();
        this.mapview.getMapControl().getMap().refresh();
    }

    /**
     * <p>
     * 响应图层显示和隐藏
     * </p>
     * @param bean
     * @since 1.0.0
     */
    @OnItemClick(R.id.lv_map_layers)
    private void layerClick(LayerControlBean bean) {
        if (bean == null) {
            return;
        }
        // LayerAdapter.ViewHolder viewHolder = (ViewHolder) itemView.getTag();
        if (LayerType.FEATURE_LAYER.equals(bean.getLayerType())) {
            // this.featureViasble = !this.featureViasble;
            if (featureLayers != null && featureLayers.size() > 0 && featureLayerVisibleMap != null && featureLayerVisibleMap.size() > 0) {
                mapControl.getMap().getTrackingLayer().clear();
                if (featureLayerVisibleMap.containsKey(bean.getLayerName())) {
                    featureLayerVisibleMap.put(bean.getLayerName(), bean.isVisible());
                }
                for (int i = 0; i < featureLayers.size(); i++) {
                    Layer l = featureLayers.get(i);
                    if (l != null && featureLayerVisibleMap.containsKey(l.getTitle()) && featureLayerVisibleMap.get(l.getTitle())) {
                        if (l.getFeatures() != null) {
                            drawFeatureLayer(l);
                        }
                    }
                }
            }
        } else if (LayerType.MARKER_LAYER.equals(bean.getLayerType())) {
            List<String> markerKeys = null;
            if (markerKeyMap != null && this.markerKeyMap.size() > 0) {
                String title = bean.getLayerName();
                if (markerKeyMap.containsKey(title)) {
                    markerKeys = markerKeyMap.get(title);
                }
            }
            int visiable = View.VISIBLE;
            if (!bean.isVisible()) {
                visiable = View.GONE;
            }

            if (popu_callout != null && markerKeys != null && markerKeys.contains(markerClickedKey)) {
                popu_callout.setVisibility(visiable);
            }
            if (markerKeys != null && calloutAdded != null && calloutAdded.size() > 0) {
                Set<Entry<String, CallOut>> entrySet = calloutAdded.entrySet();
                Iterator<Entry<String, CallOut>> it = entrySet.iterator();
                while (it.hasNext()) {
                    Entry<String, CallOut> en = it.next();
                    if (markerKeys.contains(en.getKey())) {
                        en.getValue().setVisibility(visiable);
                    }
                }
            }
        } else if (LayerType.OVERLAY_LAYER.equals(bean.getLayerType())) {
            if (mobileLayers != null && mobileLayers.containsKey(bean.getLayerName())) {
                com.supermap.mapping.Layer l = mobileLayers.get(bean.getLayerName());
                if (l != null) {
                    l.setVisible(bean.isVisible());
                }
            }
        }
        this.mapControl.getMap().refresh();
    }

    /**
     * <p>
     * 获取web图层是OGC服务的类型
     * </p>
     * @param url
     * @return
     * @since 1.0.0
     */
    private String getLayerType(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (url.contains("wmts") || url.contains("WMTS")) {
                return "WMTS";
            }
            if (url.contains("wms") || url.contains("WMS")) {
                return "WMS";
            }
        }
        return null;
    }

    /**
     * <p>
     * 获取相应矢量绘制的风格
     * </p>
     * @param styleString
     * @param type
     * @return
     * @since 1.0.0
     */
    private com.supermap.data.GeoStyle getStyle(String styleString, String type) {
        com.supermap.data.GeoStyle geoStyle = null;
        if (!TextUtils.isEmpty(styleString)) {
            geoStyle = new com.supermap.data.GeoStyle();
            GeoStyle style = JSON.parseObject(styleString, GeoStyle.class);
            if (style != null) {
                int strColorValue = -1;
                int fillColorValue = -1;
                if (style.getStrokeColor() != null) {
                    String strColor = style.getStrokeColor().substring(1);
                    strColorValue = Integer.parseInt(strColor, 16);
                }
                if (style.getFillColor() != null) {
                    String fillColor = style.getFillColor().substring(1);
                    fillColorValue = Integer.parseInt(fillColor, 16);
                }
                if (GeometryType.POINT.equals(type)) {
                    int radius = style.getPointRadius();
                    geoStyle.setMarkerSize(new Size2D(radius * 4, radius * 4));
                    if (fillColorValue != -1) {
                        geoStyle.setLineColor(new Color(fillColorValue));
                    }
                } else if (GeometryType.LINE.equals(type)) {
                    if (strColorValue != -1) {
                        geoStyle.setLineColor(new Color(strColorValue));
                    }
                } else if (GeometryType.REGION.equals(type) || "polygon".equals(type)) {
                    if (strColorValue != -1) {
                        geoStyle.setLineColor(new Color(strColorValue));
                    }
                    if (fillColorValue != -1) {
                        geoStyle.setFillForeColor(new Color(fillColorValue));
                    }
                    geoStyle.setFillOpaqueRate((new Float(style.getFillOpacity() * 100)).intValue());
                }
                geoStyle.setLineWidth(Double.parseDouble(String.valueOf(style.getStrokeWidth())) / 4.0);
            }
        }
        return geoStyle;
    }

    /**
     * <p>
     * 绘制地图中矢量图层的点
     * </p>
     * @param points 点集合
     * @param style 绘制风格
     * @since 1.0.0
     */
    private void drawPoint(Point2D[] points, com.supermap.data.GeoStyle style) {
        if (points != null && points.length > 0) {
            TrackingLayer trackingLayer = this.mapControl.getMap().getTrackingLayer();
            for (int i = 0; i < points.length; i++) {
                GeoPoint point = new GeoPoint(points[i].x, points[i].y);
                point.setStyle(style);
                trackingLayer.add(point, GeometryType.POINT);
            }
            this.mapControl.getMap().refresh();
        }

    }

    /**
     * <p>
     * 绘制地图中矢量图层的线
     * </p>
     * @param points
     * @param style
     * @since 1.0.0
     */
    private void drawLine(Point2D[] points, com.supermap.data.GeoStyle style) {
        if (points != null && points.length > 0) {
            com.supermap.data.Point2D[] pointArray = new com.supermap.data.Point2D[points.length];
            for (int i = 0; i < points.length; i++) {
                pointArray[i] = new com.supermap.data.Point2D(points[i].x, points[i].y);
            }
            TrackingLayer trackingLayer = this.mapControl.getMap().getTrackingLayer();
            // trackingLayer.clear();
            Point2Ds point2Ds = new Point2Ds(pointArray);
            GeoLine line = new GeoLine(point2Ds);
            line.setStyle(style);
            trackingLayer.add(line, GeometryType.LINE);
            this.mapControl.getMap().refresh();
        }

    }

    /**
     * <p>
     * 绘制地图中矢量图层的面
     * </p>
     * @param points
     * @param style
     * @since 1.0.0
     */
    private void drawRegion(Point2D[] points, com.supermap.data.GeoStyle style) {
        if (points != null && points.length > 0) {
            com.supermap.data.Point2D[] pointArray = new com.supermap.data.Point2D[points.length];
            for (int i = 0; i < points.length; i++) {
                pointArray[i] = new com.supermap.data.Point2D(points[i].x, points[i].y);
            }
            TrackingLayer trackingLayer = this.mapControl.getMap().getTrackingLayer();
            // trackingLayer.clear();
            Point2Ds point2Ds = new Point2Ds(pointArray);
            GeoRegion region = new GeoRegion(point2Ds);
            region.setStyle(style);
            trackingLayer.add(region, GeometryType.REGION);
            this.mapControl.getMap().refresh();
        }
    }

    private BitmapUtils getMarkerBitmapUtils() {
        if (this.bitmapUtils == null) {
            this.bitmapUtils = new BitmapUtils(this);
        }
        this.bitmapUtils.configDefaultLoadFailedImage(R.drawable.btn_map_marker);
        return this.bitmapUtils;
    }

    private BitmapUtils getMarkerPressedBitmapUtils() {
        if (this.bitmapUtils == null) {
            this.bitmapUtils = new BitmapUtils(this);
        }
        this.bitmapUtils.configDefaultLoadFailedImage(R.drawable.btn_map_marker_pressed);
        return this.bitmapUtils;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sharePopupWindow != null && sharePopupWindow.isShowing()) {
            sharePopupWindow.dismiss();
        }
        if (mapview != null) {
            mapview.removeAllCallOut();
        }
        if (mapControl != null) {
            mapControl.getMap().getTrackingLayer().clear();
            mapControl.getMap().getLayers().clear();
            mapControl.getMap().refresh();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (sharePopupWindow != null && sharePopupWindow.isShowing()) {
                sharePopupWindow.dismiss();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (sharePopupWindow != null && sharePopupWindow.isShowing()) {
            sharePopupWindow.dismiss();
        }
        super.onBackPressed();
    }

    /**
     * 从Path中截取地图的id
     * @return
     */
    public int getMapId() {
        return Integer.valueOf(this.path.substring(10));// 去掉"/web/maps/"，取后面的地图id
    }

    /**
     * <p>
     * 重新布局点击marker后弹出的气泡窗口
     * </p>
     * @since 1.0.0
     */
    public void refreshMarkerView() {
        if (point_marker != null && popu_callout != null) {
            Point p = mapview.getMapControl().getMap().mapToPixel(new com.supermap.data.Point2D(point_marker.getX(), point_marker.getY()));
            p.setY((int) (p.getY() - marker_height));
            com.supermap.data.Point2D point1 = mapview.getMapControl().getMap().pixelToMap(p);
            popu_callout.setLocation(point1.getX(), point1.getY());
        }
    }
}
