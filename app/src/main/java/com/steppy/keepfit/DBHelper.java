package com.steppy.keepfit;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by Turkleton's on 04/02/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "SQLiteGoalsList.db";
    private static final int DATABASE_VERSION = 2;

    public static final String OLD_GOAL_TABLE_NAME = "OldGoalList";
    public static final String OLD_GOAL_COLUMN_ID = "_id";
    public static final String OLD_GOAL_COLUMN_NAME = "name";
    public static final String OLD_GOAL_COLUMN_GOALVALUE = "goalValue";
    public static final String OLD_GOAL_COLUMN_PROGRESS = "goalProgress";
    public static final String OLD_GOAL_COLUMN_PERCENTAGE = "progressPercentage";
    public static final String OLD_GOAL_COLUMN_DATE = "date";
    public static final String OLD_GOAL_COLUMN_UNITS = "units";

    public static final String GOAL_TABLE_NAME = "Goal_List";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_GOALVALUE = "goalValue";
    public static final String COLUMN_PROGRESS = "goalProgress";
    public static final String COLUMN_PERCENTAGE = "progressPercentage";
    public static final String COLUMN_ACTIVE = "active";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_UNITS = "units";

    public static final String PROGRESS_TABLE_NAME = "dayProgress";
    public static final String PROGRESS_COLUMN_ID = "_id";
    public static final String PROGRESS_COLUMN_GOAL = "goalName"; //? needed?
    public static final String PROGRESS_COLUMN_STEPS = "steps";

    private Context mContext;
    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createGoalTable = "CREATE TABLE " + GOAL_TABLE_NAME + "( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_GOALVALUE + " INTEGER, " +
                COLUMN_PROGRESS + " INTEGER, " +
                COLUMN_PERCENTAGE + " INTEGER, " +
                COLUMN_ACTIVE + " TEXT, " +
                COLUMN_DATE + " TEXT," +
                COLUMN_UNITS + " TEXT);";
        db.execSQL(createGoalTable);

        String createProgressTable="CREATE TABLE " + PROGRESS_TABLE_NAME + "( "+
                PROGRESS_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                PROGRESS_COLUMN_GOAL + " INTEGER, "+
                PROGRESS_COLUMN_STEPS + " INTEGER); ";
        db.execSQL(createProgressTable);

        String createOldGoalTable = "CREATE TABLE " + OLD_GOAL_TABLE_NAME + "( " +
                OLD_GOAL_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                OLD_GOAL_COLUMN_NAME + " TEXT, " +
                OLD_GOAL_COLUMN_GOALVALUE + " INTEGER, " +
                OLD_GOAL_COLUMN_PROGRESS + " INTEGER, " +
                OLD_GOAL_COLUMN_PERCENTAGE + " INTEGER, " +
                OLD_GOAL_COLUMN_DATE + " TEXT, "+
                OLD_GOAL_COLUMN_UNITS + " TEXT );";
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

    public boolean insertGoal(String name, int goalValue, String active, String date, String units) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_GOALVALUE, goalValue);
        contentValues.put(COLUMN_ACTIVE, active);
        contentValues.put(COLUMN_DATE, date);
        contentValues.put(COLUMN_UNITS, units);
        db.insert(GOAL_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean updateGoal(int id, String name, String goalValue, String active, String date, String units) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_GOALVALUE, goalValue);
        contentValues.put(COLUMN_ACTIVE, active);
        contentValues.put(COLUMN_DATE, date);
        contentValues.put(COLUMN_UNITS,units);
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

    public boolean updateDayProgress(int steps){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resPro = db.rawQuery("SELECT * FROM " + PROGRESS_TABLE_NAME + " WHERE " +
                COLUMN_ID + "=?", new String[] {"1"});
//        resPro.setNotificationUri(mContext.getContentResolver(),uri);
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
            String goalActive="";
            try {
                goalActive = resultGoal.getString(resultGoal.getColumnIndex(DBHelper.COLUMN_NAME));
            }catch (Exception e){
                resPro.close();
                db.close();
                return false;
            }
            resultGoal.close();

            contentValues.put(PROGRESS_COLUMN_GOAL, goalActive);
            contentValues.put(PROGRESS_COLUMN_STEPS, steps); //int not string blahahahahahghhh
            db.insert(PROGRESS_TABLE_NAME, null, contentValues);
        }else{
            ContentValues contentValues1 = new ContentValues();
            resPro.moveToFirst();

            Cursor resultGoal = db.rawQuery("SELECT * FROM " + GOAL_TABLE_NAME + " WHERE "+
                    COLUMN_ACTIVE + "=?", new String[] {"true"});

            int stepsOld = resPro.getInt(resPro.getColumnIndex(DBHelper.PROGRESS_COLUMN_STEPS));
            int stepsCombine = steps + stepsOld;
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

    public int resetDayProgress(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.delete(PROGRESS_TABLE_NAME,
                null, null);
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

    public boolean insertOldGoal(String name, int goalValue, int progress, String percent, String date, String units) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(OLD_GOAL_COLUMN_NAME, name);
        contentValues.put(OLD_GOAL_COLUMN_GOALVALUE, goalValue);
        contentValues.put(OLD_GOAL_COLUMN_PROGRESS, progress);
        contentValues.put(OLD_GOAL_COLUMN_PERCENTAGE,percent);
        contentValues.put(OLD_GOAL_COLUMN_DATE, date);
        contentValues.put(OLD_GOAL_COLUMN_UNITS,units);
        db.insert(OLD_GOAL_TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getAllOldGoals(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + OLD_GOAL_TABLE_NAME, null );
        return res;
    }

    public Cursor getCustomUserOldGoals(String statistics,String startDate,String endDate, String cutOffDirection,String cutOffPercentage){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res=null;//  = db.rawQuery( "SELECT * FROM " + OLD_GOAL_TABLE_NAME,null);

        switch(cutOffDirection) {
            case "No selection":
                res = db.rawQuery( "SELECT * FROM " + OLD_GOAL_TABLE_NAME + " WHERE " +
                        OLD_GOAL_COLUMN_DATE + " BETWEEN "+ "?"+ " AND " + "?", new String[] {startDate,endDate});
                break;
            case "Goal fully completed":
                res = db.rawQuery( "SELECT * FROM " + OLD_GOAL_TABLE_NAME + " WHERE " +
                        OLD_GOAL_COLUMN_DATE + " BETWEEN "+ "?"+ " AND " + "?" +
                        " AND "+ OLD_GOAL_COLUMN_PERCENTAGE + " = ?", new String[] {startDate,endDate,"100"});
                break;
            case "Below percentage":
                res = db.rawQuery( "SELECT * FROM " + OLD_GOAL_TABLE_NAME + " WHERE " +
                        OLD_GOAL_COLUMN_DATE + " BETWEEN "+ "?"+ " AND " + "?" +
                        " AND "+ OLD_GOAL_COLUMN_PERCENTAGE + " < " + "?", new String[] {startDate,endDate,cutOffPercentage});
                break;
            case "Above percentage":
                res = db.rawQuery( "SELECT * FROM " + OLD_GOAL_TABLE_NAME + " WHERE " +
                        OLD_GOAL_COLUMN_DATE + " BETWEEN "+ "?"+ " AND " + "?" +
                        " AND "+ OLD_GOAL_COLUMN_PERCENTAGE + " > " + "?", new String[] {startDate,endDate,cutOffPercentage});
                break;

        }
        return res;
    }

    public int deleteAllOldGoals() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(OLD_GOAL_TABLE_NAME,
                null, null);
    }


    /*
        Statistics
    * */

    public  Cursor getStatistics(String startDate, String endDate, String startPercent, String endPercent){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery(" SELECT * FROM "+ OLD_GOAL_TABLE_NAME + " WHERE "+
                OLD_GOAL_COLUMN_DATE + " BETWEEN " +"? "+"AND " + "? " +
                "AND " + OLD_GOAL_COLUMN_PERCENTAGE + " BETWEEN "+ "? " + "AND " + " ?",
                new String[] {startDate, endDate, startPercent, endPercent});
        return result;
    }
}
