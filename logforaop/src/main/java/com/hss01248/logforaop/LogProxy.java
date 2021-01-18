package com.hss01248.logforaop;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class LogProxy {

    public static boolean enableLog = true;

    /**
     * 打印接口的方法; 示例:
     *   正常:              PGReport@1a5b9ed.enterV4(actionname, 300134, null, false), result:null, cost:6ms ,thread:main
     *   方法执行发生错误时:  PGReport@f1ebd59.enter(actionname, 300134, false), throw ArithmeticException:divide by zero, cost:5ms ,thread:main
     *
     *   日志tag: aspectImpl
     * @param impl
     * @param <T>
     * @return
     */
    public  static <T> T  getProxy(T impl){
        if(!enableLog){
            return impl;
        }
        return (T) Proxy.newProxyInstance(impl.getClass().getClassLoader(), impl.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                StringBuilder sb = new StringBuilder();
                String parms = Arrays.toString(args);
                parms = parms.substring(1,parms.length()-1);

                String objName = impl+"";
                if(objName.contains(".")){
                    objName = objName.substring(objName.lastIndexOf(".")+1);
                }
                sb.append(objName)
                        .append(".")
                        .append(method.getName())
                        .append("(")
                        .append(parms)
                        .append(")");

                Object obj = null;
                long start = System.currentTimeMillis();
                try {
                     obj = method.invoke(impl, args);
                     sb.append(", result:")
                             .append(obj);
                     doLog(sb,start);
                }catch (Throwable throwable){
                    sb.append(EXCEPTION_DESC)
                            .append(throwable.getClass().getName())
                            .append(":")
                            .append(throwable.getMessage());
                    doLog(sb,start);
                    throw throwable;
                }
                return obj;
            }
        });
    }

    private static void doLog(StringBuilder sb, long start) {
        long cost = System.currentTimeMillis()-start;
        sb.append(", cost:")
                .append(cost).append("ms").append(" ,thread:").append(Thread.currentThread().getName());
        String str = sb.toString();

        if("main".equals(Thread.currentThread().getName()) && cost> 50){
            Log.i("aspectImpl",str);
        }else if(str.contains(EXCEPTION_DESC)){
            Log.w("aspectImpl",str);
        } else {
            Log.d("aspectImpl",str);
        }
    }

    static String EXCEPTION_DESC = ", throw ";
}
