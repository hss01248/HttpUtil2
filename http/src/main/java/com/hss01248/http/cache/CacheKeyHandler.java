package com.hss01248.http.cache;

import com.hss01248.http.Tool;
import com.hss01248.http.utils.CollectionUtil;
import com.hss01248.http.ConfigInfo;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by hss on 2018/7/23.
 */

public class CacheKeyHandler {


    public static <T> String getCacheKey(final ConfigInfo<T> info) {
        String url = info.getUrl();
        Map<String,String> map = info.getParams();
        StringBuilder stringBuilder = new StringBuilder(200);
        stringBuilder.append(url);


        List<Map.Entry<String,String>> list = new ArrayList<Map.Entry<String,String>>(map.entrySet());
        CollectionUtil.filter(list, new CollectionUtil.Filter<Map.Entry<String,String>>() {
            @Override
            public boolean isRemain(Map.Entry<String,String> item) {
                if(info.getParamKeysSetNotForCacheKey() !=null && info.getParamKeysSetNotForCacheKey().contains(item.getKey())){
                    return false;
                }
                return true;
            }
        });



        if (list.size()>0){

            Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
                @Override
                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    return o1.getKey().compareTo(o2.getKey());
                }
            });


            for (Map.Entry<String,String> entry: list){
                //todo 顺序的影响: 需要预先将key排序以排除. 并且,有的key需要根据具体请求来过滤掉
                stringBuilder.append(entry.getKey()).append(entry.getValue());
            }

        }
        String str = stringBuilder.toString();
        Tool.logd("cache key pre:\n"+str);
        return str;
    }

    private static String getMD(String str,String algorithm) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance(algorithm);
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }
}
