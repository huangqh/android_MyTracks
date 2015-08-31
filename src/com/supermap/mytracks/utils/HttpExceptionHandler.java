package com.supermap.mytracks.utils;

import android.content.Context;
import android.widget.Toast;
import com.lidroid.xutils.exception.HttpException;
import com.supermap.mytracks.R;

/**
 * Created by xuty on 2014/12/26.
 * 用于处理http请求异常的处理，包括HttpHostConnectionException，ConnectTimeoutException等。。
 *
 * 处理的方式为：
 * 1. 如果页面没有内容，则返回错误提示信息，让页面展示异常状态界面；
 * 2. 如果页面已经有内容，则toast错误提示信息; 同时页面不再对异常信息进行处理。
 */
public class HttpExceptionHandler {
    private static long lastTimeToast;

    public static String handleExption(HttpException error,boolean isToast,Context context){
        String exceptionInfo = distinguishError(error,context);
        if (isToast) {
            // 10秒钟之内不会多次弹出toast
            if (lastTimeToast - System.currentTimeMillis() <= 1000 * 10) {
                Toast.makeText(context,exceptionInfo,Toast.LENGTH_SHORT).show();
            }
            lastTimeToast= System.currentTimeMillis();
        }
        return exceptionInfo;
    }

    /**
     * 识别错误类型
     * @return
     */
    private static String distinguishError(HttpException error,Context context){
        if (!CommonUtil.isNetworkConnected(context)) { //HttpHostConnectException
            return context.getString(R.string.tip_no_network);
        } else if (error.getMessage().contains("org.apache.http.conn.ConnectTimeoutException")) {
            return context.getString(R.string.tip_failed_timeout);
        } else {
            return context.getString(R.string.tip_client_failure);
        }
    }
}
