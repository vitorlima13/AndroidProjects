package com.example.aula4_listadetarefas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText MeuTexto;
    private ListView MinhaLista;
    private Button MeuBotao;

    private SQLiteDatabase BancoDados;

    private ArrayAdapter<String> ItensAdaptador;
    private ArrayList<Integer> ids;
    private ArrayList<String> itens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MeuTexto = (EditText) findViewById(R.id.MeuTexto);
        MinhaLista = (ListView) findViewById(R.id.MinhaLista);
        MeuBotao = (Button) findViewById(R.id.MeuBotao);

        CarregaTarefas();

        MeuBotao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdicionaTarefas(MeuTexto.getText().toString());
            }
        });

        MinhaLista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

                //ApagaTarefas(ids.get(position));
                AlertaApagarTarefa(position);

                return false;
            }
        });
    }

    private void CarregaTarefas(){
        try{
            BancoDados = openOrCreateDatabase("ListaTarefas", MODE_PRIVATE, null);
            BancoDados.execSQL("CREATE TABLE IF NOT EXISTS MinhasTarefas(id INTEGER PRIMARY KEY AUTOINCREMENT, tarefa VARCHAR)");

            //String NovaTarefa = MeuTexto.getText().toString();
            //BancoDados.execSQL("INSERT INTO MinhasTarefas(tarefa) VALUES('" + NovaTarefa + "')");

            Cursor cursor = BancoDados.rawQuery("SELECT * FROM MinhasTarefas ORDER BY id DESC", null);

            int IndiceColunaID = cursor.getColumnIndex("id");
            int IndiceColunaTarefa = cursor.getColumnIndex("tarefa");

            itens = new ArrayList<String>();
            ids = new ArrayList<Integer>();

            ItensAdaptador = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    itens);

            MinhaLista.setAdapter(ItensAdaptador);

            cursor.moveToFirst();
            while (cursor!=null){
                Log.d("BancoDados", "ID: " + cursor.getString(IndiceColunaID) + " Tarefa: " + cursor.getString(IndiceColunaTarefa));
                itens.add(cursor.getString(IndiceColunaTarefa));
                ids.add(Integer.parseInt(cursor.getString(IndiceColunaID)));
                cursor.moveToNext();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void AdicionaTarefas(String tarefa){
        if (tarefa.equals("")){
            Toast.makeText(MainActivity.this, "Preencha o campo tarefa!", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(MainActivity.this, "Tarefa " + tarefa + " adicionada!", Toast.LENGTH_SHORT).show();
            MeuTexto.setText("");
            BancoDados.execSQL("INSERT INTO MinhasTarefas(tarefa) VALUES('" + tarefa + "')");
            CarregaTarefas();
        }
    }

    private void ApagaTarefas(Integer id){
        try{
            BancoDados.execSQL("DELETE FROM MinhasTarefas WHERE id="+id);
            Toast.makeText(MainActivity.this, "Tarefa removida!", Toast.LENGTH_SHORT).show();
            CarregaTarefas();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void AlertaApagarTarefa(Integer id){
        String TarefaSelecionada = itens.get(id);
        final Integer NumeroId = id;

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Aviso!")
                .setMessage("Deseja apagar a tarefa: " + TarefaSelecionada + "?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ApagaTarefas(ids.get(NumeroId));
                    }
                }).setNegativeButton("Não", null).show();

    }
}
