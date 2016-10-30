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

	@Override
	public String toString() {
		return "ProductVO{" +
				"productId=" + productId +
				", productItemId=" + productItemId +
				", refId='" + refId + '\'' +
				", shortName='" + shortName + '\'' +
				", name='" + name + '\'' +
				", brief='" + brief + '\'' +
				", description='" + description + '\'' +
				", spec='" + spec + '\'' +
				", sold=" + sold +
				", totalSold=" + totalSold +
				", stock=" + stock +
				", buyQuota=" + buyQuota +
				", productStatus=" + productStatus +
				", fixTop=" + fixTop +
				", isPromoted='" + isPromoted + '\'' +
				", originalPrice=" + originalPrice +
				", currentPrice=" + currentPrice +
				", postFee=" + postFee +
				", startDate=" + startDate +
				", expiredDate=" + expiredDate +
				", targetPrice=" + targetPrice +
				", priceOff=" + priceOff +
				", soldNeeded=" + soldNeeded +
				", progressValue=" + progressValue +
				", nextOff=" + nextOff +
				", isFav=" + isFav +
				", status4Show='" + status4Show + '\'' +
				", imgUrl='" + imgUrl + '\'' +
				", imgUrl4List='" + imgUrl4List + '\'' +
				", productImages=" + productImages +
				", prices=" + prices +
				'}';
	}
}