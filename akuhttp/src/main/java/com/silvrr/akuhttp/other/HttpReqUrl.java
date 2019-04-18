package com.silvrr.akuhttp.other;


/**
 * Created by cyp on 2016/1/25.c
 * <p>
 * 存放Http请求的URL
 */
public class HttpReqUrl {

    /**
     * app官方下载地址
     */
    public static final String CHECK_NEW_VERSION_URL = "/api/common/appVersion/check.json";

    public static final String TERM_OF_USE_URL = "http://mall.akulaku.com/TermsConditions.html";

    public static final String REGISTER_URL = "/api/json/user/register/reg.do";  //注册
    public static final String VERIFY_CODE_URL = "/api/json/user/register/captcha/get.do"; //获取验证码
    public static final String SET_LOGIN_PWD_URL = "/api/json/user/register/pwd/set.do"; //设置登录密码
    public static final String GET_SMS_VALCODE = "/api/json/public/phone/captcha.do"; //获取短信验证码
    public static final String GET_PIC_VALCODE = "/api/json/public/image/captcha.do"; //获取图片验证码
    public static final String LOGIN_URL = "/api/json/user/login.do"; //登录
    public static final String RESET_LOGIN_PWD_URL = "/api/json/user/pwd/reset.do"; //重置密码
    public static final String CAPTCHA_CODE_URL = "/api/json/user/forget/captcha/get.json"; //获取验证码
    public static final String FIND_LOGIN_PWD_URL = "/api/json/user/forget/pwd/reset.do"; //设置新密码（用于密码找回）
    public static final String LOGOUT_URL = "/api/json/user/logout.do"; //登出
    public static final String COUNTRY_LIST_URL_V2 = "/api/common/country/list/v2.json"; //包含越南的国家列表
    public static final String HISTORY_LIST_URL = "/api/json/user/amount/list.json"; //交易流水历史列表

    //validate
    public static final String VALIDATION_UPDATE_URL = "/api/json/user/update.do"; //姓名职业地址更新
    public static final String VALIDATION_AUTHENTICATION_URL = "/api/json/user/auth/config/verify/list/v2.json"; //验证组件
    public static final String VALIDATION_OFFLINE_URL = "/api/json/user/auth/config/verify/offline/list.json"; //线下申请额外资料
    public static final String COMMIT_VALIDATION = "/api/json/user/auth/submit.do"; //提交验证
    public static final String CONFIRM_VALIDATED_URL = "/api/json/user/auth/confirm.do"; //确认提交完成
    public static final String CHECK_OFFLINE_URL = "/api/json/user/offline/status.json";
    public static final String CONFIRM_VALIDATED_OFFLINE_URL = "/api/json/user/offline/auth/confirm.do"; //确认提交完成
    public static final String CONFIRM_VALIDATED_BASE_URL = "/api/json/user/auth/base/confirm.do"; //确认基本资料提交完成
    public static final String VALIDATION_OCCUPATION_URL = "/api/json/user/occupation/list.json"; //获取身份验证的职业
    public static final String VALIDATION_QUOTA_CHECK = "/api/json/user/auth/base/check.do";//虚拟额度检查

    public static final String UPLOAD_FILE_URL = "/api/json/user/upload/url/get.json"; //上传图片
    public static final String DELIVER_LIST_URL = "/api/json/user/address/list.json"; //获取收货地址列表
    public static final String DELIVER_EDIT_URL = "/api/json/user/address/edit.do"; //编辑已收货地址
    public static final String DELIVER_ADD_URL = "/api/json/user/address/add.do"; //添加新收货地址
    public static final String DELIVER_DEL_URL = "/api/json/user/address/delete.do"; //删除收货地址
    public static final String DELIVER_DEFAULT_URL = "/api/json/user/address/default/set.do"; //设置默认收货地址
    public static final String PROFILE_AVATAR_URL = "/api/json/user/avatar/set.do"; //更新用户个人头像
    public static final String PROFILE_DETAIL_URL = "/api/json/user/detail.json"; //获取用户个人详情信息
    public static final String DELIVER_LIST_V2_URL = "/api/json/user/address/list/v2.json";

    //bill
    public static final String CURRENT_BILL_URL = "/api/json/bill/current.json"; //当前账单
    public static final String BILL_DEBT_LIST_URL = "/api/json/bill/debt/list.json"; //历史欠款详情
    public static final String BILL_DETAILS_URL = "/api/json/bill/detail.json"; //商品还款详情(还款进度，期数）
    public static final String BILL_DETAIL_LIST_URL = "/api/json/bill/detail/list.json"; //账单明细
    public static final String BILL_HISTORY_LIST_URL = "/api/json/bill/history/list.json"; //历史
    public static final String BILL_COMPLETED_COMMODITY_LIST_URL = "/api/json/bill/item/payoff/list.json"; //已还清列表
    public static final String BILL_PAY_URL = "/api/json/repayment/add.do"; //支付帐单
    public static final String PAY_METHOD_URL = "/api/json/pay/list.json"; //支付方式

    //订单
    public static final String ORDER_LIST_URL3 = "/api/json/order/list.json"; //订单列表
    public static final String ORDER_LIST_URL = "/api/json/order/item/list.json"; //订单列表
    public static final String ORDER_LIST_URL2 = "/api/json/order/third/party/query.do"; //订单列表For代购
    public static final String ORDER_CANCEL_URL = "/api/json/order/item/cancel.do"; //取消订单
    public static final String ORDER_CANCEL_URL2 = "/api/json/daigou/order/cancel.do"; //取消订单
    public static final String ORDER_DELETE_URL2 = "/api/json/daigou/order/delete.do"; //删除订单
    public static final String ORDER_RECEIPT_URL = "/api/json/order/item/receipt.do"; //确认收货
    public static final String ORDER_LOGISTIC_V2_URL = "/api/json/order/lineItem/logistics/list.json";//物流信息改版
    public static final String ORDER_DELIVERED_SHARE_URL = "/api/json/order/item/shared/count.do"; //已收货订单的分享
    public static final String ORDER_SHARE_RESULT_URL = "/api/json/order/item/shared.do"; //确认已收货订单的分享
    public static final String ORDER_INVOICE_URL = "/api/json/order/item/invoice/email.do"; //尝试发送订单的发票
    public static final String VERIFY_EMAIL_URL = "/api/json/user/email/code.do"; //尝试发送邮箱验证信息

    //信用额度
    public static final String QUOTA_NOW_URL = "/api/json/user/quote/now.json"; //当前信用信息
    public static final String QUOTA_LIST_URL = "/api/json/user/quote/list.json"; //信用信息详情列表

    //消息中心
    public static final String NEWS_LIST_URL = "/api/json/user/news/list.json"; //消息列表
    public static final String NEWS_MARK_URL = "/api/json/user/news/read.do"; //消息是不是被查看标记
    public static final String NEWS_UNREAD_URL = "/api/json/user/news/unread/cnt.json"; //未读消息数
    public static final String NEWS_UNREAD_PUBLIC_URL = "/api/json/user/news/public/cnt.json"; //未读消息数（公共类型消息）
    public static final String NEWS_MERCHANT_URL = "/api/json/chat/vendor/group.do"; //Vendor消息列表
    public static final String NEWS_TOTAL_MERCHANT_URL = "/api/json/chat/vendor/unread/cnt.do";//所有未读的merchant news
    public static final String NEWS_DELETE_URL = "/api/json/user/news/delete.do";//删除消息
    public static final String NEWS_ITEM_COLLECT_URL = "/api/json/item/collect.do"; //收藏与取消收藏接口

    public static final String NEWS_MERCHANT_DELETE_URL = "/api/json/chat/vendor/closeGroup.do"; // 删除Merchant消息

    public static final String CALL_LOG_URL = "/api/json/user/call/add.do";//上传用户通话记录
    public static final String CONTACTS_EDIT_URL = "/api/json/user/contact/edit.do";//仅修改联系人名字
    public static final String CONTACTS_ADD_URL_NEW = "/risk/user/user_address_book";//上传用户通话记录(新)
    public static final String CONTACTS_LIST_URL_NEW = "/risk/user/user_address_book";//获取联系人列表
    public static final String CONTACTS_VERSION = "/risk/user/user_address_version";//

    public static final String SMS_LASTID_URL = "/risk/user/sms_latest_id";//获取最新短信id
    public static final String SMS_UPLOAD_URL = "/risk/user/sms_report";//上传短信
    public static final String DATA_ANALYSIS_URL = "/risk/user/action_report"; //行为日志上报
    public static final String DATA_ANALYSIS_URL_NEW = "/risk/user/action_report_2"; //新行为日志上报
    public static final String DATA_RELATION_SHIP_URL = "/risk/user/fb_relationship"; //上传facebook朋友列表
    public static final String DEVICE_SUMMARY = "/risk/user/device_summary"; //设备信息上报

    //配置
    public static final String COMMON_CONFIG_URL = "/api/common/config.json"; //配置

    //地理位置上报
    public static final String LOCATION_REPORT_URL = "/api/json/user/location/add.do"; //地理位置上报
    //活动
    public static final String PROMOS_REFERRER_URL = "/api/json/user/referrer/set.do"; //推荐推广
    public static final String COUPONS_LIST_URL = "/api/json/coupon/list.json"; //卡券列表

    //修改手机号
    public static final String PHONE_OLD_VERIFY_URL = "/api/json/user/phone/change/old/captcha.json"; //旧手机验证码
    public static final String CHECK_PHONE_OLD_VERIFY_URL = "/api/json/user/phone/change/old/check.do"; //验证旧手机验证码
    public static final String PHONE_NEW_VERIFY_URL = "/api/json/user/phone/change/new/captcha.json"; //验证新手机验证码
    public static final String CHECK_PHONE_NEW_VERIFY_URL = "/api/json/user/phone/change/new/set.do"; //验证新手机验证码
    public static final String ORDER_REFUND_ACCOUNT_URL = "/api/json/user/bank/info.json";//用户退款帐号
    public static final String ORDER_REFUND_BANK_URL = "/api/common/bank/list.json";//银行列表
    public static final String ORDER_REFUND_CONFIRM_URL = "/api/json/order/item/cancel/v2.do";//取消订单&退款
    public static final String ORDER_REFUND_ALL_CONFIRM_URL = "/api/json/order/item/refund/all.do";

    //首页商品列表
    public static final String COMMODITY_CATEGORY_TAB_LIST_URL = "/api/json/public/category.do"; //商品分类tab list列表
    public static final String COMMODITY_HOME_TAB_LIST_URL = "/api/json/public/category/v2.do"; //商品首页tab list列表
    public static final String COMMODITY_ITEM_NORMAL_URL = "/api/json/public/item.do"; //普通分类商品列表
    public static final String COMMODITY_ITEM_DETAIL_URL = "/api/json/public/item/detail.do"; //商品详情

    //减价(限时销售)活动
    public static final String GOODS_ACTIVITY_PRICE_BREAK_URL = "/api/json/public/category/activity/item.do"; //获取活动商品&海报&时间等
    //主题活动
    public static final String GOODS_ACTIVITY_SUBJECT_URL = "/api/json/public/category/activity/item/v2.do";
    public static final String GOODS_ACTIVITY_URL = "/api/json/public/category/activity.do"; //获取活动列表
    public static final String GOODS_NEW_HOT_URL = "/api/json/public/new/hot.do"; //获取活动商品
    public static final String GOODS_ITEM_URL = "/api/json/public/item.do"; //获取商品
    public static final String GOODS_LIKE_URL = "/api/json/public/item/like.do"; //商品点赞
    public static final String GOODS_HOT_KEYWORD_URL = "/api/json/public/keyword.do";
    public static final String GOODS_SEARCH_URL = "/api/json/public/item/search.do";
    public static final String GOODS_SEARCH_SORT_URL = "/api/json/public/sort/list.do";//category搜索商品排序依据
    public static final String GOODS_ITEM_DETAIL_URL = "/api/json/public/item/v2.do";

    public static final String SHOPPING_CART_CNT_URL = "/api/json/cart/cnt.json"; //购物车数量
    public static final String SHOPPING_CART_ADD_URL = "/api/json/cart/add.do"; //购物车数量
    public static final String SHOPPING_CART_WEB_PAYABLE_URL = "/api/json/cart/webPayable.do";

    public static final String SUMBIT_VERIFY_CALL_TIME_URL = "/api/json/user/auth/callTime.do"; //提交在线电话验证时间

    //action link
    public static final String ACTION_LINK_URL = "/api/json/public/action/link.do"; //活动的link
    //Attribution statistics
    public static final String APPSFLYER_ATTRIBUTION_STATISTICS = "/api/json/public/appsflyer/info.do";

    //livechat
    public static final String LIVECHAT_READ_MESSAGE_URL = "/api/json/chat/cs/read.do";
    public static final String LIVECHAT_QUERY_GID_URL = "/api/json/chat/cs/gid/query.do";
    public static final String LIVECHAT_SEND_MESSAGE_URL = "/api/json/chat/cs/send.do";
    public static final String LIVECHAT_CLOSE_GROUP_URL = "/api/json/chat/cs/closeGroup.do";
    public static final String LIVECHAT_GET_UNREAD_COUNT_URL = "/api/json/chat/cs/unread/cnt.do";

    //merchant livechat
    public static final String LIVECHAT_MERCHANT_READ_MESSAGE_URL = "/api/json/chat/vendor/read.do";
    public static final String LIVECHAT_MERCHANT_SEND_MESSAGE_URL = "/api/json/chat/vendor/send.do";

    //Share Credit Limit
    public static final String SHARE_CONFIRM_URL = "/api/json/user/share/activity/confirm.do";
    public static final String SHARE_CHECK_URL = "/api/json/user/share/activity/check.do";

    //third party order
    public static final String THIRD_PARTY_GET_WEBSETE_URL = "/api/json/daigou/order/website.do";
    public static final String THIRD_PARTY_GET_CATEGORY_URL = "/api/json/daigou/order/category.do";
    public static final String THIRD_PARTY_ORDER_CREATE_URL = "/api/json/daigou/order/submit.do";
    public static final String THIRD_PARTY_CALCULATE_URL = "/api/json/daigou/order/calcPrice.do";

    //call status
    public static final String CALL_STATUS_GET = "/api/json/user/call/risk/status.do";

    //referral code evaluate
    public static final String REFERRAL_CODE_EVALUATE = "/api/json/user/referCode/evaluate.do";

    //获取城市
    public static final String SALES_AREA_CITY = "/api/json/public/item/sales/area.do";
    public static final String AREA_SWITCH_CTIY = "/api/json/user/switch/area.do";

    /**
     * 收藏列表
     */
    public static final String WISH_LIST_URL = "/api/json/item/wish/list.do";
    /**
     * 获取用户信用分
     */
    public static final String GET_CREDIT_SCORE_URL = "/risk/user/user_credit_classifier";
    /**
     * 获取用户信用分历史记录
     */
    public static final String GET_CREDIT_SCORE_HISTORY = "/risk/user/user_credit_classifier_his";
    /**
     * 新的收藏接口
     */
    public static final String COLLECT_URL_V2 = "/api/json/item/collectv2.do"; //收藏接口
    /**
     * 是否需要上传fb 好友列表
     */
    /**
     * 获取消息接口，需要登录的
     */
    public static final String GET_USER_MSG_LOGIN_URL = "/api/json/user/news/get/userMsg.do";
    /**
     * 获取消息接口，不需要登录的
     */
    public static final String GET_USER_MSG_URL = "/api/json/user/news/get/SystemMsg.do";
    /**
     * 传感器数据上传
     */
    public static final String DATA_SENSOR_LOG_URL = "/risk/user/sensor_log";


    /**
     * 获取可用的优惠券列表
     */
    public static final String PAYMENT_COUPON_GET_URL = "/api/json/coupon/usable/get.do";
    /**
     * 检测订单是否需要填入邮箱
     */
    public static final String PURCHASE_ORDER_CHECK_EMAIL = "/api/json/order/saverTicket/check.do";
    /**
     * 更新用户邮箱信息
     */
    public static final String PURCHASE_ORDER_UPLOAD_EMAIL = "/api/json/user/email/confirm/set.do";
    /**
     * 单个商品购买
     */
    public static final String PURCHASE_ORDER_ADD_URL = "/api/json/order/add.do";
    /**
     * 购物车购买
     */
    public static final String PURCHASE_ORDER_CART_ADD_URL = "/api/json/order/cart/add.do";
    /**
     * 获取录音数据
     */
    public static final String PURCHASE_ORDER_RECORD_NUMBER_URL = "/risk/user/voice/get_num";
    /**
     * 上次录音文件+文件id+录音内容(666666)
     */
    public static final String PURCHASE_ORDER_UPLOAD_RECORD_URL = "/risk/user/voice/upload";
    /**
     * 购物车列表
     */
    public static final String SHOPPING_CART_LIST_URL = "/api/json/cart/list.json";
    /**
     * 修改购物车列表的某一个
     */
    public static final String SHOPPING_CART_EDIT_URL = "/api/json/cart/edit.do";
    /**
     * 修改整个购物车列表
     */
    public static final String SHOPPING_CART_LIST_EDIT_URL = "/api/json/cart/editBatch.do";
    /**
     * 删除购物车列表(可以单独也可以批量)
     */
    public static final String SHOPPING_CART_DELETE_URL = "/api/json/cart/delete.do";

    /**
     * 运费
     */
    public static final String FREIGHT_GET_URL = "/api/json/order/calc/freight.do";

    /**
     * 活动商品限购数量
     */
    public static final String SALE_NUMBER_LIMIT_URL = "/api/json/order/activity/number/limit.do";

    /**
     * 人脸识别上传地址
     */
    public static final String FACE_DETECTION_URL = "/risk/user/upload_pic_feature";
    public static final String BANK_VALIDATOR_URL = "/api/json/order/bank/name/validator.do";
    /**
     * 请求首页模块列表
     */
    public static final String HOME_PAGE_HEADER_FRAME = "/macaron/api/json/public/module/list/get";
    /**
     * 人气商品（popular），推荐商品（for you）
     */
    public static final String HOME_PAGE_HEADER_RECOMMEND = "/macaron/api/json/public/recommend/item/get";
    /**
     * 秒杀（折扣活动 flash sale type == 4）， 活动商品 （type == 9）
     */
    public static final String HOME_PAGE_HEADER_ACTIVE = "/api/json/public/category/activity/item.do";
    /**
     * 秒杀活动列表
     */
    public static final String HOME_PAGE_HEADER_FLASH_SALE_LIST = "/macaron/api/json/public/seckill/list/get";

    /**
     * 充值服务类
     */
    public static final String RECHARGE_SERVICE_URL = "/macaron/api/json/public/service/module/get.do";

    /**充值商品列表(印尼)*/
    public static final String RECHARGE_PRODUCT_LIST_ID = "/api/json/public/sepulsa/production/list.json";

    /**充值credit pay检查(印尼)*/
    public static final String RECHARGE_CHECK_CREDITPAY_ID = "/api/json/order/sepulsa/check.json";
    /**虚拟商品credit pay检查(印尼，印尼，除了飞机票，选择支付方式为creditPay)*/
    public static final String VIRTUAL_CHECK_CREDITPAY_ID = "/macaron/api/json/user/virtual/credit/pay/check.json";

    /**越南虚拟商品分期检查(越南)*/
    public static final String RECHARGE_CHECK_INSTALLMENT_VN = "/api/json/order/vnptRecharge/check.do";

    /**充值下单(印尼)*/
    public static final String RECHARGE_CREATE_ORDER_ID = "/api/json/order/sepulsa/add.do";
    /**获取社保/电费信息*/
    public static final String GET_SOCIETY_ELECTRIC_INFO = "/api/json/order/sepulsa/inquire.json";
    /**债务账单查询*/
    public static final String GET_ID_INSTALLMENT = "/api/json/order/sepulsa/multifinance/bill/inquire.json";
    /**充值商品列表(菲律宾)*/
    public static final String RECHARGE_PRODUCT_LIST_PH = "/api/json/public/ph/recharge/products/get.do";

    /**运营商或游戏列表(菲律宾)*/
    public static final String RECHARGE_CARRIER_LIST_PH = "/api/json/public/ph/recharge/item/get.do";

    /**获取分期数据(菲律宾)*/
    public static final String RECHARGE_INSTALLMENT_PH = "/api/json/order/ph/recharge/installment/get.do";

    /**菲律宾充值下单(菲律宾)*/
    public static final String RECHARGE_CREATE_ORDER_PH = "/api/json/order/ph/recharge/order.do";

    /**运营商或游戏列表(越南)*/
    public static final String RECHARGE_CARRIER_LIST_VN = "/api/json/public/vnpt/suppliers.do";

    /**根据code获取产品列表(越南)*/
    public static final String RECHARGE_PRODUCT_LIST_VN = "/api/json/public/vnpt/product.do";

    /**获取分期数据，支持creditPay和installment(越南)*/
    public static final String RECHARGE_INSTALLMENT_VN = "/api/json/order/vnpt/recharge/credit/get.do";

    /**充值下订单(越南)*/
    public static final String RECHARGE_CREATE_ORDER_VN = "/api/json/order/vnpt/add.do";

    /**获取便利店代金券列表信息(便利店只有印尼有)*/
    public static final String RECHARGE_STORE_VOUCHER = "/api/json/cvs/voucher/get.do";

    /**便利店代金券下订单(便利店只有印尼有)*/
    public static final String RECHARGE_STORE_VOUCHER_ORDER = "/api/json/order/cvs/add.do";
    /**根据id获取便利店信息*/
    public static final String GET_CVS_INFO = "/api/json/cvs/cvs/id/get.do";
    /**密码校验*/
    public static final String CHECK_PASSWORD = "/api/json/user/password/verify.do";

    /**检查用户是否可购买便利店代金券*/
    public static final String CHECK_CVS_LIMIT = "/api/json/cvs/check.do";
    /**获取印尼水费账单信息*/
    public static final String GET_WATER_FEE_ID = "/api/json/order/sepulsa/pdam/bill/inquire.do";
    /**获取印尼水费区域信息*/
    public static final String GET_WATER_AREA_ID = "/api/json/order/sepulsa/pdam/operator/inquire.do";
    /**获取印尼UniPin游戏充值金额列表*/
    public static final String GET_GAME_MONEYLIST_ID = "/api/json/unipin/denominations.do";
    /**印尼UniPin游戏充值下单*/
    public static final String RECHARGE_GAME_ORDER_ID = "/api/json/order/unipin/add.do";
    /**印尼游乐园列表*/
    public static final String GET_PARK_LIST_ID = "/api/json/park/park/get.do";
    /**印尼游乐园开放时间列表*/
    public static final String GET_PARK_DATE_LIST_ID = "/api/json/park/voucher/get.do";
    /**印尼游乐园检查购买数量限制*/
    public static final String CHECK_PARK_LIMIT_ID = "/api/json/park/check.do";
    /**印尼游乐园下订单*/
    public static final String GET_PARK_ORDER_ID = "/api/json/order/park/add.do";
    /**印尼音乐列表、越南电影院列表*/
    public static final String GET_IDJOOX_VNFILM_LIST = "/api/json/public/code/store/get.do";
    /**印尼音乐、越南电影院*/
    public static final String GET_IDJOOX_VNFILM_INFO = "/api/json/public/code/store/id/get.do";
    /**印尼音乐、越南电影院销售信息*/
    public static final String GET_IDJOOX_VNFILM_SALEINFO = "/api/json/public/code/type/get.do";
    /**印尼音乐、越南电影院下单*/
    public static final String GET_IDJOOX_VNFILM_ORDER = "/api/json/order/code/add.do";
    /**印尼音乐购买限制*/
    public static final String CHECK_IDJOOX_VNFILM_LIMIT = "/api/json/code/check.do";
    /**印尼电影院购买限制*/
    public static final String CHECK_ID_FILM_LIMIT = "/api/json/movie/check.do";
    /**印尼UniPin游戏充值购买限制*/
    public static final String CHECK_ID_GAME_LIMIT = "/api/json/unipin/check.do";
    /**印尼飞机票下单*/
    public static final String GET_PLANE_TICKET_ORDER = "/api/json/order/tiketflight/add.do";
    /**印尼飞机票预订结果查询*/
    public static final String GET_BOOK_TICKET_RESULT = "/api/json/tiket/flight/order/check.do";
    /**印尼机票预订*/
    public static final String BOOK_PLANE_TICKET = "/api/json/tiket/flight/order/add.do";
    /**印尼删除乘客*/
    public static final String DELETE_FLIGHT_PASSENGER = "/api/json/tiket/flight/passenger/delete.do";
    /**印尼添加乘客*/
    public static final String ADD_FLIGHT_PASSENGER = "/api/json/tiket/flight/passenger/add.do";
    /**印尼获取乘客*/
    public static final String GET_FLIGHT_PASSENGER = "/api/json/tiket/flight/passenger/get.do";
    /**印尼查看机票详细*/
    public static final String GET_FLIGHT_TICKET_DETAIL = "/api/json/tiket/flight/data/get.do";
    /**印尼搜索机票*/
    public static final String SEARCH_FLIGHT_TICKET = "/api/json/tiket/flight/search.do";
    /**印尼机场列表*/
    public static final String GET_AIRPORT_LIST = "/api/json/tiket/flight/airport/get.do";
    /**印尼机票相关国家列表*/
    public static final String GET_FLIGHT_COUNTRY_LIST = "/api/json/tiket/flight/country/get.do";
    /**印尼机票相关乘客称呼列表*/
    public static final String GET_FLIGHT_PERSON_TITLE_LIST = "/api/json/tiket/flight/title/get.do";
    /**印尼机票获取token*/
    public static final String GET_FLIGHT_TOKEN = "/api/json/tiket/flight/token/get.do";
    /**获取相关相似商品列表*/
    public static final String GET_RELATE_GOODS = "/macaron/api/json/public/related/item/get.do";
    /**获取上次购买使用的地址*/
    public static final String GET_LAST_SHOPPING_ADDRESS = "/macaron/api/json/recently/used/address.json";

    /**
     * 订单结果页获取是否弹出促销弹框接口
     * (当前活动是2017/12/23-2018/01/04期间满足指定条件的订单完成后，在订单结果页会弹窗提示获得分期券，)
     */
    public static final String GET_SHOPPING_RESULT_PROMOTION = "/api/json/activity/coupon/payment/check.do";

    /**
     * 申请声音校验
     */
    public static final String UPLOAD_VOICE_FOR_APPLY = "/risk/user/voice/base/commit";

    /** 获取声纹数字 **/
    public static final String VOICE_PRE_COMMIT = "/risk/user/voice/base/pre_commit";

    /**获取app配置信息*/
    public static final String GET_APP_CONFIG = "/json/public/app-config.json";

    /**启动页广告*/
    public static final String SPLASH_AD = "/macaron/api/json/public/login/advertisement/get.do";
    //----------- 现金贷相关 接口

    /**
     *现金贷款业务获取声纹锁字符串
     */
    public static final String VOICE_DEBT_GET_NUM_URL = "/risk/user/voice/cash_debt/get_num";

    /**
     * 现金贷上传语音文件
     */
    public static final String VOICE_DEBT_UPLOAD_URL = "/risk/user/voice/cash_debt/upload";

    /**
     * 现金贷账单
     */
    public static final String LOAN_BILL_URL = "/api/json/loan/cash/unpaid/list.json ";

    /**
     * 现金贷，支付账单接口
     */
    public static final String LOAN_BILL_PAY_URL = "/api/json/loan/cash/repayment/add.do"; //支付帐单

    /**
     * 现金贷账单历史记录(即已还清)
     */
    public static final String LOAN_BILL_HISTORY_LIST_URL = "/api/json/loan/cash/paid/list.json";

    /**
     * 提交现金贷请求
     */
    public static final String LOAN_BILL_CASH_SUBMIT_URL = "/api/json/loan/cash/submit.do";

    /** 上传安装的apk信息*/


    /** 上传安装的apk信息 */
    public static final String UPDATE_INSTALLED_APK_INFOS = "/risk/user/app_report";


    /**
     *获取所有电影院线列表
     */
    public static final String GET_MOVIE_CHAIN_LIST = "/api/json/movie/movie/get.do";

    /**获取特定电影院线的所有门店信息*/
    public static final String GET_MOVIE_STORE_LIST = "/api/json/movie/store/get.do";

    /**
     * 印尼电影票下订单
     */
    public static final String GET_ID_MOVIE_TICKET_ORDER = "/api/json/order/movie/add.do";

    /**
     * 生成图片前获取邀请码
     */
    public static final String FRIEND_INVITE_GET_INVITE_CODE = "/api/json/user/invite/code/get.do";

    /**
     * 邀请列表是否显示voluteer入口
     */
    public static final String FRIEND_INVITE_IS_SHOW_VOLUTEER = "/api/json/user/volunteer/status/get.do";

    /**
     * 首页推荐好友是否显示hot图标
     */
    public static final String FRIEND_INVITE_HOME_IS_SHOW_HOT_TAG = "/api/json/user/volunteer/status/get.do";

    /**
     * 用户setting界面是否显示new图标
     */
    public static final String SETTINGS_IS_SHOW_NEW_TAG = "/api/json/public/personal/status/get.do";
    public static final String SETTINGS_IS_DISMISS_NEW_TAG = "/api/json/public/personal/status/set.do";
    /**
     * 删除推荐
     */
    public static final String FRIEND_INVITE_DELETE_FRIEND = "/api/json/user/volunteer/friend/delete.do";

    /**
     * 模糊检测额外的数据上报
     */
    public static final String BLUR_DETECT_REPORT = "/risk/user/UploadPictureClarity";

    /**
     * 商品详情-相似商品和相关商品列表,以type区分
     * https://git.silvrr.com/document/wiki/wikis/%E5%95%86%E5%93%81#%E7%9B%B8%E5%85%B3%E7%9B%B8%E4%BC%BC%E5%95%86%E5%93%81%E5%88%97%E8%A1%A8
     */
    public static final String ITEM_DETAIL_RELATED = "/macaron/api/json/public/related/item/get";




    /**
     * 获取FAQ
     */
    public static final String GET_FAQ_CATEGORY = "/api/json/faq/getFAQCategory.do";

    /**
     * 获取现金贷个人信息采集列表
     */
    public static final String GET_CASH_PERSONAL_COLLECT_LIST = "/api/json/user/auth/via/risk/list.json";

    /**
     * 提交验证 (现金贷流程)
     */
    public static final String COMMIT_CASH_VALIDATION = "/api/json/user/auth/via/risk/submit.do"; //

    /**
     * 获取声纹验证,声纹采集等，需要读取的数据
     */
    public static final String GET_VOICE_NUM = "/risk/user/voice/base/pre_commit";

    /**
     * 现金贷声纹采集接口
     */
    public static final String CASH_UPLOAD_VOICE = "/risk/user/voice/base/commit";

    /**
     * 获取现金贷验证类型
     */
    public static final String GET_CASH_VERIFY_TYPE = "/api/json/loan/cash/verify/type/get.json";
    /**
     * 设备心跳上报
     */
    public static final String DEVICE_HEART_BEAT = "/risk/user/hello_report";


    public static final String GET_PROVINCE = "/macaron/api/json/public/province";
    public static final String GET_CITY = "/macaron/api/json/public/city";
    public static final String GET_DISTRICT = "/macaron/api/json/public/district";
    public static final String GET_POSTCODE = "/macaron/api/json/public/postcode";

    /**
     * 现金贷 跳转到web 邮箱授权页
     */
    public final static String WEB_CASH_EMAIL_URL = "v2/id_cash_EMail_Authorized.html";

    /**
     * 跳转到现金贷页面
     */
    public final static String WEB_CASH_LOAN_URL = "v2/id_CashLoan.html";

    /**
     * 验证公司邮箱
     */
    public final static String VERIFY_COMPANY_EMAIL_URL = "/api/json/user/company/email/code.json";

    /**
     * 规则配置，比如个人邮箱列表等
     */
    public final static String CONFIG_REGEX_URL = "/json/public/regex.json";


}
