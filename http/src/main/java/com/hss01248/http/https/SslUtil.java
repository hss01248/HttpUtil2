package com.hss01248.http.https;

import android.util.Log;

import com.hss01248.http.HttpUtil;
import com.hss01248.http.Tool;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okio.ByteString;

/**
 * Created by Administrator on 2016/12/20 0020.
 */

public class SslUtil {

    private static List<String> certificateFiles = new ArrayList<String>();
    private static List<String> certificateAsserts = new ArrayList<String>();
    private static List<Integer> certificateRaws = new ArrayList<Integer>();

    public static void addCrtificateFile(String filePath) {
        if (certificateFiles == null) {
            certificateFiles = new ArrayList<>();
        }
        certificateFiles.add(filePath);
    }

    public static void addCrtificateAsserts(String assertName) {
        if (certificateAsserts == null) {
            certificateAsserts = new ArrayList<>();
        }
        certificateAsserts.add(assertName);
    }

    public static void addCrtificateRaws(Integer rawId) {
        if (certificateRaws == null) {
            certificateRaws = new ArrayList<>();
        }

        if (rawId <= 0) {
            return;
        }
        certificateRaws.add(rawId);
    }

    public static void setHttps(OkHttpClient.Builder builder) {
        //if(certificateFiles!= null && certificateFiles.size()>0){
        getSSLSocketFactory(builder);

        // }
    }


    /**
     * @return
     */
    private static SSLSocketFactory getSSLSocketFactory(OkHttpClient.Builder builder) {

        // List<String> certificateFiles = HttpsUtil.certificateFiles;

        CertificateFactory certificateFactory;
        try {
            certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);

            ArrayList<InputStream> inputStreams = new ArrayList<>();

            if (certificateFiles != null && certificateFiles.size() > 0) {
                for (String filePath : certificateFiles) {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        continue;
                    }
                    InputStream certificate = new FileInputStream(file);
                    inputStreams.add(certificate);
                }

            }
            if (certificateRaws != null && certificateRaws.size() > 0) {
                for (Integer rawIds : certificateRaws) {
                    InputStream inputStream = HttpUtil.context.getResources().openRawResource(rawIds);
                    inputStreams.add(inputStream);
                }
            }

            if (certificateAsserts != null && certificateAsserts.size() > 0) {
                for (String fileName : certificateAsserts) {
                    InputStream inputStream = HttpUtil.context.getAssets().open(fileName);
                    inputStreams.add(inputStream);
                }
            }

            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);


            InputStream[] streams = new InputStream[inputStreams.size()];
            for (int i = 0; i < inputStreams.size(); i++) {
                streams[i] = inputStreams.get(i);
            }
            if(inputStreams.isEmpty()){
                streams = new InputStream[]{};
            }
            HttpsCertificateUtil.SSLParams sslParams = HttpsCertificateUtil.getSslSocketFactory(null, null,
                    streams);//添加自签名证书
            builder.sslSocketFactory(sslParams.mSSLSocketFactory, sslParams.mX509TrustManager);

            //builder.sslSocketFactory()
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 指定支持的host
     * set HostnameVerifier
     * 你的host数据 列如 String hosts[]`= {“https//:aaaa,com”, “https//:bbb.com”}
     * {@link HostnameVerifier}
     */
    private static HostnameVerifier getHostnameVerifier(final List<String> hostUrls) {

        HostnameVerifier TRUSTED_VERIFIER = new HostnameVerifier() {

            public boolean verify(String hostname, SSLSession session) {
                /*boolean ret = false;
                for (String host : hostUrls) {
                    if (host.equalsIgnoreCase(hostname)) {
                        ret = true;
                    }
                }
                return ret;*/
                return true;
            }
        };

        return TRUSTED_VERIFIER;
    }


    /**
     * 让客户端通过所有证书的验证.
     * 注意:容易导致中间人攻击,轻易不要使用
     *
     * @param httpBuilder
     */
    @SuppressWarnings("deprecation")
    public static void setAllCerPass(OkHttpClient.Builder httpBuilder) {
        X509TrustManager xtm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
                for (int i = 0; i < chain.length; i++) {
                    X509Certificate certificate = chain[i];
                    Tool.logw(ByteString.of(certificate.getPublicKey().getEncoded()).sha256().hex().toString());
                    //Tool.logJson(certificate);
                }
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509Certificates = new X509Certificate[]{};
                return x509Certificates;
                // return null;
            }
        };

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");

            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }


        HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            //@Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        httpBuilder.sslSocketFactory(sslContext.getSocketFactory())
                .hostnameVerifier(DO_NOT_VERIFY);
    }


    private static void setCertificateEntry(KeyStore keyStore, CertificateFactory cf, InputStream caInput, String fileName) {

        try {
            // AssetManager assetManager = context.getAssets();
            //InputStream caInput = assetManager.open(fileName);
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                //  Log.d("SslUtilsAndroid", "ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }
            keyStore.setCertificateEntry(fileName, ca);
        } catch (Exception e) {
            Log.d("SslUtilsAndroid", "Error during getting keystore", e);
        }

    }


}
