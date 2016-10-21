package com.dajia.domain;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 给用户发放的优惠券
 *
 * Created by huhaonan on 2016/10/20.
 */
public class UserCoupon extends BaseModel {

    @Column(name = "id")
    @Id
    @GeneratedValue
    public Long id;

    /**
     * 用户
     */
    @Column(name = "user_id")
    public Long userId;

    /**
     * 券
     */
    @Column(name = "coupon_id")
    public Long couponId;

    /**
     * 过期时间 long类型 冗余字段 用来做快速索引
     */
    @Column(name = "gmt_expired")
    public Long gmtExpired;

    /**
     * 所属订单 如果已经使用
     */
    @Column(name = "order_id")
    public Long orderId;

    /**
     * 状态
     */
    public int status;

    /**
     * 金额 冗余字段
     */
    public int value;

    /**
     * 券的简要说明 冗余字段
     */
    @Column(name = "info")
    public String info;

    /**
     * 创建人
     */
    @Column(name = "created_by")
    public String createdBy;

    /**
     * 修改人
     */
    @Column(name = "modified_by")
    public String modifiedBy;

    /**
     * 备注信息
     */
    public String desc;

    /** 券的各种使用状态 **/
    // 未使用
    public final static int STATUS_NOT_USED = 0;
    // 已经使用
    public final static int STATUS_USED  = 1;
    // 系统取消发放
    public final static int STATUS_CANCELED = 2;
    // 过期未使用
    public final static int STATUS_EXPIRED = 3;
    // 用户端放弃
    public final static int STATUS_GIVEUP = 4;

    public final boolean canUse () {
        return status == STATUS_NOT_USED;
    }

    public final boolean isUsed () {
        return status == STATUS_USED;
    }

    /**
     * 是否过期
     *
     * @return
     */
    public final boolean isExpired () {
        return gmtExpired > System.currentTimeMillis();
    }


    public UserCoupon() {

    }

    public UserCoupon(Coupon coupon, Long userId, Long orderId, String createdBy) {
        this.userId = userId;
        this.orderId = null;
        this.copy(coupon);
        this.createdBy = createdBy;
        this.modifiedBy = createdBy;
        this.status = STATUS_NOT_USED;
    }

    private void copy(Coupon coupon) {
        this.value = coupon.value;
        this.gmtExpired = coupon.gmtExpired.getTime();
        this.couponId = coupon.id;
        this.info = coupon.name;
    }
}
