package com.nishant.yettoget;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.nishant.yettoget.R;
import com.nishant.yettoget.data.CartDBHelper;
import com.nishant.yettoget.data.Recent;
import com.nishant.yettoget.data.TaskContract;
import com.nishant.yettoget.data.WishDBHelper;


public class ThreeFragment extends Fragment{
    CartAdapter mCartAdapter;
    static final int COL_CART_ID = 0;
    static final int COL_CART_NAME = 1;
    ListView listView;
    private boolean isViewShown = false;

    public ThreeFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_three, container, false);

        listView = (ListView) rootView.findViewById(R.id.listview_carts);
        if (!isViewShown) {
            fetchData();
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = ((CartAdapter) parent.getAdapter()).getCursor();
                cursor.moveToPosition(position);
                String string = cursor.getString(cursor.getColumnIndex(TaskContract.CartEntry.CART_TASK));
                Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();

            }
        });

        return rootView;
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
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Take Action!!");
        menu.add("Item Bought!");
        menu.add("Cancel");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(getUserVisibleHint()){
            if(item.getTitle()== "Add To Cart") {
                Toast.makeText(getActivity(), "Cart", Toast.LENGTH_SHORT).show();
            }
            if(item.getTitle()== "Item Bought!") {
                AdapterView.AdapterContextMenuInfo info=
                        (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                int itemPosition = info.position;
                Cursor cursor = (mCartAdapter).getCursor();
                cursor.moveToPosition(itemPosition);
                Recent helper1 = new Recent(getActivity());
                SQLiteDatabase sqlDBrecent = helper1.getReadableDatabase();
                CartDBHelper helper = new CartDBHelper(getActivity());
                SQLiteDatabase sqlDB = helper.getReadableDatabase();
                String inputTaskForRecent = cursor.getString(cursor.getColumnIndex(TaskContract.CartEntry.CART_TASK));
                /*cursor = sqlDB.query(TaskContract.TaskEntry.TABLE_NAME,
                        new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COLUMN_TASK},
                        null, null, null, null, null);*/
                ContentValues values = new ContentValues();
                values.clear();
                values.put(TaskContract.recent.RECENT_TASK, inputTaskForRecent);
                //Insert the values into the Table for Tasks
                sqlDBrecent.insertWithOnConflict(
                        TaskContract.recent.TABLE_RECENT,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_IGNORE);

                String sql = String.format("DELETE FROM %s WHERE %s = '%s'",
                        TaskContract.CartEntry.TABLE_CART,
                        TaskContract.CartEntry._ID,
                        cursor.getString(COL_CART_ID));

                //Execute the delete command
                sqlDB.execSQL(sql);
                mCartAdapter.notifyDataSetChanged();
                cursor = sqlDB.query(TaskContract.CartEntry.TABLE_CART,
                        new String[]{TaskContract.CartEntry._ID, TaskContract.CartEntry.CART_TASK},
                        null,null,null,null,null);
                //Instance method with TaskAdapter so no need to use adapter.swapCursor()
                mCartAdapter.swapCursor(cursor);
                Toast.makeText(getActivity(),"Removed", Toast.LENGTH_SHORT).show();

            }
            if(item.getTitle()== "Cancel") {
                Toast.makeText(getActivity(), "Cart", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onContextItemSelected(item);
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null) {
            isViewShown = true;
            // fetchdata() contains logic to show data when page is selected mostly asynctask to fill the data
            fetchData();
        } else {
            isViewShown = false;
        }

    }
    public void fetchData()
    {
        CartDBHelper helper = new CartDBHelper(getActivity());
        SQLiteDatabase sqlDB = helper.getReadableDatabase();
        //Query database to get any existing data
        Cursor cursor = sqlDB.query(TaskContract.CartEntry.TABLE_CART,
                new String[]{TaskContract.CartEntry._ID, TaskContract.CartEntry.CART_TASK},
                null, null, null, null, null);
        //Create a new TaskAdapter and bind it to ListView
        mCartAdapter = new CartAdapter(getActivity(), cursor);
        listView.setAdapter(mCartAdapter);
    }

}