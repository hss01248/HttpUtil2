package com.hss01248.http.response;

/**
 * by hss
 * data:2020/7/1
 * desc:
 */
class ReturnMsg {
    public String code;
    public String realMsg;
    public String friendlyMsg;
    public String responseBody;

    private ReturnMsg(Builder builder) {
        code = builder.code;
        realMsg = builder.realMsg;
        friendlyMsg = builder.friendlyMsg;
        responseBody = builder.responseBody;
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public static final class Builder {
        private String code;
        private String realMsg;
        private String friendlyMsg;
        private String responseBody;

        private Builder() {
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder realMsg(String realMsg) {
            this.realMsg = realMsg;
            return this;
        }

        public Builder friendlyMsg(String friendlyMsg) {
            this.friendlyMsg = friendlyMsg;
            return this;
        }

        public Builder responseBody(String responseBody) {
            this.responseBody = responseBody;
            return this;
        }

        public ReturnMsg build() {
            return new ReturnMsg(this);
        }
    }
}
