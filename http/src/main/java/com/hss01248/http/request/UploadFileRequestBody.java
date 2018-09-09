package com.hss01248.http.request;




import com.hss01248.http.Tool;
import com.hss01248.http.ConfigInfo;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by Administrator on 2016/9/21 0021.
 */
public class UploadFileRequestBody extends RequestBody {
    private RequestBody mRequestBody;
    private BufferedSink bufferedSink;
    //private String url;
    private ConfigInfo info;
    private int index;

    public UploadFileRequestBody(File file, String mimeType, ConfigInfo info, int index) {
       // this.mRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        this.mRequestBody = RequestBody.create(MediaType.parse(mimeType), file);
        //this.url = info.url;
        this.info = info;
        this.index = index;
    }

    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    //返回了本RequestBody的长度，也就是上传的totalLength
    @Override
    public long contentLength() throws IOException {
        return mRequestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {

        if (sink instanceof Buffer) {
            // Log Interceptor,规避其导致的上传文件的异常:java.net.ProtocolException: unexpected end of stream
            //参考:https://blog.csdn.net/maosidiaoxian/article/details/78635550?locationNum=9&fps=1
            mRequestBody.writeTo(sink);
            return;
        }
        if (bufferedSink == null) {
            //包装
            bufferedSink = Okio.buffer(sink(sink));
        }
        //写入
        mRequestBody.writeTo(bufferedSink);
        //必须调用flush，否则最后一部分数据可能不会被写入
        bufferedSink.flush();
    }

    long oldTime = 0L;

    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            //当前写入字节数
            long bytesWritten = 0L;
            //总字节长度，避免多次调用contentLength()方法
            long contentLength = 0L;
            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    //获得contentLength的值，后续不再调用
                    contentLength = contentLength();
                }
                //增加当前写入的字节数
                bytesWritten += byteCount;
                long currentTime = System.currentTimeMillis();
                Tool.logd("bytesWritten  "+bytesWritten+" contentLength:"+ contentLength +" index:"+ index +" files.size:"+info.getFiles().size());
                if (currentTime - oldTime > 300 || bytesWritten == contentLength){//每300ms更新一次进度
                    oldTime = currentTime;
                    if(info.getProgressCallback() !=null){
                        Tool.runOnUI(new Runnable() {
                            @Override
                            public void run() {
                                info.getProgressCallback().onFilesUploadProgress(bytesWritten,contentLength,index,info.getFiles().size(),info);

                            }
                        });
                    }

                }
            }
        };
    }
}
