package sanity.com.AdviseME;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;
import AdviseME.R;

public class MyService extends FirebaseMessagingService {

    private static final String ADMIN_CHANNEL_ID = "admin_channel";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String CurrentUserId = user.getUid();

    public String postId;

    Uri notificationsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

   /* @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
     //do something with the new token
        if(user!= null){
            updateToken(s);
        }
        //// Retrieving existing token
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                String token = task.getResult().getToken();
                if (!task.isSuccessful()){
                   //Toast.makeText(getApplicationContext(),"Failed to getInstanceId " +task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    Log.d("FAILED: ", task.getException().getMessage());
                }
               else {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Tokens").child(CurrentUserId);
                    databaseReference.setValue(token);
               }

            }
        });
    }*/

  /*  private void updateToken(String tokenRefresh) {
        Token token = new Token(tokenRefresh);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Tokens").child(CurrentUserId);
        databaseReference.setValue(token);
    }*/

    @Override
    public void onMessageReceived(@NonNull final RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //build the notification

        if (remoteMessage.getData().size()> 0) {

            String notificationType = remoteMessage.getData().get("notification_type");
             if (notificationType.equals("Posts")) {
                 String sender = remoteMessage.getData().get("sender");
                 String Ptitle = remoteMessage.getData().get("title");
                 String Pdesc = remoteMessage.getData().get("description");

                  if (!sender.equals(CurrentUserId)) {
                     ShowNotification(Ptitle,Pdesc);
                 }
                  else{
                      String mydesc = "You have successfully added the post " ;
                      ShowNotification(Ptitle,mydesc);
                  }
             }

             else if (notificationType.equals("Comments")) {
                 // handle comment notifications

                 DatabaseReference userdb = FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUserId);
                 userdb.keepSynced(true);
                 userdb.addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                         String myfirst_name = dataSnapshot.child("First_name").getValue().toString();
                         String mylast_name = dataSnapshot.child("Last_name").getValue().toString();
                         String sex = dataSnapshot.child("Sex").getValue().toString();
                         String CurrentUserFullname = myfirst_name + " " + mylast_name;

                         String Commentor_id = remoteMessage.getData().get("commentor_id");
                         String PostAuthorFullname = remoteMessage.getData().get("post_author_name");
                         String PostAuthorId = remoteMessage.getData().get("post_author_id");
                         ////

                         postId = remoteMessage.getData().get("post_id");
                         ///////
                         String Commenttitle = remoteMessage.getData().get("title");
                         String Commentdesc = remoteMessage.getData().get("description");
                         String myTitle = Commenttitle + CurrentUserFullname;

                         if (CurrentUserId.equals(PostAuthorId) && !CurrentUserId.equals(Commentor_id) && !Commentor_id.equals(PostAuthorId)) {
                             String myDesc = Commentdesc + " commented on your post";
                             //ShowCommentNotification(myTitle,myDesc);
                         }
                         if (!CurrentUserId.equals(Commentor_id) && !CurrentUserId.equals(PostAuthorId) && !PostAuthorId.equals(Commentor_id)) {
                             String myDesc = Commentdesc + " added a comment to " + PostAuthorFullname  + "'s post";
                             //ShowCommentNotification(myTitle,myDesc);
                         }

                         if (PostAuthorId.equals(Commentor_id) && !PostAuthorId.equals(CurrentUserId) && !Commentor_id.equals(CurrentUserId)) {

                             if (sex.equals("Male")) {
                                 String myDesc = Commentdesc + " added a comment to his post";
                                 //ShowCommentNotification(myTitle,myDesc);
                             }
                             else if (sex.equals("Female")) {
                                 String myDesc = Commentdesc + " added a comment to her post";
                               //  ShowCommentNotification(myTitle,myDesc);
                             }
                         }
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError databaseError) {

                     }
                 });
             }
        }
    }

 /*   private void ShowCommentNotification(String myTitle, String myDesc) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        SharedPreferences sharedPreferences = getSharedPreferences("vib", Context.MODE_PRIVATE);
        SharedPreferences sharedPreference2 = getSharedPreferences("Sound", Context.MODE_PRIVATE);
        int Notification_Id  = new Random().nextInt(3000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            setupPostNotificationChannel(notificationManager);
        }

        Intent intent = new Intent(getApplicationContext(),comments_activity.class);
        intent.putExtra("the_post_id",postId);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icon);

        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(),0,intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,""+ ADMIN_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.logo1)
                .setColor(getResources().getColor( R.color.colorAccent))
                .setContentTitle(myTitle)
                .setContentText(myDesc)
                .setContentIntent(pi)
                //    .setLargeIcon(largeIcon)
                .setAutoCancel(true);

        if (sharedPreferences.getBoolean("Vibrate", true)) {
            Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vib.vibrate( new long[]{10,300,300,300},-1);
        }
        if (sharedPreference2.getBoolean("Sound", true)) {
            builder.setSound(notificationsound);
        }
        notificationManager.notify(Notification_Id,builder.build());
    }*/

    private void ShowNotification(String ptitle, String pdesc) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        SharedPreferences sharedPreferences = getSharedPreferences("vib", Context.MODE_PRIVATE);
        SharedPreferences sharedPreference2 = getSharedPreferences("Sound", Context.MODE_PRIVATE);
        int Notification_Id  = new Random().nextInt(3000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            setupPostNotificationChannel(notificationManager);
        }

        Intent intent = new Intent(getApplicationContext(),main_activity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);

       // Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icon);

        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(),0,intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,""+ ADMIN_CHANNEL_ID);
               builder.setSmallIcon(R.drawable.logo1)
                .setColor(getResources().getColor(R.color.colorAccent))
                .setContentTitle(ptitle)
                .setContentText(pdesc)
                .setContentIntent(pi)
                   //    .setLargeIcon(largeIcon)
                .setAutoCancel(true);

        if (sharedPreferences.getBoolean("Vibrate", true)) {
            Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vib.vibrate( new long[]{10,300,300,300},-1);
        }
        if (sharedPreference2.getBoolean("Sound", true)) {
            builder.setSound(notificationsound);
        }
        notificationManager.notify(Notification_Id,builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupPostNotificationChannel(NotificationManager notificationManager) {
        SharedPreferences sharedPreferences = getSharedPreferences("vib", Context.MODE_PRIVATE);
        SharedPreferences sharedPreference2 = getSharedPreferences("Sound", Context.MODE_PRIVATE);
        CharSequence channelname = "New Notification";
        String ChannelDesc = "Device to device notification";

        NotificationChannel adminchannel = new NotificationChannel(ADMIN_CHANNEL_ID,channelname, NotificationManager.IMPORTANCE_HIGH);
        adminchannel.setDescription(ChannelDesc);
        adminchannel.enableLights(true);
        adminchannel.setLightColor(Color.RED);

        if (sharedPreferences.getBoolean("Vibrate", true)) {
            adminchannel.enableVibration(true);
        }
        if (sharedPreference2.getBoolean("Sound", true)) {
          AudioAttributes attributes = new AudioAttributes.Builder()
                  .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                  .setUsage(AudioAttributes.USAGE_NOTIFICATION).build();
            adminchannel.setSound(notificationsound,attributes);
        }

        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminchannel);
        }
    }
}