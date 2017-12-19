package com.example.rhymebyrhymeversion2;

import android.app.ActionBar;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.rhymebyrhymeversion2.adapter.PoemsListAdapter;
import com.example.rhymebyrhymeversion2.model.Poem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;

public class PoemByTagsActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private Context context = this;
    private ImageView searchImage;
    private EditText tags;
    private LinkedList<Poem> poems;

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poem_by_tags);
        toolbar = (Toolbar) findViewById(R.id.tag_toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.poems_by_tag_action_bar);
        ((Toolbar)getSupportActionBar().getCustomView().getParent()).setContentInsetsAbsolute(0,0);

        recyclerView = (RecyclerView) findViewById(R.id.poem_by_tag_rv);
        tags = (EditText) findViewById(R.id.search_view);
        searchImage = (ImageView) findViewById(R.id.search_image);
        mAuth = FirebaseAuth.getInstance();
        searchImage.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        poems = new LinkedList();

        final String tagString = "" + tags.getText();

                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
                mRef.child("poems").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int count = (int) dataSnapshot.getChildrenCount();
                        int boof = 0;
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            boof++;
                            String poemTags = "" + postSnapshot.child("tags").getValue();
                            String author = "" + postSnapshot.child("author").getValue();
                            for (String retval : tagString.split(" ")) {
                                if(poemTags.contains(retval)) {

                                    Poem poem = postSnapshot.getValue(Poem.class);
                                    poem.setAuthor(author);
                                    poems.add(poem);
                                }
                            }
                            if(boof == count) {
                                PoemsListAdapter adapter = new PoemsListAdapter(poems,context);
                                adapter.setPath("tag");
                                recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
                                recyclerView.setAdapter(adapter);
                            }
                        }


                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

}
