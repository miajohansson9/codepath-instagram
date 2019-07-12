package com.example.instagram;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;

import com.example.instagram.model.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    RecyclerView rvPosts;
    List<Post> fPosts;
    FeedAdapter feedAdapter;
    private SwipeRefreshLayout swipeContainer;
    MenuItem miActionProgressItem;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        rvPosts = (RecyclerView) findViewById(R.id.rvFeed);
        fPosts = new ArrayList<>();
        feedAdapter = new FeedAdapter(fPosts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvPosts.setLayoutManager(linearLayoutManager);
        rvPosts.setAdapter(feedAdapter);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvPosts.addOnScrollListener(scrollListener);

        bottomNavigationView.getMenu().findItem(R.id.action_home).setIcon(R.drawable.instagram_home_filled_24);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        break;
                    case R.id.action_newPost:
                        navigationHelper(HomeActivity.class);
                        break;
                    case R.id.action_profile:
                        navigationHelper(ProfileActivity.class);
                        break;
                    default: return true;
                }
                return true;
            }
        });

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                clear();
                loadTopPosts();
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.darker_gray,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_dark);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        loadTopPosts();

        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Associate searchable configuration with the SearchView
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private void loadTopPosts() {
        showProgressBar();
        final Post.Query postsQuery = new Post.Query();
        final int limit = fPosts.size() + 20;
        postsQuery.getTop(limit).withUser();

        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    for (int i = limit - 20; i < objects.size(); i++) {
                        fPosts.add(objects.get(i));
                        feedAdapter.notifyDataSetChanged();
                    }
                    hideProgressBar();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void loadNextDataFromApi(int offset) {
        // 1. First, clear the array of data
        loadTopPosts();
        // 2. Notify the adapter of the update
        feedAdapter.notifyDataSetChanged(); // or notifyItemRangeRemoved
    }

    private void navigationHelper(Class activity) {
        final Intent loginToTimeline = new Intent(this, activity);
        startActivity(loginToTimeline);
    }

    // Clean all elements of the recycler
    public void clear() {
        fPosts.clear();
        feedAdapter.notifyDataSetChanged();
    }

    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }

}