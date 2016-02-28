package com.service.mongodb.mongodbrest.rest.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.bson.Document;

@XmlRootElement
public class AuthorXML extends GenericXML {
	private String name = null;
	private String surname = null;
	private Integer age = 0;
	private String editor = null;
	private String blog = null;
	private String social = null;
	private List<String> tags = new ArrayList<String>();
	private Double rate=0d;
	public AuthorXML() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	public String getBlog() {
		return blog;
	}

	public void setBlog(String blog) {
		this.blog = blog;
	}

	public String getSocial() {
		return social;
	}

	public void setSocial(String social) {
		this.social = social;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	@SuppressWarnings("unchecked")
	public static AuthorXML fromDocument(Document d) {
		AuthorXML book = new AuthorXML();
		if (d.containsKey("_id"))
			try {
				book.setId(d.getObjectId("_id").toString());
			} catch (Exception e1) {
				book.setId(d.getString("_id"));
			}
		else
			book.setId(d.getString("id"));
		book.setName(d.getString("name"));
		book.setSurname(d.getString("surname"));
		try {
			book.setAge(d.getInteger("age"));
		} catch (Exception e) {
			book.setAge(new Integer(d.getDouble("age").intValue()));
		}
		book.setEditor(d.getString("editor"));
		book.setBlog(d.getString("blog"));
		book.setSocial(d.getString("social"));
		try {
			book.setRate(d.getDouble("rate"));
		} catch (Exception e) {
			book.setRate(new Double(d.getInteger("rate").doubleValue()));
		}
		book.getTags().addAll((List<String>)d.get("tags"));
		return book;
	}

	@SuppressWarnings("unchecked")
	public static AuthorXML fromJSON(String json) {
		Document d = Document.parse(json);
		AuthorXML book = new AuthorXML();
		if (d.containsKey("_id"))
			try {
				book.setId(d.getObjectId("_id").toString());
			} catch (Exception e1) {
				book.setId(d.getString("_id"));
			}
		else
			book.setId(d.getString("id"));
		book.setName(d.getString("name"));
		book.setSurname(d.getString("surname"));
		try {
			book.setAge(d.getInteger("age"));
		} catch (Exception e) {
			book.setAge(new Integer(d.getDouble("age").intValue()));
		}
		book.setEditor(d.getString("editor"));
		book.setBlog(d.getString("blog"));
		book.setSocial(d.getString("social"));
		try {
			book.setRate(d.getDouble("rate"));
		} catch (Exception e) {
			book.setRate(new Double(d.getInteger("rate").doubleValue()));
		}
		book.getTags().addAll((List<String>)d.get("tags"));
		return book;
	}

	@Override
	public String toString() {
		return "AuthorXML [id=" + super.getId() + " name=" + name + ", surname=" + surname + ", age="
				+ age + ", editor=" + editor + ", blog=" + blog + ", social="
				+ social + ", tags=" + tags + ", rate=" + rate + "]";
	}
	
}
