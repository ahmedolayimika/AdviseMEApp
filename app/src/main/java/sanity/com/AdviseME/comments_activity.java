package sanity.com.AdviseME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import AdviseME.R;

public class comments_activity extends AppCompatActivity {

    private View new_comment;
    private ImageView myimageview;
    private ImageButton close;
    private Button send_comment;
    private TextView my_name,editing,commentTextview, comm_text;
    private EditText my_comment;
    private DatabaseReference userdb, commentdb,postcomments;
    private FirebaseUser current_user;
    public String my_image,myfirst_name,mylast_name,my_fullname, post_author_id, my_user_id, postAuthorFullname ;
    public String postId, currentTime, timeStamp, currentDate, postTitle;
    RecyclerView comments_list;
    private BottomSheetDialog bottomSheetDialog;
    private BottomSheetBehavior mBehavior;
    private View bottom_sheet;
    private ProgressDialog myprog;
    private NestedScrollView comment_scroll;
    private Boolean isFabHide = false;
    private FloatingActionButton addCommentBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myprog = new ProgressDialog(comments_activity.this);
        comm_text = findViewById(R.id.comm_text);
       // addCommentBtn = findViewById(R.id.new_comment);
        comment_scroll = findViewById(R.id.comment_scroll);

        bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);
        this.setTitle("Comments Section");

        Intent myint = this.getIntent();
        Bundle mybundle = myint.getExtras();
        post_author_id = mybundle.getString("the_author_id");
        postId = mybundle.getString("the_post_id");
        postTitle = mybundle.getString("post_title");
        postAuthorFullname = mybundle.getString("the_post_author");

        current_user = FirebaseAuth.getInstance().getCurrentUser();
        my_user_id = current_user.getUid();

        commentdb = FirebaseDatabase.getInstance().getReference().child("Updates").child(postId).child( "Comments");
        commentdb.keepSynced(true);
        commentdb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    comm_text.setVisibility(View.VISIBLE);
                    comm_text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addNewComment();
                        }
                    });
                }
                else{
                    comm_text.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        userdb = FirebaseDatabase.getInstance().getReference().child("Users").child(my_user_id);
        userdb.keepSynced(true);
        userdb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(comments_activity.this,"You need to set up your profile", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(comments_activity.this,edit_profile_activity.class));
                    finish();
                }
                else {
                    my_image = dataSnapshot.child("profile_Image").getValue().toString();
                    myfirst_name = dataSnapshot.child("first_name").getValue().toString();
                    mylast_name = dataSnapshot.child("last_name").getValue().toString();
                    my_fullname = myfirst_name + " " + mylast_name;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*comment_scroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY < oldScrollY) { // up
                    animateFab(false);
                }
                if (scrollY > oldScrollY) { // down
                    animateFab(true);
                }
            }
        });*/


        comments_list = findViewById(R.id.comment_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
       /* layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);*/
        comments_list.setLayoutManager(layoutManager);

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<post_comments> options =
                new FirebaseRecyclerOptions.Builder<post_comments>()
                        .setQuery(commentdb, post_comments.class)
                        .build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<post_comments, commentsViewholder>(options) {

            @NonNull
            @Override
            public commentsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view;
                LayoutInflater inflater = LayoutInflater.from(comments_activity.this);
                view = inflater.inflate(R.layout.comments,parent,false);

                return new commentsViewholder(view);

            }
            @Override
            protected void onBindViewHolder(@NonNull commentsViewholder myholder, int position, @NonNull final post_comments mymodel) {
                final String comment_id = getRef(position).getKey();
                myholder.setUser(mymodel.getUser());
                myholder.setComment(mymodel.getComment());
                myholder.setUser_Image(mymodel.getUser_Image());
                final String  commentor_id = mymodel.getUser_ID();


                myholder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // final View view = getLayoutInflater().inflate(R.layout.sheet_list, null);
                        view = getLayoutInflater().inflate(R.layout.sheet_list, null);
                        bottomSheetDialog = new BottomSheetDialog(comments_activity.this);
                        bottomSheetDialog.setContentView(view);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            bottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        }

                        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        View copy_comment = view.findViewById(R.id.copy_comment);
                        copy_comment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //  copyToClipboard(comments_activity.this, commentTextview.getText().toString());
                                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("clipboard", mymodel.getComment());
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(comments_activity.this, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
                                bottomSheetDialog.dismiss();
                            }
                        });
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        View comment_details = view.findViewById(R.id.comment_details);
                        comment_details.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                LayoutInflater inflater = LayoutInflater.from(comments_activity.this);
                                View details = inflater.inflate(R.layout.comment_details_layout,null);
                                TextView commentPersonName = details.findViewById(R.id.comment_person_name);
                                TextView commentOriginalTime = details.findViewById(R.id.comment_original_time);
                                final TextView commentEditedTime = details.findViewById(R.id.comment_edited_time);
                                commentPersonName.setText("Sent by : \n" + mymodel.getUser());
                                commentOriginalTime.setText("Time sent : \n" + mymodel.getComment_time());
                                commentdb.child(comment_id).child("Edited_time").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (!dataSnapshot.exists()) {
                                            commentEditedTime.setVisibility(View.GONE);
                                        }
                                        else {
                                            commentEditedTime.setVisibility(View.VISIBLE);
                                            commentEditedTime.setText(mymodel.getEdited_time());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(comments_activity.this);
                                alertDialog.setTitle("Details")
                                        .setView(details)
                                        .create()
                                        .show();
                                bottomSheetDialog.dismiss();
                            }
                        });
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        View user_details =view.findViewById(R.id.user_details);
                        if (!mymodel.getUser_ID().equals(current_user.getUid())) {
                            user_details.setVisibility(View.VISIBLE);
                            user_details.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent userIntent = new Intent(comments_activity.this,author_profile_activity.class);
                                    Bundle userbun = new Bundle();
                                    userbun.putString("author_name",mymodel.getUser());
                                    userbun.putString("author_id",commentor_id);
                                    userIntent.putExtras(userbun);
                                    startActivity(userIntent);
                                    bottomSheetDialog.dismiss();
                                }
                            });
                        }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        View edit_comment =  view.findViewById(R.id.edit_comment);
                        if (current_user.getUid().equals(mymodel.getUser_ID())) {
                            edit_comment.setVisibility(View.VISIBLE);
                            edit_comment.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    LayoutInflater layoutInflater = LayoutInflater.from(comments_activity.this);
                                    final View editCommentView = layoutInflater.inflate(R.layout.edit_comment_layout,null);
                                    AlertDialog.Builder ADB = new AlertDialog.Builder(comments_activity.this);

                                    commentdb.child(comment_id).child("Comment").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(!dataSnapshot.exists()){
                                                Log.d("Feedback:", "Comment has been deleted" );
                                                //Toast.makeText(comments_activity.this,"Comment deleted!",Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                String existing_comment = dataSnapshot.getValue().toString();
                                                editing = editCommentView.findViewById(R.id.new_comment_text);
                                                editing.setText(existing_comment);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    ADB.setView(editCommentView).setCancelable(false).setTitle("Edit Comment")
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            })
                                            .setPositiveButton("Save Changes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    String new_comment = editing.getText().toString().trim();
                                                    if(new_comment.isEmpty()){
                                                        Toast.makeText(comments_activity.this,"Please write something",Toast.LENGTH_SHORT).show();
                                                    }
                                                    else{
                                                        Calendar mycal2 = Calendar.getInstance();
                                                        SimpleDateFormat newDate = new SimpleDateFormat("EEEE, MMMM d, yyyy,");
                                                        String  newcurrentDate = newDate.format(mycal2.getTime());

                                                        SimpleDateFormat newtime = new SimpleDateFormat("hh:mm:ss a");
                                                        String  newcurrentTime = newtime.format(mycal2.getTime());

                                                        final String new_time = newcurrentDate + (" ") + newcurrentTime;

                                                        myprog.setMessage("Please wait . . .");
                                                        myprog.setCancelable(false);
                                                        myprog.show();
                                                        commentdb.child(comment_id).child("Comment").setValue(new_comment);
                                                        commentdb.child(comment_id).child("Edited_time").setValue("Last edited on : \n" + new_time)
                                                                .addOnCompleteListener(comments_activity.this, new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Toast.makeText(comments_activity.this,"Comment has been edited",Toast.LENGTH_SHORT).show();
                                                                        }
                                                                        else {
                                                                            Toast.makeText(comments_activity.this,"Failed to edit " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                                                        }
                                                                        myprog.dismiss();
                                                                    }
                                                                });

                                                    }

                                                }
                                            }).create().show();
                                    bottomSheetDialog.dismiss();
                                }
                            });
                        }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                        View delete_comment = view.findViewById(R.id.delete_comment);
                        if (current_user.getUid().equals(mymodel.getUser_ID())) {
                            delete_comment.setVisibility(View.VISIBLE);
                            delete_comment.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(comments_activity.this);
                                    builder.setMessage("Delete this comment?")
                                            .setCancelable(false)
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(final DialogInterface dialogInterface, int i) {
                                                    /*myprog.setMessage("Deleting . . . ");
                                                    myprog.setCancelable(false);
                                                    myprog.show();*/

                                                    commentdb.child(comment_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (!task.isSuccessful()) {
                                                                Toast.makeText(comments_activity.this,"Failed to delete " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                            // myprog.dismiss();
                                                        }
                                                    });
                                                }
                                            })
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.cancel();
                                                }
                                            }).create().show();
                                    bottomSheetDialog.dismiss();
                                }
                            });
                        }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        bottomSheetDialog.show();

                        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                bottomSheetDialog = null;
                            }
                        });
                    }
                });
            }
        };
        comments_list.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public class commentsViewholder extends RecyclerView.ViewHolder{
        TextView personName;
        View mView;
        public commentsViewholder(@NonNull View itemView) {
            super(itemView);
            mView= itemView;
        }

        public void setComment( String Comment){
            commentTextview = mView.findViewById(R.id.main_comment);
            commentTextview.setText(Comment);
        }

        public void setUser(String user){
            personName = mView.findViewById(R.id.person_name);
            personName.setText(user);
        }

        public void setUser_Image(final String user_Image){
            final ImageView personImage = mView.findViewById(R.id.person_image);
            Picasso.get().load(user_Image).networkPolicy(NetworkPolicy.OFFLINE).into(personImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(user_Image).into(personImage);
                }
            });

            personImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent imageIntent = new Intent(comments_activity.this,image_display_activity.class);
                    Bundle imagebundle = new Bundle();
                    imagebundle.putString("the_image",user_Image);
                    imagebundle.putString("the_image_name",personName.getText().toString());
                    imageIntent.putExtras(imagebundle);
                    startActivity(imageIntent);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_comment_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_comment) {
            addNewComment();
        }
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();

        }
        return super.onOptionsItemSelected(item);
    }

    private void addNewComment() {

        LayoutInflater layoutInflater = LayoutInflater.from(comments_activity.this);
        new_comment = layoutInflater.inflate(R.layout.add_comment_layout, null);
        myimageview = new_comment.findViewById(R.id.commentator_image);
        my_name = new_comment.findViewById(R.id.commentator_name);
        my_comment = new_comment.findViewById(R.id.my_comment);
        send_comment = new_comment.findViewById(R.id.send_comment);
        close = new_comment.findViewById(R.id.close);

        /////////////////////////////////////////////////////
        Picasso.get().load(my_image).networkPolicy(NetworkPolicy.OFFLINE).into(myimageview, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(my_image).into(myimageview);
            }
        });
        my_name.setText(my_fullname );

        final AlertDialog.Builder new_comment_dialog = new AlertDialog.Builder(comments_activity.this);
        new_comment_dialog.setCancelable(false)
                .setView(new_comment);
        final AlertDialog mydial = new_comment_dialog.create();
        mydial.show();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mydial.dismiss();
            }
        });

        my_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                send_comment.setEnabled(!charSequence.toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        send_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = my_comment.getText().toString().trim();
                if (!comment.isEmpty()) {

                    myprog.setMessage("Adding your comment . . . ");
                    myprog.setCancelable(false);
                    myprog.show();
                    Calendar mycal = Calendar.getInstance();
                    SimpleDateFormat Date = new SimpleDateFormat("EEEE, MMMM d, yyyy,");
                    currentDate = Date.format(mycal.getTime());

                    SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss a");
                    currentTime = time.format(mycal.getTime());

                    timeStamp = currentDate + (" ") + currentTime;
                    postcomments = commentdb.push();
                    postcomments.keepSynced(true);
                    postcomments.child("Comment").setValue(comment);
                    postcomments.child("Comment_time").setValue(timeStamp);
                    postcomments.child("User_Image").setValue(my_image);
                    postcomments.child("User_ID").setValue(current_user.getUid());
                    postcomments.child("User").setValue(my_fullname)
                            .addOnCompleteListener(comments_activity.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        PrepareNotification("Hello ",
                                                my_fullname,
                                                "Notifications",
                                                "Comments");
                                        // Toast.makeText(comments_activity.this, "Comment added", Toast.LENGTH_SHORT).show();
                                        mydial.dismiss();
                                    } else {
                                        Toast.makeText(comments_activity.this, "Failed to add comment " + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                    }
                                    myprog.cancel();
                                }
                            });
                }
            }
        });

    }

    /*private void animateFab (final boolean hide) {

        if (isFabHide && hide || !isFabHide && !hide) return;
        isFabHide = hide;
        int moveY = hide ? (2 * addCommentBtn.getHeight()) : 0;
        addCommentBtn.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();

    }*/

    private void PrepareNotification(String Title, String Description, String Topic, String NotificationType) {
        String NOTIFICATION_TOPIC = "/topics/" + Topic;
        String NOTIFICATION_TITLE = Title;
        String NOTIFICATION_MESSAGE = Description;
        String NOTIFICATION_TYPE = NotificationType;

        JSONObject notificationObject = new JSONObject();
        JSONObject notificationBody = new JSONObject();

        try {
            //notificationBody.put("commentor_name",my_fullname);
            notificationBody.put("commentor_id",my_user_id);
            notificationBody.put("post_author_name",postAuthorFullname);
            notificationBody.put("post_author_id",post_author_id);
            notificationBody.put("post_title",postTitle);
            notificationBody.put("post_id",postId);

            notificationBody.put("title",NOTIFICATION_TITLE);
            notificationBody.put("description",NOTIFICATION_MESSAGE);
            notificationBody.put("notification_type",NOTIFICATION_TYPE);

            notificationObject.put("to",NOTIFICATION_TOPIC);
            notificationObject.put("data",notificationBody);
        } catch (JSONException e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        sendNotification(notificationObject);
    }

    private void sendNotification(JSONObject notificationObject) {
        JsonObjectRequest request = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("FCM_RESPONSE", "OnResponse: " + response.toString());
                        //Toast.makeText(getApplicationContext(),"Response: " + response.toString(),Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Failed to send:", error.toString());
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map <String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
                headers.put("Authorization", "key=AAAAEcvrkCQ:APA91bHIbNSfbqA1Y4wrbUFdxUMtzzPfKuUmZaiXVxgmX3OT1vt_WZggvkubJb9_ItxtsNuzIRck281JC2pb7AXSO8hvJsJ3KSNWUbCyzBMBr4z1_etgSfQ2hJIn-FdEaYcIJT9UVvIr");
                return headers;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
        // Volley.newRequestQueue(this).add(request);
    }

    @Override
    public void onBackPressed() {
        finish();
    }


}
