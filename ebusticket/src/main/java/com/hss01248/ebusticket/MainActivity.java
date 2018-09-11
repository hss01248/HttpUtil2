package com.hss01248.ebusticket;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.hss01248.http.HttpUtil;
import com.hss01248.http.callback.MyNetCallback;
import com.hss01248.http.response.ResponseBean;
import com.orhanobut.logger.MyLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.MyToast;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_619left)
    Button btn619left;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_619left)
    public void onViewClicked() {
        HttpUtil.request("bc/phone/surplus/ticket/new",TicketLeftInfo.class)
                .post()
                //.addParam("customerId",55972)
                .addParamStr("customerId=55972&customerName=15989369965&keyCode=3b3abfcc1c423f4797e9362f752c77cb&lineId=73755&vehTime=0720&beginDate=20180911&endDate=20180930")
                .callback(new MyNetCallback<ResponseBean<TicketLeftInfo>>(true,this) {
                    @Override
                    public void onSuccess(ResponseBean<TicketLeftInfo> response) {
                        TicketLeftInfo info = response.bean;
                        MyToast.info("还有余票:"+info.getTicketNum());
                    }

                    @Override
                    public void onError(String msgCanShow) {
                        MyLog.e(msgCanShow);
                    }
                });
    }
}
