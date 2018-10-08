package com.joao.matchfood;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

import Classes.Generic;
import Classes.MatchFunctions;
import FirebaseDB.FBFunctions;
import Objetos.Usuario;

public class ActCadastro extends AppCompatActivity {

    ProgressDialog pd;
    Usuario oUsu;
    NumberPicker npMinimo;
    NumberPicker npMaximo;
    FloatingActionButton btnSalvar;
    TextView tvValueDistancia;

    EditText edNome;
    EditText edLogin;
    EditText edEmail;
    EditText edSenha;
    EditText edConfirmarSenha;
    EditText edDataNascimento;
    SeekBar sbDistancia;
    Spinner cbSexo;
    Spinner cbGenero;
    Boolean isValidLogin = false;
    int sbDistanciaValue = 50;
    String CodUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_cadastro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PrepararSeekBar();
        PrepararNumberPicker();
        PrepararCamposTexto();

        btnSalvar = (FloatingActionButton) findViewById(R.id.btnSalvar);
        btnSalvar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorDefaultBtn)));

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvarCadastro();
            }
        });

        Bundle extras = getIntent().getExtras();
        CodUsuario = extras.getString("CodUsuario");
        if (!CodUsuario.equals("")) {
            ProgressDialog pd = Generic.showLoadingDialog(this,"Carregando dados...");
            oUsu = new Usuario(Integer.parseInt(CodUsuario));
            oUsu.loadByField("codigo",CodUsuario,CodUsuario,pd);
            pd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    edNome.setText(oUsu.getNome());
                    edLogin.setText(oUsu.getLogin());
                    edEmail.setText(oUsu.getEmail());
                    edDataNascimento.setText(oUsu.getDataNascimento());
                    sbDistanciaValue = oUsu.getLocalMaxima();
                    sbDistancia.setProgress(sbDistanciaValue);
                    npMinimo.setValue(oUsu.getIdadeMinima());
                    npMaximo.setValue(oUsu.getIdadeMaxima());
                    edSenha.setText(oUsu.getSenha());
                    edConfirmarSenha.setText(oUsu.getSenha());
                    cbSexo.setSelection(Generic.getPosOnList(oUsu.getSexo(), MatchFunctions.getOptionsSexo()));
                    cbGenero.setSelection(Generic.getPosOnList(oUsu.getObjetivo(), MatchFunctions.getOptionsObjetivo()));
                }
            });
        } else {
            oUsu = new Usuario(-1);
            getLocalizacao();
        }
    }

    @Override
    public void onBackPressed() {
        Intent Login = new Intent(this, ActLogin.class);
        startActivity(Login);
        finish();
        return;
    }

    private void isValidLogin() {
        Query q = FBFunctions.getQuery(oUsu.getNomeTabela(),"login",edLogin.getText().toString());

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                isValidLogin = true;
                if (oUsu.getCodigo() == -1) {
                    if (dataSnapshot.exists()) {
                        isValidLogin = false;
                        edLogin.setError("Login "+edLogin.getText().toString()+" já existe, tente outro!");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void PrepararCamposTexto() {
        cbSexo = (Spinner) findViewById(R.id.cbSexo);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, MatchFunctions.getOptionsSexo());
        cbSexo.setAdapter(adapter);

        cbGenero = (Spinner) findViewById(R.id.cbGenero);
        ArrayAdapter<String> ad = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, MatchFunctions.getOptionsObjetivo());
        cbGenero.setAdapter(ad);
        edNome = (EditText) findViewById(R.id.edNome);
        edLogin = (EditText) findViewById(R.id.edLogin);
        edLogin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    isValidLogin();
                }
            }
        });
        edEmail = (EditText) findViewById(R.id.edEmail);
        edSenha = (EditText) findViewById(R.id.edSenha);
        edConfirmarSenha = (EditText) findViewById(R.id.edConfirmaSenha);
        edDataNascimento = (EditText) findViewById(R.id.edDataNascimento);
        edDataNascimento.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    edDataNascimento.setFocusable(false);
                    edDataNascimento.setFocusableInTouchMode(true);
                    showDialog(0);
                }
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 0) {
            final Calendar c = Calendar.getInstance();

            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            String[] DataNascimento = edDataNascimento.getText().toString().split("/");
            if (DataNascimento.length == 3) {
                year = Integer.parseInt(DataNascimento[2]);
                month = Integer.parseInt(DataNascimento[1])-1;
                day = Integer.parseInt(DataNascimento[0]);
            }

            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int year, int month, int day) {
            month += 1;
            edDataNascimento.setText(day+"/"+month+"/"+year);
        }
    };

    private void PrepararNumberPicker(){
        npMinimo = (NumberPicker) findViewById(R.id.npMinimo);
        npMinimo.setMinValue(16);
        npMinimo.setMaxValue(80);
        npMinimo.setValue(20);

        npMaximo = (NumberPicker) findViewById(R.id.npMaximo);
        npMaximo.setMinValue(16);
        npMaximo.setMaxValue(80);
        npMaximo.setValue(40);
    }

    private boolean isValidSpinner() {
        if (cbSexo.getSelectedItem().toString().equals(MatchFunctions.getOptionsSexo()[0])) {
            Generic.showErrorSpinner(cbSexo);
            return false;
        } else
        if (cbGenero.getSelectedItem().toString().equals(MatchFunctions.getOptionsObjetivo()[0])) {
            Generic.showLoadingDialog(ActCadastro.this,cbGenero.getSelectedItem().toString());//showErrorSpinner(cbGenero);
            return false;
        }
        return true;
    }

    private void PrepararSeekBar(){
        sbDistancia = (SeekBar) findViewById(R.id.sbDistancia);
        tvValueDistancia = (TextView) findViewById(R.id.tvValueDistancia);

        sbDistancia.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                tvValueDistancia.setText(progress+"km");
                tvValueDistancia.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2);
                sbDistanciaValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbDistancia.setProgress(sbDistancia.getProgress());
    }

    private void salvarCadastro() {
        if (!isValidSpinner()) {
            return;
        }
        if (oUsu.getCodigo() == -1) {
            if (!isValidLogin) {
                edLogin.setError("Login "+edLogin.getText().toString()+" já existe, tente outro!");
            }
        }
        if (    (!Generic.isValidText(edNome,"Prrencha seu nome.",0,"")) ||
                (!Generic.isValidText(edLogin,"Prrencha seu login.",0,"")) ||
                (!Generic.isValidText(edEmail,"Prrencha seu email.",0,"")) ||
                (!Generic.isValidText(edSenha,"Prrencha sua senha.",6,"Sua senha deve ter no mínimo 6 caracteres.")) ||
                (!Generic.isValidText(edDataNascimento,"Prrencha sua data de nascimento.",0,""))) {
            return;
        }
        if (!edSenha.getText().toString().equals(edConfirmarSenha.getText().toString())) {
            edConfirmarSenha.setError("Senha diferente de confirmação, favor digite novamente");
            return;
        }

        oUsu.setNome(edNome.getText().toString());
        oUsu.setLogin(edLogin.getText().toString());
        oUsu.setEmail(edEmail.getText().toString());
        oUsu.setSenha(edSenha.getText().toString());
        oUsu.setDataNascimento(edDataNascimento.getText().toString());
        oUsu.setIdadeMinima(npMinimo.getValue());
        oUsu.setIdadeMaxima(npMaximo.getValue());
        oUsu.setLocalMaxima(sbDistanciaValue);
        oUsu.setSexo(Generic.getSpinnerText(cbSexo));
        oUsu.setObjetivo(Generic.getSpinnerText(cbGenero));

        ProgressDialog ProgD = Generic.showLoadingDialog(ActCadastro.this, "Salvando dados...");
        ProgD.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                saveSucess();
            }
        });
        oUsu.sincronizarFirebase(ProgD);
    }

    private void saveSucess() {
        Intent Fotos = new Intent(this, ActFotos.class);
        Fotos.putExtra("CodUsuario", oUsu.getCodigo()+"");
        startActivity(Fotos);
        onPause();
    }

    private void getLocalizacao() {
        final ProgressDialog pd = Generic.showLoadingDialog(this,"Verificando localização...");
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location loc) {
                oUsu.setLatitude(loc.getLongitude());
                oUsu.setLongitude(loc.getLatitude());
                pd.dismiss();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
    }
}
