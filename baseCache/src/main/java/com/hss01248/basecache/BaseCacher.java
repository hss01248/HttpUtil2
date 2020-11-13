package com.hss01248.basecache;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;


import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;

import java.lang.reflect.ParameterizedType;



public abstract class BaseCacher<T> {

    private static final String SP_FILE_NAME = "BaseCacher";
    T cacheInfo;
    static Context context;

    public static void init(Context context){
        BaseCacher.context = context;

    }

    protected BaseCacher(){

    }



    /**
     * 进入app时调用
     */
    public  void prefetch(){
        get();
        get(false,true,false,null);
    }

    public void clearCache(){
        cacheInfo = null;
        putString(getClass().getSimpleName(),"");
    }

    /**
     * 返回缓存的配置
     * @return
     */
    public   T get(){
        if(cacheInfo != null){
            return cacheInfo;
        }
        String json = getString(getClass().getSimpleName(),"");
        if(!TextUtils.isEmpty(json)) {
            cacheInfo = new Gson().fromJson(json, $Gson$Types.canonicalize(((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0]));
            return cacheInfo;
        }
        return null;
    }

     static void putString(String key, String val) {
        getSP().edit().putString(key, val).apply();
    }

     static String getString(String key, String defVal) {
        return getSP().getString(key, defVal);
    }

     static SharedPreferences getSP() {
        return context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 有缓存就读缓存,没有缓存就请求网络
     * @param callBack
     */
    public  void get(IRequestCallback<T> callBack ){
        get(false,false,false,callBack);
    }

    /**
     *
     * @param fastReturn 是否快速返回结果(强制不进行网络请求) 优先级最高.
     *                   为true时,forceFromServer和readCacheIfServerFail不起作用.
     *                   为false时, 有缓存则读缓存,没有缓存,则进行网络请求.
     * @param forceFromServer 在fastReturn=false时起作用. 是否强制进行网络请求.不读本地缓存. 不得滥用
     * @param readCacheIfServerFail 在forceFromServer=true时起作用. 强制拉远程时,如果远程失败,是否读本地缓存的配置.

     一般建议: 需要立马返回结果,不转圈圈,不等待: fastReturn:true
              需要最新的服务端数据: fastReturn: false     forceFromServer: true
                    此时,如果请求失败,也需要本地缓存的配置,那么设置: readCacheIfServerFail:true.
                         如果不能接受本地数据,失败就失败,那么配置: readCacheIfServerFail:false
     *
     */
    public  void get(boolean fastReturn, boolean forceFromServer, boolean readCacheIfServerFail, IRequestCallback<T> callBack ){
        if(fastReturn){
            forceFromServer = false;
        }
        if(!forceFromServer){
            T configInfos = get();
            if(configInfos != null){
                if(callBack != null){
                    callBack.onSuccess(configInfos);
                }
                return;
            }else {
                if(fastReturn){
                    if(callBack != null){
                        callBack.onError("001","no  cache");
                    }
                    return;
                }
            }
        }
        boolean finalForceFromServer = forceFromServer;
        doRequest(new IRequestCallback<T>() {
            @Override
            public void onSuccess(T bean) {
                cacheInfo = bean;
                putString(BaseCacher.this.getClass().getSimpleName(), new Gson().toJson(bean));
                if(callBack!=null){
                    callBack.onSuccess(bean);
                }
            }

            @Override
            public void onError(String code, String msg) {
                if(finalForceFromServer && readCacheIfServerFail){
                    T configInfos = get();
                    if(configInfos != null){
                        if(callBack != null){
                            callBack.onSuccess(configInfos);
                        }
                        return;
                    }
                }
                if(callBack!=null){
                    callBack.onError(code, msg);
                }

            }
        });
    }

    protected abstract void doRequest(IRequestCallback<T> callBack);

    public interface IRequestCallback<T>{
         void onSuccess(T bean);
       void onError(String code, String msg);
    }
}
