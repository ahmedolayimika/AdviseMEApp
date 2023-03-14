package sanity.com.AdviseME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import AdviseME.R;


public class edit_post_activity extends AppCompatActivity {

    private ImageView editImage;
    private EditText editTitle, editDesc;
    private Button editedbtn, canceledit;
    private String oldTitle, oldDesc, oldImage, post_ID, myEmail, first_name, last_name, authorname, author_ID, oldtime, oldRecipients;
    private String newTitle, newDesc, newImage, newImageUri, recipients;
    private DatabaseReference thispost, user_db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private static final int GALLERY_REQUEST = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private int GALLERY_PERMISSION_CODE = 3;
    private int CAMERA_PERMISSION_CODE = 4;
    private View upload_popup;
    private TextView camera_button, gallery_button;
    private Uri img_upload;
    private ProgressDialog editprog;
    private StorageReference storageReference;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_post_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //overridePendingTransition(R.anim.slide_out_right,R.anim.slide_in_right);


        editImage = findViewById(R.id.post_img_edit);
        editTitle = findViewById(R.id.post_title_edit);
        editDesc = findViewById(R.id.post_desc_edit);
        editedbtn = findViewById(R.id.submit_edited_post);
        canceledit = findViewById(R.id.cancel_edit);
        spinner = findViewById(R.id.spinner);
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

        editprog = new ProgressDialog(this);

        Intent myint = this.getIntent();
        Bundle mybund = myint.getExtras();
        oldTitle = mybund.getString("post_title");
        oldImage = mybund.getString("post_image");
        oldDesc = mybund.getString("post_desc");
        oldtime = mybund.getString("time");
        post_ID = mybund.getString("post_id");
        //oldRecipients = mybund.getString("recipients");

        thispost = FirebaseDatabase.getInstance().getReference().child("Updates").child(post_ID);
        thispost.keepSynced(true);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        myEmail = currentUser.getEmail();
        author_ID = currentUser.getUid();
        storageReference = FirebaseStorage.getInstance().getReference();

        user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid());
        user_db.keepSynced(true);
        user_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                first_name = dataSnapshot.child("First_name").getValue().toString();
                last_name = dataSnapshot.child("Last_name").getValue().toString();
                authorname = first_name + (" ") + last_name;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //this.setTitle(oldTitle);
        this.setTitle("Edit your post");
        editTitle.setText(oldTitle);
        editDesc.setText(oldDesc);

        /*String compareValue = oldRecipients;
        ArrayAdapter<CharSequence> NewAdapter = ArrayAdapter.createFromResource(edit_profile_activity.this, R.array.Level, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(NewAdapter);
        if (compareValue != null) {
            int spinnerPosition = NewAdapter.getPosition(compareValue);
            spinner.setSelection(spinnerPosition);
        }*/

        Picasso.get().load(oldImage).resize(600, 600).centerCrop().networkPolicy(NetworkPolicy.OFFLINE)
                .into(editImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(oldImage).resize(600, 600).centerCrop().into(editImage);
                    }
                });

        editedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder submitAlert = new AlertDialog.Builder(edit_post_activity.this);
                submitAlert.setCancelable(false)
                        .setMessage("Save Changes now?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SubmitEditedPost();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                submitAlert.create()
                        .show();


            }
        });

        canceledit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(edit_post_activity.this);
                upload_popup = inflater.inflate(R.layout.upload_image_popup, null);
                camera_button = upload_popup.findViewById(R.id.camera);
                gallery_button = upload_popup.findViewById(R.id.gallery);

                android.app.AlertDialog.Builder upload_alert = new android.app.AlertDialog.Builder(edit_post_activity.this);
                upload_alert.setTitle("Choose new Image from . . . ");
                upload_alert.setCancelable(true);
                upload_alert.setView(upload_popup);
                final android.app.AlertDialog upload_dialog = upload_alert.create();
                upload_dialog.show();

                camera_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ContextCompat.checkSelfPermission(edit_post_activity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(camera, CAMERA_REQUEST_CODE);
                        } else {
                            RequestCameraPermission();
                        }
                        upload_dialog.dismiss();
                    }
                });

                gallery_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ContextCompat.checkSelfPermission(edit_post_activity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
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

    }

    private void SubmitEditedPost() {
        if (img_upload == null) {
            newTitle = editTitle.getText().toString().trim();
            newDesc = editDesc.getText().toString().trim();
            if (newTitle.equals("") || newDesc.equals("") || recipients.equals("")) {
                SharedPreferences sharedPreferences = getSharedPreferences("vib", Context.MODE_PRIVATE);
                SharedPreferences sharedPreference2 = getSharedPreferences("Notify", Context.MODE_PRIVATE);

                if (sharedPreferences.getBoolean("Vibrate", true) && sharedPreference2.getBoolean("Notify", true)) {
                    Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    vib.vibrate(100);
                }
                Toast.makeText(edit_post_activity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {

                editprog.setMessage("Updating your post . . . ");
                editprog.setCancelable(false);
                editprog.show();
                Calendar mycal = Calendar.getInstance();
                SimpleDateFormat Date = new SimpleDateFormat("EEEE, MMMM d, yyyy,");
                String currentDate = Date.format(mycal.getTime());

                SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss a");
                String currentTime = time.format(mycal.getTime());

                final String timeStamp = currentDate + (" ") + currentTime;

                thispost.child("Title").setValue(newTitle);
                thispost.child("Description").setValue(newDesc);
                thispost.child("Recipients").setValue(recipients);
                thispost.child("Author").setValue(authorname);
                thispost.child("Author_Email").setValue(myEmail);
                thispost.child("Date_and_Time").setValue(oldtime);
                thispost.child("New_Date_and_Time").setValue("Last edited on: " + "\n" + timeStamp);
                thispost.child("Author_ID").setValue(author_ID)
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
                                    finish();
                                    startActivity(new Intent(edit_post_activity.this, main_activity.class));
                                } else {
                                    Toast.makeText(getApplicationContext(), "Failed to upload" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    return;
                                }
                                editprog.dismiss();
                            }
                        });
            }

        }
        else {
            newTitle = editTitle.getText().toString().trim();
            newDesc = editDesc.getText().toString().trim();

            if (newTitle.equals("") || newDesc.equals("") || recipients.equals("")) {
                SharedPreferences sharedPreferences = getSharedPreferences("vib", Context.MODE_PRIVATE);
                SharedPreferences sharedPreference2 = getSharedPreferences("Notify", Context.MODE_PRIVATE);

                if (sharedPreferences.getBoolean("Vibrate", true) && sharedPreference2.getBoolean("Notify", true)) {
                    Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    vib.vibrate(100);
                }
                Toast.makeText(edit_post_activity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Calendar mycal = Calendar.getInstance();
                SimpleDateFormat Date = new SimpleDateFormat("EEEE, MMMM d, yyyy,");
                String currentDate = Date.format(mycal.getTime());

                SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss a");
                String currentTime = time.format(mycal.getTime());

                final String timeStamp = currentDate + (" ") + currentTime;

                StorageReference imagepath = storageReference.child("Post Images").child("Post on: " + oldtime + " edited");
                imagepath.putFile(img_upload)
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                double prog = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                editprog.setMessage("Updating your post . . . " + (int) prog + "%");
                                editprog.setCancelable(false);
                                editprog.show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                newImageUri = uri.toString();

                                                newImage = newImageUri;

                                                thispost.child("Title").setValue(newTitle);
                                                thispost.child("Description").setValue(newDesc);
                                                thispost.child("Recipients").setValue(recipients);
                                                thispost.child("Image").setValue(newImage);
                                                thispost.child("Author").setValue(authorname);
                                                thispost.child("Author_Email").setValue(myEmail);
                                                thispost.child("Date_and_Time").setValue(oldtime);
                                                thispost.child("New_Date_and_Time").setValue("Last edited on: " + "\n" + timeStamp);
                                                thispost.child("Author_ID").setValue(author_ID);
                                            }
                                        })
                                        .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                SharedPreferences sharedPreferences = getSharedPreferences("vib", Context.MODE_PRIVATE);
                                                SharedPreferences sharedPreference2 = getSharedPreferences("Notify", Context.MODE_PRIVATE);

                                                if (sharedPreferences.getBoolean("Vibrate", true) && sharedPreference2.getBoolean("Notify", true)) {
                                                    Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                                    vib.vibrate(200);
                                                }

                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getApplicationContext(), "Post uploaded successfully", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                    startActivity(new Intent(edit_post_activity.this, main_activity.class));
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Failed to upload" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                    return;
                                                }
                                                editprog.dismiss();


                                            }

                                        });
                            }
                        });


            }
        }

    }

    private void RequestReadStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(edit_post_activity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new android.app.AlertDialog.Builder(edit_post_activity.this)
                    .setCancelable(false)
                    .setTitle("Permission required")
                    .setMessage("This app needs access to your gallery")
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(edit_post_activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERMISSION_CODE);
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
            ActivityCompat.requestPermissions(edit_post_activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERMISSION_CODE);
        }
    }

    private void RequestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(edit_post_activity.this, Manifest.permission.CAMERA)) {
            new android.app.AlertDialog.Builder(edit_post_activity.this)
                    .setCancelable(false)
                    .setTitle("Permission required")
                    .setMessage("This app needs access to your phone's camera")
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(edit_post_activity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
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
            ActivityCompat.requestPermissions(edit_post_activity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera, CAMERA_REQUEST_CODE);
            } else {
                Toast.makeText(edit_post_activity.this, "Permission to use camera is denied", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == GALLERY_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent gallery = new Intent();
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, GALLERY_REQUEST);
            } else {
                Toast.makeText(edit_post_activity.this, "Permission to access gallery is denied", Toast.LENGTH_LONG).show();
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
                    .setAspectRatio(1, 1)
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

                img_upload = result.getUri();
                editImage.setImageURI(img_upload);
            }

        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Exception error = result.getError();
            Toast.makeText(edit_post_activity.this, error.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder cancelAlert = new AlertDialog.Builder(edit_post_activity.this);
        cancelAlert.setCancelable(false)
                .setMessage("Do you want to discard changes?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).create().show();
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }*/

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
