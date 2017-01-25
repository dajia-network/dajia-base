package com.dajia.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "user")
public class User extends BaseModel {

	@Column(name = "user_id")
	@Id
	@GeneratedValue
	public Long userId;

	@Column(name = "mobile")
	public String mobile;

	@Column(name = "password")
	public String password;

	@Column(name = "username")
	public String userName;

	@Column(name = "email")
	public String email;

	@Column(name = "sex")
	public String sex;

	@Column(name = "country")
	public String country;

	@Column(name = "province")
	public String province;

	@Column(name = "city")
	public String city;

	@Column(name = "head_img_url")
	public String headImgUrl;

	@Column(name = "oauth_type")
	public String oauthType;

	@Column(name = "oauth_user_id")
	public String oauthUserId;

	@Column(name = "ref_user_id")
	public Long refUserId;

	@Column(name = "is_admin")
	public String isAdmin;

	@Column(name = "is_sales")
	public String isSales;

	@Column(name = "last_visit_date")
	public Date lastVisitDate;

	@Column(name = "last_visit_ip")
	public String lastVisitIP;
	
	@Column(name = "source")
	public String source;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user", fetch = FetchType.LAZY)
	public List<UserContact> userContacts;

	public List<UserContact> getUserContacts() {
		List<UserContact> ucList = new ArrayList<UserContact>();
		for (UserContact uc : this.userContacts) {
			if ("Y".equalsIgnoreCase(uc.isActive)) {
				ucList.add(uc);
			}
		}
		return ucList;
	}
}