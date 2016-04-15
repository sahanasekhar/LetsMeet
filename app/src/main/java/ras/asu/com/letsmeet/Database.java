package ras.asu.com.letsmeet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedList;

/**
 * Created by SahanaSekhar on 4/4/16.
 */


public class Database {

    public static final String KEY_PRIMARY = "_ID";

    public static final String KEY_USERID = "USER_ID";
    public static final String KEY_USERNAME = "USER_NAME";
    public static final String KEY_USERURL = "USER_URL";
    public static final String KEY_IS_FRIEND = "IS_FRIEND";
    //public static final String DATABASE_TABLE = "FRIENDS";
    //public static final String KEY_ZVALUE = "Z_VALUE";
    private static final String TAG = "DBAdapter";
    private static final String DATABASE_NAME = "MyDB";
    private static final int DATABASE_VERSION = 3;


    protected DbHelper ourHelper;
    protected final Context ourContext;
    protected SQLiteDatabase ourDatabase;


    private static class DbHelper extends SQLiteOpenHelper {
        private static String DATABASE_TABLE="FRIENDS";


        public DbHelper(Context context,String dbName) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            DATABASE_TABLE = dbName;
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" +
                            KEY_PRIMARY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            KEY_USERID + " TEXT NOT NULL UNIQUE, "+
                            KEY_USERNAME + " TEXT NOT NULL,"+
                            KEY_IS_FRIEND+" INTEGER DEFAULT 0,"+
                            KEY_USERURL + " TEXT NOT NULL);"
            );



        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
        }

    }

    public Database(Context c){
        ourContext = c;
       // DbHelper.DATABASE_TABLE= databaseName;
    }

    public Database open() throws SQLException {
        ourHelper = new DbHelper(ourContext,DbHelper.DATABASE_TABLE);
        ourDatabase = ourHelper.getWritableDatabase();

        return this;
    }
    public void close(){
        ourHelper.close();
    }


    /*public long addTable(String TableName) {
        // TODO Auto-generated method stub

        ourDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + databaseName + " (" +
                        KEY_PRIMARY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        KEY_USERID + " TEXT NOT NULL, "+
                        KEY_USERNAME + " TEXT NOT NULL,"+
                        KEY_USERURL + " TEXT NOT NULL);"
        );
        return 0;

    }*/
    public long createEntry(String avol, String mvol,String nvol,int isFriend) {
        // TODO Auto-generated method stub
        try {
            ContentValues cv = new ContentValues();

            cv.put(KEY_USERID, avol);
            cv.put(KEY_USERNAME, mvol);
            cv.put(KEY_USERURL, nvol);
            cv.put(KEY_IS_FRIEND, isFriend);

            return ourDatabase.insert(DbHelper.DATABASE_TABLE, null, cv);

        }
catch(Exception e)
{
    e.printStackTrace();
}
return 0;
    }

    public LinkedList<User> getMutualUsers()
    {
        LinkedList<User> users = new LinkedList<User>();

        String[] columns = new String[]{ KEY_USERID,KEY_USERNAME,KEY_USERURL};
        String[] whereArgs = new String[] {
                "1"
        };
        Cursor c = ourDatabase.query(DbHelper.DATABASE_TABLE, columns, KEY_IS_FRIEND+"= ?",whereArgs , null, null, null);
        //List<Map<String , String>> d = new LinkedList<HashMap<String,String>>();
        String result="";
        int usrID = c.getColumnIndex(KEY_USERID);
        int usrName = c.getColumnIndex(KEY_USERNAME);
        int usrUrl = c.getColumnIndex(KEY_USERURL);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
            User u = new User();
            u.setKEY_USERID(c.getString(usrID));
            u.setKEY_USERNAME(c.getString(usrName));
            u.setKEY_USERURL(c.getString(usrUrl));
            users.add(u);

        }


        return users;
    }

public void  addIsFriend(String fbId)
{
try {
    ContentValues cv = new ContentValues();
    cv.put(KEY_IS_FRIEND, 1);

    ourDatabase.update(DbHelper.DATABASE_TABLE, cv, KEY_USERID + "=" + fbId, null);
}
catch(Exception e)
{
    e.printStackTrace();
}
}


    /*public List<Map<String, String>> getData(String TableName) {
        // SQLiteDatabase db = open();

        String[] columns = new String[]{KEY_PRIMARY, KEY_USERID, KEY_USERNAME, KEY_USERURL};
        Cursor c = ourDatabase.query(TableName, columns, null, null, null, null, null);
        List<Map<String, String>> result = new LinkedList<Map<String, String>>();
        int pk = c.getColumnIndex(KEY_PRIMARY);
       // int timestamp = c.getColumnIndex(KEY_TIMESTAMP);
        int x = c.getColumnIndex(KEY_USERID);
        int y = c.getColumnIndex(KEY_USERNAME);
        int z = c.getColumnIndex(KEY_USERURL);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            Map<String, String> data = new HashMap<String, String>();
            data.put(KEY_PRIMARY, String.valueOf(c.getInt(pk)));
            data.put(KEY_USERURL, c.getString(z));
            //data.put(KEY_TIMESTAMP, c.getString(timestamp));
            data.put(KEY_USERID, c.getString(x));
            data.put(KEY_USERNAME, c.getString(y));
            result.add(data);

        }


        return result;
    }*/



}



