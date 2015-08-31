package com.supermap.mytracks.utils;

import com.supermap.mytracks.customUI.CustomProgressDialog;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * <p>
 * 弹出对话框工具类
 * </p>
 * @author ${huangqh}
 * @version ${Version}
 * @since 1.0.0
 * 
 */
public class DialogUtils {
    /**
     * 显示进度条对话框
     *
     * @param context
     * @param title
     * @param message
     * @return
     */
    public static ProgressDialog showPgDialog(Context context, String title, String message) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        if (title != null) {
            pd.setTitle(title);
        }
        pd.setMessage(message);
        pd.setIndeterminate(false);
        pd.setCancelable(false);
        return pd;
    }

    public static CustomProgressDialog startProgressDialog(Context context, String message) {
        return startProgressDialog(context, null, message);
    }

    public static CustomProgressDialog startProgressDialog(Context context, CustomProgressDialog progressDialog, String message) {
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(context);
        }
        progressDialog.setMessage(message);
        progressDialog.show();
        return progressDialog;
    }

    public static void stopProgressDialog(CustomProgressDialog progressDialog) {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
