package com.abishek.comidapartner.notification;
/**
 *
 * Rambir
 * 2nd April 2020
 * Mekvahan Customer App
 */

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.abishek.comidapartner.R;
import com.abishek.comidapartner.commonFiles.LoginSessionManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.net.URL;

import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE;


public class FirebaseMessaging extends FirebaseMessagingService {
    String TAG = "FirebaseMessaging";

    private RemoteMessage message;

    @Override
    public void onNewToken(@NonNull String s) {
        Log.e(TAG, "onNewToken: "+s);

        com.google.firebase.messaging.FirebaseMessaging.getInstance().subscribeToTopic("all");
        Log.e(TAG, "Subscribed to topics for all");



        LoginSessionManager lsm = new LoginSessionManager(this);

        // Store this token in shared preference
        lsm.setFCMToken(s);
        lsm.setIsFcmRegistered(false);



}

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        Log.e(TAG,"...........data_noti "+remoteMessage.getData());
        Log.e(TAG, "MessageReceived: Complete message --> "+remoteMessage );
        Log.e(TAG, "MessageReceived: From --> "+remoteMessage.getData().get("title"));
        Log.e(TAG, "MessageReceived: Data contents --> "+remoteMessage.getData().get("body"));
        Log.e(TAG, "MessageReceived: Notification contents --> "+remoteMessage.getData().get("image"));

        message = remoteMessage;
        // Check message for any special behaviour
       /* if (remoteMessage.getData().containsKey("special")) {
            processForSpecialMessage(remoteMessage.getData());
            return;
        }
*/

        // Make and show notification using data contents
        showNotification(remoteMessage.getData().get("title"),remoteMessage.getData().get("body"),remoteMessage.getData().get("image"));

        // Store this notification in our local database to display in NOTIFICATION section

        storeNotification(remoteMessage.getData().get("title"),remoteMessage.getData().get("body"),remoteMessage.getData().get("image"));


        // Sending Broadcast to NotificationHomePage.class so that it user is on that activity, the activity will know about it
        // Doing so the activity can behave accordingly, example refresh the notification list.
        // We will send this broadcast for every notification irrespective of their type i.e. new booking, confirmation, update
        sendBroadcast();
        autoRefresh();
        super.onMessageReceived(remoteMessage);



    }


    private void showNotification(String title,String body,String image) {

        Log.e(TAG, "Notification: High Priority" );


        // Default launcher activity if nothing is specified in notification data
        Intent intent = new Intent(this, NotificationHomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        // To run intent activity without restarting app if possible
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        // Our notification channel
        String channelId = "comida";


        // Default notification tone
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        // Building notification
        final NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)     /* channel id  */
                        .setSmallIcon(R.mipmap.ic_launcher)              /* small battery like icon */
                        .setContentTitle(title)                 /* Winner Winner    */
                        .setContentText(body)                   /* You have won...   */
                        .setStyle(new NotificationCompat.BigTextStyle()     /* display multiline text  */
                                .bigText(body))
                        .setAutoCancel(true)                                /* touch to dismiss */
                        .setSound(defaultSoundUri)                          /* notification sound */
                        .setPriority(NotificationManager.IMPORTANCE_HIGH)   /* high are shown at top   */
                        .setContentIntent(pendingIntent);                   /* touch to launch  */


        // This will take care of the our notification in the pool of 'em
        final NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Comida",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }


        //  Background task to download image for the notification if link is provided
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bmp = BitmapFactory.decodeStream(new URL(BASE+image).openConnection().getInputStream());
                    notificationBuilder.setLargeIcon(bmp)
                        .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bmp)
                        .bigLargeIcon(null));

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {

                    // Handing over our notification for display on device
                    notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                }
            }
        });


    }

    private void storeNotification(String title,String body,String image) {


        String date = String.valueOf(System.currentTimeMillis()/1000L);

        // constructor for notification model
        MyNotificationTable myNotification = new MyNotificationTable(title, body, image, date, "received via fcm");

        // instance of Notification database
        NotificationDao myNotificationDB = ComidaDatabase.getDatabase(FirebaseMessaging.this).getMyNotificationDao();

        // insert newly created notification object into this database
        myNotificationDB.insertNotification(myNotification);

        Log.e("Firebase: ","notification stored");
    }

    private void sendBroadcast() {
        // Broadcast for refreshing notifications
        Intent broadcastIntent = new Intent("REFRESH_NOTIFICATIONS");

        // Sending for our application only
        broadcastIntent.setPackage(getPackageName());

        // Sending the broadcast
        sendBroadcast(broadcastIntent);
    }



    private void autoRefresh(){

        Intent broadcastIntent = new Intent("REFRESH_BOOKINGS");

        // Sending for our application only
        broadcastIntent.setPackage(getPackageName());

        // Sending the broadcast
        sendBroadcast(broadcastIntent);
    }


}
