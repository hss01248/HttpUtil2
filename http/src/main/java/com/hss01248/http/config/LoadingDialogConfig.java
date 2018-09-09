package com.hss01248.http.config;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

/**
 * Created by hss on 2018/9/2.
 */

public class LoadingDialogConfig {
    private Dialog dialog;
    private int stringResId;
    private String msg = "loading...";
    private Activity activity;
    private boolean showProgress;
    private boolean cancelable;

    public static LoadingDialogConfig newInstance(){
        return new LoadingDialogConfig();
    }


    public LoadingDialogConfig setDialog(Dialog dialog) {
        this.dialog = dialog;
        return this;
    }

    public LoadingDialogConfig setStringResId(int stringResId) {
        this.stringResId = stringResId;
        return this;
    }

    public LoadingDialogConfig setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public LoadingDialogConfig setActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    public LoadingDialogConfig setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
        return this;
    }

    public LoadingDialogConfig setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }



    public Dialog getDialog() {
        return dialog;
    }

    public int getStringResId() {
        return stringResId;
    }

    public String getMsg() {
        return msg;
    }

    public Activity getActivity() {
        return activity;
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public boolean isCancelable() {
        return cancelable;
    }


public interface ILoadingDialog{
        Dialog showLoadingDialog(Context context,String msg);
}

}
