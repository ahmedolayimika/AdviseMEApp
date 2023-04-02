package sanity.com.AdviseME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import AdviseME.R;

public class new_post_activity extends AppCompatActivity {
    private ImageView new_post_image;
    private EditText new_post_title, new_post_desc;
    private Button share_post;
    private TextView camera, gallery, recipients_textView;
    private View upload_image;
    private Uri selected_img = null;
    private static final int GALLERY_REQUEST = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private int GALLERY_PERMISSION_CODE = 3;
    private int CAMERA_PERMISSION_CODE = 4;
    Vibrator vib;
    private String post_title, post_desc, currentDate, currentTime, timeStamp, author, author_Email, recipients;
    private ProgressDialog updateProgressDialog;
    private String img_upload_url, first_name, last_name, userID, image;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference, user_db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_post_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences = getSharedPreferences("vib", Context.MODE_PRIVATE);
        SharedPreferences sharedPreference2 = getSharedPreferences("Notify", Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("Vibrate", true) && sharedPreference2.getBoolean("Notify", true)) {
            vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        }

        new_post_image = findViewById(R.id.post_image);
        share_post = findViewById(R.id.share_post_btn);
        new_post_title = findViewById(R.id.post_title);
        new_post_desc = findViewById(R.id.post_description);
        recipients_textView = findViewById(R.id.recipients_textView);

        updateProgressDialog = new ProgressDialog(this);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Updates");
        databaseReference.keepSynced(true);

        user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        user_db.keepSynced(true);
        user_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Toast.makeText(new_post_activity.this, " Please Set up your profile", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(new_post_activity.this, register_activity.class));
                    finish();
                } else {
                    first_name = dataSnapshot.child("first_name").getValue().toString();
                    last_name = dataSnapshot.child("last_name").getValue().toString();
                    image = dataSnapshot.child("profile_Image").getValue().toString();
                    recipients = dataSnapshot.child("level").getValue().toString();

                    recipients_textView.setText(recipients);


                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).into(new_post_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(image).into(new_post_image);

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        /*new_post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater layoutInflater = LayoutInflater.from(new_post_activity.this);
                upload_image = layoutInflater.inflate(R.layout.upload_image_popup, null);
                camera = upload_image.findViewById(R.id.camera);
                gallery = upload_image.findViewById(R.id.gallery);

                AlertDialog.Builder upload_alert = new AlertDialog.Builder(new_post_activity.this);
                upload_alert.setTitle("Upload Image From . . . ");
                upload_alert.setCancelable(true);
                upload_alert.setView(upload_image);
                final AlertDialog upload_dialog = upload_alert.create();
                upload_dialog.show();

                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ContextCompat.checkSelfPermission(new_post_activity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(camera, CAMERA_REQUEST_CODE);
                        } else {
                            RequestCameraPermission();
                        }
                        upload_dialog.dismiss();
                    }
                });

                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ContextCompat.checkSelfPermission(new_post_activity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            Intent gallery = new Intent();
                            gallery.setAction(Intent.ACTION_GET_CONTENT);
                            gallery.setType("image/*");
                            startActivityForResult(gallery, GALLERY_REQUEST);
                        } else {
                            RequestReadStoragePermission();
                        }
                        upload_dialog.dismiss();
                    }
                });

            }
        });*/

        share_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_title = new_post_title.getText().toString().trim();
                post_desc = new_post_desc.getText().toString().trim();

                /*if (selected_img == null) {

                    vib.vibrate(new long[]{10, 200, 100, 200}, -1);

                    Snackbar.make(view, "Your post needs an image", BaseTransientBottomBar.LENGTH_LONG)
                            .setAction("Okay", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            }).show();
                } else {*/

                if (post_title.isEmpty()) {
                    Snackbar.make(view, "Your post needs a title", BaseTransientBottomBar.LENGTH_LONG)
                            .setAction("Okay", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            }).show();
                } else if (post_desc.isEmpty()) {
                    Snackbar.make(view, "Your post needs a Description", BaseTransientBottomBar.LENGTH_LONG)
                            .setAction("Okay", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            }).show();
                } else if (post_desc.length() < 20) {
                    Snackbar.make(view, "Your Description is too short!", BaseTransientBottomBar.LENGTH_LONG)
                            .setAction("Okay", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            }).show();
                } else if (recipients.equals("")) {
                    Snackbar.make(view, "Please specify your recipients from the drop down", BaseTransientBottomBar.LENGTH_LONG)
                            .setAction("Okay", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            }).show();
                } else {
                    sharePost();
                }
                // }
            }
        });


    }

    private void sharePost() {
        updateProgressDialog.setCancelable(false);
        updateProgressDialog.show();

        Calendar mycal = Calendar.getInstance();
        SimpleDateFormat Date = new SimpleDateFormat("EEEE, MMMM d, yyyy,");
        currentDate = Date.format(mycal.getTime());

        SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss a");
        currentTime = time.format(mycal.getTime());

        timeStamp = currentDate + (" ") + currentTime;

        author = first_name + (" ") + last_name;
        author_Email = firebaseAuth.getCurrentUser().getEmail();

        DatabaseReference newpost = databaseReference.push();
        newpost.keepSynced(true);
        newpost.child("Title").setValue(post_title);
        newpost.child("Description").setValue(post_desc);
        newpost.child("Image").setValue(image);
        newpost.child("Author").setValue(author);
        newpost.child("Author_Email").setValue(author_Email);
        newpost.child("Date_and_Time").setValue(timeStamp);
        newpost.child("Author_ID").setValue(userID);
        newpost.child("Recipients").setValue(recipients)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        SharedPreferences sharedPreferences = getSharedPreferences("vib", Context.MODE_PRIVATE);
                        SharedPreferences sharedPreference2 = getSharedPreferences("Notify", Context.MODE_PRIVATE);

                        if (sharedPreferences.getBoolean("Vibrate", true) && sharedPreference2.getBoolean("Notify", true)) {
                            Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                            vib.vibrate(200);
                        }

                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Post uploaded successfully", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to upload" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        updateProgressDialog.dismiss();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(new_post_activity.this, main_activity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }



}
