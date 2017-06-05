package com.qrscanner;

/**
 * Created by wllim on 3/8/16.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.auth.api.Auth;

public class LoginGoogle extends AppCompatActivity implements OnClickListener,ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{

    //private static final int SIGN_IN = 0;
    private static final int SIGN_IN =9001;
    // Logcat tag
    private static final String TAG = "LoginGoogle";

    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 200;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    //flag for Internet connection status
    Boolean isInternetPresent = false;
    ConnectionDetector cd;

    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private boolean mIntentInProgress;
    private TextView mStatusTextView;
    private boolean mSignInClicked;

   //private ConnectionResult mConnectionResult;

    private SignInButton btnSignIn;
    private Button btnSignOut, btnRevokeAccess,btnContinue;
    private ImageView imgProfilePic, imgGoogleLogo;
    private TextView txtName, txtEmail,txtLogin;
    private LinearLayout llProfileLayout;

    //Global variable for Bitmap
    Bitmap resultBmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_googlelogin);

        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
        btnSignOut = (Button) findViewById(R.id.btn_sign_out);
        btnRevokeAccess = (Button) findViewById(R.id.btn_revoke_access);
        imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        llProfileLayout = (LinearLayout) findViewById(R.id.llProfile);
        txtLogin=(TextView)findViewById(R.id.textViewlogin);
        btnContinue=(Button)findViewById(R.id.btn_continue);
        imgGoogleLogo=(ImageView)findViewById(R.id.googlepluslogo);

        Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadeintwosec);
        txtName.startAnimation(animationFadeIn);
        txtEmail.startAnimation(animationFadeIn);
        imgProfilePic.startAnimation(animationFadeIn);

        // Button click listeners
       // btnSignIn.setColorScheme(SignInButton.COLOR_LIGHT);
        btnSignIn.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);
        btnRevokeAccess.setOnClickListener(this);
        btnContinue.setOnClickListener(this);


        //Configure sign-in to request the user's ID, email address and basic
        //profile . ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestId()
                .build();

        // Initializing google plus api client
        // mGoogleApiClient = new GoogleApiClient.Builder(this)
        //      .addConnectionCallbacks(this)
        //    .addOnConnectionFailedListener(this)
        //          .addApi(Plus.API, Plus.PlusOptions.builder().build())
        //           .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
        //   .addScope(Plus.SCOPE_PLUS_LOGIN).build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }


    protected void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
//            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
        //mGoogleApiClient.connect();
    }
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
           // mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));

            String personName = acct.getId();
            //if()
            Uri personPhoto= acct.getPhotoUrl();

                txtName.setText("ID: "+personName);
                txtEmail.setText("Login as: "+acct.getEmail());
               if(personPhoto !=null)
               {
                String personPhotoUrl=personPhoto.toString();
                personPhotoUrl= personPhotoUrl.substring(0,personPhotoUrl.length()-2)+PROFILE_PIC_SIZE;
                new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);
               }
            else{
                   imgProfilePic.setImageResource(R.drawable.googleaccountimage);
               }

                updateUI(true);


        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_in:
                signInWithGplus();
                break;
            case R.id.btn_sign_out:
                // Signout button clicked
                signOutFromGplus();
                break;
            case R.id.btn_revoke_access:
                // Revoke access button clicked
                revokeGplusAccess();
                break;
            case R.id.btn_continue:

                //creating connection detector class instance
                cd= new ConnectionDetector(getApplicationContext());
                //get Internet status
                isInternetPresent=cd.isConnectingToInternet();
                //check for internet status
                if(isInternetPresent) {
                    continueLogin();
                }else{
                    showAlertDialog(LoginGoogle.this, "No Internet Connection", "Please connect to the internet", false);
                }
                break;
        }
    }
    /**
     * Method to resolve any signin errors
     * */
    private void resolveSignInError() {
          /*
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }*/
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
      /*
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
        */
        Log.d(TAG, "onConnectionFailed:" + result);
    }
    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            handleSignInResult(result);
        }
        /*
        switch (requestCode) {
            case SIGN_IN:
                if (responseCode == RESULT_OK) {
                    mSignInClicked = false;

                }
                mIntentInProgress = false;
                if (!mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }
                break;
        } */
       /*
        if (requestCode == SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
        */
    }
    @Override
    public void onConnected(Bundle arg0) {
        mSignInClicked = false;
        Toast.makeText(this, "Connected!", Toast.LENGTH_SHORT).show();
        // Get user's information
        //getProfileInformation();
        // Update the UI after signin
        updateUI(true);
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
        updateUI(false);
    }

    private void signInWithGplus() {
       // if (!mGoogleApiClient.isConnecting()) {
       //     mSignInClicked = true;
       //     resolveSignInError();
       // }
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, SIGN_IN);
    }

    /**
     * Updating the UI, showing/hiding buttons and profile layout
     * */
    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            btnSignIn.setVisibility(View.GONE);
            imgGoogleLogo.setVisibility(View.GONE);
            txtLogin.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.VISIBLE);
            btnContinue.setVisibility(View.VISIBLE);
            btnRevokeAccess.setVisibility(View.VISIBLE);
            llProfileLayout.setVisibility(View.VISIBLE);
        } else {
            //mStatusTextView.setText(R.string.signed_out);
            btnSignIn.setVisibility(View.VISIBLE);
            txtLogin.setVisibility(View.VISIBLE);
            imgGoogleLogo.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.GONE);
            btnContinue.setVisibility(View.GONE);
            btnRevokeAccess.setVisibility(View.GONE);
            llProfileLayout.setVisibility(View.GONE);
        }
    }
    /**
     * Fetching user's information name, email, profile pic
     * */


    /**
     * Sign-out from google
     * */
    private void signOutFromGplus() {
       // if (mGoogleApiClient.isConnected()) {
       //     Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
       //     mGoogleApiClient.disconnect();
       //     mGoogleApiClient.connect();
       //     updateUI(false);
       // }
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    private void continueLogin(){

        //WifiManager wifiMgr =(WifiManager)getSystemService(WIFI_SERVICE);
        //WifiInfo wifiInfo =wifiMgr.getConnectionInfo();
        //int ip =wifiInfo.getIpAddress();
        //final String ipAddress = Formatter.formatIpAddress(ip);

           //Create a file according to username
                    File dir = new File(Environment.getExternalStorageDirectory()+File.separator+"QrHistoryFolder");
                         boolean success=true;
                    if(!dir.exists()){
                        success=dir.mkdir();
                    }
                   if(success) {
                       File emailFile = new File(dir+"/"+File.separator+txtEmail.getText().toString().substring(9).trim());// + ".txt");
                          boolean successEmailFolder= true;
                       if(!emailFile.exists()){
                           successEmailFolder=emailFile.mkdir();
                       }
                     if(successEmailFolder){
                         final Calendar dateNow= Calendar.getInstance();
                         SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
                         final String Datenow;
                         Datenow = sdf.format(dateNow.getTime());
                         File historyFile= new File(emailFile,Datenow+".txt");
                       try {
                           Writer writer = new BufferedWriter(new FileWriter(historyFile, true));
                           writer.close();
                       } catch (Exception ex) {
                           Toast.makeText(getApplication(), "Error writing file", Toast.LENGTH_SHORT).show();
                       }
                   }
                   }
        final ProgressDialog progressDialog = new ProgressDialog(LoginGoogle.this,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Redirecting...");
        progressDialog.show();

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(getApplicationContext(),"Redirecting...",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), LoginMainActivity.class);
                i.putExtra("username", txtEmail.getText().toString().substring(9).trim());
                i.putExtra("email",txtEmail.getText().toString());
                //i.putExtra("url",ipAddress);

               if(resultBmp !=null) {
                   try {

                       //write file
                       String filename = "profilepic.png";
                       FileOutputStream stream = openFileOutput(filename, Context.MODE_PRIVATE);
                       resultBmp.compress(Bitmap.CompressFormat.PNG, 100, stream);

                       //cleanup
                       stream.close();
                       resultBmp.recycle();
                       i.putExtra("image", filename);
                   }catch(Exception e){
                       e.printStackTrace();
                   }

               }
                else{
                   try {
                       //write file
                       String filename = "profilepic.png";
                       FileOutputStream stream = openFileOutput(filename, Context.MODE_PRIVATE);
                       resultBmp =BitmapFactory.decodeResource(getResources(),R.drawable.googleaccountimage);
                       resultBmp.compress(Bitmap.CompressFormat.PNG, 100, stream);

                       //cleanup
                       stream.close();
                       resultBmp.recycle();
                       i.putExtra("image", filename);
                   }catch(Exception e){
                       e.printStackTrace();
                   }

               }
                startActivity(i);
                finish();

                progressDialog.dismiss();
            }
        }, 2000);

         }

    public void showAlertDialog(Context context,String title,String message, Boolean status){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent myIntent = new Intent(getApplicationContext(), SplashScreen.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(myIntent);
                finish();
            }
        });
        alertDialog.show();
    }
    /**
     * Revoking access from google
     * */
    private void revokeGplusAccess() {
       // if (mGoogleApiClient.isConnected()) {
        //    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
         //   Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
           //         .setResultCallback(new ResultCallback<Status>() {
             //           @Override
           //             public void onResult(Status arg0) {
            //                Log.e(TAG, "User access revoked!");
            //                mGoogleApiClient.connect();
             //               updateUI(false);
              //          }

         //           });
       // }
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
   /**
    * Background Async task to load user profile picture from url
    * */
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            resultBmp=result;
            bmImage.setImageBitmap(result);

        }
    }
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
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
        Intent myIntent = new Intent(getApplicationContext(), SplashScreen.class);

        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(myIntent);
        finish();
        return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_google, menu);
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


