package com.qrscanner;

/**
 * Created by wllim on 2/19/16.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.integration.IntentIntegrator;
import com.google.integration.IntentResult;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;



public class ScanQR extends Activity {

    private Context context =this;
    private String Token;
    private String WSGetQRRet;
    Bitmap mBitmap;
    ImageView iv;
    String email,qrdata,pdf417data;


    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        startScan();
    }

    public void startScan(){
        try{
            IntentIntegrator integrator = new IntentIntegrator(this);
            //integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
            integrator.initiateScan(IntentIntegrator.DESIRED_TYPES);
            //integrator.initiateScan(IntentIntegrator.ALL_CODE_TYPES);
        }catch(ActivityNotFoundException ex){
            showDialog(this, "No Scanner Found", "Download a scanner code activity?", " Yes","No ");
        }
    }

    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo)
    {
        AlertDialog.Builder dlDialog = new AlertDialog.Builder(act);
        dlDialog.setTitle(title);
        dlDialog.setMessage(message);
        dlDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);

                } catch (ActivityNotFoundException ex) {

                }
            }
        });
        dlDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        return dlDialog.show();
    }
    //on ActivityResult method

    public void onActivityResult(int requestCode, int resultCode, Intent intent){

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if(result != null ){
             String content= result.getContents();
            if(content != null){
                vibrate();
                String formatname= result.getFormatName();
                String rawbyteToString = Util.toHexString(result.getRawBytes());
                String orientationToString  = String.valueOf(result.getOrientation());
                String errorToString = result.getErrorCorrectionLevel();

                String data = content + " ; "+formatname +" ; "+rawbyteToString+" ; "+
                        orientationToString+ " ; "+ errorToString ;

                Bundle extras = getIntent().getExtras();
                email = extras.getString("filename");

               //new startjavaService(qrdata).execute(email,content);
               // showResult(R.string.result_succeeded, result.toString(), content, qrdata);
              //  if(result.getFormatName() =="QR_CODE")
              //  {
                    showResult(R.string.result_scanned, result.toString(), content);
              //  }
              //  if(result.getFormatName()=="PDF_417")
              //  {
              //      showResultPDF417(R.string.result_scanned, result.toString(), content);
              //  }
                //UpdateHistory(data);
            }else
            {

               showResultNo(R.string.result_scanned, getString(R.string.result_failed_why),"NULL RESULT");
            }
        }else{
            finish();
        }
    }

    private void vibrate(){
        Vibrator vibrator= (Vibrator)getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    // private void showResult(int title,final String message)
    private void showResult(int title, CharSequence message, final String content)//,final String qrdata)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage("QR Code scanned successfully!");
        //builder.setMessage(message);

                builder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int id) {


                        final ProgressDialog progressDialog = new ProgressDialog(ScanQR.this, R.style.AppTheme_Dark_Dialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Redirecting...");
                        progressDialog.show();

                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Bundle extras = getIntent().getExtras();
                                String filename = extras.getString("filename");
                                Intent i = new Intent(ScanQR.this, VerifyQRwebService.class);
                                i.putExtra("filename", filename);
                                i.putExtra("image",extras.getString("image"));
                                i.putExtra("QRcontent", content);
                                i.putExtra("QRdata",qrdata);
                                startActivity(i);

                                progressDialog.dismiss();
                            }
                        }, 2000);

                    }
                });

        builder.show();

    }

    private void showResultPDF417(int title, CharSequence message, final String content)//,final String qrdata)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage("QR Code scanned successfully!");
        //builder.setMessage(message);

        builder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int id) {

                final ProgressDialog progressDialog = new ProgressDialog(ScanQR.this, R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Redirecting...");
                progressDialog.show();

                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Bundle extras = getIntent().getExtras();
                        String filename = extras.getString("filename");
                        Intent i = new Intent(ScanQR.this, VerifyQRwebService.class);
                        i.putExtra("filename", filename);
                        i.putExtra("PDF417content", content);
                       // i.putExtra("PDF417data",pdf417data);
                        //i.putExtra("url",extras.getString("url"));
                        startActivity(i);

                        progressDialog.dismiss();
                    }
                }, 2000);

            }
        });

        builder.show();

    }

    private void showResultNo(int title,  CharSequence message, final String content )
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int id) {
               ScanQR.this.finish();
            }
        });
        builder.show();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public  void onPause(){
        super.onPause();
    }

    public void UpdateHistory(String data){

        final Calendar dateNow= Calendar.getInstance();
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss;");
        String Datenow;
        Bundle extras=getIntent().getExtras();

        Date now = new Date();
        //String filename = sdf.format(now)+ extras.getString("filename");

        String filename = extras.getString("filename");
        File dir = Environment.getExternalStorageDirectory();
        File historyFile = new File(dir, "/QrHistoryFolder/" + filename + ".txt");

        if (historyFile.exists()) {
            try {
                Datenow = sdf.format(dateNow.getTime());
                Writer writer = new BufferedWriter(new FileWriter(historyFile, true));
                //writer.append(Datenow+":   "+data +"\n");
                writer.append(Datenow+data+"\n");
                writer.close();

            } catch(IOException io){
                Toast.makeText(getApplicationContext(), "Error writing on file", Toast.LENGTH_SHORT).show();
            }
        }   }


}
