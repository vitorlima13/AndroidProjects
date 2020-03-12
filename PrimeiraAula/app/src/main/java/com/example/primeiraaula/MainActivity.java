package com.example.primeiraaula;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    public Button BotaoHomem, BotaoMulher;
    public TextView TotalPessoas;
    public int QtdHomem, QtdMulher, TotPessoas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QtdHomem = 0;
        QtdMulher = 0;
        TotPessoas = 0;
    }

    public void AdicionaHomem(View botao){
        QtdHomem++;
        TotPessoas++;
        BotaoHomem = findViewById(R.id.BotaoHomens);
        BotaoHomem.setText("Homens: " + QtdHomem);
        TotalPessoas = findViewById(R.id.MeuTexto);
        TotalPessoas.setText("Total: " + TotPessoas);
    }

    public void AdicionaMulher(View botao){
        QtdMulher++;
        TotPessoas++;
        BotaoMulher = findViewById(R.id.BotaoMulheres);
        BotaoMulher.setText("Mulheres: " + QtdMulher);
        TotalPessoas = findViewById(R.id.MeuTexto);
        TotalPessoas.setText("Total: " + TotPessoas);
    }
}











