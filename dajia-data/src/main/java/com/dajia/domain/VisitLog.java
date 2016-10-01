package com.dajia.domain;

import javax.persistence.*;

@Entity
@Table(name = "user_visit_log")
public class VisitLog extends BaseModel {
	@Column(name = "visit_log_id")
	@Id
	@GeneratedValue
	public Long visitLogId;

	@Column(name = "user_id")
	public Long userId;

	@Column(name = "ref_user_id")
	public Long refUserId;

	@Column(name = "product_id")
	public String productId;
	
	@Column(name = "product_item_id")
	public String productItemId;

	@Column(name = "visit_url")
	public String visitUrl;

	@Column(name = "ref_url")
	public String refUrl;

	@Column(name = "log_type")
	public Integer logType;

	@Column(name = "visit_ip")
	public String visitIp;

}