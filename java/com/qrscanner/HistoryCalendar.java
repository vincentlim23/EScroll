package com.qrscanner;

/**
 * Created by wllim on 4/4/16.
 */

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TableRow;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import sun.bob.mcalendarview.MarkStyle;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.mCalendarView;
import sun.bob.mcalendarview.vo.DateData;
import sun.bob.mcalendarview.vo.MarkedDates;

import sun.bob.mcalendarview.CellConfig;
import sun.bob.mcalendarview.listeners.OnExpDateClickListener;
import sun.bob.mcalendarview.listeners.OnMonthScrollListener;
import sun.bob.mcalendarview.views.ExpCalendarView;


public class HistoryCalendar extends AppCompatActivity {

    ListView lvcalendar, hiddenlv;
    Bitmap mBitmap;
    //mCalendarView calendarView;
    private TextView YearMonthTv;
    private ExpCalendarView expCalendarView;
    TextView tv;
    public static final int NOTIFICATION_ID=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expandqrhistorycalendar);
        expCalendarView = ((ExpCalendarView) findViewById(R.id.calendar_exp));
        YearMonthTv = (TextView) findViewById(R.id.main_YYMM_Tv);
        lvcalendar = (ListView) findViewById(R.id.listViewCalendar);
        tv = (TextView) findViewById(R.id.TextViewDateSelected);

        expCalendarView.getMarkedDates().removeAdd();

         initiateCalendar();
    }

    private void initiateCalendar(){
        Bundle extras = getIntent().getExtras();
        final Calendar dateNow = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String Datenow;
        Datenow = sdf.format(dateNow.getTime());

        final String filename = extras.getString("filename");
        final File dir = Environment.getExternalStorageDirectory();
        // File historyFile = new File(dir, "/QrHistoryFolder/" + filename + ".txt");
        File Filefolder = new File(dir, "/QrHistoryFolder/" + filename);
        for (File f : Filefolder.listFiles()) {
            if (f.isFile() && f.length() != 0) {
                if (f.getName().endsWith(".txt")) {
                    String txtname = f.getName()
                            .replace(".txt", "").replace("-", "");
                    DateFormat textformat = new SimpleDateFormat("yyyy-MM-dd");
                    int year = Integer.valueOf(txtname.substring(0, 4));
                    int month = Integer.valueOf(txtname.substring(4, 6));
                    int day = Integer.valueOf(txtname.substring(6, 8));

                    // only not today date is marked
                    String DateFile =f.getName().replace(".txt","");
                    if(!(DateFile.equals(Datenow))){
                        //calendarView.setDateCell(R.layout.layout_date_cell).setMarkedCell(R.layout.layout_mark_cell).setMarkedStyle(MarkStyle.DOT,Color.RED).markDate(year,month,day);
                        expCalendarView.setMarkedStyle(MarkStyle.DOT, Color.RED).markDate(year, month, day);
                    }
                }
            } else {
                f.delete();

            }
        }
        final String monthName=formatMonth((Calendar.getInstance().get(Calendar.MONTH) + 1));
        YearMonthTv.setText( Calendar.getInstance().get(Calendar.YEAR) +" "+ monthName);
        //YearMonthTv.setText(Calendar.getInstance().get(Calendar.YEAR) + "-" + (Calendar.getInstance().get(Calendar.MONTH) + 1));
        //int dayOfWeek= Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        //dayOfWeek=dayOfWeek-1;
        SimpleDateFormat format2=new SimpleDateFormat("EEEE");
        String dayIs= format2.format(Calendar.getInstance().getTime());
        tv.setText("Today date is " + Calendar.getInstance().get(Calendar.YEAR) + "-" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "-" + (Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) +"," +dayIs+".");

        expCalendarView.setOnDateClickListener(new OnExpDateClickListener()).setOnMonthScrollListener(new OnMonthScrollListener() {
            @Override
            public void onMonthChange(int year, int month) {
                String monthGetName = formatMonth(month);
                YearMonthTv.setText(year + " " + monthGetName);
                //YearMonthTv.setText(String.format("%d-%d", year, month));
            }

            @Override
            public void onMonthScroll(float positionOffset) {
//                Log.i("listener", "onMonthScroll:" + positionOffset);
            }
        });
        expCalendarView.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(View view, DateData date) {

                //YearMonthTv.setText(String.format("%d-%d", date.getYear(), date.getMonth()));
                DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat ndf = new SimpleDateFormat("EEEE");
                Date currentDate = null;
                Date currentDateFolder = null;
                Date MyDate = null;

                String monthName = formatMonth(date.getMonth());
                YearMonthTv.setText(date.getYear() + " " + monthName);
                tv.setText("Date Selected: " + date.getDay() + "-" + date.getMonth() + "-" + date.getYear());
                String currDate = date.getYear() + "-" + date.getMonth() + "-" + date.getDay();
                try {
                    currentDate = sdf.parse(currDate);
                    String datefolder = sdf.format(currentDate);

                    //MyDate= ndf.parse(currDate);
                    String weekOfDate = ndf.format(currentDate);
                    tv.append("," + weekOfDate+"");

                    File historyFile = new File(dir, "/QrHistoryFolder/" + filename + "/" + datefolder + ".txt");

                    if (historyFile.exists() && historyFile.length() != 0) {
                        lvcalendar.setVisibility(View.VISIBLE);
                        try {
                            BufferedReader reader = new BufferedReader(new FileReader(historyFile));
                            String history, words[];
                            String ScannedDate = "";
                            ArrayList<String> lines = new ArrayList<>();
                           // ArrayList<String> calendarlines = new ArrayList<>();
                            final ArrayList<String> hidden = new ArrayList<>();

                            while ((history = reader.readLine()) != null) {
                                words = history.split(";");
                                lines.add(words[1] + "\n" + words[3] + "\n" + words[5] + "\n");
                                //lines.add(history);
                                hidden.add(words[0] + ";\n" + words[1] + ";\n" + words[3] + ";\n" + words[5] + ";\n");
                            }
                            int itemCount = lines.size();
                            if (itemCount == 1) {
                                tv.append(". " + itemCount + " item scanned.");
                            } else {
                                tv.append(". " + itemCount + " items scanned.");
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(HistoryCalendar.this, R.layout.list_item, lines);
                            lvcalendar.setAdapter(adapter);

                            //onclick
                            lvcalendar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    int itemPosition = position;
                                    String itemValue = (String) lvcalendar.getItemAtPosition(itemPosition);
                                    String hiddenValue = hidden.get(itemPosition);
                                    Bundle extras = getIntent().getExtras();
                                    String filename = extras.getString("filename");

                                    /* View image of cert        */
                                    //Intent i = new Intent(view.getContext(), QRimage.class);
                                    Intent i= new Intent(view.getContext(),Certimage.class);
                                    i.putExtra("filename", filename);
                                    i.putExtra("position", itemPosition);
                                    i.putExtra("itemvalue", hiddenValue);
                                    startActivity(i);
                                }
                            });
                        } catch (IOException io) {
                            io.printStackTrace();
                        }
                    } else {
                        tv.setText("Date Selected: " + date.getDay() + "-" + date.getMonth() + "-" + date.getYear() +","+weekOfDate +". No data scanned.");
                        lvcalendar.setVisibility(View.GONE);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        imageInit();
    }

    private boolean ifExpand = true;

    private void imageInit() {
        final ImageView expandIV = (ImageView) findViewById(R.id.main_expandIV);

            expandIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if is expanded, then change arrow down and shrink
                //else if is not expanded, then change arrow up and expand
                if (ifExpand) {
                    CellConfig.Month2WeekPos = CellConfig.middlePosition;
                    CellConfig.ifMonth = false;
                    expandIV.setImageResource(R.drawable.icon_arrow_down);
                    expCalendarView.shrink();
                } else {
                    CellConfig.Week2MonthPos = CellConfig.middlePosition;
                    CellConfig.ifMonth = true;
                    expandIV.setImageResource(R.drawable.icon_arrow_up);
                    expCalendarView.expand();
                }
                ifExpand = !ifExpand;
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onBackPressed() {

        Bundle extras=getIntent().getExtras();
        String username = extras.getString("filename");
        Intent myIntent = new Intent(getApplicationContext(), LoginMainActivity.class);
         myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        myIntent.putExtra("username", username);
        myIntent.putExtra("image", extras.getString("image"));
        startActivity(myIntent);
        finish();
    }

    public String formatMonth(int month){
        String monthString=String.valueOf(month);
        String formattedText="";
         try {
             SimpleDateFormat monthParse = new SimpleDateFormat("MM");
             SimpleDateFormat monthDisplay = new SimpleDateFormat("MMMM");
           formattedText = monthDisplay.format(monthParse.parseObject(monthString));
             return formattedText;
         }catch(ParseException e){
             e.printStackTrace();
         }
       return formattedText;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if(id == R.id.notification){

           Notify("E-Scroll Notification","You have a notification from E-Scroll.");
           return true;
        }else if(id == R.id.delete){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Clear History");
            builder.setMessage("Do you want to clear history? ");

            builder.setNegativeButton(R.string.yes_button, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int id) {
                    Bundle extras = getIntent().getExtras();
                    String filename = extras.getString("filename");
                    File dir = Environment.getExternalStorageDirectory();
                    File historyFile = new File(dir, "/QrHistoryFolder/" + filename + "/");
                    if (historyFile.isDirectory()) {
                        String[] children = historyFile.list();
                        for (int i = 0; i < children.length; i++) {
                            new File(historyFile, children[i]).delete();
                        }
                    }
                    //finish();
                    Intent myIntent = new Intent(getApplicationContext(), LoginMainActivity.class);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    myIntent.putExtra("username", filename);
                    myIntent.putExtra("image", extras.getString("image"));
                    startActivity(myIntent);
                    finish();
                }
            });

            builder.setPositiveButton(R.string.no_button, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int id) {
                }
            });
            builder.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void Notify(String notificationTitle, String notificationMessage){
        long[] v = {500,1000};
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this,NotificationView.class);
        PendingIntent pendingIntent= PendingIntent.getActivity(this,0,intent,0);

        NotificationCompat.Builder builder= new NotificationCompat.Builder(this);
        Notification notification= builder.setContentIntent(pendingIntent)
                                          .setSmallIcon(R.drawable.escrollblackwhite)
                                           .setTicker(notificationMessage)
                                            .setWhen(System.currentTimeMillis())
                                             .setAutoCancel(true)
                                              .setContentTitle(notificationTitle)
                                               .setVibrate(v)

                .setContentText(notificationMessage).build();

        notificationManager.notify(NOTIFICATION_ID,notification);
    }


}
