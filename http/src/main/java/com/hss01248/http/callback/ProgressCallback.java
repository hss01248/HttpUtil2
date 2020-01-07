package com.hss01248.http.callback;

import com.hss01248.http.ConfigInfo;

/**
 * Created by hss on 2018/7/29.
 */

public interface ProgressCallback {

    void onFilesUploadProgress(long transPortedBytes, long totalBytes,int fileIndex,int filesCount, ConfigInfo info);

    void onProgressChange(long transPortedBytes, long totalBytes, ConfigInfo info);
}
