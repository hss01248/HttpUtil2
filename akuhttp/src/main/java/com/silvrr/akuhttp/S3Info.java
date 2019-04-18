package com.silvrr.akuhttp;

/**
 * Created by hss on 2018/7/29.
 */

public class S3Info {
    public String url;
    public String key;
    public String src;

    public float compressRatio;

    public boolean isKeyUsed = false;

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public void setKeyUsed(boolean keyUsed) {
        isKeyUsed = keyUsed;
    }

    public boolean isKeyUsed() {
        return isKeyUsed;
    }

    public void setIsKeyUsed(boolean isKeyUsed) {
        this.isKeyUsed = isKeyUsed;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "S3Info{" +
                "isKeyUsed=" + isKeyUsed +
                ", url='" + url + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
