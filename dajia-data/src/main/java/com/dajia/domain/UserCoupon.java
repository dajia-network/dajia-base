package com.dajia.domain;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import static com.dajia.domain.CouponConstants.*;
import java.util.Date;

/**
 * 给用户发放的优惠券
 *
 * Created by huhaonan on 2016/10/20.
 */

@Entity
@Table(name = "user_coupon")
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
     * 所属订单 如果已经使用
     */
    @Column(name = "order_id")
    public Long orderId;

    /**
     * 状态
     */
    public Integer status;

    /**
     * 金额 冗余字段
     */
    public Integer value;

    /**
     * 券的类型
     *
     * 1. 代金券 -- 抵扣订单部分价格
     * 2. 满减券 -- 订单金额达到一定数目才能使用
     * 3. 打折券 -- 给订单打折使用
     */
    @Column(name = "type")
    public Integer type;

    /**
     * 可使用范围
     *
     * 1.直营 2.店铺 3.通用
     */
    @Column(name = "area")
    public Integer area;

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
     * 使用规则的简要说明 显示在券的界面上
     * 比如 "满199元使用"
     */
    @Column(name = "rule_desc")
    public String ruleDesc;

    /**
     * 这条优惠券的备注信息 后台可见 前台不可见
     */
    @Column(name = "comment")
    public String comment;

    /**
     * 过期时间 long类型 冗余字段 用来做快速索引
     */
    @Column(name = "gmt_expired")
    public Long gmtExpired;

    @Column(name = "gmt_start")
    public Long gmtStart;

    /**
     * 显示在前端的优惠券信息
     */
    @Transient
    public String displayInfo;


    public final boolean canUse () {
        return status == STATUS_ACTIVE;
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
        this.orderId = orderId;
        this.copy(coupon);
        this.createdBy = createdBy;
        this.modifiedBy = createdBy;
        this.status = STATUS_ACTIVE;
    }

    /**
     * 从券的信息复制到用户券
     *
     * @param coupon
     */
    private void copy(Coupon coupon) {
        this.value = coupon.value;

        // 如果没有设置过期时间 那么设置为过期
        if (null != this.gmtExpired) {
            this.gmtExpired = coupon.gmtExpired.getTime();
        } else {
            this.status = STATUS_EXPIRED;
            this.gmtExpired = new Date().getTime();
        }

        this.couponId = coupon.id;

        if (null != coupon.gmtStart) {
            this.gmtStart = coupon.gmtStart.getTime();
        } else {
            this.gmtStart = new Date().getTime();
        }

        this.type = coupon.type;
        this.area = coupon.area;
        this.ruleDesc = coupon.ruleDesc;

        this.comment = StringUtils.trimToEmpty(coupon.name) + "_" + StringUtils.trimToEmpty(coupon.comment);

        if (this.comment.length() == 1) {
            this.comment = null;
        }
    }

    @PostLoad
    public void setDisplayInfo() {
        this.displayInfo = String.format("%d元%s%s", value,
                StringUtils.trimToEmpty((String) LABEL_AREA.get(area)),
                StringUtils.trimToEmpty((String) LABEL_TYPE.get(type)));
    }

    @Override
    public String toString() {
        return "UserCoupon{" +
                "id=" + id +
                ", userId=" + userId +
                ", couponId=" + couponId +
                ", orderId=" + orderId +
                ", status=" + status +
                ", value=" + value +
                ", type=" + type +
                ", area=" + area +
                ", createdBy='" + createdBy + '\'' +
                ", modifiedBy='" + modifiedBy + '\'' +
                ", ruleDesc='" + ruleDesc + '\'' +
                ", comment='" + comment + '\'' +
                ", gmtExpired=" + gmtExpired +
                ", gmtStart=" + gmtStart +
                '}';
    }
}
