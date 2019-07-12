package com.example.instagram;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseFile;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {
    private List<Post> fPosts;
    Context context;
    public FeedAdapter (List<Post> posts) {
        fPosts = posts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View tweetView = inflater.inflate(R.layout.post_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Post post = fPosts.get(position);

        final String description = post.getDescription();
        final String username = post.getUser().getUsername();
        final ParseFile image = post.getImage();
        final String timestamp = getRelativeTimeAgo(post.getCreatedAt().toString());

        holder.tvUserName.setText(username);
        holder.tvDescription.setText(description);
        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent seePostDetails = new Intent(context, PostDetailsActivity.class);
                seePostDetails.putExtra("imageURL", image.getUrl());
                seePostDetails.putExtra("description", description);
                seePostDetails.putExtra("username", username);
                seePostDetails.putExtra("timestamp", timestamp);
                context.startActivity(seePostDetails);
            }
        });
        holder.timestamp.setText(timestamp);

        String url= image.getUrl();
        Glide.with(context).load(url).into(holder.postImage);
    }

    @Override
    public int getItemCount() {
        return fPosts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
//        public ImageView tvProfileImage;
        public TextView tvUserName;
        public TextView tvDescription;
//        public TextView timestamp;
        public ImageView postImage;
        public TextView timestamp;

        public ViewHolder(View itemView) {
            super(itemView);
            postImage = (ImageView) itemView.findViewById(R.id.ivPost);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUsername);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            timestamp = (TextView) itemView.findViewById(R.id.timeStamp);
        }
    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
