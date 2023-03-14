package sanity.com.AdviseME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

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


public class all_users_activity extends AppCompatActivity {
    FirebaseUser currentUser;
    DatabaseReference usersdb;
    RecyclerView users_recycler;
    String user_id, myLevel;
    private Query myquery;
    AdapterUsers adapterUsers;
    //List<users> usersList;
    List<User_details> usersList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_users_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        user_id = currentUser.getUid();
        usersdb = FirebaseDatabase.getInstance().getReference().child("Users");
        usersdb.keepSynced(true);

        usersdb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    myLevel = snapshot.child(user_id).child("level").getValue().toString();
                    setTitle(myLevel);

                    myquery = usersdb.orderByChild("level").equalTo(myLevel);

                    myquery.keepSynced(true);
                    usersList = new ArrayList<>();
                    myquery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            usersList.clear();
                            for (DataSnapshot ds : snapshot.getChildren()) {
                               // users user = ds.getValue(users.class);
                                User_details user = ds.getValue(User_details.class);

                                if (!user.getuID().equals(user_id)) {
                                    usersList.add(user);

                                }
                                // usersList.add(user);
                                adapterUsers = new AdapterUsers(all_users_activity.this, usersList);
                                users_recycler.setAdapter(adapterUsers);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    /////////

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //this.setTitle("Class Members");

        users_recycler = findViewById(R.id.users_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        users_recycler.setLayoutManager(layoutManager);

        // usersList = new ArrayList<>();

   /*     usersdb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    users user = ds.getValue(users.class);

                    if (!user.getUID().equals(user_id)) {
                        usersList.add(user);

                    }
                    adapterUsers = new AdapterUsers(all_users_activity.this, usersList);
                    users_recycler.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(all_users_activity.this, messages_activity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
