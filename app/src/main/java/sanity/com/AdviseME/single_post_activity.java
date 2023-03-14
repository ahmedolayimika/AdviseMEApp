package sanity.com.AdviseME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import AdviseME.R;

public class single_post_activity extends AppCompatActivity {

    private ImageView single_image;
    private TextView single_title, single_desc, single_author, single_date, edited_date, single_recipients;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference postref;
    private String post_id, author, title, author_email, author_ID, image, desc, date_and_time, new_date_and_time, recipients, myStatus;
    private ProgressDialog progress;
    private View ShowComments, delete_post, edit_post, author_details;
    private boolean rotate = false, isFabHide = false;
    private View back_drop;
    FloatingActionButton myfab, show_comments_fab, delete_post_fab, edit_post_fab, author_details_fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_post_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        single_image = findViewById(R.id.single_img);
        single_title = findViewById(R.id.single_title);
        single_desc = findViewById(R.id.single_desc);
        single_author = findViewById(R.id.single_author);
        single_date = findViewById(R.id.single_date);
        edited_date = findViewById(R.id.edited_date);
        single_recipients = findViewById(R.id.single_recipients);

        //// views and FABs
        ShowComments = findViewById(R.id.show_comments);
        delete_post = findViewById(R.id.delete_post);
        edit_post = findViewById(R.id.edit_post);
        author_details = findViewById(R.id.view_author_profile);

        show_comments_fab = findViewById(R.id.show_comments_fab);
        delete_post_fab = findViewById(R.id.delete_post_fab);
        edit_post_fab = findViewById(R.id.edit_post_fab);
        author_details_fab = findViewById(R.id.view_author_profile_fab);

        progress = new ProgressDialog(this);


        Intent my_intent = this.getIntent();
        Bundle bundle = my_intent.getExtras();
        post_id = bundle.getString("post_id");
        author_ID = bundle.getString("author_id");
        image = bundle.getString("post_image");
        title = bundle.getString("post_title");
        desc = bundle.getString("post_desc");
        author = bundle.getString("post_author");
        author_email = bundle.getString("author_mail");
        date_and_time = bundle.getString("date_and_time");
        new_date_and_time = bundle.getString("new_date_and_time");
        recipients = bundle.getString("recipients");

        this.setTitle(title);
        single_author.setText("Uploaded by : " + author);
        single_date.setText(date_and_time);
        single_title.setText(title);
        single_desc.setText(desc);
        single_recipients.setText("This message is important for everyone in: " + recipients);
        Picasso.get().load(image).resize(600, 600).centerCrop().networkPolicy(NetworkPolicy.OFFLINE).into(single_image, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(image).resize(600, 600).centerCrop().into(single_image);

            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        postref = FirebaseDatabase.getInstance().getReference().child("Updates");
        postref.keepSynced(true);
        postref.child(post_id).child("New_Date_and_Time").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    edited_date.setVisibility(View.VISIBLE);
                    edited_date.setText(new_date_and_time);
                } else {
                    edited_date.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myfab = findViewById(R.id.myfab);
        back_drop = findViewById(R.id.back_drop);
        back_drop.setVisibility(View.GONE);
        initShowOut(ShowComments);

        myfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotate = rotateFab(v, !rotate);
                if (rotate) {
                    if (firebaseAuth.getCurrentUser().getEmail().equals(author_email)) {
                        showIn(ShowComments);
                        showIn(edit_post);
                        showIn(delete_post);
                        ////////////////////////////////////
                        initShowOut(delete_post);
                        initShowOut(edit_post);
                        delete_post.setVisibility(View.VISIBLE);
                        edit_post.setVisibility(View.VISIBLE);

                        delete_post.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                toggleFabMode(myfab);
                                delete_post();
                            }
                        });

                        delete_post_fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                toggleFabMode(myfab);
                                delete_post();
                            }
                        });

                        edit_post.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                toggleFabMode(myfab);
                                AlertDialog.Builder editALert = new AlertDialog.Builder(single_post_activity.this);
                                editALert.setTitle("Edit this post")
                                        .setMessage("Are you sure?")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent editintent = new Intent(single_post_activity.this, edit_post_activity.class);
                                                Bundle editbundle = new Bundle();
                                                editbundle.putString("post_image", image);
                                                editbundle.putString("post_title", title);
                                                editbundle.putString("post_desc", desc);
                                                editbundle.putString("post_id", post_id);
                                                editbundle.putString("time", date_and_time);
                                                editintent.putExtras(editbundle);
                                                startActivity(editintent);
                                                //Toast.makeText(single_post_activity.this, "Go to edit post activity", Toast.LENGTH_SHORT).show();

                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }

                                        })
                                        .create().show();

                            }
                        });

                        edit_post_fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                toggleFabMode(myfab);
                                AlertDialog.Builder editALert = new AlertDialog.Builder(single_post_activity.this);
                                editALert.setTitle("Edit this post")
                                        .setMessage("Are you sure?")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent editintent = new Intent(single_post_activity.this, edit_post_activity.class);
                                                Bundle editbundle = new Bundle();
                                                editbundle.putString("post_image", image);
                                                editbundle.putString("post_title", title);
                                                editbundle.putString("post_desc", desc);
                                                editbundle.putString("post_id", post_id);
                                                editbundle.putString("time", date_and_time);
                                                editintent.putExtras(editbundle);
                                                startActivity(editintent);
                                                //Toast.makeText(single_post_activity.this, "Go to edit post activity", Toast.LENGTH_SHORT).show();

                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }

                                        })
                                        .create().show();

                            }
                        });

                        //////////////////////////////
                    }

                    else {
                        showIn(ShowComments);
                        showIn(author_details);
                        /////////////////////////////////////

                        initShowOut(author_details);
                        author_details.setVisibility(View.VISIBLE);
                        author_details.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                toggleFabMode(myfab);
                                Intent author_intent = new Intent(single_post_activity.this, author_profile_activity.class);
                                /// goes to author profile
                                Bundle mybundle = new Bundle();
                                mybundle.putString("author_name", author);
                                mybundle.putString("author_id", author_ID);
                                author_intent.putExtras(mybundle);
                                startActivity(author_intent);

                            }
                        });

                        author_details_fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                toggleFabMode(myfab);
                                Intent author_intent = new Intent(single_post_activity.this, author_profile_activity.class);
                                /// goes to author profile
                                Bundle mybundle = new Bundle();
                                mybundle.putString("author_name", author);
                                mybundle.putString("author_id", author_ID);
                                author_intent.putExtras(mybundle);
                                startActivity(author_intent);

                            }
                        });
                        ////////////////////////////////////////
                    }
                    back_drop.setVisibility(View.VISIBLE);

                } else {
                    if (firebaseAuth.getCurrentUser().getEmail().equals(author_email)) {
                        showOut(ShowComments);
                        showOut(edit_post);
                        showOut(delete_post);
                    } else {
                        showOut(ShowComments);
                        showOut(author_details);
                    }
                    back_drop.setVisibility(View.GONE);
                }
            }
        });

        back_drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFabMode(myfab);
            }
        });


        single_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imageIntent = new Intent(single_post_activity.this, image_display_activity.class);
                Bundle imagebundle = new Bundle();
                imagebundle.putString("the_image", image);
                imagebundle.putString("the_image_name", title);
                imageIntent.putExtras(imagebundle);
                startActivity(imageIntent);
            }
        });

        ShowComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFabMode(myfab);
                Intent commentIntent = new Intent(single_post_activity.this, comments_activity.class);
                Bundle commentBundle = new Bundle();
                commentBundle.putString("the_post_id", post_id);
                commentBundle.putString("post_title", title);
                commentBundle.putString("the_author_id", author_ID);
                commentBundle.putString("the_post_author", author);
                commentIntent.putExtras(commentBundle);
                startActivity(commentIntent);
                //Toast.makeText(single_post_activity.this, "Go to comments section", Toast.LENGTH_SHORT).show();
            }
        });

        show_comments_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFabMode(myfab);
                Intent commentIntent = new Intent(single_post_activity.this, comments_activity.class);
                Bundle commentBundle = new Bundle();
                commentBundle.putString("the_post_id", post_id);
                commentBundle.putString("post_title", title);
                commentBundle.putString("the_author_id", author_ID);
                commentBundle.putString("the_post_author", author);
                commentIntent.putExtras(commentBundle);
                startActivity(commentIntent);
                //Toast.makeText(single_post_activity.this, "Go to comments section", Toast.LENGTH_SHORT).show();
            }
        });


        NestedScrollView nested_content = findViewById(R.id.single_nested);
        nested_content.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY < oldScrollY) { // up
                    animateFab(false);
                }
                if (scrollY > oldScrollY) { // down
                    animateFab(true);
                }
            }
        });


    }

    private void delete_post() {
        AlertDialog.Builder delete_alert = new AlertDialog.Builder(this);
        delete_alert.setMessage("Delete this post?");
        delete_alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                progress.setMessage("Deleting post . . .");
                progress.setCancelable(false);
                progress.show();
                postref.child(post_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(single_post_activity.this, "Successfully deleted", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        } else {
                            Toast.makeText(single_post_activity.this, "Failed to delete " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        progress.dismiss();
                    }
                });
            }
        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        delete_alert.create();
        delete_alert.show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(single_post_activity.this, main_activity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.comment_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.comment) {
            Intent commentIntent = new Intent(singlePost.this, comments_activity.class);
            Bundle commentBundle = new Bundle();
            commentBundle.putString("the_post_id", post_id);
            commentBundle.putString("post_title", title);
            commentBundle.putString("the_author_id", author_ID);
            commentBundle.putString("the_post_author",author);
            commentIntent.putExtras(commentBundle);
            startActivity(commentIntent);
        }
        return false;
    }*/

    public static void initShowOut(final View v) {
        v.setVisibility(View.GONE);
        v.setTranslationY(v.getHeight());
        v.setAlpha(0f);
    }

    public static void showIn(final View v) {
        v.setVisibility(View.VISIBLE);
        v.setAlpha(0f);
        v.setTranslationY(v.getHeight());
        v.animate()
                .setDuration(200)
                .translationY(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                })
                .alpha(1f)
                .start();
    }

    public static void showOut(final View v) {
        v.setVisibility(View.VISIBLE);
        v.setAlpha(1f);
        v.setTranslationY(0);
        v.animate()
                .setDuration(200)
                .translationY(v.getHeight())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        v.setVisibility(View.GONE);
                        super.onAnimationEnd(animation);
                    }
                }).alpha(0f)
                .start();
    }

    public static boolean rotateFab(final View v, boolean rotate) {
        v.animate().setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                })
                .rotation(rotate ? 135f : 0f);
        return rotate;
    }

    private void toggleFabMode(View v) {
        rotate = rotateFab(v, !rotate);
        if (rotate) {
            if (firebaseAuth.getCurrentUser().getEmail().equals(author_email)) {
                showIn(ShowComments);
                showIn(edit_post);
                showIn(delete_post);
            } else {
                showIn(ShowComments);
                showIn(author_details);
            }
            back_drop.setVisibility(View.VISIBLE);

        } else {
            if (firebaseAuth.getCurrentUser().getEmail().equals(author_email)) {
                showOut(ShowComments);
                showOut(edit_post);
                showOut(delete_post);
            } else {
                showOut(ShowComments);
                showOut(author_details);
            }
            back_drop.setVisibility(View.GONE);
        }
    }

    private void animateFab(final boolean hide) {

        if (isFabHide && hide || !isFabHide && !hide) return;
        isFabHide = hide;
        int moveY = hide ? (2 * myfab.getHeight()) : 0;
        myfab.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();

    }

}
