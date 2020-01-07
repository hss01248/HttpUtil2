package com.hss01248.httpdemo.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/4/10 0010.
 */

public class UserInfo {



        /**
         * birthday : 2016-02-09
         * followCount : 92
         * signature : 好好的，别放弃。
         * posProvince : {"id":210000,"level":1,"name":"辽宁省","upid":0}
         * posCity : {"id":210300,"level":2,"name":"鞍山市","upid":210000}
         * description : 我要咨询师～哈哈哈哈哈哈哈哈哈哈
         * fansCount : 36
         * isAcceptConsult : 0
         * score : 1055
         * uid : 236
         * inviteCount : 0
         * identity : 5
         * userTags : [{"id":7,"status":1,"pid":1,"sort":6,"title":"家有儿子"},{"id":14,"status":1,"pid":1,"sort":13,"title":"辍学中"},{"id":7,"status":1,"pid":1,"sort":6,"title":"家有儿子"},{"id":8,"status":1,"pid":1,"sort":7,"title":"家有女儿"},{"id":14,"status":1,"pid":1,"sort":13,"title":"辍学中"}]
         * isProductCounselor : 0
         * showRole : 1
         * level : LV4
         * nickName : 张雨晴
         * sex : 1
         * mobile : 15220080434
         * bgImg :
         * avatar : http://static.qxinli.com/1f0431d411a14964b217f551c7983830.jpg
         * sessionId : 64CCA5E8FEDD881513779124C9EA9D2C
         * inviteQRCode : http://static.qxinli.com//userQr/236/1cf423b7b97745f198186bdddb2ce8f9.png
         * freeTimes : 0
         * posDistrict : {"id":210311,"level":3,"name":"千山区","upid":210300}
         * inviteCode : dsomxh
         * allTags : [{"id":2,"status":1,"pid":1,"sort":1,"title":"我是爸爸"},{"id":3,"status":1,"pid":1,"sort":2,"title":"我是妈妈"},{"id":4,"status":1,"pid":1,"sort":3,"title":"小学生"},{"id":5,"status":1,"pid":1,"sort":4,"title":"初中生"},{"id":6,"status":1,"pid":1,"sort":5,"title":"高中生"},{"id":7,"status":1,"pid":1,"sort":6,"title":"家有儿子"},{"id":8,"status":1,"pid":1,"sort":7,"title":"家有女儿"},{"id":9,"status":1,"pid":1,"sort":8,"title":"独生子女"},{"id":10,"status":1,"pid":1,"sort":9,"title":"二孩时代"},{"id":11,"status":1,"pid":1,"sort":10,"title":"高考备考"},{"id":12,"status":1,"pid":1,"sort":11,"title":"中考临近"},{"id":13,"status":1,"pid":1,"sort":12,"title":"出国党"},{"id":14,"status":1,"pid":1,"sort":13,"title":"辍学中"},{"id":15,"status":1,"pid":1,"sort":15,"title":"留守孩"},{"id":16,"status":1,"pid":1,"sort":14,"title":"单亲家庭"}]
         */

        public String birthday;
        public int followCount;
        public String signature;
        public PosProvinceBean posProvince;
        public PosCityBean posCity;
        public String description;
        public int fansCount;
        public int isAcceptConsult;
        public int score;
        public int uid;
        public int inviteCount;
        public int identity;
        public int isProductCounselor;
        public int showRole;
        public String level;
        public String nickName;
        public int sex;
        public String mobile;
        public String bgImg;
        public String avatar;
        public String sessionId;
        public String inviteQRCode;
        public int freeTimes;
        public PosDistrictBean posDistrict;
        public String inviteCode;
        public List<UserTagsBean> userTags;
        public List<AllTagsBean> allTags;

        public static class PosProvinceBean {
            /**
             * id : 210000
             * level : 1
             * name : 辽宁省
             * upid : 0
             */

            public int id;
            public int level;
            public String name;
            public int upid;
        }

        public static class PosCityBean {
            /**
             * id : 210300
             * level : 2
             * name : 鞍山市
             * upid : 210000
             */

            public int id;
            public int level;
            public String name;
            public int upid;
        }

        public static class PosDistrictBean {
            /**
             * id : 210311
             * level : 3
             * name : 千山区
             * upid : 210300
             */

            public int id;
            public int level;
            public String name;
            public int upid;
        }

        public static class UserTagsBean {
            /**
             * id : 7
             * status : 1
             * pid : 1
             * sort : 6
             * title : 家有儿子
             */

            public int id;
            public int status;
            public int pid;
            public int sort;
            public String title;
        }

        public static class AllTagsBean {
            /**
             * id : 2
             * status : 1
             * pid : 1
             * sort : 1
             * title : 我是爸爸
             */

            public int id;
            public int status;
            public int pid;
            public int sort;
            public String title;
        }

}
