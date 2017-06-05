package com.qrscanner;

/**
 * Created by wllim on 4/20/16.
 */

import android.os.Bundle;
import android.app.Activity;

public class NotificationView extends Activity{

    public static final String Notification_ID="notiID";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);
    }
}
