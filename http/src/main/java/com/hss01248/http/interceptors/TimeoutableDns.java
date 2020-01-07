package com.hss01248.http.interceptors;

import android.os.Handler;
import android.os.Looper;

import com.hss01248.http.Tool;
import com.hss01248.http.exceptions.NoNetworkException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import okhttp3.Dns;

/**
 * https://blog.csdn.net/quwei3930921/article/details/85336552
 * Created by huangshuisheng on 2018/4/3.
 */

public class TimeoutableDns implements Dns {

    private int timeout;


    private volatile boolean isRequestTimeout;
    private volatile boolean hasAdCallbackReturn;

    private Handler handler;
    ExecutorService executors;

    public TimeoutableDns(int timeout){
        this.timeout = timeout;

        Looper.prepare();
        Looper.loop();
        handler = new Handler();
        executors = Executors.newScheduledThreadPool(5);
    }
    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        if (hostname == null) throw new UnknownHostException("hostname == null");

        if(!Tool.isNetworkAvailable()){
            throw  new NoNetworkException("no network connection");
        }
        try {
            FutureTask<List<InetAddress>> task = new FutureTask<>(
                    new Callable<List<InetAddress>>() {
                        @Override
                        public List<InetAddress> call() throws Exception {
                            return Arrays.asList(InetAddress.getAllByName(hostname));
                        }
                    });
            executors.execute(task);
           // new Thread(task).start();
            return task.get(timeout, TimeUnit.MILLISECONDS);
        } catch (Exception var4) {
            UnknownHostException unknownHostException =
                    new UnknownHostException("Broken system behaviour for dns lookup of " + hostname);
            unknownHostException.initCause(var4);
            throw unknownHostException;
        }
    }
}
