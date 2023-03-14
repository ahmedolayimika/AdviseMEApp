package sanity.com.AdviseME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import AdviseME.R;


public class chat_activity extends AppCompatActivity {
    private androidx.appcompat.widget.Toolbar mytoolbar;
    ImageView person_image, BackButton;
    TextView personName, personStatus;
    RecyclerView chatRecycler;
    EditText theMessage;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference, seenRef;
    String his_name, his_image, his_UID, myUid;
    FloatingActionButton sendButton;

    ValueEventListener seenListener;
    List<ModelChat> chatList;
    AdapterChat adapterChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        mytoolbar = findViewById(R.id.mytoolbar);
        setSupportActionBar(mytoolbar);
        // mytoolbar.setTitle("");

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);
        myUid = firebaseAuth.getCurrentUser().getUid();


        person_image = findViewById(R.id.user_image_display);
        personName = findViewById(R.id.user_name_display);
        personStatus = findViewById(R.id.user_status);
        chatRecycler = findViewById(R.id.chat_recycler);
        theMessage = findViewById(R.id.the_message);
        sendButton = findViewById(R.id.send_button);
        BackButton = findViewById(R.id.back_Button);

        Intent chatIntent = this.getIntent();
        Bundle chatBundle = chatIntent.getExtras();
        his_name = chatBundle.getString("personName");
        his_image = chatBundle.getString("personImage");
        his_UID = chatBundle.getString("UID");

        personName.setText(his_name);
        Picasso.get().load(his_image).networkPolicy(NetworkPolicy.OFFLINE).into(person_image, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(his_image).into(person_image);

            }
        });

        databaseReference.child("Users").child(his_UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String typingStatus = snapshot.child("typingTo").getValue().toString();
                if (typingStatus.equals(myUid)) {
                    personStatus.setText("Typing . . .");
                } else {
                    String onlineStatus = snapshot.child("onlineStatus").getValue().toString();

                    if (onlineStatus.equals("Online")) {
                        personStatus.setText(onlineStatus);
                    } else {

                        Calendar mycal = Calendar.getInstance(Locale.ENGLISH);
                        mycal.setTimeInMillis(Long.parseLong(onlineStatus));
                        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", mycal).toString();

                        personStatus.setText("Last Seen at: " + dateTime);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = theMessage.getText().toString().trim();
                if (message.equals("")) {
                    //Toast.makeText(chat_activity.this, "Sorry, can't send empty message", Toast.LENGTH_SHORT).show();
                    sendButton.setEnabled(false);
                } else {

                    Calendar mycal = Calendar.getInstance();
                    SimpleDateFormat Date = new SimpleDateFormat("dd/MM/yyyy");
                    String currentDate = Date.format(mycal.getTime());

                    SimpleDateFormat time = new SimpleDateFormat("hh:mm a");
                    String currentTime = time.format(mycal.getTime());

                    String timeStamp = currentDate + " " + currentTime;
                    //String timestamp = String.valueOf(System.currentTimeMillis());

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", myUid);
                    hashMap.put("receiver", his_UID);
                    hashMap.put("message", message);
                    hashMap.put("timeStamp", timeStamp);
                    hashMap.put("isSeen", false);
                    databaseReference.child("Chats").push().setValue(hashMap);

                    theMessage.setText("");
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        chatRecycler.setHasFixedSize(true);
        chatRecycler.setLayoutManager(layoutManager);

        theMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() == 0) {
                    checkTypingStatus("NoOne");
                } else {
                    checkTypingStatus(his_UID);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        readMessages();

        seenMessage();

        final DatabaseReference ChatListRef1 = databaseReference.child("ChatList").child(myUid).child(his_UID);
        ChatListRef1.keepSynced(true);
        ChatListRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    ChatListRef1.child("id").setValue(his_UID);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final DatabaseReference ChatListRef2 = databaseReference.child("ChatList").child(his_UID).child(myUid);
        ChatListRef2.keepSynced(true);
        ChatListRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    ChatListRef2.child("id").setValue(myUid);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void seenMessage() {
        seenRef = databaseReference.child("Chats");
        seenListener = seenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat.getReceiver().equals(myUid) && chat.getSender().equals(his_UID)) {
                        HashMap<String, Object> hasSeen = new HashMap<>();
                        hasSeen.put("isSeen", true);
                        ds.getRef().updateChildren(hasSeen);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMessages() {
        chatList = new ArrayList<>();
        DatabaseReference chatRef = databaseReference.child("Chats");
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelChat chat = ds.getValue(ModelChat.class);

                    if (chat.getReceiver().equals(myUid) && chat.getSender().equals(his_UID) ||
                            chat.getReceiver().equals(his_UID) && chat.getSender().equals(myUid)) {
                        chatList.add(chat);
                    }

                    adapterChat = new AdapterChat(chat_activity.this, chatList, his_image);
                    adapterChat.notifyDataSetChanged();
                    chatRecycler.setAdapter(adapterChat);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkOnlineStatus(String status) {
        DatabaseReference myref = databaseReference.child("Users").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("OnlineStatus", status);
        myref.updateChildren(hashMap);

    }

    private void checkTypingStatus(String typing) {
        DatabaseReference myref = databaseReference.child("Users").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("TypingTo", typing);
        myref.updateChildren(hashMap);

    }

    @Override
    protected void onPause() {
        super.onPause();

        String lastSeen = String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(lastSeen);
        checkTypingStatus("NoOne");
        seenRef.removeEventListener(seenListener);
    }

    @Override
    protected void onStart() {
        checkOnlineStatus("Online");
        super.onStart();
    }

    @Override
    protected void onResume() {

        checkOnlineStatus("Online");
        super.onResume();
    }

}
