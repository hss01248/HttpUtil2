package com.hss01248.http;

import android.util.Log;
import android.webkit.MimeTypeMap;

import com.hss01248.http.config.GlobalClient;
import com.hss01248.http.config.ParamsProcessor;
import com.hss01248.http.request.ApiService;
import com.hss01248.http.request.UploadFileRequestBody;
import com.hss01248.http.utils.HttpHeaders;
import com.hss01248.http.utils.HttpMethod;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by hss on 2018/7/22.
 */

public class RetrofitHelper {

    public static final String UPLOAD_BINARY_KEY = "uploadBinary666";


    //@RxLogObservable
    public static <T> Observable<ResponseBody> getResponseObservable(ConfigInfo<T> info) {
        ApiService service = GlobalClient.getApiService(info);
        Observable<ResponseBody> observable = null;
        switch (info.getMethod()) {
            case HttpMethod.GET:
                if (info.isDownload()) {
                    observable = service.download(info.getUrl(), info.getParams2(), info.getHeaders());
                } else {
                    observable = service.get(info.getUrl(), info.getParams2(), info.getHeaders());
                }
                break;
            case HttpMethod.POST:
                if (info.isUploadMultipart()) {
                    Map<String, RequestBody> params = RetrofitHelper.buildMultipartParams(info);
                    Map<String, RequestBody> files = RetrofitHelper.buildMyltipartFiles(info);
                    observable = service.uploadMultipart(info.getUrl(), params, files, info.getHeaders());
                } else if (info.isParamsAsJson()) {
                    RequestBody body = RetrofitHelper.buidlJsonRequestBody(info);
                    info.getHeaders().put(HttpHeaders.HEAD_KEY_CONTENT_TYPE, "application/json");
                    observable = service.jsonPost(info.getUrl(), body, info.getHeaders());
                } else if (info.isUploadBinary()) {
                    RequestBody body = RetrofitHelper.buildBinaryRequestBody(info);
                    observable = service.uploadRawByPost(info.getUrl(), body, info.getHeaders());
                } else {
                    observable = service.post(info.getUrl(), info.getParams2(), info.getHeaders());
                }

                break;
            case HttpMethod.PUT:
                if (info.isUploadBinary()) {
                    RequestBody body = RetrofitHelper.buildBinaryRequestBody(info);
                    observable = service.uploadRawByPut(info.getUrl(), body, info.getHeaders());
                }
                break;
        }
        return observable;
    }

    private static <T> RequestBody buildBinaryRequestBody(ConfigInfo<T> info) {
        String value = info.getFiles().get(UPLOAD_BINARY_KEY);
        File file = new File(value);
        String type = getMimeType(value);
        Log.d("type", "mimetype:" + type);
        UploadFileRequestBody fileRequestBody = new UploadFileRequestBody(file, type, info, 0);
        return fileRequestBody;
    }


    public static <T> Map<String, RequestBody> buildMultipartParams(ConfigInfo<T> info) {
        //将key-value放进body中:
        Map<String, RequestBody> paramsMap = new HashMap<>();
        if (info.getParams() != null && info.getParams().size() > 0) {
            Map<String, String> params = info.getParams2();
            int count = params.size();
            if (count > 0) {
                Set<Map.Entry<String, String>> set = params.entrySet();
                for (Map.Entry<String, String> entry : set) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    String type = "text/plain";
                    RequestBody fileRequestBody = RequestBody.create(MediaType.parse(type), value);
                    paramsMap.put(key, fileRequestBody);
                }
            }
        }
        return paramsMap;
    }

    public static <T> Map<String, RequestBody> buildMyltipartFiles(ConfigInfo<T> info) {
        Map<String, RequestBody> filesMap = new HashMap<>();

        //将文件放进map中
        if (info.getFiles() != null && info.getFiles().size() > 0) {
            Map<String, String> files = info.getFiles();
            int count = files.size();
            if (count > 0) {
                Set<Map.Entry<String, String>> set = files.entrySet();
                int i = 0;
                for (Map.Entry<String, String> entry : set) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    File file = new File(value);
                    String type = getMimeType(value);
                    Log.d("type", "mimetype:" + type);
                    UploadFileRequestBody fileBody = new UploadFileRequestBody(file, type, info, i);
                    //RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
                    filesMap.put(key + "\"; filename=\"" + file.getName(), fileBody);
                    i++;
                }
            }
        }
        return filesMap;
    }

     static <T> RequestBody buidlJsonRequestBody(ConfigInfo<T> info)  {
        String jsonStr = ParamsProcessor.getFinalJsonStr(info);
        return RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), jsonStr);

    }

    private static String getMimeType(String fileUrl) {


        String suffix = getSuffix(new File(fileUrl));
        if (suffix == null) {
            return "file";
        }
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix);
        if (type != null && !type.isEmpty()) {
            return type;
        }
        return "file";
    }

    private static String getSuffix(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return null;
        }
        String fileName = file.getName();
        if (fileName.equals("") || fileName.endsWith(".")) {
            return null;
        }
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            return fileName.substring(index + 1).toLowerCase(Locale.US);
        } else {
            return null;
        }
    }
}
