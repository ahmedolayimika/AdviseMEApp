package sanity.com.AdviseME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
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


public class main_activity extends AppCompatActivity {
    private ActionBar actionBar;
    private androidx.appcompat.widget.Toolbar toolbar;
    private DrawerLayout drawer;
    private View drawer_header;
    private ImageView profile_image;
    public TextView username, user_email, postText;
    private FirebaseAuth firebaseAuth;
    String myUserId, firstName, LastName, image, myLevel;
    private DatabaseReference reff, post_database, views_db, status_db;
    private FirebaseUser current_user;
    private RecyclerView update_list;
    // private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private Query myquery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        current_user = firebaseAuth.getCurrentUser();
        myUserId = current_user.getUid();

        postText = findViewById(R.id.post_text);


        // Navigation drawer
        NavigationView nav_view = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer_layout);
        drawer_header = nav_view.getHeaderView(0);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(main_activity.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        username = drawer_header.findViewById(R.id.username);
        user_email = drawer_header.findViewById(R.id.email);
        profile_image = drawer_header.findViewById(R.id.my_profile_image);


        if (current_user == null) {
            finishAffinity();
            startActivity(new Intent(main_activity.this, login_activity.class));
        }

        //status whether current user is a staff or student
        status_db = FirebaseDatabase.getInstance().getReference().child("Users").child(myUserId);
        status_db.keepSynced(true);
        status_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(main_activity.this, "Please update your profile", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(main_activity.this, edit_profile_activity.class));

                } else {

                    String status = snapshot.child("status").getValue().toString().trim();
                    myLevel = snapshot.child("level").getValue().toString();


                    if (status.equals("Student")) {

                        // get student details
                        reff = FirebaseDatabase.getInstance().getReference().child("Users").child(myUserId);
                        reff.keepSynced(true);
                        reff.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    Toast.makeText(main_activity.this, "Please update your profile", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(main_activity.this, edit_profile_activity.class));
                                } else {
                                    firstName = dataSnapshot.child("first_name").getValue().toString();
                                    LastName = dataSnapshot.child("last_name").getValue().toString();

                                    username.setText(firstName + " " + LastName);
                                    //user_email.setText(current_user.getEmail());
                                    user_email.setText(status);


                                    image = dataSnapshot.child("profile_Image").getValue().toString();

                                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).into(profile_image, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Picasso.get().load(image).into(profile_image);

                                        }


                                    });

                                    profile_image.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent imageIntent = new Intent(main_activity.this, image_display_activity.class);
                                            Bundle imagebundle = new Bundle();
                                            imagebundle.putString("the_image", image);
                                            imagebundle.putString("the_image_name", firstName + " " + LastName);
                                            imageIntent.putExtras(imagebundle);
                                            startActivity(imageIntent);
                                            //startActivity(new Intent(main_activity.this, image_display_activity.class));
                                            drawer.closeDrawers();
                                            // add extras
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }

                        });

                        //hide my posts for students
                        Menu menu = nav_view.getMenu();
                        MenuItem item1 = menu.findItem(R.id.nav_my_posts);
                        item1.setVisible(false);
                    } else if (status.equals("Adviser")) {
                        // get Adviser details
                        reff = FirebaseDatabase.getInstance().getReference().child("Users").child(myUserId);
                        reff.keepSynced(true);
                        reff.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    Toast.makeText(main_activity.this, "Please update your profile", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(main_activity.this, edit_profile_activity.class));
                                } else {
                                    firstName = dataSnapshot.child("first_name").getValue().toString();
                                    LastName = dataSnapshot.child("last_name").getValue().toString();

                                    username.setText(firstName + " " + LastName);
                                    //user_email.setText(current_user.getEmail());
                                    user_email.setText(status);


                                    image = dataSnapshot.child("profile_Image").getValue().toString();

                                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).into(profile_image, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Picasso.get().load(image).into(profile_image);

                                        }


                                    });

                                    profile_image.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent imageIntent = new Intent(main_activity.this, image_display_activity.class);
                                            Bundle imagebundle = new Bundle();
                                            imagebundle.putString("the_image", image);
                                            imagebundle.putString("the_image_name", firstName + " " + LastName);
                                            imageIntent.putExtras(imagebundle);
                                            startActivity(imageIntent);
                                            //startActivity(new Intent(main_activity.this, image_display_activity.class));
                                            drawer.closeDrawers();
                                            // add extras
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }

                        });
                    }


                    // post database
                    post_database = FirebaseDatabase.getInstance().getReference().child("Updates");
                    myquery = post_database.orderByChild("Recipients").equalTo(myLevel);
                    myquery.keepSynced(true);
                    myquery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists() && status.equals("Adviser")) {
                                postText.setVisibility(View.VISIBLE);
                                postText.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(new Intent(main_activity.this, new_post_activity.class));
                                    }
                                });
                            } else if (!dataSnapshot.exists() && status.equals("Student")) {
                                postText.setVisibility(View.VISIBLE);
                                postText.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Toast.makeText(main_activity.this, "Kindly wait for messages from your adivser", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                postText.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    /////////////////////////////////

                    FirebaseRecyclerOptions<updates> options =
                            new FirebaseRecyclerOptions.Builder<updates>()
                                    .setQuery(myquery, updates.class)
                                    .build();
                    FirebaseRecyclerAdapter<updates, updateViewHolder> PostsAdapter = new FirebaseRecyclerAdapter<updates, updateViewHolder>(options) {

                        @NonNull
                        @Override
                        public updateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view;
                            LayoutInflater inflater = LayoutInflater.from(main_activity.this);
                            view = inflater.inflate(R.layout.posts, parent, false);

                            return new updateViewHolder(view);

                        }

                        @Override
                        protected void onBindViewHolder(@NonNull final updateViewHolder holder, int position, @NonNull final updates model) {
                            final String post_key = getRef(position).getKey();

                            holder.setTitle(model.getTitle());
                            holder.setDescription(model.getDescription());
                            holder.setAuthor(model.getAuthor());
                            holder.setImage(model.getImage());
                            final ImageView badge = holder.mView.findViewById(R.id.tag_new);

                            views_db = FirebaseDatabase.getInstance().getReference().child("Views");
                            views_db.keepSynced(true);
                            final DatabaseReference postView = views_db.child("Views for " + post_key);
                            postView.child(current_user.getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        badge.setVisibility(View.GONE);
                                    } else {
                                        badge.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            holder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    postView.child(current_user.getUid()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (!dataSnapshot.exists()) {
                                                postView.child(current_user.getUid()).setValue(current_user.getEmail());
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                    Intent singlePost = new Intent(main_activity.this, single_post_activity.class);
                                    Bundle extras = new Bundle();
                                    extras.putString("post_image", model.getImage());
                                    extras.putString("post_title", model.getTitle());
                                    extras.putString("post_desc", model.getDescription());
                                    extras.putString("post_author", model.getAuthor());
                                    extras.putString("post_id", post_key);
                                    extras.putString("author_mail", model.getAuthor_Email());
                                    extras.putString("date_and_time", model.getDate_and_Time());
                                    extras.putString("new_date_and_time", model.getNew_Date_and_Time());
                                    extras.putString("author_id", model.getAuthor_ID());
                                    extras.putString("recipients", model.getRecipients());

                                    singlePost.putExtras(extras);
                                    startActivity(singlePost);
                                }
                            });
                        }
                    };

                    update_list.setAdapter(PostsAdapter);
                    PostsAdapter.startListening();
                    PostsAdapter.notifyDataSetChanged();

                    ///////////////////////////////////
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Navigation Listener
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nav_home:
                        //startActivity(new Intent(getApplicationContext(), main_activity.class));
                        break;

                    // check if status == adviser before displaying this

                    case R.id.nav_my_posts:
                        startActivity(new Intent(main_activity.this, my_posts_activity.class));
                        finish();

                        break;


                    case R.id.nav_messages:
                        startActivity(new Intent(main_activity.this, messages_activity.class));
                        finish();
                        break;


                    case R.id.nav_settings:
                        startActivity(new Intent(main_activity.this, settings_activity.class));
                        finish();
                        break;

                    case R.id.nav_profile:
                        startActivity(new Intent(main_activity.this, profile_activity.class));
                        finish();
                        break;

                    case R.id.about_app:
                        LayoutInflater inflater = getLayoutInflater();
                        View about_app = inflater.inflate(R.layout.about_app_popup, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(main_activity.this);
                        builder.setView(about_app)
                                .setCancelable(false);
                        final AlertDialog about_app_dialog = builder.create();
                        about_app_dialog.show();
                        ImageView close = about_app.findViewById(R.id.close);
                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                about_app_dialog.dismiss();
                            }
                        });
                        break;

                    /*case R.id.about_developer:
                        LayoutInflater dev_inflater = getLayoutInflater();
                        View about_dev = dev_inflater.inflate(R.layout.about_dev_popup, null);
                        AlertDialog.Builder dev_builder = new AlertDialog.Builder(main_activity.this);
                        dev_builder.setView(about_dev)
                                .setCancelable(false);
                        final AlertDialog about_dev_dialog = dev_builder.create();
                        about_dev_dialog.show();
                        ImageView close_dev_dialog = about_dev.findViewById(R.id.close);
                        close_dev_dialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                about_dev_dialog.dismiss();
                            }
                        });

                        break;*/

                    case R.id.logout:
                        final AlertDialog.Builder logout_builder = new AlertDialog.Builder(main_activity.this);
                        logout_builder.setCancelable(false)
                                .setMessage("Do you want to log out now?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        firebaseAuth.signOut();
                                        finishAffinity();
                                        startActivity(new Intent(main_activity.this, login_activity.class));
                                        finish();
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).create().show();
                        break;

                    case R.id.exit_app:
                        finishAffinity();

                }
                drawer.closeDrawers();
                return true;
            }
        });

        update_list = findViewById(R.id.home_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        update_list.setLayoutManager(layoutManager);

        //onStart();
    }

   /* @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<updates> options =
                new FirebaseRecyclerOptions.Builder<updates>()
                        .setQuery(myPostsQuery, updates.class)
                        .build();
        FirebaseRecyclerAdapter<updates, updateViewHolder> PostsAdapter = new FirebaseRecyclerAdapter<updates, updateViewHolder>(options) {

            @NonNull
            @Override
            public updateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view;
                LayoutInflater inflater = LayoutInflater.from(main_activity.this);
                view = inflater.inflate(R.layout.posts, parent, false);

                return new updateViewHolder(view);

            }

            @Override
            protected void onBindViewHolder(@NonNull final updateViewHolder holder, int position, @NonNull final updates model) {
                final String post_key = getRef(position).getKey();

                holder.setTitle(model.getTitle());
                holder.setDescription(model.getDescription());
                holder.setAuthor(model.getAuthor());
                holder.setImage(model.getImage());
                final ImageView badge = holder.mView.findViewById(R.id.tag_new);

                views_db = FirebaseDatabase.getInstance().getReference().child("Views");
                views_db.keepSynced(true);
                final DatabaseReference postView = views_db.child("Views for " + post_key);
                postView.child(current_user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            badge.setVisibility(View.GONE);
                        } else {
                            badge.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        postView.child(current_user.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    postView.child(current_user.getUid()).setValue(current_user.getEmail());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        Intent singlePost = new Intent(main_activity.this, single_post_activity.class);
                        Bundle extras = new Bundle();
                        extras.putString("post_image", model.getImage());
                        extras.putString("post_title", model.getTitle());
                        extras.putString("post_desc", model.getDescription());
                        extras.putString("post_author", model.getAuthor());
                        extras.putString("post_id", post_key);
                        extras.putString("author_mail", model.getAuthor_Email());
                        extras.putString("date_and_time", model.getDate_and_Time());
                        extras.putString("new_date_and_time", model.getNew_Date_and_Time());
                        extras.putString("author_id", model.getAuthor_ID());
                        extras.putString("recipients", model.getRecipients());

                        singlePost.putExtras(extras);
                        startActivity(singlePost);
                    }
                });
            }
        };

        update_list.setAdapter(PostsAdapter);
        PostsAdapter.startListening();
        PostsAdapter.notifyDataSetChanged();
    }*/

    public static class updateViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public updateViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String Title) {
            TextView post_title = mView.findViewById(R.id.display_post_title);
            post_title.setText(Title);
        }

        public void setDescription(String Description) {
            TextView post_desc = mView.findViewById(R.id.display_post_desc);
            // post_desc.setText(Description);
            if (Description.length() > 100) {
                String desc = Description.substring(0, 100) + " ...";
                post_desc.setText(Html.fromHtml(desc + "<font color ='red' > <u><i> See more</i> </u> </font"));
            } else {
                post_desc.setText(Description);
            }
        }

        public void setAuthor(String Author) {
            TextView post_author = mView.findViewById(R.id.display_post_author);
            post_author.setText(Author);
        }

        public void setImage(final String Image) {
            final ImageView post_image = mView.findViewById(R.id.display_post_image);
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
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer((GravityCompat.START));
        } else {
            doExitApp();
        }

    }

    private long exitTime = 0;

    private void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "Press again to exit app", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            //finishAffinity();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //status whether current user is an adviser or student
        status_db = FirebaseDatabase.getInstance().getReference().child("Users").child(myUserId);
        status_db.keepSynced(true);
        status_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String status = snapshot.child("status").getValue().toString().trim();

                    if (status.equals("Student")) {

                    } else if (status.equals("Adviser")) {

                        getMenuInflater().inflate(R.menu.home_menu, menu);

                    }
                } else {
                    Toast.makeText(main_activity.this, "Please update your profile", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(main_activity.this, edit_profile_activity.class));

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //////////////////
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.new_post) {
            startActivity(new Intent(main_activity.this, new_post_activity.class));
            finish();
        }

        if (item.getItemId() == R.id.refresh) {
            Toast.makeText(main_activity.this, "System updated", Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
    }


}
