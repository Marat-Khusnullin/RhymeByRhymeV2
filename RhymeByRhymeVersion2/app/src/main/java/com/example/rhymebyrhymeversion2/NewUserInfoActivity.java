package com.example.rhymebyrhymeversion2;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewUserInfoActivity extends AppCompatActivity {

    ImageView background;
    CircleImageView profile;
    EditText name;
    EditText surname;
    EditText country;
    Toolbar toolbar;
    TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_info);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.new_poem_action_bar);
        ((Toolbar)getSupportActionBar().getCustomView().getParent()).setContentInsetsAbsolute(0,0);
        titleText = (TextView) findViewById(R.id.new_pub_toolbar_text);
        titleText.setText("Редактирование");

        background = (ImageView) findViewById(R.id.background_image);
        profile = (CircleImageView) findViewById(R.id.profile_image);

        name = (EditText) findViewById(R.id.new_name_text);
        surname = (EditText) findViewById(R.id.new_surname_text);
        country = (EditText) findViewById(R.id.new_country_text);




    }
}
