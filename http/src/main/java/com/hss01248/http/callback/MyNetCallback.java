package com.hss01248.http.callback;


import android.support.annotation.Nullable;

import com.hss01248.http.config.LoadingDialogConfig;

/**
 * Created by Administrator on 2016/4/15 0015.
 */
//@MainThread
public abstract class MyNetCallback<T>  extends BaseSubscriber<T>{


    public MyNetCallback(){
        super();
    }
    public MyNetCallback(boolean showLoadingDialog, @Nullable Object tagForCancel) {
        super(showLoadingDialog, tagForCancel);
    }

    public MyNetCallback(LoadingDialogConfig dialogConfig, @Nullable Object tagForCancel) {
        super(dialogConfig, tagForCancel);
    }


}
