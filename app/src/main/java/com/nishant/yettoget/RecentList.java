package com.nishant.yettoget;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.nishant.yettoget.data.Recent;
import com.nishant.yettoget.data.TaskContract;
import com.nishant.yettoget.data.TaskDBHelper;

public class RecentList extends AppCompatActivity {

    RecentAdapter recentAdapter;
    static final int COL_RECENT_NAME = 1;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_list);
        listView=(ListView)findViewById(R.id.recentlistView);

        Recent helper = new Recent(getApplicationContext());
        SQLiteDatabase sqlDB = helper.getReadableDatabase();

        //Query database to get any existing data

        Cursor cursor = sqlDB.query(TaskContract.recent.TABLE_RECENT,
                new String[]{TaskContract.recent._ID, TaskContract.recent.RECENT_TASK},
                null,null,null,null,null);


        //Create a new TaskAdapter and bind it to ListView
        recentAdapter = new RecentAdapter(this, cursor);
        listView.setAdapter(recentAdapter);
    }
}
