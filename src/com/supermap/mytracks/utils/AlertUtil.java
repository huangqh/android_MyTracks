package com.supermap.mytracks.utils;

import com.supermap.mytracks.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * <p>
 * 弹出窗口工具类
 * </p>
 * @author ${huangqh}
 * @version ${Version}
 * @since 1.0.0
 * 
 */
public class AlertUtil {
    /**
     * 中间屏幕弹出的警告对话框
     *
     * @param context
     * @param titlte
     * @param message
     */
    public static android.app.AlertDialog showAlert(Context context, String titlte, String message)
    {
        android.app.AlertDialog.Builder ab = new android.app.AlertDialog.Builder(context);
        ab.setIcon(R.drawable.ic_launcher);
        if (titlte != null)
        {
            ab.setTitle(titlte);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.alert_view_message, null);
        TextView textView = (TextView)view.findViewById(R.id.tv_alert_message);
        textView.setText(message);
        ab.setCancelable(false);
        ab.setView(view);
        android.app.AlertDialog alertDialog = ab.create();
        alertDialog.show();
        return alertDialog;
    }

    /**
     * 中间屏幕弹出的警告对话框,带确认按钮
     *
     * @param context
     * @param titlte
     * @param message
     */
    public static void showAlertConfirm(Context context, String titlte, String message)
    {
        android.app.AlertDialog.Builder ab = new android.app.AlertDialog.Builder(context);
        // ab.setIcon(R.drawable.ic_launcher);
        if (titlte != null)
        {
            ab.setTitle(titlte);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.alert_view_message, null);
        TextView textView = (TextView)view.findViewById(R.id.tv_alert_message);
        textView.setText(message);
        ab.setCancelable(false);
        ab.setView(view);
        ab.setPositiveButton(context.getResources().getString(R.string.confirm),
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.dismiss();
                    }
                });
        android.app.AlertDialog alertDialog = ab.create();
        alertDialog.show();

    }

    /**
     * 消息确认对话框 自定义确认按钮操作
     *
     * @param context
     * @param titlte
     * @param message
     */
    public static void showAlertConfirm(Context context, String titlte, String message,
                                        DialogInterface.OnClickListener PositiveButtonlistener,
                                        DialogInterface.OnClickListener NegativeButtonlistener)
    {
        android.app.AlertDialog.Builder ab = new android.app.AlertDialog.Builder(context);
        // ab.setIcon(R.drawable.ic_launcher);
        if (titlte != null)
        {
            ab.setTitle(titlte);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.alert_view_message, null);
        TextView textView = (TextView)view.findViewById(R.id.tv_alert_message);
        textView.setText(message);
        ab.setCancelable(false);
        ab.setView(view);
        if (NegativeButtonlistener == null)
        {
            ab.setNegativeButton(context.getResources().getString(R.string.cancle),
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            dialogInterface.dismiss();
                        }
                    });
        }
        else
        {
            ab.setNegativeButton(context.getResources().getString(R.string.cancle),
                    NegativeButtonlistener);
        }

        ab.setPositiveButton(context.getResources().getString(R.string.confirm),
                PositiveButtonlistener);
        android.app.AlertDialog alertDialog = ab.create();
        alertDialog.show();

    }

    // /只是网络的弹出框
    public static void showAlertConfirmNet(Context context, String titlte, String message,
                                           DialogInterface.OnClickListener PositiveButtonlistener,
                                           DialogInterface.OnClickListener NegativeButtonlistener)
    {
        android.app.AlertDialog.Builder ab = new android.app.AlertDialog.Builder(context);
        // ab.setIcon(R.drawable.ic_launcher);
        if (titlte != null)
        {//
            ab.setTitle(titlte);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.alert_view_message, null);
        TextView textView = (TextView)view.findViewById(R.id.tv_alert_message);
        textView.setText(message);
        ab.setCancelable(false);
        ab.setView(view);
        if (NegativeButtonlistener == null)
        {
            ab.setNegativeButton(context.getResources().getString(R.string.iknow),
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            dialogInterface.dismiss();
                        }
                    });
        }
        else
        {
            ab.setNegativeButton(context.getResources().getString(R.string.iknow),
                    NegativeButtonlistener);
        }

        ab.setPositiveButton(context.getResources().getString(R.string.setnet),
                PositiveButtonlistener);
        android.app.AlertDialog alertDialog = ab.create();
        try
        {
            if (alertDialog != null) alertDialog.show();
        }
        catch (Exception exception0)
        {}

    }

    /**
     * 消息确认对话框 自定义确认按钮操作
     *
     * @param context
     * @param titlte
     * @param message
     */
    public static void showAlertConfirm(Context context, String titlte, String message,
                                        DialogInterface.OnClickListener PositiveButtonlistener)
    {
        android.app.AlertDialog.Builder ab = new android.app.AlertDialog.Builder(context);
        // ab.setIcon(R.drawable.ic_launcher);
        if (titlte != null)
        {
            ab.setTitle(titlte);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.alert_view_message, null);
        TextView textView = (TextView)view.findViewById(R.id.tv_alert_message);
        textView.setText(message);
        ab.setCancelable(false);
        ab.setView(view);

        ab.setPositiveButton(context.getResources().getString(R.string.confirm),
                PositiveButtonlistener);
        android.app.AlertDialog alertDialog = ab.create();
        alertDialog.show();

    }
    
    public static final void showResultDialog(Context context, String msg,
            String title) {
        if(msg == null) return;
        String rmsg = msg.replace(",", "\n");
//        Log.d("AlertUtil", rmsg);
        new AlertDialog.Builder(context).setTitle(title).setMessage(rmsg)
                .setNegativeButton("知道了", null).create().show();
    }

}
