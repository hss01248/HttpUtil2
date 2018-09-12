package com.hss01248.ebusticket;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.hss01248.http.HttpUtil;
import com.hss01248.http.callback.MyNetCallback;
import com.hss01248.http.response.ResponseBean;
import com.orhanobut.logger.MyLog;
import es.dmoral.toasty.MyToast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_619left)
    Button btn619left;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        PermissionUtils.askExternalStorage(new PermissionUtils.PermissionListener() {
            @Override
            public void onGranted() {

            }

            @Override
            public void onDenied(List<String> permissions) {

            }
        });
    }

    @OnClick(R.id.btn_619left)
    public void onViewClicked() {
        request619();
    }

    Handler handler;

    private Handler getHandler(){
        if(handler == null){
            handler = new Handler(Looper.getMainLooper());
        }
        return handler;
    }

    private void request619() {
        HttpUtil.request("bc/phone/surplus/ticket/new",TicketLeftInfo.class)
                .post()
                //.addParam("customerId",55972)
                .addParamStr("customerId=55972&customerName=15989369965&keyCode=3b3abfcc1c423f4797e9362f752c77cb&lineId=73755&vehTime=0720&beginDate=20180911&endDate=20180930")
                .callback(new MyNetCallback<ResponseBean<TicketLeftInfo>>(true,this) {
                    @Override
                    public void onSuccess(ResponseBean<TicketLeftInfo> response) {
                        TicketLeftInfo info = response.bean;
                        MyToast.info("619还有余票:"+info.getTicketNum());
                        if(info.getTicketNum() >0){
                            tellme();
                            next();
                        }else {
                            next();
                        }

                    }

                    @Override
                    public void onError(String msgCanShow) {
                        MyLog.e(msgCanShow);
                        next();
                    }
                });
    }

    private void next() {
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                request619();
            }
        },60*1000);

    }

    private void tellme() {
        vibrate();
        //打电话,弹出ebus app.震动手机
        pullupEbus();
    }

    private void pullupEbus() {
        try {
            Intent intent = getPackageManager().getLaunchIntentForPackage("zxzs.ppgj");
            if (intent != null) {
                //intent.putExtra("type", "110");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }catch (Throwable e){
            e.printStackTrace();
        }

    }


    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }
}
