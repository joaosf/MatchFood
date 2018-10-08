package com.joao.matchfood;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import Classes.Generic;
import FirebaseDB.FBFunctions;
import FirebaseDB.FBObjeto;
import LocalBD.BDAdapter;
import LocalBD.BDFunctions;
import Objetos.Fotos;
import Objetos.Usuario;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ActLogin extends AppCompatActivity {
    private static final int REQUEST_PERMISSION = 0;
    private AutoCompleteTextView mLoginView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private CheckBox ckManter;
    String Localizacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_login);
        // Set up the login form.
        mLoginView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);

        Button mEmailSignInButton = (Button) findViewById(R.id.Entrar);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                efetuarLogin();
            }
        });

        Button mNovoUsuario = (Button) findViewById(R.id.CadastrarUsuario);
        mNovoUsuario.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //getLocalizacao();
                abrirCadastroNovoUsuario();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        ckManter = (CheckBox) findViewById(R.id.ckManter);
        requestPermissionGPS();
        //requestPermissionFiles();
        AutoLogin();
    }

    private boolean requestPermissionGPS() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
            Snackbar.make(mLoginView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
                        }
                    });
        } else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
        }
        return false;
    }

    private boolean requestPermissionFiles() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(mLoginView, R.string.permission_file, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
                        }
                    });
        } else {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        }
        return false;
    }

    private void abrirCadastroNovoUsuario() {
        Intent Cadastro = new Intent(this, ActCadastro.class);
        Cadastro.putExtra("CodUsuario", "");
        startActivity(Cadastro);
        finish();
    }

    private void efetuarLogin() {

        final Usuario oUsu = new Usuario(-1);
        final ProgressDialog pd = Generic.showLoadingDialog(ActLogin.this,"Efetuando login...");
        //mLoginView.getText().toString()
        //mPasswordView.getText().toString()

        Query q = FBFunctions.getQuery(oUsu.getNomeTabela(),"login",mLoginView.getText().toString());
        q.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = (Map) dataSnapshot.getValue();
                String Senha = map.get("senha").toString();
                String CodUser = map.get("codigo").toString();
                if (!dataSnapshot.exists()) {
                    mLoginView.setError("Usuário não existe em nossa base.");
                } else
                if (Senha.equals(mPasswordView.getText().toString())) {
                    if (ckManter.isChecked()) {
                        BDAdapter.executarComandoSQL(getApplicationContext(), "delete from UsuLog");
                        BDAdapter.executarComandoSQL(getApplicationContext(), "insert into UsuLog values(" + CodUser + ")");
                    }
                    AbrirTelaInicial(CodUser);
                } else {
                    mPasswordView.setError("Senha incorreta, tente novamente!");
                }
                pd.dismiss();
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

    private void AbrirTelaInicial(String CodUser) {
        Intent TelaInicial = new Intent(ActLogin.this, ActMenu.class);
        TelaInicial.putExtra("CodUsuario",CodUser);
        startActivity(TelaInicial);
        finish();
    }

    private void AutoLogin() {
        Cursor curSelect = BDAdapter.executaConsultaSQL(ActLogin.this,"select CodUsuario from usulog");
        for (boolean hasItem = curSelect.moveToFirst(); hasItem; hasItem = curSelect.moveToNext()) {
            int colunaNome = curSelect.getColumnIndex("CodUsuario");
            String CodUser = curSelect.getString(colunaNome);
            AbrirTelaInicial(CodUser);
        }
    }
}

