package com.silvrr.akuhttp;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * Created by Administrator on 2017/2/15 0015.
 *
 * 参考:https://zhuanlan.zhihu.com/p/25221428
 */

public class ActivityStackManager {


    private static ActivityStackManager sInstance = new ActivityStackManager();
    private WeakReference<Activity> topAttachedActivityWeakRef;
    private static Stack<Activity> mActivityStack = new Stack<>();


    private ActivityStackManager() {

    }

    public static ActivityStackManager getInstance() {
        return sInstance;
    }

    public Stack<Activity> getActivityStack(){
        if(mActivityStack != null){
            return mActivityStack;
        }
        return null;
    }

    /**
     * activity from oncreate callback is useable for dialog ,but not usable for popupwindow
     * @return
     */
    public Activity getTopActivity() {
        Activity currentActivity = null;
        if(mActivityStack.size()>0){
            currentActivity = mActivityStack.get(mActivityStack.size()-1);
        }
        /*Activity currentActivity = null;
        if (sCurrentActivityWeakRef != null) {
            currentActivity = sCurrentActivityWeakRef.get();
            if(currentActivity ==null){
                if(mActivityStack.size()>0){
                    currentActivity = mActivityStack.get(mActivityStack.size()-1);
                }
            }
            if(currentActivity !=null && currentActivity.isFinishing()){
                currentActivity = null;
            }
        }else {
            if(mActivityStack.size()>0){
                currentActivity = mActivityStack.get(mActivityStack.size()-1);
            }
            if(currentActivity !=null && currentActivity.isFinishing()){
                currentActivity = null;
            }
        }*/
        return currentActivity;
    }

    public Activity getTopActivity(Class activityExpected){
        Activity activity = getTopActivity();
        if(activity !=null && activity.getClass().equals(activityExpected)){
            return activity;
        }else {
            return null;
        }
    }

    public Activity getActivity(Class activityExpected){
        if (mActivityStack == null) {
            return null;
        }
        for (Activity activity1 : mActivityStack) {
            if (activity1.getClass().equals(activityExpected)) {
                return activity1;
            }
        }
        return null;
    }

    public Activity getActivityAt(int index){
        Activity activity = null;
        if (mActivityStack == null) {
            return null;
        }
        if(mActivityStack.size() >= index){
            activity =  mActivityStack.get(index);
        }
        return activity;
    }

    /**
     * 获取指定位置的activity
     * 
     */
    public boolean finishActivityByIdx(int idxTop){
        if(mActivityStack.isEmpty()){
            return false;
        }
        int index = mActivityStack.size() -idxTop;
       Activity activity =  mActivityStack.remove(index);
       if(activity !=null && !activity.isFinishing()){
           activity.finish();
           return true;
       }
       return true;
    }


    /**
     * 在基类activity 的 onAttachToWindow时调用
     * @param activity
     */
    public void setTopAttached(Activity activity) {
        topAttachedActivityWeakRef = new WeakReference<Activity>(activity);
    }

    /**
     * 在基类activity 的 onDetachToWindow时调用
     * @param activity
     */
    public void removeTopAttached(Activity activity){
        if(activity ==null){
            return;
        }
        if(topAttachedActivityWeakRef!=null){
            Activity activity1 =  topAttachedActivityWeakRef.get();
            if(activity.equals(activity1)){
                topAttachedActivityWeakRef = null;
            }

        }
    }

    /**
     * if return in not null,then it can be used for a popupwindow
     * @return
     */
    public Activity getTopAttached(){
        if(topAttachedActivityWeakRef!=null){
            Activity activity =  topAttachedActivityWeakRef.get();
            if(isUsable(activity)){
                return activity;
            }
        }
        return null;
    }

    public static boolean isUsable(Activity activity) {
        if(activity ==null){
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

        //是否attached
        /*if(activity.getWindowManager() ==null){
            return false;
        }
        if(!activity.getWindow().isActive()){
            return false;
        }*/

        return true;
    }


    /**
     * 返回栈大小
     *
     * @return 大小
     */
    public int size() {
        return mActivityStack.size();
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (mActivityStack == null) {
            mActivityStack = new Stack<>();
        }
        if (activity != null) {
            mActivityStack.add(activity);
           // Log.i("dialog","mActivityStack.size()--addActivity:"+activity.getClass().getSimpleName()+mActivityStack.size());
        }
        printStack("onCreate:\n");
    }

    /**
     * 移除activity
     */
    public void removeActivity(Activity activity) {
        if(mActivityStack.contains(activity)){
            mActivityStack.remove(activity);
        }
       // DialogsMaintainer.onDestory(activity);
       // Log.i("dialog"," mActivityStack.size()--removeActivity:"+activity.getClass().getSimpleName()+mActivityStack.size());
        printStack("onDestory:\n");
    }

    private void printStack(String time) {
        StringBuilder builder = new StringBuilder();
        for (int i = mActivityStack.size()-1; i >=0; i--) {
            builder.append(mActivityStack.get(i).toString())
                .append("--index:")
                .append(i)
                .append("\n");
        }
        Log.i("printStack dialog",time+builder.toString());
    }

    /**
     * 判断某个activity是否还存活
     *
     * @param cls
     * @return
     */
    public boolean isActivityAlive(Class<?> cls) {
        if (mActivityStack == null) {
            return false;
        }

        for (Activity activity1 : mActivityStack) {
            if (activity1.getClass().equals(cls)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (mActivityStack == null) {
            return;
        }

        if (activity != null) {
            mActivityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        if (mActivityStack == null) {
            return;
        }

        try {
            Iterator<Activity> iterator = mActivityStack.iterator();
            List<Activity> destoryedActivities = new ArrayList<>();
            while (iterator.hasNext()){
                Activity activity = iterator.next();
                if(activity.getClass().equals(cls)){
                    iterator.remove();
                    destoryedActivities.add(activity);
                }
            }
            if(destoryedActivities.size()>0){
                for (Activity activity:destoryedActivities) {
                    if(activity.isFinishing()){
                        return;
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        if(activity.isDestroyed()){
                            return;
                        }
                    }
                    activity.finish();
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /**
     * finish除了指定activity类型之外的其他所有activity
     * @param cls 不需要移除的activity的Class类型
     */
    public void finishActivitiesExcept(Class<?>... cls){
        if(mActivityStack.isEmpty()){
            return;
        }
        if(cls.length==0){
            return;
        }
        List list = Arrays.asList(cls);
        Iterator<Activity> iterator = mActivityStack.iterator();
        while (iterator.hasNext()){
            Activity activity = iterator.next();
            Class clazz = activity.getClass();
            if(!list.contains(clazz)){
                iterator.remove();
                activity.finish();
            }
        }
    }

    /**
     * 清除栈内所有的实例并退出app
     */
    public void finishAllActivityAndExit () {
        if (null != mActivityStack) {
            for (int i = 0, size = mActivityStack.size(); i < size; i++) {
                if (null != mActivityStack.get(i)) {
                    ActivityCompat.finishAfterTransition(mActivityStack.get(i));
                }
            }
            clean ();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    public void clean () {
        if (sInstance != null) {
            mActivityStack.clear();
            mActivityStack = null;
            sInstance = null;
        }
    }

    public static void init(Application application){
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                ActivityStackManager.getInstance().addActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

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
                ActivityStackManager.getInstance().removeActivity(activity);
            }
        });
    }
}
