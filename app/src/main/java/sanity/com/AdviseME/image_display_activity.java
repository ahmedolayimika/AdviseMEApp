package sanity.com.AdviseME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import AdviseME.R;


public class image_display_activity extends AppCompatActivity {

    private ImageView lone_image_view;
    String lone_image, image_title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_display_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lone_image_view = findViewById(R.id.lone_image);
        Intent myintent = this.getIntent();
        Bundle mybundle = myintent.getExtras();
        image_title = mybundle.getString("the_image_name");
        lone_image =mybundle.getString("the_image");
        Picasso.get().load(lone_image).networkPolicy(NetworkPolicy.OFFLINE).into(lone_image_view, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(lone_image).into(lone_image_view);
            }
        });
        this.setTitle(image_title);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
