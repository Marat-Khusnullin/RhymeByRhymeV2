package com.example.rhymebyrhymeversion2;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rhymebyrhymeversion2.adapter.PoemsListAdapter;
import com.example.rhymebyrhymeversion2.model.ChatMessage;
import com.firebase.ui.database.FirebaseListAdapter;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private FirebaseListAdapter<ChatMessage> adapter;
    Toolbar toolbar;
    TextView titleText;
    ImageView backButton;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.chat_action_bar);
        ((Toolbar)getSupportActionBar().getCustomView().getParent()).setContentInsetsAbsolute(0,0);
        titleText = (TextView) findViewById(R.id.chat_toolbar_text);
        backButton = (ImageView) findViewById(R.id.chat_close_image);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String anotherUserID = getIntent().getStringExtra("userID");
        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        final String[] userFullName = {""};
        mRef.child("users").child(anotherUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userFullName[0] = dataSnapshot.child("name").getValue().toString() + " " + dataSnapshot.child("surname").getValue().toString();
                titleText.setText(userFullName[0]);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText) findViewById(R.id.input);

                mRef.child("chat/" + currentUser.getUid() + "/" + anotherUserID).push().setValue(new ChatMessage(input.getText().toString(),
                                FirebaseAuth.getInstance().getCurrentUser().getUid(), anotherUserID)
                );
                mRef.child("chat/" + anotherUserID + "/" + currentUser.getUid()).push().setValue(new ChatMessage(input.getText().toString(),
                                FirebaseAuth.getInstance().getCurrentUser().getUid(), anotherUserID)
                );
                input.setText("");
            }
        });

        ListView listOfMessages = (ListView) findViewById(R.id.list_of_messages);

        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class, R.layout.message, FirebaseDatabase.getInstance().getReference().child("chat/" + currentUser.getUid() + "/" + anotherUserID)) {
            @Override
            protected void populateView(final View v, final ChatMessage model, int position) {
                final CircleImageView ownImage = (CircleImageView) findViewById(R.id.ownPhoto);
                CircleImageView image = (CircleImageView) findViewById(R.id.photo);
                TextView messageText = (TextView) v.findViewById(R.id.message_text);
                final TextView messageUser = (TextView) v.findViewById(R.id.message_user);
                TextView messageTime = (TextView) v.findViewById(R.id.message_time);

                messageText.setText(model.getMessageText());
                mRef.child("users").child(model.getWhoSend()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        messageUser.setText(dataSnapshot.child("name").getValue().toString() + " " + dataSnapshot.child("surname").getValue().toString());

                        /*StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                        mStorageRef.child("images/" + model.getWhoSend()).getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                            @Override
                            public void onSuccess(StorageMetadata storageMetadata) {
                                String path = storageMetadata.getDownloadUrl().toString();
                                if(ownImage!=null)
                                Picasso.with(v.getContext()).load(path).resize(200,200).centerCrop().into(ownImage);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Picasso.with(context)
                                        .load(R.drawable.profile)
                                        .resize(200,200).centerCrop().into(ownImage);

                            }
                        });*/

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                messageTime.setText(DateFormat.format("dd MMMM yyyy (HH:mm:ss)",
                        model.getMessageTime()));
            }
        };

        listOfMessages.setAdapter(adapter);
    }
}
