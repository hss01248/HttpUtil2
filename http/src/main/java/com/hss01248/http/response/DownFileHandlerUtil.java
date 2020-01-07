package com.hss01248.http.response;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.webkit.MimeTypeMap;
import android.widget.Toast;


import com.hss01248.http.GlobalConfig;
import com.hss01248.http.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2017/1/17 0017.
 */

public class DownFileHandlerUtil {


    public static void hideFile(File file){
        if(!file.exists()){
            return;
        }

        if(file.isDirectory()){
            try {
                new File(file,".nomedia").createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        //文件,则隐藏文件所在目录
      File dir =   file.getParentFile();
        try {
            new File(dir,".nomedia").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static  void refreshMediaCenter(Context activity, String filePath){
       /* File file  = new File(filePath);
        try {
            MediaStore.Images.Media.insertImage(activity.getContentResolver(),file.getAbsolutePath(), file.getName(), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/


        if (Build.VERSION.SDK_INT>19){
            String mineType =getMineType(filePath);
            GlobalConfig.get().getTool().logd("filepath:"+filePath+"---mimetype:"+mineType);

            saveImageSendScanner(activity,new MyMediaScannerConnectionClient(filePath,mineType));
        }else {

            saveImageSendBroadcast(activity,filePath);
        }
    }

    public static String getMineType(String filePath) {

        String type = "text/plain";
        String extension = MimeTypeMap.getFileExtensionFromUrl(filePath);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
           type = mime.getMimeTypeFromExtension(extension);
        }
        return type;


       /* MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String mime = "text/plain";
        if (filePath != null) {
            try {
                mmr.setDataSource(filePath);
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            } catch (IllegalStateException e) {
                return mime;
            } catch (IllegalArgumentException e) {
                return mime;
            } catch (RuntimeException e) {
                return mime;
            }
        }
        return mime;*/
    }

    /**
     * 保存后用广播扫描，Android4.4以下使用这个方法
     * @author YOLANDA
     */
    private static void saveImageSendBroadcast(Context activity,String filePath){
        activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePath)));
        //  activity. sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory()+ filePath)));
    }

    /**
     * 保存后用MediaScanner扫描，通用的方法
     *
     */
    private static void saveImageSendScanner (Context context,   MyMediaScannerConnectionClient scannerClient) {

        final MediaScannerConnection scanner = new MediaScannerConnection(context, scannerClient);
        scannerClient.setScanner(scanner);
        scanner.connect();
    }
    private   static class MyMediaScannerConnectionClient implements MediaScannerConnection.MediaScannerConnectionClient {

        private MediaScannerConnection mScanner;

        private String mScanPath;
        private String mimeType;

        public MyMediaScannerConnectionClient(String scanPath,String mimeType) {
            mScanPath = scanPath;
            this.mimeType = mimeType;
        }

        public void setScanner(MediaScannerConnection con) {
            mScanner = con;
        }

        @Override
        public void onMediaScannerConnected() {
            mScanner.scanFile(mScanPath, mimeType);
        }

        @Override
        public void onScanCompleted(String path, Uri uri) {
            mScanner.disconnect();
        }
    }


    private static boolean checkEndsWithInStringArray(String checkItsEnd,
                                               String[] fileEndings){
        for(String aEnd : fileEndings){
            if(checkItsEnd.endsWith(aEnd))
                return true;
        }
        return false;
    }

    public static void openFile(Context context,File currentPath){
        if(currentPath!=null&&currentPath.isFile()) {
            String fileName = currentPath.toString();
            Intent intent  = getFileOpenIntent(context,fileName,currentPath);
            if(intent ==null){
                showMessage("无法打开，请安装相应的软件！",context);
            }else {
                context.startActivity(intent);
            }

        }else {
            showMessage("对不起，这不是文件！",context);
       }

    }

    public static Intent getFileOpenIntent(Context context, String fileName, File currentPath) {

        Intent intent = null;
        if(checkEndsWithInStringArray(fileName, context.getResources().
                getStringArray(R.array.fileEndingImage))){
            intent = FileOpenIntents.getImageFileIntent(currentPath);

        }else if(checkEndsWithInStringArray(fileName, context.getResources().
                getStringArray(R.array.fileEndingWebText))){
            intent = FileOpenIntents.getHtmlFileIntent(currentPath);

        }else if(checkEndsWithInStringArray(fileName, context.getResources().
                getStringArray(R.array.fileEndingPackage))){
            intent = FileOpenIntents.getApkFileIntent(currentPath);


        }else if(checkEndsWithInStringArray(fileName, context.getResources().
                getStringArray(R.array.fileEndingAudio))){
            intent = FileOpenIntents.getAudioFileIntent(currentPath);

        }else if(checkEndsWithInStringArray(fileName, context.getResources().
                getStringArray(R.array.fileEndingVideo))){
            intent = FileOpenIntents.getVideoFileIntent(currentPath);

        }else if(checkEndsWithInStringArray(fileName, context.getResources().
                getStringArray(R.array.fileEndingText))){
            intent = FileOpenIntents.getTextFileIntent(currentPath);

        }else if(checkEndsWithInStringArray(fileName, context.getResources().
                getStringArray(R.array.fileEndingPdf))){
            intent = FileOpenIntents.getPdfFileIntent(currentPath);

        }else if(checkEndsWithInStringArray(fileName, context.getResources().
                getStringArray(R.array.fileEndingWord))){
            intent = FileOpenIntents.getWordFileIntent(currentPath);

        }else if(checkEndsWithInStringArray(fileName, context.getResources().
                getStringArray(R.array.fileEndingExcel))){
            intent = FileOpenIntents.getExcelFileIntent(currentPath);

        }else if(checkEndsWithInStringArray(fileName, context.getResources().
                getStringArray(R.array.fileEndingPPT))){
            intent = FileOpenIntents.getPPTFileIntent(currentPath);

        }
        return intent;
    }

    private static void showMessage(String s, Context context) {
        Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
    }


}
