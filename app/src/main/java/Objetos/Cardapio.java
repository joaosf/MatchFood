package Objetos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import Classes.Generic;
import FirebaseDB.FBFunctions;
import FirebaseDB.FBObjeto;
import LocalBD.BDAdapter;
import LocalBD.BDFunctions;

public class Cardapio {

    private int Codigo;
    private Uri Imagem;
    private int CountImages;
    private Context ctx;

    private String NomeTabela = "cardapio";

    public Cardapio(final ProgressDialog ProgD, final String CodigoUsuario) {
        ctx = ProgD.getContext();
        loadLikes(ProgD,CodigoUsuario);
        loadCountImages(ProgD);
    }

    private void loadLikes(final ProgressDialog ProgD, final String CodigoUsuario) {
        Query query = FBFunctions.getQuery("curtidas", "codigousuario",CodigoUsuario);
        query.keepSynced(true);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                BDAdapter.executarComandoSQL(ProgD.getContext(),"insert into curtidas values ("+dataSnapshot.child("codigocardapio").getValue(String.class)+")");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                BDAdapter.executarComandoSQL(ProgD.getContext(),"delete from curtidas where codigocardapio = "+dataSnapshot.child("codigocardapio").getValue(String.class));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void loadCountImages(final ProgressDialog ProgD) {
        Query q = FBFunctions.getQuery("contador",NomeTabela,null);

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int Chave = Integer.parseInt(dataSnapshot.child(NomeTabela).getValue(String.class));
                setCountImages(Chave);
                Generic.stopLoadingDialog(ProgD);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void loadToMemory(final int CodigoImagem, final ImageView imageView, final ProgressBar ProgBar) {
        String NomeArquivo = "images/cardapio/"+CodigoImagem+".bmp";
        final StorageReference storageReference = FBFunctions.downloadUriImage(NomeArquivo);
        final File localFile;
        try {
            localFile = File.createTempFile("images", "bmp");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    imageView.setImageURI(Uri.fromFile(localFile));
                    setCodigo(CodigoImagem);
                    setImagem(Uri.fromFile(localFile));
                    ProgBar.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getProximaImagem(final ImageView imageView, final ProgressBar ProgBar) {
        ProgBar.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        Thread td = new Thread(new Runnable() {
            @Override
            public void run() {
                int imgRand;
                int Count = 1;
                Random random = new Random();
                imgRand = random.nextInt(CountImages);
                while (haveLike(imgRand)) {
                    imgRand = random.nextInt(CountImages);
                    Count += 1;
                    if (Count == CountImages) {
                        return;
                    }
                }
                loadToMemory(imgRand, imageView, ProgBar);
            }
        });
        td.start();
    }

    private boolean haveLike(final int CodigoCardapio) {
        Cursor curSelect = BDAdapter.executaConsultaSQL(ctx,"select codigocardapio from curtidas where codigocardapio ="+CodigoCardapio);
        return (curSelect.getCount() > 0);
    }

    private void setCountImages(int countImages) {
        CountImages = countImages;
    }

    private void setCodigo(int codigo) {
        Codigo = codigo;
    }

    private void setImagem(Uri imagem) {
        Imagem = imagem;
    }

    public int getCodigo() {
        return Codigo;
    }
}
