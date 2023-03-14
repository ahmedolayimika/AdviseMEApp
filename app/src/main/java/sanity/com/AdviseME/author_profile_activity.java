package sanity.com.AdviseME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import AdviseME.R;

public class author_profile_activity extends AppCompatActivity {

    private ImageView authorImage;
    private TextView authorEmail, authorFullName,authorAge,authorAddress,authorPhone, authorSex, authorMatric, authorLevel;
    private DatabaseReference authordb;
    private ProgressDialog fetching;
    private String first,last,image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.author_profile_activity);
        authorImage = findViewById(R.id.author_img);
        authorEmail = findViewById(R.id.author_email);
        authorFullName = findViewById(R.id.author_full_name);
        authorAge = findViewById(R.id.author_age);
        authorAddress = findViewById(R.id.author_address);
        authorPhone = findViewById(R.id.author_phone);
        authorSex = findViewById(R.id.author_sex);
        authorMatric = findViewById(R.id.author_matric_number);
        authorLevel = findViewById(R.id.author_level);

        fetching = new ProgressDialog(this);

        Intent author = this.getIntent();
        Bundle mybundle = author.getExtras();
        String author_id = mybundle.getString("author_id");
        String author_name = mybundle.getString("author_name");

        this.setTitle(author_name);

        fetching.setMessage("Fetching Author's details . . .");
        fetching.setCancelable(false);
        fetching.show();
        authordb = FirebaseDatabase.getInstance().getReference().child("Users").child(author_id);
        authordb.keepSynced(true);
        authordb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                image = dataSnapshot.child("profile_Image").getValue().toString();
                first = dataSnapshot.child("first_name").getValue().toString();
                last = dataSnapshot.child("last_name").getValue().toString();
                String address = dataSnapshot.child("address").getValue().toString();
                String age = dataSnapshot.child("age").getValue().toString();
                String phone = dataSnapshot.child("phone").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                String sex = dataSnapshot.child("sex").getValue().toString();
               String matric = dataSnapshot.child("status").getValue().toString();
                String level= dataSnapshot.child("level").getValue().toString();


                Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).into(authorImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(image).into(authorImage);

                    }
                });
                authorEmail.setText(email);
                authorFullName.setText(first + " " + last);
                authorSex.setText(sex);
                authorAge.setText(age);
                authorAddress.setText(address);
                authorPhone.setText(phone);
                authorMatric.setText(matric);
                authorLevel.setText(level);
                fetching.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });

        authorImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showpersonalimage();
            }
        });

    }

    private void showpersonalimage() {
        Intent imageIntent = new Intent(author_profile_activity.this,image_display_activity.class);
        Bundle imagebundle = new Bundle();
        imagebundle.putString("the_image",image);
        imagebundle.putString("the_image_name",first + " " + last);
        imageIntent.putExtras(imagebundle);
        startActivity(imageIntent);

    }

}
