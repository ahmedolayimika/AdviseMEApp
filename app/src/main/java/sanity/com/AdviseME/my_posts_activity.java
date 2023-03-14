package sanity.com.AdviseME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import AdviseME.R;


public class my_posts_activity extends AppCompatActivity {

    private RecyclerView myUpdates;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mypost_database;
    private Query myquery;
    private FirebaseUser current_user;
    private TextView fav_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_posts_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fav_text = findViewById(R.id.my_post_textview);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(my_posts_activity.this, main_activity.class));
        }

        current_user = firebaseAuth.getCurrentUser();
        String User_email = current_user.getEmail();
        mypost_database = FirebaseDatabase.getInstance().getReference().child("Updates");
        mypost_database.keepSynced(true);

        myquery = mypost_database.orderByChild("Author_Email").equalTo(User_email);
        myquery.keepSynced(true);
        myquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    fav_text.setVisibility(View.VISIBLE);
                    fav_text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(my_posts_activity.this, new_post_activity.class));
                        }
                    });
                } else {
                    fav_text.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myUpdates = findViewById(R.id.fav_recycler);
        LinearLayoutManager mylinear = new LinearLayoutManager(this);
        mylinear.setReverseLayout(true);
        mylinear.setStackFromEnd(true);
        myUpdates.setLayoutManager(mylinear);

        onStart();

    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<updates> options =
                new FirebaseRecyclerOptions.Builder<updates>()
                        .setQuery(myquery, updates.class)
                        .build();

        FirebaseRecyclerAdapter<updates, myupdatesviewHolder> myadapter = new FirebaseRecyclerAdapter<updates, myupdatesviewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull myupdatesviewHolder holder, int position, @NonNull final updates model) {
                final String post_key = getRef(position).getKey();

                holder.setTitle(model.getTitle());
                holder.setDescription(model.getDescription());
                holder.setAuthor(model.getAuthor());
                holder.setImage(model.getImage());

                holder.myview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent single = new Intent(my_posts_activity.this, single_post_activity.class);
                        Bundle extras = new Bundle();
                        extras.putString("post_id", post_key);
                        extras.putString("post_title", model.getTitle());
                        extras.putString("post_image", model.getImage());
                        extras.putString("post_desc", model.getDescription());
                        extras.putString("post_author", model.getAuthor());
                        extras.putString("author_mail", model.getAuthor_Email());
                        extras.putString("date_and_time", model.getDate_and_Time());
                        extras.putString("new_date_and_time", model.getNew_Date_and_Time());
                        extras.putString("author_id", model.getAuthor_ID());

                        single.putExtras(extras);
                        startActivity(single);
                    }
                });

            }

            @NonNull
            @Override
            public myupdatesviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view;
                LayoutInflater inflater = LayoutInflater.from(my_posts_activity.this);
                view = inflater.inflate(R.layout.my_posts_row, parent, false);

                return new myupdatesviewHolder(view);
            }
        };
        myUpdates.setAdapter(myadapter);
        myadapter.startListening();
    }

    public static class myupdatesviewHolder extends RecyclerView.ViewHolder {

        View myview;

        public myupdatesviewHolder(@NonNull View itemView) {
            super(itemView);
            myview = itemView;
        }

        public void setTitle(String Title) {
            TextView post_title = myview.findViewById(R.id.display_post_title);
            post_title.setText(Title);
        }

        public void setDescription(String Description) {
            TextView post_desc = myview.findViewById(R.id.display_post_desc);

            if (Description.length() > 100) {
                String desc = Description.substring(0, 100) + " ...";
                post_desc.setText(Html.fromHtml(desc + "<font color ='red' > <u><i> See more</i> </u> </font"));
            } else {
                post_desc.setText(Description);
            }
        }

        public void setAuthor(String Author) {
            TextView post_Author = myview.findViewById(R.id.display_post_author);
            post_Author.setText(Author);
        }

        public void setImage(final String Image) {
            final ImageView post_image = myview.findViewById(R.id.display_post_image);
            Picasso.get().load(Image).resize(300, 300).centerCrop().networkPolicy(NetworkPolicy.OFFLINE).into(post_image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(Image).resize(300, 300).centerCrop().into(post_image);

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(my_posts_activity.this, main_activity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_posts_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        if (item.getItemId() == R.id.new_post) {
            startActivity(new Intent(my_posts_activity.this, new_post_activity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
