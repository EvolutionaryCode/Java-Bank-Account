package org.aagrandpre.bank;
//MongoDB Imports
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

//Java Imports
import java.util.concurrent.*;


/**
 *@author Austin Grandpre
 */
public class Database {

    public static void main(String args[]) {
        // Creating a Mongo client
        MongoClientURI connectionString = new MongoClientURI("mongodb://localhost:27017");
        MongoClient mongoClient = new MongoClient(connectionString);
        MongoDatabase database = mongoClient.getDatabase("mydb");
    }
}

