package com.hss01248.ebusticket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.xdandroid.hellodaemon.IntentWrapper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

        PermissionUtils.askCallPhone(new PermissionUtils.PermissionListener() {
            @Override
            public void onGranted() {

            }

            @Override
            public void onDenied(List<String> permissions) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        IntentWrapper.onBackPressed(this);
    }

    @OnClick(R.id.btn_619left)
    public void onViewClicked() {
        //request619();
        this.startService(new Intent(this, EbusService.class));
        //IntentWrapper.whiteListMatters(this,"加入白名单,信春哥,得永生");
        TicketUtil.tellme();
    }




}
