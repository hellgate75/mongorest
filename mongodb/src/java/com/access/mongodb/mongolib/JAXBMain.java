package com.access.mongodb.mongolib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.access.mongodb.mongolib.conf.Context;
import com.access.mongodb.mongolib.conf.ServerSpec;
import com.access.mongodb.mongolib.db.MongoDbClient;

public class JAXBMain {
	
	static {
		if (System.getProperty("log4j.configurationFile")==null)
			System.setProperty("log4j.configurationFile", "resources/log4j2.xml");
	}

	private static final Logger logger = LoggerFactory.getLogger("com.access.mongodb.mongolib");

	public static void main(String[] args) throws Exception {
		Context contextOut = new Context();
		List<ServerSpec> servers = new ArrayList<ServerSpec>(0);
		ServerSpec server = new ServerSpec();
		server.setAddress("localhost");
		server.setPort(27017);
		servers.add(server);
		contextOut.setServers(servers);
		contextOut.save(new FileOutputStream(new File("input.xml")));

		Context contextIn = Context.load(new FileInputStream(new File("input.xml")));

		contextIn.save(System.out);
		
		MongoDbClient client = new MongoDbClient(contextIn);
		client.database("accounts");
		client.collection("books");
		List<Document> docs = client.all();
		for(Document doc: docs) {
			logger.info("Book=" + doc.toJson());
		}
		client.disconnect();
	}

}
