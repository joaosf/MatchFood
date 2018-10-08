package com.joao.matchfood;

import android.graphics.Point;
import android.support.annotation.IntegerRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

import Classes.Generic;
import FirebaseDB.FBFunctions;
import Objetos.Match;
import Objetos.Mensagem;

public class ActConversa extends AppCompatActivity {

    ListView lvListaConversa;

    LinearLayout layout;
    FloatingActionButton sendButton;
    EditText messageArea;
    ScrollView scrollView;
    //Firebase reference1, reference2;
    DatabaseReference myRef;

    String UsuarioLogado;
    String CodMatch = "";
    Match oMatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_conversa);
        //PrepararConversa();

        Bundle extras = getIntent().getExtras();

        CodMatch = extras.getString("CodMatch");
        UsuarioLogado = extras.getString("UsuarioLogado");

        layout = (LinearLayout) findViewById(R.id.layout1);
        sendButton = (FloatingActionButton) findViewById(R.id.btnSendMessage);
        messageArea = (EditText) findViewById(R.id.txtMsg);

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        oMatch = new Match(Integer.parseInt(CodMatch));

        myRef = FBFunctions.getTableReference("mensagens", true).child(CodMatch);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    /*Map<String, String> map = new HashMap<String, String>();
                    map.put("mensagem", messageText);
                    map.put("usuario", UsuOrigem);
                    map.put("notificado", "false");
                    myRef.push().setValue(map);
                    */
                    Mensagem msg = new Mensagem();
                    msg.setMensagem(messageText);
                    msg.setMatch(Integer.parseInt(CodMatch));
                    msg.setUsuario(Integer.parseInt(UsuarioLogado));

                    msg.setUsuariodestino(oMatch.getCodigoUsuario1());
                    msg.setNomeusuario(oMatch.getNomeUsuario2());
                    if (oMatch.getCodigoUsuario1() == Integer.parseInt(UsuarioLogado)) {
                        msg.setUsuariodestino(oMatch.getCodigoUsuario2());
                        msg.setNomeusuario(oMatch.getNomeUsuario1());
                    }
                    msg.sincronizarFirebase();

                    messageArea.setText("");
                }
            }
        });
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = (Map) dataSnapshot.getValue();
                Mensagem msg = new Mensagem();
                msg.setNomeusuario(map.get("nomeusuario").toString());
                msg.setUsuario(Integer.parseInt(map.get("usuario").toString()));
                msg.setUsuariodestino(Integer.parseInt(map.get("usuariodestino").toString()));
                msg.setMatch(Integer.parseInt(map.get("match").toString()));
                msg.setMensagem(map.get("mensagem").toString());

                if((msg.getUsuario()+"").equals(UsuarioLogado)){
                    addMessageBox(msg.getNomeusuario() + ":\n" + msg.getMensagem(), 1);
                }
                else{
                    addMessageBox(msg.getNomeusuario() + ":\n" + msg.getMensagem(), 2);
                }
                //layout.scrollTo(0, 0);
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

    public void addMessageBox(String message, int type) {
        TextView textView = new TextView(ActConversa.this);
        textView.setText(message);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        lp.setMargins(30, 5, 30, 5);
        textView.setBackgroundResource(R.drawable.rounded_corner1);
        if(type == 1) {
            textView.setBackgroundResource(R.drawable.rounded_corner2);
        }

        textView.setLayoutParams(lp);
        layout.addView(textView);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }
}
