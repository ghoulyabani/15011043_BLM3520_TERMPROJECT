package com.example.calendarapp2;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CustomCalendarView extends LinearLayoutCompat {
    ImageButton btnNext, btnPrev;
    TextView currentDate;
    GridView gridView;
    private static final int MAX_CALENDAR_DAYS = 42;
    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    Context context;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
    SimpleDateFormat eventDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    GridAdapter gridAdapter;
    Spinner spnFreq;
    AlertDialog alertDialog;
    List<Date> dates = new ArrayList<>();
    List<Events> eventList = new ArrayList<>();

    public CustomCalendarView(Context context) {
        super(context);
    }

    public CustomCalendarView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initializeLayout();
        SetUpCalendar();

        spnFreq =  findViewById(R.id.spnFreq);


        btnPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, -1);
                SetUpCalendar();
            }
        });

        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, 1);
                SetUpCalendar();
                gridView.getOnItemClickListener();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                final View addView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_new_event_layout, null);
                final EditText edtCaption = addView.findViewById(R.id.edtCaption);
                final EditText edtDesc = addView.findViewById(R.id.edtDescription);
                final ImageButton startTime = addView.findViewById(R.id.setStartTime);
                ImageButton endTime = addView.findViewById(R.id.setEndTime);
                final Spinner spnFreq = addView.findViewById(R.id.spnFreq);
                final Button btnAdd = addView.findViewById(R.id.btnEkle);
                final TextView txtStart = addView.findViewById(R.id.txtStart);
                final TextView txtEnd = addView.findViewById(R.id.txtEnd);
                Button btnReminder = addView.findViewById(R.id.btnReminder);
                ListView reminderListView = addView.findViewById(R.id.listReminder);
                final List<String> reminderListString = new ArrayList<>();
                final ArrayAdapter<String> adapter;
                final SharedPreferences sharedPreferencesDefault = context.getSharedPreferences("SHARED_PREFERENCES", context.MODE_PRIVATE);

                String selectedSpinnerItem = sharedPreferencesDefault.getString("spinner", "Asla");

                if (selectedSpinnerItem.equals("Asla")){
                    spnFreq.setSelection(0);
                }
                else if (selectedSpinnerItem.equals("Günlük")){
                    spnFreq.setSelection(1);
                }
                else if (selectedSpinnerItem.equals("Haftalık")){
                    spnFreq.setSelection(2);
                }
                else if (selectedSpinnerItem.equals("Aylık")){
                    spnFreq.setSelection(3);
                }
                else if (selectedSpinnerItem.equals("Yıllık")){
                    spnFreq.setSelection(4);
                }

                adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, reminderListString);
                reminderListView.setAdapter(adapter);

                reminderListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(context, "Hatırlatıcı silindi.", Toast.LENGTH_SHORT).show();
                        reminderListString.remove(position);
                        adapter.notifyDataSetChanged();
                        return true;
                    }
                });

                btnReminder.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog alertDialogTime;
                        AlertDialog.Builder addTimeBuilder = new AlertDialog.Builder(context);
                        addTimeBuilder.setCancelable(true);
                        View timeView = LayoutInflater.from(parent.getContext()).inflate(R.layout.remind_timer_layout, null);
                        Button btnAddTime = timeView.findViewById(R.id.btnAddTime);
                        RadioGroup radioGroup = timeView.findViewById(R.id.radioGroup);
                        final RadioButton rbtnMinute = timeView.findViewById(R.id.rbtnMinute);
                        final RadioButton rbtnHour = timeView.findViewById(R.id.rbtnHour);
                        final RadioButton rbtnDay = timeView.findViewById(R.id.rbtnDay);
                        final EditText edtReminder = timeView.findViewById(R.id.edtRemind);
                        final String[] remindBefore = {"dakika"};

                        addTimeBuilder.setView(timeView);
                        alertDialogTime = addTimeBuilder.create();
                        alertDialogTime.show();

                        String defaultTime = sharedPreferencesDefault.getString("reminder", "30");
                        edtReminder.setText(defaultTime);

                        String defaultInterval = sharedPreferencesDefault.getString("radioButton", "Dakika");
                        if (defaultInterval.equals("Dakika")){
                            rbtnMinute.setChecked(true);
                        }
                        else if (defaultInterval.equals("Saat")){
                            rbtnHour.setChecked(true);
                        }
                        else if (defaultInterval.equals("Gün")){
                            rbtnDay.setChecked(true);
                        }


                        btnAddTime.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (rbtnDay.isChecked()){
                                    remindBefore[0] = edtReminder.getText().toString() + " gün";
                                }
                                else if (rbtnHour.isChecked()){
                                    remindBefore[0] = edtReminder.getText().toString() + " saat";
                                }
                                else if (rbtnMinute.isChecked()){
                                    remindBefore[0] = edtReminder.getText().toString() + " dakika";
                                }
                                if (rbtnDay.isChecked() || rbtnHour.isChecked() || rbtnMinute.isChecked()){
                                    if (!edtReminder.getText().toString().matches("")){
                                        reminderListString.add(remindBefore[0]);
                                        adapter.notifyDataSetChanged();
                                        alertDialogTime.dismiss();
                                    }
                                    else {
                                        Toast.makeText(context, "Lütfen zamanı tam giriniz.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    Toast.makeText(context, "Lütfen zamanı tam giriniz.", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });


                    }
                });

                startTime.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        int hours = calendar.get(Calendar.HOUR_OF_DAY);
                        int minutes = calendar.get(Calendar.MINUTE);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(addView.getContext(), R.style.Theme_AppCompat_Dialog,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        Calendar c = Calendar.getInstance();
                                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        c.set(Calendar.MINUTE, minute);
                                        c.setTimeZone(TimeZone.getDefault());
                                        SimpleDateFormat hformat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
                                        String startT = hformat.format(c.getTime());
                                        txtStart.setText(startT);
                                    }
                                }, hours, minutes, true);
                        timePickerDialog.show();
                    }
                });

                endTime.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        int hours = calendar.get(Calendar.HOUR_OF_DAY);
                        int minutes = calendar.get(Calendar.MINUTE);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(addView.getContext(), R.style.Theme_AppCompat_Dialog,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        Calendar c = Calendar.getInstance();
                                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        c.set(Calendar.MINUTE, minute);
                                        c.setTimeZone(TimeZone.getDefault());
                                        SimpleDateFormat hformat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
                                        String endT = hformat.format(c.getTime());
                                        txtEnd.setText(endT);
                                    }
                                }, hours, minutes, true);
                        timePickerDialog.show();
                    }
                });

                final String date = eventDateFormat.format(dates.get(position));
                final String month = monthFormat.format(dates.get(position));
                final String year = yearFormat.format(dates.get(position));

                btnAdd.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!reminderListString.isEmpty()){
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            formatter.setLenient(false);
                            String dateTime = date + " " + txtStart.getText().toString();
                            Date selectedDate = null;
                            try {
                                selectedDate = formatter.parse(dateTime);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Long dateInMS = selectedDate.getTime();
                            long timeToSubtract = 0;
                            long notifyInMS;
                            for (String remind : reminderListString){
                                if (remind.contains(" dakika")){
                                    String[] minute = remind.split(" ");
                                    timeToSubtract = Long.parseLong(minute[0]) * 60 * 1000;
                                }
                                else if (remind.contains(" saat")){
                                    String[] hour = remind.split(" ");
                                    timeToSubtract = Long.parseLong(hour[0]) * 60 * 60 * 1000;
                                }
                                else if (remind.contains(" gün")){
                                    String[] day = remind.split(" ");
                                    timeToSubtract = Long.parseLong(day[0]) * 24 * 60 * 60 * 1000;
                                }
                                notifyInMS = dateInMS - timeToSubtract;
                                Intent intent = new Intent(context,ReminderHelper.class);
                                intent.putExtra("Event", edtCaption.getText().toString());
                                intent.putExtra("Time", remind);

                                SharedPreferences sharedPreferences = context.getSharedPreferences("SHARED_PREFERENCES", context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                int intendID = sharedPreferences.getInt("INTENTID", 0);
                                editor.putInt("INTENTID", intendID + 1);
                                editor.apply();

                                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, intendID, intent, 0);
                                AlarmManager alarmManager =(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, notifyInMS, pendingIntent);
                            }
                        }
                        if (reminderListString.isEmpty()){
                            SaveEvent(edtCaption.getText().toString(), date, txtStart.getText().toString(),
                                    txtEnd.getText().toString(), month, year, edtDesc.getText().toString(),
                                    spnFreq.getSelectedItem().toString(), "Hatırlatıcı Yok");
                        }
                        else{
                            SaveEvent(edtCaption.getText().toString(), date, txtStart.getText().toString(),
                                    txtEnd.getText().toString(), month, year, edtDesc.getText().toString(),
                                    spnFreq.getSelectedItem().toString(), convertListToString(reminderListString));
                        }

                        SetUpCalendar();
                        alertDialog.dismiss();
                    }
                });

                builder.setView(addView);
                alertDialog = builder.create();
                alertDialog.show();
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String Date = eventDateFormat.format(dates.get(position));
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                View showView = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_events_layout, null);
                RecyclerView recyclerView = showView.findViewById(R.id.EventsRV);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(showView.getContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                EventRecyclerAdapter eventRecyclerAdapter = new EventRecyclerAdapter(showView.getContext(),
                        collectEventsByDate(Date));
                recyclerView.setAdapter(eventRecyclerAdapter);
                eventRecyclerAdapter.notifyDataSetChanged();

                builder.setView(showView);
                alertDialog = builder.create();
                alertDialog.show();
                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        SetUpCalendar();
                    }
                });

                return true;
            }
        });



    }


    private ArrayList<Events> collectEventsByDate(String date){
        ArrayList<Events> arrayList = new ArrayList<>();
        DBOpenHelper dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadEvents(date, database);
        while (cursor.moveToNext()){
            String event = cursor.getString(cursor.getColumnIndex(DBStruct.EVENT));
            String detail = cursor.getString(cursor.getColumnIndex(DBStruct.DETAIL));
            String startTime = cursor.getString(cursor.getColumnIndex(DBStruct.STARTTIME));
            String endTime = cursor.getString(cursor.getColumnIndex(DBStruct.FINISHTIME));
            String remindTimer = cursor.getString(cursor.getColumnIndex(DBStruct.REMINDTIMER));
            String repeat = cursor.getString(cursor.getColumnIndex(DBStruct.REPEAT));
            String address = cursor.getString(cursor.getColumnIndex(DBStruct.ADRESS));
            String cMonth = cursor.getString(cursor.getColumnIndex(DBStruct.MONTH));
            String cYear = cursor.getString(cursor.getColumnIndex(DBStruct.YEAR));
            String Date = cursor.getString(cursor.getColumnIndex(DBStruct.DATE));
            Events events = new Events(event, detail, startTime, endTime, remindTimer, repeat, Date, cMonth, cYear, address);
            arrayList.add(events);
        }
        cursor.close();
        dbOpenHelper.close();
        return  arrayList;
    }

    public CustomCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    private void SaveEvent(String event, String date, String startTime, String endTime, String month
    , String year, String detail, String repeat, String remindTimer){
        DBOpenHelper dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        dbOpenHelper.SaveEvent(event, detail, startTime, endTime, remindTimer, repeat, date,
                month, year, null, database);
        dbOpenHelper.close();
        Toast.makeText(context, "Etkinlik Kaydedildi", Toast.LENGTH_SHORT).show();
    }

    private void initializeLayout(){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calendar_layout, this);
        btnNext = view.findViewById(R.id.btnNext);
        btnPrev = view.findViewById(R.id.btnPrev);
        currentDate = view.findViewById(R.id.txtCurrentDate);
        gridView = view.findViewById(R.id.gridView);
    }

    private void SetUpCalendar(){
        String crntDate = dateFormat.format(calendar.getTime());
        currentDate.setText(crntDate);
        dates.clear();
        Calendar monthCalendar = (Calendar)calendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth);
        CollectEventsPerMonth(monthFormat.format(calendar.getTime()), yearFormat.format(calendar.getTime()));

        while(dates.size() < MAX_CALENDAR_DAYS){
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        gridAdapter = new GridAdapter(context, dates, calendar, eventList);
        gridView.setAdapter(gridAdapter);
    }

    private void CollectEventsPerMonth(String month, String year){
        eventList.clear();
        DBOpenHelper dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadEventsPerMonth(month, year, database);

        while (cursor.moveToNext()){
            String event = cursor.getString(cursor.getColumnIndex(DBStruct.EVENT));
            String detail = cursor.getString(cursor.getColumnIndex(DBStruct.DETAIL));
            String startTime = cursor.getString(cursor.getColumnIndex(DBStruct.STARTTIME));
            String endTime = cursor.getString(cursor.getColumnIndex(DBStruct.FINISHTIME));
            String remindTimer = cursor.getString(cursor.getColumnIndex(DBStruct.REMINDTIMER));
            String repeat = cursor.getString(cursor.getColumnIndex(DBStruct.REPEAT));
            String address = cursor.getString(cursor.getColumnIndex(DBStruct.ADRESS));
            String cMonth = cursor.getString(cursor.getColumnIndex(DBStruct.MONTH));
            String cYear = cursor.getString(cursor.getColumnIndex(DBStruct.YEAR));
            String date = cursor.getString(cursor.getColumnIndex(DBStruct.DATE));
            Events events = new Events(event, detail, startTime, endTime, remindTimer, repeat, date, cMonth, cYear, address);
            eventList.add(events);
        }
        cursor.close();
        dbOpenHelper.close();
    }

    public static String convertListToString(List<String> stringList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : stringList) {
            stringBuilder.append(str).append(",");
        }

        stringBuilder.setLength(stringBuilder.length() - ",".length());

        return stringBuilder.toString();
    }

    public static List<String> convertStringToList(String str) {
        return Arrays.asList(str.split(","));
    }


}
