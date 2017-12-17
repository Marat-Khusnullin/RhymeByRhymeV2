package com.example.rhymebyrhymeversion2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rhymebyrhymeversion2.model.User;
import com.example.rhymebyrhymeversion2.model.UserWithID;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.hdodenhof.circleimageview.CircleImageView;

public class TopAuthorsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference mRef;
    StorageReference mStorageRef;
    FirebaseUser mUser;
    ArrayList<UserWithID> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_authors);

        recyclerView = (RecyclerView) findViewById(R.id.top_authors_rv);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot eachUser: dataSnapshot.getChildren()){
                    User newUser = new User();
                    newUser.setName(eachUser.child("name").getValue().toString());
                    newUser.setSurname(eachUser.child("surname").getValue().toString());
                    newUser.setRating(Integer.parseInt(eachUser.child("rating").getValue().toString()));
                    UserWithID newUserWithID = new UserWithID(newUser, eachUser.getKey());
                    users.add(newUserWithID);
                }
                Collections.sort(users, new Comparator<UserWithID>() {
                    @Override
                    public int compare(UserWithID userWithID, UserWithID t1) {
                        return t1.getUser().getRating() - userWithID.getUser().getRating();
                    }
                });
                TopAuthorsListAdapter mAdapter = new TopAuthorsListAdapter(users, TopAuthorsActivity.this);
                recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private class TopAuthorsListAdapter extends RecyclerView.Adapter<TopAuthorsListAdapter.TopAuthorsListViewHolder>{
        ArrayList<UserWithID> usersList;
        Context context;

        public TopAuthorsListAdapter(ArrayList<UserWithID> usersList, Context context) {
            this.usersList = usersList;
            this.context = context;
        }

        @Override
        public TopAuthorsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TopAuthorsListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.top_authors_item, parent, false));
        }

        @Override
        public void onBindViewHolder(final TopAuthorsListViewHolder holder, int position) {
            final UserWithID user = usersList.get(position);
            holder.textView.setText(user.getUser().getName() + " " + user.getUser().getSurname());
            mStorageRef.child("images/" + user.getUserID()).getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    String path = storageMetadata.getDownloadUrl().toString();
                    Picasso.with(context).load(path).resize(200,200).centerCrop().into(holder.image);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Picasso.with(context)
                            .load(R.drawable.profile)
                            .resize(200,200).centerCrop().into(holder.image);
                }
            });
            if (position < 3)
                holder.star.setImageResource(R.drawable.ic_star_border_black_24dp);

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TopAuthorsActivity.this, UserProfileActivity.class);
                    intent.putExtra("userID", user.getUserID());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return usersList == null ? 0 : usersList.size();
        }

        class TopAuthorsListViewHolder extends RecyclerView.ViewHolder{
            CircleImageView image;
            TextView textView;
            ImageView star;
            View view;
            public TopAuthorsListViewHolder(View itemView) {
                super(itemView);
                view = itemView;
                image = (CircleImageView) itemView.findViewById(R.id.top_authors_image);
                textView = (TextView) itemView.findViewById(R.id.top_authors_textView);
                star = (ImageView) itemView.findViewById(R.id.top_authors_star);
            }
        }
    }
}
