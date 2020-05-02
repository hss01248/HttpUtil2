package com.hss01248.httpdemo.wanandroid.bean;

import java.util.List;

/**
 * time:2020/5/2
 * author:hss
 * desription:
 */
public class ArticleListBean {


    /**
     * curPage : 1
     * datas : [{"apkLink":"","audit":1,"author":"","canEdit":false,"chapterId":502,"chapterName":"自助","collect":false,"courseId":13,"desc":"","descMd":"","envelopePic":"","fresh":false,"id":13217,"link":"https://juejin.im/post/5e8ca71ae51d4546fe2624e5","niceDate":"2天前","niceShareDate":"2天前","origin":"","prefix":"","projectLink":"","publishTime":1588202666000,"selfVisible":0,"shareDate":1588202666000,"shareUser":"躬行之","superChapterId":494,"superChapterName":"广场Tab","tags":[],"title":"Android Jetpack组件之LiveData详解","type":0,"userId":23270,"visible":1,"zan":0}]
     * offset : 0
     * over : false
     * pageCount : 420
     * size : 20
     * total : 8386
     */

    public int curPage;
    public int offset;
    public boolean over;
    public int pageCount;
    public int size;
    public int total;
    public List<DatasBean> datas;

    public static class DatasBean {
        /**
         * apkLink :
         * audit : 1
         * author :
         * canEdit : false
         * chapterId : 502
         * chapterName : 自助
         * collect : false
         * courseId : 13
         * desc :
         * descMd :
         * envelopePic :
         * fresh : false
         * id : 13217
         * link : https://juejin.im/post/5e8ca71ae51d4546fe2624e5
         * niceDate : 2天前
         * niceShareDate : 2天前
         * origin :
         * prefix :
         * projectLink :
         * publishTime : 1588202666000
         * selfVisible : 0
         * shareDate : 1588202666000
         * shareUser : 躬行之
         * superChapterId : 494
         * superChapterName : 广场Tab
         * tags : []
         * title : Android Jetpack组件之LiveData详解
         * type : 0
         * userId : 23270
         * visible : 1
         * zan : 0
         */

        public String apkLink;
        public int audit;
        public String author;
        public boolean canEdit;
        public int chapterId;
        public String chapterName;
        public boolean collect;
        public int courseId;
        public String desc;
        public String descMd;
        public String envelopePic;
        public boolean fresh;
        public int id;
        public String link;
        public String niceDate;
        public String niceShareDate;
        public String origin;
        public String prefix;
        public String projectLink;
        public long publishTime;
        public int selfVisible;
        public long shareDate;
        public String shareUser;
        public int superChapterId;
        public String superChapterName;
        public String title;
        public int type;
        public int userId;
        public int visible;
        public int zan;
        public List<?> tags;
    }
}
