package com.supermap.mytracks.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * <p>
 * 显示时，根据分辨率实现像素和dp直接的转换等
 * </p>
 * @author ${huangqh}
 * @version ${Version}
 * @since 1.0.0
 * 
 */
public class DisplayUtil {
    private static DisplayMetrics metrics = null;

    public static int dpWidth(Context context) {
        getDisplayMetrics(context);
        return (int)(metrics.widthPixels / metrics.density);
    }

    public static int dpHeight(Context context) {
        getDisplayMetrics(context);
        return (int)(metrics.heightPixels /  metrics.density);
    }

    public static int pxWidth(Context context) {
        getDisplayMetrics(context);
        return metrics.widthPixels;
    }

    public static int pxHeight(Context context) {
        getDisplayMetrics(context);
        return metrics.heightPixels;
    }

    public static int getDPI(Context context) {
        getDisplayMetrics(context);
        return metrics.densityDpi;
    }
    
    /**
     * 获取屏幕的像素值
     * @param context
     * @param dp
     * @return
     */
    public static double getPxFromDp(Context context, double dp) {
        getDisplayMetrics(context);
        return dp * metrics.density;
    }
    
    /**
     * 获取屏幕的dp值
     * @param context
     * @param px
     * @return
     */
    public static double getDpFromPx(Context context, double px){
        getDisplayMetrics(context);
        return px/metrics.density;
    }

    private static DisplayMetrics getDisplayMetrics(Context context) {
        if (metrics == null) {
            metrics = new DisplayMetrics();
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            manager.getDefaultDisplay().getMetrics(metrics);
        }
        return metrics;
    }
}
