package com.access.mongodb.mongolib.test.flapdoodle;

import static com.access.mongodb.mongolib.test.flapdoodle.persistence.MongoPersistenceContext.DEFAULT_MONGO_COLLECTION;
import static com.access.mongodb.mongolib.test.flapdoodle.persistence.MongoPersistenceContext.DEFAULT_MONGO_DB;
import static com.access.mongodb.mongolib.test.flapdoodle.persistence.MongoPersistenceContext.SECONDARY_MONGO_COLLECTION;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.access.mongodb.mongolib.db.MongoDbClient;
import com.access.mongodb.mongolib.db.MyDocument;
import com.access.mongodb.mongolib.test.flapdoodle.persistence.MongoPersistenceContext;
import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.ArtifactStoreBuilder;
import de.flapdoodle.embed.mongo.config.IMongoCmdOptions;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongoCmdOptionsBuilder;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.extract.UserTempNaming;
import de.flapdoodle.embed.process.runtime.Network;

public class MongoDbClientTest {
	static {
		if (System.getProperty("log4j.configurationFile")==null)
			System.setProperty("log4j.configurationFile", "resources/log4j2.xml");
	}

	private static final Logger logger = LoggerFactory.getLogger("com.access.mongodb.mongolib.test");
    private static final String MONGO_HOST = "localhost";
    private static final int MONGO_PORT = 12345;
    private static final String IN_MEM_CONNECTION_URL = MONGO_HOST + ":" + MONGO_PORT;
 
    private static MongodExecutable mongodExe;
    private static MongodProcess mongod;
    private static MongoClient mongo;
    private static MongoDbClient mongoClient;
    private static List<String> jsonData;
    private static List<Document> documentCollection;
    
    private String authorSingleJSONData = "{\"id\":\"56c9cf00d677708784fe79af\",\"age\":41,\"blog\":\"http://ie.linkedin.com/in/fabriziotorelli\",\"editor\":\"Fabrizio Editore Ltd\",\"name\":\"Fabrizio\",\"rate\":3.6,\"social\":\"http://www.facebook.com/fabrizio.torelli\",\"surname\":\"Torelli\",\"tags\":[\"Architecture\",\"Information Technology\",\"Object Oriented Architecture\",\"UML\"]}";
    /**
     * Start in-memory Mongo DB process
     */
    @BeforeClass
    public static void setup() throws Exception {
    	File f = new File("./target/temp");
    	f.mkdirs();
    	f = new File("./target/db");
    	f.mkdirs();
    	//System.setProperty("java.io.tmpdir", "target/temp");
    	jsonData = getMockedJSONList();
    	documentCollection = new ArrayList<Document>(0);
        for (String json: jsonData) {
        	documentCollection.add(createMongoDbDocument(json, "id"));
        }
        IRuntimeConfig _runtimeConfig=new RuntimeConfigBuilder()
		.defaults(Command.MongoD)
		.artifactStore(new ArtifactStoreBuilder()
				.defaults(Command.MongoD)
				.executableNaming(new UserTempNaming())
				.useCache(false)
				.build())
		.build();
        IMongoCmdOptions cmdOptions = new MongoCmdOptionsBuilder()
        .master(true)
        .enableAuth(false)
        .useNoJournal(false)
        .enableTextSearch(true)
        .verbose(false)
        .build();
        IMongodConfig mongoConfig = new MongodConfigBuilder()
		.net(new Net(MONGO_PORT, Network.localhostIsIPv6()))
		.cmdOptions(cmdOptions)
		.version(Version.Main.PRODUCTION)
		.build();

        mongodExe = MongodStarter.getInstance(_runtimeConfig).prepare(mongoConfig);
        mongod = mongodExe.start();
        mongoClient = new MongoDbClient(MONGO_HOST, MONGO_PORT, true);
        List<String> databases = mongoClient.databases();
    	System.out.println("Databases = " + databases);
        mongoClient.database(DEFAULT_MONGO_DB);
        mongoClient.collection(DEFAULT_MONGO_COLLECTION);
       	mongoClient.insert(documentCollection);
    }
 
    /**
     * Shutdown in-memory Mongo DB process
     */
    @AfterClass
    public static void teardown() throws Exception {
        if (mongoClient != null) {
			mongoClient.collection(DEFAULT_MONGO_COLLECTION);
        	Document d = new Document();
        	mongoClient.delete(d, false);
			mongoClient.collection(SECONDARY_MONGO_COLLECTION);
			mongoClient.delete(d, false);
        }
       if (mongoClient != null) {
        	mongoClient.disconnect();
        }
        if (mongod != null) {
            mongod.stop();
        }
        if (mongodExe != null) {
            mongod.stop();
        }
    }
    
    @Test
    public void shouldMockedDataCurrentlyInMongoDb() throws Exception {
       logger.info("shouldMockedDataCurrentlyInMongoDb - expected 4 elements in the collection ....");
        assertEquals(mongoClient.count(), 4);
        logger.info("shouldMockedDataCurrentlyInMongoDb - elements match with the original list ....");
        assertEquals(mongoClient.all(), documentCollection);
    }

    @Test
    public void shouldBeNavigableTheCollections() throws Exception {
    	logger.info("shouldBeNavigableTheCollections - database name should be " + DEFAULT_MONGO_DB);
    	assertEquals(mongoClient.getDbName(), DEFAULT_MONGO_DB);
    	logger.info("shouldBeNavigableTheCollections - collection name should be " + DEFAULT_MONGO_COLLECTION);
    	assertEquals(mongoClient.getCollectionName(), DEFAULT_MONGO_COLLECTION);
    	logger.info("shouldBeNavigableTheCollections - changing to a new collection the selected one should be " + SECONDARY_MONGO_COLLECTION);
    	mongoClient.collection(SECONDARY_MONGO_COLLECTION);
    	assertEquals(mongoClient.getCollectionName(), SECONDARY_MONGO_COLLECTION);
    	logger.info("shouldBeNavigableTheCollections - The new collection '"+SECONDARY_MONGO_COLLECTION+"' should not contain data");
        assertEquals(mongoClient.count(), 0);
    	logger.info("shouldBeNavigableTheCollections - After insert one element in the collection it should be saved it");
        mongoClient.insert(createMongoDbDocument(authorSingleJSONData, "id"));
        assertEquals(mongoClient.count(), 1);
    	logger.info("shouldBeNavigableTheCollections - After reopened the previous collection are expected 4 elements");
    	mongoClient.collection(DEFAULT_MONGO_COLLECTION);
        assertEquals(mongoClient.count(), 4);
    }

    @Test
    public void shouldBeAbleToAllowInsertAndDelete() throws Exception {
     	logger.info("shouldBeNavigableTheCollections - initial elements of the collection should be 4");
        assertEquals(mongoClient.count(), 4);
     	logger.info("shouldBeNavigableTheCollections - inserting a an existing element without duplication the number of elements should not increase");
     	try {
			mongoClient.insert(documentCollection.get(0));
		} catch (Exception e) {
	     	logger.warn("shouldBeNavigableTheCollections - MongoDbClient has raised and exception due to duplicate unique key.");
		}
        assertEquals(mongoClient.count(), 4);
    }
    
    protected static synchronized Document createMongoDbDocument(String json, String id) throws Exception {
    	return MyDocument.fromJSON(json, id);
    }
    
    protected static synchronized List<String> getMockedJSONList() throws Exception {
    	List<String> mockedList = new ArrayList<String>(0);
    	try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(MongoDbClientTest.class.getResourceAsStream("/jsonlist.txt")));
			while(reader.ready()) {
				String json = reader.readLine();
				if (json.trim().length()>0)
					mockedList.add(json);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return mockedList;
    }
    
   protected MongoPersistenceContext getMongoPersistenceContext() {
        // Returns an instance of the class containing business logic towards Mongo
        return new MongoPersistenceContext();
    }

}
