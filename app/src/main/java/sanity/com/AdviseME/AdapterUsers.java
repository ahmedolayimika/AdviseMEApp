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

import java.util.List;
import AdviseME.R;


public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.myHolder> {

    //List<users> usersList;
    List<User_details> usersList;
    Context context;

    public AdapterUsers( Context context, List<User_details> usersList) {
        this.usersList = usersList;
        this.context = context;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.each_user, parent, false);
        return new myHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final myHolder holder, final int position) {
        final String image = usersList.get(position).getProfile_Image();
        final String FullName = usersList.get(position).getFirst_name() + " " + usersList.get(position).getLast_name();
        String Email = usersList.get(position).getEmail();

        holder.UserFullName.setText(FullName);
        holder.USerEmail.setText(Email);
        Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).into(holder.UserImage, new Callback() {
            @Override
            public void onSuccess() {
                Picasso.get().load(image).into(holder.UserImage);
            }

            @Override
            public void onError(Exception e) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent chatIntent = new Intent(context, chat_activity.class);
                Bundle chatBundle = new Bundle();
                chatBundle.putString("personName", FullName);
                chatBundle.putString("personImage", image);
                chatBundle.putString("UID", usersList.get(position).getuID());
                chatIntent.putExtras(chatBundle);
                context.startActivity(chatIntent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    static class myHolder extends RecyclerView.ViewHolder {

        ImageView UserImage;
        TextView UserFullName;
        TextView USerEmail;


        public myHolder(@NonNull View itemView) {
            super(itemView);
            UserImage = itemView.findViewById(R.id.user_image);
            UserFullName = itemView.findViewById(R.id.each_user_name);
            USerEmail = itemView.findViewById(R.id.each_user_email);
        }
    }
}
