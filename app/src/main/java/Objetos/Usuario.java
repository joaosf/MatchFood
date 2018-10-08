package Objetos;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.joao.matchfood.ActLogin;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;

import Classes.Generic;
import FirebaseDB.FBFunctions;
import FirebaseDB.FBObjeto;

public class Usuario {
    private int Codigo;
    private String Nome;
    private String Login;
    private String Email;
    private String Senha;
    private String DataNascimento;
    private int IdadeMinima = 20;
    private int IdadeMaxima = 50;
    private int LocalMaxima = 50;
    private Double Latitude = 0.0;
    private Double Longitude = 0.0;
    private String Sexo;
    private String Objetivo;

    private Fotos ImagensPerfil;

    private String NomeTabela = "usuarios";

    public Usuario(int codigo) {
        this.Codigo = codigo;
    }

    public FBObjeto toObject() {
        FBObjeto obj = new FBObjeto(NomeTabela);
        obj.setChave(getCodigo()+"");
        obj.setCampos(new String[]{"codigo","nome","login","email","senha","nascimento", "latitude", "longitude", "idade_minima", "idade_maxima", "distancia","sexo","objetivo"});
        obj.setValores(new String[]{getCodigo()+"",
                getNome(),
                getLogin(),
                getEmail(),
                getSenha(),
                getDataNascimento().toString(),
                getLatitude().toString(),
                getLongitude().toString(),
                getIdadeMinima()+"",
                getIdadeMaxima()+"",
                getLocalMaxima()+"",
                getSexo(),
                getObjetivo()});
        return obj;
    }

    public void loadByField(String Campo, String Valor, final String Chave, final ProgressDialog ProgD) {
        Query q = FBFunctions.getQuery(NomeTabela,Campo,Valor);

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    setCodigo(Integer.parseInt(FBFunctions.getStringValue(dataSnapshot, Chave + "/codigo")));
                    setNome(FBFunctions.getStringValue(dataSnapshot, Chave + "/nome"));
                    setLogin(FBFunctions.getStringValue(dataSnapshot, Chave + "/login"));
                    setDataNascimento(FBFunctions.getStringValue(dataSnapshot, Chave + "/nascimento"));
                    setEmail(FBFunctions.getStringValue(dataSnapshot, Chave + "/email"));
                    setSenha(FBFunctions.getStringValue(dataSnapshot, Chave + "/senha"));
                    setLatitude(Double.parseDouble(FBFunctions.getStringValue(dataSnapshot, Chave + "/latitude")));
                    setLongitude(Double.parseDouble(FBFunctions.getStringValue(dataSnapshot, Chave + "/longitude")));
                    setIdadeMinima(Integer.parseInt(FBFunctions.getStringValue(dataSnapshot, Chave + "/idade_minima")));
                    setIdadeMaxima(Integer.parseInt(FBFunctions.getStringValue(dataSnapshot, Chave + "/idade_maxima")));
                    setLocalMaxima(Integer.parseInt(FBFunctions.getStringValue(dataSnapshot, Chave + "/distancia")));
                    setSexo(FBFunctions.getStringValue(dataSnapshot, Chave + "/sexo"));
                    setObjetivo(FBFunctions.getStringValue(dataSnapshot, Chave + "/objetivo"));

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
                    FBObjeto ObjUsuario = toObject();
                    FBFunctions.setValues(ObjUsuario);
                    FBFunctions.getTableReference("contador",false).child(NomeTabela).setValue(Chave+"");
                    Generic.stopLoadingDialog(ProgD);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            FBObjeto ObjUsuario = toObject();
            FBFunctions.setValues(ObjUsuario);
            Generic.stopLoadingDialog(ProgD);
        }
    }

    public int getCodigo() {
        return Codigo;
    }

    public void setCodigo(int codigo) {
        Codigo = codigo;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public String getLogin() {
        return Login;
    }

    public void setLogin(String login) {
        Login = login;
    }

    public String getSenha() {
        return Senha;
    }

    public void setSenha(String senha) {
        Senha = senha;
    }

    public String getDataNascimento() {
        return DataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        DataNascimento = dataNascimento;
    }

    public Fotos getImagens() {
        return ImagensPerfil;
    }

    public void setImagens(Fotos imagensPerfil) {
        ImagensPerfil = imagensPerfil;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "Codigo=" + Codigo +
                ", Nome='" + Nome + '\'' +
                ", Login='" + Login + '\'' +
                ", Email='" + Email + '\'' +
                ", Senha='" + Senha + '\'' +
                ", DataNascimento='" + DataNascimento + '\'' +
                ", IdadeMinima=" + IdadeMinima +
                ", IdadeMaxima=" + IdadeMaxima +
                ", LocalMaxima=" + LocalMaxima +
                ", Latitude=" + Latitude +
                ", Longitude=" + Longitude +
                ", NomeTabela='" + NomeTabela + '\'' +
                '}';
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

    public int getLocalMaxima() {
        return LocalMaxima;
    }

    public void setLocalMaxima(int localMaxima) {
        LocalMaxima = localMaxima;
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

    public String getNomeTabela() {
        return NomeTabela;
    }

    public void getImagensPerfil(ProgressDialog pd, DialogInterface.OnDismissListener EventoFim) {
        if (ImagensPerfil == null) {
            ImagensPerfil = new Fotos(null, Codigo+"", NomeTabela);
        }
        ImagensPerfil.loadFromStorage(pd,EventoFim);
    }

    public String getSexo() {
        return Sexo;
    }

    public void setSexo(String sexo) {
        Sexo = sexo;
    }

    public String getObjetivo() {
        return Objetivo;
    }

    public void setObjetivo(String objetivo) {
        Objetivo = objetivo;
    }
}
