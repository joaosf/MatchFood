package Objetos;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.joao.matchfood.ActLogin;

import java.util.HashMap;
import java.util.Map;

import Classes.Generic;
import FirebaseDB.FBFunctions;
import FirebaseDB.FBObjeto;
import LocalBD.BDAdapter;
import LocalBD.BDFunctions;
import LocalBD.BDObjeto;

public class Curtidas {
    private int Codigo;
    private int CodigoUsuario;
    private String NomeUsuario;
    private int CodigoCardapio;
    private String Resposta;
    private Double Latitude;
    private Double Longitude;
    private int IdadeMinima;
    private int IdadeMaxima;
    private int Distancia;
    private String Objetivo;
    private String Sexo;
    private int CodigoMatch;

    private String NomeTabela = "curtidas";

    public Curtidas(int codigoCardapio) {
        this.CodigoCardapio = codigoCardapio;
    }

    public BDObjeto toDBObject() {
        BDObjeto obj = new BDObjeto(NomeTabela);
        obj.setCampoChave("codigo");
        obj.setChave(getCodigo()+"");
        obj.setCampos(this.getCampos());
        obj.setValores(this.getValores());
        return obj;
    }

    public FBObjeto toFBObject() {
        FBObjeto obj = new FBObjeto(NomeTabela);
        obj.setChave(getCodigo()+"");
        obj.setCampos(getCampos());
        obj.setValores(getValores());
        return obj;
    }

    public void load(Context ctx) {
        BDObjeto obj = toDBObject();
        String SQL = BDFunctions.getSQLSelectByObjeto(obj,true);
        Cursor curSelect = BDAdapter.executaConsultaSQL(ctx,SQL);
        curSelect.moveToFirst();
        if (curSelect.getCount() > 0) {
            setCodigoCardapio(curSelect.getInt(curSelect.getColumnIndex("codigocardapio")));
            setCodigoUsuario(curSelect.getInt(curSelect.getColumnIndex("codigousuario")));
            setNomeUsuario(curSelect.getString(curSelect.getColumnIndex("nomeusuario")));
            setResposta(curSelect.getString(curSelect.getColumnIndex("resposta")));
            setLatitude(curSelect.getDouble(curSelect.getColumnIndex("latitude")));
            setLongitude(curSelect.getDouble(curSelect.getColumnIndex("longitude")));
            setObjetivo(curSelect.getString(curSelect.getColumnIndex("objetivo")));
            setSexo(curSelect.getString(curSelect.getColumnIndex("sexo")));
            setDistancia(curSelect.getInt(curSelect.getColumnIndex("distanciamaxima")));
            setIdadeMinima(curSelect.getInt(curSelect.getColumnIndex("idademinima")));
            setIdadeMaxima(curSelect.getInt(curSelect.getColumnIndex("idademaxima")));
            setCodigoMatch(curSelect.getInt(curSelect.getColumnIndex("codigomatch")));
        }
        curSelect.close();
    }

    public void load(Context ctx, String CampoChave, String Chave) {
        BDObjeto obj = toDBObject();
        obj.setCampoChave(CampoChave);
        obj.setChave(Chave);
        String SQL = BDFunctions.getSQLSelectByObjeto(obj,true);
        Cursor curSelect = BDAdapter.executaConsultaSQL(ctx,SQL);
        curSelect.moveToFirst();
        if (curSelect.getCount() > 0) {
            setCodigo(curSelect.getInt(curSelect.getColumnIndex("codigo")));
            setCodigoCardapio(curSelect.getInt(curSelect.getColumnIndex("codigocardapio")));
            setCodigoUsuario(curSelect.getInt(curSelect.getColumnIndex("codigousuario")));
            setNomeUsuario(curSelect.getString(curSelect.getColumnIndex("nomeusuario")));
            setResposta(curSelect.getString(curSelect.getColumnIndex("resposta")));
            setLatitude(curSelect.getDouble(curSelect.getColumnIndex("latitude")));
            setLongitude(curSelect.getDouble(curSelect.getColumnIndex("longitude")));
            setObjetivo(curSelect.getString(curSelect.getColumnIndex("objetivo")));
            setSexo(curSelect.getString(curSelect.getColumnIndex("sexo")));
            setDistancia(curSelect.getInt(curSelect.getColumnIndex("distanciamaxima")));
            setIdadeMinima(curSelect.getInt(curSelect.getColumnIndex("idademinima")));
            setIdadeMaxima(curSelect.getInt(curSelect.getColumnIndex("idademaxima")));
            setCodigoMatch(curSelect.getInt(curSelect.getColumnIndex("codigomatch")));
        }
        curSelect.close();
    }

    /*
    public void insert(Context ctx) {
        BDAdapter.executarComandoSQL(ctx,BDFunctions.getSQLInsertByObjeto(toDBObject()));
    }
    */

    public void delete(Context ctx) {
        BDAdapter.executarComandoSQL(ctx,BDFunctions.getSQLDeleteByObjeto(toDBObject()));
    }

    public void loadByField(String Campo, String Valor, final String Chave, final ProgressDialog ProgD) {
        Query q = FBFunctions.getQuery(NomeTabela,Campo,Valor);

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    setCodigo(Integer.parseInt(FBFunctions.getStringValue(dataSnapshot, Chave + "/codigo")));
                    setCodigoCardapio(Integer.parseInt(FBFunctions.getStringValue(dataSnapshot, Chave + "/codigocardapio")));
                    setCodigoUsuario(Integer.parseInt(FBFunctions.getStringValue(dataSnapshot, Chave + "/codigousuario")));
                    setNomeUsuario(FBFunctions.getStringValue(dataSnapshot, Chave + "/nomeusuario"));
                    setResposta(FBFunctions.getStringValue(dataSnapshot, Chave + "/resposta"));
                    setLatitude(Double.parseDouble(FBFunctions.getStringValue(dataSnapshot, Chave + "/latitude")));
                    setLongitude(Double.parseDouble(FBFunctions.getStringValue(dataSnapshot, Chave + "/longitude")));
                    setObjetivo(FBFunctions.getStringValue(dataSnapshot, Chave + "/objetivo"));
                    setSexo(FBFunctions.getStringValue(dataSnapshot, Chave + "/sexo"));
                    setIdadeMinima(Integer.parseInt(FBFunctions.getStringValue(dataSnapshot, Chave + "/idademinima")));
                    setIdadeMaxima(Integer.parseInt(FBFunctions.getStringValue(dataSnapshot, Chave + "/idademaxima")));
                    setDistancia(Integer.parseInt(FBFunctions.getStringValue(dataSnapshot, Chave + "/distancia")));
                    setCodigoMatch(Integer.parseInt(FBFunctions.getStringValue(dataSnapshot, Chave + "/codigomatch")));
                    ProgD.dismiss();
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sincronizarFirebase(final ProgressDialog ProgD) {
        if (getCodigo() == -1) {
            Query q = FBFunctions.getQuery("contador",NomeTabela,null);

            q.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int Chave;
                    try {
                        Chave = Integer.parseInt(dataSnapshot.child(NomeTabela).getValue(String.class))+1;
                    } catch (Exception e) {
                        Chave = 1;
                    }

                    setCodigo(Chave);
                    Map<String, String> map = new HashMap<String, String>();
                    for (int i = 0;i < getCampos().length;i++) {
                        map.put(getCampos()[i], getValores()[i]);
                    }
                    FBFunctions.getTableReference(NomeTabela,false).push().setValue(map);

                    FBFunctions.getTableReference("contador",false).child(NomeTabela).setValue(Chave+"");
                    Generic.stopLoadingDialog(ProgD);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            FBObjeto Obj = toFBObject();
            FBFunctions.setValues(Obj);
            Generic.stopLoadingDialog(ProgD);
        }
    }

    public int getCodigo() {
        return Codigo;
    }

    public void setCodigo(int codigo) {
        Codigo = codigo;
    }

    public int getCodigoCardapio() {
        return CodigoCardapio;
    }

    public void setCodigoCardapio(int codigoCardapio) {
        CodigoCardapio = codigoCardapio;
    }

    public String getResposta() {
        return Resposta;
    }

    public void setResposta(String resposta) {
        Resposta = resposta;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public String getSexo() {
        return Sexo;
    }

    public void setSexo(String sexo) {
        Sexo = sexo;
    }

    public int getIdadeMinima() {
        return IdadeMinima;
    }

    public void setIdadeMinima(int idadeMinima) {
        IdadeMinima = idadeMinima;
    }

    public int getIdadeMaxima() {
        return IdadeMaxima;
    }

    public void setIdadeMaxima(int idadeMaxima) {
        IdadeMaxima = idadeMaxima;
    }

    public int getDistancia() {
        return Distancia;
    }

    public void setDistancia(int distancia) {
        Distancia = distancia;
    }

    public void setObjetivo(String objetivo) {
        Objetivo = objetivo;
    }

    public String getObjetivo() {
        return Objetivo;
    }

    @Override
    public String toString() {
        return  "codigo="+Codigo+"\n"+
                "codigocardapio="+CodigoCardapio+"\n"+
                "resposta="+Resposta+"\n"+
                "latitude="+Latitude+"\n"+
                "longitude="+Longitude+"\n"+
                "objetivo="+Objetivo+"\n"+
                "sexo="+Sexo+"\n"+
                "distanciamaxima="+Distancia+"\n"+
                "idademaxima="+IdadeMaxima+"\n"+
                "idademinima="+IdadeMinima+"\n";
    }

    public int getCodigoUsuario() {
        return CodigoUsuario;
    }

    public void setCodigoUsuario(int codigoUsuario) {
        CodigoUsuario = codigoUsuario;
    }

    public String getNomeUsuario() {
        return NomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        NomeUsuario = nomeUsuario;
    }

    public int getCodigoMatch() {
        return CodigoMatch;
    }

    public void setCodigoMatch(int codigoMatch) {
        CodigoMatch = codigoMatch;
    }

    private String[] getCampos() {
        return new String[]{
                "codigo","codigousuario","nomeusuario","codigocardapio", "resposta", "latitude", "longitude", "objetivo", "sexo",
                "distanciamaxima", "idademaxima", "idademinima","codigomatch","cardapio_usuario","cardapio_usuario_resposta"};
    }

    private String[] getValores() {
        return new String[]{
                getCodigo()+"", getCodigoUsuario()+"", getNomeUsuario(), getCodigoCardapio()+"", getResposta(), getLatitude()+"", getLongitude()+"",
                getObjetivo(), getSexo(), getDistancia()+"", getIdadeMaxima()+"", getIdadeMinima()+"",getCodigoMatch()+"",
                getCodigoCardapio()+"_"+getCodigoUsuario(), getCodigoCardapio()+"_"+getCodigoUsuario()+"_"+getResposta()};
    }
}
