package sanity.com.AdviseME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import AdviseME.R;

public class register_activity extends AppCompatActivity {
    private EditText reg_email, reg_password, confirm_reg_password;
    private Button register_now;
    private TextView login_now;
    private ProgressDialog progress;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        progress = new ProgressDialog(this);

        reg_email = findViewById(R.id.reg_email);
        //reg_username = findViewById(R.id.reg_username);
        reg_password = findViewById(R.id.reg_password);
        confirm_reg_password = findViewById(R.id.confirm_password);
        register_now = findViewById(R.id.register_btn);
        login_now = findViewById(R.id.login_now);

        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(register_activity.this, edit_profile_activity.class));
            finish();
        }

        login_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* startActivity(new Intent(register_activity.this, login_activity.class));
                finish();*/
                onBackPressed();
            }
        });

        register_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email, username, password1, password2;
                email = reg_email.getText().toString().trim();
                // username = reg_username.getText().toString().trim();
                password1 = reg_password.getText().toString().trim();
                password2 = confirm_reg_password.getText().toString().trim();

                if (email.isEmpty() || password1.isEmpty() || password2.isEmpty()) {
                    SharedPreferences sharedPreferences = getSharedPreferences("vib", Context.MODE_PRIVATE);
                    SharedPreferences sharedPreference2 = getSharedPreferences("Notify", Context.MODE_PRIVATE);
                    if (sharedPreferences.getBoolean("Vibrate", true) && sharedPreference2.getBoolean("Notify", true)) {
                        Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                        vib.vibrate(100);
                    }

                    Snackbar.make(view, "Please fill all fields", BaseTransientBottomBar.LENGTH_LONG).show();
                } else if (!password1.equals(password2)) {
                    SharedPreferences sharedPreferences = getSharedPreferences("vib", Context.MODE_PRIVATE);
                    SharedPreferences sharedPreference2 = getSharedPreferences("Notify", Context.MODE_PRIVATE);
                    if (sharedPreferences.getBoolean("Vibrate", true) && sharedPreference2.getBoolean("Notify", true)) {
                        Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                        vib.vibrate(100);
                    }

                    Snackbar.make(view, "Your passwords do not match", Snackbar.LENGTH_LONG).show();
                } else if (password1.length() < 6 || password2.length() < 6) {

                    SharedPreferences sharedPreferences = getSharedPreferences("vib", Context.MODE_PRIVATE);
                    SharedPreferences sharedPreference2 = getSharedPreferences("Notify", Context.MODE_PRIVATE);
                    if (sharedPreferences.getBoolean("Vibrate", true) && sharedPreference2.getBoolean("Notify", true)) {
                        Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                        vib.vibrate(100);
                    }
                    Snackbar.make(view, "Your password must be more than 5 characters", Snackbar.LENGTH_LONG).show();
                } else {
                    progress.setMessage("Registering user. Please wait . . . ");
                    progress.show();

                    firebaseAuth.createUserWithEmailAndPassword(email, password1)
                            .addOnCompleteListener(register_activity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    SharedPreferences sharedPreferences = getSharedPreferences("vib", Context.MODE_PRIVATE);
                                    SharedPreferences sharedPreference2 = getSharedPreferences("Notify", Context.MODE_PRIVATE);
                                    if (sharedPreferences.getBoolean("Vibrate", true) && sharedPreference2.getBoolean("Notify", true)) {
                                        Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                        vib.vibrate(100);
                                    }
                                    if (task.isSuccessful()) {
                                        Toast.makeText(register_activity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(register_activity.this, edit_profile_activity.class));
                                        finish();

                                    } else {
                                        Toast.makeText(register_activity.this, "Failed to register ..." + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                    progress.dismiss();

                                }

                            });

                    //Toast.makeText(register_activity.this, "Okay now", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(register_activity.this, login_activity.class));
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
