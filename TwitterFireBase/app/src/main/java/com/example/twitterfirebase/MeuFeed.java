package com.example.twitterfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeuFeed extends AppCompatActivity {

    //cast dos objetos
    private ListView listView;
    private SimpleAdapter simpleAdapter;
    private List<Map<String,String>> tweetData = new ArrayList<>();
    private ArrayList<String> seguindo;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();

    private ChildEventListener childEventListener;
    private Query queryRef;
    private String MeuUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_meu_feed);

        listView = (ListView) findViewById(R.id.ListView);
        tweetData = new ArrayList<>();

        simpleAdapter  = new SimpleAdapter(this, tweetData,
                android.R.layout.simple_list_item_2,
                new String[]{"conteudo","nomeUsuario"},
                new int[]{android.R.id.text1, android.R.id.text2});

        listView.setAdapter(simpleAdapter);

        //receber o StringArrayList da activity anterior
        Intent i = getIntent();
        seguindo = i.getStringArrayListExtra("seguindo");
    }

    @Override
    protected void onStart(){
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user==null){
            finish();
        } else {
            MeuUid = user.getUid();
        }

        tweetData.clear();

        queryRef = ref.child("tweets").orderByChild("data");
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (seguindo.contains(dataSnapshot.child("uid").getValue(String.class)) || dataSnapshot.child("uid").getValue(String.class).equals(MeuUid)){
                    Map<String,String> tweet = new HashMap<>(2);
                    tweet.put("conteudo", dataSnapshot.child("msg").getValue(String.class));
                    tweet.put("nomeUsuario", dataSnapshot.child("nome").getValue(String.class));
                    tweetData.add(tweet);
                    simpleAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        queryRef.addChildEventListener(childEventListener);

    }

    @Override
    protected void onStop(){
        super.onStop();
        queryRef.removeEventListener(childEventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        int id = item.getItemId();

        if (id==R.id.inicio){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
