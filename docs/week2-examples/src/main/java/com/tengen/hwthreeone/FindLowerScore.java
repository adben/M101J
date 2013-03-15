package com.tengen.hwthreeone;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
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
                BasicDBObject currentStudent = (BasicDBObject) cursor.next();
                System.out.println(currentStudent);
                massageCurrentStudent(currentStudent, studentsCollection);
                // studentsCollection.
            }
        } finally {
            System.out.print(cursor.count());
            cursor.close();
        }
    }


    private static void massageCurrentStudent(BasicDBObject currentStudent, DBCollection collection) {
        final BasicDBList scores = (BasicDBList) currentStudent.get("scores");
        Iterator<Object> iterator = scores.iterator();
        BasicDBObject oldScoreObject = null;
        while (iterator.hasNext()) {
            BasicDBObject object = (BasicDBObject) iterator.next();
            if (null != object && object.get("type").toString().equalsIgnoreCase("homework")) {
                BasicDBObject currentScore = object;
                if (null == oldScoreObject) {
                    oldScoreObject = currentScore;
                } else {
                    System.out.println(":::: homework oldscore ::::" + oldScoreObject.getDouble("score"));
                    System.out.println(":::: homework currentscore::::" + currentScore.getDouble("score"));
                    if (oldScoreObject.getDouble("score") < currentScore.getDouble("score")) {
                        System.out.println(":::: to remove >>> " + oldScoreObject.getDouble("score"));
                        collection.update(new BasicDBObject("_id", currentStudent.get("_id")), new BasicDBObject("$pull", new BasicDBObject("scores", oldScoreObject.getDouble("score"))));
                    } else {
                        System.out.println(":::: to remove >>> " + currentScore.getDouble("score"));
                        collection.update(new BasicDBObject("_id", currentStudent.get("_id")), new BasicDBObject("$pull", new BasicDBObject("scores", currentScore.getDouble("score"))));
                    }
                    oldScoreObject = null;
                }
            }
        }
    }



//    private static BasicDBObject massageCurrentStudent(BasicDBObject currentStudent) {
//        BasicDBList scores = (BasicDBList) currentStudent.get("scores");
//
//        Iterator iterator = ((ArrayList) scores).iterator();
//        while (iterator.hasNext()) {
//            BasicDBObject object = (BasicDBObject) iterator.next();
//            BasicDBObject nextObject = (BasicDBObject) iterator.next();
//
//            if (null != object && object.get("type").toString().equalsIgnoreCase("homework")) {
//
//                System.out.println(":::: homework this ::::" + object.getDouble("score"));
//                System.out.println(":::: homework future ::::" + ((BasicDBObject) iterator.next()).getDouble("score"));
//                if (object.getDouble("score") < ((BasicDBObject) iterator.next()).getDouble("score")) {
//                    System.out.println(":::: to remove >>> " + object.getDouble("score"));
//                    iterator.remove();
//                } else {
//                    System.out.println(":::: to remove >>> " + ((BasicDBObject) iterator.next()).getDouble("score"));
//                    Object next = iterator.next();
//                    ((BasicDBObject) iterator.next()).remove(next);
//
//                }
//            }
//        }
//        return currentStudent;
//    }

}
