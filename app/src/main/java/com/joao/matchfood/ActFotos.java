package com.joao.matchfood;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import Classes.Generic;
import FirebaseDB.FBFunctions;
import Objetos.Fotos;
import Objetos.Usuario;

public class ActFotos extends AppCompatActivity {

    TextView tvAtual;
    ImageView isImagens;
    Button btnAnterior;
    Button btnNovo;
    Button btnProximo;
    //Fotos arImagens;
    int imgSelecionada = 0;

    Usuario ObjUsuario;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_fotos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tvAtual = (TextView) findViewById(R.id.tvAtual);

        Bundle extras = getIntent().getExtras();
        String Codigo = extras.getString("CodUsuario");
        ObjUsuario = new Usuario(Integer.parseInt(Codigo));

        pd = Generic.showLoadingDialog(this, "Salvando dados...");

        ObjUsuario.loadByField("codigo",Codigo,Codigo, pd);
        ProgressDialog pd = Generic.showLoadingDialog(this,"Carregando imagens.");
        DialogInterface.OnDismissListener Evento = new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isImagens.setImageURI(ObjUsuario.getImagens().getFotos()[0]);
                imgSelecionada = 0;
                refreshLabelImg();
            }
        };
        ObjUsuario.getImagensPerfil(pd,Evento);

        FloatingActionButton btnSalvar = (FloatingActionButton) findViewById(R.id.btnSalvar);
        btnSalvar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorDefaultBtn)));
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            ProximaTela();
            }
        });
        PrepararIS();
    }

    public void ProximaTela() {
        Intent Menu = new Intent(ActFotos.this, ActMenu.class);
        Menu.putExtra("CodUsuario", ObjUsuario.getCodigo()+"");
        startActivity(Menu);
        finish();
    }

    private void PrepararIS() {
        isImagens = (ImageView) findViewById(R.id.ivImagens);
        btnAnterior = (Button) findViewById(R.id.btnAnterior);
        btnNovo = (Button) findViewById(R.id.btnNovo);
        btnProximo = (Button) findViewById(R.id.btnProximo);
        isImagens.setImageResource(R.drawable.ic_users);

        btnNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int FotoSelecionada = 20495;
                Intent Tela = new Intent();
                Tela.setType("image/*");
                Tela.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(Tela,"Selecione sua foto!"), FotoSelecionada);
            }
        });

        btnProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((ObjUsuario.getImagens().getFotos() != null) && (imgSelecionada < ObjUsuario.getImagens().getFotos().length-1)) {
                    isImagens.setImageURI(ObjUsuario.getImagens().getFotos()[imgSelecionada+1]);
                    imgSelecionada = imgSelecionada +1;
                    refreshLabelImg();
                }
            }
        });

        btnAnterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgSelecionada > 0) {
                    isImagens.setImageURI(ObjUsuario.getImagens().getFotos()[imgSelecionada-1]);
                    imgSelecionada = imgSelecionada -1;
                    refreshLabelImg();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 20495 && resultCode == RESULT_OK) {
            Uri imagemSelecionada = data.getData();
            /*
            try {
                Bitmap bm = Generic.decodeUri(ActFotos.this,imagemSelecionada,325);
                imagemSelecionada = Generic.getImageUri(ActFotos.this,bm);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            */
            isImagens.setImageURI(imagemSelecionada);
            ObjUsuario.getImagens().addFoto(imagemSelecionada);
            imgSelecionada = ObjUsuario.getImagens().getFotos().length-1;
            ObjUsuario.getImagens().sincronizarFirebase(Generic.showLoadingDialog(ActFotos.this,"Enviando imagem..."),
                    ObjUsuario.getImagens().getFotos().length-1);

            refreshLabelImg();
        }
    }

    private void refreshLabelImg() {
        int LabelImgAtual = imgSelecionada+1;
        tvAtual.setText(LabelImgAtual+"/"+ObjUsuario.getImagens().getFotos().length);
    }
}
