package com.dajia.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@JsonIgnoreProperties(value = { "user" })
@Table(name = "user_contact")
public class UserContact extends BaseModel {

	@Column(name = "user_contact_id")
	@Id
	@GeneratedValue
	public Long contactId;

	@Column(name = "contact_name", nullable = false)
	public String contactName;

	@Column(name = "contact_mobile", nullable = false)
	public String contactMobile;

	@Column(name = "zipcode")
	public String zipcode;

	@Column(name = "is_default")
	public String isDefault;

	@Column(name = "address_1")
	public String address1;

	@Column(name = "address_2")
	public String address2;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "province", referencedColumnName = "id")
	public Location province;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "city", referencedColumnName = "id")
	public Location city;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "district", referencedColumnName = "id")
	public Location district;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", referencedColumnName = "user_id")
	public User user;
}