package com.hss01248.httpdemo.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/9/20 0020.
 */
public class PostCommonJsonBean {


    /**
     * data : [{"uid":54140,"createTime":1474124040,"nickName":"隨 訫","articleId":1738,"avatar":"http://www.qxinli.com/Uploads/Picture/2015-11-27/56583b0d4093f.png","showRole":1,"content":"回复 @木兰殇 ：这个问题我也在思考"},{"uid":55722,"createTime":1473805862,"nickName":"木兰殇","articleId":1738,"avatar":"http://www.qxinli.com/Uploads/Picture/2015-11-27/56583b0d4093f.png","showRole":1,"content":"回复 @圆滚滚 ：人往往做起来会很难"},{"uid":55722,"createTime":1473805815,"nickName":"木兰殇","articleId":1738,"avatar":"http://www.qxinli.com/Uploads/Picture/2015-11-27/56583b0d4093f.png","showRole":1,"content":"嗯嗯，我也是这样想的"},{"uid":511,"createTime":1473776866,"nickName":"圆滚滚","articleId":1738,"avatar":"http://7xnwnf.com2.z0.glb.qiniucdn.com/Uploads_Avatar_511_565fd7cc53c1e.jpg?imageMogr2/crop/!100x100a0a0","showRole":1,"content":"虽然知道改变认知很重要，但是怎么改变"}]
     * code : 0
     */

    public int code;
    /**
     * uid : 54140
     * createTime : 1474124040
     * nickName : 隨 訫
     * articleId : 1738
     * avatar : http://www.qxinli.com/Uploads/Picture/2015-11-27/56583b0d4093f.png
     * showRole : 1
     * content : 回复 @木兰殇 ：这个问题我也在思考
     */

    public List<DataBean> data;

    public static class DataBean {
        public int uid;
        public int createTime;
        public String nickName;
        public int articleId;
        public String avatar;
        public int showRole;
        public String content;
    }
}
