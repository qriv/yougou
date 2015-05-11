package com.gsh.app.client.mall.https;

/**
 * Created by taosj on 15/1/30.
 */
public final class Urls {

    private Urls() {
    }


    public static final String CODE_TOKEN_INVALID = "10011";//token失效
    public static final String CODE_CAPCHA_WRONG = "10012";//验证码错误
    public static final String CODE_PASSWORD_WRONG = "10013";//密码错误
    public static final String CODE_USER_EXIST = "10014";//用户已存在
    public static final String CODE_ADDRESS_EMPTY = "10015";//未添加收货地址

    //    public static final String BASE_URL = "http://192.168.0.189:8888/rest";
    public static final String BASE_URL = "http://ugou.imolin.cn/rest";//official
    //    public static final String BASE_URL = "http://42.121.81.33:9480/rest";//internal test
    //        public static final String BASE_URL = "http://192.168.0.101:8180/rest";//xu
    //    public static final String BASE_URL = "http://192.168.0.126:8082/rest";//song
//    public static final String IMAGE_PREFIX = "http://imaged.gangsh.com/";
    //    public static final String IMAGE_PREFIX = "http://imaget.gangsh.com/";
    public static final String IMAGE_PREFIX = "http://ugou.images.imolin.cn/";
    //    public static final String IMAGE_LOGO = "http://www.imolin.cn/app_logo.png";
    public static final String IMAGE_LOGO = "http://ugou.images.imolin.cn/logo.png";

    //    1.1 登录
    public static final String MEMBER_LOGIN = BASE_URL + "/member/login";
    //    1.2 获取注册验证码
    public static final String MEMBER_REGISTER_CAPTCHA = BASE_URL + "/member/register/captcha";
    //    1.3 用户注册
    public static final String MEMBER_REGISTER = BASE_URL + "/member/register";
    //1.4 修改账户信息
    public static final String MEMBER_PROFILE_UPDATE = BASE_URL + "/member/profile/update";
    //    1.5 获取修改密码验证码
    public static final String MEMBER_PASSWORD_CHANGE_CAPTCHA = BASE_URL + "/member/password/change/captcha";
    //    1.6 修改密码
    public static final String MEMBER_PASSWORD_CHANGE = BASE_URL + "/member/password/change";
    // 重置密码
    public static final String MEMBER_PASSWORD_FIND = BASE_URL + "/member/password/find";

    //  1.8 是否已经设置支付密码
    public static final String MEMBER_PAYMENT_PASSWORD_VALID = BASE_URL + "/member/paymentpassword/valid";

    //  1.10 获取陌邻余额
    public static final String MEMBER_WALLET_REMAIN_SUM = BASE_URL + "/member/wallet/remain/sum";
    //  1.11 获取陌邻余额详情
    public static final String MEMBER_WALLET_REMAINS = BASE_URL + "/member/wallet/remains";
    //  1.12 获取陌邻积分
    public static final String MEMBER_WALLET_CREDIT_SUM = BASE_URL + "/member/wallet/credit/sum";
    //  1.13 获取陌邻积分详情
    public static final String MEMBER_WALLET_CREDITS = BASE_URL + "/member/wallet/credits";
    //  1.14 获取陌邻代金券
    public static final String MEMBER_WALLET_COUPON_SUM = BASE_URL + "/member/wallet/coupon/sum";
    //  1.15 获取陌邻代金券详情
    public static final String MEMBER_WALLET_COUPONS = BASE_URL + "/member/wallet/coupons";

    // 1.16 获取地址信息列表
    public static final String MEMBER_ADDRESS = BASE_URL + "/member/address";
    // 1.17 添加地址
    public static final String MEMBER_ADDRESS_CREATE = BASE_URL + "/member/address/create";
    // 1.18 修改地址
    public static final String MEMBER_ADDRESS_CHANGE = BASE_URL + "/member/address/change";

    // 1.19 查询用户信息
    public static final String MEMBER_OBTAIN = BASE_URL + "/member/obtain";
    //1.20 更换手机号验证码
    public static final String MEMBER_MOBILE_CHANGE__CAPTCHA = BASE_URL + "/member/mobile/change/captcha";
    //    1.21 修改手机号码
    public static final String MEMBER_MOBILE_CHANGE = BASE_URL + "/member/mobile/change";

    //1.22 获取地址信息
    public static final String MEMBER_ADDRESS_ITEM = BASE_URL + "/member/address/item";
    //1.23 删除地址信息
    public static final String MEMBER_ADDRESS_DELETE = BASE_URL + "/member/address/delete";
    //1.24 设置默认地址
    public static final String MEMBER_ADDRESS_DEFAULT = BASE_URL + "/member/address/default";

    // 1.27关闭支付密码
    public static final String MEMBER_PAYMENT_PASSWORD_CLOSE = BASE_URL + "/member/paymentpassword/off";
    // 1.28启用支付密码
    public static final String MEMBER_PAYMENT_PASSWORD_ON = BASE_URL + "/member/paymentpassword/on";
    //  1.9 修改支付密码
    public static final String MEMBER_PAYMENT_PASSWORD_CHANGE = BASE_URL + "/member/paymentpassword/change";

    //    2.1社区列表
    public static final String COMMUNITY_LIST = BASE_URL + "/communities";
    //    2.2选择社区
    public static final String COMMUNITY_CHANGE = BASE_URL + "/member/community/change";
    //  2.3获取社区信息
    public static final String COMMUNITIES = BASE_URL + "/community";
    //  2.4获取服务站所属社区信息
    public static final String MEMBER_STATION_COMMUNITIES = BASE_URL + "/member/station/communities";
    //    3.1 获取商品类别
    public static final String CATEGORY = BASE_URL + "/goods/categories";
    //    3.2 根据类别获取商品列表
    public static final String GOODS_CATEGORY = BASE_URL + "/goods";
    //    3.3 商品搜索
    public static final String GOODS_SEARCH = BASE_URL + "/goods/search";
    //  3.4 商品评论
    public static final String GOODS_COMMENT_LIST = BASE_URL + "/goods/comments";
    //  3.5根据商品二维码获取商品详情
    public static final String GOODS_QR_CODE = BASE_URL + "/goods/obtain/barcode";
    //  3.6 商品图文详情
    public static final String GOODS_PICTURE = BASE_URL + "/goods/details";
    //  3.7 商品参数
    public static final String GOODS_PARAMETER = BASE_URL + "/goods/params";
    //  3.8 商品售后服务
    public static final String GOODS_SERVICE = BASE_URL + "/goods/sale/after-service-text";
    //  3.9 获取收藏商品信息
    public static final String MEMBER_FAVORITES = BASE_URL + "/member/favorites";
    //  3.10 删除收藏商品信息
    public static final String MEMBER_FAVORITE_DELETE = BASE_URL + "/member/favorite/delete";
    //  3.11 添加收藏商品信息
    public static final String MEMBER_FAVORITE_ADD = BASE_URL + "/member/favorite/add";
    //  3.12获取购物车商品信息
    public static final String MEMBER_CART_GOODS = BASE_URL + "/member/cart/goods";
    //  3.13删除购物车商品信息
    public static final String MEMBER_CART_GOODS_DELETE = BASE_URL + "/member/cart/goods/delete";
    //  3.14添加购物车商品信息
    public static final String MEMBER_CART_GOODS_ADD = BASE_URL + "/member/cart/goods/add";
    //  3.15从购物车把商品移到收藏夹
    public static final String MEMBER_CART_MOVE_FAVORITE = BASE_URL + "/member/cart/move/favorite";
    //  3.16购物车修改数量
    public static final String MEMBER_CART_GOODS_CHANGE = BASE_URL + "/member/cart/goods/change";
    //    3.17 获取限时特惠
    public static final String GOODS_PANIC_BUYING = BASE_URL + "/goods/special/preferences";
    //    3.18 获取预售列表
    public static final String GOODS_PRESALE = BASE_URL + "/goods/presales";
    //    3.19 根据id获取商品
    public static final String GOODS_ITEM = BASE_URL + "/goods/item";
    //    3.20 是否收藏该商品
    public static final String GOODS_IS_COLLECT = BASE_URL + "/goods/ismyfavorite";
    //    3.21 购物车商品数量
    public static final String SHOPPING_CAR_COUNT = BASE_URL + "/member/cart/goods/count";
    //  3.22获取购物车商品数
    public static final String MEMBER_FAVORITE_MOVE_CART = "/member/favorite/move/cart";
    // 评价商品
    public static final String GOODS_COMMENT = BASE_URL + "/goods/comment";
    //    4.1 创建订单
    public static final String ORDER_CREATE = BASE_URL + "/member/order/create";
    //    4.2 提交订单
    public static final String ORDER_SUBMIT = BASE_URL + "/member/order/submit";
    //    4.3 通过陌邻余额确认支付
    public static final String MEMBER_ORDER_PAYMENT_REMAIN = BASE_URL + "/member/order/payment/remain";
    //    4.5 获取我的订单
    public static final String ORDER_LIST = BASE_URL + "/member/orders";
    //    4.6 删除订单
    public static final String MEMBER_ORDER_DELETE = BASE_URL + "/member/order/delete";
    //    4.7 取消订单
    public static final String ORDER_CANCEL = BASE_URL + "/member/order/cancel";
    //    4.8 再次购买
    public static final String ORDER_REBUY = BASE_URL + "/member/order/rebuy";
    //    4.9 订单可用代金券和积分
    public static final String ORDER_COUPON = BASE_URL + "/member/order/coupon";
    //   4.10 退货 add
    public static final String ORDER_RETURN = BASE_URL + "/member/order/return";
    //   4.11 配送信息 add
    public static final String ORDER_DELIVER = BASE_URL + "/member/order/return";
    // 4.12 兑换代金券
    public static final String COUPON_EXCHANGE = BASE_URL + "/member/order/coupon/exchange";
    //    5.1首页信息
    public static final String HOME = BASE_URL + "/home";
    //    5.2 消息列表 unused
    public static final String MEMBER_MESSAGES = BASE_URL + "/member/messages";
    //    5.3 陌邻头条
    public static final String MEMBER_MESSAGE_DETAIL = BASE_URL + "/member/message/detail";
    // 5.4 获取充值流水号(订单号)
    public static final String PAY_PREPAID = BASE_URL + "/pay/prepaid";
    // 5.5 支付宝充值key和value
    public static final String PAY_ALI = BASE_URL + "/pay/ali";
    //test 陌邻充值
    public static final String PAY_TEST = BASE_URL + "/pay/test";

    //6.1 获取套包列表
    public static final String EXTERN_PACKAGE_LIST = BASE_URL + "/combo/list";
    //6.2 获取套包详情
    public static final String EXTERN_PACKAGE = BASE_URL + "/extern/package";
    //6.3 发现列表
    public static final String EXTERN_DISCOVERY_LIST = BASE_URL + "/extern/discovery/list";
    //6.4发现点赞
    public static final String EXTERN_DISCOVERY_LIKE = BASE_URL + "/discovery/like";
    //6.6套包时间轴列表
    public static final String EXTERN_DELIVER_TIMELINE_LIST = BASE_URL + "/extern/deliver/timeline/list";
    //6.7套包时间轴详情
    public static final String EXTERN_DELIVER_TIMELINE = BASE_URL + "/extern/deliver/timeline";
    //6.8套包节点配送延后
    public static final String EXTERN_DELIVER_TIMELINE_DELAY = BASE_URL + "/extern/deliver/timeline/delay";
    //6.9套包节点配送恢复
    public static final String EXTERN_DELIVER_TIMELINE_RECOVERY = BASE_URL + "/extern/deliver/timeline/recovery";
    //6.10 是否在服务范围内
    public static final String EXTERN_ONSERVICE = BASE_URL + "/extern/onService";
    //6.12 套包评论列表
    public static final String EXTERN_COMMENT_LIST = BASE_URL + "/extern/comment/list";
    //6.13 发表套包评论
    public static final String EXTERN_COMMENT = BASE_URL + "/extern/comment";
    //6.14 相关套餐
    public static final String EXTERN_PACKAGE_RELATED = BASE_URL + "/extern/package/related";
    //6.15创建套餐订单
    public static final String EXTERN_DELIVER_ORDER_CREATE = BASE_URL + "/extern/deliver/order/create";//输入：packageId 输出：M60，address为空code设为10015
    //6.16提交套餐订单
    public static final String EXTERN_DELIVER_ORDER_CONFIRM = BASE_URL + "/combo/order/create";
    //6.17 浏览发现详情
//    public static final String DISCOVERY_REVIEW = BASE_URL + "/discovery/review";
    //6.18 分享详情
//    public static final String DISCOVERY_SHARE = BASE_URL + "/discovery/share";


    public static final String COMBO_ORDER_CREATE = BASE_URL + "/combo/order/create";

    public static final String COMBO_ORDER_LIST = BASE_URL + "/combo/order/list";

    public static final String COMBO_ORDER_DETAILS = BASE_URL + "/combo/order/details";

    public static final String COMBO_ORDER_DELIVERY_SIGN = BASE_URL + "/combo/order/delivery/sign";

    public static final String COMBO_DETAILS = BASE_URL + "/combo/details";

    public static final String COMBO_VOTE = BASE_URL + "/combo/vote";

    public static final String COMBO_COMMENT_LIST = BASE_URL + "/combo/comment/list";

    public static final String DISCOVERY_LIST = BASE_URL + "/discovery/list";

    public static final String DISCOVERY_DETAILS = BASE_URL + "/discovery/details";//discoveryId

    public static final String DISCOVERY_LIKE = BASE_URL + "/discovery/like";//discoveryId isLike

    public static final String DISCOVERY_REVIEW = BASE_URL + "/discovery/review";//discoveryId

    public static final String DISCOVERY_SHARE = BASE_URL + "/discovery/share";//discoveryId

    public static final String COMBO_LIST = BASE_URL + "/combo/list";

}
