package com.nishant.yettoget;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.FacebookDialog;
import com.facebook.messenger.ShareToMessengerParamsBuilder;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.nishant.yettoget.R;
import com.nishant.yettoget.data.CartDBHelper;
import com.nishant.yettoget.data.TaskContract;
import com.nishant.yettoget.data.TaskDBHelper;
import com.nishant.yettoget.data.WishDBHelper;


public class TwoFragment extends Fragment{

    WishAdapter mWishAdapter;
    static final int COL_WISH_ID = 0;
    static final int COL_WISH_NAME = 1;
    ListView listView;
    private boolean isViewShown = false;
    private Bitmap image;
    FloatingActionButton mButton;
    View rootView;
    private ShareButton shareButton;
    //counter
    private int counter = 0;



    public TwoFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_two, container, false);
       ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentTitle("YetToGet")
                .setContentDescription("My YetToGet WishList")
                .build();
        shareButton = (ShareButton) rootView.findViewById(R.id.share_button);
        shareButton.setShareContent(content);
        /* shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });*/
        listView = (ListView) rootView.findViewById(R.id.listview_wishs);
        if (!isViewShown) {
            fetchData();
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = ((WishAdapter) parent.getAdapter()).getCursor();
                cursor.moveToPosition(position);
                String string = cursor.getString(cursor.getColumnIndex(TaskContract.WishEntry.WISH_TASK));
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
        menu.add("Add To Cart");
        menu.add("Remove");
        menu.add("Cancel");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(getUserVisibleHint()){
            if(item.getTitle()== "Add To Cart") {
                AdapterView.AdapterContextMenuInfo info=
                        (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                int itemPosition = info.position;
                Cursor cursor = (mWishAdapter).getCursor();
                cursor.moveToPosition(itemPosition);
                WishDBHelper helper = new WishDBHelper(getActivity());
                SQLiteDatabase sqlDB = helper.getReadableDatabase();
                //getting the string of the data
                String inputTask = cursor.getString(cursor.getColumnIndex(TaskContract.WishEntry.WISH_TASK));
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
                String sql1 = String.format("DELETE FROM %s WHERE %s = '%s'",
                        TaskContract.WishEntry.WISH_TABLE,
                        TaskContract.WishEntry._ID,
                        cursor.getString(COL_WISH_ID));

                //Execute the delete command
                sqlDB.execSQL(sql1);
                mWishAdapter.notifyDataSetChanged();
                cursor = sqlDB.query(TaskContract.WishEntry.WISH_TABLE,
                        new String[]{TaskContract.WishEntry._ID, TaskContract.WishEntry.WISH_TASK},
                        null,null,null,null,null);
                //Instance method with TaskAdapter so no need to use adapter.swapCursor()
                mWishAdapter.swapCursor(cursor);
                Toast.makeText(getActivity(), "Done" , Toast.LENGTH_SHORT).show();

            }
            if(item.getTitle()== "Remove") {
                AdapterView.AdapterContextMenuInfo info=
                        (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                int itemPosition = info.position;
                Cursor cursor = (mWishAdapter).getCursor();
                cursor.moveToPosition(itemPosition);
                WishDBHelper helper = new WishDBHelper(getActivity());
                SQLiteDatabase sqlDB = helper.getReadableDatabase();
                /*cursor = sqlDB.query(TaskContract.TaskEntry.TABLE_NAME,
                        new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COLUMN_TASK},
                        null, null, null, null, null);*/

                String sql = String.format("DELETE FROM %s WHERE %s = '%s'",
                        TaskContract.WishEntry.WISH_TABLE,
                        TaskContract.WishEntry._ID,
                        cursor.getString(COL_WISH_ID));

                //Execute the delete command
                sqlDB.execSQL(sql);
                mWishAdapter.notifyDataSetChanged();
                cursor = sqlDB.query(TaskContract.WishEntry.WISH_TABLE,
                        new String[]{TaskContract.WishEntry._ID, TaskContract.WishEntry.WISH_TASK},
                        null,null,null,null,null);
                //Instance method with TaskAdapter so no need to use adapter.swapCursor()
                mWishAdapter.swapCursor(cursor);
                Toast.makeText(getActivity(),"Removed", Toast.LENGTH_SHORT).show();

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
    public void fetchData() {
        WishDBHelper helper = new WishDBHelper(getActivity());
        SQLiteDatabase sqlDB = helper.getReadableDatabase();
        //Query database to get any existing data
        Cursor cursor = sqlDB.query(TaskContract.WishEntry.WISH_TABLE,
                new String[]{TaskContract.WishEntry._ID, TaskContract.WishEntry.WISH_TASK},
                null, null, null, null, null);
        //Create a new TaskAdapter and bind it to ListView
        mWishAdapter = new WishAdapter(getActivity(), cursor);
        listView.setAdapter(mWishAdapter);
    }
    /*public void shareWishList(){
        //check counter
        if(counter == 0) {
            //save the screenshot

            rootView.setDrawingCacheEnabled(true);
            // creates immutable clone of image
            image = Bitmap.createBitmap(rootView.getDrawingCache());
            // destroy
            rootView.destroyDrawingCache();

            //share dialog
            AlertDialog.Builder shareDialog = new AlertDialog.Builder(getActivity());
            shareDialog.setTitle("Share Screen Shot");
            shareDialog.setMessage("Share image to Facebook?");
            shareDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //share the image to Facebook
                    SharePhoto photo = new SharePhoto.Builder().setBitmap(image).build();
                    SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
                    shareButton.setShareContent(content);
                    counter = 1;
                    shareButton.performClick();
                }
            });
            shareDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            shareDialog.show();
        }
        else {
            counter = 0;
            shareButton.setShareContent(null);
        }
    }
    public void setShareContent(final ShareContent shareContent) {
        this.shareContent = shareContent;
        if (!enabledExplicitlySet) {
            internalSetEnabled(canShare());
        }
    }*/
}