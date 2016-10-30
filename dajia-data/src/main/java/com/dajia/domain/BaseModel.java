package com.dajia.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
public class BaseModel implements Serializable {

	@Column(name = "created_date")
	public Date createdDate;

	@Column(name = "modified_date")
	public Date modifiedDate;

	@Column(name = "is_active")
	public String isActive;

	@PrePersist
	public void beforeCreation() {
		this.isActive = "Y";
		this.createdDate = new Date();
	}

	@PreUpdate
	public void beforeModification() {
		this.modifiedDate = new Date();
	}
}