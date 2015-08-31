package com.supermap.mytracks.share;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;
import com.lidroid.xutils.util.LogUtils;
import com.supermap.mytracks.R;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.utils.OauthHelper;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

/**
 * <p>
 * 第三方平台分享工具类，分享的真正实现
 * </p>
 * @author ${huangqh}
 * @version ${Version}
 * @since 1.0.0
 * 
 */
public class UmengShareUtils {
    private final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
    private Context context;
    private SHARE_MEDIA currPlatform;

    // ...返回分享是否成功的信息，用来对“正在分享...”进度条是否的控制
    private Handler responseHandler;
    public static final int MSG_SHARE_START = 0;
    public static final int MSG_SHARE_SUCCESS = 1;
    public static final int MSG_SHARE_FAILED = 2;
    public static final int MSG_SHARE_CANCEL = 3;
    public static final int MSG_SHARE_AUTH_START = 101;
    public static final int MSG_SHARE_AUTH_ERROR = 102;
    public static final int MSG_SHARE_AUTH_COMPLETE = 103;
    public static final int MSG_SHARE_AUTH_CANCLE = 104;

    private String mapURL;
    private String mapTitle;
    private String mapDes;
    private UMImage mapThumbnailImage;

    // 微信开发平台注册应用的AppID/appSecret
    private final String WEIXIN_APPID = "wx87c340b281d26aec";
    private final String WEIXIN_APPSECRET = "f64f6cbdd1039e76fccc8b5beaa72a4a";
    private final String QZONE_APPID = "1104814324";
    private final String QZONE_APPSECRET = "e72ab79jUgPeRlKN";

    public UmengShareUtils(Context context) {
        this.context = context;
        initPlatSDK();

        // 关闭toast
        if (mController!=null){
            mController.getConfig().closeToast();
        }
    }

    public UMSocialService getController() {
        return mController;
    }

    public void setHandler(Handler responseHandler) {
        this.responseHandler = responseHandler;
    }

    /**
     * 必设参数—地图缩略图URL
     * @param mapThumbnailURL
     */
    public void setMapThumbnailURL(String mapThumbnailURL) {
        this.mapThumbnailImage =new UMImage(context, mapThumbnailURL);
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

    private void initPlatSDK(){
        initWechat();
        initSina();
        initQzone();
    }

    private void initWechat() {
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(context, WEIXIN_APPID, WEIXIN_APPSECRET);
        wxHandler.addToSocialSDK();
        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(context, WEIXIN_APPID, WEIXIN_APPSECRET);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }

    private void initSina() {
        //设置新浪SSO handler
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
    }

    private void initQzone() {
        //参数1为当前Activity， 参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler((Activity) context, QZONE_APPID,
                QZONE_APPSECRET);
        qZoneSsoHandler.addToSocialSDK();
    }

    public void share(String tag) {
        // 将TAG转化为SHARE_MEDIA类型
        currPlatform = getShareType(tag);

        // 新浪微博需要授权
        if (SHARE_MEDIA.SINA.equals(currPlatform)) {
            // 如果未授权，则需要先授权
//            if (!OauthHelper.isAuthenticated(context, currPlatform)) {
                mController.doOauthVerify(context, currPlatform, new SocializeListeners.UMAuthListener() {
                    @Override
                    public void onStart(SHARE_MEDIA platform) {
                        responseHandler.sendEmptyMessage(MSG_SHARE_AUTH_START);
                    }
                    @Override
                    public void onError(SocializeException e, SHARE_MEDIA platform) {
                        responseHandler.sendEmptyMessage(MSG_SHARE_AUTH_ERROR);
                    }
                    @Override
                    public void onComplete(Bundle value, SHARE_MEDIA platform) {
                        responseHandler.sendEmptyMessage(MSG_SHARE_AUTH_COMPLETE);
                        //获取相关授权信息或者跳转到自定义的分享编辑页面
                        //String uid = value.getString("uid");
                        // ...授权成功，进行分享
                        doShare(currPlatform);
                    }
                    @Override
                    public void onCancel(SHARE_MEDIA platform) {
                        responseHandler.sendEmptyMessage(MSG_SHARE_AUTH_CANCLE);
                    }
                });
//            }
//            // 已授权，直接分享
//            else {
//                doShare(currPlatform);
//            }
        }
        // 其它平台无需授权
        else {
            doShare(currPlatform);
        }

    }

    private SHARE_MEDIA getShareType(String tag) {
        if (SharePopupWindow.TAG_SINA.equals(tag)) {
            return SHARE_MEDIA.SINA;
        } else if (SharePopupWindow.TAG_WEIXIN.equals(tag)) {
            return SHARE_MEDIA.WEIXIN;
        } else if (SharePopupWindow.TAG_WEIXINCIRCLE.equals(tag)) {
            return SHARE_MEDIA.WEIXIN_CIRCLE;
        } else if (SharePopupWindow.TAG_QZONE.equals(tag)) {
            return SHARE_MEDIA.QZONE;
        }
        return null;
    }

    private void doShare(SHARE_MEDIA share_media) {
        if (SHARE_MEDIA.WEIXIN.equals(share_media)) {
            prepareContent_for_weixin();
        } else if (SHARE_MEDIA.WEIXIN_CIRCLE.equals(share_media)) {
            prepareContent_for_weixinCircle();
        } else if (SHARE_MEDIA.SINA.equals(share_media)) {
            prepareContent_for_sina();
        } else if (SHARE_MEDIA.QZONE.equals(share_media)) {
            prepareContent_for_qzone();
        }
        share_post();
    }

    //微信分享必须设置targetURL，需要为http链接格式
    private void prepareContent_for_weixin() {
        //设置微信好友分享内容
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        //设置分享文字
        weixinContent.setShareContent(mapDes);
        //设置title
        weixinContent.setTitle(mapTitle);
        //设置分享内容跳转URL
        weixinContent.setTargetUrl(mapURL);
        //设置分享图片
        weixinContent.setShareImage(mapThumbnailImage);
        mController.setShareMedia(weixinContent);
    }

    // 微信朋友圈只能显示title，并且过长会被微信截取部分内容
    private void prepareContent_for_weixinCircle() {
        //设置微信朋友圈分享内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(mapDes);
        circleMedia.setTitle(mapTitle);
        circleMedia.setTargetUrl(mapURL);
        circleMedia.setShareImage(mapThumbnailImage);
        mController.setShareMedia(circleMedia);
    }


    private void prepareContent_for_sina() {
        SinaShareContent sinaShareContent=new SinaShareContent();
        // ...设置分享内容
        String content = mapTitle;
        if (!TextUtils.isEmpty(mapDes)) {
            content += ":" + mapDes;
        }
        content += mapURL;
        sinaShareContent.setShareContent(content);
        // ...缩略图
        sinaShareContent.setShareImage(mapThumbnailImage);
        mController.setShareMedia(sinaShareContent);
    }

    private void prepareContent_for_qzone() {
        QZoneShareContent qzone = new QZoneShareContent();
        //设置分享文字
        qzone.setShareContent(mapDes);
        //设置点击消息的跳转URL
        qzone.setTargetUrl(mapURL);
        //设置分享内容的标题
        qzone.setTitle(mapTitle);
        //设置分享图片
         qzone.setShareImage(mapThumbnailImage);
        mController.setShareMedia(qzone);
    }

    private void share_post() {
        // directShare---直接分享
        // postShare---有分享编辑页，只针对微博，微信两者没有区别。
        mController.postShare(context, currPlatform,
                new SocializeListeners.SnsPostListener() {
                    @Override
                    public void onStart() {
                        // 开始
                        if (responseHandler!=null){
                            responseHandler.sendEmptyMessage(MSG_SHARE_START);
                        }
                    }
                    @Override
                    public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                        LogUtils.e("平台:"+platform.name()+"ecode="+eCode+"\nentity.getNickName()="+entity.getNickName());
                        // 成功
                        if ( StatusCode.ST_CODE_SUCCESSED==eCode) {
                             if (responseHandler!=null){
                                responseHandler.sendEmptyMessage(MSG_SHARE_SUCCESS);}
                        } else {
                            // 取消
                            if (StatusCode.ST_CODE_ERROR_CANCEL == eCode){
                                responseHandler.sendEmptyMessage(MSG_SHARE_CANCEL);
                                return;
                            }
                            // 失败
                            Message message = new Message();
                            if (StatusCode.ST_CODE_SDK_NO_OAUTH == eCode) {
                                message.obj= context.getString(R.string.map_share_no_auth);
                            }else{
                                message.obj = context.getString(R.string.map_share_failed_error_code)+"[" + eCode+"]";
                            }
                            if (responseHandler != null) {
                                responseHandler.sendMessage(message);
                            }
                        }
                    }
                });
    }
}
