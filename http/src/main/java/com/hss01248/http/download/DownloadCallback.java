package com.hss01248.http.download;

import com.hss01248.http.ConfigInfo;

public interface DownloadCallback {

    void onProgressChange(long transPortedBytes, long totalBytes, ConfigInfo info);
}
