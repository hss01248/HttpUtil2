package com.hss01248.http.config;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by hss on 2018/7/21.
 */

public class DataCodeMsgJsonConfig {

    private DataCodeMsgJsonConfig(Builder builder) {
        key_data = builder.key_data;
        key_code = builder.key_code;
        key_msg = builder.key_msg;
        key_isSuccess = builder.key_isSuccess;
        isCodeAsString = builder.isCodeAsString;
        key_extra1 = builder.key_extra1;
        key_extra2 = builder.key_extra2;
        key_extra3 = builder.key_extra3;
        successJudge = builder.successJudge;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(DataCodeMsgJsonConfig copy) {
        Builder builder = new Builder();
        builder.key_data = copy.getKey_data();
        builder.key_code = copy.getKey_code();
        builder.key_msg = copy.getKey_msg();
        builder.key_isSuccess = copy.getKey_isSuccess();
        builder.isCodeAsString = copy.isCodeAsString();
        builder.key_extra1 = copy.getKey_extra1();
        builder.key_extra2 = copy.getKey_extra2();
        builder.key_extra3 = copy.getKey_extra3();
        builder.successJudge = copy.getSuccessJudge();
        return builder;
    }

    public interface DataSuccessJudge{
        boolean isResponseSuccess(JSONObject object);
    }

    public String getKey_data() {
        return key_data;
    }

    public String getKey_code() {
        return key_code;
    }

    public String getKey_msg() {
        return key_msg;
    }

    public String getKey_isSuccess() {
        return key_isSuccess;
    }

    public boolean isCodeAsString() {
        return isCodeAsString;
    }

    public String getKey_extra1() {
        return key_extra1;
    }

    public String getKey_extra2() {
        return key_extra2;
    }

    public String getKey_extra3() {
        return key_extra3;
    }

    public JSONObject getResponseMap() {
        return json;
    }

    public DataSuccessJudge getSuccessJudge() {
        return successJudge;
    }

    //设置标准格式json本次响应的不同字段
    public String key_data ;
    public String key_code ;
    public String key_msg ;
    public String key_isSuccess ;

    boolean isCodeAsString;//code对应的字段是int还是String

    public String key_extra1 ;//json外层额外的字段,如果为空就说明没有
    public String key_extra2 ;
    public String key_extra3 ;

    public JSONObject json;

    public DataSuccessJudge successJudge;


    public static final class Builder {
        private String key_data = "data";
        private String key_code= "code";
        private String key_msg = "msg";
        private String key_isSuccess = "success";
        private boolean isCodeAsString;
        private String key_extra1 = "sysTime";
        private String key_extra2 = "extra2";
        private String key_extra3 = "extra3";
        private Map<String, Object> responseMap;
        private DataSuccessJudge successJudge;

        private Builder() {
        }

        public Builder key_data(String key_data) {
            this.key_data = key_data;
            return this;
        }

        public Builder key_code(String key_code) {
            this.key_code = key_code;
            return this;
        }

        public Builder key_msg(String key_msg) {
            this.key_msg = key_msg;
            return this;
        }

        public Builder key_isSuccess(String key_isSuccess) {
            this.key_isSuccess = key_isSuccess;
            return this;
        }

        public Builder isCodeAsString(boolean isCodeAsString) {
            this.isCodeAsString = isCodeAsString;
            return this;
        }

        public Builder key_extra1(String key_extra1) {
            this.key_extra1 = key_extra1;
            return this;
        }

        public Builder key_extra2(String key_extra2) {
            this.key_extra2 = key_extra2;
            return this;
        }

        public Builder key_extra3(String key_extra3) {
            this.key_extra3 = key_extra3;
            return this;
        }

        public Builder responseMap(Map<String, Object> responseMap) {
            this.responseMap = responseMap;
            return this;
        }

        public Builder successJudge(DataSuccessJudge successJudge) {
            this.successJudge = successJudge;
            return this;
        }

        public DataCodeMsgJsonConfig build() {
            return new DataCodeMsgJsonConfig(this);
        }
    }
}
