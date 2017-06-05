package com.qrscanner;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wllim on 8/11/16.
 */



public class Certimage extends FragmentActivity {


    Bitmap mBitmap;
    ImageView iv;
    String email,url,content;

    public static final int NOTIFICATION_ID=1;
    JSONParser jParser = new JSONParser();
    JSONObject json;
    TouchImageView img;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cert_image);

        Bundle extras = getIntent().getExtras();
        email = extras.getString("filename");
         String qrdetails = extras.getString("itemvalue");
         String[] splitString = qrdetails.split(";");
          content = splitString[0].trim();

        //Get the new URL from loginMainActivity
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences prefs= getApplicationContext().getSharedPreferences("url", MODE_PRIVATE);
        String newURL= prefs.getString("string_url", "no url");
        //String url="http://"+ newURL+":8081/jw/GraduatesInfo?";
        url="http://"+ newURL+":8081/jw/web/json/plugin/com.iris.joget.plugin.EScrollAppController/service?";
        //url="https://"+ newURL+":8445/jw/web/json/plugin/com.iris.joget.plugin.EScrollAppController/service?";

        //iv=(ImageView)findViewById(R.id.Certimage);
        img=(TouchImageView)findViewById(R.id.Certimage);
        new startjavaService(img).execute(email, content, url);


    }

    private class startjavaService extends AsyncTask<String , Void, String> {
        String TAG = "javaService";
      //  ImageView imageView;
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

            //img.setMaxZoom(4f);
            //imageView.setImageBitmap(img.getDrawingCache());
            //imageView.setImageBitmap(certimage);

        }
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes= Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

}
