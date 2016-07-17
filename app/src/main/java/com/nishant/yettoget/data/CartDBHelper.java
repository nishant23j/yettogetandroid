package com.nishant.yettoget.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Pavilion on 10-07-2016.
 */
public class CartDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "carts.db";
    private static final int DATABASE_VERSION = 1;

    public CartDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_CART_TABLE =
                "CREATE TABLE " + TaskContract.CartEntry.TABLE_CART + " (" +
                        TaskContract.CartEntry._ID + " INTEGER PRIMARY KEY," +
                        TaskContract.CartEntry.CART_TASK + " TEXT NOT NULL" +
                        " );";
        db.execSQL(SQL_CREATE_CART_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TaskContract.CartEntry.TABLE_CART);
        onCreate(db);
    }

}