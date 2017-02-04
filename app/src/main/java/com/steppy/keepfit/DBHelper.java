package com.steppy.keepfit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Turkleton's on 04/02/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "SQLiteGoalsList.db";
    private static final int DATABASE_VERSION = 1;
    public static final String GOAL_TABLE_NAME = "Goal_List";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_GOALVALUE = "goalValue";
    public static final String COLUMN_ACTIVE = "active";
    public static final String COLUMN_DATE = "date";

    public static final String PROGRESS_TABLE_NAME = "progress";
    public static final String PROGRESS_COLUMN_ID = "_id";
    public static final String PROGRESS_COLUMN_GOAL = "goal name"; //? needed?
    public static final String PROGRESS_COLUMN_STEPS = "steps";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createGoalTable = "CREATE TABLE " + GOAL_TABLE_NAME + "( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_GOALVALUE + " TEXT, " +
                COLUMN_ACTIVE + " TEXT, " +
                COLUMN_DATE + " TEXT );";
        db.execSQL(createGoalTable);

        String createProgressTable="CREATE TABLE " + PROGRESS_TABLE_NAME + "( "+
                PROGRESS_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                PROGRESS_COLUMN_GOAL + " TEXT, "+
                PROGRESS_COLUMN_STEPS + " TEXT); ";
        db.execSQL(createProgressTable);

        //Only make this entry once
        db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PROGRESS_COLUMN_GOAL, "name");
        contentValues.put(PROGRESS_COLUMN_STEPS, "0");
        db.insert(GOAL_TABLE_NAME, null, contentValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GOAL_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PROGRESS_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertGoal(String name, String goalValue, String active, String date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_GOALVALUE, goalValue);
        contentValues.put(COLUMN_ACTIVE, active);
        contentValues.put(COLUMN_DATE, date);
        db.insert(GOAL_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean updateGoal(int id, String name, String goalValue, String active, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_GOALVALUE, goalValue);
        contentValues.put(COLUMN_ACTIVE, active);
        contentValues.put(COLUMN_DATE, date);
        db.update(GOAL_TABLE_NAME, contentValues, COLUMN_ID + " = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Cursor getGoal(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + GOAL_TABLE_NAME + " WHERE " +
                COLUMN_NAME + "=?", new String[] { name } );
        return res;
    }

    public Cursor getGoal(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + GOAL_TABLE_NAME + " WHERE " +
                COLUMN_ID + "=?", new String[] { Integer.toString(id) } );
        return res;
    }

    public Cursor getActiveGoal(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + GOAL_TABLE_NAME + " WHERE "+
                COLUMN_ACTIVE + "=?", new String[]{"true"});
        return res;
    }

    public Cursor getAllGoals() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + GOAL_TABLE_NAME, null );
        return res;
    }

//    public Cursor makeAllGoalsUnActive(){
//        SQLiteDatabase db = this.getWritableDatabase();
//
//    }

    public Integer deleteGoal(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(GOAL_TABLE_NAME,
                COLUMN_ID + " = ? ",
                new String[] { Integer.toString(id) });
    }

    public Integer deleteGoal(String name){
        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor res = db.rawQuery( "SELECT * FROM " + GOAL_TABLE_NAME + " WHERE " +
//                COLUMN_NAME + "=?", new String[] { name } );
        return db.delete(GOAL_TABLE_NAME, COLUMN_NAME + "=?",new String[] { name });
    }


    /*
        Steps db functions
     */

    public boolean updateDayProgress(int steps){
        //maybe right nopw??
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Cursor res = db.rawQuery("SELECT * FROM " + PROGRESS_TABLE_NAME + " WHERE " +
                COLUMN_ID + "=?", new String[] {"0"});
        res.moveToFirst();
        String goal = res.getString(res.getColumnIndex(DBHelper.PROGRESS_COLUMN_GOAL));
        contentValues.put(PROGRESS_COLUMN_GOAL, goal);
        contentValues.put(PROGRESS_COLUMN_STEPS, steps); //int not string blahahahahahghhh

        db.insert(PROGRESS_TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getDayProgress(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + PROGRESS_TABLE_NAME + " WHERE " +
                COLUMN_ID + "=?", new String[] {"0"});
        return res;
    }


    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

}
