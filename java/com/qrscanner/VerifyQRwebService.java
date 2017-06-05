package com.qrscanner;

/**
 * Created by wllim on 3/10/16.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;


public class VerifyQRwebService extends Activity  {

    //SharedPreferences prefs;
    private static final String SOAP_ACTION= "http://tempuri.org/VerifyQR";
    private static final String OPERATION_NAME="VerifyQR";
    private static final String WS_NAMESPACE= "http://tempuri.org/";
    //private static final String SOAP_ADDRESS ="http://172.16.253.39/MyWS/Service1.asmx";
    private static final String SOAP_ADDRESS="http://172.16.252.137:8081/jw/GraduatesInfo?qrContent=MDAwMDAwMDEwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwQUVDM0I0MEM2Q0FENUJFNDk3RTMwMDE0RTUwODEyMjc4Mzg3N0JBMjE2Q0YyMDFDQ0EzNUNEOTZEOTBEODkzOEIwQjgzNTRFM0YwRjM4NjYwQjE3RTkzQTQ4QUE1NzMzMDQ0MDIyMDI0OUEwN0RERDNGOTY0NkMwRTU0NENFNDI4MDc1M0M1QzE4MEI5Njc5MDA5OEU1RTE4QjE1QTQ4MjA1NzZCNTEwMjIwMTBCRjJBMkY3REU4RkM2Qzc2MjU1MzU0Nzc0QzM5RjZCMEQwMjQxMTg2RTMwQjJEOUZFNjRENDhFMDlERTdEQQ==&email=zafienas@iris.com.my";

    String email;
    String qrContent;
    TextView tv,tvresult;
    EditText et;
    Button btnCert, btnback;
    String outData;


    JSONParser jParser = new JSONParser();
    JSONObject json;

    int count= 1;

   // private static String url="http://172.16.252.137:8081/jw/GraduatesInfo?";
   // private static String url="http://192.168.1.111:8081/jw/GraduatesInfo?";



    @Override
    public  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webservice_layout);

        Bundle extras = getIntent().getExtras();
        String email=extras.getString("filename");
        String content = extras.getString("QRcontent");
        String image = extras.getString("image");
        String qrdata= extras.getString("QRdata");
       // String pdf417content = extras.getString("PDF417content");

        //Get the new URL from loginMainActivity
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
         SharedPreferences prefs= getApplicationContext().getSharedPreferences("url",MODE_PRIVATE);
        String newURL= prefs.getString("string_url", "no url");
        //String url="http://"+ newURL+":8081/jw/GraduatesInfo?"; // "http://172.16.252.137:8081/jw/GraduatesInfo?";
        final String url="http://"+ newURL+":8081/jw/web/json/plugin/com.iris.joget.plugin.EScrollAppController/service?";
        //final String url="https://"+ newURL+":8445/jw/web/json/plugin/com.iris.joget.plugin.EScrollAppController/service?";


        // tv.setText("QRCode content: \n"+content);
        findViewsById();

        new startjavaService(tvresult).execute(email, content,url);

       // SharedPreferences.Editor editor = prefs.edit();
       // editor.clear().apply();

       Animation animationFadeIn = AnimationUtils.loadAnimation(this,R.anim.fadein);
       btnback.startAnimation(animationFadeIn);
       btnCert.startAnimation(animationFadeIn);

       btnback.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               Intent myIntent = new Intent(getApplicationContext(), LoginMainActivity.class);
               Bundle extras = getIntent().getExtras();
               myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               String username = extras.getString("filename");
               myIntent.putExtra("username", username);
               myIntent.putExtra("image", extras.getString("image"));
               startActivity(myIntent);
               finish();
           }
       });

        // To view the Certificate in Image format
        btnCert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent= new Intent(getApplicationContext(),VerifyQRwebServiceCertImage.class );
                Bundle extras = getIntent().getExtras();
              //  myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                String content = extras.getString("QRcontent");
                String username = extras.getString("filename");
                String email=extras.getString("filename");

                myIntent.putExtra("username", username);
                myIntent.putExtra("image", extras.getString("image"));
                myIntent.putExtra("QRcontent", content);
                myIntent.putExtra("email",email);
                myIntent.putExtra("url",url);
                startActivity(myIntent);
             //   finish();
            }
        });

    }
    private void findViewsById(){

        tvresult=(TextView)findViewById(R.id.resultfromWS);
        btnback=(Button)findViewById(R.id.button_back_main);
        btnCert=(Button)findViewById(R.id.button_view_cert);
    }

    private class startjavaService extends AsyncTask<String , Void, String>{
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
            //json = jParser.makeHttpRequest(url, "GET", params);
            json = jParser.makeHttpRequest(url,"POST",params);
            String s=null;
            String p =null;

                try {
                    s = json.getString("verification");
                    p = json.getString("idDesc");
                    Log.d("Msg", json.getString("verification"));

                    // get spm cert
                    if(s.equals("pass") && p.equals("null"))
                    {
                        String alldata = "Doc SerialNo: " + json.getString("docSerialNo") + "\n"
                                + "Name: " + json.getString("name") + "\n"
                                + "NRIC No.: " + json.getString("icNo") + "\n"
                                + "School: " + json.getString("schoolName") + "\n"
                                + "Total subject taken: "+ json.getString("subjectTotal") +"\n"
                                + "Exam year: "+ json.getString("examYear");
                        return alldata;
                    }
                     if (s.equals("pass") && p.equals("NRIC No.")) {
                        String alldata = "Doc SerialNo: " + json.getString("docSerialNo") + "\n"
                                + "Name: " + json.getString("name") + "\n"
                                + "NRIC No.: " + json.getString("icNo") + "\n"
                                + "University: " + json.getString("university") + "\n"
                                + "Center: " + json.getString("center") + "\n"
                                + "Program: " + json.getString("program") + "\n"
                                + "Seal Date: " + json.getString("sealDate") + "\n"
                                + "Completion Date: " + json.getString("completionDate");
                        return alldata;
                    }

                    else {
                        String alldata = "Doc SerialNo: " + json.getString("docSerialNo") + "\n"
                                + "Name: " + json.getString("name") + "\n"
                                + "Passport No.: " + json.getString("icNo") + "\n"
                                + "University: " + json.getString("university") + "\n"
                                + "Center: " + json.getString("center") + "\n"
                                + "Program: " + json.getString("program") + "\n"
                                + "Seal Date: " + json.getString("sealDate") + "\n"
                                + "Completion Date: " + json.getString("completionDate");
                        return alldata;
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            s="Invalid QR Code";
            return s;
        }

        protected void onPostExecute(String result){
            Log.i(TAG, "onPostExecute");
            //textView.setTextSize(20);
            textView.setText(result);
            if(!result.equals("Invalid QR Code") )
            {
                UpdateHistory(result.replace("\n",";"));
            }

        }
    }
    public void UpdateHistory(String data){

        final Calendar dateNow= Calendar.getInstance();
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss;");
        SimpleDateFormat sdffolder= new SimpleDateFormat("yyyy-MM-dd");
        String Datenow,Datefolder;
        Bundle extras=getIntent().getExtras();

        Date now = new Date();
        //String filename = sdf.format(now)+ extras.getString("filename");
        String content = extras.getString("QRcontent");
        String filename = extras.getString("filename");
        File dir = Environment.getExternalStorageDirectory();
        Datefolder= sdffolder.format(dateNow.getTime());
        File historyFile = new File(dir, "/QrHistoryFolder/" + filename +"/"+Datefolder+ ".txt");

        if(!historyFile.exists()){
            try {
                historyFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (historyFile.exists()) {
            try {
                Datenow = sdf.format(dateNow.getTime());
                Writer writer = new BufferedWriter(new FileWriter(historyFile, true));
                //writer.append(Datenow+":   "+data +"\n");
                writer.append(content+";"+Datenow+data+"\n");
                writer.close();

            } catch(IOException io){
                Toast.makeText(getApplicationContext(), "Error writing on file", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
        }

        return super.onKeyDown(keyCode, event);
    }

    public void onBackPressed() {
        Intent myIntent = new Intent(getApplicationContext(), LoginMainActivity.class);
        Bundle extras=getIntent().getExtras();
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        String username = extras.getString("filename");
        myIntent.putExtra("username", username);
        myIntent.putExtra("image", extras.getString("image"));
        startActivity(myIntent);
        finish();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    // get ipaddress from device cwifi connection

    protected  String wifiIpAddress(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            Log.e("WIFIIP", "Unable to get host address.");
            ipAddressString = null;
        }
        return ipAddressString;
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes= Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

}
