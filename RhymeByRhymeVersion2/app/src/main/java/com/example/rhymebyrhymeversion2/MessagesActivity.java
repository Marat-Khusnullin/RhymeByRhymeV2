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
import android.widget.TextView;

import com.example.rhymebyrhymeversion2.model.ChatMessage;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesActivity extends AppCompatActivity {

    DatabaseReference mRef;
    FirebaseUser mUser;
    RecyclerView recyclerView;
    ArrayList<ChatMessage> chatMessageArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        recyclerView = (RecyclerView) findViewById(R.id.list_of_messages_rv);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference();
        mRef.child("chat/" + mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final long personNumber = dataSnapshot.getChildrenCount();
                final long[] i = {0};
                for (DataSnapshot eachPerson: dataSnapshot.getChildren()){
                    eachPerson
                            .getRef()
                            .limitToLast(1)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot eachMessage: dataSnapshot.getChildren()){
                                chatMessageArrayList.add(eachMessage.getValue(ChatMessage.class));
                            }
                            i[0]++;
                            if (i[0] == personNumber){
                                MessagesListAdapter mAdapter = new MessagesListAdapter(chatMessageArrayList, MessagesActivity.this);
                                recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
                                recyclerView.setAdapter(mAdapter);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public class MessagesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private ArrayList<ChatMessage> arrayList;
        private Context context;

        public MessagesListAdapter(ArrayList<ChatMessage> arrayList, Context context) {
            this.arrayList = arrayList;
            this.context = context;
        }

        @Override
        public int getItemViewType(int position) {
            if (arrayList.get(position).getWhoSend().equals(mUser.getUid()))
                return 1;
            else
                return 0;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType){
                case 0:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_of_message_list, parent, false);
                    return new MessageListLastNotMineViewHolder(view);
                case 1:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_of_message_list_2, parent, false);
                    return new MessageListLastMineViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final ChatMessage message = arrayList.get(position);
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
            mRef = FirebaseDatabase.getInstance().getReference();
            if (holder instanceof MessageListLastNotMineViewHolder){
                final MessageListLastNotMineViewHolder mHolder = (MessageListLastNotMineViewHolder) holder;
                mRef.child("users").child(message.getWhoSend()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mHolder.personName.setText(dataSnapshot.child("name").getValue().toString() + " " + dataSnapshot.child("surname").getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mHolder.messageText.setText(message.getMessageText());
                mStorageRef.child("images/" + message.getToWhomSend()).getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        String path = storageMetadata.getDownloadUrl().toString();
                        Picasso.with(context).load(path).resize(200,200).centerCrop().into(mHolder.personAvatar);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Picasso.with(context)
                                .load(R.drawable.profile)
                                .resize(200,200).centerCrop().into(mHolder.personAvatar);
                    }
                });

                mHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MessagesActivity.this, ChatActivity.class);
                        intent.putExtra("userID", message.getToWhomSend());
                        startActivity(intent);
                    }
                });
            }
            else if (holder instanceof MessageListLastMineViewHolder){
                final MessageListLastMineViewHolder mHolder = (MessageListLastMineViewHolder) holder;
                mRef.child("users").child(message.getToWhomSend()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mHolder.personName.setText(dataSnapshot.child("name").getValue().toString() + " " + dataSnapshot.child("surname").getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mHolder.messageText.setText(message.getMessageText());
                mStorageRef.child("images/" + message.getToWhomSend()).getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        String path = storageMetadata.getDownloadUrl().toString();
                        Picasso.with(context).load(path).resize(200,200).centerCrop().into(mHolder.personAvatar);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Picasso.with(context)
                                .load(R.drawable.profile)
                                .resize(200,200).centerCrop().into(mHolder.personAvatar);
                    }
                });
                mStorageRef.child("images/" + mUser.getUid()).getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        String path = storageMetadata.getDownloadUrl().toString();
                        Picasso.with(context).load(path).resize(200,200).centerCrop().into(mHolder.myAvatar);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Picasso.with(context)
                                .load(R.drawable.profile)
                                .resize(200,200).centerCrop().into(mHolder.myAvatar);
                    }
                });

                mHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MessagesActivity.this, ChatActivity.class);
                        intent.putExtra("userID", message.getToWhomSend());
                        startActivity(intent);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return arrayList == null ? 0 : arrayList.size();
        }

        class MessageListLastMineViewHolder extends RecyclerView.ViewHolder{
            CircleImageView personAvatar;
            CircleImageView myAvatar;
            TextView personName;
            TextView messageText;
            View view;

            public MessageListLastMineViewHolder(View itemView) {
                super(itemView);
                view = itemView;
                personAvatar = (CircleImageView) itemView.findViewById(R.id.profile_image2);
                myAvatar = (CircleImageView) itemView.findViewById(R.id.my_profile_image_message);
                personName = (TextView) itemView.findViewById(R.id.last_message_human2);
                messageText = (TextView) itemView.findViewById(R.id.last_message_text2);
            }
        }

        class MessageListLastNotMineViewHolder extends RecyclerView.ViewHolder{
            CircleImageView personAvatar;
            TextView personName;
            TextView messageText;
            View view;

            public MessageListLastNotMineViewHolder(View itemView) {
                super(itemView);
                view = itemView;
                personAvatar = (CircleImageView) itemView.findViewById(R.id.profile_image);
                personName = (TextView) itemView.findViewById(R.id.last_message_human);
                messageText = (TextView) itemView.findViewById(R.id.last_message_text);
            }
        }
    }
}
