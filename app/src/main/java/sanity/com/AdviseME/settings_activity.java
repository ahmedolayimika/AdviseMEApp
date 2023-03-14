package sanity.com.AdviseME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import AdviseME.R;

public class settings_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_frame, new settings_activity.SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
            Preference changePassword = findPreference("changePassword");
            Preference switchAccount = findPreference("switchAccount");
            /*Preference notifypref = findPreference("notify");
            Preference soundpref = findPreference("sound");
            Preference vibpref = findPreference("vibration");*/

            /*-----------------------------------------------------------------------------------------------------------------------------------*/
            changePassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final EditText old_password_txt, new_password_txt, confirm_new_password_txt;
                    Button change_password_btn, cancel_btn;
                    ImageView close;
                    AlertDialog.Builder change_password_builder = new AlertDialog.Builder(getContext());
                    LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                    View change_password_view = layoutInflater.inflate(R.layout.change_password_popup, null);
                    change_password_builder.setView(change_password_view)
                            .setCancelable(false);
                    final AlertDialog change_password_dialog = change_password_builder.create();
                    change_password_dialog.show();

                    old_password_txt = change_password_view.findViewById(R.id.old_password);
                    new_password_txt = change_password_view.findViewById(R.id.new_password);
                    confirm_new_password_txt = change_password_view.findViewById(R.id.confirm_new_password);
                    change_password_btn = change_password_view.findViewById(R.id.change_password_btn);
                    cancel_btn = change_password_view.findViewById(R.id.cancel_btn);
                    close = change_password_view.findViewById(R.id.close);

                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            change_password_dialog.dismiss();
                        }
                    });

                    cancel_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            change_password_dialog.dismiss();
                        }
                    });

                    change_password_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            final ProgressDialog changeprog = new ProgressDialog(getContext());

                            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                            final FirebaseUser user = firebaseAuth.getCurrentUser();
                            String userMail = user.getEmail();

                            // String old_password, new_password, confirm_new_password;
                            String old_password = old_password_txt.getText().toString().trim();
                            final String new_password = new_password_txt.getText().toString().trim();
                            String confirm_new_password = confirm_new_password_txt.getText().toString().trim();

                            if (old_password.isEmpty() || new_password.isEmpty() || confirm_new_password.isEmpty()) {
                                Toast.makeText(getContext(), "No field can be empty", Toast.LENGTH_SHORT).show();
                            } else if (!new_password.equals(confirm_new_password)) {
                                Toast.makeText(getContext(), "Your new passwords do not match", Toast.LENGTH_SHORT).show();

                            } else if (old_password.equals(new_password)) {
                                Toast.makeText(getContext(), "Use a password that is not your old password", Toast.LENGTH_LONG).show();
                            } else {
                                changeprog.setMessage("Reauthenticating . . .");
                                changeprog.setTitle("Please wait...");
                                changeprog.setCancelable(false);
                                changeprog.setCanceledOnTouchOutside(false);
                                changeprog.show();

                                AuthCredential credential = EmailAuthProvider.getCredential(userMail, old_password);
                                user.reauthenticate(credential)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    changeprog.setMessage("Updating Password . . . ");
                                                    user.updatePassword(new_password)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task1) {
                                                                    if (task1.isSuccessful()) {
                                                                        Toast.makeText(getContext(), "Password Updated successfully", Toast.LENGTH_LONG).show();
                                                                        changeprog.dismiss();
                                                                        change_password_dialog.dismiss();
                                                                    } else {
                                                                        Toast.makeText(getContext(), "Failed to Change password... " + task1.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                                        changeprog.dismiss();
                                                                    }

                                                                }
                                                            });

                                                } else {
                                                    Toast.makeText(getContext(), "Failed to reauthenticate... " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                    changeprog.dismiss();
                                                }
                                            }

                                        });


                            }

                        }
                    });

                    //Toast.makeText(getContext(), "Not available", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            /*-----------------------------------------------------------------------------------------------------------------------------------*/
            switchAccount.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Switch Account")
                            .setCancelable(false)
                            .setMessage("Do you want to log out of this account?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    FirebaseAuth firebaseAuth =  FirebaseAuth.getInstance();
                                    firebaseAuth.signOut();
                                    startActivity(new Intent(getContext(),login_activity.class));
                                    getActivity().finishAffinity();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();

                    return true;
                }
            });

            /*-----------------------------------------------------------------------------------------------------------------------------------*/
            /*notifypref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    if (newValue.equals(true)) {

                        FirebaseMessaging.getInstance().subscribeToTopic("Notifications");

                        SharedPreferences sharedpref = getContext().getSharedPreferences("Notify", Context.MODE_PRIVATE);
                        SharedPreferences.Editor notifyedit = sharedpref.edit();
                        notifyedit.putBoolean("Notify", true);
                        notifyedit.apply();
                    } else {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("Notifications");

                        SharedPreferences sharedpref = getContext().getSharedPreferences("Notify", Context.MODE_PRIVATE);
                        SharedPreferences.Editor notifyedit = sharedpref.edit();
                        notifyedit.putBoolean("Notify", false);
                        notifyedit.apply();
                    }

                    return true;
                }
            });
            *//*-----------------------------------------------------------------------------------------------------------------------------------*//*

            soundpref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    if (newValue.equals(true)) {
                        SharedPreferences myshare = getContext().getSharedPreferences("Notify", Context.MODE_PRIVATE);
                        if (myshare.getBoolean("Notify", true)) {

                            SharedPreferences soundpreference = getContext().getSharedPreferences("Sound", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = soundpreference.edit();
                            editor.putBoolean("Sound", true);
                            editor.apply();
                        }
                    } else {
                        SharedPreferences soundpreference = getContext().getSharedPreferences("Sound", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = soundpreference.edit();
                        editor.putBoolean("Sound", false);
                        editor.apply();
                    }

                    return true;
                }
            });

            *//*-----------------------------------------------------------------------------------------------------------------------------------*//*

            vibpref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    if (newValue.equals(true)) {
                        SharedPreferences sharedpref = getContext().getSharedPreferences("Notify", Context.MODE_PRIVATE);
                        if (sharedpref.getBoolean("Notify", true)) {
                            Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(new long[]{10, 300, 200, 300}, -1);

                            SharedPreferences sharedPreferences = getContext().getSharedPreferences("vib", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("Vibrate", true);
                            editor.apply();
                        }

                    } else {
                        SharedPreferences sharedPreferences = getContext().getSharedPreferences("vib", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("Vibrate", false);
                        editor.apply();
                    }

                    return true;
                }
            });*/
            /*-----------------------------------------------------------------------------------------------------------------------------------*/
        }

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(settings_activity.this, main_activity.class));
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
