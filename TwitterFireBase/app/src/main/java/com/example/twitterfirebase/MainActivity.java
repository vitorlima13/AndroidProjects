package com.example.twitterfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth MinhaAuth;
    private FirebaseAuth.AuthStateListener MinhaAuthListener;
    private EditText CampoNome,CampoEmail, CampoSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MinhaAuth = FirebaseAuth.getInstance();

        //Inicializar objetos de interface
        CampoNome = (EditText) findViewById(R.id.editText);
        CampoEmail = (EditText) findViewById(R.id.editText2);
        CampoSenha = (EditText) findViewById(R.id.editText3);

        MinhaAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user!=null) {
                    //se possuir usuario passa para a proxima activity
                    Log.d("MeuLog", "Usuário conectado: " + user.getUid());
                    Intent i = new Intent(getApplicationContext(), SegundaTela.class);
                    startActivity(i);
                } else {
                    Log.d("MeuLog","Sem usuários conectados");
                }
            }
        };

    }

    @Override
    public void onStart(){
        super.onStart();
        MinhaAuth.addAuthStateListener(MinhaAuthListener);
    }

    //quando parar a view remove o listener
    @Override
    public void onStop(){
        super.onStop();
        if (MinhaAuthListener!=null){
            MinhaAuth.removeAuthStateListener(MinhaAuthListener);
        }
    }

    public void ClicaLogin(View view){
        MinhaAuth.signInWithEmailAndPassword(CampoEmail.getText().toString(), CampoSenha.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            Log.d("MeuLog", "Falha na autenticação");
                        }
                    }
                });
    }

    public void ClicaCriarUsuario(View view){
        MinhaAuth.createUserWithEmailAndPassword(CampoEmail.getText().toString(), CampoSenha.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            Log.d("MeuLog","Falha no cadastro. Causa: " + task.getException().getMessage());
                        } else {
                            //Criar o firebase database
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            //cria uma referencia chamada 'usuarios'
                            //FirebaseAuth.getInstance() cria um id para essa autenticação
                            //cria uma nova referencia dentro do campo 'usuarios' e o filho
                            //dele vai criar um objeto com id (getUid) dele
                            DatabaseReference ref = database.getReference("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            //dentro desse ojbeto vamos salvar o nome do usuario
                            ref.child("nome").setValue(CampoNome.getText().toString());
                            //e o user id, utilizado para login do usuario
                            ref.child("uid").setValue(FirebaseAuth.getInstance().getUid());
                        }
                    }
                });
    }
}
