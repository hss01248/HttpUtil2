package com.hss01248.http.config;

import com.hss01248.http.response.DownloadParser;

/**
 * Created by hss on 2018/7/21.
 */

public class FileDownlodConfig {

    //下載文件的保存路徑,包括文件名
    public String filePath;
    //默认只有fileDir,如果没有设置文件名,则采用下载文件本身的文件名
    public String fileDir = DownloadParser.mkDefaultDownloadDir();
    public String fileName;
    /**
     * 文件后缀,优先级最低
     * 仅在没有配置filename时,且解析url失败时采用
     */
    public String subffix = "unknown";
    //是否打開,是否讓媒体库扫描,是否隐藏文件夹
    public boolean isOpenAfterSuccess ;//默认不扫描
    public boolean isHideFolder ;
    public boolean isNotifyMediaCenter =true;//媒体文件下载后,默认:通知mediacenter扫描


    //文件校验相关设置(默认不校验)
    public String verifyStr;//为空则校驗文件
    public boolean verfyByMd5OrShar1 ;

    //下载后赋值,用于携带数据
    public String mimeType;

    private FileDownlodConfig(Builder builder) {
        filePath = builder.filePath;
        fileDir = builder.fileDir;
        fileName = builder.fileName;
        isOpenAfterSuccess = builder.isOpenAfterSuccess;
        isHideFolder = builder.isHideFolder;
        isNotifyMediaCenter = builder.isNotifyMediaCenter;
        verifyStr = builder.verifyStr;
        verfyByMd5OrShar1 = builder.verfyByMd5OrShar1;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private String filePath;
        private String fileDir;
        private String fileName;
        private boolean isOpenAfterSuccess;
        private boolean isHideFolder;
        private boolean isNotifyMediaCenter;
        private String verifyStr;
        private boolean verfyByMd5OrShar1;

        private Builder() {
        }

        public Builder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder fileDir(String fileDir) {
            this.fileDir = fileDir;
            return this;
        }

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder isOpenAfterSuccess(boolean isOpenAfterSuccess) {
            this.isOpenAfterSuccess = isOpenAfterSuccess;
            return this;
        }

        public Builder isHideFolder(boolean isHideFolder) {
            this.isHideFolder = isHideFolder;
            return this;
        }

        public Builder isNotifyMediaCenter(boolean isNotifyMediaCenter) {
            this.isNotifyMediaCenter = isNotifyMediaCenter;
            return this;
        }

        public Builder verifyBySha1(String verifyStr) {
            this.verifyStr = verifyStr;
            verfyByMd5OrShar1 = false;
            return this;
        }

        public Builder verifyByMd5(String verifyStr) {
            this.verifyStr = verifyStr;
            verfyByMd5OrShar1 = true;
            return this;
        }

        public FileDownlodConfig build() {
            return new FileDownlodConfig(this);
        }
    }



    /*public Builder verifyBySha1(String verifyStr) {
            this.verifyStr = verifyStr;
            verfyByMd5OrShar1 = false;
            return this;
        }

        public Builder verifyByMd5(String verifyStr) {
            this.verifyStr = verifyStr;
            verfyByMd5OrShar1 = true;
            return this;
        }*/
}
