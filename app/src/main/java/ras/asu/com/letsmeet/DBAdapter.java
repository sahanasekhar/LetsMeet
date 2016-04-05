package ras.asu.com.letsmeet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DBAdapter {
    //public static String TableName;
    public static final String KEY_PRIMARY = "_ID";
    //public static final String KEY_TIMESTAMP = "timestamp";
    //public static final String KEY_XVALUE = "X_VALUE";
    //public static final String KEY_YVALUE = "Y_VALUE";
    public static final String KEY_USERID = "USER_VALUE";
    public static final String KEY_USERNAME = "USER_NAME";
    public static final String KEY_URL = "USER_URL";
    private static final String TAG = "DBAdapter";
    private static final String DATABASE_NAME = "MyFriends";


    private static final int DATABASE_VERSION = 1;
    //private static final String DATABASE_CREATE =
    //"create table contacts (_id integer primary key autoincrement, "
    //      + "name text not null, email text not null);";
    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {

            String create = "CREATE TABLE  " + "Details" + " (" + KEY_PRIMARY + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_USERID + " TEXT NOT NULL, " + KEY_USERNAME + " TEXT NOT NULL, " + KEY_URL + " TEXT NOT NULL);" ;
            db.execSQL( create );

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            /*Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS contacts");*/
            onCreate(db);
        }
    }


    //---opens the database---
    public DBAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }


    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }

    // creating a dynamic table








    //---insert a contact into the database---
    public long insertData(String _ID,String USER_VALUE, String USER_NAME, String USER_URL, String TableName)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_PRIMARY, _ID);
       // initialValues.put(KEY_TIMESTAMP, timestamp);
        initialValues.put(KEY_USERID, USER_VALUE);
        initialValues.put(KEY_USERNAME, USER_NAME);
        initialValues.put(KEY_URL, USER_URL);
        return db.insert(TableName, null, initialValues);

    }

    public List<Map<String,String>> getData(String TableName)
    {
        String[] columns = new String [] { KEY_PRIMARY,KEY_USERID,KEY_USERNAME,KEY_URL};
        Cursor c = db.query(TableName,columns, null, null, null,null,null);
        List<Map<String,String>> result= new LinkedList<Map<String,String>>();
        int pk = c.getColumnIndex(KEY_PRIMARY);
       // int timestamp= c.getColumnIndex(KEY_TIMESTAMP);
        int x= c.getColumnIndex(KEY_USERID);
        int y =c.getColumnIndex(KEY_USERNAME);
        int z = c.getColumnIndex(KEY_URL);
        for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
        {
            Map <String ,String>data = new HashMap<String,String>();
            data.put(KEY_PRIMARY,String.valueOf(c.getInt(pk)));
            data.put(KEY_URL,c.getString(z));
           // data.put(KEY_TIMESTAMP,c.getString(timestamp));
            data.put(KEY_USERID,c.getString(x));
            data.put(KEY_USERNAME,c.getString(y));
            result.add(data);

        }


        return result;
    }

}