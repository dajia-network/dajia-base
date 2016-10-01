package com.dajia.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.dajia.domain.Price;
import com.dajia.domain.ProductImage;

public class ProductVO {

	public Long productId;

	public Long productItemId;

	public String refId;

	public String shortName;

	public String name;

	public String brief;

	public String description;

	public String spec;

	public Long sold;

	public Long totalSold;

	public Long stock;

	public Integer buyQuota;

	public Integer productStatus;

	public Integer fixTop;

	public String isPromoted;

	public BigDecimal originalPrice;

	public BigDecimal currentPrice;

	public BigDecimal postFee;

	public Date startDate;

	public Date expiredDate;

	public BigDecimal targetPrice;

	public BigDecimal priceOff;

	public long soldNeeded;

	public long progressValue;

	public BigDecimal nextOff;

	public boolean isFav;

	public String status4Show;

	public String imgUrl;

	public String imgUrl4List;

	public List<ProductImage> productImages;

	public List<Price> prices;

}