package com.service.mongodb.mongodbrest.rest.test.persistence;

import java.util.concurrent.ConcurrentHashMap;

import com.mongodb.MongoClient;

public class MongoPersistenceContext {

    public static final String DEFAULT_MONGO_DB = "access";
    public static final String DEFAULT_MONGO_COLLECTION = "books";
    public static final String SECONDARY_MONGO_COLLECTION = "authors";

    private ConcurrentHashMap<String, MongoClient> connections = new ConcurrentHashMap<>();

    public MongoClient getConnection(final String connectionUrl) {
        if (connectionUrl == null) {
            throw new IllegalArgumentException("MongoClient connection url must be specified");
        }
        MongoClient connection = connections.get(connectionUrl);
        if (connection == null) {
            try {
                MongoClient newConnection = new MongoClient(connectionUrl);
                connection = connections.putIfAbsent(connectionUrl, newConnection);
                if (connection == null) {
                    connection = newConnection;
                } else {
                    newConnection.close();
                }
            } catch (Throwable e) {
                throw new IllegalArgumentException("Unknown host specified as MongoClient connection url", e);
            }
        }
        return connection;
    }

}
