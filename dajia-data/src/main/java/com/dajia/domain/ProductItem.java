package com.dajia.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
// @JsonIgnoreProperties(value = { "product" })
@Table(name = "product_item")
@NamedEntityGraph(name = "ProductItem.parent",
attributeNodes = @NamedAttributeNode("product"))
public class ProductItem extends BaseModel {

	@Column(name = "product_item_id")
	@Id
	@GeneratedValue
	public Long productItemId;

	@Column(name = "sold")
	public Long sold;

	@Column(name = "stock")
	public Long stock;

	@Column(name = "buy_quota")
	public Integer buyQuota;

	@Column(name = "product_status")
	public Integer productStatus;

	@Column(name = "fix_top")
	public Integer fixTop;

	@Column(name = "is_promoted")
	public String isPromoted;

	@Column(name = "original_price")
	public BigDecimal originalPrice;

	@Column(name = "current_price")
	public BigDecimal currentPrice;

	@Column(name = "post_fee")
	public BigDecimal postFee;

	@Column(name = "start_date")
	public Date startDate;

	@Column(name = "expired_date")
	public Date expiredDate;

	@Transient
	public BigDecimal targetPrice;

	@Transient
	public BigDecimal priceOff;

	@Transient
	public long soldNeeded;

	@Transient
	public long progressValue;

	@Transient
	public BigDecimal nextOff;

	@Transient
	public String status4Show;

	@Transient
	public Long realSold;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "productItem", fetch = FetchType.EAGER)
	public List<Price> prices;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "product_id", referencedColumnName = "product_id")
	public Product product;

}