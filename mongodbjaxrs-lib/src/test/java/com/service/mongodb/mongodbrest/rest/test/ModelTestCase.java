package com.service.mongodb.mongodbrest.rest.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.junit.Test;

import com.service.mongodb.mongodbrest.rest.model.AuthorXML;
import com.service.mongodb.mongodbrest.rest.model.BookXML;
import com.service.mongodb.mongodbrest.rest.model.ResponseBase;

public class ModelTestCase {
	private String jsonBookSample = "{\"id\":\"4c1a86bb2955000000004076\",\"description\":\"MongoDB is no sql database\",\"likes\":100,\"tags\":[\"mongodb\",\"database\",\"NoSQL\"],\"title\":\"MongoDB Overview\",\"url\":\"http://www.tutorialspoint.com\",\"user\":\"tutorials point site\"}";
	private String jsonAuthorSample = "{\"id\":\"56c9cf00d677708784fe79af\",\"age\":41,\"blog\":\"http://ie.linkedin.com/in/fabriziotorelli\",\"editor\":\"Fabrizio Editore Ltd\",\"name\":\"Fabrizio\",\"rate\":3.6,\"social\":\"http://www.facebook.com/fabrizio.torelli\",\"surname\":\"Torelli\",\"tags\":[\"Architecture\",\"Information Technology\",\"Object Oriented Architecture\",\"UML\"]}";
	private String jsonResponseSample = "{\"code\":404,\"message\":\"Service unavailable\"}";
	
	public ModelTestCase() {
        
	}
	
	@Test
	public void testBookXML() {
		System.out.println("Test BookXML created by a JSON String - checking attributes ...");
		BookXML myBook1 = BookXML.fromJSON(jsonBookSample);
		assertEquals("Book id must is null and the test is not relevant", myBook1.getId(), "4c1a86bb2955000000004076");
		assertEquals("Book descriptioon must is null and the test is not relevant", myBook1.getDescription(), "MongoDB is no sql database");
		assertEquals("Book title must is null and the test is not relevant", myBook1.getTitle(), "MongoDB Overview");
		List<String> tags1 = new ArrayList<String>(0);
		tags1.add("mongodb");
		tags1.add("database");
		tags1.add("NoSQL");
		assertEquals("Book tags must is null and the test is not relevant", myBook1.getTags(),tags1);
		assertEquals("Book likes must is null and the test is not relevant", myBook1.getLikes(), new Integer(100));
		assertEquals("Book url must is null and the test is not relevant", myBook1.getUrl(), "http://www.tutorialspoint.com");
		assertEquals("Book user must is null and the test is not relevant", myBook1.getUser(), "tutorials point site");
		System.out.println("Test BookXML created by a bson Document - checking attributes ...");
		BookXML myBook2 = BookXML.fromDocument(Document.parse(jsonBookSample));
		assertEquals("Book id must is null and the test is not relevant", myBook2.getId(), "4c1a86bb2955000000004076");
		assertEquals("Book descriptioon must is null and the test is not relevant", myBook2.getDescription(), "MongoDB is no sql database");
		assertEquals("Book title must is null and the test is not relevant", myBook2.getTitle(), "MongoDB Overview");
		List<String> tags2 = new ArrayList<String>(0);
		tags2.add("mongodb");
		tags2.add("database");
		tags2.add("NoSQL");
		assertEquals("Book tags must is null and the test is not relevant", myBook2.getTags(),tags2);
		assertEquals("Book likes must is null and the test is not relevant", myBook2.getLikes(), new Integer(100));
		assertEquals("Book url must is null and the test is not relevant", myBook2.getUrl(), "http://www.tutorialspoint.com");
		assertEquals("Book user must is null and the test is not relevant", myBook2.getUser(), "tutorials point site");
		
	}

	@Test
	public void testAuthorXML() {
		System.out.println("Test AuthorXML created by a JSON String - checking attributes ...");
		AuthorXML myAuthor1 = AuthorXML.fromJSON(jsonAuthorSample);
		assertEquals("Author id must is null and the test is not relevant", myAuthor1.getId(), "56c9cf00d677708784fe79af");
		assertEquals("Author age must is null and the test is not relevant", myAuthor1.getAge(), new Integer(41));
		assertEquals("Author blog must is null and the test is not relevant", myAuthor1.getBlog(), "http://ie.linkedin.com/in/fabriziotorelli");
		assertEquals("Author editor must is null and the test is not relevant", myAuthor1.getEditor(), "Fabrizio Editore Ltd");
		assertEquals("Author name must is null and the test is not relevant", myAuthor1.getName(), "Fabrizio");
		assertEquals("Author surname must is null and the test is not relevant", myAuthor1.getSurname(), "Torelli");
		assertEquals("Author social must is null and the test is not relevant", myAuthor1.getSocial(), "http://www.facebook.com/fabrizio.torelli");
		assertEquals("Author rate must is null and the test is not relevant", myAuthor1.getRate(), new Double(3.6));
		List<String> tags1 = new ArrayList<String>(0);
		tags1.add("Architecture");
		tags1.add("Information Technology");
		tags1.add("Object Oriented Architecture");
		tags1.add("UML");
		assertEquals("Author tags must is null and the test is not relevant", myAuthor1.getTags(),tags1);
		System.out.println("Test AuthorXML created by a bson Document - checking attributes ...");
		AuthorXML myAuthor2 = AuthorXML.fromDocument(Document.parse(jsonAuthorSample));
		assertEquals("Author id must is null and the test is not relevant", myAuthor2.getId(), "56c9cf00d677708784fe79af");
		assertEquals("Author age must is null and the test is not relevant", myAuthor2.getAge(), new Integer(41));
		assertEquals("Author blog must is null and the test is not relevant", myAuthor2.getBlog(), "http://ie.linkedin.com/in/fabriziotorelli");
		assertEquals("Author editor must is null and the test is not relevant", myAuthor2.getEditor(), "Fabrizio Editore Ltd");
		assertEquals("Author name must is null and the test is not relevant", myAuthor2.getName(), "Fabrizio");
		assertEquals("Author surname must is null and the test is not relevant", myAuthor2.getSurname(), "Torelli");
		assertEquals("Author social must is null and the test is not relevant", myAuthor2.getSocial(), "http://www.facebook.com/fabrizio.torelli");
		assertEquals("Author rate must is null and the test is not relevant", myAuthor2.getRate(), new Double(3.6));
		List<String> tags2 = new ArrayList<String>(0);
		tags2.add("Architecture");
		tags2.add("Information Technology");
		tags2.add("Object Oriented Architecture");
		tags2.add("UML");
		assertEquals("Author tags must is null and the test is not relevant", myAuthor2.getTags(),tags2);
		
		
	}

	@Test
	public void testResponseBase() {
		System.out.println("Test ResponseBase created by a JSON String - checking attributes ...");
		ResponseBase myResponse1 = ResponseBase.fromJSON(jsonResponseSample);
		assertEquals("Response code must is null and the test is not relevant", myResponse1.getCode(), new Long(404));
		assertEquals("Response message must is null and the test is not relevant", myResponse1.getMessage(),"Service unavailable");
		System.out.println("Test ResponseBase created by a bson Document - checking attributes ...");
		ResponseBase myResponse2 = ResponseBase.fromDocument(Document.parse(jsonResponseSample));
		assertEquals("Response code must is null and the test is not relevant", myResponse2.getCode(), new Long(404));
		assertEquals("Response message must is null and the test is not relevant", myResponse2.getMessage(),"Service unavailable");
		
		
	}

}
