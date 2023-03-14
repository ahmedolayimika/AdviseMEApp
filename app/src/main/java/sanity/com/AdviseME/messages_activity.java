package sanity.com.AdviseME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import AdviseME.R;

public class messages_activity extends AppCompatActivity {
    private FloatingActionButton all_users;
    RecyclerView chatListRecycler;
    FirebaseAuth firebaseAuth;
    List<ModelChatList> chatListList;
    List<User_details> usersList;
    DatabaseReference ChatListRef, UsersRef;
    Query myquery;
    FirebaseUser currentuser;
    AdapterChatList adapterChatList;
    String myLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        all_users = findViewById(R.id.all_users);
        chatListList = new ArrayList<>();
        chatListRecycler = findViewById(R.id.chatList_recycler);
        firebaseAuth = FirebaseAuth.getInstance();
        currentuser = firebaseAuth.getCurrentUser();
        ChatListRef = FirebaseDatabase.getInstance().getReference().child("ChatList").child(currentuser.getUid());
        ChatListRef.keepSynced(true);
        ChatListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatListList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelChatList chatList = ds.getValue(ModelChatList.class);
                    chatListList.add(chatList);
                }
                LoadChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        all_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(messages_activity.this, all_users_activity.class));
                finish();
            }
        });


    }

    /*private void LoadChats() {
        usersList = new ArrayList<>();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        UsersRef.keepSynced(true);
        myLevel = UsersRef.child(currentuser.getUid()).child("level").toString();
        myquery = UsersRef.orderByChild("level").equalTo(myLevel);
        myquery.keepSynced(true);

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    users users = dataSnapshot.getValue(users.class);
                    for (ModelChatList chatList : chatListList) {
                        if (users.getUID() != null && users.getUID().equals(chatList.getId())) {
                            usersList.add(users);
                            break;
                        }
                    }

                    adapterChatList = new AdapterChatList(messages_activity.this, usersList);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(messages_activity.this);
                    layoutManager.setStackFromEnd(true);
                    layoutManager.setReverseLayout(true);
                    chatListRecycler.setLayoutManager(layoutManager);
                    // chatListRecycler.setHasFixedSize(true);
                    chatListRecycler.setAdapter(adapterChatList);


                    for (int i = 0; i < usersList.size(); i++) {
                        lastMessage(usersList.get(i).getUID());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

    private void LoadChats() {
        usersList = new ArrayList<>();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        UsersRef.keepSynced(true);

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User_details users = dataSnapshot.getValue(User_details.class);
                    for (ModelChatList chatList : chatListList) {
                        if (users.getuID() != null && users.getuID().equals(chatList.getId())) {
                            usersList.add(users);
                            break;
                        }
                    }

                    adapterChatList = new AdapterChatList(messages_activity.this, usersList);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(messages_activity.this);
                    layoutManager.setStackFromEnd(true);
                    layoutManager.setReverseLayout(true);
                    chatListRecycler.setLayoutManager(layoutManager);
                    // chatListRecycler.setHasFixedSize(true);
                    chatListRecycler.setAdapter(adapterChatList);


                    for (int i = 0; i < usersList.size(); i++) {
                        lastMessage(usersList.get(i).getuID());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void lastMessage(final String UserID) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chats");
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String theLastMessage = "default";
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat == null) {
                        continue;
                    }
                    String sender = chat.getSender();
                    String receiver = chat.getReceiver();

                    if (sender == null || receiver == null) {
                        continue;
                    }

                    if (chat.getReceiver().equals(currentuser.getUid()) && chat.getSender().equals(UserID) ||
                            chat.getSender().equals(currentuser.getUid()) && chat.getReceiver().equals(UserID)) {
                        theLastMessage = chat.getMessage();
                    }
                }

                adapterChatList.SetLastMessageMap(UserID, theLastMessage);
                adapterChatList.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(messages_activity.this, main_activity.class));
        finishAffinity();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
