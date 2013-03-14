package com.tengen;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.QueryBuilder;
import com.mongodb.ServerAddress;

import java.net.UnknownHostException;

/**
 * Created with IntelliJ IDEA.
 * User: abenedetti
 * Date: 12-03-13
 * Time: 01:27
 * To change this template use File | Settings | File Templates.
 */
public class FindAndDo {
    public static void main(String[] args) throws UnknownHostException {
        MongoClient client = new MongoClient(new ServerAddress("localhost", 27017));

        DB database = client.getDB("students");
        final DBCollection collection = database.getCollection("grades");
        DBObject query = QueryBuilder.start("type").is("homework").get();
        //DBCursor cursor = collection.find(query, new BasicDBObject("student_id", true).append("_id", false)).sort(new BasicDBObject("student_id", 1).append("score", -1));
        DBCursor cursor = collection.find(query).sort(new BasicDBObject("student_id", -1).append("score", -1));
        try {
            DBObject actual = null;
            DBObject anterior = null;
            while (cursor.hasNext()) {
                if (cursor.hasNext()) {
                    anterior = actual;
                    actual = cursor.next();
                } else {
                    collection.remove(actual);
                    System.out.println(" del " + actual);
                    System.out.println(" ===== ");
                }
                System.out.println(" anterior " + anterior);
                System.out.println(" actual " + actual);
                if (null != anterior && actual.get("student_id").toString().equalsIgnoreCase(anterior.get("student_id").toString())) {
                    System.out.println(actual.get("student_id").toString() + " == " + anterior.get("student_id").toString());
                    collection.remove(actual);
                    System.out.println(" del " + actual);
                    System.out.println(" ===== ");
                }

            }

        } finally {
            System.out.print(cursor.count());
            cursor.close();
        }

    }

}
