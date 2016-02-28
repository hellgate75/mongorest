package com.service.mongodb.mongodbrest.rest.model;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.sun.jersey.api.NotFoundException;

public class ModelInterceptor {

	private ModelInterceptor() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public GenericXML getModelByCollection(String modelName, Document document) throws NotFoundException {
		if ("books".equals(modelName)) {
			return BookXML.fromDocument(document);
		}
		else if ("authors".equals(modelName)) {
			return AuthorXML.fromDocument(document);
		}
		throw new NotFoundException("");
	}

	public List<? extends GenericXML> getModelByCollection(String modelName, List<Document> documents) throws NotFoundException {
		if ("books".equals(modelName)) {
			List<BookXML> books = new ArrayList<BookXML>(0);
			for(Document document: documents)
				books.add((BookXML)this.getModelByCollection(modelName, document));
			return books;
		}
		else if ("authors".equals(modelName)) {
			List<AuthorXML> authors = new ArrayList<AuthorXML>(0);
			for(Document document: documents)
				authors.add((AuthorXML)this.getModelByCollection(modelName, document));
			return authors;
		}
		throw new NotFoundException("");
	}
	private static ModelInterceptor instance = null;
	
	public static synchronized ModelInterceptor getInstance() {
		if (instance==null) {
			instance = new ModelInterceptor();
		}
		return instance;
	}

}
