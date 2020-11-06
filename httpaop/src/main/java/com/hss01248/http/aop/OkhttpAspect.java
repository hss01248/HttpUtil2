package com.hss01248.http.aop;

import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

/**
 * by hss
 * data:2020/7/17
 * desc:
 */
@Aspect
public class OkhttpAspect {

    public static final String TAG = "OkhttpAspect";

    @Pointcut("execution(* okhttp3.OkHttpClient.Builder.build(..))")
    public void methodTime() {

    }

    static List<OkhttpHook> hooks = new ArrayList<>();

    static {
        hooks = new ArrayList<>();
        //hooks.add(new Tls2AdapterBelowAndroid5());
        hooks.add(new IgnoreSslVerifyForAll());
        //hooks.add(new SSLPinerAspect());//与上面的ignore互斥
        hooks.add(new HostNameAspect());
    }

    public static void addHook(OkhttpHook hook){
        hooks.add(hook);
    }

    @Around("methodTime()")
    public Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();
        //String funName = methodSignature.getMethod().getAnnotation(TimeSpend.class).value();

        //统计时间
        long begin = System.currentTimeMillis();
        Object result = null;
        try {
            if(joinPoint.getThis() instanceof OkHttpClient.Builder){
                OkHttpClient.Builder builder = (OkHttpClient.Builder) joinPoint.getThis();
                if(hooks.size() > 0){
                    for (int i = 0; i < hooks.size(); i++) {
                        hooks.get(i).beforeBuild(builder);
                    }
                }
            }
             result = joinPoint.proceed();
        }catch (Throwable throwable){
            throwable.printStackTrace();
        }
        long duration = System.currentTimeMillis() - begin;
        Log.d(TAG,joinPoint.getSignature().toShortString()+"  耗时:"+duration+"ms" );

        return result;
    }

    public  interface OkhttpHook{
        void beforeBuild(OkHttpClient.Builder builder);
    }
}
