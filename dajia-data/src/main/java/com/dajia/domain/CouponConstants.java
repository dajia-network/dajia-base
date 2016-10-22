package com.dajia.domain;

/**
 * Created by huhaonan on 2016/10/23.
 */
public interface CouponConstants {

    /**
     * 券的种类
     *
     * 1. 代金券 -- 抵扣订单部分价格
     * 2. 满减券 -- 订单金额达到一定数目才能使用
     * 3. 打折券 -- 给订单打折使用
     */
    int TYPE_MONEY = 1;

    int TYPE_FULL_RETURN = 2;

    int TYPE_CUT_OFF = 3;


    /**
     * 券的各种使用状态
     **/
    // 未使用
    int STATUS_ACTIVE = 1;

    // 某些其他原因导致不能使用
    int STATUS_INACTIVE = 0;

    // 已经使用
    int STATUS_USED  = 2;

    // 系统取消发放
    int STATUS_CANCELED = 3;

    // 过期未使用
    int STATUS_EXPIRED = 4;

    // 用户端放弃
    int STATUS_GIVEUP = 5;


    /**
     * 券的使用范围
     *
     * 1.直营
     * 2.店铺
     * 3.通用
     */
    int AREA_SELF = 1;

    int AREA_SHOP = 2;

    int AREA_ALL  = 3;

}
