package com.hss01248.http.response;

import android.content.Intent;
import android.net.Uri;


import com.hss01248.http.HttpUtil;
import com.hss01248.openuri.OpenUri;

import java.io.File;

/**
 * Created by Administrator on 2017/1/17 0017.
 */

public class FileOpenIntents {

    //android获取一个用于打开HTML文件的intent
    public static Intent getHtmlFileIntent(File file) {
        Uri uri = OpenUri.fromFile(HttpUtil.context, file)
                .buildUpon()
                .encodedAuthority("com.android.htmlfileprovider")
                .scheme("content")
                .encodedPath(file.toString()).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        OpenUri.addPermissionRW(intent);
        return intent;
    }

    //android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = OpenUri.fromFile(HttpUtil.context, file);
        intent.setDataAndType(uri, "image/*");
        OpenUri.addPermissionRW(intent);
        return intent;
    }

    //android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = OpenUri.fromFile(HttpUtil.context, file);
        intent.setDataAndType(uri, "application/pdf");
        OpenUri.addPermissionR(intent);
        return intent;
    }

    //android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = OpenUri.fromFile(HttpUtil.context, file);
        intent.setDataAndType(uri, "text/plain");
        OpenUri.addPermissionRW(intent);
        return intent;
    }

    //android获取一个用于打开音频文件的intent
    public static Intent getAudioFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = OpenUri.fromFile(HttpUtil.context, file);
        intent.setDataAndType(uri, "audio/*");
        OpenUri.addPermissionRW(intent);
        return intent;
    }

    //android获取一个用于打开视频文件的intent
    public static Intent getVideoFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = OpenUri.fromFile(HttpUtil.context, file);
        intent.setDataAndType(uri, "video/*");
        OpenUri.addPermissionRW(intent);
        return intent;
    }


    //android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = OpenUri.fromFile(HttpUtil.context, file);
        intent.setDataAndType(uri, "application/x-chm");
        OpenUri.addPermissionRW(intent);
        return intent;
    }


    //android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = OpenUri.fromFile(HttpUtil.context, file);
        intent.setDataAndType(uri, "application/msword");
        OpenUri.addPermissionRW(intent);
        return intent;
    }

    //android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = OpenUri.fromFile(HttpUtil.context, file);
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        OpenUri.addPermissionRW(intent);
        return intent;
    }

    //android获取一个用于打开PPT文件的intent
    public static Intent getPPTFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = OpenUri.fromFile(HttpUtil.context, file);
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        OpenUri.addPermissionRW(intent);
        return intent;
    }

    //android获取一个用于打开apk文件的intent
    public static Intent getApkFileIntent(File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(OpenUri.fromFile(HttpUtil.context, file), "application/vnd.android.package-archive");
        OpenUri.addPermissionRW(intent);
        return intent;
    }
}
