package com.example.rhymebyrhymeversion2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MenuActivity extends Activity {

    TextView news;
    TextView topAuthors;
    TextView messages;
    TextView likedPosts;
    TextView notifications;
    TextView settings;
    TextView logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        news = (TextView) findViewById(R.id.news_menu);
        topAuthors = (TextView) findViewById(R.id.top_authors_menu);
        messages = (TextView) findViewById(R.id.messages_menu);
        likedPosts = (TextView) findViewById(R.id.liked_posts_menu);
        notifications = (TextView) findViewById(R.id.notifications_menu);
        settings = (TextView) findViewById(R.id.settings_menu);
        logout = (TextView) findViewById(R.id.exit_account_menu);

        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, MessagesActivity.class);
                startActivity(intent);
                finish();
            }
        });

        topAuthors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, TopAuthorsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, PoemByTagsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
