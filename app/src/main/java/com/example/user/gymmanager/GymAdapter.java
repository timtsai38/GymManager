package com.example.user.gymmanager;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.user.gymmanager.database.GymContract;

/**
 * Created by User on 2018/4/4.
 */

public class GymAdapter extends RecyclerView.Adapter<GymAdapter.GymViewHolder> {
    private Cursor mCursor;

    private OnCheckedListener mOnCheckedListener;

    public GymAdapter(OnCheckedListener onCheckedListener) {
        mOnCheckedListener = onCheckedListener;
    }

    private GymTask getGymTask(int id, String title, String time, boolean isEnabled){
        return new GymTask()
                .setId(id)
                .setTitle(title)
                .setTime(time)
                .setIsEnabled(isEnabled);
    }

    public void swapCursor(Cursor currentCusor){
        mCursor = currentCusor;
        notifyDataSetChanged();
    }

    public interface OnCheckedListener {
        void onChecked(GymTask gymTask);
    }

    @Override
    public GymViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_gym, parent, false);

        return new GymViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GymViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        GymTask gymTask = getGymTask(
                mCursor.getInt(mCursor.getColumnIndex(GymContract.GymEntry._ID)),
                mCursor.getString(mCursor.getColumnIndex(GymContract.GymEntry.TITLE)),
                mCursor.getString(mCursor.getColumnIndex(GymContract.GymEntry.TIME)),
                mCursor.getInt(mCursor.getColumnIndex(GymContract.GymEntry.IS_ENABLED)) > 0);

        holder.mGymTaskTitleTextView.setText(gymTask.getTitle());
        holder.mGymTaskTimeTextView.setText(gymTask.getTime());
        holder.mGymTaskDoneCheckBox.setChecked(gymTask.getIsEnabled());

        setIdTag(holder);
    }

    private void setIdTag(GymViewHolder holder) {
        int idColumnIndex = mCursor.getColumnIndex(GymContract.GymEntry._ID);
        int id = mCursor.getInt(idColumnIndex);
        holder.itemView.setTag(id);
    }

    @Override
    public int getItemCount() {
        return (mCursor != null) ? mCursor.getCount() : 0;
    }

    class GymViewHolder extends RecyclerView.ViewHolder{

        private TextView mGymTaskTitleTextView;
        private TextView mGymTaskTimeTextView;
        private CheckBox mGymTaskDoneCheckBox;

        public GymViewHolder(View itemView) {
            super(itemView);

            mGymTaskTitleTextView = (TextView) itemView.findViewById(R.id.tv_gym_task_title);
            mGymTaskTimeTextView = (TextView) itemView.findViewById(R.id.tv_gym_task_time);
            mGymTaskDoneCheckBox = (CheckBox) itemView.findViewById(R.id.cb_gym_task_is_enabled);

            mGymTaskDoneCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mCursor.moveToPosition(getAdapterPosition());

                    GymTask gymTask = getGymTask(
                            mCursor.getInt(mCursor.getColumnIndex(GymContract.GymEntry._ID)),
                            mCursor.getString(mCursor.getColumnIndex(GymContract.GymEntry.TITLE)),
                            mCursor.getString(mCursor.getColumnIndex(GymContract.GymEntry.TIME)),
                            isChecked);

                    mOnCheckedListener.onChecked(gymTask);
                }
            });
        }
    }
}
