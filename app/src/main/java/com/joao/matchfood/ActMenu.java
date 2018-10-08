package com.joao.matchfood;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import Classes.Generic;
import LocalBD.BDAdapter;
import Notificacoes.NTFuncoes;
import Objetos.Cardapio;
import Objetos.Curtidas;
import Objetos.Match;
import Objetos.Usuario;

public class ActMenu extends AppCompatActivity {

    Button btnSim;
    Button btnNao;

    ImageView ivImagem;
    ProgressBar pbCarregando;

    FloatingActionButton btnChat;
    FloatingActionButton btnLogout;
    FloatingActionButton btnPerfil;
    String CodUsuario;
    Usuario oUsu;
    Cardapio oCardapio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_menu);

        Bundle extras = getIntent().getExtras();

        CodUsuario = extras.getString("CodUsuario");

        Match mt = new Match(ActMenu.this, CodUsuario);
        PrepararBotoesLike();

        btnChat = (FloatingActionButton) findViewById(R.id.btnChat);
        btnLogout = (FloatingActionButton) findViewById(R.id.btnLogout);
        btnPerfil = (FloatingActionButton) findViewById(R.id.btnPerfil);

        btnChat.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkgreen)));
        btnLogout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
        btnPerfil.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorDefaultBtn)));

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirChat();
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                execLogout();
            }
        });
        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirConfig();
            }
        });
        oUsu = new Usuario(Integer.parseInt(CodUsuario));
        oUsu.loadByField("codigo",CodUsuario,CodUsuario, Generic.showLoadingDialog(ActMenu.this,"Carregando dados..."));
        PrepararImageView();
        //NTFuncoes.showNotification(ActMenu.this, getIntent().getExtras(), "Fulano te enviou uma mensagem","Fulano: OLA");
        Notificacoes.Match m = new Notificacoes.Match(ActMenu.this,CodUsuario);
    }

    private void PrepararImageView() {
        ivImagem = (ImageView) findViewById(R.id.ivImagemCardapio);
        pbCarregando = (ProgressBar) findViewById(R.id.pbCarregando);
        ProgressDialog pd = Generic.showLoadingDialog(ActMenu.this,"Carregando cardápio...");
        pd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                oCardapio.getProximaImagem(ivImagem, pbCarregando);
            }
        });
        oCardapio = new Cardapio(pd,CodUsuario);


    }

    private void PrepararBotoesLike() {
        btnNao = (Button) findViewById(R.id.btnNao);

        btnNao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InserirCurtida("N");
            }
        });

        btnSim = (Button) findViewById(R.id.btnSim);
        btnSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InserirCurtida("S");
            }
        });
    }

    private void InserirCurtida(String Botao) {
        if (ivImagem.getVisibility() != View.VISIBLE) {
            Generic.MostrarMensagem(ActMenu.this,"Aguarde o proximo item do cardápio");
            return;
        }
        final Curtidas c = new Curtidas(oCardapio.getCodigo());

        //final Curtidas c = new Curtidas(1);
        c.setNomeUsuario(oUsu.getNome());
        c.setCodigoUsuario(oUsu.getCodigo());
        c.setIdadeMaxima(oUsu.getIdadeMaxima());
        c.setIdadeMinima(oUsu.getIdadeMinima());
        c.setResposta(Botao);
        c.setSexo(oUsu.getSexo());
        c.setCodigo(-1);
        c.setDistancia(oUsu.getLocalMaxima());
        c.setLatitude(oUsu.getLatitude());
        c.setLongitude(oUsu.getLongitude());
        c.setObjetivo(oUsu.getObjetivo());
        c.setCodigoMatch(-1);
        ProgressDialog pd = Generic.showLoadingDialog(ActMenu.this,"Salvando dados...");
        c.sincronizarFirebase(pd);
        pd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //c.insert(ActMenu.this);
                oCardapio.getProximaImagem(ivImagem, pbCarregando);
            }
        });
    }

    private void abrirConfig() {
        Intent Tela = new Intent(this, ActCadastro.class);
        Tela.putExtra("CodUsuario",CodUsuario);
        startActivity(Tela);
    }

    private void abrirChat() {
        Intent Tela = new Intent(this, ActChat.class);
        startActivity(Tela);
    }

    private void execLogout(){
        final Context ctx = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Tem certeza que deseja sair?")
                .setMessage("Já vai embora? Está cedo...")
                .setPositiveButton("Sair", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BDAdapter.executarComandoSQL(getApplicationContext(), "delete from UsuLog");
                        Intent Tela = new Intent(ctx, ActLogin.class);
                        startActivity(Tela);
                        finish();
                    }
                })
                .setNegativeButton("Ficar", null)
                .create()
                .show();
    }

}