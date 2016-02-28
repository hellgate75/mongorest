package com.service.mongodb.mongodbrest.rest.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.bson.Document;

@XmlRootElement
public class BookXML extends GenericXML {
	private String title = null;
	private String description = null;
	private String user = null;
	private String url = null;
	private List<String> tags = new ArrayList<String>();
	private Integer likes=0;
	public BookXML() {
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public Integer getLikes() {
		return likes;
	}
	public void setLikes(Integer likes) {
		this.likes = likes;
	}
	@SuppressWarnings("unchecked")
	public static BookXML fromDocument(Document d) {
		BookXML book = new BookXML();
		if (d.containsKey("_id"))
			try {
				book.setId(d.getObjectId("_id").toString());
			} catch (Exception e1) {
				book.setId(d.getString("_id"));
			}
		else
			book.setId(d.getString("id"));
		book.setTitle(d.getString("title"));
		book.setDescription(d.getString("description"));
		if (d.containsKey("user"))
			book.setUser(d.getString("user"));
		else if (d.containsKey("by_user"))
			book.setUser(d.getString("by_user"));
		book.setUrl(d.getString("url"));
		try {
			book.setLikes(d.getInteger("likes"));
		} catch (Exception e) {
			try {
				book.setLikes(new Integer(d.getDouble("likes").intValue()));
			} catch (Exception e2) {
				try {
					book.setLikes(Integer.parseInt(d.getString("likes")));
				} catch (Exception e3) {
					book.setLikes(0);
				}
			}
		}
		book.getTags().addAll((List<String>)d.get("tags"));
		return book;
	}

	@SuppressWarnings("unchecked")
	public static BookXML fromJSON(String json) {
		Document d = Document.parse(json);
		BookXML book = new BookXML();
		if (d.containsKey("_id"))
			try {
				book.setId(d.getObjectId("_id").toString());
			} catch (Exception e1) {
				book.setId(d.getString("_id"));
			}
		else
			book.setId(d.getString("id"));
		book.setTitle(d.getString("title"));
		book.setDescription(d.getString("description"));
		if (d.containsKey("user"))
			book.setUser(d.getString("user"));
		else if (d.containsKey("by_user"))
			book.setUser(d.getString("by_user"));
		book.setUrl(d.getString("url"));
		try {
			book.setLikes(d.getInteger("likes"));
		} catch (Exception e) {
			try {
				book.setLikes(new Integer(d.getDouble("likes").intValue()));
			} catch (Exception e2) {
				try {
					book.setLikes(Integer.parseInt(d.getString("likes")));
				} catch (Exception e3) {
					book.setLikes(0);
				}
			}
		}
		book.getTags().addAll((List<String>)d.get("tags"));
		return book;
	}
@Override
	public String toString() {
		return "BookXML [id=" + super.getId() + "title=" + title + ", description=" + description
				+ ", user=" + user + ", url=" + url + ", tags=" + tags
				+ ", likes=" + likes + "]";
	}
	
}
