package com.dajia.domain;

import javax.persistence.*;

@Entity
@Table(name = "property")
public class Property extends BaseModel {

	@Column(name = "property_id")
	@Id
	@GeneratedValue
	public Long propertyId;

	@Column(name = "property_key", nullable = false)
	public String propertyKey;

	@Column(name = "property_value")
	public String propertyValue;

}