package com.example.rhymebyrhymeversion2;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
        /*mainLayout.setVisibility(RelativeLayout.GONE);
        progressBar.setVisibility(ProgressBar.VISIBLE);*/

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
    }

    private void setUserInfo() {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        mRef.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                publications.setText(dataSnapshot.child("poemCount").getValue() + "\n публикации");
                name.setText(""+ dataSnapshot.child("name").getValue());
                surname = "" + dataSnapshot.child("surname").getValue();
                country = "" + dataSnapshot.child("country").getValue();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mStorageRef.child("images/" + user.getUid()).getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                String path = storageMetadata.getDownloadUrl().toString();
                Picasso.with(context).load(path).resize(200,200).centerCrop().into(mProfileImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        /*progressBar.setVisibility(ProgressBar.GONE);
                        mainLayout.setVisibility(RelativeLayout.VISIBLE);*/
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
            }
        });

        mStorageRef.child("images/background/" + user.getUid()).getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                String path = storageMetadata.getDownloadUrl().toString();
                Picasso.with(context).load(path).resize(200,200).centerCrop().into(mBackgroundImage);

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


}
