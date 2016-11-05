package com.dajia.domain;

import static com.dajia.domain.CouponConstants.LABEL_AREA;
import static com.dajia.domain.CouponConstants.LABEL_TYPE;
import static com.dajia.domain.CouponConstants.STATUS_ACTIVE;
import static com.dajia.domain.CouponConstants.STATUS_USED;

import java.text.DecimalFormat;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

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

    /**
     * 开始时间
     */
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
        this.couponId = coupon.id;

        this.type = coupon.type;
        this.area = coupon.area;
        this.ruleDesc = coupon.ruleDesc;

        this.setStartAndExpireTime(coupon);

        this.comment = StringUtils.trimToEmpty(coupon.name) + "_" + StringUtils.trimToEmpty(coupon.comment);

        if (this.comment.length() == 1) {
            this.comment = null;
        }
    }

    /**
     * 计算用户手上优惠券的有效时间
     *
     * 由于UserCoupon的构造函数是在领券(发券)的时候调用
     * 所以有效时间设置规则如下
     *
     *  1. 如果coupon的gmtStart和gmtExpired不是Null 那么直接拷贝过来
     *  2. 否则把开始时间设置为现在, 过期时间按照expiredDays来计算
     *
     * @param coupon
     */
    private void setStartAndExpireTime(Coupon coupon) {
        if (null != coupon.gmtStart && null != coupon.gmtExpired) {
            this.gmtStart = coupon.gmtStart.getTime();
            this.gmtExpired = coupon.gmtExpired.getTime();
        } else {
            this.gmtStart = System.currentTimeMillis();
            // 过期时间为 当前时间 + N天的23:59:59
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, coupon.expiredDays);
            c.set(Calendar.HOUR, 23);
            c.set(Calendar.MINUTE, 59);
            c.set(Calendar.SECOND, 59);
            this.gmtExpired = c.getTimeInMillis();
        }
    }

    @PostLoad
    public void setDisplayInfo() {
    	double amtOff = (double) value / 100;
    	DecimalFormat df = new DecimalFormat("#.00");
    	String couponVal = df.format(amtOff);
    	this.displayInfo = String.format("%s元%s%s", couponVal, StringUtils.trimToEmpty((String) LABEL_AREA.get(area)),
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
