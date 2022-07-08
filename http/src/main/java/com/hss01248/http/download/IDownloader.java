package com.hss01248.http.download;

import com.hss01248.http.ConfigInfo;
import com.hss01248.http.config.FileDownlodConfig;
import com.hss01248.http.response.DownloadParser;

public interface IDownloader {

    default FileDownlodConfig defaultDownloadConfig(){
        return FileDownlodConfig.newBuilder().build();
    }

    default String defaultSaveDir(){
        return DownloadParser.mkDefaultDownloadDir();
    }

    void doDownload(ConfigInfo info,DownloadCallback callback);
}
