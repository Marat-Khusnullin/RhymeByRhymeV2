package com.example.rhymebyrhymeversion2;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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

public class UserProfileActivity extends AppCompatActivity {
    Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private ImageView mBackgroundImage;
    private ImageView mProfileImage;
    private ImageView sendMessage;
    private ImageView subscribeToUser;
    private TextView publications;
    private TextView subscribers;
    private TextView subsriptions;
    private TextView name;
    private Context context = this;
    private ImageView menu;
    private String surname = "";
    private String country = "";
    String userID;
    private ProgressBar progressBar;
    private RelativeLayout mainLayout;

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.user_profile_action_bar);
        ((Toolbar)getSupportActionBar().getCustomView().getParent()).setContentInsetsAbsolute(0,0);

        mRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mRecyclerView = (RecyclerView) findViewById(R.id.poems_recycler_user);
        mBackgroundImage = (ImageView) findViewById(R.id.background_image_user);
        mProfileImage = (ImageView) findViewById(R.id.profile_image_user);
        menu = (ImageView) findViewById(R.id.image_menu_user);
        publications = (TextView) findViewById(R.id.publication_user);
        subscribers = (TextView) findViewById(R.id.subscribers_user);
        subsriptions = (TextView) findViewById(R.id.subscriptions_user);
        subscribeToUser = (ImageView) findViewById(R.id.subscribe_user);
        sendMessage = (ImageView) findViewById(R.id.send_message_user);
        name = (TextView) findViewById(R.id.name_main_user);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_user);
        mainLayout = (RelativeLayout) this.findViewById(R.id.main_layout_user);
        userID = getIntent().getStringExtra("userID");
        mainLayout.setVisibility(RelativeLayout.GONE);
        progressBar.setVisibility(ProgressBar.VISIBLE);

        publications.setTypeface(Typeface.createFromAsset(
                getAssets(), "fonts/Roboto-BoldCondensed.ttf"));
        subscribers.setTypeface(Typeface.createFromAsset(
                getAssets(), "fonts/Roboto-BoldCondensed.ttf"));
        subsriptions.setTypeface(Typeface.createFromAsset(
                getAssets(), "fonts/Roboto-BoldCondensed.ttf"));

        setCorrectSubscribeButton(subscribeToUser, userID, FirebaseAuth.getInstance().getCurrentUser().getUid());

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this, ChatActivity.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });

        subscribeToUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRef.child("subscribers").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            mRef.child("subscriptions").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(userID).setValue(Boolean.TRUE);
                            mRef.child("subscribers").child(userID).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(Boolean.TRUE);
                            subscribeToUser.setImageResource(R.drawable.ic_check_black_24dp);
                        }
                        else {
                            mRef.child("subscriptions").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(userID).removeValue();
                            mRef.child("subscribers").child(userID).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                            subscribeToUser.setImageResource(R.drawable.ic_arrow_forward_black_24dp);
                        }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        setUserInfo();
    }

    private void setCorrectSubscribeButton(final ImageView subscribeToUser, final String userID, final String currentUserID) {
        mRef.child("subscribers").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(currentUserID)){
                    subscribeToUser.setImageResource(R.drawable.ic_arrow_forward_black_24dp);
                }
                else {
                    subscribeToUser.setImageResource(R.drawable.ic_check_black_24dp);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setUserInfo() {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        mRef.child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                publications.setText("           " + dataSnapshot.child("poemCount").getValue() + "\n  публикации");
                name.setText(""+ dataSnapshot.child("name").getValue());
                surname = "" + dataSnapshot.child("surname").getValue();
                country = "" + dataSnapshot.child("country").getValue();
                DatabaseReference mRef2 = FirebaseDatabase.getInstance().getReference();
                mRef2.child("subscribers").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        subscribers.setText("           " + dataSnapshot.getChildrenCount() + "\n  подписчики");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mRef2.child("subscriptions").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
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

        mStorageRef.child("images/" + userID).getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                String path = storageMetadata.getDownloadUrl().toString();
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
            }
        });

        mStorageRef.child("images/background/" + userID).getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
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
