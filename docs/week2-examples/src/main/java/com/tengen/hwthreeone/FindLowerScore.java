package com.tengen.hwthreeone;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import java.net.UnknownHostException;
import java.util.Iterator;

/**
 * Finds the lower score removing that entry from the student document
 */
public class FindLowerScore {
    public static void main(String[] args) {
        MongoClient client = null;
        try {
            client = new MongoClient(new ServerAddress("localhost", 27017));
        } catch (UnknownHostException e) {
            System.out.println("Unknown host check the connection settings");
            System.out.println(e); // e.printStackTrace();
        }
        assert client != null;
        DB database = client.getDB("school");
        DBCollection studentsCollection = database.getCollection("students");
        DBCursor cursor = studentsCollection.find().sort(new BasicDBObject("student_id", -1));
        try {
            while (cursor.hasNext()) {
                DBObject currentStudent = cursor.next();
                System.out.println(currentStudent);
                final BasicDBList scores = (BasicDBList) currentStudent.get("scores");
                Iterator<Object> iterator = scores.iterator();
                BasicDBObject firstHWObject = null;
                while (iterator.hasNext()) {
                    BasicDBObject secondHWObject = (BasicDBObject) iterator.next();
                    if (secondHWObject.get("type").toString().equalsIgnoreCase("homework")) {
                        if (null == firstHWObject) {
                            System.out.println("==== homework oldscore initializing ====");
                            firstHWObject = secondHWObject;
                        } else {
                            final double firstHWScore = firstHWObject.getDouble("score");
                            System.out.println(":::: homework oldscore ::::" + firstHWScore);
                            final double secondHWscore = secondHWObject.getDouble("score");
                            System.out.println(":::: homework currentscore::::" + secondHWscore);
                            if (firstHWScore < secondHWscore) {
                                System.out.println(":::: to remove >>> " + firstHWScore);
                                studentsCollection.update(new BasicDBObject("_id", currentStudent.get("_id")), new BasicDBObject("$pull", new BasicDBObject("scores", firstHWObject)));
                            } else {
                                System.out.println(":::: to remove >>> " + secondHWscore);
                                studentsCollection.update(new BasicDBObject("_id", currentStudent.get("_id")), new BasicDBObject("$pull", new BasicDBObject("scores", secondHWObject)));
                            }
                            firstHWObject = null;
                        }
                    }
                }                // studentsCollection.
            }
        } finally {
            System.out.print(cursor.count());
            cursor.close();
        }
    }
}
