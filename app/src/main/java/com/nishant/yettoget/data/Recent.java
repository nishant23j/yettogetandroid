package com.nishant.yettoget.data;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nishant.yettoget.data.TaskContract;

/**
 * Created by nishant on 19/7/16.
 */
public class Recent  extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "brought.db";
    private static final int DATABASE_VERSION = 1;


    public Recent(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_Brought_TABLE =
                "CREATE TABLE " + TaskContract.recent.TABLE_RECENT + " (" +
                        TaskContract.recent._ID + " INTEGER primary key ," +
                        TaskContract.recent.RECENT_TASK + " TEXT NOT NULL" +
                        " );";
        db.execSQL(SQL_CREATE_Brought_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TaskContract.recent.TABLE_RECENT);
        onCreate(db);
    }
}
