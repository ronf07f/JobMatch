package com.example.jobmatch;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

public class MatchCheckService extends Service {

    private final FirebaseFirestore DB =FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String userId = mAuth.getCurrentUser().getUid();
    boolean firstRun= true;
    public MatchCheckService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       // firstRun = true;
        isMatched();

        final  String CHANNEL_ID = "Foreground";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            Notification.Builder notification = new Notification.Builder(this,CHANNEL_ID)
                    //.setContentText("text")
                    .setContentTitle("we check for new matches")
                    .setSmallIcon(R.mipmap.ic_launcher);
            startForeground(1001,notification.build());


        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void isMatched() {
       DB.collection(GlobalVerbs.USERS_COLLECTION).document(userId).collection(GlobalVerbs.MATCH).addSnapshotListener((snapshots, e) -> {
           Log.i("abc","match ");
           if (e != null) {

               Log.w("abc", "listen:error", e);
               return;
           }

           for (DocumentChange dc : snapshots.getDocumentChanges()) {
               switch (dc.getType()) {
                   case ADDED:
                       if(!firstRun) {
                           Log.d("abc", "New Match: ");
                           final  String CHANNEL_ID = "Foreground";
                           if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                               NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH);
                               getSystemService(NotificationManager.class).createNotificationChannel(channel);
                               Notification.Builder notification = new Notification.Builder(MatchCheckService.this,CHANNEL_ID)
                                       //.setContentText("text")
                                       .setContentTitle("You Got A NEW MATCH")
                                       .setSmallIcon(R.mipmap.ic_launcher);
                               NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MatchCheckService.this);

// notificationId is a unique int for each notification that you must define
                               notificationManager.notify(1123, notification.build());


                           }
                       }
                       break;
                   case MODIFIED:
                       Log.d("abc", "Modified Match");
                       break;
                   case REMOVED:
                       Log.d("abc", "Removed Match");
                       break;
               }
           }
           firstRun = false;


       });
        }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //boolean firstRun = true;
    }
}