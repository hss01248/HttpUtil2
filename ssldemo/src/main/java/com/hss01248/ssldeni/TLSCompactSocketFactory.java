package com.hss01248.ssldeni;

import android.os.Build;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 5.0以下开启TLSv1.1,TLSv1.2
 * https://developer.android.com/reference/javax/net/ssl/SSLSocket
 *
 * @author daixiaogang
 * @version 1.0
 * @since 2019-06-12
 */
public class TLSCompactSocketFactory extends SSLSocketFactory {

    private SSLSocketFactory delegate;

    private static final String[] PROTOCOL_ARRAY;

    static {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            PROTOCOL_ARRAY = new String[]{"TLSv1", "TLSv1.1", "TLSv1.2"};
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            PROTOCOL_ARRAY = new String[]{"SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2"};
        } else {
            PROTOCOL_ARRAY = new String[]{"SSLv3", "TLSv1"};
        }
    }

    public TLSCompactSocketFactory() {
        delegate = getDefaultSslContext().getSocketFactory();
    }

    public TLSCompactSocketFactory(SSLContext sslContext) {
        if (sslContext == null) {
            sslContext = getDefaultSslContext();
        }
        delegate = sslContext.getSocketFactory();
    }

    private SSLContext getDefaultSslContext() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    //Android作为服务端时,校验客户端证书
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    //Android作为客户端时,校验服务端证书
                    //只校验有效性,未校验与域名是否匹配
                    try {
                        for (int i = 0; i < chain.length; i++) {
                            chain[i].checkValidity();
                        }
                    } catch (CertificateExpiredException expiredException) {
                        expiredException.printStackTrace();
                        throw new RuntimeException(expiredException);
                    } catch (CertificateNotYetValidException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return sslContext;
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return delegate.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return delegate.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return enableTLSOnSocket(delegate.createSocket(s, host, port, autoClose));
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        return enableTLSOnSocket(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
            throws IOException, UnknownHostException {
        return enableTLSOnSocket(delegate.createSocket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return enableTLSOnSocket(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort)
            throws IOException {
        return enableTLSOnSocket(delegate.createSocket(address, port, localAddress, localPort));
    }

    private Socket enableTLSOnSocket(Socket socket) {
        if ((socket instanceof SSLSocket)) {
            ((SSLSocket) socket).setEnabledProtocols(PROTOCOL_ARRAY);
        }
        return socket;
    }
}
