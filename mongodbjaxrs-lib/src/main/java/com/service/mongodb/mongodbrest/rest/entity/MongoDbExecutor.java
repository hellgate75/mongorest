package com.service.mongodb.mongodbrest.rest.entity;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.access.mongodb.mongolib.db.MongoDbClient;
import com.service.mongodb.mongodbrest.rest.model.GenericXML;
import com.service.mongodb.mongodbrest.rest.model.ModelInterceptor;

public class MongoDbExecutor {
	private Properties mongoProperties = new Properties();
	private MongoDbClient mongoClient = null;
    private Logger logger = LoggerFactory.getLogger("com.service.mongodb.mongodbrest.rest");


	public MongoDbExecutor() {
		if (mongoClient==null) {
			try {
				mongoProperties.load(this.getClass().getClassLoader().getResourceAsStream("mongoconfig.properties"));
				mongoClient = getMongoDbClient();
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
		}
	}
	public MongoDbExecutor(String resource) {
		if (mongoClient==null) {
			try {
				mongoProperties.load(this.getClass().getClassLoader().getResourceAsStream(resource));
				mongoClient = getMongoDbClient();
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
		}
	}
	public MongoDbExecutor(InputStream resourceStream) {
		if (mongoClient==null) {
			try {
				mongoProperties.load(resourceStream);
				mongoClient = getMongoDbClient();
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
		}
	}
	private MongoDbClient getMongoDbClient() throws Exception {
		String hostname = mongoProperties.getProperty("mongoclient.hostname", "localhost");
		Integer port = Integer.parseInt(mongoProperties.getProperty("mongoclient.port", "27017"));
		Boolean journaled = Boolean.parseBoolean(mongoProperties.getProperty("mongoclient.journaled", "false"));
		MongoDbClient mongoClient = new MongoDbClient(hostname, port, journaled);
		mongoClient.database(mongoProperties.getProperty("mongoclient.database", "accounts"));
		return mongoClient;

	}
	public void disconnect() {
		try {
			mongoClient.disconnect();
		} catch (Exception e) {
		}
	}
	public void connect() {
		try {
			mongoClient.reconnect();
			mongoClient.database(mongoProperties.getProperty("mongoclient.database", "accounts"));
		} catch (Exception e) {
		}
	}
	public void connect(String database) {
		try {
			mongoClient.reconnect();
			mongoClient.database(database);
		} catch (Exception e) {
		}
	}
	public synchronized List<? extends GenericXML> selectAllInACollection(String collection) {
		try {
			mongoClient.collection(collection);
			List<Document> documents = mongoClient.all();
			logger.info("selectAllInACollection() - collection : " + collection + " - objects : " + documents.size());
			return ModelInterceptor.getInstance().getModelByCollection(collection, documents);
		} catch (Throwable t) {
			disconnect();
			throw new RuntimeException(t);
		}
	}

	public synchronized GenericXML selectOneInACollection(String collection, String id) {
		try {
			mongoClient.collection("authors");
			Document filter = new Document();
			filter.put("id", MongoDbClient.convertObjectId(id));
			List<Document> documents = mongoClient.filter(filter);
			return ModelInterceptor.getInstance().getModelByCollection(collection, documents.get(0));
		} catch (Throwable t) {
			disconnect();
			throw new RuntimeException(t);
		}
	}

	public synchronized void updateOneInACollection(String collection, String uniqueId, String json) {
		try {
			System.out.println("json = " + json);
			Document book = Document.parse(json);
			System.out.println("book = " + book);
			if ("new".equalsIgnoreCase(uniqueId)||"".equalsIgnoreCase(uniqueId)||uniqueId==null) {
				book.append("_id",  MongoDbClient.createObjectId());
				book.remove("id");
			}
			else {
				book.append("_id", MongoDbClient.convertObjectId(uniqueId));
				book.remove("id");
				book.remove("type");
			}
			System.out.println("connecting ...");
			System.out.println("collection = " + collection + " ...");
			mongoClient.collection(collection);
			System.out.println("processing  uniqueId = " + uniqueId + " ...");
			if ("new".equalsIgnoreCase(uniqueId)) {
				mongoClient.insert(book);
			}
			else {
				Document filter = new Document();
				filter.put("_id", MongoDbClient.convertObjectId(uniqueId));
				mongoClient.update(filter, book, mongoClient.createUpdateOption(true), true);
			}
			System.out.println("disconnect ...");
		} catch (Throwable t) {
			disconnect();
			throw new RuntimeException(t);
		}
	}

	public synchronized void deleteOneInACollection(String collection, String uniqueid) {
		try {
			mongoClient.collection(collection);
			Document filter = new Document();
			filter.append("_id", MongoDbClient.convertObjectId(uniqueid));
			mongoClient.delete(filter, true);
		} catch (Throwable t) {
			disconnect();
			throw new RuntimeException(t);
		}
	}

}
