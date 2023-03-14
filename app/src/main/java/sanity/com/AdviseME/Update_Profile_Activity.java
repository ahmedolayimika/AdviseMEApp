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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
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

import AdviseME.R;

public class Update_Profile_Activity extends AppCompatActivity {

    private ImageView updated_image, upload_image_btn;
    private EditText updated_first_name, updated_last_name, updated_address, updated_age, updated_phone;
    private TextView camera, gallery;
    private Button save_info;
    private View upload_image;
    private Uri selected_img = null;
    private static final int GALLERY_REQUEST = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private int GALLERY_PERMISSION_CODE = 3;
    private int CAMERA_PERMISSION_CODE = 4;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog, progressDialog2, pd;
    private StorageReference image_storage;
    private String downloadUri, User_ID, currentFirstName, currentLastName, currentAge, currentPhone, currentAddress, currentImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

        updated_first_name = findViewById(R.id.update_first_name);
        updated_image = findViewById(R.id.update_profile_image);
        upload_image_btn = findViewById(R.id.camera_btn);
        updated_last_name = findViewById(R.id.update_last_name);
        updated_address = findViewById(R.id.update_address);
        updated_age = findViewById(R.id.update_age);
        updated_phone = findViewById(R.id.update_phone);
        save_info = findViewById(R.id.update_btn);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog2 = new ProgressDialog(this);
        pd = new ProgressDialog(this);
        User_ID = firebaseAuth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.keepSynced(true);
        image_storage = FirebaseStorage.getInstance().getReference().child("Profile Images");

        upload_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(Update_Profile_Activity.this);
                upload_image = layoutInflater.inflate(R.layout.upload_image_popup, null);
                camera = upload_image.findViewById(R.id.camera);
                gallery = upload_image.findViewById(R.id.gallery);

                AlertDialog.Builder upload_alert = new AlertDialog.Builder(Update_Profile_Activity.this);
                upload_alert.setTitle("Upload Image From . . . ");
                upload_alert.setCancelable(true);
                upload_alert.setView(upload_image);
                final AlertDialog upload_dialog = upload_alert.create();
                upload_dialog.show();

                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ContextCompat.checkSelfPermission(Update_Profile_Activity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
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
                        if (ContextCompat.checkSelfPermission(Update_Profile_Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
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

        databaseReference.child(User_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentFirstName = dataSnapshot.child("first_name").getValue().toString();
                    currentLastName = dataSnapshot.child("last_name").getValue().toString();
                    currentAddress = dataSnapshot.child("address").getValue().toString();
                    currentAge = dataSnapshot.child("age").getValue().toString();
                    currentPhone = dataSnapshot.child("phone").getValue().toString();


                    updated_first_name.setText(currentFirstName);
                    updated_last_name.setText(currentLastName);
                    updated_age.setText(currentAge);
                    updated_address.setText(currentAddress);
                    updated_phone.setText(currentPhone);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child(User_ID).child("profile_Image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentImage = dataSnapshot.getValue().toString();
                    Picasso.get().load(currentImage).networkPolicy(NetworkPolicy.OFFLINE).into(updated_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(currentImage).into(updated_image);
                        }
                    });

                    save_info.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (selected_img == null) {
                                final String First_name = updated_first_name.getText().toString().trim();
                                final String Last_name = updated_last_name.getText().toString().trim();
                                final String Address = updated_address.getText().toString().trim();
                                final String Age = updated_age.getText().toString().trim();
                                final String Phone = updated_phone.getText().toString().trim();
                                final String UID = firebaseAuth.getCurrentUser().getUid();


                                if (First_name.equals("") || Last_name.equals("") || Address.equals("") || Age.equals("") || Phone.equals("")) {
                                    SharedPreferences sharedPreferences = getSharedPreferences("vib", Context.MODE_PRIVATE);
                                    SharedPreferences sharedPreference2 = getSharedPreferences("Notify", Context.MODE_PRIVATE);
                                    if (sharedPreferences.getBoolean("Vibrate", true) && sharedPreference2.getBoolean("Notify", true)) {
                                        Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                        vib.vibrate(100);
                                    }
                                    Snackbar.make(getCurrentFocus(), "Please verify all fields", Snackbar.LENGTH_LONG).setAction("OK", null).show();

                                } else {

                                    progressDialog2.setMessage("Please Wait . . .");
                                    progressDialog2.setCancelable(false);
                                    progressDialog2.show();

                                    DatabaseReference userdb = databaseReference.child(UID);
                                    userdb.child("first_name").setValue(First_name);
                                    userdb.child("last_name").setValue(Last_name);
                                    userdb.child("address").setValue(Address);
                                    userdb.child("age").setValue(Age);
                                    userdb.child("phone").setValue(Phone)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    SharedPreferences sharedPreferences = getSharedPreferences("vib", Context.MODE_PRIVATE);
                                                    SharedPreferences sharedPreference2 = getSharedPreferences("Notify", Context.MODE_PRIVATE);
                                                    if (sharedPreferences.getBoolean("Vibrate", true) && sharedPreference2.getBoolean("Notify", true)) {
                                                        Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                                        vib.vibrate(100);
                                                    }

                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(Update_Profile_Activity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(Update_Profile_Activity.this, "Profile updated failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                    }

                                                    progressDialog2.dismiss();
                                                    onBackPressed();
                                                }
                                            });

                                }

                            } else {

                                final String First_name = updated_first_name.getText().toString().trim();
                                final String Last_name = updated_last_name.getText().toString().trim();
                                final String Address = updated_address.getText().toString().trim();
                                final String Age = updated_age.getText().toString().trim();
                                final String Phone = updated_phone.getText().toString().trim();
                                final String UID = firebaseAuth.getCurrentUser().getUid();


                                if (First_name.equals("") || Last_name.equals("") || Address.equals("") || Age.equals("") || Phone.equals("")) {
                                    SharedPreferences sharedPreferences = getSharedPreferences("vib", Context.MODE_PRIVATE);
                                    SharedPreferences sharedPreference2 = getSharedPreferences("Notify", Context.MODE_PRIVATE);
                                    if (sharedPreferences.getBoolean("Vibrate", true) && sharedPreference2.getBoolean("Notify", true)) {
                                        Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                        vib.vibrate(100);
                                    }
                                    Snackbar.make(getCurrentFocus(), "No field can be empty", Snackbar.LENGTH_LONG).setAction("OK", null).show();
                                } else {
                                    StorageReference filepath2 = image_storage.child(firebaseAuth.getCurrentUser().getEmail() + " profile Image");

                                    filepath2.putFile(selected_img)
                                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot2) {
                                                    double progress = (100 * taskSnapshot2.getBytesTransferred() / taskSnapshot2.getTotalByteCount());
                                                    pd.setMessage("Updating Profile . . . " + (int) progress + "%");
                                                    pd.setCancelable(false);
                                                    pd.show();
                                                }
                                            })
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            downloadUri = uri.toString();
                                                            String Profile_Image = downloadUri;
                                                            // User_details user_details = new User_details(First_name, Last_name, Address, Age, Phone, Profile_Image, Email, sex, UID, OnlineStatus, TypingTo, ID, Level, statusToSave);
                                                            // databaseReference.child(firebaseAuth.getCurrentUser().getUid()).setValue(user_details)
                                                            DatabaseReference userdb = databaseReference.child(UID);
                                                            userdb.child("first_name").setValue(First_name);
                                                            userdb.child("last_name").setValue(Last_name);
                                                            userdb.child("address").setValue(Address);
                                                            userdb.child("age").setValue(Age);
                                                            userdb.child("profile_Image").setValue(Profile_Image);
                                                            userdb.child("phone").setValue(Phone)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            SharedPreferences sharedPreferences = getSharedPreferences("vib", Context.MODE_PRIVATE);
                                                                            SharedPreferences sharedPreference2 = getSharedPreferences("Notify", Context.MODE_PRIVATE);
                                                                            if (sharedPreferences.getBoolean("Vibrate", true) && sharedPreference2.getBoolean("Notify", true)) {
                                                                                Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                                                                vib.vibrate(100);
                                                                            }

                                                                            if (task.isSuccessful()) {
                                                                                Toast.makeText(Update_Profile_Activity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                                                            } else {
                                                                                Toast.makeText(Update_Profile_Activity.this, "Profile updated failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                                            }

                                                                            pd.dismiss();
                                                                            onBackPressed();
                                                                        }
                                                                    });

                                                        }
                                                    });
                                                }
                                            });


                                }

                            }

                        }


                    });

                } else {
                    updated_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(Update_Profile_Activity.this, "Touch the camera icon to upload an image", Toast.LENGTH_SHORT).show();
                        }
                    });

                    save_info.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (selected_img == null) {

                                SharedPreferences sharedPreferences = getSharedPreferences("vib", Context.MODE_PRIVATE);
                                SharedPreferences sharedPreference2 = getSharedPreferences("Notify", Context.MODE_PRIVATE);
                                if (sharedPreferences.getBoolean("Vibrate", true) && sharedPreference2.getBoolean("Notify", true)) {
                                    Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                    vib.vibrate(100);
                                }
                                Toast.makeText(getApplicationContext(), "Please select an Image", Toast.LENGTH_SHORT).show();

                            } else {
                                final String First_name = updated_first_name.getText().toString().trim();
                                final String Last_name = updated_last_name.getText().toString().trim();
                                final String Address = updated_address.getText().toString().trim();
                                final String Age = updated_age.getText().toString().trim();
                                final String Phone = updated_phone.getText().toString().trim();
                                final String UID = firebaseAuth.getCurrentUser().getUid();


                                if (First_name.equals("") || Last_name.equals("") || Address.equals("") || Age.equals("") || Phone.equals("")) {
                                    SharedPreferences sharedPreferences = getSharedPreferences("vib", Context.MODE_PRIVATE);
                                    SharedPreferences sharedPreference2 = getSharedPreferences("Notify", Context.MODE_PRIVATE);
                                    if (sharedPreferences.getBoolean("Vibrate", true) && sharedPreference2.getBoolean("Notify", true)) {
                                        Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                        vib.vibrate(100);
                                    }
                                    Snackbar.make(getCurrentFocus(), "No field can be empty", Snackbar.LENGTH_LONG).setAction("OK", null).show();
                                } else {

                                    StorageReference filepath = image_storage.child(firebaseAuth.getCurrentUser().getEmail() + " profile Image");

                                    filepath.putFile(selected_img)
                                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                    double progress = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                                    progressDialog.setMessage("Updating Profile . . . " + (int) progress + "%");
                                                    progressDialog.setCancelable(false);
                                                    progressDialog.show();

                                                }
                                            })
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            downloadUri = uri.toString();

                                                            String Profile_Image = downloadUri;

                                                           /* User_details user_details = new User_details(First_name, Last_name, Address, Age, Phone, Profile_Image, Email, sex, UID, OnlineStatus, TypingTo, ID, Level, statusToSave);

                                                            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).setValue(user_details)*/
                                                            DatabaseReference userdb = databaseReference.child(UID);
                                                            userdb.child("first_name").setValue(First_name);
                                                            userdb.child("last_name").setValue(Last_name);
                                                            userdb.child("address").setValue(Address);
                                                            userdb.child("age").setValue(Age);
                                                            userdb.child("profile_Image").setValue(Profile_Image);
                                                            userdb.child("phone").setValue(Phone)

                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            SharedPreferences sharedPreferences = getSharedPreferences("vib", Context.MODE_PRIVATE);
                                                                            SharedPreferences sharedPreference2 = getSharedPreferences("Notify", Context.MODE_PRIVATE);
                                                                            if (sharedPreferences.getBoolean("Vibrate", true) && sharedPreference2.getBoolean("Notify", true)) {
                                                                                Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                                                                vib.vibrate(100);
                                                                            }

                                                                            if (task.isSuccessful()) {
                                                                                Toast.makeText(Update_Profile_Activity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                                                            } else {
                                                                                Toast.makeText(Update_Profile_Activity.this, "Profile updated failed ....." + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                                            }

                                                                            progressDialog.dismiss();
                                                                            onBackPressed();
                                                                        }
                                                                    });

                                                        }
                                                    });
                                                }
                                            });
                                }

                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void RequestReadStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Update_Profile_Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(Update_Profile_Activity.this)
                    .setCancelable(false)
                    .setTitle("Permission required")
                    .setMessage("This app needs access to your gallery")
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(Update_Profile_Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERMISSION_CODE);
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
            ActivityCompat.requestPermissions(Update_Profile_Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERMISSION_CODE);
        }
    }

    private void RequestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Update_Profile_Activity.this, Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(Update_Profile_Activity.this)
                    .setCancelable(false)
                    .setTitle("Permission required")
                    .setMessage("This app needs access to your phone's camera")
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(Update_Profile_Activity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
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
            ActivityCompat.requestPermissions(Update_Profile_Activity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera, CAMERA_REQUEST_CODE);
            } else {
                Toast.makeText(Update_Profile_Activity.this, "Permission to use camera is denied", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == GALLERY_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent gallery = new Intent();
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, GALLERY_REQUEST);
            } else {
                Toast.makeText(Update_Profile_Activity.this, "Permission to access gallery is denied", Toast.LENGTH_LONG).show();
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

                selected_img = result.getUri();
                updated_image.setImageURI(selected_img);
            }

        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Exception error = result.getError();
            Toast.makeText(Update_Profile_Activity.this, error.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Update_Profile_Activity.this, profile_activity.class));
        finish();
        //super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}