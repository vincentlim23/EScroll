package com.qrscanner;

/**
 * Created by wllim on 5/3/16.
 */
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.molpay.molpayxdk.MOLPayActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.molpay.molpayxdk.MOLPayService;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class MOLPayment extends AppCompatActivity{

    private static final String SOAP_ACTION= "http://tempuri.org/VerifyQR";
    private static final String OPERATION_NAME="VerifyQR";
    private static final String WS_NAMESPACE= "http://tempuri.org/";
    //private static final String SOAP_ADDRESS ="http://172.16.252.137:8081/jw/GetAccountBalance?";
    private static final String SOAP_ADDRESS ="http://192.168.1.111:8081/jw/GetAccountBalance?";
    //private static String url="http://172.16.252.137:8081/jw/GraduatesInfo?";
    //private static String urlUpdate="http://172.16.252.137:8081/jw/UpdateAccountBalance?";

    private static String url="http://192.168.1.111:8081/jw/GetAccountBalance?";
    private static String urlUpdate="http://192.168.1.111:8081/jw/UpdateAccountBalance?";

    JSONParser jParser = new JSONParser();
    JSONObject json;



    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.molpay_layout);

        Toolbar toolbar= (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        String value =extras.getString("topup");

        /// mandatory string. payment values
        HashMap<String, Object>paymentDetails = new HashMap<>();
        paymentDetails.put(MOLPayActivity.mp_amount,value);
        paymentDetails.put(MOLPayActivity.mp_username, "api_iris_Dev");
        paymentDetails.put(MOLPayActivity.mp_password, "api_iD4#2!Hg?s");
        paymentDetails.put(MOLPayActivity.mp_merchant_ID, "iris_Dev");
        paymentDetails.put(MOLPayActivity.mp_app_name, "IRISMOLTestApp");
        paymentDetails.put(MOLPayActivity.mp_order_ID, "DEMO123");
        paymentDetails.put(MOLPayActivity.mp_currency, "MYR");
        paymentDetails.put(MOLPayActivity.mp_country, "MY");
        paymentDetails.put(MOLPayActivity.mp_verification_key, "f1c0097a720f1e760f5d1c94bcf2375a");

        //// optional string
        paymentDetails.put(MOLPayActivity.mp_channel, "multi");
        paymentDetails.put(MOLPayActivity.mp_bill_description, "Topup for E-Scroll account");
        paymentDetails.put(MOLPayActivity.mp_bill_name, "E-Scroll MOLPay");
        paymentDetails.put(MOLPayActivity.mp_bill_email, "demo@mail.com");
        paymentDetails.put(MOLPayActivity.mp_bill_mobile, "01123232323");
        paymentDetails.put(MOLPayActivity.mp_channel_editing, false);
        paymentDetails.put(MOLPayActivity.mp_editing_enabled, false);

       ////for transaction request use only, do not use this on payment process
       //paymentDetails.put(MOLPayActivity.mp_transaction_id, "");
       // paymentDetails.put(MOLPayActivity.mp_request_type, "");


        String details = extras.getString("username");
        String filename = extras.getString("image");
        tv=(TextView)findViewById(R.id.resultTV);
        new UpdateBalancefromWS(tv).execute(details,value);

        Intent intent = new Intent(MOLPayment.this, Profile.class);
        intent.putExtra("updatebalance",tv.getText().toString());
        intent.putExtra("username",details);
        intent.putExtra("image",filename);
        startActivity(intent);
       // Intent intent = new Intent(MOLPayment.this,MOLPayActivity.class);
       // intent.putExtra(MOLPayActivity.MOLPayPaymentDetails,paymentDetails);
        //startActivityForResult(intent, MOLPayActivity.MOLPayXDK);

    /*
    // transactionRequest example
    MOLPayService mpservice = new MOLPayService();
    mpservice.transactionRequest(paymentDetails, new MOLPayService.Callback(){

    @Override
    public void onTransactionRequestCompleted(String result){
    Log.d(MOLPayActivity.MOLPAY,"onTransactionRequestCompleted result= "+ result);
    }

    @Override
    public void onTransactionRequestFailed(String error){
    Log.d(MOLPayActivity.MOLPAY,"onTransactionRequestCompleted result= "+error);
    }
  });
        */
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
            String p = null;

            try{
                s= json.getString("transaction");
                Log.d("Msg",json.getString("transaction"));
                if(s.equals("success")) {

                    String alldata = "Email: " + json.getString("email") + "\n"
                            + "Name: " + json.getString("name") + "\n"
                            //+ "University: " + json.getString("university") + "\n"
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data ){
        super.onActivityResult(requestCode, resultCode,data);
        Bundle extras = getIntent().getExtras();
        String email =extras.getString("username");
        String value =extras.getString("topup");

        if(requestCode== MOLPayActivity.MOLPayXDK && resultCode==RESULT_OK){
            Log.d(MOLPayActivity.MOLPAY,"MOLPay result="+data.getStringExtra(MOLPayActivity.MOLPayTransactionResult));
             tv=(TextView)findViewById(R.id.resultTV);
             tv.setText(String.format("Payment status: %s \n Transaction Result: %s \n",
                     data.getStringExtra(MOLPayActivity.MOLPayPaymentDetails),
                     data.getStringExtra(MOLPayActivity.MOLPayTransactionResult)));


             // send the value to WebService
            SoapObject request = new SoapObject(WS_NAMESPACE,OPERATION_NAME);
            request.addProperty("value",value);

            SoapSerializationEnvelope envelope= new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet=true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE httpTransport= new HttpTransportSE(SOAP_ADDRESS);

            try{
                httpTransport.call(SOAP_ACTION,envelope);
                Object response=envelope.getResponse();
                tv.append(response.toString());
            }catch(Exception e){
                String exception = e.toString();
                tv.append(exception);
                Log.i("TAG",exception);
            }


        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
