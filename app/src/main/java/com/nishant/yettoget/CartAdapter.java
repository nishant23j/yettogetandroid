package com.nishant.yettoget;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.nishant.yettoget.data.CartDBHelper;
import com.nishant.yettoget.data.TaskContract;
import com.nishant.yettoget.data.WishDBHelper;

/**
 * Created by Pavilion on 10-07-2016.
 */
public class CartAdapter extends CursorAdapter{
    private static Context context;
    CartDBHelper helper;

    public CartAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        this.context = context;
        helper = new CartDBHelper(context);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_cart, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find Views to populate in inflated template
        TextView textView = (TextView) view.findViewById(R.id.list_item_task_textview_cart);
        Button done_button = (Button) view.findViewById(R.id.list_item_task_done_button_cart);
        //Button done_button = (Button) view.findViewById(R.id.wish);
        // Extract properties from cursor
        final String id = cursor.getString(ThreeFragment.COL_CART_ID);
        final String task = cursor.getString(ThreeFragment.COL_CART_NAME);

        // Populate views with extracted properties
        textView.setText(task);
        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create a SQL command for deleting a particular ID.
                String sql = String.format("DELETE FROM %s WHERE %s = '%s'",
                        TaskContract.CartEntry.TABLE_CART,
                        TaskContract.CartEntry._ID,
                        id);
                SQLiteDatabase sqlDB = helper.getWritableDatabase();
                //Execute the delete command
                sqlDB.execSQL(sql);
                notifyDataSetChanged();

                //Query database for updated data
                Cursor cursor = sqlDB.query(TaskContract.CartEntry.TABLE_CART,
                        new String[]{TaskContract.CartEntry._ID, TaskContract.CartEntry.CART_TASK},
                        null,null,null,null,null);
                //Instance method with TaskAdapter so no need to use adapter.swapCursor()
                swapCursor(cursor);
                Toast.makeText(context,"Removed",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
