package com.steppy.keepfit;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Turkleton's on 04/02/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "SQLiteGoalsList.db";
    private static final int DATABASE_VERSION = 1;

    public static final String OLD_GOAL_TABLE_NAME = "OldGoalList";
    public static final String OLD_GOAL_COLUMN_ID = "_id";
    public static final String OLD_GOAL_COLUMN_NAME = "name";
    public static final String OLD_GOAL_COLUMN_GOALVALUE = "goalValue";
    public static final String OLD_GOAL_COLUMN_PROGRESS = "goalProgress";
    public static final String OLD_GOAL_COLUMN_DATE = "date";

    public static final String GOAL_TABLE_NAME = "Goal_List";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_GOALVALUE = "goalValue";
    public static final String COLUMN_PROGRESS = "goalProgress";
    public static final String COLUMN_ACTIVE = "active";
    public static final String COLUMN_DATE = "date";

    public static final String PROGRESS_TABLE_NAME = "dayProgress";
    public static final String PROGRESS_COLUMN_ID = "_id";
    public static final String PROGRESS_COLUMN_GOAL = "goalName"; //? needed?
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
                COLUMN_PROGRESS + " TEXT, " +
                COLUMN_ACTIVE + " TEXT, " +
                COLUMN_DATE + " TEXT );";
        db.execSQL(createGoalTable);

        String createProgressTable="CREATE TABLE " + PROGRESS_TABLE_NAME + "( "+
                PROGRESS_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                PROGRESS_COLUMN_GOAL + " TEXT, "+
                PROGRESS_COLUMN_STEPS + " TEXT); ";
        db.execSQL(createProgressTable);

        String createOldGoalTable = "CREATE TABLE " + OLD_GOAL_TABLE_NAME + "( " +
                OLD_GOAL_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                OLD_GOAL_COLUMN_NAME + " TEXT, " +
                OLD_GOAL_COLUMN_GOALVALUE + " TEXT, " +
                OLD_GOAL_COLUMN_PROGRESS + " TEXT, " +
                OLD_GOAL_COLUMN_DATE + " TEXT );";
        db.execSQL(createOldGoalTable);
    }

     /*
      * Goals db functions
      */

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GOAL_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PROGRESS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + OLD_GOAL_TABLE_NAME);
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
     * Steps db functions
     */

    public boolean updateDayProgress(String steps){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resPro = db.rawQuery("SELECT * FROM " + PROGRESS_TABLE_NAME + " WHERE " +
                COLUMN_ID + "=?", new String[] {"1"});

        if(resPro.getCount()==0){
            //If there is no progress entry
            ContentValues contentValues = new ContentValues();
            Cursor resultGoal = db.rawQuery("SELECT * FROM " + GOAL_TABLE_NAME + " WHERE "+
                        COLUMN_ACTIVE + "=?", new String[] {"true"});
            if(resultGoal==null){
                //If there is not active goal
                resPro.close();
                db.close();
                return false;
            }
            resultGoal.moveToFirst();
            String goalActive = resultGoal.getString(resultGoal.getColumnIndex(DBHelper.COLUMN_NAME));
            resultGoal.close();

            contentValues.put(PROGRESS_COLUMN_GOAL, goalActive);
            contentValues.put(PROGRESS_COLUMN_STEPS, steps); //int not string blahahahahahghhh
            db.insert(PROGRESS_TABLE_NAME, null, contentValues);
        }else{
            ContentValues contentValues1 = new ContentValues();
            resPro.moveToFirst();

            Cursor resultGoal = db.rawQuery("SELECT * FROM " + GOAL_TABLE_NAME + " WHERE "+
                    COLUMN_ACTIVE + "=?", new String[] {"true"});

            String stepsOld = resPro.getString(resPro.getColumnIndex(DBHelper.PROGRESS_COLUMN_STEPS));
            String stepsCombine = Integer.toString(Integer.parseInt(steps) + Integer.parseInt(stepsOld));
            resultGoal.moveToFirst();
            String goalActive = resultGoal.getString(resultGoal.getColumnIndex(DBHelper.COLUMN_NAME));
            int id = resPro.getInt(resPro.getColumnIndex(DBHelper.PROGRESS_COLUMN_ID));
            resultGoal.close();
            resPro.close();
            contentValues1.put(PROGRESS_COLUMN_GOAL, goalActive);
            contentValues1.put(PROGRESS_COLUMN_STEPS,stepsCombine);
            db.update(PROGRESS_TABLE_NAME, contentValues1, PROGRESS_COLUMN_ID + " = ? ", new String[] { Integer.toString(id) } );
        }
        db.close();
        return true;
    }

    public Cursor getDayProgress(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + PROGRESS_TABLE_NAME + " WHERE " +
                COLUMN_ID + "=?", new String[] {"1"});
        return res;
    }


    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    /*
     * Old goals db functions
     */

    public boolean insertOldGoal(String name, String goalValue, String progress, String date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(OLD_GOAL_COLUMN_NAME, name);
        contentValues.put(OLD_GOAL_COLUMN_GOALVALUE, goalValue);
        contentValues.put(OLD_GOAL_COLUMN_PROGRESS, progress);
        contentValues.put(OLD_GOAL_COLUMN_DATE, date);
        db.insert(OLD_GOAL_TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getAllOldGoals(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + OLD_GOAL_TABLE_NAME, null );
        return res;
    }

}
