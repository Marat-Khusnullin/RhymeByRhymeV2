package com.example.rhymebyrhymeversion2;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.rhymebyrhymeversion2.adapter.PoemsListAdapter;
import com.example.rhymebyrhymeversion2.model.Poem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;

public class MainProfileActivity extends AppCompatActivity  {
    Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private ImageView mBackgroundImage;
    private ImageView mProfileImage;
    private TextView publications;
    private TextView subscribers;
    private TextView subsriptions;
    private TextView newPublication;
    private TextView name;
    private Context context = this;
    private ImageView edit;
    private ImageView menu;
    private String surname = "";
    private String country = "";
    private ProgressBar progressBar;
    private RelativeLayout mainLayout;
    private LinkedList<Poem> poems;
    PoemsListAdapter adapter;

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.main_profile_action_bar);
        ((Toolbar)getSupportActionBar().getCustomView().getParent()).setContentInsetsAbsolute(0,0);

        mRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mRecyclerView = (RecyclerView) findViewById(R.id.poems_recycler);
        mBackgroundImage = (ImageView) findViewById(R.id.background_image);
        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        edit = (ImageView) findViewById(R.id.image_edit);
        menu = (ImageView) findViewById(R.id.image_menu);
        publications = (TextView) findViewById(R.id.publication);
        subscribers = (TextView) findViewById(R.id.subscribers);
        subsriptions = (TextView) findViewById(R.id.subscriptions);
        newPublication = (TextView) findViewById(R.id.newPub);
        name = (TextView) findViewById(R.id.name_main);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mainLayout = (RelativeLayout) this.findViewById(R.id.main_layout);

        mainLayout.setVisibility(RelativeLayout.GONE);
        progressBar.setVisibility(ProgressBar.VISIBLE);

        publications.setTypeface(Typeface.createFromAsset(
                getAssets(), "fonts/Roboto-BoldCondensed.ttf"));
        subscribers.setTypeface(Typeface.createFromAsset(
                getAssets(), "fonts/Roboto-BoldCondensed.ttf"));
        subsriptions.setTypeface(Typeface.createFromAsset(
                getAssets(), "fonts/Roboto-BoldCondensed.ttf"));
        newPublication.setTypeface(Typeface.createFromAsset(
                getAssets(), "fonts/Roboto-Light.ttf"));

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainProfileActivity.this, NewUserInfoActivity.class);
                intent.putExtra("name", name.getText().toString());
                intent.putExtra("surname",surname);
                intent.putExtra("country", country);
                startActivity(intent);
            }
        });

        newPublication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainProfileActivity.this, NewPoemActivity.class);
                intent.putExtra("name", name.getText().toString());
                startActivity(intent);
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainProfileActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });

        setUserInfo();
        //setPoems();
    }

    private void setUserInfo() {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        final FirebaseUser user = mAuth.getCurrentUser();
        mRef.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                publications.setText("           " + dataSnapshot.child("poemCount").getValue() + "\n  публикации");
                name.setText(""+ dataSnapshot.child("name").getValue());
                surname = "" + dataSnapshot.child("surname").getValue();
                country = "" + dataSnapshot.child("country").getValue();
                DatabaseReference mRef2 = FirebaseDatabase.getInstance().getReference();
                mRef2.child("subscribers").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        subscribers.setText("           " + dataSnapshot.getChildrenCount() + "\n  подписчики");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mRef2.child("subscriptions").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        subsriptions.setText("           " + dataSnapshot.getChildrenCount() + "\n   подписки");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mStorageRef.child("images/" + user.getUid()).getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                String path = storageMetadata.getDownloadUrl().toString();
                    adapter = new PoemsListAdapter(poems, context);
                    adapter.setPath(path);
                    setPoems();
                Picasso.with(context).load(path).resize(200,200).centerCrop().into(mProfileImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(ProgressBar.GONE);
                        mainLayout.setVisibility(RelativeLayout.VISIBLE);

                    }

                    @Override
                    public void onError() {

                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Picasso.with(context)
                        .load(R.drawable.profile)
                        .resize(200,200).centerCrop().into(mProfileImage);
                adapter = new PoemsListAdapter(poems, context);
                setPoems();
            }
        });

        mStorageRef.child("images/background/" + user.getUid()).getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                String path = storageMetadata.getDownloadUrl().toString();
                Picasso.with(context).load(path).resize(500,500).centerCrop().into(mBackgroundImage);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Picasso.with(context)
                        .load(R.drawable.background)
                        .resize(200,200).centerCrop().into(mBackgroundImage);
            }
        });

    }


    private void setPoems() {
        poems = new LinkedList();
        DatabaseReference mRef;
        FirebaseUser user = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference();
        mRef.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String author = "" + dataSnapshot.child("name").getValue();
                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
                mRef.child("users").child(mAuth.getCurrentUser().getUid()).child("poems").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Poem poem = postSnapshot.getValue(Poem.class);
                            poem.setAuthor(author);
                            poems.add(poem);
                        }
                        adapter.setList(poems);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
                        mRecyclerView.setAdapter(adapter);

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }





}
