package com.dajia.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

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
     * 券的说明
     */
    public String desc;

    /**
     * 面额
     */
    public int value;

    /**
     * 数量
     */
    public Long amount = -1L;

    /**
     * 剩余数量
     */
    public Long remain = -1L;

    /**
     * 类型
     */
    public int type;

    /**
     * 状态
     */
    public int status;

    /**
     * 过期时间
     */
    @Column(name = "gmt_expired")
    public Date gmtExpired;

    @Column(name = "created_by")
    public String createdBy;

    @Column(name = "modified_by")
    public String modifiedBy;

    @Override
    public String toString() {
        return "Coupon{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", value=" + value +
                ", amount=" + amount +
                ", remain=" + remain +
                ", type=" + type +
                ", status=" + status +
                ", gmtExpired=" + gmtExpired +
                ", createdDate=" + createdDate +
                ", modifiedDate=" + modifiedDate +
                ", createdBy='" + createdBy + '\'' +
                ", modifiedBy='" + modifiedBy + '\'' +
                '}';
    }

    /**
     * 券的状态定义
     */
    // 正常状态
    public final static int STATUS_ACTIVE  = 5;
    // 券已经过期
    public final static int STATUS_EXPIRED = 4;
    // 券已经作废
    public final static int STATUS_CANCELED  = 3;


    /**
     * 券的状态是否正常
     *
     * @return
     */
    public final boolean isActive () {
        return status >= STATUS_ACTIVE;
    }


}
