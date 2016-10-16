package com.dajia.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.List;

@Entity
@JsonIgnoreProperties(value = { "productItems" })
@Table(name = "product")
public class Product extends BaseModel {

	@Column(name = "product_id")
	@Id
	@GeneratedValue
	public Long productId;

	@Column(name = "ref_id")
	public String refId;

	@Column(name = "short_name")
	public String shortName;

	@Column(name = "name", nullable = false)
	public String name;

	@Column(name = "brief")
	public String brief;

	@Column(name = "description")
	public String description;

	@Column(name = "spec")
	public String spec;

	@Column(name = "total_sold")
	public Long totalSold;

	@Transient
	public boolean isFav;

	@Column(name = "img_url_home")
	public String imgUrl;

	@Column(name = "img_url_list")
	public String imgUrl4List;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "product", fetch = FetchType.EAGER)
	public List<ProductImage> productImages;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "product", fetch = FetchType.EAGER)
	public List<ProductItem> productItems;
}