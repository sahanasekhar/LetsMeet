package ras.asu.com.letsmeet;

/**
 * Created by SahanaSekhar on 2/23/16.
 */
import android.app.Notification;
import android.app.NotificationManager;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import android.content.Context;
import android.app.PendingIntent;
import android.support.v4.app.NotificationCompat;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmMessageHandler extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    NotificationManager mNotificationManager;
    String mes,from;
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
        from = extras.getString("sendersFbId");
        sendNotification(extras.getString("message")+" from " +from,extras.getString("sendersFbId"));

        showToast();
        Log.i("GCM", "Received : (" +messageType+" )  "+extras.getString("title"));

        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }
    private void sendNotification(String msg , String title) {
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
        clearIntent.putExtra("action","cancelReq");
        PendingIntent pClearIntent = PendingIntent.getActivity(this, 1, clearIntent, 0);

        Intent colorsIntent = new Intent(this, MenuList.class);
        colorsIntent.setAction("acceptReq");
        clearIntent.putExtra("action", "acceptReq");
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

    public void showToast(){
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(),mes+" from "+from , Toast.LENGTH_LONG).show();
            }
        });

    }
}