 
package com.service.mongodb.mongodbrest.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.service.mongodb.mongodbrest.rest.entity.MongoDbExecutor;
import com.service.mongodb.mongodbrest.rest.model.AuthorXML;
import com.service.mongodb.mongodbrest.rest.model.BookXML;
import com.service.mongodb.mongodbrest.rest.model.ResponseBase;


@Path("MongoDbRest")
public class MongoDbRest {
	
	static {
		if (System.getProperty("log4j.configurationFile")==null)
			System.setProperty("log4j.configurationFile", "log4j2.xml");
	}
	private Logger logger = null;

    @Context
    protected Providers providers;

    private MongoDbExecutor mongoExecutor = null;
 
    @Context
    private ServletContext context;

	/**
     * Default constructor. 
     */
    public MongoDbRest() throws Exception {
        logger = LoggerFactory.getLogger("com.service.mongodb.mongodbrest.rest");
     }

    @PostConstruct
    public void init() {
   		try {
			mongoExecutor = new MongoDbExecutor(context.getInitParameter("mongo-config-file"));
		} catch (Throwable e) {
			mongoExecutor = new MongoDbExecutor(this.getClass().getClassLoader().getResourceAsStream("mongoconfig.properties"));
		}
   	}
    @PreDestroy
    public void releaseMongo() {
   		try {
			mongoExecutor.disconnect();;
		} catch (Throwable e) {
			mongoExecutor = new MongoDbExecutor(this.getClass().getClassLoader().getResourceAsStream("mongoconfig.properties"));
		}
   	}
    
    /**
     * Retrieves representation of an instance of MongoDbRest
     * @return an instance of com.services.rest.mongodb.model.ResList
     */
	@SuppressWarnings("unchecked")
	@GET
	@Path("/books")
	@Produces(MediaType.APPLICATION_JSON)
	public List<BookXML> lookupBooks() {
		return (List<BookXML>)mongoExecutor.selectAllInACollection("books");
	}

	@GET
	@Path("/byid/{collection}/{uniqueid}")
	@Produces(MediaType.APPLICATION_JSON)
	public BookXML getOneElementsById(@PathParam("collection") String collection, @PathParam("uniqueid") String uniqueid) {
		return (BookXML)mongoExecutor.selectOneInACollection(collection, uniqueid);
	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("/authors")
	@Produces(MediaType.APPLICATION_JSON)
	public List<AuthorXML> lookupAuthors() {
		return (List<AuthorXML>)mongoExecutor.selectAllInACollection("authors");
	}
	/**
     * Retrieves representation of an instance of MongoDbRest
     * @return an instance of com.services.rest.mongodb.model.ResList
     * @throws IOException 
     */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{collection}/{uniqueid}")
	public ResponseBase insertOneElementIntoACollection(@PathParam("collection") String collection, @PathParam("uniqueid") String uniqueid, String bookInput) throws IOException, Exception {
		try {
			String json = java.net.URLDecoder.decode(bookInput, "UTF-8");
			logger.info("insertOneElementIntoACollection() - json to save = " + json);
			mongoExecutor.updateOneInACollection(collection, uniqueid, json);
			return new ResponseBase(0, "Book : "+uniqueid+" saved!");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseBase(202, "Book data not valid for : "+uniqueid+" book!");
		}
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/insert/{collection}/{uniqueid}")
	public ResponseBase testInsertOneElementIntoACollection(@PathParam("collection") String collection, @PathParam("uniqueid") String uniqueid, String json) throws IOException, Exception {
		try {
			logger.info("insertOneElementIntoACollection() - json to save = " + json);
			mongoExecutor.updateOneInACollection(collection, uniqueid, json);
			return new ResponseBase(0, "Book : "+uniqueid+" saved!");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseBase(202, "Book data not valid for : "+uniqueid+" book!");
		}
	}

	/**
     * GET method for updating or creating an instance of MongoDbRest
     * @content content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
	@GET
	@Path("/remove/{collection}/{uniqueid}")
//	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ResponseBase removeOneElementFromACollectionById(@PathParam("collection") String collection, @PathParam("uniqueid") String uniqueid) {
		logger.info("removeElementFromACollectionById - called - colletion : " + collection + "  uniqueid : " + uniqueid);
		try {
			mongoExecutor.deleteOneInACollection(collection, uniqueid);
			return new ResponseBase(0, "Remove from collection : "+collection+" the element with uid : "+uniqueid+"!");
		} catch (Exception e) {
		}
		return new ResponseBase(200, "Errors removing from collection : "+collection+" the element with uid : "+uniqueid+"!");
	}
}