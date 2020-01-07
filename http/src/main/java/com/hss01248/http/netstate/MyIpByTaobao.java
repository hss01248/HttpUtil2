package com.hss01248.http.netstate;

/**
 * time:2019/8/24
 * author:hss
 * desription:
 */
public class MyIpByTaobao {

    /**
     * code : 0
     * data : {"ip":"119.139.197.176","country":"中国","area":"","region":"广东","city":"深圳","county":"XX","isp":"电信","country_id":"CN","area_id":"","region_id":"440000","city_id":"440300","county_id":"xx","isp_id":"100017"}
     */

    public int code;
    public DataBean data;

    public static class DataBean {
        /**
         * ip : 119.139.197.176
         * country : 中国
         * area :
         * region : 广东
         * city : 深圳
         * county : XX
         * isp : 电信
         * country_id : CN
         * area_id :
         * region_id : 440000
         * city_id : 440300
         * county_id : xx
         * isp_id : 100017
         */

        public String ip;
        public String country;
        public String area;
        public String region;
        public String city;
        public String county;
        public String isp;
        public String country_id;
        public String area_id;
        public String region_id;
        public String city_id;
        public String county_id;
        public String isp_id;
    }
}
