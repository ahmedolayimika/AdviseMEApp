package sanity.com.AdviseME;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.HashMap;
import java.util.List;
import AdviseME.R;


public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    Context context;
    List<ModelChat> chatlist;
    String ImageUrl;

    FirebaseUser user;

    public AdapterChat(Context context, List<ModelChat> chatlist, String imageUrl) {
        this.context = context;
        this.chatlist = chatlist;
        ImageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);
            return new MyHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false);
            return new MyHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {
        String message = chatlist.get(position).getMessage();
        String timeStamp = chatlist.get(position).getTimeStamp();

        holder.message.setText(message);
        holder.time.setText(timeStamp);
        Picasso.get().load(ImageUrl).networkPolicy(NetworkPolicy.OFFLINE).into(holder.receiver_image, new Callback() {
            @Override
            public void onSuccess() {
                Picasso.get().load(ImageUrl).into(holder.receiver_image);
            }

            @Override
            public void onError(Exception e) {

            }
        });

        holder.MessageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
               builder.setMessage("Delete this message?")
                       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               deleteMessage(position);
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

        if (position == chatlist.size() - 1) {
            if (chatlist.get(position).isSeen) {
                holder.isSeen.setText("Seen");
            } else {
                holder.isSeen.setText("Delivered");
            }
        } else {
            holder.isSeen.setVisibility(View.GONE);
        }
    }

    private void deleteMessage(int position) {



        String MsgTimeStamp = chatlist.get(position).getTimeStamp();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chats");
        Query query = reference.orderByChild("timeStamp").equalTo(MsgTimeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                for (DataSnapshot ds : snapshot.getChildren()) {

                    /// to delete only my messages
                    if (ds.child("sender").getValue().equals(myUID)) {

                        ///// change message to "this message was deleted"

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("message", "this message was deleted ...");
                        ds.getRef().updateChildren(hashMap);

                        //remove message entirely

                        // ds.getRef().removeValue();


                    }
                    else {
                        Toast.makeText(context, "You can delete only your own messages!", Toast.LENGTH_SHORT).show();
                    }

                    // to delete any message

                    //remove message entirely
                    //ds.getRef().removeValue();

                    ///// change message to "this message was deleted"
                    /*HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("message", "this message was deleted ...");
                    ds.getRef().updateChildren(hashMap);*/

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return chatlist.size();
    }

    @Override
    public int getItemViewType(int position) {

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (chatlist.get(position).getSender().equals(user.getUid())) {
            return MSG_TYPE_RIGHT;

        } else {
            return MSG_TYPE_LEFT;
        }
    }

    class MyHolder extends RecyclerView.ViewHolder {

        ImageView receiver_image;
        TextView message, time, isSeen;
        LinearLayout MessageLayout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            receiver_image = itemView.findViewById(R.id.receiver_image);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.messageTime);
            isSeen = itemView.findViewById(R.id.isSeen);
            MessageLayout = itemView.findViewById(R.id.message_layout);

        }
    }
}
