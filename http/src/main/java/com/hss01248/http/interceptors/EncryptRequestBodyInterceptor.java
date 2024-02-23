package com.hss01248.http.interceptors;

import android.util.Log;

import com.hss01248.okhttpinterceptors.BaseOkhttpInterceptor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;

public class EncryptRequestBodyInterceptor extends BaseOkhttpInterceptor {

    public static void setCryptKey(String cryptKey) {
        EncryptRequestBodyInterceptor.cryptKey = cryptKey;
    }

    private static String cryptKey = "uyuuuuuyyyytxxxx";

    @Override
    protected Response interceptReally(Interceptor.Chain chain) throws IOException {
        Request originalRequest = chain.request();
        if (originalRequest.body() == null) {
            return chain.proceed(originalRequest);
        }
        Request compressedRequest = originalRequest.newBuilder()
                .header("Content-Encoding", "gzip")
                .method(originalRequest.method(), forceContentLength(originalRequest.body()))
                .build();
        return chain.proceed(compressedRequest);

    }

    /**
     * https://github.com/square/okhttp/issues/350
     */
    private RequestBody forceContentLength(final RequestBody requestBody) throws IOException {
        final Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);

        final long size = buffer.size();


        final byte[] bytes = new byte[(int) size];
        buffer.readFully(bytes);

        final byte[] bytesEncrypted = encrypt(bytes);
        if (isDebug) {
            Log.w("buffer", "buffer.readFully(bytes)\n" + new String(bytes));
            Log.w("buffer", "after encrypted\n" + new String(bytesEncrypted));

            Log.w("buffer", "after decrypted\n" + new String(decrypt(bytesEncrypted)));
        }


        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return requestBody.contentType();
            }

            @Override
            public long contentLength() {
                return size;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                //sink.write(buffer.snapshot());
                sink.write(bytesEncrypted);
            }
        };
    }

    private static final String AES = "AES";

    private Key getSecretKey() {
        return new SecretKeySpec(cryptKey.getBytes(), AES);
    }

    private byte[] encrypt(byte[] bytes) {
        try {
            InputStream is = new ByteArrayInputStream(bytes);//目标
            ByteArrayOutputStream out = new ByteArrayOutputStream(bytes.length);
            //SecretKey deskey = new SecretKeySpec(getKey().getBytes(), ENCRYPT_TYPE);
            //Key length not 128/192/256 bits
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
            // 创建加密流
            CipherInputStream cis = new CipherInputStream(is, cipher);
            byte[] buffer = new byte[1024];
            int r;
            while ((r = cis.read(buffer)) > 0) {
                out.write(buffer, 0, r);
            }
            return out.toByteArray();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return bytes;
        }
    }

    private byte[] decrypt(byte[] bytes) {
        try {
            InputStream is = new ByteArrayInputStream(bytes);//目标
            ByteArrayOutputStream out = new ByteArrayOutputStream(bytes.length);
            //SecretKey deskey = new SecretKeySpec(getKey().getBytes(), ENCRYPT_TYPE);
            //Key length not 128/192/256 bits
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
            // 创建加密流
            CipherInputStream cis = new CipherInputStream(is, cipher);
            byte[] buffer = new byte[1024];
            int r;
            while ((r = cis.read(buffer)) > 0) {
                out.write(buffer, 0, r);
            }
            return out.toByteArray();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return bytes;
        }
    }
}
