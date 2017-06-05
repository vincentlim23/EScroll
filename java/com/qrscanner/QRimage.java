package com.qrscanner;

/**
 * Created by wllim on 3/11/16.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.support.v7.widget.ShareActionProvider;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.pdf417.PDF417Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import com.molpay.molpayxdk.MOLPayActivity;
import android.view.View.OnClickListener;

public class QRimage extends FragmentActivity implements ShareActionProvider.OnShareTargetSelectedListener {

    Bitmap mBitmap;
    ImageView iv;
    TextView tv,realdata;
    String email,url,content;
    ShareActionProvider mShareActionProvider;
    ListView lv;
    public static final int NOTIFICATION_ID=1;
    JSONParser jParser = new JSONParser();
    JSONObject json;
    // private static String url="http://172.16.252.137:8081/jw/GraduatesInfo?";
   // private static String url="http://192.168.1.111:8081/jw/GraduatesInfo?";
   // private static String url="http://172.16.252.137:8081/jw/web/json/plugin/com.iris.joget.plugin.EScrollAppController/service?";


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrimage);

        Bundle extras = getIntent().getExtras();
        String qrdetails = extras.getString("itemvalue");
        tv = (TextView) findViewById(R.id.qrimagedetails);

        Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
        tv.startAnimation(animationFadeIn);

        String[] splitString = qrdetails.split(";");
        StringBuilder details = new StringBuilder(100);

        details.append("This QR Code is scanned on :").append(splitString[1]).append('\n');
        //details.append("Contents: ").append(splitString[1]).append('\n');
        //details.append("Format: ").append(splitString[2]).append('\n');
        //details.append("Raw bytes: ").append(splitString[3]).append('\n');
        //details.append("Orientation: ").append(splitString[4]).append('\n');
        //details.append("EC Level: ").append(splitString[5]).append('\n');
        tv.append(details);

        //generate Image from data
        iv = (ImageView) findViewById(R.id.QRCodeimage);
         content = splitString[0].trim();
        generateQRCode(content);
        //Generate PDF417 CODE
        //generatePDF417(content);
        email = extras.getString("filename");
        File sd=Environment.getExternalStorageDirectory();
        File dest= new File(sd,"/QrHistoryFolder/"+ email+"/"+"qrimage.png");
        if(dest.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(dest.getAbsolutePath());

           iv.setImageBitmap(myBitmap);
        }

        //Get the new URL from loginMainActivity
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences prefs= getApplicationContext().getSharedPreferences("url",MODE_PRIVATE);
        String newURL= prefs.getString("string_url", "no url");
        //String url="http://"+ newURL+":8081/jw/GraduatesInfo?";
        url="http://"+ newURL+":8081/jw/web/json/plugin/com.iris.joget.plugin.EScrollAppController/service?";


        realdata = (TextView)findViewById(R.id.realdata);
         new startjavaService(realdata).execute(email,content,url);

       }

    private void generateQRCode(String data ) {
        Hashtable hintMap = new Hashtable();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        QRCodeWriter writer = new QRCodeWriter();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        Date now =new Date();
        //String finaldata = Uri.encode(data, "ISO-8859-1");
        String finaldata = data;
        String filename = "qrimage.png";
        Bundle extras = getIntent().getExtras();
        String email = extras.getString("filename");
        File sd =Environment.getExternalStorageDirectory();
        File dest= new File(sd,"/QrHistoryFolder/"+email+"/"+filename);
        FileOutputStream out = null;

        try {

            BitMatrix bm = writer.encode(finaldata, BarcodeFormat.QR_CODE, 350,350,hintMap);
            mBitmap = Bitmap.createBitmap(350, 350, Bitmap.Config.ARGB_8888);

            for (int i = 0; i < 350; i++) {
                for (int j = 0; j < 350; j++) {
                    mBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }
            if (mBitmap != null) {

                try {
                    out = new FileOutputStream(dest);
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush(); out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.userInputError),
                        Toast.LENGTH_SHORT).show();
            }
        }catch(WriterException e){
            e.printStackTrace();
        }
    }

    private class startjavaService extends AsyncTask<String , Void, String> {
        String TAG = "javaService";
        TextView textView;

        public startjavaService(TextView textView){
            this.textView=textView;
        }

        protected String doInBackground(String... args) {
            Log.i(TAG, "doInBackground");
            // Getting username and password from user input
            JSONArray jarr;
            String email = args[0];
            String content =args[1];
            String url=args[2];

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("qrContent",content));
            params.add(new BasicNameValuePair("email",email));

           // json = jParser.makeHttpRequest(url, "GET", params);
            json = jParser.makeHttpRequest(url,"POST",params);

            String s=null;
            String p=null;
            String c=null;
            try {
                s= json.getString("verification");
                p=json.getString("idDesc");

                Log.d("Msg", json.getString("verification"));


                if(s.equals("pass") && p.equals("NRIC No.")){
                    String alldata="Doc SerialNo: "+ json.getString("docSerialNo") +"\n"
                            +"Name: "+json.getString("name")+"\n"
                            +"NRIC No.: "+json.getString("icNo")+"\n"
                            +"University: "+json.getString("university")+"\n"
                            +"Center: "+json.getString("center")+"\n"
                            +"Program: "+json.getString("program")+"\n"
                            +"Seal Date: "+json.getString("sealDate")+"\n"
                            +"Completion Date: "+json.getString("completionDate");
                    return alldata;
                }
                else {
                    String alldata="Doc SerialNo: "+ json.getString("docSerialNo") +"\n"
                            +"Name: "+json.getString("name")+"\n"
                            +"Passport No.: "+json.getString("icNo")+"\n"
                            +"University: "+json.getString("university")+"\n"
                            +"Center: "+json.getString("center")+"\n"
                            +"Program: "+json.getString("program")+"\n"
                            +"Seal Date: "+json.getString("sealDate")+"\n"
                            +"Completion Date: "+json.getString("completionDate");
                    return alldata;
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return s;
        }

        protected void onPostExecute(String result){
            Log.i(TAG, "onPostExecute");
            textView.setText(result);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_qrimage, menu);

        MenuItem shareItem= menu.findItem(R.id.share);
        if(shareItem !=null){
            mShareActionProvider=(ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        }
        setShareIntent();

        return true;
        //return(super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onShareTargetSelected(ShareActionProvider source,
                                         Intent intent) {
        Toast.makeText(this, intent.getComponent().toString(),
                Toast.LENGTH_LONG).show();

        return(false);
    }

    private void setShareIntent(){

        if(mShareActionProvider !=null){
            TextView data = (TextView)findViewById(R.id.realdata);
            String text =data.getText().toString();
            Intent shareIntent= new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
           // shareIntent.setType("image/png");
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "QR Code Details");
           //shareIntent.putExtra(Intent.EXTRA_STREAM, iv.getDrawingCache());
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
            mShareActionProvider.setShareIntent(shareIntent);
        }
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

           }
            else if(id == R.id.notification){
            Notify("E-Scroll Notification","You have a notification from E-Scroll.");
            return true;
           }else if(id == R.id.delete){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Clear QRImage");
            builder.setMessage("Do you want to clear QR Images? ");


            builder.setNegativeButton(R.string.yes_button, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int id) {

                    File dir = Environment.getExternalStorageDirectory();
                    File imageFolder = new File(dir, "/QrHistoryFolder/qrimage.png");

                    boolean delete = imageFolder.delete();

                   // String[] children = imageFolder.list();
                   // for (int i = 0; i < children.length; i++) {
                   //     File child = new File(imageFolder, children[i]);
                   //     child.delete();
                   // }
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

        PendingIntent pendingIntent= PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder= new NotificationCompat.Builder(this);
        Notification notification= builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.escrollblackwhite)
                .setTicker(notificationMessage)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(notificationTitle)
                .setVibrate(v)
                .setContentText(notificationMessage).build();
;
        notificationManager.notify(NOTIFICATION_ID,notification);
    }




}
