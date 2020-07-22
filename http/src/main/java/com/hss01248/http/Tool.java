package com.hss01248.http;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.hss01248.http.callback.BaseSubscriber;
import com.hss01248.http.config.LoadingDialogConfig;

import java.io.File;
import java.net.URLDecoder;
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

    public static Handler getMainHandler() {
        if (mainHandler == null) {
            mainHandler = new android.os.Handler(Looper.getMainLooper());
        }
        return mainHandler;
    }

    private static android.os.Handler mainHandler;

    private static ConcurrentHashMap<Object,Set<Disposable>> callMap = new ConcurrentHashMap<>();


    public static boolean isNetworkAvailable() {
        ConnectivityManager conManager = (ConnectivityManager) HttpUtil.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conManager != null) {
            NetworkInfo[] netInf = conManager.getAllNetworkInfo();
            for (int i = 0; i < netInf.length; i++) {
                if (netInf[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getString(int id){
        return HttpUtil.context.getResources().getString(id);
    }

    /**
     * 检查是否存在代理,System.getProperty,有io操作
     * @return
     */
    public static boolean detectIfProxyExist() {
        boolean IS_ICS_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
        String proxyHost;
        int proxyPort;
        try {
            if (IS_ICS_OR_LATER) {
                proxyHost = System.getProperty("http.proxyHost");
                String port = System.getProperty("http.proxyPort");
                proxyPort = Integer.parseInt(port != null ? port : "-1");
            } else {
                proxyHost = android.net.Proxy.getHost(GlobalConfig.get().getContext());
                proxyPort = android.net.Proxy.getPort(GlobalConfig.get().getContext());

            }
            return proxyHost != null && proxyPort != -1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 取消请求,常在activity ondestory处调用.直接传入activity即可,不会保存引用,直接识别其名字作为tag
     * @param obj 内部以obj.tostring来保存
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
        obj = obj.toString();
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

    /**
     * 内部以obj.tostring为key来保存
     * @param obj
     * @param disposable
     */
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
        String str = obj.toString();
        Set<Disposable> set = null;
        if(!callMap.containsKey(str)){
            set = new HashSet<>();
            callMap.put(str,set);
        }else {
            set = callMap.get(str);
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
            //fastjson在华为荣耀6上直接anr
            GlobalConfig.get().getTool().logdJson(GlobalConfig.get().getTool().toJsonStr(t));
            //GlobalConfig.get().getTool().logObj(t);
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


    public static <T> boolean showLoadingDialog(LoadingDialogConfig dialogConfig, Object tagForCancel, BaseSubscriber<T> subscriber) {
        if(dialogConfig ==null ){
            return false;
        }
        if(dialogConfig.getDialog() != null && dialogConfig.getDialog().isShowing()){
            return false;
        }

        Activity activity = dialogConfig.getActivity();
        if(activity == null){
            activity = Tool.getTopActivity();
        }
        if(tagForCancel instanceof Activity){
            activity = (Activity) tagForCancel;
        }
        if(!Tool.isUseable(activity)){
            return false;
        }

        String msg = dialogConfig.getMsg();
        if(dialogConfig.getStringResId() != 0){
            msg = activity.getResources().getString(dialogConfig.getStringResId());
        }
        final Dialog[] dialog = {null};
        Activity finalActivity = activity;
        String finalMsg = msg;
        runOnUI(new Runnable() {
            @Override
            public void run() {
                if(GlobalConfig.get().getDefaultLoadingDialog() != null){
                    dialog[0] = GlobalConfig.get().getDefaultLoadingDialog().buildLoadingDialog(finalActivity, finalMsg);
                }else {
                    ProgressDialog progressDialog = new ProgressDialog(finalActivity);
                    progressDialog.setMessage(finalMsg);
                    if(dialogConfig.isShowProgress()){
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    }
                    dialog[0] = progressDialog;
                }
                dialog[0].setCancelable(dialogConfig.isCancelable());
                dialogConfig.setDialog(dialog[0]);

                Object object = tagForCancel;
                if(object == null){
                    object = dialog[0];
                    Tool.addByTag(object, subscriber);
                }

                Object finalObject = object;
                dialog[0].setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        cancelByTag(finalObject,subscriber,false);
                    }
                });

                dialog[0].setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        cancelByTag(finalObject,subscriber,true);
                    }
                });
                dialog[0].show();//在回调的start里去show
            }
        });
        return true;
    }

    public static <T> boolean dismissLoadingDialog(LoadingDialogConfig dialogConfig, Object tagForCancel) {
        if(dialogConfig == null){
            return false;
        }
        if(dialogConfig.getDialog() != null){
            runOnUI(new Runnable() {
                @Override
                public void run() {
                    dialogConfig.getDialog().dismiss();
                }
            });
            return true;
        }
        return false;


    }

    public static String urlDecode(String str){
        try {
            str = str.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            String urlStr = URLDecoder.decode(str, "UTF-8");
            return urlStr;
        }catch (Exception e){
            e.printStackTrace();
            return str;
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conManager != null) {
            NetworkInfo[] netInf = conManager.getAllNetworkInfo();
            for (int i = 0; i < netInf.length; i++) {
                if (netInf[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }
}
