package Objetos;

import android.content.Context;
import android.database.Cursor;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import Classes.MatchFunctions;
import FirebaseDB.FBFunctions;
import FirebaseDB.FBObjeto;

public class Match {
    private int Codigo;
    private int CodigoUsuario1;
    private int CodigoUsuario2;
    private String NomeUsuario1;
    private String NomeUsuario2;
    private int CodigoCardapio;

    private Context ctx;
    String NomeTabela = "match";

    public Match(Context context, final String UsuarioLogado) {
        ctx = context;
        Thread td = new Thread(new Runnable() {
            @Override
            public void run() {
                Query query = FBFunctions.getTableReference("curtidas",true);//getQuery("curtidas","codigocardapio","");
                query.keepSynced(true);
                query.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        Map map = (Map) dataSnapshot.getValue();
                        String Codigo = map.get("codigo").toString();
                        final int CodigoUsuario = Integer.parseInt(map.get("codigousuario").toString());
                        final String NomeUsuario = map.get("nomeusuario").toString();
                        final int CodigoCardapio = Integer.parseInt(map.get("codigocardapio").toString());
                        final String Resposta = map.get("resposta").toString();
                        final Double Latitude = Double.parseDouble(map.get("latitude").toString());
                        final Double Longitude = Double.parseDouble(map.get("longitude").toString());
                        int IdadeMinima = Integer.parseInt(map.get("idademaxima").toString());
                        int IdadeMaxima = Integer.parseInt(map.get("idademinima").toString());
                        final int Distancia = Integer.parseInt(map.get("distanciamaxima").toString());
                        final String Objetivo = map.get("objetivo").toString();
                        final String Sexo = map.get("sexo").toString();
                        final int CodigoMatch = Integer.parseInt(map.get("codigomatch").toString());

                        if (CodigoUsuario == Integer.parseInt(UsuarioLogado)) {
                            return;
                        }
                        Query isMatch = FBFunctions.getQuery("match","codigos_usuarios",CodigoUsuario+"_"+UsuarioLogado);
                        if (CodigoUsuario > Integer.parseInt(UsuarioLogado)) {
                            isMatch = FBFunctions.getQuery("match","codigos_usuarios",UsuarioLogado+"_"+CodigoUsuario);
                        }
                        isMatch.keepSynced(true);
                        isMatch.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() == null) {
                                    Query qry =  FBFunctions.getQuery("curtidas","cardapio_usuario_resposta",CodigoCardapio+"_"+
                                            UsuarioLogado+"_S");
                                    qry.keepSynced(true);
                                    qry.addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            Map map = (Map) dataSnapshot.getValue();
                                            String mySexo = map.get("sexo").toString();
                                            String myObjetivo = map.get("objetivo").toString();
                                            int myDistancia = Integer.parseInt(map.get("distanciamaxima").toString());
                                            Double myLatitude = Double.parseDouble(map.get("latitude").toString());
                                            Double myLongitude = Double.parseDouble(map.get("longitude").toString());
                                            final int myCodigoUsuario = Integer.parseInt(map.get("codigousuario").toString());
                                            final int myCodigoCardapio = Integer.parseInt(map.get("codigocardapio").toString());
                                            final String myNomeUsuario = map.get("nomeusuario").toString();

                                            if (!MatchFunctions.inSexoRange(Sexo,
                                                    Objetivo,
                                                    mySexo,
                                                    myObjetivo)) {
                                                return;
                                            } else
                                            if (!MatchFunctions.inLocationRange( Latitude,
                                                    Longitude,
                                                    myLatitude,
                                                    myLongitude,
                                                    Distancia,
                                                    myDistancia)) {
                                                return;
                                            } else
                                            if (!Resposta.equals("S")) {
                                                return;
                                            }

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
                                                    FBFunctions.getTableReference("contador",false).child(NomeTabela).setValue(Chave+"");

                                                    if (CodigoUsuario < myCodigoUsuario) {
                                                        setCodigoUsuario1(CodigoUsuario);
                                                        setCodigoUsuario2(myCodigoUsuario);
                                                        setNomeUsuario1(NomeUsuario);
                                                        setNomeUsuario2(myNomeUsuario);
                                                    } else {
                                                        setCodigoUsuario1(myCodigoUsuario);
                                                        setCodigoUsuario2(CodigoUsuario);
                                                        setNomeUsuario1(myNomeUsuario);
                                                        setNomeUsuario2(NomeUsuario);
                                                    }

                                                    setCodigoCardapio(CodigoCardapio);

                                                    FBObjeto Obj = toObject();
                                                    FBFunctions.setValues(Obj);

                                                    deletarCurtidaFirebase(myCodigoCardapio+"_"+myCodigoUsuario);
                                                    deletarCurtidaFirebase(CodigoCardapio+"_"+CodigoUsuario);
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
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
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

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
        td.start();
    }

    public Match(int Codigo) {
        setCodigo(Codigo);
        load("codigo",getCodigo()+"");
    }

    public void load(String campo, String valor) {
        Query query = FBFunctions.getQuery("match",campo,valor);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = (Map) dataSnapshot.getValue();
                setCodigo(Integer.parseInt(map.get("codigo").toString()));
                setCodigoCardapio(Integer.parseInt(map.get("codigocardapio").toString()));
                setCodigoUsuario1(Integer.parseInt(map.get("codigousuario1").toString()));
                setCodigoUsuario2(Integer.parseInt(map.get("codigousuario2").toString()));
                setNomeUsuario1(map.get("nomeusuario1").toString());
                setNomeUsuario2(map.get("nomeusuario2").toString());
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

    private void deletarCurtidaFirebase(final String Chave) {
        System.out.println("HERE = "+Chave);
        Query qry =  FBFunctions.getQuery("curtidas","cardapio_usuario",Chave);
        qry.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = (Map) dataSnapshot.getValue();
                if (map.get("codigomatch").toString().equals("-1")) {
                    //FBFunctions.getTableReference("curtidas",false).child(dataSnapshot.getKey()+"/codigomatch").setValue(getCodigo());
                    FBFunctions.getTableReference("curtidas",false).child(dataSnapshot.getKey()).setValue(null);
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

    public int getCodigo() {
        return Codigo;
    }

    public void setCodigo(int codigo) {
        Codigo = codigo;
    }

    public int getCodigoUsuario1() {
        return CodigoUsuario1;
    }

    public void setCodigoUsuario1(int codigoUsuario1) {
        CodigoUsuario1 = codigoUsuario1;
    }

    public int getCodigoUsuario2() {
        return CodigoUsuario2;
    }

    public void setCodigoUsuario2(int codigoUsuario2) {
        CodigoUsuario2 = codigoUsuario2;
    }

    public String getNomeUsuario1() {
        return NomeUsuario1;
    }

    public void setNomeUsuario1(String nomeUsuario1) {
        NomeUsuario1 = nomeUsuario1;
    }

    public String getNomeUsuario2() {
        return NomeUsuario2;
    }

    public void setNomeUsuario2(String nomeUsuario2) {
        NomeUsuario2 = nomeUsuario2;
    }

    public int getCodigoCardapio() {
        return CodigoCardapio;
    }

    public void setCodigoCardapio(int codigoCardapio) {
        CodigoCardapio = codigoCardapio;
    }

    private FBObjeto toObject() {
        FBObjeto obj = new FBObjeto(NomeTabela);
        obj.setChave(getCodigoUsuario1()+"_"+getCodigoUsuario2());
        obj.setCampos(getCampos());
        obj.setValores(getValores());
        return obj;
    }

    private String[] getCampos() {
        return new String[]{
                "codigo","codigousuario1","codigousuario2","nomeusuario1", "nomeusuario2", "codigos_usuarios",
                "codigocardapio","notificadousuario1","notificadousuario2"};
    }

    private String[] getValores() {
        return new String[]{
                getCodigo()+"", getCodigoUsuario1()+"",getCodigoUsuario2()+"", getNomeUsuario1(),getNomeUsuario2(),
                getCodigoUsuario1()+"_"+getCodigoUsuario2(), getCodigoCardapio()+"", "false", "false"};
    }
}
