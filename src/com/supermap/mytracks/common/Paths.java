package com.supermap.mytracks.common;

import android.os.Environment;

/**
 * <p>
 * 路径参数枚举类
 * </p>
 * @author ${huangq}
 * @version ${Version}
 * @since 1.0.0
 * 
 */
public class Paths {
    // 默认创建应用程序的文件夹，相关的文件名与路径
    public final static String PRODUCT = Environment.getExternalStorageDirectory() + "/myTracks/";
    public final static String LOG = PRODUCT + "log/";
    public final static String LICENSE = PRODUCT + "license/";
    public final static String WEBCACHE = PRODUCT + "webcache/";
    public static final String LOGPATH = PRODUCT + "crash/";
    public static final String LICENSENAME = "TrialLicense.slm";
}
