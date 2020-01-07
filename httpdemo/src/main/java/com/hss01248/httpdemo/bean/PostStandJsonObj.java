package com.hss01248.httpdemo.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/9/20 0020.
 */
public class PostStandJsonObj {


    /**
     * selfInfo : {}
     * overAvgNum : 0
     * avgScore : 10000
     * selfRanking : 0
     * ranking : [{"score":10000,"createTime":1474334684000,"praiseCount":0,"words":"Great！你的笑容千金难买哦！","isPraise":0,"id":5413,"user":{"id":49845,"name":"","nickname":"远方恒星","avatar":"http://www.qxinli.com/Uploads/Picture/2015-11-27/56583b0d4093f.png","sex":0,"score":75,"showRole":1,"smallWaistcoat":0,"isFollow":0,"mobile":""},"emoticon":"http://7xmzc7.com1.z0.glb.clouddn.com/face:emotion_6_2.png","desc":"，，，，"}]
     * overRate : 0%
     */

    public int overAvgNum;
    public int avgScore;
    public int selfRanking;
    public String overRate;
    /**
     * score : 10000
     * createTime : 1474334684000
     * praiseCount : 0
     * words : Great！你的笑容千金难买哦！
     * isPraise : 0
     * id : 5413
     * user : {"id":49845,"name":"","nickname":"远方恒星","avatar":"http://www.qxinli.com/Uploads/Picture/2015-11-27/56583b0d4093f.png","sex":0,"score":75,"showRole":1,"smallWaistcoat":0,"isFollow":0,"mobile":""}
     * emoticon : http://7xmzc7.com1.z0.glb.clouddn.com/face:emotion_6_2.png
     * desc : ，，，，
     */

    public List<RankingBean> ranking;

    public static class RankingBean {
        public int score;
        public long createTime;
        public int praiseCount;
        public String words;
        public int isPraise;
        public int id;
        /**
         * id : 49845
         * name :
         * nickname : 远方恒星
         * avatar : http://www.qxinli.com/Uploads/Picture/2015-11-27/56583b0d4093f.png
         * sex : 0
         * score : 75.0
         * showRole : 1
         * smallWaistcoat : 0
         * isFollow : 0
         * mobile :
         */

        public UserBean user;
        public String emoticon;
        public String desc;

        public static class UserBean {
            public int id;
            public String name;
            public String nickname;
            public String avatar;
            public int sex;
            public double score;
            public int showRole;
            public int smallWaistcoat;
            public int isFollow;
            public String mobile;
        }
    }
}
