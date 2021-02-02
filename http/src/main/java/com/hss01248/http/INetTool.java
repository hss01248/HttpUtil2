package com.hss01248.http;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by huangshuisheng on 2017/12/25.
 */

public interface INetTool {

    default String toJsonStr(Object obj){
      return   Tool.getGson().toJson(obj);
    }

   default   <T> T parseObject(String str, Class<T> clazz){
       return Tool.getGson().fromJson(str,clazz);
   }

   /* public static <T> T parse(String str, Class<T> clazz) {
        // return new Gson().fromJson(str,clazz);
        return JSON.parseObject(str, clazz);
    }*/

    default  <E> List<E> parseArray(String str, Class<E> clazz){
        return Tool.getGson().fromJson(str,new TypeToken<ArrayList<E>>(){}.getType());
     }


    default void logi(String str){
        if(GlobalConfig.get().isDebug()){
            Log.i("httputil",str);
        }
    }

  default   void logd(String str){
      if(GlobalConfig.get().isDebug()){
          Log.d("httputil",str);
      }
  }

   default void logw(String str){
       if(GlobalConfig.get().isDebug()){
           Log.w("httputil",str);
       }
   }

   default void logdJson(String json){
       if(GlobalConfig.get().isDebug()){
           Log.d("httputil",json);
       }
   }

   default void initialStetho(Application application){}

  default   void addChuckInterceptor(OkHttpClient.Builder builder){}

   default void addStethoInterceptor(OkHttpClient.Builder builder){}

   default void addHttpLogInterceptor(OkHttpClient.Builder builder){
       if(GlobalConfig.get().isDebug()){
           builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
       }
   }

   default Activity getTopActivity(){
        return HActivityCallback.getTop();
   }

   default void logObj(Object t){
       if(GlobalConfig.get().isDebug()){
           Log.d("httputil",new GsonBuilder().setPrettyPrinting().serializeNulls().create().toJson(t));
       }
   }

   default void reportError(String code,String msg,String url){
       logw( code+" ,"+msg+" , "+url );
   }
}
