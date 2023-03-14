package sanity.com.AdviseME;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import AdviseME.R;


public class AdapterChatList extends RecyclerView.Adapter<AdapterChatList.myViewHolder> {

    private Context context;
   private List<User_details> usersList;
    private HashMap<String, String> lastMessageMap;

    public AdapterChatList(Context context, List<User_details> usersList) {
        this.context = context;
        this.usersList = usersList;
        lastMessageMap = new HashMap<>();
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_chatlist, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final myViewHolder holder, final int position) {

        //get data
        final String userName = usersList.get(position).getFirst_name() + " " + usersList.get(position).getLast_name();
        String userUID = usersList.get(position).getuID();
        final String UserImage = usersList.get(position).getProfile_Image();
        String onlineStatus = usersList.get(position).getOnlineStatus();
        String LastMessage = lastMessageMap.get(userUID);

        //set data
        //name
        holder.hisName.setText(userName);

        //last Message
        if (LastMessage == null || LastMessage.equals("default")) {
            holder.lastMessageTV.setVisibility(View.GONE);
        } else {
            holder.lastMessageTV.setVisibility(View.VISIBLE);
            holder.lastMessageTV.setText(LastMessage);
        }
        //his image
        Picasso.get().load(UserImage).networkPolicy(NetworkPolicy.OFFLINE).into(holder.his_image, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(UserImage).into(holder.his_image);
            }
        });


        //online status
        if (onlineStatus.equals("Online")) {
           // Picasso.get().load(R.drawable.circle_online).into(holder.onlineStatus);
            holder.onlineStatus.setImageResource(R.drawable.circle_online);
        }
        else {
            //Picasso.get().load(R.drawable.circle_offline).into(holder.onlineStatus);
            holder.onlineStatus.setImageResource(R.drawable.circle_offline);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent = new Intent(context, chat_activity.class);
                Bundle chatBundle = new Bundle();
                chatBundle.putString("personName", userName);
                chatBundle.putString("personImage", UserImage);
                chatBundle.putString("UID", usersList.get(position).getuID());
                chatIntent.putExtras(chatBundle);
                context.startActivity(chatIntent);
            }
        });
    }

    public void SetLastMessageMap (String USerID, String LastMessage){
        lastMessageMap.put(USerID,LastMessage);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        ImageView his_image, onlineStatus;
        TextView hisName, lastMessageTV;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            his_image = itemView.findViewById(R.id.his_image);
            onlineStatus = itemView.findViewById(R.id.online_status);
            hisName = itemView.findViewById(R.id.his_name);
            lastMessageTV = itemView.findViewById(R.id.last_message);
        }
    }
}
