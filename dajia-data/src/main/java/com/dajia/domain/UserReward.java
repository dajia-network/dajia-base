package com.dajia.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.Date;

/**
 * User will get reward when products sold thru the links he/she shared.
 * 
 * @author Puffy
 */
@Entity
@Table(name = "user_reward")
public class UserReward extends BaseModel {

	@Column(name = "reward_id")
	@Id
	@GeneratedValue
	public Long rewardId;

	@Column(name = "ref_user_id")
	public Long refUserId;

	@Column(name = "ref_order_id")
	public Long refOrderId;

	@Column(name = "product_id")
	public Long productId;

	@Column(name = "product_item_id")
	public Long productItemId;

	@Column(name = "order_id")
	public Long orderId;

	@Column(name = "order_user_id")
	public Long orderUserId;

	@Column(name = "reward_status")
	public Integer rewardStatus;

	@Column(name = "reward_ratio")
	public Integer rewardRatio;

	@Column(name = "reward_date")
	public Date rewardDate;

	@Column(name = "expired_date")
	public Date expiredDate;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}