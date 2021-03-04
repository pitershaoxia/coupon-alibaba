package com.simple.coupon.common.constant;

/**
 * @Auther Piter_Liu
 * @Description <h1>常量定义</h1>
 * @Date 2020/8/4
 */
public class CacheConstant {

    /**
     * kafka 消息的topic
     */
    public static final String TOPIC = "simple_user_coupon_op";

    // Redis key 前缀定义  coupon_template_code: 其实已经分组了，后面那串是多余了

    /** 优惠券码 key 前缀 */
    public static final String RedisPrefix_COUPON_TEMPLATE = "coupon_template_code:";

    /** 用户当前所有可用的优惠券 key 前缀 */
    public static final String RedisPrefix_USER_COUPON_USEABLE = "user_coupon_usable:";

    /** 用户当前所有已使用的优惠券 key 前缀 */
    public static final String RedisPrefix_USER_COUPON_USED = "user_coupon_used:";

    /** 用户当前所有已过期的优惠券 key 前缀 */
    public static final String RedisPrefix_USER_COUPON_EXPIRED = "user_coupon_expired:";


}
