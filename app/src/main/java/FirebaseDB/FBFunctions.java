package FirebaseDB;

import android.app.ProgressDialog;
import android.net.Uri;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Classes.Generic;
import Objetos.Fotos;


public class FBFunctions {

    public static DatabaseReference getTableReference(String sTable, boolean bManterOffline) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(sTable);
        myRef.keepSynced(bManterOffline);
        return myRef;
    }

    public static Query getQuery(String ReferenceName, String FiltroCampo, String FiltroValor) {
        DatabaseReference myRef = getTableReference(ReferenceName, false);
        if (FiltroValor != null) {
            return myRef.orderByChild(FiltroCampo).equalTo(FiltroValor);
        }
        return myRef.orderByChild(FiltroCampo);
    }

    public static void setValues(FBObjeto oCadastro) {
        String ReferenceName = oCadastro.getTabela();
        String Chave = oCadastro.getChave();
        String[] Campos = oCadastro.getCampos();
        String[] Valores = oCadastro.getValores();

        DatabaseReference myRef = getTableReference(ReferenceName, false);
        for (int i = 0; i < Campos.length;i++) {
            myRef.child(Chave).child(Campos[i]).setValue(Valores[i]);
        }
    }

    public static void putValues(FBObjeto oCadastro) {
        String ReferenceName = oCadastro.getTabela();
        String Chave = oCadastro.getChave();
        String[] Campos = oCadastro.getCampos();
        String[] Valores = oCadastro.getValores();

        DatabaseReference myRef = getTableReference(ReferenceName, false);
        Map<String, String> map = new HashMap<String, String>();

        for (int i = 0; i < Campos.length;i++) {
            map.put(Campos[i],Valores[i]);
        }

        myRef.child(Chave).push().setValue(map);
    }

    public static void setValues(FBObjeto[] oCadastro) {
        for (int i = 0; i < oCadastro.length;i++) {
            String ReferenceName = oCadastro[i].getTabela();
            String Chave = oCadastro[i].getChave();
            String[] Campos = oCadastro[i].getCampos();
            String[] Valores = oCadastro[i].getValores();

            DatabaseReference myRef = getTableReference(ReferenceName, false);
            for (int x = 0; x < Campos.length;x++) {
                myRef.child(Chave).child(Campos[x]).setValue(Valores[x]);
            }
        }
    }


    public static void deleteValues(FBObjeto oCadastro) {
        String ReferenceName = oCadastro.getTabela();
        String Chave = oCadastro.getChave();
        DatabaseReference myRef = getTableReference(ReferenceName, false);
        myRef.child(Chave).setValue(null);
    }

    public static void sendUriImage(Uri Imagem, String Usuario, final ProgressDialog pd, String Destino) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://matchfood-36098.appspot.com");

        StorageReference riversRef = storageRef.child(Destino);
        //"images/"+Usuario+"/"+Imagem.getLastPathSegment()

        UploadTask upTask = riversRef.putFile(Imagem);
        upTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                pd.dismiss();
            }
        });
    }

    public static StorageReference downloadUriImage(final String Destino) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://matchfood-36098.appspot.com/"+Destino);
        return storageRef;
    }



    public static String getStringValue(DataSnapshot ds, String Campo) {
        return ds.child(Campo).getValue(String.class);
    }
}