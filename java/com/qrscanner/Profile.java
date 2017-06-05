package com.qrscanner;



/**
 * Created by wllim on 5/4/16.
 */
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.view.animation.AnimationUtils;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.molpay.molpayxdk.MOLPayActivity;

//import com.parse.Parse;
//import com.paypal.android.sdk.payments.PayPalAuthorization;
//import com.paypal.android.sdk.payments.PayPalConfiguration;
//import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
//import com.paypal.android.sdk.payments.PayPalPayment;
//import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
//import com.paypal.android.sdk.payments.PayPalService;
//import com.paypal.android.sdk.payments.PaymentActivity;
//import com.paypal.android.sdk.payments.PaymentConfirmation;



import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Profile extends FragmentActivity {

    //PayPal details
    private static final String ACTION_STRING_SERVICE = "toService";
    private static final String ACTION_STRING_ACTIVITY = "toActivity";
    private static final String TAG = "paymentExample";
    /**
     * - Set to PayPalConfiguration.ENVIRONMENT_PRODUCTION to move real money.
     * <p/>
     * - Set to PayPalConfiguration.ENVIRONMENT_SANDBOX to use your test credentials
     * from https://developer.paypal.com
     * <p/>
     * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
     * without communicating to PayPal's servers.
     */
/*
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;

    // note that these credentials will differ between live & sandbox environments.
    private static final String CONFIG_CLIENT_ID = "ATiuE0W6Yt44iUI6sw0Bs2vjWoI7WYlSIvehUJGLlV-AM1ML02Cp_EKaUcHOyNYvoyF_N8_-TSP6sJvO";

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
                    // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("IRIS Corporation Berhad")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));
*/

    //WebService details
    private static final String SOAP_ACTION= "http: //tempuri.org/VerifyQR";
    private static final String OPERATION_NAME="VerifyQR";
    private static final String WS_NAMESPACE= "http://tempuri.org/";
    //private static final String SOAP_ADDRESS ="http://172.16.253.39/MyWS/Service1.asmx";

    JSONParser jParser = new JSONParser();
    JSONObject json;

    //private static String url="http://172.16.252.137:8081/jw/GetAccountBalance?";
    //private static String urlUpdate="http://172.16.252.137:8081/jw/UpdateAccountBalance?";
    private static String url="http://192.168.1.111:8081/jw/GetAccountBalance?";
    private static String urlUpdate="http://192.168.1.111:8081/jw/UpdateAccountBalance?";


    TextView tv,tvtopupvalue;
    NumberPicker np;
    ImageView iv;
    Button btn_top_upMOL,btn_top_upPAYPAL ;
    Bitmap bmp;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userprofile_main);
        Bundle extras = getIntent().getExtras();
        iv=(ImageView)findViewById(R.id.userprofilepic);

        String filename = getIntent().getStringExtra("image");
        try{
            FileInputStream is = openFileInput(filename);
            bmp= BitmapFactory.decodeStream(is);
            iv.setImageBitmap(bmp);
            is.close();
        }catch(Exception e){
            e.printStackTrace();
        }

       /*
        //get profile pic
        // Intent intent = getIntent();
        // bmp=(Bitmap)intent.getParcelableExtra("image");
        //bmp=extras.getParcelable("image");
        if(bmp !=null)
        {
             iv.setImageBitmap(bmp);
        }
        else
        {
            int img_link=getIntent().getIntExtra("image",R.drawable.googleaccountimage);
            iv.setImageResource(img_link);
        }
       */

        btn_top_upMOL= (Button)findViewById(R.id.buyItBtn);
       // btn_top_upPAYPAL= (Button)findViewById(R.id.buyItBtn2);
        np=(NumberPicker)findViewById(R.id.numberPicker);
        String[]TopUpValue = new String[5];
        TopUpValue[0] = "RM10 ";
        TopUpValue[1] = "RM20 ";
        TopUpValue[2] = "RM50 ";
        TopUpValue[3] = "RM70 ";
        TopUpValue[4] = "RM100 ";
        np.setDisplayedValues(TopUpValue);
        np.setMinValue(0);
        np.setMaxValue(4);
        np.setValue(0);
        np.setWrapSelectorWheel(false);
        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        tvtopupvalue=(TextView)findViewById(R.id.tvtopupvalue);

       tvtopupvalue.append(np.getDisplayedValues()[np.getValue()]);
        
        //MOLPAY button
        btn_top_upMOL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle extras = getIntent().getExtras();
                String details = extras.getString("username");
                String filename = getIntent().getStringExtra("image");
                String TopUp = np.getDisplayedValues()[np.getValue()].substring(2, 5).trim();
                //new UpdateBalancefromWS(tv).execute(details,TopUp);
                Intent i = new Intent(Profile.this, MOLPayment.class);
                i.putExtra("username",details);
                i.putExtra("topup", TopUp);
                i.putExtra("image",filename);
                startActivity(i);

            }
        });

/*
        //Intent for PAYPALSERVICE.CLASS
        Intent intent = new Intent(getApplicationContext(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        getApplicationContext().startService(intent);

        /*
        //PAYPAL button
        btn_top_upPAYPAL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);
                Intent intent1 = new Intent();
                Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

                startActivityForResult(intent, REQUEST_CODE_PAYMENT);

            }
        });
*/
        String details = extras.getString("username");
        String email=extras.getString("email");
        tv=(TextView)findViewById(R.id.creditbalance);

        // get the balance from WebService
       new GetBalancefromWS(tv).execute(details);
        Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
        tv.startAnimation(animationFadeIn);


        //Parse notification
       // Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
       //                   .applicationId("com.qrscanner")
       //                  .server("http://")
       //         .build()
       // );
       }

    /*
    private void sendAuthorizationToServer(PayPalAuthorization authorization) {

        /**
         * TODO: Send the authorization response to your server, where it can
         * exchange the authorization code for OAuth access and refresh tokens.
         *
         * Your server must then store these tokens, so that your server code
         * can execute payments for this user in the future.
         *
         * A more complete example that includes the required app-server to
         * PayPal-server integration is available from
         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
         */
   // }
/*

    private PayPalPayment getThingToBuy(String paymentIntent) {
        String TopUp = np.getDisplayedValues()[np.getValue()].substring(2, 5).trim();
        //update the balance
        // UpdateBalance(TopUp);
        return new PayPalPayment(new BigDecimal(TopUp), "USD", "IRIS E-Scroll Top Up",
                paymentIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                if (confirm != null) {
                    try {
                        Log.i(TAG, confirm.toJSONObject().toString(4));
                        Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));
                        Toast.makeText(getApplicationContext(), "Top up successful.", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        TAG,
                        "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("FuturePaymentExample", auth.toJSONObject().toString(4));
                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("FuturePaymentExample", authorization_code);

                        sendAuthorizationToServer(auth);
                        Toast.makeText(
                                getApplicationContext(),
                                "Future Payment code received from PayPal", Toast.LENGTH_LONG)
                                .show();

                    } catch (JSONException e) {
                        Log.e("FuturePaymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("FuturePaymentExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        "FuturePaymentExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_PROFILE_SHARING) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalProfileSharingActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("ProfileSharingExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("ProfileSharingExample", authorization_code);

                        sendAuthorizationToServer(auth);
                        Toast.makeText(
                                getApplicationContext(),
                                "Profile Sharing code received from PayPal", Toast.LENGTH_LONG)
                                .show();

                    } catch (JSONException e) {
                        Log.e("ProfileSharingExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("ProfileSharingExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        "ProfileSharingExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }
    }
*/

    private class GetBalancefromWS extends AsyncTask<String ,Void, String> {
        String TAG="balanceService";
        TextView tv;

        public GetBalancefromWS(TextView tv){
            this.tv =tv;
        }

        protected String doInBackground(String...args){
            Log.i(TAG, "doInBackground");
            JSONArray jarr;
            String email = args[0];

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("email",email));
            json = jParser.makeHttpRequest(url, "GET", params);
            String s = null;
            String p = null;

            try{
                //s= json.getString("balance");
                //Log.d("Msg",json.getString("balance"));
                //if(s.equals("pass")) {
                    String alldata = "Email: " + json.getString("email") + "\n"
                            + "Name: " + json.getString("name") + "\n"
                            //+ "University: " + json.getString("university") + "\n"
                            + "Balance:RM " + json.getString("balance") ;
                    return alldata;

                //}
            }catch(JSONException e){
                e.printStackTrace();
            }
            s="Failed to load details.";
            return s;
        }

        protected  void onPostExecute(String result){
            Log.i(TAG, "onPostExecute");
            tv.setText(result);
        }
    }

    private class UpdateBalancefromWS extends AsyncTask<String ,Void, String> {
        String TAG="UpdateBalance Service";
        TextView tv;

        public UpdateBalancefromWS(TextView tv){
            this.tv =tv;
        }

        protected String doInBackground(String...args){
            Log.i(TAG, "doInBackground");

            String email = args[0];
            String topup=args[1];

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("topup",topup));
            params.add(new BasicNameValuePair("email",email));
            json = jParser.makeHttpRequest(urlUpdate, "GET", params);
            String s = null;

            try{
                s= json.getString("transaction");
                Log.d("Msg",json.getString("transaction"));
                if(s.equals("success")) {

                String alldata = "Id: " + json.getString("id") + "\n"
                        + "Name: " + json.getString("name") + "\n"
                        + "University: " + json.getString("university") + "\n"
                        + "Balance:RM " + json.getString("balance") + "\n";

                return alldata;
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
            s="Failed to update balance.";
            return s;
        }

        protected  void onPostExecute(String result){
            Log.i(TAG, "onPostExecute");
            tv.setText(result);
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
        //iv.setImageResource(0);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle extras = getIntent().getExtras();
        myIntent.putExtra("username",extras.getString("username"));
        myIntent.putExtra("image",extras.getString("image"));
        startActivity(myIntent);
        finish();
    }

}
