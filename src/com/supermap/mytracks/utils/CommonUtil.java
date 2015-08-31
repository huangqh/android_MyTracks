package com.supermap.mytracks.utils;

import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import java.io.*;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import com.supermap.mytracks.common.Paths;

/**
 * 通用助手类
 */
public class CommonUtil {

    /**
     * 在外部存储目录上创建本应用的存储目录
     */
    public static void createDir(String foldername) {
        File dir = new File(foldername);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static boolean isFileExist(String fileName) {
        File file = new File(fileName);
        file.isFile();
        return file.exists();
    }
    
    public static void createSysDir(){
        createDir(Paths.PRODUCT);
        createDir(Paths.LICENSE);
        createDir(Paths.LOG);
        createDir(Paths.WEBCACHE);
    }
    
    public static void initLicense(Context context) {
        File licenseDir = new File(Paths.LICENSE);
        if (!licenseDir.exists()) {
            licenseDir.mkdirs();
            configLicense(context);
        } else {
            File file = new File(Paths.LICENSE + Paths.LICENSENAME);
            if (!file.exists()) {
                configLicense(context);
            }
        }
    }

    private static void configLicense(Context context) {
        AssetManager am = context.getAssets();
        InputStream is;
        try {
            is = am.open(Paths.LICENSENAME);
            if (is != null) {
                File file = new File(Paths.LICENSE + Paths.LICENSENAME);
                FileUtils.copyInputStreamToFile(is, file);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * 得到应用的版本
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return verCode;
    }

    public static String getVersionName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return verName;
    }

    /**
     * 检测网络是否可用
     *
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    /**
     * MD5 取HASH值
     *
     * @param str
     * @return
     */
    public static String getMD5Str(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }

        return md5StrBuff.toString();
    }

    /**
     * textView.setCompoundDrawables()等方法需要传入的Drawable，需要有Bounds属性
     * @param context
     * @param id
     * @return
     */
    public static Drawable getDrawableWithBound(Context context, int id) {
        Drawable drawable = context.getResources().getDrawable(id);
        // 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        return drawable;
    }

    /**
     * 邮箱用户名对@邮箱地址使用***代替
     * @param name
     * @return
     */
    public static String getNickName(String name) {
        if (!TextUtils.isEmpty(name)) {
            // 电子邮件正则表达匹配
            String check = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(name);
            if (matcher.matches()) {
                int index = name.indexOf("@");
                String nickName = name.substring(0, index);
                nickName += "@***";
                return nickName;
            }

        }
        return name;
    }
    
    /**
     * 输入毫秒数返回日期yyyy-MM-dd
     * @param time
     * @return
     */
    public static String getDate(long time){
        Date d = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(d);
    }
}
