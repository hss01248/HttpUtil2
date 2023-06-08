package com.hss01248.http.aop.cerverify;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.security.auth.x500.X500Principal;

import okio.ByteString;

/**
 * @Despciption todo
 * @Author hss
 * @Date 08/06/2023 11:16
 * @Version 1.0
 */
public class CertInfoInHostNameVerifier implements HostnameVerifier {
    @Override
    public boolean verify(String hostname, SSLSession session) {
        try {
            printInfo(session);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void printInfo(SSLSession session) throws Exception {
        Certificate[] peerCertificates = session.getPeerCertificates();
        List<Map<String,Object>> certList = new ArrayList<>();
        for (int i = 0; i < peerCertificates.length; i++) {
            Certificate certificate = peerCertificates[i];
            Map<String,Object> map = new TreeMap<>();
            certList.add(map);
            String sha256 = ByteString.of(certificate.getEncoded()).sha256().hex();
            map.put("sha256",sha256);
            if(certificate instanceof X509Certificate){
                X509Certificate x509 = (X509Certificate) certificate;

                X500Principal principal  = x509.getIssuerX500Principal();
                map.put("IssuerX500Principal",principal.toString().replaceAll("\u003d","="));
                map.put("IssuerDN",x509.getIssuerDN().getName().replaceAll("\u003d","="));
                // Issuer: C=BE, O=GlobalSign nv-sa, OU=Root CA, CN=GlobalSign Root CA
                map.put("NotAfter",x509.getNotAfter());
                map.put("NotBefore",x509.getNotBefore());
                map.put("SigAlgName",x509.getSigAlgName());
                map.put("SigAlgOID",x509.getSigAlgOID());
                map.put("SubjectDN",x509.getSubjectDN().getName().replaceAll("\u003d","="));
                //map.put("SubjectAlternativeNames",x509.getSubjectAlternativeNames());
                log(i+"---->\n"+new GsonBuilder().setPrettyPrinting().create().toJson(map));
            }
           //log(certificate.toString());

        }
    }

    private void log(String sha256) {
        System.out.println(sha256);
    }
}
