package com.supermap.mytracks.activity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.http.entity.StringEntity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
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
import com.supermap.mapping.CallOut;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.TrackingLayer;
import com.supermap.mytracks.R;
import com.supermap.mytracks.adapter.LayerControlAdapter;
import com.supermap.mytracks.adapter.MapLayersAdapter;
import com.supermap.mytracks.bean.Attributes;
import com.supermap.mytracks.bean.AuthorizeSetting;
import com.supermap.mytracks.bean.Content;
import com.supermap.mytracks.bean.Feature;
import com.supermap.mytracks.bean.GeoStyle;
import com.supermap.mytracks.bean.Geometry;
import com.supermap.mytracks.bean.GeometryType;
import com.supermap.mytracks.bean.Layer;
import com.supermap.mytracks.bean.LayerControlBean;
import com.supermap.mytracks.bean.LayerType;
import com.supermap.mytracks.bean.Maps;
import com.supermap.mytracks.bean.Marker;
import com.supermap.mytracks.bean.Point2D;
import com.supermap.mytracks.bean.Rectangle2D;
import com.supermap.mytracks.bean.RequestParam;
import com.supermap.mytracks.bean.WebLayer;
import com.supermap.mytracks.common.Params;
import com.supermap.mytracks.common.Paths;
import com.supermap.mytracks.common.URLs;
import com.supermap.mytracks.customUI.CustomProgressDialog;
import com.supermap.mytracks.customUI.GridViewWithHF;
import com.supermap.mytracks.customUI.MapComponent;
import com.supermap.mytracks.utils.AlertUtil;
import com.supermap.mytracks.utils.CommonUtil;
import com.supermap.mytracks.utils.DialogUtils;
import com.supermap.mytracks.utils.DisplayUtil;
import com.supermap.mytracks.utils.HttpExceptionHandler;
import com.supermap.mytracks.utils.HttpUtil;
import com.supermap.mytracks.utils.MapsActive;

/**
 * <p>
 * 创建(草绘)简单地图
 * </p>
 * @author ${huangqh}
 * @version ${Version}
 * @since 1.0.0
 * 
 */
public class CreateMapActivity extends BaseActivity {
    public static final String VIEW_MARKER = "markerView";
    private MapLayersAdapter mlAdapter = null;
    private RequestCallBack<String> mapCallBack = null;
    private RequestCallBack<String> mapsCallBack = null;
    private FrameLayout parent = null;
    private com.supermap.mytracks.bean.Map map = null;
    // private String path = "/web/maps/2";// 默认底图
    private MapComponent component = null;
    private MapView mapview = null;
    private MapControl mapControl = null;
    private CustomProgressDialog dialog = null;
    private BitmapUtils bitmapUtils = null;
    @ViewInject(R.id.tv_map_title)
    private TextView tv_map_title;
    @ViewInject(R.id.ll_marker_view)
    private LinearLayout ll_marker_view;
    @ViewInject(R.id.ll_layers_view)
    private LinearLayout ll_layers_view;
    @ViewInject(R.id.ll_map_options_view)
    private LinearLayout ll_map_options_view;
    @ViewInject(R.id.lv_map_options)
    private ListView lv_map_options;
    @ViewInject(R.id.gv_layers_grid)
    private GridViewWithHF gv_layers_grid;
    @ViewInject(R.id.tv_layers_title)
    private TextView tv_layers_title;
    private List<LayerControlBean> layers = new ArrayList<LayerControlBean>();
    private List<com.supermap.data.Point2D> calloutPoints = new ArrayList<com.supermap.data.Point2D>();
    private View bg_gray; // 灰色背景，当弹出分享面板是显示
    private View marker_detail = null;
    private Map<String, Marker> markerMap = new HashMap<String, Marker>();
    private EditText marker_title = null;
    private EditText marker_desc = null;
    private ImageView iv_marker_clear;
    private Button bn_marker_pos;
    private Button bn_marker_neg;
    private boolean initView = false;
//    private ImageView preMarker; // 上一个被选中的markerView
//    private String preIcon; // 上一个被选中的marker
    private CallOut popu_callout = null;
    private Point2D point_marker = null;
    private int marker_height;
    private List<Layer> addMarkerLayers = new ArrayList<Layer>();
    private List<Layer> addWebLayers = new ArrayList<Layer>();
    private int zIndex = 0;
    private boolean isAddMarker;// 添加完一次就置为false
    private boolean isMapOpened;
    private int markerIconId = -1;
    private int epsgCode = -1;
    private int oldEpsgCode = -1;// 切换底图后epsgCode发生变化则重新发送获取可以被叠加的web图层
    protected String webLayerTitle;
    private List<WebLayer> webLayers = new ArrayList<WebLayer>();
    private List<WebLayer> baseLayers = new ArrayList<WebLayer>();
    private MapsActive mapsActive;
    private long clickTime;
    private Marker markerClicked;
    private Map<String, CallOut> calloutAdded = new HashMap<String, CallOut>();
    private boolean isGetBaseLayers = false;
    private boolean isCreateMaping = false;// 防止多次点击创建地图创建多次地图
    private boolean isGetDefBaseLayer = true;
    private final String options[] = new String[] { "添加标注", "添加web图层", "切换底图", "保存设置" };
    private final String options1[] = new String[] { "缩放至", "重命名", "删除" };
    private AlertDialog alertDialog;
    private String saveMapId;
    // private PopupWindow layerWindow = null;
    private View layersView = null;
    private LayerControlAdapter layerAdapter = null;
    private boolean featureViasble = true;
//    private boolean markerViasble = true;
    // key是marker图层名，value是图层中所有marker的标签名，便于控制图层的显示和隐藏以及删除图层
    private Map<String, List<String>> markerKeyMap = new HashMap<String, List<String>>();
    private String markerClickedKey;
    // 为了显示和隐藏web图层的时候方便
    private Map<String, com.supermap.mapping.Layer> mobileLayers = new HashMap<String, com.supermap.mapping.Layer>();
    private View view;
    private LayerControlBean layerBeanSeleted;
    private String baseLayerName;
    private List<String> layerTitleList = new ArrayList<String>();// 保证图层重名使用
    private String describe;
    private String mapTag;
    private String mapTitle;
    private long lastTime;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        context = this;
        this.parent = new FrameLayout(this);
        this.setContentView(this.parent);
        dialog = CustomProgressDialog.createDialog(this);
        this.dialog.setContentView(R.layout.progress_custom);
        dialog = DialogUtils.startProgressDialog(this, dialog, "正在请求...");
        mapCallBack = new GetMapCallBack();
        mapsCallBack = new GetMapsCallBack();
        if (!Params.isInitLicense) {
            this.init();
        } else {
            this.initView();
        }
    }

    /**
     * <p>
     * 初始化界面控件及布局
     * </p>
     * @since 1.0.0
     */
    private void initView() {
        if (!this.initView) {
            this.component = new MapComponent(this);
            if (component.getParent() != null) {
                FrameLayout p = (FrameLayout) component.getParent();
                p.removeView(component);
            }
            parent.addView(component);
            this.mapview = component.getMapView();
            this.mapControl = this.mapview.getMapControl();
            LinearLayout.inflate(this, R.layout.map_create_top, parent);

            // 增加灰色背景，当弹出分享面板时使用
            bg_gray = new View(this);
            LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            bg_gray.setLayoutParams(params);
            bg_gray.setBackgroundColor(getResources().getColor(R.color.bg_half_transparent));
            bg_gray.setVisibility(View.GONE);
            parent.addView(bg_gray);
            // 增加Marker列表添加的视图
            FrameLayout.inflate(this, R.layout.btn_markers, parent);
            // 增加web图层列表添加的视图
            LayoutInflater.from(this).inflate(R.layout.btn_map_layers, parent);
            // 增加创建地图时选择切换底图、添加标注和web图层的选项卡
            LayoutInflater.from(this).inflate(R.layout.btn_map_options, parent);

            ViewUtils.inject(this, parent);
            this.initView = true;
            // switchBasedLayer(this.path);
            getDefBaseLayer();
            ll_marker_view.setVisibility(View.GONE);// 默认Marker列表视图不可见
            ll_layers_view.setVisibility(View.GONE);// 默认web图层列表视图不可见
            ll_map_options_view.setVisibility(View.GONE);// 默认选项卡 列表视图不可见
            lv_map_options.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                    if (pos == 0) {
                        // 弹出marker
                        // 弹出输入标注图层的title，填完后可以添加标注，设置可见
                        ll_map_options_view.setVisibility(View.GONE);
                        ll_marker_view.setVisibility(View.GONE);
                        if (isMapOpened) {
                            String titleName = getUniqueTitle("标注图层名称");// 判断是否同名图层已经存在;
                            showTitleSetDialog("MAKERLAYER", titleName);
                        }
                    } else if (pos == 1) {
                        ll_map_options_view.setVisibility(View.GONE);
                        // 弹出可添加的图层列表
                        if (isMapOpened) {
                            isGetBaseLayers = false;
                            getWebLayers();
                        }
                    } else if (pos == 2) {
                        ll_map_options_view.setVisibility(View.GONE);
                        // 弹出切换地图图层列表
                        if (isMapOpened) {
                            if ((addWebLayers != null && addWebLayers.size() > 0) || (addMarkerLayers != null && addMarkerLayers.size() > 0)) {
                                Toast.makeText(CreateMapActivity.this, "已添加其他图层，不支持切换底图。", Toast.LENGTH_SHORT).show();
                            } else {
                                getBaseLayers();
                            }
                        }
                    } else if (pos == 3) {
                        ll_map_options_view.setVisibility(View.GONE);
                        // 弹出地图设置窗口
                        if (isMapOpened) {
                            showMapMesSetDialog(false);
                        }
                    }
                }
            });
            lv_map_options.setAdapter(new SimpleAdapter(this, getSimpleAdapterData(), R.layout.btn_map_options_item, new String[] { "option" },
                    new int[] { R.id.tv_map_options_item }));
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
                        removePopupCallOut();
                        if (isAddMarker) {
                            // 绘制marker
                            com.supermap.data.Point2D p = mapControl.getMap().pixelToMap(new Point((int) ev.getRawX(), (int) ev.getRawY()));
                            addMarker(new Point2D(p.getX(), p.getY()));
                            isAddMarker = false;
                        }
                    }
                    break;
                case MotionEvent.ACTION_DOWN:
                    ll_map_options_view.setVisibility(View.GONE);
                    closeLayerControlView();
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
     * 地图操作的简单列表选项适配器
     * </p>
     * @return
     * @since 1.0.0
     */
    private List<Map<String, Object>> getSimpleAdapterData() {
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < options.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("option", options[i]);
            data.add(map);
        }
        return data;
    }

    /**
     * <p>
     * 地图图层控制的简单列表选项适配器
     * </p>
     * @return
     * @since 1.0.0
     */
    private List<Map<String, Object>> getSimpleAdapterData1() {
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < options1.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("option", options1[i]);
            data.add(map);
        }
        return data;
    }

    @OnClick({ R.id.iv_map_back, R.id.iv_map_publish, R.id.iv_map_save, R.id.iv_marker_red, R.id.iv_marker_blue, R.id.iv_marker_purple, R.id.iv_layers_close,
            R.id.iv_map_more, R.id.iv_map_layercontrol })
    private void onClick(View v) {
        switch (v.getId()) {
        case R.id.iv_map_back:
            finish();
            break;
        case R.id.iv_map_publish:
            // 发布地图
            if (mapTag != null && !"".equals(mapTag)) {
                sendCreateMap("PUBLISHED");
            } else {
                Toast.makeText(this, "请先保存地图！", Toast.LENGTH_SHORT).show();
            }
//            if (mapTag != null && !"".equals(mapTag)) {
//                sendCreateMap("PUBLISHED");
//            } else {
//                showMapMesSetDialog(true);
//            }
//            closeLayerControlView();
            break;
        case R.id.iv_map_save:
            // 保存地图
            if (mapTag != null && !"".equals(mapTag)) {
                sendCreateMap("SAVED");
            } else {
                showMapMesSetDialog(true);
            }
            closeLayerControlView();
            break;
        case R.id.iv_marker_red:
            // 添加红色标注
            isAddMarker = true;
            markerIconId = 2;
            break;
        case R.id.iv_marker_blue:
            // 添加蓝色标注
            isAddMarker = true;
            markerIconId = 1;
            break;
        case R.id.iv_marker_purple:
            // 添加紫色标注
            isAddMarker = true;
            markerIconId = 0;
            break;
        case R.id.iv_layers_close:// 关闭web图层列表视图
            ll_layers_view.setVisibility(View.GONE);
            break;
        case R.id.iv_map_more:// 打开选项卡列表
            ll_map_options_view.setVisibility(View.VISIBLE);
            closeLayerControlView();
            break;
        case R.id.iv_map_layercontrol:// 打开图层控制窗口
            layerCtrlClick();
            break;
        }
    }

    /**
     * <p>
     * 隐藏图层控制窗口界面
     * </p>
     * @since 1.0.0
     */
    private void closeLayerControlView() {
        if (view != null) {
            view.setVisibility(View.GONE);
        }
        if (layersView != null) {
            layersView.setVisibility(View.GONE);
        }
    }

    /**
     * <p>
     * 点击图层控制按钮的处理
     * </p>
     * @since 1.0.0
     */
    private void layerCtrlClick() {
        if (this.layersView == null) {
            return;
        }
        if (this.layersView.isShown()) {
            closeLayerControlView();
        } else {
            this.layersView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * <p>
     * 删除已添加到标注图层中的Marker对象
     * </p>
     * @since 1.0.0
     */
    private void deleteMarker() {
        if (markerClicked != null) {
            String layerTitle = null;
            if (markerMap.containsValue(markerClicked)) {
                if (addMarkerLayers != null && addMarkerLayers.size() > 0) {
                    for (int i = 0; i < addMarkerLayers.size(); i++) {
                        Layer layer = addMarkerLayers.get(i);
                        if (layer != null && layer.getMarkers() != null) {
                            if (layer.getMarkers().remove(markerClicked)) {
                                layerTitle = layer.getTitle();
                                break;
                            }
                        }
                    }
                }

                String key = null;
                Set<Entry<String, Marker>> entrySet = markerMap.entrySet();
                Iterator<Entry<String, Marker>> it = entrySet.iterator();
                while (it.hasNext()) {
                    Entry<String, Marker> entry = it.next();
                    if (entry.getValue().equals(markerClicked)) {
                        key = entry.getKey();
                        break;
                    }
                }
                if (key != null) {
                    this.calloutAdded.remove(key);
                    markerMap.remove(key);
                    mapview.removeCallOut(key);
                    if (layerTitle != null && markerKeyMap != null && markerKeyMap.containsKey(layerTitle)) {
                        List<String> list = markerKeyMap.get(layerTitle);
                        if (list != null) {
                            list.remove(key);
                        }
                    }
                }
                removePopupCallOut();
            }

        }
    }

    /**
     * <p>
     * 去掉marker响应点击事件弹出的气泡框
     * </p>
     * @since 1.0.0
     */
    private void removePopupCallOut() {
        CallOut callout = mapview.getCallOut(VIEW_MARKER);
        if (callout != null && callout.isShown()) {
            mapview.removeCallOut(VIEW_MARKER);
        }
    }

    /**
     * <p>
     * 发送创建地图的请求
     * </p>
     * @param status
     * @since 1.0.0
     */
    private void sendCreateMap(String status) {
        if (!isMapOpened) {
            Toast.makeText(this, "底图正在初始化中...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isCreateMaping) {
            Toast.makeText(this, "地图正在创建中...", Toast.LENGTH_SHORT).show();
            return;
        }
        // 构建post请求的请求体后发送请求
        try {
            com.supermap.mytracks.bean.Map map = getMapEntity(status);
            if (map != null) {
                HttpRequest.HttpMethod method = HttpRequest.HttpMethod.POST;
                String path = URLs.getUrl("/web/maps");
                if (saveMapId != null && (!"".equals(saveMapId))) {
                    method = HttpRequest.HttpMethod.PUT;
                    path = URLs.getUrl("/web/maps/" + saveMapId);
                    map.setId(Integer.parseInt(saveMapId));
                }
                String entityStr = JSON.toJSONString(map);
                StringEntity entity = new StringEntity(entityStr, "UTF-8");
                entity.setContentType("application/json");
                isCreateMaping = true;
                HttpUtil.sendRequestByEntity(method, path, null, entity, new CreateMapCallBack(status));
            }
        } catch (Exception e) {
            e.printStackTrace();
            isCreateMaping = false;
        }
    }

    /**
     * <p>
     * 获取创建地图的请求体内容
     * </p>
     * @param status 
     * @return
     * @since 1.0.0
     */
    private com.supermap.mytracks.bean.Map getMapEntity(String status) {
        com.supermap.mytracks.bean.Map map = new com.supermap.mytracks.bean.Map();
        if ("PUBLISHED".equalsIgnoreCase(status)) {
            List<AuthorizeSetting> authorizeSettings = new ArrayList<AuthorizeSetting>();
            AuthorizeSetting as = new AuthorizeSetting();
            as.setPermissionType("READ");
            as.setEntityName("GUEST");
            as.setEntityType("USER");
            authorizeSettings.add(as);
            map.setAuthorizeSetting(authorizeSettings);
        }
        map.setStatus("PUBLISHED");// SAVED
        if (tv_map_title.getText() != null) {
            map.setTitle(tv_map_title.getText().toString());// 用户输入
        } else {
            map.setTitle("未命名地图");// 用户输入
        }
        
        if (describe != null && !"".equals(describe)) {
            map.setDescription(describe);// 用户输入
        } else {
            map.setDescription("地图描述信息");// 用户输入
        }
        if (epsgCode != -1) {
            map.setEpsgCode(epsgCode);// 底图
        } else {
            map.setEpsgCode(3857);// 底图
        }
        com.supermap.data.Point2D cp = this.mapControl.getMap().getCenter();
        map.setCenter(new Point2D(cp.getX(), cp.getY()));// imobile获取
        com.supermap.data.Rectangle2D vb = this.mapControl.getMap().getViewBounds();
        map.setExtent(new Rectangle2D(vb.getLeft(), vb.getBottom(), vb.getRight(), vb.getTop()));// imobile获取
        List<String> tags = new ArrayList<String>();
        if (mapTag != null && !"".equals(mapTag)) {
            if (mapTag.contains("，")) {
                String[] subs = mapTag.split("，");
                if (subs != null && subs.length > 0) {
                    for (String s : subs) {
                        if (s != null && !"".equals(s.trim())) {
                            tags.add(s);
                        }
                    }
                } else {
                    tags.add(mapTag);
                }
            } else {
                tags.add(mapTag);
            }
        } else {
            tags.add("地图标签");// 用户输入
        }
        map.setTags(tags);
        map.setSourceType("MAPVIEWER");
        // map.setUserName("");//当前用户名
        double scale = this.mapControl.getMap().getScale();
        map.setScale(scale);// 设置分辨率和比例尺
        double res = 0.0254 / scale / this.mapControl.getMap().getMapDPI();// 0.0254000508/dpi/scale
        if (this.epsgCode == 4326) {
            res = res / 3.141592653589793D / 6378137.0D * 180.0D;
        }
        map.setResolution(res);// 创建缩列图需要这个参数，必传
        // 初始化图层
        map.setLayers(new ArrayList<Layer>());
        // getBasedLayer();
        Layer basedLayer = getBasedLayer();
        map.getLayers().add(basedLayer);
        map.setThumbnail(basedLayer.getUrl() + "/entireImage.png");
        if (addWebLayers != null && addWebLayers.size() > 0) {
            for (Layer l : addWebLayers) {
                map.getLayers().add(l);
            }
        }

        if (addMarkerLayers != null && addMarkerLayers.size() > 0) {
            for (Layer l : addMarkerLayers) {
                map.getLayers().add(l);
            }
        }
        // if (addMarkerLayer != null) {
        // map.getLayers().add(addMarkerLayer);
        // }
        return map;
    }

    /**
     * <p>
     * 获取构建地图的底图信息
     * </p>
     * @return
     * @since 1.0.0
     */
    private Layer getBasedLayer() {
        // 使用map填充
        Layer basedLayer = new Layer();
        basedLayer.setTitle("China");
        basedLayer.setUrl(URLs.getURL() + "/services/map-china400/rest/maps/China");
        basedLayer.setBounds(new Rectangle2D(-20037508.3427892, -20037508.3427891, 20037508.3427892, 20037508.3427891));// 请求获取到的
        basedLayer.setType("SUPERMAP_REST");
        if (map != null && map.getLayers() != null && map.getLayers().size() > 0) {
            Layer layer = map.getLayers().get(0);
            if (layer != null) {
                basedLayer.setTitle(layer.getTitle());
                basedLayer.setUrl(layer.getUrl());
                basedLayer.setBounds(layer.getBounds());// 请求获取到的
                basedLayer.setType(layer.getType());
            }
        }
        if (baseLayerName != null && !"".equals(baseLayerName)) {
            basedLayer.setTitle(baseLayerName);
        }
        basedLayer.setLayerType(LayerType.BASE_LAYER);
        basedLayer.setZindex(0);
        return basedLayer;
    }

    /**
     * <p>
     * 创建地图请求返回的回调类
     * </p>
     * @author ${Author}
     * @version ${Version}
     * @since 1.0.0
     * 
     */
    class CreateMapCallBack extends RequestCallBack<String> {
        private String status;

        public CreateMapCallBack(String status) {
            super();
            this.status = status;
        }

        @Override
        public void onFailure(HttpException arg0, String arg1) {
            String msg = "发布地图失败!";
            if ("SAVED".equalsIgnoreCase(status)) {
                msg = "保存地图失败!";
            }
            Toast.makeText(CreateMapActivity.this, msg, Toast.LENGTH_SHORT).show();
            isCreateMaping = false;
        }

        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            String msg = "发布地图成功!";
            if ("SAVED".equalsIgnoreCase(status)) {
                msg = "保存地图成功!";
            }
            Toast.makeText(CreateMapActivity.this, msg, Toast.LENGTH_SHORT).show();
            isCreateMaping = false;
            JSONObject result = JSONObject.parseObject(responseInfo.result);
         // 获取创建新地图的id或是 URI,创建地图成功就跳转到查看地图页面
            if (result != null && result.containsKey("newResourceID")) {// newResourceLocation创建新地图的 URI
                String mapId = result.getString("newResourceID");
                if ("SAVED".equalsIgnoreCase(status)) {// 创建地图并保存成功还停留当前页面可以继续修改再次保存
                    saveMapId = mapId;
                } else {// 创建地图并共享成功就跳转到查看地图页面
                    forward("/web/maps/" + mapId);
                    CreateMapActivity.this.finish();
                }
            } else {
                // 再次保存不做处理，修改保存后的地图共享成功跳转
                if (saveMapId != null && !"".equals(saveMapId) && (!"SAVED".equalsIgnoreCase(status))) {
                    forward("/web/maps/" + saveMapId);
                    CreateMapActivity.this.finish();
                }
            }
//            if (result != null && result.containsKey("newResourceID")) {// newResourceLocation创建新地图的 URI
//                saveMapId = result.getString("newResourceID");
//            }
            
        }

    }

    /**
     * <p>
     * 跳转到查看地图页面，并提供地图的地址参数
     * </p>
     * @param path
     * @since 1.0.0
     */
    private void forward(String path) {
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    
    /**
     * <p>
     * 获取地图信息请求返回的回调类
     * </p>
     * @author ${Author}
     * @version ${Version}
     * @since 1.0.0
     * 
     */
    class GetMapCallBack extends RequestCallBack<String> {

        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            String result = responseInfo.result;
            map = JSON.parseObject(result, com.supermap.mytracks.bean.Map.class);
            if (map != null) {
                // tv_map_title.setText(map.getTitle());
                // oldEpsgCode = epsgCode;
                epsgCode = map.getEpsgCode();
                openMap(map);
                isMapOpened = true;
//                showTitleSetDialog("MAP", tv_map_title.getText().toString());
            }
        }

        @Override
        public void onFailure(HttpException error, String msg) {
            HttpExceptionHandler.handleExption(error, true, context);
            DialogUtils.stopProgressDialog(dialog);
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
            case 3:
                initView();// 环境（使用gis移动端产品相关的一些初始化）初始化完成后再初始化界面
                break;
            case 9:// 图层控制界面中更多按钮所触发的处理实现
                if (msg.getData() != null && (msg.getData().getSerializable("layerBean") instanceof LayerControlBean)) {
                    LayerControlBean layerBean = (LayerControlBean) msg.getData().getSerializable("layerBean");
                    if (layerBean != null && layerBean.equals(layerBeanSeleted)) {//
                        if (view != null) {
                            if (view.isShown()) {
                                view.setVisibility(View.GONE);
                            } else {
                                view.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        layerBeanSeleted = layerBean;
                        showOptionView(msg.getData().getInt("locationX"), msg.getData().getInt("locationY"));
                    }
                }
                break;
            }
        }
    };    

    /**
     * <p>
     * 点击图层控制界面中 更多 按钮后弹出相关操作选项列表
     * </p>
     * @param x 操作选项列表布局的左上的坐标x
     * @param y 操作选项列表布局的左上的坐标y
     * 
     * @since 1.0.0
     */
    private void showOptionView(int x, int y) {
        if (view == null) {
            view = FrameLayout.inflate(this, R.layout.map_layeroption, null);
            ListView layerOptions = (ListView) view.findViewById(R.id.lv_map_layeroptions);
            layerOptions.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                    if (pos == 0) {// 缩放至
                        if (layerBeanSeleted != null && layerBeanSeleted.getBounds() != null) {
                            Rectangle2D rect = layerBeanSeleted.getBounds();
                            mapControl.getMap().setViewBounds(new com.supermap.data.Rectangle2D(rect.left, rect.bottom, rect.right, rect.top));
                            mapControl.getMap().refresh();
                            view.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(context, "不支持缩放至该图层", Toast.LENGTH_SHORT).show();
                        }
                    } else if (pos == 1) {// 重命名
                        view.setVisibility(View.GONE);
                        showTitleSetDialog("RENAME", layerBeanSeleted.getLayerName());
                    } else if (pos == 2) {// 删除
                        if (layerBeanSeleted != null) {
                            if (LayerType.BASE_LAYER.equals(layerBeanSeleted.getLayerType())) {
                                Toast.makeText(context, "底图不能删除!", Toast.LENGTH_SHORT).show();
                            } else {// 删除
                                // todo
                                AlertUtil.showAlertConfirm(context, "删除图层", "确定删除图层吗?", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        deleteLayer();
                                    }
                                }, null);
                                view.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            });
            layerOptions.setAdapter(new SimpleAdapter(this, getSimpleAdapterData1(), R.layout.btn_map_options_item, new String[] { "option" },
                    new int[] { R.id.tv_map_options_item }));
        } else {
            parent.removeView(view);
        }
        int viewWidth = (int) DisplayUtil.getPxFromDp(context, 90);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(viewWidth, LayoutParams.WRAP_CONTENT);
        int h = DisplayUtil.pxHeight(context);
        int w = DisplayUtil.pxWidth(context);
        if (x + viewWidth > w) {// 右边不够宽就向左布局
            layoutParams.leftMargin = x - viewWidth - 70;
        } else {// 右边够宽就向右布局
            layoutParams.leftMargin = x;
        }
        int viewH = view.getHeight();
        if (y + viewH > h) {
            layoutParams.topMargin = y - viewH - 35;
        } else {
            layoutParams.topMargin = y;
        }
        parent.addView(view, layoutParams);
        view.setVisibility(View.VISIBLE);// 默认选项卡 列表视图不可见
    }

    /**
     * <p>
     * 删除地图中已添加的图层
     * </p>
     * @since 1.0.0
     */
    private void deleteLayer() {
        if (layerBeanSeleted == null) {
            return;
        }
        String layerName = layerBeanSeleted.getLayerName();
        if (LayerType.MARKER_LAYER.equals(layerBeanSeleted.getLayerType())) {
            if (addMarkerLayers != null && addMarkerLayers.size() > 0) {
                Layer layerFind = null;
                for (int i = 0; i < addMarkerLayers.size(); i++) {
                    Layer layer = addMarkerLayers.get(i);
                    if (layer != null && layerName != null && layerName.equals(layer.getTitle())) {
                        layerFind = layer;
                        break;
                    }
                }
                if (layerFind != null) {
                    addMarkerLayers.remove(layerFind);
                }
            }
            if (layerName != null && markerKeyMap != null && markerKeyMap.containsKey(layerName)) {
                List<String> list = markerKeyMap.get(layerName);
                if (list != null) {
                    for (int i = 0; i < list.size(); i++) {
                        String key = list.get(i);
                        this.calloutAdded.remove(key);
                        markerMap.remove(key);
                        mapview.removeCallOut(key);
                        // 如果被删除的图层中的marker正在弹出气泡，那么气泡要消失
                        if (markerClickedKey != null && markerClickedKey.equals(key)) {
                            removePopupCallOut();
                        }
                    }
                }
                markerKeyMap.remove(layerName);
            }

            layers.remove(layerBeanSeleted);
            if (layerAdapter != null) {
                layerAdapter.notifyDataSetChanged();
            }
            layerBeanSeleted = null;
        } else if (LayerType.OVERLAY_LAYER.equals(layerBeanSeleted.getLayerType())) {
            if (addWebLayers != null && addWebLayers.size() > 0) {
                Layer layerFind = null;
                for (int i = 0; i < addWebLayers.size(); i++) {
                    Layer layer = addWebLayers.get(i);
                    if (layer != null && layerName != null && layerName.equals(layer.getTitle())) {
                        layerFind = layer;
                        break;
                    }
                }
                addWebLayers.remove(layerFind);
            }

            if (mobileLayers != null && layerName != null && mobileLayers.containsKey(layerName)) {
                com.supermap.mapping.Layer l = mobileLayers.get(layerName);
                if (l != null) {
                    mapControl.getMap().getLayers().remove(l.getCaption());
                }
                // 关闭名为图层名对应的数据源，不然重新添加的时候会崩溃
                if (mapControl.getMap().getWorkspace() != null && mapControl.getMap().getWorkspace().getDatasources() != null) {
                    mapControl.getMap().getWorkspace().getDatasources().close(layerName);
                }
            }

            layers.remove(layerBeanSeleted);
            if (layerAdapter != null) {
                layerAdapter.notifyDataSetChanged();
            }
            layerBeanSeleted = null;
        } else if (LayerType.FEATURE_LAYER.equals(layerBeanSeleted.getLayerType())) {
            // 暂时没有
        }
        layerTitleList.remove(layerName);
        this.mapControl.getMap().refresh();
    }

    /**
     * <p>
     * 切换地图的底图
     * </p>
     * @param path
     * @since 1.0.0
     */
    public void switchBasedLayer(String path) {
        if (mapControl != null && mapControl.getMap() != null && mapControl.getMap().getLayers() != null) {
            this.mapControl.getMap().getLayers().clear();
        }
        // if(mapControl != null && mapControl.getMap() != null && mapControl.getMap().getWorkspace() != null){
        // mapControl.getMap().getWorkspace().close();
        // }
        MapsActive mapActive = new MapsActive();
        mapActive.setPath(path);
        mapActive.setRequestCallBack(mapCallBack);
        mapActive.active();
    }

    /**
     * <p>
     * 添加一个marker到标注图层中
     * </p>
     * @param point2D
     * @since 1.0.0
     */
    public void addMarker(Point2D point2D) {
        Layer markerLayer = getMarkerLayer();
        Marker marker = createMarker(point2D, "标注名称", "标注描述");
        addMarkerToLayer(marker, markerLayer);
    }

    /**
     * <p>
     * 添加一个marker到标注图层中,并绘制标注
     * </p>
     * @param marker
     * @param markerLayer
     * @since 1.0.0
     */
    private void addMarkerToLayer(Marker marker, Layer markerLayer) {
        if (markerLayer != null && marker != null) {
            if (markerLayer.getMarkers() == null) {
                markerLayer.setMarkers(new ArrayList<Marker>());
            }
            String id = markerLayer.getTitle().hashCode()+"-m"+markerLayer.getMarkers().size();
            marker.setId(id);
            markerLayer.getMarkers().add(marker);
            drawMarker(marker);
        }
    }

    /**
     * <p>
     * 构建一个marker
     * </p>
     * @param point2d
     * @param title
     * @param description
     * @return
     * @since 1.0.0
     */
    private Marker createMarker(Point2D point2d, String title, String description) {
        Marker marker = new Marker();
        Geometry g = new Geometry();
        g.setType(GeometryType.POINT);
        g.setPoints(new Point2D[] { point2d });
        marker.setGeometry(g);
        Attributes as = new Attributes();
        as.setTitle(title);
        as.setDescription(description);
        marker.setAttributes(as);
        String icon = URLs.getURL() + "/apps/viewer/static/images/markers/mark_red.png";
        if (markerIconId == 0) {
            icon = URLs.getURL() + "/apps/viewer/static/images/markers/mark_purple.png";
        } else if (markerIconId == 1) {
            icon = URLs.getURL() + "/apps/viewer/static/images/markers/mark_blue.png";
        }
        marker.setIcon(icon);// 不同icon的地址http://192.168.168.111:8090/iportal/apps/viewer/static/images/markers/mark_red.png
        return marker;
    }

    /**
     * <p>
     * 获取可以被添加的所有web图层的列表
     * </p>
     * @since 1.0.0
     */
    private void getWebLayers() {
        // 切换底图后epsgCode发生变化则重新发送获取可以被叠加的web图层
        if (webLayers == null || webLayers.size() < 1 || (oldEpsgCode != -1 && oldEpsgCode != epsgCode)) {
            if (webLayers != null && webLayers.size() > 0) {// 重新请求前先清空webLayers
                webLayers.clear();
            }
            oldEpsgCode = epsgCode;// 触发重新请求获取可以被添加的所有web图层后，设置oldEpsgCode和epsgCode一致
            // 发送请求
            if (mapsActive == null) {
                mapsActive = new MapsActive();
            }
            RequestParam param = new RequestParam();
            param.setPageSize(100);
            param.setCurrentPage(1);
            // param.setMapStatus("PUBLISHED");
            param.setEpsgCode(epsgCode);
            List<String> sourceTypes = new ArrayList<String>();
            sourceTypes.add("SUPERMAP_REST");
            // sourceTypes.add("WMS");
            // sourceTypes.add("WMTS");
            param.setSourceTypes(sourceTypes.toString());
            param.setUnique(true);
            mapsActive.setParam(param);
            mapsActive.setPath("/web/maps");
            mapsActive.setRequestCallBack(this.mapsCallBack);
            mapsActive.active();
        } else {
            // 直接可视化web图层列表
            if (this.mlAdapter == null) {
                this.mlAdapter = new MapLayersAdapter(this);
                gv_layers_grid.setAdapter(mlAdapter);
            }
            if (!webLayers.equals(mlAdapter.getWebLayers())) {
                mlAdapter.setWebLayers(webLayers);
                mlAdapter.notifyDataSetChanged();
            }
            tv_layers_title.setText("添加Web图层");
            ll_layers_view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * <p>
     * 可视化所有底图图层的列表供用户切换底图，没有底图时先发送获取的请求后再可视化
     * </p>
     * @since 1.0.0
     */
    private void getBaseLayers() {
        isGetBaseLayers = true;
        if (baseLayers == null || baseLayers.size() < 1) {
            // 发送请求
            sendGetBaseLayersReq();
        } else {
            // 直接可视化web图层列表
            if (this.mlAdapter == null) {
                this.mlAdapter = new MapLayersAdapter(this);
                if (!baseLayers.equals(mlAdapter.getWebLayers())) {
                    mlAdapter.setWebLayers(baseLayers);
                }
                gv_layers_grid.setAdapter(mlAdapter);
            } else {
                if (!baseLayers.equals(mlAdapter.getWebLayers())) {
                    mlAdapter.setWebLayers(baseLayers);
                    mlAdapter.notifyDataSetChanged();
                }
            }
            tv_layers_title.setText("切换底图");
            ll_layers_view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * <p>
     * 从所有底图图层的列表中选取其一作为默认底图，已有底图直接获取，没有发送 获取所有底图图层 的请求后并选取其一作为默认底图
     * </p>
     * @since 1.0.0
     */
    private void getDefBaseLayer() {
        isGetDefBaseLayer = true;
        if (baseLayers == null || baseLayers.size() < 1) {
            // 发送请求
            sendGetBaseLayersReq();
        } else {
            // 直接选取其一作为默认底图
            switchBasedLayer("/web/maps/" + baseLayers.get(0).getId());// 默认初始化第一幅底图
        }
    }

    /**
     * <p>
     * 获取所有底图图层的列表
     * </p>
     * @since 1.0.0
     */
    private void sendGetBaseLayersReq() {
        // 发送请求
        if (mapsActive == null) {
            mapsActive = new MapsActive();
        }
        RequestParam param = new RequestParam();
        param.setPageSize(100);
        param.setCurrentPage(1);

        // param.setSuggest(true);// 获取所有的默认底图
        // 获取所有的底图
        List<String> sourceTypes = new ArrayList<String>();
        sourceTypes.add("SUPERMAP_REST");
        // sourceTypes.add("WMS");
        // sourceTypes.add("WMTS");
        // sourceTypes.add("SUPERMAP_REST_VECTOR");
        param.setSourceTypes(sourceTypes.toString());
        param.setUnique(true);

        mapsActive.setParam(param);
        mapsActive.setPath("/web/maps");
        mapsActive.setRequestCallBack(this.mapsCallBack);
        mapsActive.active();
    }

    /**
     * <p>
     * 获取所有满足条件的地图的回调处理类
     * </p>
     * @author ${Author}
     * @version ${Version}
     * @since 1.0.0
     * 
     */
    class GetMapsCallBack extends RequestCallBack<String> {

        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            Maps maps = JSON.parseObject(responseInfo.result, Maps.class);
            if (maps != null && maps.getContent() != null && maps.getContent().size() > 0) {
                for (int i = 0; i < maps.getContent().size(); i++) {
                    Content map = maps.getContent().get(i);
                    WebLayer wl = new WebLayer();
                    wl.setThumbnail(map.getThumbnail());
                    wl.setTitle(map.getTitle());
                    wl.setId(map.getId());
                    wl.setEpsgCode(map.getEpsgCode());
                    if (isGetBaseLayers || isGetDefBaseLayer) {
                        baseLayers.add(wl);
                        if (i == 0) {
                            switchBasedLayer("/web/maps/" + map.getId());// 默认初始化第一幅底图
                        }
                    } else {
                        List<Layer> layers = map.getLayers();
                        if (layers != null && layers.size() > 0) {// 其实只有一个图层，所以
                            wl.setUrl(layers.get(0).getUrl());
                            wl.setSubLayers(layers.get(0).getSubLayers());// 此处layer中的bounds为空
                            // wl.setBounds(layers.get(0).getBounds());// 此处layer中的bounds为空
                            // wl.setBounds(new Rectangle2D(12848875.3381427, 4785292.75986976, 13081668.2157625, 5021316.15264565));// 此处layer中的bounds为空
                            webLayers.add(wl);
                        }
                    }
                }
            } else {
                String msg = "";
                if (isGetBaseLayers || isGetDefBaseLayer) {
                    msg = "没有可用的底图数据！";
                    DialogUtils.stopProgressDialog(dialog);
                } else {
                    msg = "没有可以被叠加的web底图数据！";
                }
                Toast.makeText(CreateMapActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
            // 修改选择web图层视图的宽度为屏幕的2/3
            // double width = ;
            LayoutParams lp = ll_layers_view.getLayoutParams();
            lp.width = 2 * DisplayUtil.pxWidth(context) / 3;
            lp.height = DisplayUtil.pxHeight(context) / 2;
            ll_layers_view.setLayoutParams(lp);

            if (isGetDefBaseLayer) {// 如果是获取默认底图，那么无需可视化图层列表
                isGetDefBaseLayer = false;
                return;
            }

            if (isGetBaseLayers) {
                tv_layers_title.setText("切换底图");
            } else {
                tv_layers_title.setText("添加Web图层");
            }

            ll_layers_view.setVisibility(View.VISIBLE);
            if (mlAdapter == null) {
                mlAdapter = new MapLayersAdapter(context);
                if (isGetBaseLayers) {
                    mlAdapter.setWebLayers(baseLayers);
                    // isGetBaseLayers = false;
                } else {
                    mlAdapter.setWebLayers(webLayers);
                }
                gv_layers_grid.setAdapter(mlAdapter);
            } else {
                if (isGetBaseLayers) {
                    mlAdapter.setWebLayers(baseLayers);
                    // isGetBaseLayers = false;
                } else {
                    mlAdapter.setWebLayers(webLayers);
                }
                mlAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onFailure(HttpException error, String msg) {
            if (isGetDefBaseLayer) {
                isGetDefBaseLayer = false;
                DialogUtils.stopProgressDialog(dialog);
            }
            HttpExceptionHandler.handleExption(error, true, context);
//             DialogUtils.stopProgressDialog(dialog);
        }
        
    }

    /**
     * <p>
     * 添加一个web图层
     * </p>
     * @param wl
     * @since 1.0.0
     */
    public void addWebLayer(WebLayer wl) {
        Layer layer = createWebLayer(wl);
        if (layer != null) {
            Workspace workspace = this.mapControl.getMap().getWorkspace();
            drawAddedLayer(workspace, layer);
            Toast.makeText(this, "web图层 " + wl.getTitle() + " 添加成功!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * <p>
     * 创建一个web图层
     * </p>
     * @param wl
     * @return
     * @since 1.0.0
     */
    private Layer createWebLayer(WebLayer wl) {
        if (wl != null) {
            if (addWebLayers == null) {
                addWebLayers = new ArrayList<Layer>();
            }
            boolean isAdded = false;// 判断web图层是否已添加
            if (map != null && map.getLayers() != null && map.getLayers().size() > 0) {
                for (Layer layer : map.getLayers()) {
                    if (layer != null && layer.getUrl() != null && layer.getUrl().equals(wl.getUrl())) {
                        isAdded = true;
                        break;
                    }
                }
            }
            if (!isAdded && addWebLayers.size() > 0) {
                for (Layer layer : addWebLayers) {
                    if (layer != null && layer.getUrl() != null && layer.getUrl().equals(wl.getUrl())) {
                        isAdded = true;
                        break;
                    }
                }
            }
            if (isAdded) {
                Toast.makeText(this, "web图层 " + wl.getTitle() + " 不能重复添加!", Toast.LENGTH_SHORT).show();
            } else {
                Layer webLayer = new Layer();
                String title = getUniqueTitle(wl.getTitle());// 判断是否同名图层已经存在
                webLayer.setTitle(title);
                webLayer.setUrl(wl.getUrl());
                webLayer.setType("SUPERMAP_REST");
                webLayer.setZindex(++zIndex);
                webLayer.setLayerType(LayerType.OVERLAY_LAYER);
                webLayer.setBounds(wl.getBounds());
                // List<String> subLayers = new ArrayList<String>();// 待初始化
                webLayer.setSubLayers(wl.getSubLayers());
                addWebLayers.add(webLayer);
                return webLayer;
            }
        }
        return null;
    }

    /**
     * <p>
     * 判断当前的图层名是否存在，如果存在则递增判断图层名+i的名称是否存在，直到找到不存在同名后返回
     * </p>
     * @param title
     * @return
     * @since 1.0.0
     */
    private String getUniqueTitle(String title) {
        String uniqueTitle = title;
        if (layerTitleList != null && layerTitleList.contains(title)) {
            int i = 0;
            while (true) {
                i++;
                if (!layerTitleList.contains(title + i)) {
                    uniqueTitle = title + i;
                    break;
                }
            }
        }
        return uniqueTitle;
    }

    /**
     * <p>
     * 从已添加的marker图层集合中获取当前正在构建的marker图层
     * </p>
     * @return
     * @since 1.0.0
     */
    private Layer getMarkerLayer() {
        if (addMarkerLayers != null && addMarkerLayers.size() > 0) {
            return addMarkerLayers.get(addMarkerLayers.size() - 1);
        }
        return null;
    }

    /**
     * <p>
     * 创建一个marker图层
     * </p>
     * @param title
     * @since 1.0.0
     */
    private void createMarkerLayer(String title) {
        Layer addMarkerLayer = new Layer();
        addMarkerLayer.setTitle(title);// 接收参数
        addMarkerLayer.setOpacty(1);
        int zIndex = 1;// 底图才是0.所以从1开始
        if (this.addWebLayers != null && this.addWebLayers.size() > 0) {
            zIndex = zIndex + addWebLayers.size();
        }
        if (this.addMarkerLayers != null && this.addMarkerLayers.size() > 0) {
            zIndex = zIndex + addMarkerLayers.size();
        }
        addMarkerLayer.setZindex(zIndex);
        addMarkerLayer.setLayerType(LayerType.MARKER_LAYER);
        addMarkerLayer.setIsVisible(true);
        addMarkerLayer.setMarkers(new ArrayList<Marker>());
        if (addMarkerLayers == null) {
            addMarkerLayers = new ArrayList<Layer>();
        }
        addMarkerLayers.add(addMarkerLayer);
        layerTitleList.add(title);
        LayerControlBean lcb = new LayerControlBean(null, LayerType.MARKER_LAYER, title, true);
        if (!this.layers.contains(lcb)) {
            this.layers.add(lcb);
            if (layerAdapter != null) {
                layerAdapter.notifyDataSetChanged();
            }
        }
        // return addMarkerLayer;
    }

    /**
     * <p>
     * 初始化环境，即移动端产品所需的许可配置等
     * </p>
     * @since 1.0.0
     */
    private void init() {
        new Thread(new Runnable() {
            public void run() {
                com.supermap.data.Environment.initialization(getApplicationContext());
                CommonUtil.createSysDir();
                CommonUtil.initLicense(context);
                com.supermap.data.Environment.setLicensePath(Paths.LICENSE);
//                com.supermap.data.Environment.setLicenseCode("a2432cde1bc172bb8ab41c37a1f333a6");
                com.supermap.data.Environment.setWebCacheDirectory(Paths.WEBCACHE);
                // 首次启动app，清理地图缓存--2015.1.4 解决地图部分
//                if (VersionDetect.isFirstLoadNewAPK(CreateMapActivity.this)) {
//                    try {
//                        // 只删除第2级，以及第4级缓存图片
//                        String quanguoPath = Paths.WEBCACHE + "Rest/www.isupermap.com_/map/quanguo/JP_256_/";
//                        File level2File = new File(quanguoPath + 147914678);
//                        File level4File = new File(quanguoPath + 36978669);
//                        FileUtils.deleteDirectory(level2File);
//                        FileUtils.deleteDirectory(level4File);
//                        if (level2File.exists()) {
//                            FileUtils.deleteDirectory(level2File);
//                        }
//                        if (level4File.exists()) {
//                            FileUtils.deleteDirectory(level4File);
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                ((Iapplication) getApplication()).setInit(true);
                Params.isInitLicense = true;
                handler.sendEmptyMessage(3);
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * <p>
     * 绘制地图，可视化
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
        layers.clear();
        if (layerAdapter != null) {
            layerAdapter.notifyDataSetChanged();
        }
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
     * 初始化图层控制界面的布局
     * </p>
     * @since 1.0.0
     */
    private void initLayerView() {
        if (this.layers == null || this.layers.size() == 0) {
            return;
        }
        if (layersView == null) {
            this.layersView = FrameLayout.inflate(this, R.layout.map_layercontrol, null);
            ListView layersListView = (ListView) layersView.findViewById(R.id.lv_map_layers);
            layerAdapter = new LayerControlAdapter(this);
            layerAdapter.setLayers(this.layers);
            layerAdapter.setHandler(handler);
            layersListView.setAdapter(layerAdapter);
            this.layersView.setVisibility(View.GONE);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
            parent.addView(layersView, layoutParams);
        }
        // this.mapview.getMapControl().getMap().refresh();
    }

    /**
     * <p>
     * 图层控制时，隐藏和显示图层的响应事件
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
            this.featureViasble = !this.featureViasble;
            // if (this.featureViasble){
            // viewHolder.layerIcon.setBackgroundResource(R.drawable.btn_layer_icon_sel_down);
            // } else {
            // viewHolder.layerIcon.setBackgroundResource(R.drawable.btn_layer_icon_sel_up);
            // }

            // icon.setBackground(this.getResources().getDrawable(R.drawable.btn_layer_icon_sel_down));
            this.mapControl.getMap().getTrackingLayer().setVisible(this.featureViasble);
            // layerAdapter.getItem(arg0)
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
            // for (int i = 0; i < this.calloutAdded.size(); i++) {
            // this.calloutAdded.get(i).setVisibility(visiable);
            // }
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
     * 可视化地图的图层
     * </p>
     * @param workspace
     * @param layer
     * @since 1.0.0
     */
    private void addLayer(Workspace workspace, Layer layer) {
        if (LayerType.FEATURE_LAYER.equals(layer.getLayerType())) {
            drawFeatureLayer(layer);
            return;
        }
        if (LayerType.MARKER_LAYER.equals(layer.getLayerType())) {
            if (layer.getMarkers() != null) {
                LayerControlBean lcb = new LayerControlBean(null, LayerType.MARKER_LAYER, layer.getTitle(), true);
                if (!this.layers.contains(lcb)) {
                    this.layers.add(lcb);
                    if (layerAdapter != null) {
                        // layerAdapter.setLayers(this.layers);
                        layerAdapter.notifyDataSetChanged();
                    }
                }
                layerTitleList.add(layer.getTitle());
                for (int i = 0; i < layer.getMarkers().size(); i++) {
                    Marker marker = layer.getMarkers().get(i);
                    drawMarker(marker);
                }
            }
            return;
        }
        drawAddedLayer(workspace, layer);
    }

    /**
     * <p>
     * 绘制web图层，包含底图的绘制
     * </p>
     * @param workspace
     * @param layer
     * @since 1.0.0
     */
    private void drawAddedLayer(Workspace workspace, Layer layer) {
        DatasourceConnectionInfo dsInfo = new DatasourceConnectionInfo();
        String url = layer.getUrl();
        String type = layer.getType();
        if (!TextUtils.isEmpty(type)) {
            LayerControlBean lcb = new LayerControlBean(layer.getBounds(), layer.getLayerType(), layer.getTitle(), true);
            if (layer.getLayerType() == null && layer.getZindex() == 0) {
                lcb.setLayerType(LayerType.BASE_LAYER);
            }
            this.layers.add(lcb);
            if (layerAdapter != null) {
                // layerAdapter.setLayers(this.layers);
                layerAdapter.notifyDataSetChanged();
            }
            layerTitleList.add(layer.getTitle());
            if ("SUPERMAP_REST".equals(type)) {
                dsInfo.setEngineType(EngineType.Rest);
            } else {
                dsInfo.setEngineType(EngineType.OGC);
                String layertype = this.getLayerType(url);
                dsInfo.setDriver(layertype);
            }
            dsInfo.setServer(url);
            dsInfo.setAlias(layer.getTitle());// 保证多个rest图层的别名不一样，解决别名一样发生崩溃
            if (workspace != null) {
                Datasource ds = workspace.getDatasources().open(dsInfo);
                if (ds != null) {
                    Dataset dt = ds.getDatasets().get(0);
                    com.supermap.mapping.Layer lay = this.mapControl.getMap().getLayers().add(dt, true);
                    mobileLayers.put(layer.getTitle(), lay);
                    this.mapControl.getMap().refresh();
                }
            }
        }
    }

    /**
     * <p>
     * 绘制一个marker
     * </p>
     * @param marker
     * @since 1.0.0
     */
    private void drawMarker(Marker marker) {
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
                storeMakerKey(markserKey);
                markerMap.put(markserKey, marker);
                CallOut callout = new CallOut(this);
                callout.setContentView(calloutLayout); // 设置显示内容
                callout.setCustomize(true); // 设置自定义背景图片
                callout.setLocation(point.x, point.y);
                this.mapview.addCallout(callout, markserKey);
                this.calloutAdded.put(markserKey, callout);
                this.getMarkerBitmapUtils().display(btnMarker, icon);
                // this.markers.add(callout);
            }
        }
    }

    /**
     * <p>
     * 分类存储marker，按不同标签图层分类，便于图层控制
     * </p>
     * @param markserKey
     * @since 1.0.0
     */
    private void storeMakerKey(String markserKey) {
        if (markerKeyMap == null) {
            markerKeyMap = new HashMap<String, List<String>>();
        }
        Layer markerLayer = getMarkerLayer();
        if (markerLayer != null && markerLayer.getTitle() != null) {
            if (markerKeyMap.containsKey(markerLayer.getTitle())) {
                List<String> list = markerKeyMap.get(markerLayer.getTitle());
                if (list != null) {
                    list.add(markserKey);
                } else {
                    list = new ArrayList<String>();
                    list.add(markserKey);
                    markerKeyMap.put(markerLayer.getTitle(), list);
                }
            } else {
                List<String> list = new ArrayList<String>();
                list.add(markserKey);
                markerKeyMap.put(markerLayer.getTitle(), list);
            }
        }
    }

    /**
     * <p>
     * 绘制矢量图层
     * </p>
     * @param layer
     * @since 1.0.0
     */
    private void drawFeatureLayer(Layer layer) {
        if (layer.getFeatures() != null) {
            LayerControlBean lcb = new LayerControlBean(layer.getBounds(), LayerType.FEATURE_LAYER, layer.getTitle(), true);
            if (!this.layers.contains(lcb)) {
                this.layers.add(lcb);
                if (layerAdapter != null) {
                    // layerAdapter.setLayers(this.layers);
                    layerAdapter.notifyDataSetChanged();
                }
            }
            layerTitleList.add(layer.getTitle());
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
    }

    /**
     * <p>
     * 点击地图上每个marker后的响应事件
     * </p>
     * @param view
     * @since 1.0.0
     */
    @OnClick(R.id.btn_mapview_marker)
    private void markerClick(View view) {
        this.mapview.removeCallOut(VIEW_MARKER);
        markerClickedKey = String.valueOf(view.hashCode());
        markerClicked = markerMap.get(markerClickedKey);
        if (markerClicked == null) {
            return;
        }
        if (this.marker_detail == null) {
            this.marker_detail = LinearLayout.inflate(this, R.layout.marker_detail_set, null);
            marker_title = (EditText) marker_detail.findViewById(R.id.et_marker_title);
            marker_desc = (EditText) marker_detail.findViewById(R.id.et_marker_desc);
            iv_marker_clear = (ImageView) marker_detail.findViewById(R.id.iv_marker_clear);
            iv_marker_clear.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    deleteMarker();// 删除一个marker
                }
            });
            bn_marker_pos = (Button) marker_detail.findViewById(R.id.bn_marker_pos);
            bn_marker_pos.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    updateMarkerInfo();// 更新marker的信息
                }
            });
            bn_marker_neg = (Button) marker_detail.findViewById(R.id.bn_marker_neg);
            bn_marker_neg.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    removePopupCallOut();
                }
            });
            // 设置textView可以滚动
            marker_desc.setMovementMethod(ScrollingMovementMethod.getInstance());
        }

        point_marker = markerClicked.getGeometry().getPoints()[0];
        Point p = this.mapview.getMapControl().getMap().mapToPixel(new com.supermap.data.Point2D(point_marker.getX(), point_marker.getY()));
        this.marker_height = view.getMeasuredHeight();
        p.setY((int) (p.getY() - this.marker_height));
        com.supermap.data.Point2D point1 = this.mapview.getMapControl().getMap().pixelToMap(p);

        if (TextUtils.isEmpty(markerClicked.getAttributes().getTitle())) {
            marker_title.setText("暂无标题");
        } else {
            marker_title.setText(markerClicked.getAttributes().getTitle());
        }

        if (TextUtils.isEmpty(markerClicked.getAttributes().getDescription())) {
            marker_desc.setText("暂无描述");
        } else {
            marker_desc.setText(markerClicked.getAttributes().getDescription());
        }
        if (this.popu_callout == null) {
            popu_callout = new CallOut(this);
            popu_callout.setContentView(marker_detail);
            popu_callout.setCustomize(true);
        }
        popu_callout.setLocation(point1.getX(), point1.getY());
        popu_callout.setVisibility(View.VISIBLE);
        this.mapview.addCallout(popu_callout, VIEW_MARKER);

    }

    /**
     * <p>
     * 更新marker的属性
     * </p>
     * @since 1.0.0
     */
    protected void updateMarkerInfo() {
        if (markerClicked != null) {
            String title = marker_title.getText().toString();
            String desc = marker_desc.getText().toString();
            Attributes as = new Attributes();
            as.setTitle(title);
            as.setDescription(desc);
            markerClicked.setAttributes(as);
            removePopupCallOut();
        }
    }

    /**
     * <p>
     * 获取图层类型
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
     * 获取移动端可视化对应的风格
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
     * 绘制地图中矢量图层的点图层
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
     * 绘制地图中矢量图层的线图层
     * </p>
     * @param points 组成线的点集合
     * @param style 绘制风格
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
     * 绘制地图中矢量图层的面图层
     * </p>
     * @param points 组成面的点集合
     * @param style 绘制风格
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

    /**
     * <p>
     * 获取图片的处理工具对象
     * </p>
     * @return
     * @since 1.0.0
     */
    private BitmapUtils getMarkerBitmapUtils() {
        if (this.bitmapUtils == null) {
            this.bitmapUtils = new BitmapUtils(this);
        }
        this.bitmapUtils.configDefaultLoadFailedImage(R.drawable.btn_map_marker);
        return this.bitmapUtils;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    public void onBackPressed() {
//        if (System.currentTimeMillis() - lastTime > 2000) {
//            lastTime = System.currentTimeMillis();
//            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
//        } else {
//            saveMapId = null;// 销毁当前保存地图的id
////            this.finish();
//            System.exit(0);
//        }
        super.onBackPressed();
    }

    /**
     * <p>
     * 回退处理，可以不实现，跟默认的一致
     * </p>
     * @param keyCode
     * @param event
     * @return
     * @since 1.0.0
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (System.currentTimeMillis() - lastTime > 2000) {
                lastTime = System.currentTimeMillis();
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            } else {
                saveMapId = null;// 销毁当前保存地图的id
//                this.finish();
                System.exit(0);
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
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

    /**
     * <p>
     * 弹出设置地图标题、maker图层标题的弹出框
     * </p>
     * @param type
     * @param titleName
     * @since 1.0.0
     */
    private void showTitleSetDialog(String type, String titleName) {
        android.app.AlertDialog.Builder ab = new android.app.AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.alert_view_titleset, null);
        TextView tv_alert_title = (TextView) view.findViewById(R.id.tv_alert_title);
        TextView tv_title_name = (TextView) view.findViewById(R.id.tv_title_name);
        final EditText et_title_name = (EditText) view.findViewById(R.id.et_title_name);
        et_title_name.setText(titleName);// 初始化
        if (titleName != null) {
            et_title_name.setSelection(titleName.length());
        }
//        ab.setCancelable(false);
        ab.setView(view);
        DialogInterface.OnClickListener positiveOCL = null;
        ab.setNegativeButton(this.getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // dialogInterface.dismiss();
                // if ("MAP".equalsIgnoreCase(type)) {
                // setBaseLayerTitle();
                // }
                setMShowing(dialogInterface, true);
            }
        });
        if ("MAP".equalsIgnoreCase(type)) {
            tv_alert_title.setText("设置地图名称");
            tv_title_name.setText("地图名称:");
            positiveOCL = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String text = et_title_name.getText().toString();
                    if (text == null || "".equals(text)) {
                        setMShowing(dialogInterface, false);
                        Toast.makeText(CreateMapActivity.this, "地图名称不能为空", Toast.LENGTH_SHORT).show();
                    } else {
                        setMShowing(dialogInterface, true);
                        tv_map_title.setText(text);
                        // setBaseLayerTitle();
                        // dialogInterface.dismiss();
                    }

                }
            };
        } else if ("WEBLAYER".equalsIgnoreCase(type)) {
            tv_alert_title.setText("添加web图层");
            tv_title_name.setText("图层名称:");
            positiveOCL = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String text = et_title_name.getText().toString();
                    if (text == null || "".equals(text)) {
                        setMShowing(dialogInterface, false);
                        Toast.makeText(CreateMapActivity.this, "图层名称不能为空", Toast.LENGTH_SHORT).show();
                    } else {
                        webLayerTitle = text;
                        setMShowing(dialogInterface, true);
                        // dialogInterface.dismiss();
                    }

                }
            };
        } else if ("MAKERLAYER".equalsIgnoreCase(type)) {
            tv_alert_title.setText("添加标注图层");
            tv_title_name.setText("图层名称:");
            positiveOCL = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String text = et_title_name.getText().toString();
                    if (text == null || "".equals(text)) {
                        setMShowing(dialogInterface, false);
                        Toast.makeText(CreateMapActivity.this, "图层名称不能为空", Toast.LENGTH_SHORT).show();
                    } else {
                        if (layerTitleList != null && layerTitleList.contains(text)) {
                            Toast.makeText(CreateMapActivity.this, text + " 已存在，" + "请重新修改图层名!", Toast.LENGTH_SHORT).show();
                            setMShowing(dialogInterface, false);
                            return;
                        }
                        setMShowing(dialogInterface, true);
                        createMarkerLayer(text);
                        ll_marker_view.setVisibility(View.VISIBLE);
                        // markerTitle = text;
                        // dialogInterface.dismiss();
                    }
                }
            };
            // ab.setNegativeButton(this.getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
            // @Override
            // public void onClick(DialogInterface dialogInterface, int i) {
            // iv_marker_red.setVisibility(View.VISIBLE);
            // dialogInterface.dismiss();
            // }
            // });
        } else if ("RENAME".equalsIgnoreCase(type)) {
            tv_alert_title.setText("图层重命名");
            tv_title_name.setText("图层名称:");
            positiveOCL = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String text = et_title_name.getText().toString();
                    if (text == null || "".equals(text)) {
                        setMShowing(dialogInterface, false);
                        Toast.makeText(CreateMapActivity.this, "图层名称不能为空", Toast.LENGTH_SHORT).show();
                    } else {
                        if (layerTitleList != null && layerTitleList.contains(text)) {
                            Toast.makeText(CreateMapActivity.this, text + " 已存在，" + "请重新修改图层名!", Toast.LENGTH_SHORT).show();
                            setMShowing(dialogInterface, false);
                            return;
                        }
                        setMShowing(dialogInterface, true);
                        renameLayerTitle(text);
                    }

                }
            };
        }
        ab.setPositiveButton(this.getResources().getString(R.string.confirm), positiveOCL);
        alertDialog = ab.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
    
    /**
     * <p>
     * 弹出设置地图信息的弹出框
     * </p>
     * @param isSave
     * @since 1.0.0
     */
    private void showMapMesSetDialog(final boolean isSave) {
        android.app.AlertDialog.Builder ab = new android.app.AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.alert_view_mapmes_set, null);
        final EditText et_map_title_name = (EditText) view.findViewById(R.id.et_map_title_name);
        final EditText et_map_tag = (EditText) view.findViewById(R.id.et_map_tag);
        final EditText et_map_describe = (EditText) view.findViewById(R.id.et_map_describe);
        if(mapTitle != null && !"".equals(mapTitle) ) {
            et_map_title_name.setText(mapTitle);
        }
        if(mapTag != null && !"".equals(mapTag) ) {
            et_map_tag.setText(mapTag);
        }
        if(describe != null && !"".equals(describe) ) {
            et_map_describe.setText(describe);
        }
//        ab.setCancelable(false);
        ab.setView(view);
        ab.setNegativeButton(this.getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setMShowing(dialogInterface, true);
            }
        });       
        ab.setPositiveButton(this.getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String title = et_map_title_name.getText().toString();
                String tag = et_map_tag.getText().toString();               
                if (title == null || "".equals(title)) {
                    setMShowing(dialogInterface, false);
                    Toast.makeText(CreateMapActivity.this, "地图标题不能为空", Toast.LENGTH_SHORT).show();
                }  else if(tag == null || "".equals(tag)){
                    setMShowing(dialogInterface, false);
                    Toast.makeText(CreateMapActivity.this, "地图标签不能为空", Toast.LENGTH_SHORT).show();
                }else {
                    setMShowing(dialogInterface, true);
                    tv_map_title.setText(title);
                    mapTitle = title;
                    describe = et_map_describe.getText().toString();
                    mapTag = tag;
                    if(isSave){
                        sendCreateMap("SAVED"); 
                    }
                    // dialogInterface.dismiss();
                }
            }
        });
        AlertDialog alertDialog = ab.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    /**
     * <p>
     * 弹出框显示和隐藏的
     * </p>
     * @param dialog
     * @param mShowing
     * @since 1.0.0
     */
    private void setMShowing(DialogInterface dialog, boolean mShowing) {
        try {
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, mShowing);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * <p>
     * 重命名图层
     * </p>
     * @param text
     * @since 1.0.0
     */
    protected void renameLayerTitle(String text) {
        if (layerBeanSeleted == null) {
            return;
        }
        if (LayerType.BASE_LAYER.equals(layerBeanSeleted.getLayerType())) {
            baseLayerName = text;
            resetLayers(text);
        } else if (LayerType.FEATURE_LAYER.equals(layerBeanSeleted.getLayerType())) {
            // 暂时没有
            resetLayers(text);
        } else if (LayerType.MARKER_LAYER.equals(layerBeanSeleted.getLayerType())) {
            if (addMarkerLayers != null && addMarkerLayers.size() > 0) {
                for (int i = 0; i < addMarkerLayers.size(); i++) {
                    Layer layer = addMarkerLayers.get(i);
                    if (layer != null && layerBeanSeleted.getLayerName().equals(layer.getTitle())) {
                        layer.setTitle(text);
                        break;
                    }
                }
            }
            resetLayers(text);
        } else if (LayerType.OVERLAY_LAYER.equals(layerBeanSeleted.getLayerType())) {
            if (addWebLayers != null && addWebLayers.size() > 0) {
                for (int i = 0; i < addWebLayers.size(); i++) {
                    Layer layer = addWebLayers.get(i);
                    if (layer != null && layerBeanSeleted.getLayerName().equals(layer.getTitle())) {
                        layer.setTitle(text);
                        break;
                    }
                }
            }
            resetLayers(text);
        }
    }

    /**
     * <p>
     * 重命名图层时提取的公用代码
     * </p>
     * @param text
     * @since 1.0.0
     */
    private void resetLayers(String text) {
        if (layers != null && layers.size() > 0) {
            for (int i = 0; i < layers.size(); i++) {
                LayerControlBean bean = layers.get(i);
                if (bean != null && layerBeanSeleted.getLayerType().equals(bean.getLayerType()) && layerBeanSeleted.getLayerName().equals(bean.getLayerName())) {
                    layerTitleList.remove(layerBeanSeleted.getLayerName());
                    layerTitleList.add(text);
                    bean.setLayerName(text);
                    break;
                }
            }
            if (layerAdapter != null) {
                layerAdapter.notifyDataSetChanged();
            }
        }

    }

    /**
     * <p>
     * 地图切换和添加web图层时的处理
     * </p>
     * @param parent
     * @param itemView
     * @param position
     * @param id
     * @since 1.0.0
     */
    @OnItemClick(R.id.gv_layers_grid)
    public void groupMapOnClick(AdapterView<?> parent, View itemView, int position, long id) {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - this.clickTime) < 1000) {
            return;
        }
        this.clickTime = currentTime;
        if (parent.getAdapter().getItem(position) instanceof WebLayer) {
            final WebLayer wl = new WebLayer((WebLayer) parent.getAdapter().getItem(position));
            if (isGetBaseLayers && map != null && map.getId() == wl.getId()) {
                Toast.makeText(CreateMapActivity.this, "切换的地图与当前地图是同一个地图！", Toast.LENGTH_SHORT).show();
                return;
            }
            final ImageView img = (ImageView) itemView.findViewById(R.id.group_mapthumbnail);
            ObjectAnimator animator = ObjectAnimator.ofFloat(img, "rotationY", 0, 180);
            animator.setDuration(600);
            animator.start();
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator arg0) {
                    if (isGetBaseLayers) {
                        // if (epsgCode == wl.getEpsgCode()) {
                        isMapOpened = false;
                        switchBasedLayer("/web/maps/" + wl.getId());
                        // 关闭底图图层列表视图
                        ll_layers_view.setVisibility(View.GONE);
                        // } else {
                        // Toast.makeText(CreateMapActivity.this, "当前地图使用的投影为" + epsgCode + "，而要切换的底图投影为：" + wl.getEpsgCode() + "，图层无法叠加", Toast.LENGTH_SHORT)
                        // .show();
                        // }
                    } else {
                        // 获取添加图层的地图状态，说白了就是bounds
                        getAddWebLayerStatus(wl);
                    }
                    img.postDelayed(new Runnable() {
                        public void run() {
                            img.setRotationY(0);
                        }
                    }, 2000);
                }
            });
        }
    }

    /**
     * <p>
     * 获取添加web图层的信息，目前是bounds
     * </p>
     * @param wl
     * @since 1.0.0
     */
    protected void getAddWebLayerStatus(WebLayer wl) {
        if (wl != null && wl.getUrl() != null && !"".equals(wl.getUrl())) {
            HttpUtil.sendRequest(HttpRequest.HttpMethod.GET, wl.getUrl() + ".json", null, new GetWebLayerInfoCallBack(wl));
        }
    }

    /**
     * <p>
     * 获取添加web图层的信息的回调类
     * </p>
     * @author ${Author}
     * @version ${Version}
     * @since 1.0.0
     * 
     */
    class GetWebLayerInfoCallBack extends RequestCallBack<String> {
        private WebLayer wl;

        public GetWebLayerInfoCallBack(WebLayer wl) {
            this.wl = wl;
        }

        @Override
        public void onFailure(HttpException arg0, String arg1) {
            Toast.makeText(CreateMapActivity.this, "获取添加图层信息失败:" + arg1, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            // Toast.makeText(CreateMapActivity.this, "创建地图成功!", Toast.LENGTH_SHORT).show();
            JSONObject layerInfo = JSONObject.parseObject(responseInfo.result);
            if (layerInfo != null && layerInfo.containsKey("bounds")) {
                JSONObject layerBounds = layerInfo.getJSONObject("bounds");
                if (layerBounds != null && layerBounds.containsKey("left") && layerBounds.containsKey("top") && layerBounds.containsKey("right")
                        && layerBounds.containsKey("bottom")) {
                    double left = layerBounds.getDouble("left");
                    double bottom = layerBounds.getDouble("bottom");
                    double right = layerBounds.getDouble("right");
                    double top = layerBounds.getDouble("top");
                    wl.setBounds(new Rectangle2D(left, bottom, right, top));
                }
            }
            // 关闭web图层列表视图
            ll_layers_view.setVisibility(View.GONE);
            // showTitleSetDialog("WEBLAYER", "Web图层名称");
            addWebLayer(wl);
        }
    }
}
