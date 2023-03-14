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
    private TextView camera, gallery;
    private View upload_image;
    private Uri selected_img = null;
    private static final int GALLERY_REQUEST = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private int GALLERY_PERMISSION_CODE = 3;
    private int CAMERA_PERMISSION_CODE = 4;
    Vibrator vib;
    private String post_title, post_desc, currentDate, currentTime, timeStamp, author, author_Email, recipients;
    private ProgressDialog updateProgressDialog;
    private String img_upload_url, first_name, last_name, userID;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference, user_db;
    Spinner spinner;

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

        spinner = findViewById(R.id.recipients_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Level, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                recipients = adapterView.getItemAtPosition(i).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        new_post_image.setOnClickListener(new View.OnClickListener() {
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
        });

        share_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_title = new_post_title.getText().toString().trim();
                post_desc = new_post_desc.getText().toString().trim();

                if (selected_img == null) {

                    vib.vibrate(new long[]{10, 200, 100, 200}, -1);

                    Snackbar.make(view, "Your post needs an image", BaseTransientBottomBar.LENGTH_LONG)
                            .setAction("Okay", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            }).show();
                } else {

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
                }
            }
        });


    }

    private void sharePost() {

        Calendar mycal = Calendar.getInstance();
        SimpleDateFormat Date = new SimpleDateFormat("EEEE, MMMM d, yyyy,");
        currentDate = Date.format(mycal.getTime());

        SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss a");
        currentTime = time.format(mycal.getTime());

        timeStamp = currentDate + (" ") + currentTime;

        StorageReference imagepath = storageReference.child("Post Images").child("Post on: " + timeStamp);
        imagepath.putFile(selected_img)
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double prog = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        updateProgressDialog.setMessage("Submitting your post . . . " + (int) prog + "%");
                        updateProgressDialog.setCancelable(false);
                        updateProgressDialog.show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                img_upload_url = uri.toString();

                                String image = img_upload_url;
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
                                newpost.child("Recipients").setValue(recipients);

                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                SharedPreferences sharedPreferences = getSharedPreferences("vib", Context.MODE_PRIVATE);
                                SharedPreferences sharedPreference2 = getSharedPreferences("Notify", Context.MODE_PRIVATE);

                                if (sharedPreferences.getBoolean("Vibrate", true) && sharedPreference2.getBoolean("Notify", true)) {
                                    Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                    vib.vibrate(200);
                                }

                                if (task.isSuccessful()) {
                                    /*PrepareNotification("New post available",
                                            "' " + post_title + " '" + " by " + author,
                                            "Notifications",
                                            "Posts");*/

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

    private void RequestReadStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(new_post_activity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(new_post_activity.this)
                    .setCancelable(false)
                    .setTitle("Permission required")
                    .setMessage("This app needs access to your gallery")
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(new_post_activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(new_post_activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERMISSION_CODE);
        }
    }

    private void RequestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(new_post_activity.this, Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(new_post_activity.this)
                    .setCancelable(false)
                    .setTitle("Permission required")
                    .setMessage("This app needs access to your phone's camera")
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(new_post_activity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(new_post_activity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera, CAMERA_REQUEST_CODE);
            } else {
                Toast.makeText(new_post_activity.this, "Permission to use camera is denied", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == GALLERY_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent gallery = new Intent();
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, GALLERY_REQUEST);
            } else {
                Toast.makeText(new_post_activity.this, "Permission to access gallery is denied", Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            Uri image_uri = data.getData();
            CropImage.activity(image_uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    // .setAspectRatio(1, 1)
                    .start(this);

        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

            Uri image_uri = data.getData();
            CropImage.activity(image_uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);

        }

        CropImage.ActivityResult result = null;

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                selected_img = result.getUri();
                new_post_image.setImageURI(selected_img);
            }

        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Exception error = result.getError();
            Toast.makeText(new_post_activity.this, error.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void PrepareNotification(String Title, String Description, String Topic, String NotificationType) {
        String NOTIFICATION_TOPIC = "/topics/" + Topic;
        String NOTIFICATION_TITLE = Title;
        String NOTIFICATION_MESSAGE = Description;
        String NOTIFICATION_TYPE = NotificationType;

        JSONObject notificationObject = new JSONObject();
        JSONObject notificationBody = new JSONObject();

        try {
            notificationBody.put("sender", userID);
            notificationBody.put("title", NOTIFICATION_TITLE);
            notificationBody.put("description", NOTIFICATION_MESSAGE);
            notificationBody.put("notification_type", NOTIFICATION_TYPE);

            notificationObject.put("to", NOTIFICATION_TOPIC);
            notificationObject.put("data", notificationBody);
        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        sendNotification(notificationObject);
    }

    private void sendNotification(JSONObject notificationObject) {
        JsonObjectRequest request = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("FCM_RESPONSE", "OnResponse: " + response.toString());
                        //Toast.makeText(getApplicationContext(),"Response: " + response.toString(),Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Failed to send:", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "key=AAAAEcvrkCQ:APA91bHIbNSfbqA1Y4wrbUFdxUMtzzPfKuUmZaiXVxgmX3OT1vt_WZggvkubJb9_ItxtsNuzIRck281JC2pb7AXSO8hvJsJ3KSNWUbCyzBMBr4z1_etgSfQ2hJIn-FdEaYcIJT9UVvIr");
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
        // Volley.newRequestQueue(this).add(request);
    }

}
