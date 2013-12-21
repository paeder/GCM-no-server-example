package com.aedermedia.gcm_no_server_example.gcm;

/**
 * Created by paeder on 12/16/13.
 */
public class Config {

    public static final String TAG = "GCM_Example";
    public static final int SIMPLE_NOTIFICATION = 22;
    public static String MY_REG_ID;

    // Change the SENDER_ID and GCM_API_KEY to match your project number/server key from the Google Cloud Console
    public static final String SENDER_ID   = "809052790750";
    public static final String GCM_API_KEY = "AIzaSyBfHkgdrIbRM41rOF9FMupa-8M2lunjk2g";
    public static final String BASE_URL = "https://android.googleapis.com/gcm/send";

    public static String getMyRegId() {
        return MY_REG_ID;
    }

    public static void setMyRegId(String MY_REG_ID) {
        Config.MY_REG_ID = MY_REG_ID;
    }
}
