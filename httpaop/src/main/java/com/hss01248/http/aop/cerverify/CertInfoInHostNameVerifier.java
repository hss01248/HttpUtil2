package com.hss01248.http.aop.cerverify;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
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
            List<Map<String,Object>> certList = printInfo(session);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private  List<Map<String,Object>> printInfo(SSLSession session) throws Exception {
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
               // map.put("IssuerX500Principal",principal.toString().replaceAll("\u003d","###"));
                map.put("issuerDN",parseDNToMap(x509.getIssuerDN().getName().replaceAll("\u003d","###")));
                // Issuer: C=BE, O=GlobalSign nv-sa, OU=Root CA, CN=GlobalSign Root CA
                map.put("notAfter",x509.getNotAfter());
                map.put("notBefore",x509.getNotBefore());
                map.put("sigAlgName",x509.getSigAlgName());
                map.put("sigAlgOID",x509.getSigAlgOID());
                map.put("subjectDN",parseDNToMap(x509.getSubjectDN().getName().replaceAll("\u003d","###")));
                // "SubjectDN": "CN###baidu.com,O###Beijing Baidu Netcom Science Technology Co.
                // \\, Ltd,OU###service operation department,L###beijing,ST###beijing,C###CN",
                if(x509.getSubjectAlternativeNames() != null){
                    map.put("subjectAlternativeNames", Arrays.toString(x509.getSubjectAlternativeNames().toArray()));
                }

                log(i+"---->\n"+new GsonBuilder().setPrettyPrinting().create().toJson(map));
            }
           //log(certificate.toString());

        }
        return certList;
    }

    private Map<String,String> parseDNToMap(String str) {
        Map<String,String> map = new TreeMap<>();
      //  String str = x509.getSubjectDN().getName().replaceAll("\u003d","###");
        str = str.replaceAll("\\\\,","!!!!");
        String[] splits = str.split(",");
        //Co.\\, Ltd,OU###service ope
        for (String split : splits) {
            split = split.trim();
            if(!TextUtils.isEmpty(split)){
                if(split.contains("###")){
                    String[] split1 = split.split("###");
                    map.put(split1[0],split1[1].replaceAll("!!!!",","));
                }
            }
        }
        return map;
    }

    private void log(String sha256) {
        System.out.println(sha256);
    }
}
