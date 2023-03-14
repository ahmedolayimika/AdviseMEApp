package sanity.com.AdviseME;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import AdviseME.R;

public class splash_screen_activity extends AppCompatActivity {
    public static int SPLASH_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_activity);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent main = new Intent(splash_screen_activity.this, login_activity.class);
                startActivity(main);
                finish();
            }
        }, SPLASH_TIME);
    }
}
