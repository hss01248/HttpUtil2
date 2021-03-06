package com.hss01248.http.https;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class HttpsCertificateUtil {

    public static class SSLParams {
        public SSLSocketFactory mSSLSocketFactory;
        public X509TrustManager mX509TrustManager;
    }

    /**
     * https://www.jianshu.com/p/54afe380b83b
     * https://www.jianshu.com/p/2c9820bed794
     * @param bksFile
     * @param password
     * @param certificates
     * @return
     */
    public static SSLParams getSslSocketFactory(InputStream bksFile, String password, InputStream[] certificates) {
        SSLParams sslParams = new SSLParams();
        try {
            //如果是双向通信，还需要一个 KeyManager
            //KeyManager 负责提供证书和私钥，证书发给对方peer
            //双向https认证 需要服务端向客户端发送一个 Certificate Request,客户端才发送客户端证书  即服务端开启sslContext.createSSLEngine().setNeedClientAuth(true)
            KeyManager[] keyManagers = prepareKeyManager(bksFile, password);
            //TrustManager 负责验证peer 发来的证书
            javax.net.ssl.TrustManager[] trustManagers = prepareTrustManager(certificates);
            SSLContext sslContext = SSLContext.getInstance("TLS");


            X509TrustManager trustManager;
            if (trustManagers != null) {
                trustManager = new TrustManager(chooseTrustManager(trustManagers));
            } else {
                trustManager = new UnSafeTrustManager();
            }
            sslContext.init(keyManagers, new javax.net.ssl.TrustManager[]{trustManager}, null);
            sslParams.mSSLSocketFactory = new TLSCompactSocketFactory(sslContext);
            //打开tls1.1,1.2 (SSLSocketFactory) SSLSocketFactory.getDefault();
            sslParams.mX509TrustManager = trustManager;
            return sslParams;
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        } catch (KeyManagementException e) {
            throw new AssertionError(e);
        } catch (KeyStoreException e) {
            throw new AssertionError(e);
        }
    }

    private static javax.net.ssl.TrustManager[] prepareTrustManager(InputStream... certificates) {
        if (certificates == null || certificates.length <= 0) return null;
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                try {
                    if (certificate != null) certificate.close();
                } catch (IOException e) {
                   e(e);
                }
            }
            TrustManagerFactory trustManagerFactory;
            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            return trustManagerFactory.getTrustManagers();
        } catch (NoSuchAlgorithmException e) {
           e(e);
        } catch (CertificateException e) {
           e(e);
        } catch (KeyStoreException e) {
           e(e);
        } catch (Exception e) {
           e(e);
        }
        return null;
    }

    private static void e(Exception e) {
        e.printStackTrace();
    }

    private static KeyManager[] prepareKeyManager(InputStream bksFile, String password) {
        try {
            if (bksFile == null || password == null) return null;
            KeyStore clientKeyStore = KeyStore.getInstance("BKS");
            clientKeyStore.load(bksFile, password.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeyStore, password.toCharArray());
            return keyManagerFactory.getKeyManagers();
        } catch (KeyStoreException e) {
           e(e);
        } catch (NoSuchAlgorithmException e) {
           e(e);
        } catch (UnrecoverableKeyException e) {
           e(e);
        } catch (CertificateException e) {
           e(e);
        } catch (IOException e) {
           e(e);
        } catch (Exception e) {
           e(e);
        }
        return null;
    }

    private static X509TrustManager chooseTrustManager(javax.net.ssl.TrustManager[] trustManagers) {
        for (javax.net.ssl.TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        return null;
    }

    private static class UnSafeTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    private static class TrustManager implements X509TrustManager {
        private X509TrustManager mDefaultTrustManager;
        private X509TrustManager mLocalTrustManager;

        public TrustManager(X509TrustManager localTrustManager) throws NoSuchAlgorithmException, KeyStoreException {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            mDefaultTrustManager = chooseTrustManager(trustManagerFactory.getTrustManagers());
            this.mLocalTrustManager = localTrustManager;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            try {
                mDefaultTrustManager.checkServerTrusted(chain, authType);
            } catch (CertificateException ce) {
                mLocalTrustManager.checkServerTrusted(chain, authType);
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
