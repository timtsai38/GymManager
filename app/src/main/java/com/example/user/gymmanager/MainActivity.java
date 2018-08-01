package com.example.user.gymmanager;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.user.gymmanager.Utilities.TimeParserUtilities;
import com.example.user.gymmanager.database.GymContract;
import com.example.user.gymmanager.schedule.GymTaskFirebaseJobUtilities;

public class MainActivity extends AppCompatActivity implements GymAdapter.OnCheckedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int GYM_TASKS_LOADER_ID = 87;

    private RecyclerView mGymRecyclerView;

    private GymAdapter mGymAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolBar();

        setRecyclerView();
        swipeDeleteGymTask();

        getLoaderManager().initLoader(GYM_TASKS_LOADER_ID, null, this);
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setRecyclerView() {
        mGymRecyclerView = (RecyclerView) findViewById(R.id.rv_gym);
        mGymRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mGymAdapter = new GymAdapter(this);
        mGymRecyclerView.setAdapter(mGymAdapter);
    }

    private void startJobSchedule(int gymTaskId, int gymTaskTimeHour, int gymTaskTimeMinute) {
        GymTaskFirebaseJobUtilities.scheduleFirstGymReminder(this, gymTaskId, gymTaskTimeHour, gymTaskTimeMinute);
    }

    private void cancelJobSchedule(int gymTaskId) {
        GymTaskFirebaseJobUtilities.cancelScheduleFirstGymReminder(this, gymTaskId);
        GymTaskFirebaseJobUtilities.cancelScheduleGymReminder(this, gymTaskId);
    }

    private void swipeDeleteGymTask(){
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int id = (int) viewHolder.itemView.getTag();
                String idString = Integer.toString(id);

                Uri uri = GymContract.CONTENT_URI
                        .buildUpon()
                        .appendPath(idString)
                        .build();

                getContentResolver().delete(uri, null, null);

                getLoaderManager().restartLoader(GYM_TASKS_LOADER_ID, null, MainActivity.this);
            }
        }).attachToRecyclerView(mGymRecyclerView);
    }

    public void onAddButtonClicked(View view) {
        createDialog().show();
    }

    private AlertDialog createDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        View contentView = inflater.inflate(R.layout.alert_add_gym_task, null);

        builder.setMessage(R.string.message_add_task)
                .setView(contentView);

        final EditText titleEditText = (EditText) contentView.findViewById(R.id.et_gym_task_title);
        final TimePicker timePicker = (TimePicker) contentView.findViewById(R.id.tp_gym_task_time);

        return builder.setPositiveButton(getString(R.string.message_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = titleEditText.getText().toString();
                String time = timePicker.getHour() + TimeParserUtilities.SEPARATOR + timePicker.getMinute();

                ContentValues values = new ContentValues();
                values.put(GymContract.GymEntry.TITLE, title);
                values.put(GymContract.GymEntry.TIME, time);
                values.put(GymContract.GymEntry.IS_ENABLED, 1);

                Uri id = getContentResolver().insert(GymContract.CONTENT_URI, values);
                getLoaderManager().restartLoader(GYM_TASKS_LOADER_ID, null, MainActivity.this);

                int gymTaskId = (int) ContentUris.parseId(id);
                int gymTasKTimeHour = timePicker.getHour();
                int gymTasKTimeMinute = timePicker.getMinute();

                startJobSchedule(gymTaskId, gymTasKTimeHour, gymTasKTimeMinute);
            }
        }).setNegativeButton(R.string.message_cancel, null).create();
    }

    @Override
    public void onChecked(GymTask gymTask) {
        int gymTaskId = gymTask.getId();
        Uri uri = ContentUris.withAppendedId(GymContract.CONTENT_URI, gymTaskId);

        ContentValues values = new ContentValues();
        values.put(GymContract.GymEntry.TITLE, gymTask.getTitle());
        values.put(GymContract.GymEntry.TIME, gymTask.getTime());
        values.put(GymContract.GymEntry.IS_ENABLED, gymTask.getIsEnabled() ? 1 : 0);
        getContentResolver().update(uri, values, null, null);

        int gymTaskTimeHour = TimeParserUtilities.parseHour(gymTask.getTime());
        int gymTaskTimeMinute = TimeParserUtilities.parseMinute(gymTask.getTime());

        if(gymTask.getIsEnabled()){
            startJobSchedule(gymTaskId, gymTaskTimeHour, gymTaskTimeMinute);
        }else{
            cancelJobSchedule(gymTaskId);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String orderByTime = GymContract.GymEntry.TIME + " ASC";

        if(id == GYM_TASKS_LOADER_ID){
            return new CursorLoader(this, GymContract.CONTENT_URI, null, null, null, orderByTime);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        if(id == GYM_TASKS_LOADER_ID){
            mGymAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        int id = loader.getId();
        if(id == GYM_TASKS_LOADER_ID){
            mGymAdapter.swapCursor(null);
        }
    }
}
