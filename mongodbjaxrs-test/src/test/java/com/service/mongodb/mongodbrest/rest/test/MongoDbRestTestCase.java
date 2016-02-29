package com.service.mongodb.mongodbrest.rest.test;

import static com.service.mongodb.mongodbrest.rest.test.persistence.MongoPersistenceContext.DEFAULT_MONGO_COLLECTION;
import static com.service.mongodb.mongodbrest.rest.test.persistence.MongoPersistenceContext.SECONDARY_MONGO_COLLECTION;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.bson.Document;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.access.mongodb.mongolib.db.MyDocument;
import com.service.mongodb.mongodbrest.rest.model.GenericXML;
import com.service.mongodb.mongodbrest.rest.model.ModelInterceptor;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.ExtractedArtifactStoreBuilder;
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

@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class MongoDbRestTestCase {
	private String json = "{\"id\":\"4c1a86bb2955000000004076\",\"description\":\"MongoDB is no sql database\",\"likes\":100,\"tags\":[\"mongodb\",\"database\",\"NoSQL\"],\"title\":\"MongoDB Overview\",\"url\":\"http://www.tutorialspoint.com\",\"user\":\"tutorials point site\"}";
	private static final Logger logger = LoggerFactory.getLogger("com.access.mongodb.mongolib.test");
	private static final String MONGO_HOST = "127.0.0.1";
	private static final int MONGO_PORT = 27017;

	private static MongodExecutable mongodExe;
	private static MongodProcess mongod;
	private static List<String> jsonData;

	private static String authorSingleJSONData = "{\"id\":\"56c9cf00d677708784fe79af\",\"age\":41,\"blog\":\"http://ie.linkedin.com/in/fabriziotorelli\",\"editor\":\"Fabrizio Editore Ltd\",\"name\":\"Fabrizio\",\"rate\":3.6,\"social\":\"http://www.facebook.com/fabrizio.torelli\",\"surname\":\"Torelli\",\"tags\":[\"Architecture\",\"Information Technology\",\"Object Oriented Architecture\",\"UML\"]}";
	@BeforeClass
	public static void setup() throws Exception {
		try {

			logger.info("setup() - creating MongoDb embedded listener ....");
	        IRuntimeConfig _runtimeConfig=new RuntimeConfigBuilder()
			.defaults(Command.MongoD)
			.artifactStore(new ExtractedArtifactStoreBuilder()
					.defaults(Command.MongoD)
					.executableNaming(new UserTempNaming())
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

			logger.info("setup() - starting MongoDb embedded listener ....");
	        mongodExe = MongodStarter.getInstance(_runtimeConfig).prepare(mongoConfig);
	        mongod = mongodExe.start();
			logger.info("setup() - inserting mocked data in the MongoDb embedded insrtance ....");
			logger.info("setup() - process clompleted ....");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("setup() - MongoDb embedded instance shutdown ....");
			teardown();
		}
	}
	
	protected static void teardown() throws Exception {
		logger.info("teardown() - starting MongoDb instance shutdown in progress ....");
/*		if (mongod != null) {
			mongod.stop();
		}
		if (mongodExe != null) {
			mongodExe.stop();
		}
*/		logger.info("teardown() - starting MongoDb instance shutdown completed!!");
	}
	protected static boolean mockData() throws Exception {
		logger.info("mockData() - loading json mocked data ....");
		boolean success = true;
		try {
			jsonData = getMockedJSONList();
			for(String json: jsonData) {
				String id = Document.parse(json).getString("id");
				if (!insertDataInTheService(DEFAULT_MONGO_COLLECTION, id, json) )
					throw new RuntimeException("Unable to run insert for json <"+DEFAULT_MONGO_COLLECTION+"> : " + json);
			}
			String authorId = Document.parse(authorSingleJSONData).getString("id");
			if (!insertDataInTheService(SECONDARY_MONGO_COLLECTION, authorId, authorSingleJSONData))
				throw new RuntimeException("Unable to run insert for json <"+SECONDARY_MONGO_COLLECTION+"> : " + authorSingleJSONData);

		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		}
		logger.info("mockData() - loading json mocked data completed!!");
		return success;
	}

	@Deployment
	public static WebArchive createDeploymentPackage() {
		logger.info("createDeploymentPackage() - deploying and running webarchive mongodbjaxrs-0.0.1-SNAPSHOT.war ....");
		return ShrinkWrap.createFromZipFile(WebArchive.class, new File("../mongodbjaxrs/target/mongodbjaxrs-0.0.1-SNAPSHOT.war"));
	}

	@Test
	public void test0ShouldBeAbleToMock() throws Exception {
		
		logger.info("shouldBeAbleToMock ....");
		assertEquals(true, mockData());
	}

	@Test
	public void test1Should1GetBookXMLListCorrect() throws Exception 
	{
		logger.info("shouldGetBookXMLListCorrect ....");
		String expected = "{\"bookXML\":[{\"id\":\"4c1a86bb2955000000004076\",\"description\":\"MongoDB is no sql database\",\"likes\":\"100\",\"tags\":[\"mongodb\",\"database\",\"NoSQL\"],\"title\":\"MongoDB Overview\",\"url\":\"http://www.tutorialspoint.com\",\"user\":\"tutorials point\"},{\"id\":\"4c1a86bb2955000000004077\",\"description\":\"No sql database is very fast\",\"likes\":\"10\",\"tags\":[\"mongodb\",\"database\",\"NoSQL\"],\"title\":\"NoSQL Overview\",\"url\":\"http://www.tutorialspoint.com\",\"user\":\"tutorials point\"},{\"id\":\"4c1a86bb2955000000004078\",\"description\":\"Neo4j is no sql database\",\"likes\":\"750\",\"tags\":[\"neo4j\",\"database\",\"NoSQL\"],\"title\":\"Neo4j Overview\",\"url\":\"http://www.neo4j.com\",\"user\":\"Neo4j\"},{\"id\":\"56c923c8d677705b78424cc4\",\"description\":\"Design Patterns and OOA techniques\",\"likes\":\"0\",\"tags\":[\"Design Pattern\",\"Google Books\",\"Object Oriented Architecture\"],\"title\":\"Design Patterns: Elements of Reusable Object-Oriented Software\",\"url\":\"https://books.google.ie/books?id=6oHuKQe3TjQC\",\"user\":\"Erich Gamma Richard Helm Ralph Johnson John Vlissides\"}]}";
		Client client = Client.create();

		WebResource webResource = client
				.resource("http://localhost:8085/mongodbjaxrs/resources/MongoDbRest/books");
		ClientResponse response = webResource.type("application/json")
				.get(ClientResponse.class);//, input);
		assertEquals(200, response.getStatus());
		assertEquals(expected, response.getEntity(String.class));
	}

	@Test
	public void test2ShouldGetAuthorXMLListCorrect() throws Exception 
	{
		logger.info("shouldGetAuthorXMLListCorrect ....");
		String expected = "{\"authorXML\":{\"id\":\"56c9cf00d677708784fe79af\",\"age\":\"41\",\"blog\":\"http://ie.linkedin.com/in/fabriziotorelli\",\"editor\":\"Fabrizio Editore Ltd\",\"name\":\"Fabrizio\",\"rate\":\"3.6\",\"social\":\"http://www.facebook.com/fabrizio.torelli\",\"surname\":\"Torelli\",\"tags\":[\"Architecture\",\"Information Technology\",\"Object Oriented Architecture\",\"UML\"]}}";
		Client client = Client.create();

		WebResource webResource = client
				.resource("http://localhost:8085/mongodbjaxrs/resources/MongoDbRest/authors");
		ClientResponse response = webResource.type("application/json")
				.get(ClientResponse.class);//, input);
		assertEquals(200, response.getStatus());
		assertEquals(expected, response.getEntity(String.class));
	}

	@Test
	public void test3ShouldDeleteBookXMLAndReduceTheList() throws Exception 
	{
		logger.info("shouldDeleteBookXMLAndReduceTheList ....");
		String expected = "{\"code\":\"0\",\"message\":\"Remove from collection : books the element with uid : 4c1a86bb2955000000004076!\"}";
		Client client = Client.create();

		WebResource webResource = client
				.resource("http://localhost:8085/mongodbjaxrs/resources/MongoDbRest/remove/books/4c1a86bb2955000000004076");
		ClientResponse response = webResource.type("application/json")
				.get(ClientResponse.class);//, input);
		assertEquals(200, response.getStatus());
		assertEquals(expected, response.getEntity(String.class));
	}

	@Test
	public void test0ShouldDeleteAuthorXMAndReduceTheList() throws Exception 
	{
		logger.info("shouldDeleteAuthorXMAndReduceTheList ....");
		String expected = "{\"code\":\"0\",\"message\":\"Remove from collection : authors the element with uid : 56d21602d67770c8ec685b18!\"}";
		Client client = Client.create();

		WebResource webResource = client
				.resource("http://localhost:8085/mongodbjaxrs/resources/MongoDbRest/remove/authors/56d21602d67770c8ec685b18");
		ClientResponse response = webResource.type("application/json")
				.get(ClientResponse.class);//, input);
		assertEquals(200, response.getStatus());
		assertEquals(expected, response.getEntity(String.class));
	}

	
	protected static boolean insertDataInTheService(String collection, String id, String json) throws Exception 
	{
		logger.info("shouldGetAuthorXMLListCorrect ....");
		ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        Client client = Client.create(clientConfig);

        // Jersey client POST example
        Document entityDoc = Document.parse(json);
        GenericXML entity = ModelInterceptor.getInstance().getModelByCollection(collection, entityDoc);
        String postURL = "http://localhost:8085/mongodbjaxrs/resources/MongoDbRest/insert/"+collection+"/"+id;
        WebResource webResourcePost = client.resource(postURL);
        ClientResponse response = webResourcePost.type(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, entity);
        String responseEntity = response.getEntity(String.class);
		logger.warn("insertDataInTheService() - Response : " + response.getStatus());
		logger.warn("insertDataInTheService() - Response Entity : " + responseEntity);
		Document responseDoc = Document.parse(responseEntity);
		return responseEntity!=null && responseDoc!=null && responseDoc.getString("code").equals("0");
	}
	protected static synchronized Document createMongoDbDocument(String json, String id) throws Exception {
		return MyDocument.fromJSON(json, id);
	}

	protected static String getMavenHome() {
		String mavenHome=null;
		Map<String, String> env = System.getenv();
		for (String envName : env.keySet()) {
			if(envName.equalsIgnoreCase("MAVEN_HOME")||envName.equalsIgnoreCase("M2_HOME")) {
				mavenHome = env.get(envName);
			}
		}
		return mavenHome;
	}

	protected static synchronized List<String> getMockedJSONList() throws Exception {
		List<String> mockedList = new ArrayList<String>(0);
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(MongoDbRestTestCase.class.getResourceAsStream("/jsonlist.txt")));
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

}
