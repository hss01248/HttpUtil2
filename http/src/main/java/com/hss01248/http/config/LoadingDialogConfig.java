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

    /**
     * 注意: 如果是自定义的dialog,那么setContentView应该在onShowListener的回调里调用:
     *
     * .setDefaultLoadingDialog(new LoadingDialogConfig.ILoadingDialog() {
                @Override
                public Dialog buildLoadingDialog(Context context, String msg) {
                    ProgressDialog dialog =  new ProgressDialog(context);
                    //dialog.setContentView(R.layout.toast_layout);
                    dialog.setMessage(msg);
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog0) {
                            dialog.setContentView(R.layout.toast_layout);
                            }
                        });
                return dialog;
                }
                })
     * @param context
     * @param msg
     * @return
     */
    Dialog buildLoadingDialog(Context context, String msg);
}

}
