package com.dajia.domain;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.Date;

import static com.dajia.domain.CouponConstants.*;

/**
 * 优惠券
 *
 * Created by huhaonan on 2016/10/20.
 */
@Entity
public class Coupon extends BaseModel {

    @Column(name = "id")
    @Id
    @GeneratedValue
    public Long id;

    /**
     * 券的名称
     */
    @Column(nullable = false)
    public String name;

    /**
     * 券的说明 这是内部人员查看的时候显示的简介 不会显示在优惠券界面上
     *
     */
    public String comment;

    /**
     * 面额
     */
    public Integer value;

    /**
     * 数量
     */
    public Long amount;

    /**
     * 剩余数量
     */
    public Long remain;

    /**
     * 1. 代金券 -- 抵扣订单部分价格
     * 2. 满减券 -- 订单金额达到一定数目才能使用
     * 3. 打折券 -- 给订单打折使用
     */
    public Integer type;

    /**
     * 可使用范围
     *
     * 1.直营 2.店铺 3.通用
     */
    public Integer area;

    /**
     * 来源 商家ID 默认是1 即打价网
     */
    @Column(name = "sourceId")
    public Long sourceId = 1L;

    /**
     * 状态
     *
     * {@See com.dajia.domain.CouponConstants }
     */
    @Column(name = "status")
    public Integer status = STATUS_ACTIVE;

    /**
     * 规则简述
     *
     * 显示在优惠券的界面上 例如"全场通用" "满199元使用"
     */
    @Column(name = "rule_desc")
    public String ruleDesc;

    /**
     * 过期时间
     */
    @Column(name = "gmt_expired")
    public Date gmtExpired;

    /**
     * 可以使用的最早时间
     */
    @Column(name = "gmt_start")
    public Date gmtStart;

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

    @Transient
    public String displayInfo;

    public Coupon() {}

    /**
     *
     * @param name
     * @param comment
     * @param value
     * @param type
     * @param area
     * @param ruleDesc
     * @param gmtStart
     * @param gmtExpired
     * @param createdBy
     */
    public Coupon(String name, String comment, Integer value, Integer type, Integer area, String ruleDesc, Date gmtStart, Date gmtExpired, String createdBy) {
        this.name = name;
        this.comment = comment;
        this.value = value;
        this.type = type;
        this.area = area;
        this.ruleDesc = ruleDesc;
        this.gmtStart = gmtStart;
        this.gmtExpired = gmtExpired;
        this.createdBy = createdBy;
        this.modifiedBy = createdBy;
    }

    @PostLoad
    public void setDisplayInfo() {
        this.displayInfo = String.format("%d元%s%s", value,
                StringUtils.trimToEmpty((String) LABEL_AREA.get(area)),
                StringUtils.trimToEmpty((String) LABEL_TYPE.get(type)));
    }

    @Override
    public String toString() {
        return "Coupon{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                ", value=" + value +
                ", amount=" + amount +
                ", remain=" + remain +
                ", type=" + type +
                ", area=" + area +
                ", sourceId=" + sourceId +
                ", status=" + status +
                ", gmtExpired=" + gmtExpired +
                ", gmtStart=" + gmtStart +
                ", createdBy='" + createdBy + '\'' +
                ", modifiedBy='" + modifiedBy + '\'' +
                '}';
    }

    /**
     * 券的状态是否正常
     *
     * @return
     */
    public final boolean isActive () {
        return status == STATUS_ACTIVE;
    }

}
