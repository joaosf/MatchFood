package Objetos;

import android.app.ProgressDialog;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import Classes.Generic;
import FirebaseDB.FBFunctions;
import FirebaseDB.FBObjeto;

/**
 * Created by joaos on 29/07/2017.
 */

public class Mensagem {
    private String mensagem;
    private int usuario;
    private int match;
    private int usuariodestino;
    private String nomeusuario;

    private String NomeTabela = "mensagens";

    public void Mensagem() {
    }

    public String getNomeusuario() {
        return nomeusuario;
    }

    public void setNomeusuario(String nomeusuario) {
        this.nomeusuario = nomeusuario;
    }

    public int getUsuariodestino() {
        return usuariodestino;
    }

    public void setUsuariodestino(int usuariodestino) {
        this.usuariodestino = usuariodestino;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public int getUsuario() {
        return usuario;
    }

    public void setUsuario(int usuario) {
        this.usuario = usuario;
    }

    public int getMatch() {
        return match;
    }

    public void setMatch(int match) {
        this.match = match;
    }

    public void sincronizarFirebase() {
        FBObjeto Obj = toObject();
        FBFunctions.putValues(Obj);
    }

    private FBObjeto toObject() {
        FBObjeto obj = new FBObjeto(NomeTabela);
        obj.setChave(getMatch()+"");
        obj.setCampos(getCampos());
        obj.setValores(getValores());
        return obj;
    }

    private String[] getCampos() {
        return new String[]{
                "usuario","mensagem","match","usuariodestino","notificado","nomeusuario"};
    }

    private String[] getValores() {
        return new String[]{
                getUsuario()+"",getMensagem(),getMatch()+"",getUsuariodestino()+"","false",getNomeusuario()};
    }
}
