package com.hss01248.ebusticket;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;

import com.hss01248.http.HttpUtil;
import com.hss01248.http.callback.MyNetCallback;
import com.hss01248.http.response.ResponseBean;
import com.orhanobut.logger.MyLog;

import java.util.Random;

import es.dmoral.toasty.MyToast;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by hss on 2018/9/12.
 */

public class TicketUtil {

    private static boolean isStarted;
    public static void request619() {
        if(isStarted){
        }else {
            isStarted = true;
        }
         HttpUtil.request("bc/phone/surplus/ticket/new",TicketLeftInfo.class)
                .post()
                //.addParam("customerId",55972)
                .addParamStr("customerId=486902&customerName=16675354908&keyCode=42398a19e695e90fe007375d3eb56fdb&lineId=73755&vehTime=0720&beginDate=20180913&endDate=20180930")
                .callback(new MyNetCallback<ResponseBean<TicketLeftInfo>>(false,null) {
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

    private static void next() {
        int random = new Random().nextInt(60)+60;

        TicketApp.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                request619();
            }
        },random*1000);

    }

    public static void tellme() {
        playAudio();//播放BGM
        vibrate();//震动15s
        pullupEbus();//自动打开ebus app

        //callPhone("15989369965");






    }

    private static void playAudio() {
        try {
            MediaPlayer mp = new MediaPlayer();
            AssetFileDescriptor file = TicketApp.getContext().getResources().openRawResourceFd(R.raw.bgm);
            mp.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            mp.prepare();
            mp.setVolume(1f, 1f);
            mp.setLooping(false);
            mp.start();
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*AudioPlayerManager.get(TicketApp.getContext())
                .setDataSource(uri)
                .setCallback(new PlayerCallback() {
                    @Override
                    public void onPreparing(Object o, AudioPlayerManager audioPlayerManager) {

                    }

                    @Override
                    public void onPlaying(Object o, AudioPlayerManager audioPlayerManager) {

                    }

                    @Override
                    public void onPause(Object o, AudioPlayerManager audioPlayerManager) {

                    }

                    @Override
                    public void onCompletion(Object o, AudioPlayerManager audioPlayerManager) {

                    }

                    @Override
                    public void onStop(Object o, AudioPlayerManager audioPlayerManager) {

                    }

                    @Override
                    public void onError(String s, Object o, AudioPlayerManager audioPlayerManager) {

                    }

                    @Override
                    public void onRelease(Object o, AudioPlayerManager audioPlayerManager) {

                    }

                    @Override
                    public void onGetMaxDuration(int i) {

                    }

                    @Override
                    public void onProgress(int i, Object o, AudioPlayerManager audioPlayerManager) {

                    }

                    @Override
                    public void onSeeking(Object o, AudioPlayerManager audioPlayerManager) {

                    }

                    @Override
                    public void onBufferingUpdate(int i, AudioPlayerManager audioPlayerManager) {

                    }
                })
                .start();*/
    }

    /**
     * 拨打电话（直接拨打电话）
     *
     * @param phoneNum 电话号码
     */
    @SuppressLint("MissingPermission")
    public static void callPhone(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        TicketApp.getContext().startActivity(intent);
    }



    private static void pullupEbus() {
        try {
            Intent intent = TicketApp.getContext().getPackageManager().getLaunchIntentForPackage("zxzs.ppgj");
            if (intent != null) {
                //intent.putExtra("type", "110");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                TicketApp.getContext().startActivity(intent);
            }
        }catch (Throwable e){
            e.printStackTrace();
        }

    }


    private static void vibrate() {
        Vibrator vibrator = (Vibrator) TicketApp.getContext().getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(15000);
    }



}
