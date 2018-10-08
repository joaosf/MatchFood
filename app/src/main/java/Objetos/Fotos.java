package Objetos;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.joao.matchfood.ActFotos;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import Classes.Generic;
import FirebaseDB.FBFunctions;
import FirebaseDB.FBObjeto;

public class Fotos {
    private Uri[] arFotos;
    private String OrigemChave;
    private String Origem;

    private int Contador = 0;

    private String NomeTabela = "fotos";

    public Fotos(Uri[] Fotos, String origemchave, String origem) {
        arFotos = Fotos;
        OrigemChave = origemchave;
        Origem = origem;
    }

    public FBObjeto[] toObject() {
        FBObjeto[] obj = new FBObjeto[arFotos.length];
        for (int i = 0; i < arFotos.length;i++) {
            obj[i] = new FBObjeto(NomeTabela+"/"+Origem+"/"+OrigemChave);
            obj[i].setChave(i+"");
            obj[i].setCampos(new String[]{"chave","origem","foto"});
            obj[i].setValores(new String[]{getChave()+"",
                    getChave(),
                    getOrigem(),
                    i+""});
        }
        return obj;
    }

    public void sincronizarFirebase(final ProgressDialog ProgD) {
        FBObjeto[] Obj = toObject();
        FBFunctions.setValues(Obj);
        if (arFotos.length > 0) {
            sendToStorage(ProgD,0);
        }
    }

    public void sincronizarFirebase(final ProgressDialog ProgD, int atPosition) {
        FBObjeto[] Obj = toObject();
        FBFunctions.setValues(Obj);
        sendToStorage(ProgD,atPosition);
    }

    public void loadFromStorage(final ProgressDialog pd, final DialogInterface.OnDismissListener EventoFim) {
        String sTabela = NomeTabela+"/"+Origem;
        Query q = FBFunctions.getQuery(sTabela,OrigemChave,null);

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Contador = (int) dataSnapshot.child(OrigemChave).getChildrenCount();
                pd.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        pd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (Contador > 0) {
                    pd.show();
                    arFotos = new Uri[Contador];
                    loadToMemory(pd,0,EventoFim);
                }
                pd.setOnDismissListener(null);
            }
        });
    }

    private void loadToMemory(final ProgressDialog ProgD, final int CodigoImagem, final DialogInterface.OnDismissListener EventoFim) {
        if (CodigoImagem == arFotos.length) {
            ProgressDialog p = Generic.showLoadingDialog(ProgD.getContext(),"Imagens carregadas...");
            p.setOnDismissListener(EventoFim);
            p.dismiss();
            return;
        }

        int FotoAtual = CodigoImagem +1;
        String NomeArquivo = "images/"+Origem+"/"+OrigemChave+"/"+CodigoImagem+".bmp";
        ProgD.setMessage("Carregando imagem "+FotoAtual+" de "+arFotos.length);

        if (!ProgD.isShowing()) {
            ProgD.show();
        }
        final StorageReference storageReference = FBFunctions.downloadUriImage(NomeArquivo);

        final File localFile;
        try {
            localFile = File.createTempFile("images", "bmp");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    arFotos[CodigoImagem] = Uri.fromFile(localFile);
                    ProgD.dismiss();
                    loadToMemory(ProgD,CodigoImagem+1,EventoFim);
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

    private void sendToStorage(final ProgressDialog ProgD, final int CodigoImagem) {
        if (CodigoImagem == arFotos.length) {
            return;
        }

        int FotoAtual = CodigoImagem +1;
        String NomeArquivo = "images/"+Origem+"/"+OrigemChave+"/"+CodigoImagem+".bmp";
        ProgD.setMessage("Enviando imagem "+FotoAtual+" de "+arFotos.length);
        if (!ProgD.isShowing()) {
            ProgD.show();
        }
        FBFunctions.sendUriImage(arFotos[CodigoImagem], CodigoImagem+"", ProgD,NomeArquivo);
        ProgD.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                sendToStorage(ProgD,CodigoImagem+1);
            }
        });
    }

    public Uri[] getFotos() {
        return arFotos;
    }

    public Uri getFotos(int atPos) {
        return arFotos[atPos];
    }

    public void setFotos(Uri[] fotos) {
        arFotos = fotos;
    }

    public String getChave() {
        return OrigemChave;
    }

    public String getOrigem() {
        return Origem;
    }

    public void addFoto(Uri Imagem) {
        Uri[] newUriArray;
        try {
            newUriArray = new Uri[arFotos.length+1];
            for (int i = 0; i < arFotos.length; i++) {
                newUriArray[i] = arFotos[i];
            }
        } catch (Exception e) {
            newUriArray = new Uri[1];
        }
        newUriArray[newUriArray.length-1] = Imagem;

        arFotos = newUriArray;
    }
}
