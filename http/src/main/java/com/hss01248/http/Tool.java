package com.hss01248.http;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.hss01248.http.callback.BaseSubscriber;
import com.hss01248.http.config.LoadingDialogConfig;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.disposables.Disposable;

/**
 * Created by hss on 2018/7/29.
 */

public class Tool {

    private static android.os.Handler mainHandler;

    private static ConcurrentHashMap<Object,Set<Disposable>> callMap = new ConcurrentHashMap<>();


    /**
     * 取消请求,常在activity ondestory处调用.直接传入activity即可,不会保存引用,直接识别其名字作为tag
     * @param obj
     * @param disposable 不为空时,只移除特定的disposable
     */
    public static void cancelByTag(Object obj,@Nullable Disposable disposable,boolean closeSocket){
        //cancel(RiskRetrofitClient.instance().getHttpClient(),obj);
        //cancel(RetrofitClient.instance().getHttpClient(),obj);
        if(obj ==null){
            return;
        }
        if(callMap == null || callMap.isEmpty()){
            return;
        }
        /*if(obj instanceof Activity){
            obj = obj.toString();
        }
        if(obj instanceof Fragment || obj instanceof android.support.v4.app.Fragment){
            obj = obj.toString();
        }*/
        // XLogUtil.obj(callMap);
        // XLogUtil.obj("key:"+obj);
        if(!callMap.containsKey(obj)){
            return;
        }
        boolean cancelAll = disposable == null;
        Set<Disposable> calls = cancelAll ? callMap.remove(obj) : callMap.get(obj);//从gc root引用中删除
        if(calls == null || calls.isEmpty()){
            return;
        }

        Iterator<Disposable> iterator = calls.iterator();
        while (iterator.hasNext()){
            Disposable disposable1 = iterator.next();
            if(cancelAll){
                if(!disposable1.isDisposed() && closeSocket){
                    disposable1.dispose();
                    logd(":---really cancel a call:"+disposable1+"----tagForCancle:"+obj);
                }
            }else {
                if(disposable.equals(disposable1)){
                    if(!disposable1.isDisposed()&& closeSocket){
                        disposable1.dispose();
                        logd(":---really cancel a call:"+disposable1+"----tagForCancle:"+obj);
                    }
                    iterator.remove();
                }
            }
        }
        if(!cancelAll){
            if(calls.isEmpty()){
                callMap.remove(obj);
            }
        }
        logObj(callMap);
    }

    public static void addByTag(Object obj,Disposable disposable){
        if(obj == null){
            return;
        }
        if(disposable == null){
            return;
        }
        if(callMap == null ){
            callMap = new ConcurrentHashMap<>();
        }
        Set<Disposable> set = null;
        if(!callMap.containsKey(obj)){
            set = new HashSet<>();
            callMap.put(obj,set);
        }else {
            set = callMap.get(obj);
        }

        set.add(disposable);
        logObj(callMap);


    }








    private static String getSuffix(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return null;
        }
        String fileName = file.getName();
        if (fileName.equals("") || fileName.endsWith(".")) {
            return null;
        }
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            return fileName.substring(index + 1).toLowerCase(Locale.US);
        } else {
            return null;
        }
    }


    public static String getMimeType(String fileUrl) {


        String suffix = getSuffix(new File(fileUrl));
        if (suffix == null) {
            return "file";
        }
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix);
        if (!TextUtils.isEmpty(type)) {
            return type;
        }
        return "file";
    }

    public static void runOnUI(Runnable runnable) {
        if (mainHandler == null) {
            mainHandler = new android.os.Handler(Looper.getMainLooper());
        }
        mainHandler.post(runnable);
    }

    public static void logd(String str){
        if(GlobalConfig.get().isOpenLog()){
            GlobalConfig.get().getTool().logd(str);
        }
    }
    public static void logi(String str){
        if(GlobalConfig.get().isOpenLog()){
            GlobalConfig.get().getTool().logi(str);
        }
    }
    public static void logw(String str){
        if(GlobalConfig.get().isOpenLog()){
            GlobalConfig.get().getTool().logw(str);
        }
    }
    public static void logJson(String str){
        if(GlobalConfig.get().isOpenLog()){
            GlobalConfig.get().getTool().logdJson(str);
        }
    }

    public static void logJson(Object t){
        if(GlobalConfig.get().isOpenLog()){
            GlobalConfig.get().getTool().logdJson(GlobalConfig.get().getTool().toJsonStr(t));
        }
    }

    public static void logObj(Object t){
        if(GlobalConfig.get().isOpenLog()){
            GlobalConfig.get().getTool().logObj(t);
        }
    }

    public static Activity getTopActivity(){
       return GlobalConfig.get().getTool().getTopActivity();
    }

    public static boolean isUseable(Activity activity) {
        if(activity == null){
            return false;
        }
        if(activity.isFinishing()){
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (activity.isDestroyed()){
                return false;
            }
        }
        return true;
    }


    public static <T> void showLoadingDialog(LoadingDialogConfig dialogConfig, Object tagForCancel, BaseSubscriber<T> subscriber) {
        if(dialogConfig ==null ){
            return;
        }
        if(dialogConfig.getDialog() != null && dialogConfig.getDialog().isShowing()){
            return;
        }

        Activity activity = dialogConfig.getActivity();
        if(activity == null){
            activity = Tool.getTopActivity();
        }
        if(tagForCancel instanceof Activity){
            activity = (Activity) tagForCancel;
        }
        if(!Tool.isUseable(activity)){
            return;
        }
        ProgressDialog dialog = new ProgressDialog(activity);
        String msg = dialogConfig.getMsg();
        if(dialogConfig.getStringResId() != 0){
            msg = activity.getResources().getString(dialogConfig.getStringResId());
        }
        dialog.setMessage(msg);
        dialog.setCancelable(dialogConfig.isCancelable());

        if(dialogConfig.isShowProgress()){
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        }
        dialogConfig.setDialog(dialog);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                cancelByTag(tagForCancel,subscriber,false);
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancelByTag(tagForCancel,subscriber,false);
            }
        });


        dialog.show();//在回调的start里去show

    }

    public static <T> boolean dismissLoadingDialog(LoadingDialogConfig dialogConfig, Object tagForCancel, BaseSubscriber<T> subscriber) {
        if(dialogConfig == null){
            return false;
        }

        if(dialogConfig.getDialog() != null){
            dialogConfig.getDialog().dismiss();
            return true;
        }
        return false;


    }
}
