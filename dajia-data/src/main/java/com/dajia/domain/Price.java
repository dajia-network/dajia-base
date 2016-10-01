package com.dajia.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * price & sold combination to define the formula of price change
 * 
 * @author Puffy
 */
@Entity
@JsonIgnoreProperties(value = { "productItem" })
@Table(name = "price")
public class Price extends BaseModel {

	@Column(name = "price_id")
	@Id
	@GeneratedValue
	public Long priceId;

	@Column(name = "sort")
	public int sort;

	@Column(name = "sold")
	public Long sold;

	@Column(name = "target_price")
	public BigDecimal targetPrice;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_item_id", referencedColumnName = "product_item_id")
	public ProductItem productItem;

	public Price clone() {
		Price clonePrice = new Price();
		clonePrice.sort = this.sort;
		clonePrice.sold = this.sold;
		clonePrice.targetPrice = this.targetPrice;
		return clonePrice;
	}
}