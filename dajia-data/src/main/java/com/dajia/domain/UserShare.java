package com.dajia.domain;

import javax.persistence.*;

/**
 * User will get 1 yuan refund every time the link he/she shared be clicked.
 * 
 * @author Puffy
 */
@Entity
@Table(name = "user_share")
public class UserShare extends BaseModel {
	@Column(name = "share_id")
	@Id
	@GeneratedValue
	public Long shareId;

	@Column(name = "user_id")
	public Long userId;

	@Column(name = "order_id")
	public Long orderId;

	@Column(name = "product_id")
	public Long productId;

	@Column(name = "product_item_id")
	public Long productItemId;

	@Column(name = "visit_user_id")
	public Long visitUserId;

	@Column(name = "visit_username")
	public String visitUserName;

	@Column(name = "visit_head_img_url")
	public String visitHeadImgUrl;

	@Column(name = "share_type")
	public Integer shareType;

}