package com.example.calendarapp2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EventRecyclerAdapter extends RecyclerView.Adapter<EventRecyclerAdapter.MyViewHolder> {

    Context context;
    ArrayList<Events> arrayList;
    DBOpenHelper dbOpenHelper;

    public EventRecyclerAdapter(Context context, ArrayList<Events> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_events_row_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final Events events = arrayList.get(position);
        holder.event.setText(events.getName());
        holder.detail.setText(events.getDetail());
        holder.startTime.setText(events.getStartTime());
        holder.endTime.setText(events.getFinishTime());
        holder.remindTimer.setText(events.getRemindTimer());
        holder.repeat.setText(events.getRepeat());
        holder.date.setText(events.Date());
        holder.address.setText(events.getAdress());
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCalendarEvent(events.getName(), events.Date(), events.getStartTime(), events.getFinishTime());
                arrayList.remove(position);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView event, detail, date, startTime, endTime, remindTimer, repeat, address;
        Button btnDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            event = itemView.findViewById(R.id.event_name);
            date = itemView.findViewById(R.id.event_date);
            detail = itemView.findViewById(R.id.event_desc);
            startTime = itemView.findViewById(R.id.event_start);
            endTime = itemView.findViewById(R.id.event_finish);
            remindTimer = itemView.findViewById(R.id.event_remind);
            repeat = itemView.findViewById(R.id.event_repeat);
            address = itemView.findViewById(R.id.event_address);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    private void deleteCalendarEvent(String event, String date, String startTime, String endTime){
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.DeleteEvent(event, date, startTime, endTime, sqLiteDatabase);
        dbOpenHelper.close();
        Toast.makeText(context, "Etkinlik Silindi", Toast.LENGTH_SHORT).show();
    }
}
