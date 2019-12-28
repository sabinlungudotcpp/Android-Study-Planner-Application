package com.example.twitterclone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class StudyPlannerDB extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "planner.db"; // DB name
    private static final String TABLE_NAME = "planner_data"; // Table name
    private static final String ID_COLUMN = "planner_id"; // Unique Primary Key of the data
    private int index = 0;

    // 4 Columns in the Database
    private static final String COLUMN_1 = "module_name";
    private static final String COLUMN_2 = "day";
    private static final String COLUMN_3 = "start_time";
    private static final String COLUMN_4 = "end_time";

    // Query to CREATE the table
    private static final String CREATE_PLANNER_TABLE = "CREATE TABLE " + TABLE_NAME +
            "(" + ID_COLUMN + " INTEGER PRIMARY KEY, " + COLUMN_1 + " TEXT, " +
            COLUMN_2 + " TEXT, " + COLUMN_3 + " TEXT, " + COLUMN_4 + " TEXT" + ")";

    public StudyPlannerDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) { // Creates the table name
        db.execSQL(CREATE_PLANNER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public void addStudyPlanner(StudyPlanner studyPlanner) { // Adds (Inserts) the study planner to the database
        SQLiteDatabase plannerDB = this.getWritableDatabase(); // Write to the DB   query
        ContentValues planner_values = new ContentValues(); // A new instance of the values of the planner

        planner_values.put(ID_COLUMN, studyPlanner.getPlannerID());
        planner_values.put(COLUMN_1, studyPlanner.getModule());
        planner_values.put(COLUMN_2, studyPlanner.getDay());
        planner_values.put(COLUMN_3, studyPlanner.getStartTime());
        planner_values.put(COLUMN_4, studyPlanner.getEndTime());

        plannerDB.insert(TABLE_NAME, null, planner_values);
    }


    public void deletePlanner(StudyPlanner planner) { // Routine to delete a planner
        SQLiteDatabase database = this.getReadableDatabase();

        database.delete(TABLE_NAME, ID_COLUMN + " = ? ", new String[]{String.valueOf(planner.getPlannerID())});
        database.close();
    }

    public int updatePlanner(StudyPlanner planner) { // Routine (not invoked) to update the planner
        SQLiteDatabase database = this.getReadableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_1, planner.getModule());
        values.put(COLUMN_2, planner.getDay());
        values.put(COLUMN_3, planner.getStartTime());
        values.put(COLUMN_4, planner.getEndTime());

        return database.update(TABLE_NAME, values, ID_COLUMN + " = ? " , new String[]{String.valueOf(planner.getPlannerID())});

    }

    public ArrayList<StudyPlanner> getAllPlanners() { // Routine to get all of the planners
        ArrayList<StudyPlanner> listOfPlanners = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME; // Query to SELECT the data

        Cursor readCursor = database.rawQuery(query, null); // Create a cursor

        if(readCursor.moveToFirst()) { // If the read cursor is on the first row

          do {

              StudyPlanner planner = new StudyPlanner();

              planner.setPlannerID(Integer.parseInt(readCursor.getString(index)));
              planner.setModule(readCursor.getString(1));
              planner.setDay(readCursor.getString(2));
              planner.setStartTime(readCursor.getString(3));
              planner.setEndTime(readCursor.getString(4));

              listOfPlanners.add(planner); // Add the planner to the array list

          } while (readCursor.moveToNext()); // While loop to loop over the plans
        }

        readCursor.close(); // Close the reader

        return listOfPlanners;
    }
}