package com.example.rhymebyrhymeversion2;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PoemProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private String id;
    private CircleImageView mProfileImage;
    private TextView mTitle;
    private TextView mDate;
    private TextView mLikes;
    private TextView mText;
    private ImageView mHeart;
    private TextView mAuthor;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private String path;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poem_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.poem_profile_action_bar);
        ((Toolbar)getSupportActionBar().getCustomView().getParent()).setContentInsetsAbsolute(0,0);
        mProfileImage = (CircleImageView) findViewById(R.id.poem_profile_profile);
        mTitle = (TextView) findViewById(R.id.poem_profile_title);
        mDate = (TextView) findViewById(R.id.poem_profile_date);
        mLikes = (TextView) findViewById(R.id.poem_profile_likes);
        mText = (TextView) findViewById(R.id.poem_profile_text);
        mHeart = (ImageView) findViewById(R.id.poem_profile_heart);
        mAuthor = (TextView) findViewById(R.id.poem_profile_name);
        mHeart.setOnClickListener(this);

        mRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        id = getIntent().getStringExtra("id");
        path = getIntent().getStringExtra("path");
        setPoemInfo();



    }




    private void setPoemInfo() {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        mRef.child("poems").child(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mTitle.setText("" + dataSnapshot.child("title").getValue());
                        mText.setText(Html.fromHtml("" + dataSnapshot.child("text").getValue()));
                        mDate.setText("" + dataSnapshot.child("date").getValue());
                        mAuthor.setText(getIntent().getStringExtra("author"));


                        if(dataSnapshot.child("likesAuthors").child(mAuth.getCurrentUser().getUid()).getValue()!=null
                                && (boolean) dataSnapshot.child("likesAuthors").child(mAuth.getCurrentUser().getUid()).getValue()) {
                            mHeart.setImageResource(R.drawable.blackheart);


                        } else {
                            mHeart.setImageResource(R.drawable.heart);
                            //poems.get(position).setLike(false);
                        }
                        mLikes.setText("" + dataSnapshot.child("likes").getValue());
                        //poems.get(position).setLikes(Integer.parseInt("" + dataSnapshot.child("likes").getValue()));

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
          if(path != null) {
                Picasso.with(context).load(path).resize(200, 200).centerCrop().into(mProfileImage);
            } else {
                Picasso.with(context).load(R.drawable.profile).resize(200, 200).centerCrop().into(mProfileImage);
            }
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.poem_profile_heart) {
            mRef.child("poems").child(id)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child("likesAuthors").child(mAuth.getCurrentUser().getUid()).getValue()!=null
                                    && (boolean)  dataSnapshot.child("likesAuthors").child(mAuth.getCurrentUser().getUid()).getValue()) {
                                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
                                mRef.child("poems").child(id).child("likesAuthors").child(mAuth.getCurrentUser().getUid())
                                                .setValue(false);
                                int likes = Integer.parseInt("" + mLikes.getText()) -1;
                                mRef.child("poems").child(id).child("likes").setValue(likes);
                                //mHeart.setImageResource(R.drawable.heart);
                            } else {
                                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
                                mRef.child("poems").child(id).child("likesAuthors").child(mAuth.getCurrentUser().getUid())
                                        .setValue(true);
                                int likes = Integer.parseInt("" + mLikes.getText()) +1;
                                mRef.child("poems").child(id).child("likes").setValue(likes);
                                //mHeart.setImageResource(R.drawable.blackheart);
                            }










                            /*if(dataSnapshot.child("likesAuthors").child(mAuth.getCurrentUser().getUid()).child("like").getValue()!=null
                                    && dataSnapshot.child("likesAuthors").child(mAuth.getCurrentUser().getUid()).child("like").getValue().equals("true")) {
                                mHeart.setImageResource(R.drawable.blackheart);


                            } else {
                                mHeart.setImageResource(R.drawable.heart);
                                //poems.get(position).setLike(false);
                            }
                            mLikes.setText("" + dataSnapshot.child("likes").getValue());
                            //poems.get(position).setLikes(Integer.parseInt("" + dataSnapshot.child("likes").getValue()));*/

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });




        }
    }
}
