package com.qrscanner;

/**
 * Created by wllim on 8/9/16.
 */

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.AsyncTask;
import android.os.Environment;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.ShareActionProvider;

import android.util.Base64;
import android.util.Log;

import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import android.os.Bundle;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;


public class VerifyQRwebServiceCertImage extends FragmentActivity {


    ImageView iv;
    JSONParser jParser = new JSONParser();
    JSONObject json;
   TouchImageView img;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cert_image);

        Bundle extras = getIntent().getExtras();
        String content = extras.getString("QRcontent");
        String url=extras.getString("url");
        String email=extras.getString("email");
        String username = extras.getString("filename");

        //generate Image from data
        //iv=(ImageView)findViewById(R.id.Certimage);
        img=(TouchImageView)findViewById(R.id.Certimage);

        new startjavaService(img).execute(email, content, url);

    }

    private class startjavaService extends AsyncTask<String , Void, String>{
        String TAG = "javaService";
       // ImageView imageView;

        TouchImageView touchImageView;
        //  public startjavaService(ImageView imageView){
        //      this.imageView=imageView;
        //  }

        public startjavaService(TouchImageView touchImageView){
            this.touchImageView=touchImageView;
        }

        protected String doInBackground(String... args) {
            Log.i(TAG, "doInBackground");

            String email = args[0];
            String content =args[1];
            String url=args[2];

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("qrContent",content));
            params.add(new BasicNameValuePair("email",email));
            json = jParser.makeHttpRequest(url,"POST",params);
            String s= null;
            String c=null;
            try {
                s = json.getString("verification");

                Log.d("Msg", json.getString("verification"));
                Log.d("Msg",json.getString("image"));
                // retrieve spm cert image

                if (s.equals("pass"))
                {
                    // pass me the base64 String here
                    //***********
                 c=json.getString("image");
                    return c;
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            c="Invalid Certificate";
            return c;
        }

        protected void onPostExecute(String result) {
            Log.i(TAG, "onPostExecute");
            Bitmap certimage= decodeBase64(result);
            touchImageView.setImageBitmap(certimage);
           // imageView.setImageBitmap(certimage);
        }
    }

   /*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onBackPressed() {

      Intent myIntent = new Intent(getApplicationContext(), VerifyQRwebService.class);
        Bundle extras=getIntent().getExtras();
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        String username = extras.getString("username");
        myIntent.putExtra("username", username);
        myIntent.putExtra("image", extras.getString("image"));
        String email=extras.getString("username");
        String content = extras.getString("QRcontent");
        myIntent.putExtra("filename", email);
        myIntent.putExtra("QRcontent", content);
        startActivity(myIntent);
        finish();
    }
  */

    @Override
    public void onResume(){
        super.onResume();
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes= Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


}
