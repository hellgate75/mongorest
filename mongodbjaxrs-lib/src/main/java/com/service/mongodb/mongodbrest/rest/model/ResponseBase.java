package com.service.mongodb.mongodbrest.rest.model;

import javax.xml.bind.annotation.XmlRootElement;

import org.bson.Document;

@XmlRootElement
public class ResponseBase {
	private Long code;
	private String message;

	public ResponseBase() {
	}

	public ResponseBase(long code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public Long getCode() {
		return code;
	}

	public void setCode(Long code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public static ResponseBase fromDocument(Document d) {
		ResponseBase response = new ResponseBase();
		try {
			response.setCode(d.getLong("code"));
		} catch (Exception e) {
			response.setCode(new Long(d.getInteger("code").intValue()));
		}
		response.setMessage(d.getString("message"));
		return response;
	}

	public static ResponseBase fromJSON(String json) {
		Document d = Document.parse(json);
		ResponseBase response = new ResponseBase();
		try {
			response.setCode(d.getLong("code"));
		} catch (Exception e) {
			response.setCode(new Long(d.getInteger("code").intValue()));
		}
		response.setMessage(d.getString("message"));
		return response;
	}

}
