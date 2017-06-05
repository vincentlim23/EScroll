package com.qrscanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;


public class LoginMainActivity extends FragmentActivity {

    private WebView mWebview;
    private Context context = this;
    TextView checklogin;
    EditText etName;
    private GoogleApiClient mGoogleApiClient;
    Bitmap bmp=null;
    private String ipaddress="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_layout);
        Bundle extras=getIntent().getExtras();
        checklogin=(TextView)findViewById(R.id.tv_checklogin);
        if (extras !=null){
            String value =extras.getString("username");
            checklogin.setText("Welcome back, "+value);
        }
        //profile button
        Button btn_profilelogo= (Button)findViewById(R.id.btn_profile_logo);
        btn_profilelogo.setOnClickListener(new View.OnClickListener(){
            @Override
        public void onClick(View view){
                Bundle extras = getIntent().getExtras();

                //Intent intent = getIntent();
                //bmp=extras.getParcelable("image");
                //bmp=(Bitmap)intent.getParcelableExtra("image");

               // if(checklogin.getVisibility()== View.VISIBLE)
               // {
               //     Intent i = new Intent(getApplicationContext(), Profile.class);
               //     i.putExtra("username",extras.getString("username"));
               //     i.putExtra("email",extras.getString("email"));
               //     i.putExtra("image", extras.getString("image"));
               //     startActivity(i);

               // }
               // else{
               //     Toast.makeText(getApplicationContext(),"Please Log in to proceed.",Toast.LENGTH_SHORT).show();
               // }
            }

        });

        // scanQR button
        Button btn_scanlogo= (Button)findViewById(R.id.btn_scan_logo);
        btn_scanlogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check url
                SharedPreferences prefs= getApplicationContext().getSharedPreferences("url",MODE_PRIVATE);
                String newURL= prefs.getString("string_url", "no url");
                if(newURL.isEmpty() || newURL.equals("no url")){

                    final AlertDialog.Builder builder = new AlertDialog.Builder(LoginMainActivity.this);
                    builder.setTitle("No URL found");
                    builder.setMessage("Please insert an URL through Setting!");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                             dialog.cancel();

                        }
                    });
                    builder.show();
             }else
                {

                //launching scan screen
                 Bundle extras = getIntent().getExtras();
                if(checklogin.getVisibility()== View.VISIBLE)
                {
                    Intent i = new Intent(getApplicationContext(), ScanQR.class);
                    i.putExtra("filename",extras.getString("username"));
                    i.putExtra("image",extras.getString("image"));
                   //i.putExtra("url",extras.getString("url"));
                    startActivity(i);

                }
                else{
                    Toast.makeText(getApplicationContext(),"Please Log in to proceed.",Toast.LENGTH_SHORT).show();
                }
            }
            }
        });

        // history calendar button
        Button btn_history= (Button)findViewById(R.id.btn_history_logo);
        btn_history.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View view){
                //launching history screen

                //check url
                SharedPreferences prefs= getApplicationContext().getSharedPreferences("url",MODE_PRIVATE);
                String newURL= prefs.getString("string_url", "no url");
                if(newURL.isEmpty() || newURL.equals("no url")){

                    final AlertDialog.Builder builder = new AlertDialog.Builder(LoginMainActivity.this);
                    builder.setTitle("No URL found");
                    builder.setMessage("Please insert an URL through Setting!");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                        }
                    });
                    builder.show();
                }
                else{
                    Bundle extras = getIntent().getExtras();
                    Intent i = new Intent(getApplicationContext(),HistoryCalendar.class);
                    i.putExtra("filename",extras.getString("username"));
                    i.putExtra("image",extras.getString("image"));

                    startActivity(i);
                }
            }
        });

        // Feedback Feature , currently only xlarge\fragment_layout.xml
        Button btn_feedback=(Button)findViewById(R.id.btn_feedback_logo);
        btn_feedback.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View view){
                //launching feedback
                //Bundle extras = getIntent().getExtras();
                //i.putExtra("filename",extras.getString("username"));
                Intent email= new Intent(Intent.ACTION_SENDTO);
                email.setType("text/email");
                email.setData(Uri.parse("mailto:"));
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"escrolladmin@iris.com"});
                //email.putExtra(Intent.EXTRA_CC,new String[]{extras.getString("username")});
                email.putExtra(Intent.EXTRA_SUBJECT,"E-Scroll Feedback ");
                email.putExtra(Intent.EXTRA_TEXT,"Dear Admin,"+"\n\n");
                startActivity(Intent.createChooser(email,"Send Feedback:"));
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

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("url", Context.MODE_PRIVATE);
        //SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor= prefs.edit();
        editor.clear();
        editor.commit();

        Intent myIntent = new Intent(getApplicationContext(), LoginGoogle.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(myIntent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_layout, menu);
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

          }else if(id == R.id.exit){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Exit Alert");
            builder.setMessage("Do you want to exit? ");


            builder.setNegativeButton(R.string.yes_button, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int id) {

                    SharedPreferences prefs = getApplicationContext().getSharedPreferences("url", Context.MODE_PRIVATE);
                    //SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor= prefs.edit();
                    editor.clear();
                    editor.apply();

                    finish();
                }
            });

            builder.setPositiveButton(R.string.no_button, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int id) {
                }
            });
            builder.show();

            return true;
        }else if(id== R.id.logout){
            final ProgressDialog progressDialog = new ProgressDialog(LoginMainActivity.this,R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Redirecting...");
            progressDialog.show();

            SharedPreferences prefs = getApplicationContext().getSharedPreferences("url", Context.MODE_PRIVATE);
            //SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor= prefs.edit();
            editor.clear();
            editor.apply();

            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    // Toast.makeText(getApplicationContext(), "Redirecting....", Toast.LENGTH_SHORT).show();
                    Intent logoutPage = new Intent(getApplicationContext(), SplashScreen.class);
                    //logoutPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(logoutPage);
                    finish();
                    progressDialog.dismiss();
                }
            }, 2000);

            return true;
        }

        else if(id== R.id.EnterURL){
             // String ipAddress="";
            final EditText input = new EditText(this);
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Please enter IP");
            builder.setMessage("Example: 172.16.252.137, 172.16.252.138 ");
            input.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    ipaddress = input.getText().toString();
                    Context mContext = getApplicationContext();
                    //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences prefs = mContext.getSharedPreferences("url", Context.MODE_PRIVATE);
                    String prefValue = prefs.getString("string_url", null);
                    if (prefValue != null) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.remove("string_url");
                        editor.clear().apply();
                        editor.putString("string_url", ipaddress); //InputString: from the EditText
                        editor.commit();

                    } else {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("string_url", ipaddress); //InputString: from the EditText
                        editor.apply();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
       }

        return super.onOptionsItemSelected(item);
    }




}
