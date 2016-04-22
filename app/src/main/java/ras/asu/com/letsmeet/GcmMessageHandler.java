package ras.asu.com.letsmeet;

/**
 * Created by SahanaSekhar on 2/23/16.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import android.content.Context;
import android.app.PendingIntent;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GcmMessageHandler extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    NotificationManager mNotificationManager;
    String mes, from;
    private Handler handler;

    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        handler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
        mes = extras.getString("message");
        String action = extras.getString("action");
        from = extras.getString("sendersFbId");
        if (action != null && action.equalsIgnoreCase("invitation")) {
            sendNotification(mes + " from " + from, extras.getString("title"), from);
            showToast();

        }
        else if (action != null && action.equalsIgnoreCase("startNavigation")) {
            sendNotification(mes + " from " + from, extras.getString("title"), from);
            Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + extras.getString("lat").toString() + "," + extras.getString("longt").toString()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getBaseContext().startActivity(intent1);

        }
        else if (action != null && action.equalsIgnoreCase("letsmeetRequest")) {
            double latitude = 0;
            double longitude = 0;
            GPSTracker gps = new GPSTracker(GcmMessageHandler.this);
            if (gps.canGetLocation()) {

                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
            } else {
                gps.showSettingsAlert();
            }
            Map<String, String> jobj = new HashMap<String, String>();
            jobj.put("action", "sendMyCurrentLocation");
            jobj.put("lat", String.valueOf(latitude));
            jobj.put("long", String.valueOf(longitude));
            jobj.put("fbId2", ProjCostants.FB_ID);
            jobj.put("fbId", from);
            sendData(jobj);
            showToast();
        } else if (action != null && action.equalsIgnoreCase("getUsersCurrentLocation")) {
            GPSTracker gps = new GPSTracker(GcmMessageHandler.this);
            double latitude=0;
            double longitude=0;
            if (gps.canGetLocation()) {

                 latitude = gps.getLatitude();
                 longitude = gps.getLongitude();

                // \n is for new line
                //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            } else {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
            }
            //double latitude = LocationService.getLocationManager(getApplicationContext()).latitude;
            //double longitude = LocationService.getLocationManager(getApplicationContext()).longitude;

            extras.getString("long");
            // Intent intent1 = new Intent(this,MapsActivity.class);
            Intent dialogIntent = new Intent(getBaseContext(), MapsActivity.class);
            dialogIntent.setAction("fromReceiver");
            dialogIntent.putExtra("action", "fromReceiver");
            dialogIntent.putExtra("mylat", String.valueOf(latitude));
            dialogIntent.putExtra("mylong", String.valueOf(longitude));
            dialogIntent.putExtra("yourlat", extras.getString("lat"));
            dialogIntent.putExtra("yourlong", extras.getString("long"));
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                getBaseContext().startActivity(dialogIntent);
            }
            catch (Exception e) {
                e.printStackTrace();
            }    }
try {
    Log.i("GCM", "Received : (" + messageType + " )  " + extras.getString("title"));

    GcmBroadcastReceiver.completeWakefulIntent(intent);
}
catch (Exception e) {
    e.printStackTrace();
}
    }

    private void sendNotification(String msg, String title, String from) {
        /*
        Log.d("TAG", "Preparing to send notification...: " + msg);
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MenuList.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.backgroundpic)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Log.d("Notification", "Notification sent successfully.");


*/

        Intent receiverIntent = new Intent(this, MenuList.class);
        PendingIntent pReceiverIntent = PendingIntent.getActivity(this, 1, receiverIntent, 0);
        Intent clearIntent = new Intent(this, LoginActivity.class);
        clearIntent.setAction("cancelReq");
        clearIntent.putExtra("fbID", from);
        PendingIntent pClearIntent = PendingIntent.getActivity(this, 1, clearIntent, 0);

        Intent colorsIntent = new Intent(this, MenuList.class);
        colorsIntent.setAction("acceptReq");
        colorsIntent.putExtra("fbID", from);
        PendingIntent pColorsIntent = PendingIntent.getActivity(this, 1, colorsIntent, 0);
        NotificationCompat.Action cancel = new NotificationCompat.Action.Builder(R.drawable.cancel, "Cancel", pClearIntent).build();
        NotificationCompat.Action accept = new NotificationCompat.Action.Builder(R.drawable.accpt, "Accept", pColorsIntent).build();

        NotificationCompat.Builder builder;
        builder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.backgroundpic).setAutoCancel(false)
                .setContentTitle(title).setContentText(msg)
                .setContentIntent(pReceiverIntent).addAction(cancel).addAction(accept);
        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notification);
    }

    public void showToast() {
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), mes + " from " + from, Toast.LENGTH_LONG).show();
            }
        });

    }

    public void sendData(Map<String, String> paramsMap) {
        String result = "";

        URL serverUrl = null;
        try {
            serverUrl = new URL(ProjCostants.SERVER_ADDRESS);
            Log.d("URL PROBLEM", serverUrl.toString());
        } catch (MalformedURLException e) {
            Log.e("AppUtil", "URL Connection Error: "
                    + ProjCostants.SERVER_ADDRESS, e);
            result = ProjCostants.SERVER_ADDRESS;
        }
        StringBuilder postBody = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = paramsMap.entrySet()
                .iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> param = iterator.next();
            postBody.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                postBody.append('&');
            }
        }
        String body = postBody.toString();
        byte[] bytes = body.getBytes();
        HttpURLConnection httpCon = null;
        try {
            httpCon = (HttpURLConnection) serverUrl.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setUseCaches(false);
            httpCon.setFixedLengthStreamingMode(bytes.length);
            httpCon.setRequestMethod("GET");
            httpCon.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            OutputStream out = httpCon.getOutputStream();
            out.write(bytes);
            out.close();

            int status = httpCon.getResponseCode();
            if (status == 200) {
                result = "Success";
            } else {
                result = "Post Failure." + " Status: " + status;
            }
        } catch (Exception e) {
            Log.d("SOME ERROR", e.toString());
            httpCon.disconnect();
        }
        // return null;
    }

    private LocationManager mLocationManager;

    /**
     * try to get the 'best' location selected from all providers
     */
    /*
    private class LocationListener implements android.location.LocationListener{
        private
        Location mLastLocation;
        private String TAG = "Location";
        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }
        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
        }
        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }
        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }
    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };*//*
    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e("Location", "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }
    @Override
    public void onCreate()
    {
        Log.e("Location", "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i("Location", "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d("Location", "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i("Location", "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d("Location", "gps provider does not exist " + ex.getMessage());
        }
    }
    @Override
    public void onDestroy()
    {
        Log.e("Location", "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i("Location", "fail to remove location listners, ignore", ex);
                }
            }
        }
    }
    private void initializeLocationManager() {
        Log.e("Location", "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }*/
}