package com.dajia.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "product", fetch = FetchType.LAZY)
	public List<ProductImage> productImages;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "product", fetch = FetchType.LAZY)
	public List<ProductItem> productItems;

	@ManyToMany(targetEntity = ProductTag.class, cascade = { CascadeType.ALL })
	@JoinTable(name = "product_tag_link", joinColumns = { @JoinColumn(name = "product_id") }, inverseJoinColumns = { @JoinColumn(name = "tag_id") })
	public List<ProductTag> tags;
}