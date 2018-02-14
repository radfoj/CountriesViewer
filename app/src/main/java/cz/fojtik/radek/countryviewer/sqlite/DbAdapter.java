package cz.fojtik.radek.countryviewer.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class DbAdapter {
    private myDbHelper myhelper;
    public DbAdapter(Context context)
    {
        myhelper = new myDbHelper(context);
    }

    public long insertData(String tableName, String[] col, String[] colValues)
    {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i<col.length; i++)
        {
            contentValues.put(col[i],colValues[i]);
        }
        return dbb.insert(tableName, null , contentValues);
    }

    public List<String> getData(String tableName, String[] columns)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        /*/String[] columns = {myDbHelper.UID,myDbHelper.NAME,myDbHelper.MyPASSWORD};/*/
        Cursor cursor =db.query(tableName,columns,null,null,null,null,null);
        List<String>buffer= new ArrayList<String>(){};
        while (cursor.moveToNext())
        {
            String res = "";
            for (String itm : columns)
            {
                res += cursor.getString(cursor.getColumnIndex(itm)) + "|";
            }

            if (!res.isEmpty())
                res = res.substring(0, res.length() - 1);

            buffer.add(res);
        }
        return buffer;
    }

    public int GetCountRecords(String tableName)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        return  db.query(tableName,null,null,null,null,null,null).getCount();

    }

    public void upgradeDB(){
        SQLiteDatabase db = myhelper.getWritableDatabase();
        myhelper.onUpgrade(db, 2,2);
    }
    public  int delete(String tableName, String whereCl)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();

        int count =db.delete(tableName ,whereCl,new String[]{});
        return  count;
    }
/*/
    public int updateName(String tableName , String whereCl)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.NAME,newName);
        String[] whereArgs= new String[]{};
        int count =db.update(myDbHelper.TABLE_NAME,contentValues, myDbHelper.NAME+" = ?",whereArgs );
        return count;
    }
    /*/

    static class myDbHelper extends SQLiteOpenHelper
    {
        private static final String DATABASE_NAME = "CountryViewer";    // Database Name
        private static final int DATABASE_Version = 1;   // Database Version
        private static final String UID="_id";     // Column I (Primary Key)
        private Context context;

        public myDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context=context;
        }

        public void onCreate(SQLiteDatabase db) {

            try {

                db.execSQL("CREATE TABLE "+Tables.history.dbName()+
                        " ("+UID+" INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(255) ,create_time VARCHAR(225)" +
                        " ,found VARCHAR(1));");
                db.execSQL("CREATE TABLE "+Tables.settings.dbName()+
                        " ("+UID+" INTEGER PRIMARY KEY AUTOINCREMENT, save_history VARCHAR(255) ,save_found VARCHAR(225),max_history VARCHAR(225));");
                db.execSQL("insert into " +Tables.settings.dbName() + " (_id, save_history, save_found, max_history ) values (1,1,1,15);" ); //default settings
            } catch (Exception e) {
                Message.message(context,""+e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                Message.message(context,"OnUpgrade");
                for (Tables table : Tables.values())
                {
                    db.execSQL("DROP TABLE IF EXISTS " + table.dbName());
                }
                onCreate(db);
            }catch (Exception e) {
                Message.message(context,""+e);
            }
        }
    }
}

