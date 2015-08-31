package com.supermap.mytracks.share;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.PaintDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.*;
import android.widget.*;

import com.lidroid.xutils.ViewUtils;
import com.supermap.mytracks.R;
import com.supermap.mytracks.customUI.CustomProgressDialog;
import com.supermap.mytracks.utils.DialogUtils;
import com.umeng.socialize.controller.UMSocialService;

/**
 * <p>
 * 分享彈出窗界面
 * </p>
 * @author ${huangqh}
 * @version ${Version}
 * @since 1.0.0
 * 
 */
public class SharePopupWindow extends PopupWindow {
    private Context context;

    private String mapURL;
    private String mapThumbnailURL;
    private String mapTitle;
    private String mapDes;

    public static final String TAG_SINA="TAG_SINA";
    public static final String TAG_WEIXIN="TAG_WEIXIN";
    public static final String TAG_WEIXINCIRCLE="TAG_WEIXINCIRCLE";
    public static final String TAG_QZONE="TAG_QZONE";

    private UmengShareUtils umengShareUtils;

    private CustomProgressDialog pg_Sharing;


    /**
     * 控制进度条的显示
     * @param msg
     */
    private Handler responseHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String toastText = "";
            switch (msg.what) {
                case UmengShareUtils.MSG_SHARE_START:
                    toastText=context.getString(R.string.map_share_start);
                    break;
                case UmengShareUtils.MSG_SHARE_SUCCESS:
                    if (pg_Sharing != null && pg_Sharing.isShowing()) {
                        DialogUtils.stopProgressDialog(pg_Sharing);
                    }
                    toastText = context.getString(R.string.map_share_success);
                    break;
                case UmengShareUtils.MSG_SHARE_FAILED:
                    if (pg_Sharing != null && pg_Sharing.isShowing()) {
                        DialogUtils.stopProgressDialog(pg_Sharing);
                    }
                    String errorInfo = "";
                    if (msg.obj != null) {
                        errorInfo = String.valueOf(msg.obj);
                    }
                    toastText = context.getString(R.string.map_share_failed)+"," + errorInfo;
                    break;
                case UmengShareUtils.MSG_SHARE_CANCEL:
                    if (pg_Sharing != null && pg_Sharing.isShowing()) {
                        DialogUtils.stopProgressDialog(pg_Sharing);
                    }
                    toastText = context.getString(R.string.map_share_cancle);
                    break;
                // ....授权相关
                case UmengShareUtils.MSG_SHARE_AUTH_START:
                    toastText=context.getString(R.string.map_share_auth_start);
                    break;
                case UmengShareUtils.MSG_SHARE_AUTH_CANCLE:
                    toastText=context.getString(R.string.map_share_auth_cancle);
                    break;
                case UmengShareUtils.MSG_SHARE_AUTH_COMPLETE:
                    toastText=context.getString(R.string.map_share_auth_complete);
                    break;
                case UmengShareUtils.MSG_SHARE_AUTH_ERROR:
                    toastText=context.getString(R.string.map_share_auth_error);
                    break;
            }
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();

        }
    };

    public SharePopupWindow(Context context) {
        this.context = context;
        initView();
    }

    /**
     * 必设参数—地图缩略图URL
     * @param mapThumbnailURL
     */
    public void setMapThumbnailURL(String mapThumbnailURL) {
        this.mapThumbnailURL = mapThumbnailURL;
    }

    /**
     * 必设参数—地图URL
     * @param mapURL
     */
    public void setMapURL(String mapURL) {
        this.mapURL = mapURL;
    }

    /**
     * 必设参数—地图名称
     * @param mapTitle
     */
    public void setMapTitle(String mapTitle) {
        this.mapTitle = mapTitle;
    }

    /**
     * 必设参数—地图详细信息
     * @param mapDes
     */
    public void setMapDes(String mapDes) {
        this.mapDes = mapDes;
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.map_share, null);

        // 取消
        Button btn_map_share_cancle=(Button)popupView.findViewById(R.id.btn_map_share_cancle);
        btn_map_share_cancle.setOnClickListener(onClickCancel);

        // 得到分享界面的各种分享平台的图标与标题，分别进行设置。
        LinearLayout lv_map_share_sina=(LinearLayout)popupView.findViewById(R.id.lv_map_share_sina);
        LinearLayout lv_map_share_weixin=(LinearLayout)popupView.findViewById(R.id.lv_map_share_weixin);
        LinearLayout lv_map_share_weixincircle=(LinearLayout)popupView.findViewById(R.id.lv_map_share_weixincircle);
        LinearLayout lv_map_share_qzone=(LinearLayout)popupView.findViewById(R.id.lv_map_share_qzone);

        ImageView iv_map_share_plat_icon_sina=(ImageView)lv_map_share_sina.findViewById(R.id.iv_map_share_plat_icon);
        ImageView iv_map_share_plat_icon_weixin=(ImageView)lv_map_share_weixin.findViewById(R.id.iv_map_share_plat_icon);
        ImageView iv_map_share_plat_icon_weixincircle=(ImageView)lv_map_share_weixincircle.findViewById(R.id.iv_map_share_plat_icon);
        ImageView iv_map_share_plat_icon_qzone=(ImageView)lv_map_share_qzone.findViewById(R.id.iv_map_share_plat_icon);

        TextView tv_map_share_plat_title_sina=(TextView)lv_map_share_sina.findViewById(R.id.tv_map_share_plat_title);
        TextView tv_map_share_plat_title_weixin=(TextView)lv_map_share_weixin.findViewById(R.id.tv_map_share_plat_title);
        TextView tv_map_share_plat_title_weixincircle=(TextView)lv_map_share_weixincircle.findViewById(R.id.tv_map_share_plat_title);
        TextView tv_map_share_plat_title_qzone=(TextView)lv_map_share_qzone.findViewById(R.id.tv_map_share_plat_title);

        // 设置图标
        //iv_map_share_plat_icon_sina.setImageResource(R.drawable.icon_map_share_sina);
        //iv_map_share_plat_icon_weixin.setImageResource(R.drawable.icon_map_share_weixin);
        //iv_map_share_plat_icon_weixincircle.setImageResource(R.drawable.icon_map_share_weixincircle);
        //iv_map_share_plat_icon_qzone.setImageResource(R.drawable.icon_map_share_qzone);
        iv_map_share_plat_icon_sina.setImageResource(R.drawable.btn_map_share_sina_selector);
        iv_map_share_plat_icon_weixin.setImageResource(R.drawable.btn_map_share_weixin_selector);
        iv_map_share_plat_icon_weixincircle.setImageResource(R.drawable.btn_map_share_weixincircle_selector);
        iv_map_share_plat_icon_qzone.setImageResource(R.drawable.btn_map_share_qzone_selector);
        // 设置图标标题
        tv_map_share_plat_title_sina.setText(R.string.map_share_sina);
        tv_map_share_plat_title_weixin.setText(R.string.map_share_sina_weixin);
        tv_map_share_plat_title_weixincircle.setText(R.string.map_share_sina_weixincircle);
        tv_map_share_plat_title_qzone.setText(R.string.map_share_sina_qzone);

        // 点击事件
        lv_map_share_sina.setOnClickListener(onClickPlats);
        lv_map_share_weixin.setOnClickListener(onClickPlats);
        lv_map_share_weixincircle.setOnClickListener(onClickPlats);
        lv_map_share_qzone.setOnClickListener(onClickPlats);

        // 弹出框整体设置
        this.setContentView(popupView);
        ViewUtils.inject((Activity)context);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT); // 设置弹出窗体的宽度
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setBackgroundDrawable(new PaintDrawable(context.getResources().getColor(R.color.transparent)));// 响应返回键必须的语句

        this.setFocusable(true);// 设置弹出窗体可点击 锁定后方界面
        this.setOutsideTouchable(true);
        this.setAnimationStyle(R.style.AnimBottom);

        popupView.setOnTouchListener(new View.OnTouchListener()
        {

            public boolean onTouch(View v, MotionEvent event)
            {

                int height = popupView.findViewById(R.id.lv_map_share).getTop();
                int y = (int)event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    if (y < height)
                    {
                        dismiss();
                    }
                }
                return true;
            }
        });

        popupView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (popupView.isShown()){
                    dismiss();
                }
                return false;
            }
        });
    }

    private View.OnClickListener onClickPlats=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (pg_Sharing == null) {
                pg_Sharing = DialogUtils.startProgressDialog(context, context.getString(R.string.map_share_sharing));
            }
            pg_Sharing.show();
            switch (view.getId()){
                case R.id.lv_map_share_sina:
                    share(TAG_SINA);
                    break;
                case R.id.lv_map_share_weixin:
                    share(TAG_WEIXIN);
                    break;
                case R.id.lv_map_share_weixincircle:
                    share(TAG_WEIXINCIRCLE);
                    break;
                case R.id.lv_map_share_qzone:
                    share(TAG_QZONE);
                    break;
            }
        }
    };


    private View.OnClickListener onClickCancel=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (SharePopupWindow.this.isShowing()){
                SharePopupWindow.this.dismiss();
            }
        }
    } ;



    private void share(String tag){
        if (umengShareUtils==null){
            umengShareUtils=new UmengShareUtils(context);
            umengShareUtils.setHandler(responseHandler);
            umengShareUtils.setMapTitle(mapTitle);
            umengShareUtils.setMapDes(mapDes);
            umengShareUtils.setMapThumbnailURL(mapThumbnailURL);
            umengShareUtils.setMapURL(mapURL);
        }
        umengShareUtils.share(tag);
    }

    public UMSocialService getUmengShareController(){
        return umengShareUtils.getController();
    }

}