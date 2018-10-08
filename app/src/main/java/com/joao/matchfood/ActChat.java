package com.joao.matchfood;

import android.content.ClipData;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Classes.Generic;

public class ActChat extends AppCompatActivity {

    ListView lvLista ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lvLista = (ListView) findViewById(R.id.Lista);

        FloatingActionButton btnVoltar = (FloatingActionButton) findViewById(R.id.Voltar);
        btnVoltar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorDefaultBtn)));
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        PrepararLista();
    }

    public void AbrirConversa() {
        Intent Tela = new Intent(this, ActConversa.class);
        Tela.putExtra("UsuOrigem", "1");
        Tela.putExtra("UsuOrigemNome", "João");
        Tela.putExtra("UsuDestino", "2");
        Tela.putExtra("UsuDestinoNome", "José");
        Tela.putExtra("CodMatch", "1");
        Tela.putExtra("UsuarioLogado","1");
        startActivity(Tela);
    }

    private void PrepararLista(){
        String[] Titulos = new String[]{
                "Fulano 1"
        };

        Integer[] Imagens = new Integer[]{
                R.drawable.ic_users
        };

        String[] SubTitulos = new String[]{
                "Esteve online há 1 hora atrás"
        };
        SimpleAdapter sa = Generic.getAdapterListView(getBaseContext(),Titulos, SubTitulos, Imagens);
        lvLista.setAdapter(sa);

        lvLista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AbrirConversa();
            }
        });
    }
}
