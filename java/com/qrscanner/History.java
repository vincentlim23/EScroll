package com.qrscanner;

/**
 * Created by wllim on 2/24/16.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
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
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeSet;

import com.google.integration.IntentIntegrator;
import com.google.integration.IntentResult;

import org.w3c.dom.Text;

public class History extends FragmentActivity {

    ListView lv,hiddenlv;
    Bitmap mBitmap;
    ImageView iv;
    private Context HistoryContext;
    private ImageView imageGen;
    private static final int LONG_DELAY=3000;
    private String content;

    private static final int TYPE_ITEM=0;
    private static final int TYPE_SEPARATOR=1;

    private TreeSet<Integer>sectionHeader = new TreeSet<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_history);
        lv=(ListView)findViewById(R.id.listView);
        //setContentView(R.layout.qrhistorycalendar);
        //lv=(ListView)findViewById(R.id.listViewCalendar);
        Bundle extras=getIntent().getExtras();

        String filename = extras.getString("filename");
        File dir = Environment.getExternalStorageDirectory();
        File historyFile = new File(dir, "/QrHistoryFolder/" + filename + ".txt");

        try{
             BufferedReader reader = new BufferedReader(new FileReader(historyFile));
             String history;
            String words[];
            ArrayList<String>lines = new ArrayList<>();
            final ArrayList<String>hidden = new ArrayList<>();
            while((history = reader.readLine()) !=null  )
            {
                  words=history.split(";");
                  lines.add(words[1]+"\n"+words[3]+"\n"+words[5]+"\n");
                  //lines.add(history);
                 hidden.add(words[0]+";\n"+words[1]+";\n"+words[3]+";\n"+words[5]+";\n");
             }

            ArrayAdapter<String>adapter = new ArrayAdapter<String>(this,R.layout.list_item,lines);

            lv.setAdapter(adapter);

            //onclick
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int itemPosition = position;
                    String itemValue = (String) lv.getItemAtPosition(itemPosition);
                    String hiddenValue= hidden.get(itemPosition);

                    Bundle extras = getIntent().getExtras();
                    String filename = extras.getString("filename");
                    Intent i = new Intent(view.getContext(), QRimage.class);
                    i.putExtra("filename", filename);
                    i.putExtra("position", itemPosition);
                     i.putExtra("itemvalue", hiddenValue);
                    startActivity(i);
                }

            });
        }catch (Exception io){
            io.printStackTrace();
        }

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

        }else if(id == R.id.delete){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Clear History");
            builder.setMessage("Do you want to clear history? ");


            builder.setNegativeButton(R.string.yes_button, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int id) {
                    Bundle extras=getIntent().getExtras();
                    String filename = extras.getString("filename");
                    File dir = Environment.getExternalStorageDirectory();
                    File historyFile = new File(dir, "/QrHistoryFolder/" + filename + ".txt");
                    try{
                        PrintWriter writer = new PrintWriter(historyFile);
                        writer.print("");
                        writer.close();
                    }catch(FileNotFoundException ex)
                    {
                        ex.printStackTrace();
                    }
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




}
