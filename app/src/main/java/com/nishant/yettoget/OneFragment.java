package com.nishant.yettoget;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.nishant.yettoget.R;
import com.nishant.yettoget.data.CartDBHelper;
import com.nishant.yettoget.data.TaskContract;
import com.nishant.yettoget.data.TaskDBHelper;
import com.nishant.yettoget.data.WishDBHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class OneFragment extends Fragment {
    TaskAdapter mTaskAdapter;
    WishAdapter mWishAdapter;
    static final int COL_TASK_ID = 0;
    static final int COL_TASK_NAME = 1;
    ListView listView;
    //TaskDBHelper helper = new TaskDBHelper(getActivity());
    //SQLiteDatabase sqlDB = helper.getReadableDatabase();

    public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview= inflater.inflate(R.layout.fragment_one, container, false);
        FloatingActionButton mButton = (FloatingActionButton) rootview.findViewById(R.id.addTaskBtn);
        mButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add an item");
                builder.setMessage("What do you want to buy later?");
                final EditText inputField = new EditText(getActivity());
                builder.setView(inputField);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Get user input
                        String inputTask = inputField.getText().toString();

                        //Get DBHelper to write to database
                        TaskDBHelper helper = new TaskDBHelper(getActivity());
                        SQLiteDatabase db = helper.getWritableDatabase();

                        //Put in the values within a ContentValues.
                        ContentValues values = new ContentValues();
                        values.clear();
                        values.put(TaskContract.TaskEntry.COLUMN_TASK, inputTask);

                        //Insert the values into the Table for Tasks
                        db.insertWithOnConflict(
                                TaskContract.TaskEntry.TABLE_NAME,
                                null,
                                values,
                                SQLiteDatabase.CONFLICT_IGNORE);

                        //Query database again to get updated data
                        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE_NAME,
                                new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COLUMN_TASK},
                                null, null, null, null, null);

                        //Swap old data with new data for display
                        mTaskAdapter.swapCursor(cursor);
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.create().show();
            }
        });
       listView = (ListView) rootview.findViewById(R.id.listview_tasks);

        //Get DBHelper to read from database
        TaskDBHelper helper = new TaskDBHelper(getActivity());
        SQLiteDatabase sqlDB = helper.getReadableDatabase();

        //Query database to get any existing data
        Cursor cursor = sqlDB.query(TaskContract.TaskEntry.TABLE_NAME,
                new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COLUMN_TASK},
                null, null, null, null, null);

        //Create a new TaskAdapter and bind it to ListView
        mTaskAdapter = new TaskAdapter(getActivity(), cursor);
        listView.setAdapter(mTaskAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = ((TaskAdapter)parent.getAdapter()).getCursor();
                cursor.moveToPosition(position);
                String string = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK));
                Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();

            }
        });

        return rootview;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Take Action!!");
        menu.add("Add To WishList");
        menu.add("Add To Cart");
        menu.add("Remove");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(getUserVisibleHint()){
            if(item.getTitle()== "Remove") {
                AdapterView.AdapterContextMenuInfo info=
                        (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                int itemPosition = info.position;
                Cursor cursor = (mTaskAdapter).getCursor();
                cursor.moveToPosition(itemPosition);
               TaskDBHelper helper = new TaskDBHelper(getActivity());
                SQLiteDatabase sqlDB = helper.getReadableDatabase();
                /*cursor = sqlDB.query(TaskContract.TaskEntry.TABLE_NAME,
                        new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COLUMN_TASK},
                        null, null, null, null, null);*/

                String sql = String.format("DELETE FROM %s WHERE %s = '%s'",
                        TaskContract.TaskEntry.TABLE_NAME,
                        TaskContract.TaskEntry._ID,
                        cursor.getString(COL_TASK_ID));

                //Execute the delete command
                sqlDB.execSQL(sql);
                mTaskAdapter.notifyDataSetChanged();
                cursor = sqlDB.query(TaskContract.TaskEntry.TABLE_NAME,
                        new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COLUMN_TASK},
                        null,null,null,null,null);
                //Instance method with TaskAdapter so no need to use adapter.swapCursor()
                mTaskAdapter.swapCursor(cursor);
                    Toast.makeText(getActivity(),"Removed", Toast.LENGTH_SHORT).show();

            }
            if(item.getTitle()== "Add To WishList") {
                AdapterView.AdapterContextMenuInfo info=
                        (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                int itemPosition = info.position;
                Cursor cursor = (mTaskAdapter).getCursor();
                cursor.moveToPosition(itemPosition);
                TaskDBHelper helper = new TaskDBHelper(getActivity());
                SQLiteDatabase sqlDB = helper.getReadableDatabase();
                //getting the string of the data
                String inputTask = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK));
                //ading string to the WIsh DataBase
                WishDBHelper helper1 =new WishDBHelper(getActivity());
                SQLiteDatabase sqldb1 = helper1.getReadableDatabase();
                //Put in the values within a ContentValues.
                ContentValues values = new ContentValues();
                values.clear();
                values.put(TaskContract.WishEntry.WISH_TASK, inputTask);
                //Insert the values into the Table for Tasks
                sqldb1.insertWithOnConflict(
                        TaskContract.WishEntry.WISH_TABLE,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_IGNORE);



                //deleting that string from the old database
                String sql = String.format("DELETE FROM %s WHERE %s = '%s'",
                        TaskContract.TaskEntry.TABLE_NAME,
                        TaskContract.TaskEntry._ID,
                        cursor.getString(COL_TASK_ID));

                //Execute the delete command
                sqlDB.execSQL(sql);
                mTaskAdapter.notifyDataSetChanged();
                cursor = sqlDB.query(TaskContract.TaskEntry.TABLE_NAME,
                        new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COLUMN_TASK},
                        null,null,null,null,null);
                //Instance method with TaskAdapter so no need to use adapter.swapCursor()
                mTaskAdapter.swapCursor(cursor);
                Toast.makeText(getActivity(),"Removed and added to WishList", Toast.LENGTH_SHORT).show();

            }
            if(item.getTitle()== "Add To Cart") {
                AdapterView.AdapterContextMenuInfo info=
                        (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                int itemPosition = info.position;
                Cursor cursor = (mTaskAdapter).getCursor();
                cursor.moveToPosition(itemPosition);
                TaskDBHelper helper = new TaskDBHelper(getActivity());
                SQLiteDatabase sqlDB = helper.getReadableDatabase();
                //getting the string of the data
                String inputTask = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK));
                //ading string to the WIsh DataBase
                CartDBHelper helper2 =new CartDBHelper(getActivity());
                SQLiteDatabase sqldb2 = helper2.getReadableDatabase();
                //Put in the values within a ContentValues.
                ContentValues values = new ContentValues();
                values.clear();
                values.put(TaskContract.CartEntry.CART_TASK, inputTask);
                //Insert the values into the Table for Tasks
                sqldb2.insertWithOnConflict(
                        TaskContract.CartEntry.TABLE_CART,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_IGNORE);



                //deleting that string from the old database
                String sql = String.format("DELETE FROM %s WHERE %s = '%s'",
                        TaskContract.TaskEntry.TABLE_NAME,
                        TaskContract.TaskEntry._ID,
                        cursor.getString(COL_TASK_ID));

                //Execute the delete command
                sqlDB.execSQL(sql);
                mTaskAdapter.notifyDataSetChanged();
                cursor = sqlDB.query(TaskContract.TaskEntry.TABLE_NAME,
                        new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COLUMN_TASK},
                        null,null,null,null,null);
                //Instance method with TaskAdapter so no need to use adapter.swapCursor()
                mTaskAdapter.swapCursor(cursor);
                Toast.makeText(getActivity(), "Added to the Cart", Toast.LENGTH_SHORT).show();
            }

                return true;
        }
        return super.onContextItemSelected(item);

    }
    public void onResume()
    {
        super.onResume();
        registerForContextMenu(listView);
    }
    @Override
    public void onPause()
    {
        unregisterForContextMenu(listView);
        super.onPause();
    }
}