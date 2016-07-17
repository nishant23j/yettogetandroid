package com.nishant.yettoget.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Pavilion on 08-07-2016.
 */
public class WishDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "wish.db";
    private static final int DATABASE_VERSION = 1;

    public WishDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_WISH_TABLE =
                "CREATE TABLE " + TaskContract.WishEntry.WISH_TABLE + " (" +
                        TaskContract.WishEntry._ID + " INTEGER PRIMARY KEY," +
                        TaskContract.WishEntry.WISH_TASK + " TEXT NOT NULL" +
                        " );";
        db.execSQL(SQL_CREATE_WISH_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TaskContract.WishEntry.WISH_TABLE);
        onCreate(db);
    }
}
