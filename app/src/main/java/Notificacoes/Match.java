package Notificacoes;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.joao.matchfood.ActMenu;

import java.util.Map;

import FirebaseDB.FBFunctions;

public class Match {
    private Context ctx;
    private String Usuario;

    public Match(Context context, String Usuario) {
        this.ctx = context;
        this.Usuario = Usuario;
        startThread();
    }

    private void startThread() {
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Query query = FBFunctions.getQuery("match","codigousuario1",Usuario);
                query.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Map map = (Map) dataSnapshot.getValue();
                        Boolean notificado = Boolean.parseBoolean(map.get("notificadousuario1").toString());
                        String nome = map.get("nomeusuario2").toString();
                        if (!notificado) {
                            Bundle b = new Bundle();
                            b.putString("CodUsuario",Usuario);
                            NTFuncoes.showNotification(ctx, b, "Você tem um novo Match!","Você tem um novo Match com "+nome);
                            FBFunctions.getTableReference("match",false).child(dataSnapshot.getKey()+"/notificadousuario1").setValue("true");
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        thread1.start();
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Query query = FBFunctions.getQuery("match","codigousuario2",Usuario);
                query.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Map map = (Map) dataSnapshot.getValue();
                        Boolean notificado = Boolean.parseBoolean(map.get("notificadousuario2").toString());
                        String nome = map.get("nomeusuario1").toString();
                        if (!notificado) {
                            Bundle b = new Bundle();
                            b.putString("CodUsuario",Usuario);
                            NTFuncoes.showNotification(ctx, b, "Você tem um novo Match!","Você tem um novo Match com "+nome);
                            FBFunctions.getTableReference("match",false).child(dataSnapshot.getKey()+"/notificadousuario2").setValue("true");
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        thread2.start();
    }
}
