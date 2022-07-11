package com.hss01248.httpdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.hss01248.http.HttpUtil;
import com.hss01248.http.cache.CacheMode;
import com.hss01248.http.callback.BaseSubscriber;
import com.hss01248.http.callback.MyNetCallback;
import com.hss01248.http.config.DataCodeMsgJsonConfig;
import com.hss01248.http.config.FileDownlodConfig;
import com.hss01248.http.response.ResponseBean;
import com.hss01248.httpdemo.bean.GetCommonJsonBean;
import com.hss01248.httpdemo.bean.GetStandardJsonBean;
import com.hss01248.httpdemo.bean.PostCommonJsonBean;
import com.hss01248.httpdemo.bean.PostStandardJsonArray;
import com.hss01248.httpdemo.threadpool.ThreadPoolFactory;
import com.orhanobut.logger.MyLog;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.MyToast;

public class MainActivityNew extends AppCompatActivity {


    @BindView(R.id.get_string)
    Button getString;
    @BindView(R.id.post_string)
    Button postString;
    @BindView(R.id.get_json)
    Button getJson;
    @BindView(R.id.post_json)
    Button postJson;
    @BindView(R.id.get_standard_json)
    Button getStandardJson;
    @BindView(R.id.post_standard_json)
    Button postStandardJson;
    @BindView(R.id.download)
    Button download;
    @BindView(R.id.upload)
    Button upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        ButterKnife.bind(this);


    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @OnClick({R.id.get_string, R.id.post_string, R.id.get_json, R.id.post_json, R.id.get_standard_json,
            R.id.post_standard_json, R.id.download, R.id.upload,R.id.postbyjson,R.id.testvoice,R.id.testvoice2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.get_string:
                //测试自签名/未被android系统承认的的https
                HttpUtil.requestString("https://kyfw.12306.cn/otn/regist/init")
                        .setIgnoreCer(true)
                        .callback(new MyNetCallback<ResponseBean<String>>(true,null) {
                            @Override
                            public void onSuccess(ResponseBean<String> response) {
                                MyLog.i(response.data);
                            }

                            @Override
                            public void onError(String msgCanShow) {
                                MyLog.e(msgCanShow);
                            }
                        });
                break;
            case R.id.post_string:

                //todo 同步请求
                //todo showDialog
                ThreadPoolFactory.getDownLoadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        MyLog.e("net work thread:"+Thread.currentThread().getName());
                        HttpUtil.requestString("article/getArticleCommentList/v1.json")
                                .post()
                                .setSync(true)
                                .addParam("pageSize","30")
                                .addParam("articleId","1738")
                               // .setCacheMode(CacheStrategy.FIRST_CACHE_THEN_REQUEST)
                                .addParam("pageIndex","1")
                                .callback(new MyNetCallback<ResponseBean<String>>(true,null) {
                                    @Override
                                    public void onSuccess(ResponseBean<String> response) {
                                        MyLog.i(response.data);
                                    }

                                    @Override
                                    public void onError(String msgCanShow) {
                                        MyLog.e(msgCanShow);
                                    }
                                });
                        MyLog.e("after:");

                    }
                });


                break;
            case R.id.get_json:

                HttpUtil.request("version/latestVersion/v1.json",GetCommonJsonBean.class)
                        .setCacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)
                        .responseAsNormalJson()
                        .callback(new MyNetCallback<ResponseBean<GetCommonJsonBean>>(true,null) {
                            @Override
                            public void onSuccess(ResponseBean<GetCommonJsonBean> response) {
                                MyLog.i("---from cache-----listener: method:"+response.isFromCache);
                            }

                            @Override
                            public void onError(String msgCanShow) {
                                MyLog.e(msgCanShow);
                            }
                        });

                break;
            case R.id.post_json:

                HttpUtil.requestAsJsonArray("article/getArticleCommentList/v1.json",PostCommonJsonBean.class)
                        .addParam("pageSize","30")
                        .addParam("articleId","1738")
                        //.setCacheMode(CacheStrategy.FIRST_CACHE_THEN_REQUEST)
                        .addParam("pageIndex","1")
                        .post()
                        .responseAsNormalJson()
                        .setTotalTimeOut(10)
                        .callbackByLiveData(this, new MyNetCallback<ResponseBean<List<PostCommonJsonBean>>>() {
                            @Override
                            public void onSuccess(ResponseBean<List<PostCommonJsonBean>> response) {
                                MyLog.json(response);
                            }

                            @Override
                            public void onError(String msgCanShow) {
                                MyLog.w(msgCanShow);
                                MyToast.error(msgCanShow);
                            }
                        });
                        /*.asLiveData()
                        .observe(this, new Observer<ResponseBean<List<PostCommonJsonBean>>>() {
                            @Override
                            public void onChanged(ResponseBean<List<PostCommonJsonBean>> listResponseBean) {
                                MyLog.json(listResponseBean);
                            }
                        });*/
                       /* .asObservable()
                        .subscribe(new BaseSubscriber<ResponseBean<List<PostCommonJsonBean>>>(true,null) {
                            @Override
                            public void onSuccess(ResponseBean<List<PostCommonJsonBean>> response) {

                            }

                            @Override
                            public void onError(String msgCanShow) {
                                MyLog.e(msgCanShow);
                            }
                        });*/

                break;
            case R.id.get_standard_json:

                /*	聚合api:笑话大全
                    sort	string	是	类型，desc:指定时间之前发布的，asc:指定时间之后发布的
                    page	int	否	当前页数,默认1
                    pagesize	int	否	每次返回条数,默认1,最大20
                    time	string	是	时间戳（10位），如：1418816972
                    key 	string  您申请的key*/
                Map<String,String> map4 = new HashMap<>();
                map4.put("sort","desc");
                map4.put("page","1");
                map4.put("pagesize","4");
                map4.put("time",System.currentTimeMillis()/1000+"");
                map4.put("key","fuck you");


                HttpUtil.request("http://japi.juhe.cn/joke/content/list.from",GetStandardJsonBean.class)
                        .addParam("sort","desc")
                        .addParam("page","1")
                        .addParam("pagesize","4")
                        .addParam("time",System.currentTimeMillis()/1000+"")
                        .addParam("key","fuck you")
                        .setDataCodeMsgJsonConfig(DataCodeMsgJsonConfig
                                .newBuilder()
                                .key_code("error_code")
                                .key_data("result")
                                .key_msg("reason")
                                .key_extra1("resultcode")
                                .successJudge(new DataCodeMsgJsonConfig.DataSuccessJudge() {
                                    @Override
                                    public boolean isResponseSuccess(JSONObject object) {
                                        int code = object.optInt("error_code");
                                        return code ==200;
                                    }
                                })
                                .build()
                        )
                        //.setCacheMode(CacheStrategy.FIRST_CACHE_THEN_REQUEST)
                        .callback(new MyNetCallback<ResponseBean<GetStandardJsonBean>>(true,null) {
                            @Override
                            public void onSuccess(ResponseBean<GetStandardJsonBean> response) {
                                MyLog.json(MyJson.toJsonStr(response.data));
                            }

                            @Override
                            public void onError(String msgCanShow) {
                                MyLog.e(msgCanShow);
                            }
                        });
                break;
            case R.id.post_standard_json:

                HttpUtil.requestAsJsonArray("article/getArticleCommentList/v1.json",
                        PostStandardJsonArray.class)
                        .addParam("pageSize","30")
                        .addParam("articleId","1738")
                        .addParam("pageIndex","1")
                        .post()
                        .callback(new MyNetCallback<ResponseBean<List<PostStandardJsonArray>>>() {
                            @Override
                            public void onSuccess(ResponseBean<List<PostStandardJsonArray>> response) {
                                //response.
                            }

                            @Override
                            public void onError(String msgCanShow) {

                            }
                        });


                HttpUtil.requestAsJsonArray("article/getArticleCommentList/v1.json",PostStandardJsonArray.class)
                        .addParam("pageSize","30")
                        .addParam("articleId","1738")
                        .addParam("pageIndex","1")
                        .post()
                        .setCacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
                       // .setCacheMode(CacheStrategy.REQUEST_FAILED_READ_CACHE)
                        .callback(new MyNetCallback<ResponseBean<List<PostStandardJsonArray>>>(true,null) {
                            @Override
                            public void onSuccess(ResponseBean<List<PostStandardJsonArray>> response) {
                                MyLog.json(MyJson.toJsonStr(response.data));
                            }

                            @Override
                            public void onError(String msgCanShow) {
                                MyLog.e(msgCanShow);

                            }
                        });

                break;
            case R.id.download:
                /*File dir = Environment.getExternalStorageDirectory();
                final File file = new File(dir,"2.jpg");
                if (file.exists()){
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }*/
                PermissionUtils.askExternalStorage(new PermissionUtils.PermissionListener() {
                    @Override
                    public void onGranted() {
                        String url2 = "https://kiwivm.64clouds.com/dist/openvpn-install-2.4.5-I601.exe";
                        HttpUtil.download(url2)
                                .setFileDownlodConfig(
                                        FileDownlodConfig.newBuilder()
                                        .verifyBySha1("76DAB206AE43FB81A15E9E54CAC87EA94BB5B384")
                                        .isOpenAfterSuccess(true)
                                        .build())
                                .callback(new MyNetCallback<ResponseBean<FileDownlodConfig>>() {
                                    @Override
                                    public void onSuccess(ResponseBean<FileDownlodConfig> response) {
                                        MyLog.i("path:"+response.data.filePath);
                                    }


                                    @Override
                                    public void onError(String msgCanShow) {
                                        MyLog.e(msgCanShow);
                                    }
                                });
                    }

                    @Override
                    public void onDenied(List<String> permissions) {

                    }
                });

                break;
            case R.id.upload:


                HttpUtil.request("http://192.168.108.102:8080/uploadImgs",String.class)
                        .uploadMultipart()
                        .responseAsString()
                        .addFile("file1","/storage/emulated/0/Download/httpdemo/qxinli.apk")
                        .addFile("file2","/storage/emulated/0/Download/httpdemo/qxinli2.apk")
                        .addParam("name","898767hjk")
                        .callback(new MyNetCallback<ResponseBean<String>>() {
                            @Override
                            public void onSuccess(ResponseBean<String> response) {
                                MyLog.i(response.data);
                            }

                            @Override
                            public void onError(String msgCanShow) {
                                MyLog.e(msgCanShow);
                            }
                        });
                break;

            case R.id.postbyjson:


                HttpUtil.request("http://app.cimc.com:9090/app/appVersion/getLatestVersion", com.hss01248.httpdemo.bean.VersionInfo.class)
                        .addParam("versionName","1.0.0")
                        .addParam("appType","0")
                         .postParamsAsJson()
                        .responseAsNormalJson()
                        .callback(new MyNetCallback<ResponseBean<com.hss01248.httpdemo.bean.VersionInfo>>(true,null) {
                            @Override
                            public void onSuccess(ResponseBean<com.hss01248.httpdemo.bean.VersionInfo> response) {
                                MyLog.i(response.bodyStr);
                            }

                            @Override
                            public void onError(String msgCanShow) {
                                MyLog.e(msgCanShow);
                            }
                        });

                break;
            case R.id.testvoice:
                HttpUtil.requestString("http://192.168.108.102:8080/uploadImgsByPut")
                        .uploadBinary("/storage/emulated/0/Download/httpdemo/qxinli.apk")
                        .put()
                        .callback(new MyNetCallback<ResponseBean<String>>() {
                            @Override
                            public void onSuccess(ResponseBean<String> response) {
                                MyLog.i(response.data);
                            }

                            @Override
                            public void onError(String msgCanShow) {
                                MyLog.e(msgCanShow);
                            }
                        });

                break;
            case R.id.testvoice2:{

                HttpUtil.request("http://192.168.108.102:8080/uploadImgsByPost",String.class)
                        .uploadBinary("/storage/emulated/0/Download/httpdemo/qxinli.apk")
                        .post()
                        .setDataCodeMsgJsonConfig(DataCodeMsgJsonConfig.newBuilder()
                                .successJudge(new DataCodeMsgJsonConfig.DataSuccessJudge() {
                                    @Override
                                    public boolean isResponseSuccess(JSONObject object) {
                                        return object.optBoolean("success");
                                    }
                                })
                        .build())
                        .treatEmptyDataAsSuccess()
                        .callback(new MyNetCallback<ResponseBean<String>>() {
                            @Override
                            public void onSuccess(ResponseBean<String> response) {
                                MyLog.i(response.bodyStr);
                            }

                            @Override
                            public void onError(String msgCanShow) {
                                MyLog.e(msgCanShow);
                            }
                        });
                 /*
                $sign = get_sign($appkey, $params, $secret, $time);
                签名算法：使用MD5加密 MD5（appkey + interfaces +cti + act+ params+appSecret+time） 注”+”不包含.

                http://api.mixcom.cn/v2/?m=interfaces&c=virt&a=index&act=bindnumber&appkey=d6906c470a7886edaa99802cb87fd465&sign=e4842570f261ff1571ae541371f1e809&time=1480327411
                   &virtualnumber=86170****0673&aparty=86153****2774&bparty=86183****9530&recording=0&endDate=2016-01-01 00:00:00*/

                String smallNum = "8617092580665";
                String aparty = "8615989369965";
                String bparty = "8617722810218";
                String appkey = "8b575f9208f4181d974b72a71ca3ad24";
                String appSecret = "ebvbBE";
                String timeStamp2 = System.currentTimeMillis()/1000+"";
               // MyLog.e("time:"+timeStamp2);

                String act = "bindnumber";
                String c = "virt";
                String m = "interfaces";

                String params = "";
                params=smallNum + aparty + bparty;

                String str = appkey + m +c + act + params + appSecret + timeStamp2;
               // MyLog.e("str:"+str);
                String sign = "";
               // MyLog.e("签名后的:"+sign);

                Map map11 = new HashMap<>();
                map11.put("m",m);
                map11.put("c",c);
                map11.put("a","index");
                map11.put("act",act);
                map11.put("appkey",appkey);
                map11.put("sign",sign);
                map11.put("time",timeStamp2);

                map11.put("virtualnumber",smallNum);
                map11.put("aparty",aparty);
                map11.put("bparty",bparty);

               /* MyNetApi.getStandardJson("http://api.mixcom.cn/v2/",
                        map11, VersionInfo.class, new MyNetListener<VersionInfo>() {
                            @Override
                            public void onSuccess(VersionInfo response, String resonseStr) {
                                MyLog.e(resonseStr);
                            }

                            @Override
                            public void onEmpty() {
                                super.onEmpty();
                            }

                            @Override
                            public void onError(String msgCanShow) {
                                super.onError(msgCanShow);
                                MyLog.e(msgCanShow);
                            }
                        })
                        .setIsAppendToken(false)
                        .setCustomCodeValue(200,-1,-1)
                        .start();*/
            }


                break;

            default:break;


        }
    }

    String app_key = "4d3d1f40e7a841316084b64c0c4575b1";
    String app_secert = "VQ8bciAjkUl4fiTTvafdvTLnBNGlSS";


}
