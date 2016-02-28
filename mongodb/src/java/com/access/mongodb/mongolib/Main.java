package com.access.mongodb.mongolib;

import java.util.List;

import org.bson.Document;

import com.access.mongodb.mongolib.db.MongoDbClient;
import com.mongodb.client.MongoCollection;

public class Main {

	public static void main(String[] args) throws Throwable {
		final String ADDRESS = "localhost";
		final int PORT = 27017;
		final String DATABASE = "accounts";
		final String COLLECTION = "books";
		MongoDbClient mongoClient = null;
		try {
			System.out.println("Connecting to MongoDB Server : " + ADDRESS + ":" + PORT);
			mongoClient = new MongoDbClient(ADDRESS, PORT, true);
			System.out.println("Opening database : " + DATABASE);
			mongoClient.database(DATABASE);
			System.out.println("Opened database : " + mongoClient.getDbName());
			System.out.println("Retrieving collection : " + COLLECTION);
			MongoCollection<Document> coll =  mongoClient.collection(COLLECTION);
			if (coll!=null) {
				System.out.println("Retrivied collection : " + mongoClient.getCollectionName());
				System.out.println("Size : " + mongoClient.count());
				List<Document> listOfDocs = mongoClient.all();
				for(Document d: listOfDocs)
					System.out.println("Document : " + d.toJson());
			}
			else {
				System.out.println("Error retrieving collection : " + COLLECTION);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (mongoClient!=null)
					mongoClient.disconnect();
			} catch (Throwable e) {
			}
		}
		
		

	}

}
