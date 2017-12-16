package com.example.rhymebyrhymeversion2;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rhymebyrhymeversion2.model.Poem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.UUID;

public class NewPoemActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    Toolbar toolbar;
    ImageView cancel;
    ImageView accept;
    EditText title;
    EditText poem;
    EditText tags;
    TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_poem);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.new_poem_action_bar);
        ((Toolbar)getSupportActionBar().getCustomView().getParent()).setContentInsetsAbsolute(0,0);
        titleText = (TextView) findViewById(R.id.new_pub_toolbar_text);
        titleText.setText("Новая публикация");

        cancel = (ImageView) findViewById(R.id.new_pub_close_image);
        accept = (ImageView) findViewById(R.id.new_pub_accept_image);

        title = (EditText) findViewById(R.id.editTextTitle);
        poem = (EditText) findViewById(R.id.editTextPoem);
        tags = (EditText) findViewById(R.id.editTextTags);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                mRef = FirebaseDatabase.getInstance().getReference();
                final FirebaseUser firebaseUser = mAuth.getCurrentUser();

                DatabaseReference mRef2 = FirebaseDatabase.getInstance().getReference();
                mRef2.child("users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int id =  Integer.parseInt("" + dataSnapshot.child("poemCount").getValue());
                        String newTitle = title.getText().toString();
                        if(newTitle.equals("")) newTitle = "Без названия";
                        String uniqueID = UUID.randomUUID().toString();
                        mRef.child("users").child(firebaseUser.getUid()).child("poems").child(uniqueID).setValue(new Poem(firebaseUser.getUid(),
                                uniqueID
                                ,newTitle,
                                Html.toHtml(poem.getText()),0,""+ DateFormat.format("dd.MM.yyyy",new Date()), tags.getText().toString(),
                                getIntent().getStringExtra("name")));
                        mRef = FirebaseDatabase.getInstance().getReference();
                        mRef.child("poems").child(uniqueID).setValue(new Poem(firebaseUser.getUid(),
                                uniqueID
                                ,newTitle,
                                Html.toHtml(poem.getText()),0,""+ DateFormat.format("dd.MM.yyyy",new Date()), tags.getText().toString(),
                                getIntent().getStringExtra("name")));
                        mRef = FirebaseDatabase.getInstance().getReference();
                        mRef.child("users").child(firebaseUser.getUid()).child("poemCount").setValue(++id);
                        Toast.makeText(NewPoemActivity.this, "ПУБЛИКАЦИЯ ПРОШЛА", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(NewPoemActivity.this, MainProfileActivity.class);
                        startActivity(intent);

                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
