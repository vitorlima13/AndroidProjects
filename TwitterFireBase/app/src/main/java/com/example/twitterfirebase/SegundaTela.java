package com.example.twitterfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SegundaTela extends AppCompatActivity {

    private android.widget.ListView ListView;
    private ArrayAdapter arrayAdapter;

    private ArrayList<String> usuarios;
    private ArrayList<String> UserIds;
    private ArrayList<String> seguindo;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();

    private ChildEventListener childEventListener;
    private ValueEventListener valueEventListener;

    private String MeuUid;
    private String MeuNome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segunda_tela);

        //inicializar meus objetos
        seguindo = new ArrayList<>();
        usuarios = new ArrayList<>(); //através do EventList vai obter o objeto (usuario) do firebase
        UserIds = new ArrayList<>();

        //associar a lista de usuarios
        arrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_checked, usuarios);

        ListView = (ListView) findViewById(R.id.ListView);
        //choice mode adiciona on/off nos itens da lista
        ListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        ListView.setAdapter(arrayAdapter);

        ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                CheckedTextView checkedTextView = (CheckedTextView) view;
                if (checkedTextView.isChecked()){
                    seguindo.add(UserIds.get(position));
                } else {
                    seguindo.remove(seguindo.indexOf(UserIds.get(position)));
                }
                ref.child("usuarios").child(MeuUid).child("seguindo")
                        .setValue(seguindo);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        //inflar o menu. isso adiciona itens a action bar, se presente
        getMenuInflater().inflate(R.menu.menu_twitter, menu);
        return true;
    }

    //metodo utilizado para capturar que item foi clicado
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();

        if (id==R.id.feed){

            Intent i = new Intent(getApplicationContext(), MeuFeed.class);
            //próxima linha envia os dados para a próxima tela
            i.putStringArrayListExtra("seguindo", seguindo);
            startActivity(i);

        }else if (id==R.id.tweet){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Fazer um Tweet");
            final EditText MeuTweet = new EditText(this);
            builder.setView(MeuTweet);

            builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Map<String, Object> tweet = new HashMap<>();
                    tweet.put("msg", MeuTweet.getText().toString());
                    tweet.put("uid", MeuUid);
                    //cria um numero unico de tempo do sistema em milisegundos
                    //-1 serve para ordenar, em função do tempo
                    //tendo em vista que os dados no firebase não são ordenados
                    tweet.put("data", -1*System.currentTimeMillis());
                    tweet.put("nome", MeuNome);
                    //Cria uma referencia do firebase chamada tweets
                    //e define um valor a partir de 'tweet'
                    ref.child("tweets").push().setValue(tweet);
                    Toast.makeText(getApplicationContext(),"Seu tweet foi enviado",
                            Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();

            return true;

        }else if (id==R.id.sair){
            FirebaseAuth.getInstance().signOut();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart(){
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user==null){
            //se usuario estiver vazio, encerra a activity
            finish();
        } else {
            MeuUid = user.getUid();
            //adiciona um listener para a seguinte referencia do firebase
            ref.child("usuarios").child(MeuUid).child("nome").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //quando mudar o dado vamos obter o valor
                    MeuNome = dataSnapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        UserIds.clear();
        usuarios.clear();
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (!dataSnapshot.child("uid").getValue(String.class).equals(MeuUid)){
                    usuarios.add(dataSnapshot.child("nome").getValue(String.class));
                    UserIds.add(dataSnapshot.child("uid").getValue(String.class));
                    arrayAdapter.notifyDataSetChanged();
                    atualizarLista();
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

        ref.child("usuarios").addChildEventListener(childEventListener);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                seguindo.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    seguindo.add(data.getValue(String.class));
                }
                Log.d("MeuLog", "Seguindo: " + seguindo);
                atualizarLista();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.child("usuarios").child(MeuUid).child("seguindo").addValueEventListener(valueEventListener);
    }

    public void atualizarLista(){
        for (String uid:UserIds){
            if (seguindo.contains(uid)){
                ListView.setItemChecked(UserIds.indexOf(uid), true);
            } else {
                ListView.setItemChecked(UserIds.indexOf(uid), false);
            }
        }
    }
}
