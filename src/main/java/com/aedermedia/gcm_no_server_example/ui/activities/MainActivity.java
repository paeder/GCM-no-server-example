package com.aedermedia.gcm_no_server_example.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aedermedia.gcm_no_server_example.R;
import com.aedermedia.gcm_no_server_example.gcm.Config;
import com.aedermedia.gcm_no_server_example.gcm.GcmNotification;
import com.aedermedia.gcm_no_server_example.utility.ConnectionDetector;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by paeder on 12/16/13.
 */
public class MainActivity extends Activity{

    public static final String SHOW_RESPONSE = "show_response";
    private RelativeLayout sendMessageLayout,showResponseLayout;

    private ConnectionDetector cd;
    private AsyncTask<Void, Void, Void> mRegisterTask;
    private Runnable checkForConnection;
    private Handler connectionHandler;
    private int timeInFuture = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendMessageLayout  = (RelativeLayout)findViewById(R.id.sendMessageLayout);
        showResponseLayout = (RelativeLayout)findViewById(R.id.showResponseLayout);

        Button simpleNotification = (Button)findViewById(R.id.simpleNotification);
        simpleNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Config.getMyRegId()!=null){

                    // list of regIds you want to send this message to. normally you could get this from your
                    // storage server of choice. For testing we are only sending one message to ourselves.
                    List<String> regIds = new ArrayList<String>();
                    regIds.add(Config.getMyRegId());

                    // basic notification fields
                    int nIcon = R.drawable.ic_launcher;
                    String alertText = "Simple Notification";
                    String titleText = "Notification Title";
                    String contentText = "tap for more info";

                    // just something you can recognize on the other side. helpful when sending multiple types of notifications
                    int nType = Config.SIMPLE_NOTIFICATION;

                    Map<String, String> msgParams;
                    msgParams = new HashMap<String, String>();
                    msgParams.put("data.alertText", alertText);
                    msgParams.put("data.titleText", titleText);
                    msgParams.put("data.contentText", contentText);
                    msgParams.put("data.nIcon",String.valueOf(nIcon));
                    msgParams.put("data.nType", String.valueOf(nType));

                    GcmNotification gcmNotification = new GcmNotification();
                    gcmNotification.sendNotification(msgParams,regIds, com.aedermedia.gcm_no_server_example.ui.activities.MainActivity.this);

                }else{
                    Toast.makeText(com.aedermedia.gcm_no_server_example.ui.activities.MainActivity.this,"regId is currently null",Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button resetActivity = (Button)findViewById(R.id.activityReset);
        resetActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent reset = getIntent();
                reset.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                reset.removeExtra("show_response");
                finish();

                startActivity(reset);
            }
        });

        checkForConnection = new Runnable()
        {
            @Override
            public void run()
            {
                cd = new ConnectionDetector(getApplicationContext());
                if(cd.isConnectingToInternet()){
                    registerDevice();
                }else{
                    if(timeInFuture<=32000){
                        timeInFuture = timeInFuture*2;
                    }else{
                        timeInFuture = 60000;
                    }
                    connectionHandler.postDelayed(checkForConnection,timeInFuture);
                }
            }
        };

        cd = new ConnectionDetector(getApplicationContext());
        if (!cd.isConnectingToInternet()) {
            connectionHandler = new Handler();
            connectionHandler.post(checkForConnection);
        }else{
            registerDevice();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(getIntent().hasExtra(SHOW_RESPONSE)){

            sendMessageLayout.setVisibility(View.GONE);
            showResponseLayout.setVisibility(View.VISIBLE);

        }else{

            showResponseLayout.setVisibility(View.GONE);
            sendMessageLayout.setVisibility(View.VISIBLE);
        }
    }

    public static byte[] generateUUID(){
        return ("YOUR_APP_NAME-" + UUID.randomUUID()).getBytes(Charset.forName("US-ASCII"));
    }

    public void registerDevice(){
        final Context context = this;
        mRegisterTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    // if you have not yet created a userId you could do it here
                    String userId = String.valueOf(generateUUID());

                    String regId = null;
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(com.aedermedia.gcm_no_server_example.ui.activities.MainActivity.this);
                    regId = gcm.register(Config.SENDER_ID);

                    if(regId!=null){

                        Log.i(Config.TAG,"device userId: "+userId+" gcm regId: "+regId);

                        // SAVE YOUR USERID AND REGID TO THE STORAGE PLACE OF YOUR CHOICE HERE
                        // for testing I am going to send messages back to this device
                        Config.setMyRegId(regId);

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                mRegisterTask = null;
            }
        };
        mRegisterTask.execute(null, null, null);
    }
}
