package com.dajia.vo;

public class ExportConfig {

	public String resource;

	public Long recordLimit;

	public ExportConfig(String resource) {
		this.resource = resource;
		this.recordLimit = -1L;
	}
}
