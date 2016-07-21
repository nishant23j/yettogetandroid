package com.nishant.yettoget.data;

import android.provider.BaseColumns;

/**
 * Created by Pavilion on 04-07-2016.
 */
public class TaskContract {
    public class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME ="tasks";
        public static final String COLUMN_TASK = "task";


    } public class WishEntry implements BaseColumns
    {
        public static final String WISH_TABLE = "wishs";
        public static final String WISH_TASK ="wish";
    }
    public class CartEntry implements BaseColumns
    {
        public static final String TABLE_CART = "carts";
        public static final String CART_TASK ="cart";
    }
    public class recent implements BaseColumns
    {
        public static final String TABLE_RECENT="recents";
        public static final String RECENT_TASK="recent";
        //public static final String RECENT_ID = "_ID";
    }
}
