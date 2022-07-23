package com.hss01248.http.response;

import android.os.Environment;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.blankj.utilcode.util.Utils;
import com.hss01248.http.HttpUtil;

import com.hss01248.http.Tool;
import com.hss01248.http.ConfigInfo;
import com.hss01248.http.config.FileDownlodConfig;
import com.hss01248.http.GlobalConfig;
import com.hss01248.http.exceptions.FileDownloadException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;

import okhttp3.MediaType;
import okhttp3.ResponseBody;

/**
 * Created by hss on 2018/7/28.
 */

public class DownloadParser {


    public static <T> ResponseBean<T> receiveInputStream(ConfigInfo<T> info, ResponseBody body) throws Exception {
        ResponseBean<T> bean = new ResponseBean<>();
        bean.url = info.getUrl();

        FileDownlodConfig config = info.getDownlodConfig();

        //todo 文件名生成规则
        config.filePath = generateFinalFilePath(config, info.getUrl(), body);
        File outputPath = new File(config.filePath);
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            byte[] fileReader = new byte[4096];
            final long fileSize = body.contentLength();
            long fileSizeDownloaded = 0;
            inputStream = body.byteStream();
            outputStream = new FileOutputStream(outputPath);

            long oldTime = 0L;
            while (true) {
                int read = inputStream.read(fileReader);
                if (read == -1) {
                    break;
                }
                outputStream.write(fileReader, 0, read);
                fileSizeDownloaded += read;
                Tool.logd("file download: " + fileSizeDownloaded + " of " + fileSize);
                //todo 控制频率
                long currentTime = System.currentTimeMillis();
                if (currentTime - oldTime > 300 || fileSizeDownloaded == fileSize) {//每300ms更新一次进度
                    oldTime = currentTime;
                    if (info.getProgressCallback() != null) {
                        long finalFileSizeDownloaded = fileSizeDownloaded;
                        Tool.runOnUI(new Runnable() {
                            @Override
                            public void run() {
                                info.getProgressCallback().onProgressChange(finalFileSizeDownloaded, fileSize,info);
                            }
                        });
                    }
                }

            }
            outputStream.flush();
            Tool.logd(String.format("file from %s  saved in path:%s", info.getUrl(), config.filePath));

            try {
                bean.data = (T) config;
                bean.success = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            postProcess(bean, info);//todo 内部在失败时删除文件
            return bean;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    private static String generateFinalFilePath(FileDownlodConfig config, String url, ResponseBody body) {
        if (!TextUtils.isEmpty(config.filePath)) {
            //将filepath拆解成dir和name
            File file = new File(config.filePath);
            config.fileDir = file.getParent();
            config.fileName = file.getName();
            return config.filePath;
        }
        if (TextUtils.isEmpty(config.fileDir)) {
            config.fileDir = GlobalConfig.get().getDownloadDir();
        }
        if (!TextUtils.isEmpty(config.fileName)) {
            File file = new File(config.fileDir, config.fileName);
            return file.getAbsolutePath();
        }
        //先从url中取
        String contentType = "";
        MediaType type = body.contentType();
        if (type != null) {
            contentType = type.toString();
        }
        String fileName = URLUtil.guessFileName(url, "", contentType);
        if (TextUtils.isEmpty(fileName)) {
            if (type != null) {
                fileName = System.currentTimeMillis() + "9527." + type.type();//todo 将mime类型映射成后缀名
            } else {
                fileName = System.currentTimeMillis() + "9527." + config.subffix;
            }
        }
        config.fileName = fileName;
        File file = new File(config.fileDir, fileName);
        return file.getAbsolutePath();
    }

    public static <T> void postProcess(ResponseBean<T> tResponseBean, ConfigInfo<T> info0) throws Exception {

        FileDownlodConfig info = info0.getDownlodConfig();
        if (TextUtils.isEmpty(info.verifyStr)) {
            //handleMedia(info);
            return;
        }
        //文件校验
        String str = "";
        if (info.verfyByMd5OrShar1) {//md5
            str = fileToMD5(info.filePath);
        } else {//sha1
            str = fileToSHA1(info.filePath);
        }
        if (TextUtils.isEmpty(str)) {//md算法失败
            tResponseBean.success = false;
            throw new FileDownloadException("file verify fail:algorithm fail-generate str is empty", info0, tResponseBean);
        }
        GlobalConfig.get().getTool().logd("real md:" + str + " --- expect md:" + info.verifyStr);
        if (str.equalsIgnoreCase(info.verifyStr)) {//校验通过
            // handleMedia(info);
        } else {
            String error = String.format("file verify fail,expect:%s,actual:%s", info.verifyStr, str);
            tResponseBean.success = false;
            throw new FileDownloadException(error, info0, tResponseBean);
        }

    }

    public static void handleMedia(FileDownlodConfig configInfo) {

        if (configInfo.isNotifyMediaCenter) {
            Tool.runOnUI(new Runnable() {
                @Override
                public void run() {
                    DownFileHandlerUtil.refreshMediaCenter(HttpUtil.context, configInfo.filePath);
                }
            });

        } else {
            if (configInfo.isHideFolder) {
                DownFileHandlerUtil.hideFile(new File(configInfo.filePath));
            }
        }

        if (configInfo.isOpenAfterSuccess) {
            Tool.runOnUI(new Runnable() {
                @Override
                public void run() {
                    DownFileHandlerUtil.openFile(HttpUtil.context, new File(configInfo.filePath));
                }
            });

        }
    }


    /**
     * Get the md5 value of the filepath specified file
     *
     * @param filePath The filepath of the file
     * @return The md5 value
     */
    private static String fileToMD5(String filePath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath); // Create an FileInputStream instance according to the filepath
            byte[] buffer = new byte[1024]; // The buffer to read the file
            MessageDigest digest = MessageDigest.getInstance("MD5"); // Get a MD5 instance
            int numRead = 0; // Record how many bytes have been read
            while (numRead != -1) {
                numRead = inputStream.read(buffer);
                if (numRead > 0)
                    digest.update(buffer, 0, numRead); // Update the digest
            }
            byte[] md5Bytes = digest.digest(); // Complete the hash computing
            return convertHashToString(md5Bytes); // Call the function to convert to hex digits
        } catch (Exception e) {
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close(); // Close the InputStream
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * Get the sha1 value of the filepath specified file
     *
     * @param filePath The filepath of the file
     * @return The sha1 value
     */
    private static String fileToSHA1(String filePath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath); // Create an FileInputStream instance according to the filepath
            byte[] buffer = new byte[1024]; // The buffer to read the file
            MessageDigest digest = MessageDigest.getInstance("SHA-1"); // Get a SHA-1 instance
            int numRead = 0; // Record how many bytes have been read
            while (numRead != -1) {
                numRead = inputStream.read(buffer);
                if (numRead > 0)
                    digest.update(buffer, 0, numRead); // Update the digest
            }
            byte[] sha1Bytes = digest.digest(); // Complete the hash computing
            return convertHashToString(sha1Bytes); // Call the function to convert to hex digits
        } catch (Exception e) {
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close(); // Close the InputStream
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * Convert the hash bytes to hex digits string
     *
     * @param hashBytes
     * @return The converted hex digits string
     */
    private static String convertHashToString(byte[] hashBytes) {
        String returnVal = "";
        for (int i = 0; i < hashBytes.length; i++) {
            returnVal += Integer.toString((hashBytes[i] & 0xff) + 0x100, 16).substring(1);
        }
        return returnVal.toLowerCase();
    }

    public static String mkDefaultDownloadDir() {
        //有权限,就放到外面

        File dir = mkDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), true);
        if (dir != null) {
            return dir.getAbsolutePath();
        }


        File dir2 = Utils.getApp().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (dir2 != null) {
            return dir2.getAbsolutePath();
        }


        return HttpUtil.context.getFilesDir().getAbsolutePath();
    }

    private static File mkDir(File dir, boolean isPublic) {
        String pk = HttpUtil.context.getPackageName();
        String fileName = pk.substring(pk.lastIndexOf(".") + 1);
        File dir2 = new File(dir, fileName);
        if (dir2.exists() && dir2.isDirectory()) {
            return dir2;
        }
        if (dir2.exists() && dir2.isFile()) {
            dir2.delete();
        }
        try {
            boolean success = dir2.mkdirs();
            if (success) {
                return dir2;
            } else {
                if (isPublic) {
                    return null;
                }
                return dir;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (isPublic) {
                return null;
            }
            return dir;
        }
    }
}
