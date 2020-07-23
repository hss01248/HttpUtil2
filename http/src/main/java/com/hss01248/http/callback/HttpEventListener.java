package com.hss01248.http.callback;

import androidx.annotation.Nullable;

import com.hss01248.http.Tool;
import com.hss01248.http.utils.CollectionUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

import okhttp3.Call;
import okhttp3.Connection;
import okhttp3.EventListener;
import okhttp3.Handshake;
import okhttp3.HttpUrl;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

/**
 * time:2019/7/20
 * author:hss
 * desription:https://blog.csdn.net/joye123/article/details/82115562
 */
public class HttpEventListener extends EventListener {
    /**
     * 自定义EventListener工厂
     */
    public static final Factory FACTORY = new Factory() {
        final AtomicLong nextCallId = new AtomicLong(1L);

        @Override
        public EventListener create(Call call) {
            long callId = nextCallId.getAndIncrement();
            return new HttpEventListener(callId, call.request().url(), System.nanoTime());
        }
    };

    /**
     * 每次请求的标识
     */
    private final long callId;

    /**
     * 每次请求的开始时间，单位纳秒
     */
    private final long preStart;

    private StringBuilder sbLog;
    private LinkedHashMap<String,Long> time = new LinkedHashMap<>();
    private LinkedHashMap<String, Long> cost = new LinkedHashMap<>();

    public HttpEventListener(long callId, HttpUrl url, long callStartNanos) {
        this.callId = callId;
        this.preStart = callStartNanos;
        this.sbLog = new StringBuilder(url.toString()).append(" ").append(callId).append(":\n");
    }

    private void recordEventLog(String name) {
        long elapseNanos = System.nanoTime() - preStart;
        time.put(name,elapseNanos);

        /*sbLog.append(String.format(Locale.CHINA, "%.3f-%s", elapseNanos / 1000000000d, name)).append(";");
        if(name.endsWith("End")|| name.endsWith("Acquired")){
            sbLog.append("\n");
        }*/
        if (name.equalsIgnoreCase("callEnd") || name.equalsIgnoreCase("callFailed")) {
            //打印出每个步骤的时间点
            CollectionUtil.forEach(time, new CollectionUtil.EveryMap<String, Long>() {
                @Override
                public void item(Map.Entry<String, Long> entry) {
                    entry.setValue(entry.getValue()/1000000) ;
                }
            });
            CollectionUtil.forEach(cost, new CollectionUtil.EveryMap<String, Long>() {
                @Override
                public void item(Map.Entry<String, Long> entry) {
                    entry.setValue(entry.getValue()/1000000) ;
                }
            });
            Tool.logObj(time);
            Tool.logObj(cost);
        }
    }

    @Override
    public void callStart(Call call) {
        super.callStart(call);
        recordEventLog("callStart");
    }

    @Override
    public void dnsStart(Call call, String domainName) {
        super.dnsStart(call, domainName);
        recordEventLog("dnsStart");
    }

    @Override
    public void dnsEnd(Call call, String domainName, List<InetAddress> inetAddressList) {
        super.dnsEnd(call, domainName, inetAddressList);
        recordEventLog("dnsEnd");
        cost.put("dns",System.nanoTime() - preStart - time.get("dnsStart"));
    }

    @Override
    public void connectStart(Call call, InetSocketAddress inetSocketAddress, Proxy proxy) {
        super.connectStart(call, inetSocketAddress, proxy);
        recordEventLog("connectStart");
    }

    @Override
    public void secureConnectStart(Call call) {
        super.secureConnectStart(call);
        recordEventLog("secureConnectStart");
    }

    @Override
    public void secureConnectEnd(Call call, @Nullable Handshake handshake) {
        super.secureConnectEnd(call, handshake);
        recordEventLog("secureConnectEnd");
        cost.put("ssl",System.nanoTime() - preStart - time.get("secureConnectStart"));
    }

    @Override
    public void connectEnd(Call call, InetSocketAddress inetSocketAddress, Proxy proxy, @Nullable Protocol protocol) {
        super.connectEnd(call, inetSocketAddress, proxy, protocol);
        recordEventLog("connectEnd");
        if(cost.containsKey("ssl")){
            cost.put("tcp",System.nanoTime() - preStart - time.get("connectStart") - cost.get("ssl"));
        }else {
            cost.put("tcp",System.nanoTime() - preStart - time.get("connectStart") );
        }

    }

    @Override
    public void connectFailed(Call call, InetSocketAddress inetSocketAddress, Proxy proxy, @Nullable Protocol protocol, IOException ioe) {
        super.connectFailed(call, inetSocketAddress, proxy, protocol, ioe);
        recordEventLog("connectFailed");
        if(cost.containsKey("ssl")){
            cost.put("tcp",System.nanoTime() - preStart - time.get("connectStart") - cost.get("ssl"));
        }else {
            cost.put("tcp",System.nanoTime() - preStart - time.get("connectStart") );
        }
    }

    @Override
    public void connectionAcquired(Call call, Connection connection) {
        super.connectionAcquired(call, connection);
        recordEventLog("connectionAcquired");
    }

    @Override
    public void requestHeadersStart(Call call) {
        super.requestHeadersStart(call);
        recordEventLog("requestHeadersStart");
    }

    @Override
    public void requestHeadersEnd(Call call, Request request) {
        super.requestHeadersEnd(call, request);
        recordEventLog("requestHeadersEnd");
        cost.put("requestHeaders",System.nanoTime() - preStart - time.get("requestHeadersStart") );
    }

    @Override
    public void requestBodyStart(Call call) {
        super.requestBodyStart(call);
        recordEventLog("requestBodyStart");
    }

    @Override
    public void requestBodyEnd(Call call, long byteCount) {
        super.requestBodyEnd(call, byteCount);
        recordEventLog("requestBodyEnd");
        cost.put("requestBody",System.nanoTime() - preStart - time.get("requestBodyStart") );
    }

    @Override
    public void responseHeadersStart(Call call) {
        super.responseHeadersStart(call);
        recordEventLog("responseHeadersStart");
    }

    @Override
    public void responseHeadersEnd(Call call, Response response) {
        super.responseHeadersEnd(call, response);
        recordEventLog("responseHeadersEnd");
        cost.put("responseHeaders",System.nanoTime() - preStart - time.get("responseHeadersStart") );
    }

    @Override
    public void responseBodyStart(Call call) {
        super.responseBodyStart(call);
        recordEventLog("responseBodyStart");
    }

    @Override
    public void responseBodyEnd(Call call, long byteCount) {
        super.responseBodyEnd(call, byteCount);
        recordEventLog("responseBodyEnd");
        cost.put("responseBody",System.nanoTime() - preStart - time.get("responseBodyStart") );
    }


    @Override
    public void connectionReleased(Call call, Connection connection) {
        super.connectionReleased(call, connection);
        recordEventLog("connectionReleased");
    }

    @Override
    public void callEnd(Call call) {
        super.callEnd(call);
        recordEventLog("callEnd");
        cost.put("totalCost",System.nanoTime() - preStart );
    }

    @Override
    public void callFailed(Call call, IOException ioe) {
        super.callFailed(call, ioe);
        recordEventLog("callFailed");
        cost.put("totalCost",System.nanoTime() - preStart );
    }

}
