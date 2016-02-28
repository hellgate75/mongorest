package com.service.mongodb.mongodbrest.rest.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GenericXML {
	private String id = "";
	public GenericXML() {
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

}
