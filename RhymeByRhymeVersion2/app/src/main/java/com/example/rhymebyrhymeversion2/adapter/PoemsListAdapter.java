package com.example.rhymebyrhymeversion2.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rhymebyrhymeversion2.R;
import com.example.rhymebyrhymeversion2.model.Poem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Марат on 16.12.2017.
 */

public class PoemsListAdapter extends RecyclerView.Adapter<PoemsListAdapter.MyListViewHolder> {

    private LinkedList<Poem> poems;
    private Context context;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private String path;



    public PoemsListAdapter(LinkedList list, Context context){
        this.poems = list;
        this.context = context;

    }


    @Override
    public PoemsListAdapter.MyListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.poem_item, parent, false);
        return new MyListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PoemsListAdapter.MyListViewHolder holder, final int position) {

        mRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        holder.title.setText(poems.get(position).getTitle());
        holder.date.setText(poems.get(position).getDate());
        holder.likes.setText("" + poems.get(position).getLikes());
        holder.text.setText(Html.fromHtml(poems.get(position).getText()));
        holder.author.setText(poems.get(position).getAuthor());
        Picasso.with(context).load(path).resize(200,200).centerCrop().into(holder.image);
        mRef.child("poems").child(""+ poems.get(position).getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("likesAuthors").child(mAuth.getCurrentUser().getUid()).child("like").getValue()!=null
                                && dataSnapshot.child("likesAuthors").child(mAuth.getCurrentUser().getUid()).child("like").getValue().equals("true")) {
                            holder.heart.setImageResource(R.drawable.blackheart);
                            poems.get(position).setLike(true);

                        } else {
                            holder.heart.setImageResource(R.drawable.heart);
                            poems.get(position).setLike(false);
                        }
                        holder.likes.setText("" + dataSnapshot.child("likes").getValue());
                        //poems.get(position).setLikes(Integer.parseInt("" + dataSnapshot.child("likes").getValue()));

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }


    @Override
    public int getItemCount() {
        return poems.size();
    }

    public class MyListViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView date;
        TextView likes;
        TextView text;
        TextView full;
        ImageView heart;
        CircleImageView image;
        TextView author;

        public MyListViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.poemTitle);
            title.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-BoldCondensed.ttf"));
            date = (TextView) itemView.findViewById(R.id.poemDate);
            date.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-LightItalic.ttf"));
            likes = (TextView) itemView.findViewById(R.id.poemLikes);
            likes.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf"));
            text = (TextView) itemView.findViewById(R.id.poemText);
            text.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf"));
            full = (TextView) itemView.findViewById(R.id.full);
            full.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf"));
            heart = (ImageView) itemView.findViewById(R.id.poem_item_heart);
            image = (CircleImageView) itemView.findViewById(R.id.poem_item_profile);
            author = (TextView) itemView.findViewById(R.id.poem_item_name);
        }
    }


    public void setPath(String path){
        this.path = path;
    }

    public void setList(LinkedList poems) {
        this.poems = poems;
    }





}
