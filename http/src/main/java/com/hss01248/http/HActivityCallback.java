package com.hss01248.http;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;

import java.lang.ref.WeakReference;

public class HActivityCallback implements Application.ActivityLifecycleCallbacks {
   static WeakReference<Activity> ref;

   public static Activity getTop(){
       if(ref == null){
           return null;
       }
       if(ref.get() == null){
           return null;
       }
       if(ref.get().isFinishing() ){
           return null;
       }
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
           if(ref.get().isDestroyed()){
               return null;
           }
       }
       return ref.get();
   }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        ref = new WeakReference<>(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        ref = new WeakReference<>(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
