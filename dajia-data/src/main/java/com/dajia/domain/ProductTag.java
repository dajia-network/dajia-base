package com.dajia.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "product_tag")
public class ProductTag extends BaseModel {

	@Column(name = "tag_id")
	@Id
	@GeneratedValue
	public Long tagId;

	@Column(name = "tag_name")
	public String tagName;

}