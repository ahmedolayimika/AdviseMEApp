package sanity.com.AdviseME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import AdviseME.R;

public class profile_activity extends AppCompatActivity {
    private Button edit_profile_btn;
    private TextView current_user_email, current_first_name, current_last_name, current_age, current_address, current_phone, current_level, CurrentID;
    private ImageView current_image;
    private String image, first, last, address, age, phone, level, myID;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reff;
    private ProgressDialog pd;
    private FirebaseUser current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        current_user_email = findViewById(R.id.current_email);
        current_first_name = findViewById(R.id.current_first_name);
        current_last_name = findViewById(R.id.current_last_name);
        current_age = findViewById(R.id.current_age);
        current_address = findViewById(R.id.current_address);
        current_phone = findViewById(R.id.current_phone);
        current_image = findViewById(R.id.current_profile_image);
        current_level = findViewById(R.id.current_level);
        CurrentID = findViewById(R.id.currentID);

        pd = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        current_user = firebaseAuth.getCurrentUser();

        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(profile_activity.this, login_activity.class));
            finish();
        }

        current_user_email.setText(current_user.getEmail());

        pd.setMessage("Please wait . . .  ");
        pd.setTitle("Fetching details");
        pd.setCanceledOnTouchOutside(false);
        pd.show();


        reff = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user.getUid());
        reff.keepSynced(true);
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Toast.makeText(profile_activity.this, "Please update your profile", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(profile_activity.this, edit_profile_activity.class));
                } else {
                    first = dataSnapshot.child("first_name").getValue().toString();
                    last = dataSnapshot.child("last_name").getValue().toString();
                    address = dataSnapshot.child("address").getValue().toString();
                    age = dataSnapshot.child("age").getValue().toString();
                    phone = dataSnapshot.child("phone").getValue().toString();
                    level = dataSnapshot.child("level").getValue().toString();
                    myID = dataSnapshot.child("iD").getValue().toString();

                    current_first_name.setText(first);
                    current_last_name.setText(last);
                    current_address.setText(address);
                    current_age.setText(age);
                    current_phone.setText(phone);
                    current_level.setText(level);
                    CurrentID.setText(myID);
                }
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        reff.child("profile_Image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    image = dataSnapshot.getValue().toString();
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).into(current_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(image).into(current_image);

                        }
                    });
                    current_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent imageIntent = new Intent(profile_activity.this, image_display_activity.class);
                            Bundle imagebundle = new Bundle();
                            imagebundle.putString("the_image", image);
                            imagebundle.putString("the_image_name", first + " " + last);
                            imageIntent.putExtras(imagebundle);
                            startActivity(imageIntent);
                        }
                    });
                } else {
                    current_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(profile_activity.this, "Please update your profile ", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(profile_activity.this, edit_profile_activity.class));

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        edit_profile_btn = findViewById(R.id.edit_profile);
        edit_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(profile_activity.this, Update_Profile_Activity.class));
                finish();
            }
        });

        /*current_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                *//*Intent image_intent = new Intent(profile_activity.this, image_display_activity.class);
                Bundle image_bundle = new Bundle();
                image_bundle.putString("image",image);*//*

                startActivity(new Intent(profile_activity.this, image_display_activity.class));

            }
        });
*/
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(profile_activity.this, main_activity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
