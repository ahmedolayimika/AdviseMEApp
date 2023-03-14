package sanity.com.AdviseME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import AdviseME.R;


public class login_activity extends AppCompatActivity {
    private Button login_btn, reset_password_btn;
    private TextView forgot_password, register, login_txt;
    private EditText email_txtbox, password_txtbox, user_email_txtbox;
    String user_email, user_password;
    public View reset_password_popup, about_app_popup;
    private ImageView cancel_icon, close_dialog;

    private FirebaseAuth firebaseAuth;
    private AlertDialog dialog;
    ProgressDialog progressDialog, prod;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        login_btn = findViewById(R.id.login_btn);
        forgot_password = findViewById(R.id.forgot_password);
        register = findViewById(R.id.register);
        email_txtbox = findViewById(R.id.login_username);
        password_txtbox = findViewById(R.id.login_password);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        currentUser = firebaseAuth.getCurrentUser();
        prod = new ProgressDialog(this);

        if (currentUser != null) {
            startActivity(new Intent(login_activity.this, main_activity.class));
            finish();
        }

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signInUser();

            }
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(login_activity.this);
                reset_password_popup = inflater.inflate(R.layout.reset_password_popup, null);
                login_txt = reset_password_popup.findViewById(R.id.login_txt);
                user_email_txtbox = reset_password_popup.findViewById(R.id.user_email);
                cancel_icon = reset_password_popup.findViewById(R.id.cancel);
                reset_password_btn = reset_password_popup.findViewById(R.id.reset_password);

                final AlertDialog.Builder reset_alert = new AlertDialog.Builder(login_activity.this);
                reset_alert.setCancelable(false);
                reset_alert.setView(reset_password_popup);
                dialog = reset_alert.create();
                dialog.show();

                cancel_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                login_txt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                reset_password_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        reset_password();
                    }
                });
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(login_activity.this, register_activity.class));
                finish();
            }
        });


    }

    private void signInUser() {

        user_email = email_txtbox.getText().toString().trim();
        user_password = password_txtbox.getText().toString().trim();

        if (user_email.isEmpty()) {
            Toast.makeText(login_activity.this, "Please Enter your E-mail", Toast.LENGTH_SHORT).show();
        } else if (user_password.isEmpty()) {
            Toast.makeText(login_activity.this, "Please Enter your password", Toast.LENGTH_SHORT).show();
        } else {

            progressDialog.setMessage("Please wait . . . ");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(user_email, user_password)
                    .addOnCompleteListener(login_activity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            SharedPreferences sharedPreferences = getSharedPreferences("vib", Context.MODE_PRIVATE);
                            SharedPreferences NotifyPreferences = getSharedPreferences("Notify", Context.MODE_PRIVATE);
                            if (NotifyPreferences.getBoolean("Notify", true) && sharedPreferences.getBoolean("Vibrate", true)) {
                                Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                vib.vibrate(100);
                            }


                            if (task.isSuccessful()) {
                                Toast.makeText(login_activity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(login_activity.this, main_activity.class));
                                finish();

                            } else {
                                Toast.makeText(login_activity.this, "Login Failed!..... " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    public void reset_password() {
        String mail = user_email_txtbox.getText().toString().trim();

        if (mail.equals("")) {
            Toast.makeText(login_activity.this, "Please Enter your E-mail", Toast.LENGTH_LONG).show();
        } else {
            prod.setMessage("Please wait . . .");
            prod.setCanceledOnTouchOutside(false);
            prod.show();
            firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(login_activity.this, "A link has been sent to your e-mail", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(login_activity.this, "Failed to send link..." + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                    prod.dismiss();
                }
            });
        }
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Exit now?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finishAffinity();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.about) {
            LayoutInflater about_inflater = LayoutInflater.from(login_activity.this);
            about_app_popup = about_inflater.inflate(R.layout.about_app_popup, null);
            close_dialog = about_app_popup.findViewById(R.id.close);

            AlertDialog.Builder about_dialog = new AlertDialog.Builder(this);
            about_dialog.setView(about_app_popup);
            about_dialog.setCancelable(false);
            final AlertDialog dialog = about_dialog.create();
            dialog.show();

            close_dialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }
        return true;
    }
}