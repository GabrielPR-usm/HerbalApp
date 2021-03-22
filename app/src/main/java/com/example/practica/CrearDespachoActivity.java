package com.example.practica;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CrearDespachoActivity extends AppCompatActivity {

    LinearLayout llDestinos;
    EditText etEmpresa, etResponsable, etNombre1, etNombre2, etRut1, etRut2, etValor;
    TextView tvFecha, tvHora;
    CheckBox cbMoto, cbCamioneta, cbGestionP, cbGestionE;
    Button btnAddDestino, btnCrearDespacho, btnCancel;
    Toolbar toolbar;

    private SharedPreferences mPrefs;

    String empresa, responsable, nombre1, nombre2, rut1, rut2, valor;
    Boolean moto = false;
    Boolean camioneta = false;
    Boolean gestionP = false;
    Boolean gestionE = false;

    Location currentLocation;

    String destinos = "";//lat1;lng1<lat2;lng2<lat3;lng3<
    String newDestino = "";

    int nDestino;

    Activity activity;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_crear_despacho);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        Double lat;
        Double lng;

        if (extras != null) {
            lat = extras.getDouble("lat");
            lng = extras.getDouble("lng");

            newDestino = lat + ";" + lng + "<";
            // and get whatever type user account id is
        }

        nDestino = 1;
        activity = this;
        context = this;

        setupUI();
    }

    @Override
    public void onStart() {
        super.onStart();
        restore();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /*
    @Override
    protected void onPause() {
        super.onPause();

        savePreferences(0);
    }

     */

    //Se instancian los componentes de la vista y se le setean los valores a los correspondientes
    private void setupUI(){
        /* INSTANCIO COMPONENTES */
        llDestinos = (LinearLayout) findViewById(R.id.llDestinos);

        etEmpresa = (EditText) findViewById(R.id.etEmpresa);
        etResponsable = (EditText) findViewById(R.id.etResponsable);
        etNombre1 = (EditText) findViewById(R.id.etNombre1);
        etNombre2 = (EditText) findViewById(R.id.etNombre2);
        etRut1 = (EditText) findViewById(R.id.etRut1);
        etRut2 = (EditText) findViewById(R.id.etRut2);
        etValor = (EditText) findViewById(R.id.etValor);

        tvFecha = (TextView) findViewById(R.id.tvFecha);
        tvHora = (TextView) findViewById(R.id.tvHora);

        cbMoto = (CheckBox) findViewById(R.id.cbMoto);
        cbCamioneta = (CheckBox) findViewById(R.id.cbCamioneta);
        cbGestionP = (CheckBox) findViewById(R.id.cbGestionP);
        cbGestionE = (CheckBox) findViewById(R.id.cbGestionE);

        btnAddDestino = (Button) findViewById(R.id.btnAddDestino);
        btnCrearDespacho = (Button) findViewById(R.id.btnCrearDespacho);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        /* SETEO VALORES */
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        Date datetime = new Date();
        String fecha = dateFormat.format(datetime);
        String hora = timeFormat.format(datetime);

        tvFecha.setText(fecha);
        tvHora.setText(hora);

        /* LISTENERS */
        setListeners();
    }

    //Se ceran los listeners y acciones de los botonoes presentes en la vista
    private void setListeners(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final AlertDialog alertD = new AlertDialog.Builder(this).create();

        btnAddDestino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_add_despacho, null);

                TextView tvTitle = (TextView) popupView.findViewById(R.id.tvTitle);
                Button btnUbiActual = (Button) popupView.findViewById(R.id.btnMapaOrigen);
                Button btnUbiMapa = (Button) popupView.findViewById(R.id.btnMapaDestino);
                TextView tvX = popupView.findViewById(R.id.tvX);

                tvTitle.setText("Añadir Destino");
                btnUbiActual.setText("Usar ubicación actual");
                btnUbiMapa.setText("Buscar ubicación en el mapa");

                btnUbiActual.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        currentLocation = getLastKnownLocation();

                        if(currentLocation != null) {

                            String addr = MapsActivity.getAddress(context, currentLocation.getLatitude(), currentLocation.getLongitude(), 1);

                            TextView tv = new TextView(context);

                            String s = "<b>Destino " + nDestino + ": </b>" + addr;
                            tv.setText(Html.fromHtml(s));

                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                            lp.gravity = Gravity.CENTER;
                            tv.setLayoutParams(lp);

                            tv.setTextSize(15);
                            tv.setPadding(5, 6, 5, 6);

                            llDestinos.addView(tv);

                            destinos += currentLocation.getLatitude() + ";" + currentLocation.getLongitude() + "<";
                            nDestino++;

                        }else{
                            Toast.makeText(getApplicationContext(), "No se pudo obtener su ubicacion", Toast.LENGTH_SHORT).show();
                            MapsActivity.enableUbication(activity);
                        }

                        alertD.cancel();
                    }
                });

                btnUbiMapa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getValues();
                        savePreferences(0);
                        startActivity(new Intent(CrearDespachoActivity.this, MapsActivity.class));
                        alertD.cancel();
                    }
                });

                tvX.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertD.cancel();
                    }
                });

                alertD.setView(popupView);
                alertD.show();

            }
        });

        btnCrearDespacho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getValues();

                if(empresa.isEmpty() || nombre1.isEmpty() || rut1.isEmpty() || valor.isEmpty()) {

                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                    spannableStringBuilder.clear();

                    if (empresa.isEmpty()) {
                        etEmpresa.setError(spannableStringBuilder);
                    }

                    if (nombre1.isEmpty()) {
                        etNombre1.setError(spannableStringBuilder);
                    }

                    if (rut1.isEmpty()) {
                        etRut1.setError(spannableStringBuilder);
                    }

                    if (valor.isEmpty()) {
                        etValor.setError(spannableStringBuilder);
                    }
                    Toast.makeText(getApplicationContext(), "Porfavor complete los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                if( !(moto || camioneta || gestionP || gestionE) ){

                    Toast.makeText(getApplicationContext(), "Debe seleccionar alguna de las GESTIONES", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(destinos == ""){
                    Toast.makeText(getApplicationContext(), "Debe agregar por lo menos un DESTINOS", Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] destinosArr = destinos.split("<");
                Double lat = 0.0;
                Double lng = 0.0;
                String city = "";

                for(String destino : destinosArr){
                    String[] coord = destino.split(";");

                    lat = Double.valueOf(coord[0]);
                    lng = Double.valueOf(coord[1]);
                }

                city = MapsActivity.getAddress(context, lat, lng, 2);

                Boolean done = FirebaseMethods.addDispatch(empresa,
                responsable,
                nombre1,
                nombre2,
                rut1,
                rut2,
                valor,
                city,
                moto,
                camioneta,
                gestionP,
                gestionE,
                destinos);

                if(done){
                    Toast.makeText(getApplicationContext(), "Se ha agregado despacho a la BD", Toast.LENGTH_LONG).show();

                    savePreferences(1);
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(), "Inténtelo nuevamente", Toast.LENGTH_LONG).show();

                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                savePreferences(1);
                finish();

            }
        });

    }

    //Se obtiene la ubicacion actual
    private Location getLastKnownLocation() {
        Location l=null;
        LocationManager mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
                l = mLocationManager.getLastKnownLocation(provider);
            }
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    //Se guardan localmente los valores inputeados en la vista
    private void getValues(){
        empresa = etEmpresa.getText().toString();
        responsable = etResponsable.getText().toString();
        nombre1 = etNombre1.getText().toString();
        nombre2 = etNombre2.getText().toString();
        rut1 = etRut1.getText().toString();
        rut2 = etRut2.getText().toString();
        valor = etValor.getText().toString();

        moto = cbMoto.isChecked();
        camioneta = cbCamioneta.isChecked();
        gestionP = cbGestionP.isChecked();
        gestionE = cbGestionE.isChecked();
    }

    //En el caso de haber habido un cambio de vista, se obtienen los valores guardados previamente en Sharedpreferences. Ademas se restauran las vistas con estos valores
    public void restore() {

        nDestino = 1;

        mPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);

        empresa = mPrefs.getString("empresa", "");
        responsable = mPrefs.getString("responsable", "");
        nombre1 = mPrefs.getString("nombre1", "");
        nombre2 = mPrefs.getString("nombre2", "");
        rut1 = mPrefs.getString("rut1", "");
        rut2 = mPrefs.getString("rut2", "");
        valor = mPrefs.getString("valor", "");

        moto = mPrefs.getBoolean("moto", false);
        camioneta = mPrefs.getBoolean("camioneta", false);
        gestionP = mPrefs.getBoolean("gestionP", false);
        gestionE = mPrefs.getBoolean("gestionE", false);

        destinos = mPrefs.getString("destinos", "");

        Log.d("onRestore", " datos leidos");

        //setupUI();

        //SETEO VALORES
        etEmpresa.setText(empresa);
        etResponsable.setText(responsable);
        etNombre1.setText(nombre1);
        etNombre2.setText(nombre2);
        etRut1.setText(rut1);
        etRut2.setText(rut2);
        etValor.setText(valor);

        cbMoto.setChecked(moto);
        cbCamioneta.setChecked(camioneta);
        cbGestionP.setChecked(gestionP);
        cbGestionE.setChecked(gestionE);

        Log.d("destinos: antes ", destinos);

        destinos += newDestino;

        Log.d("destinos: dsps ", destinos);

        if(destinos != ""){
            String[] destinosArray = destinos.split("<");

            for(String dest : destinosArray){
                String[] latLng = dest.split(";");
                Double lat = Double.valueOf(latLng[0]);
                Double lng = Double.valueOf(latLng[1]);

                String addr = MapsActivity.getAddress(this, lat, lng, 1);

                TextView tv = new TextView(context);

                String s = "<b>Destino " + nDestino + ": </b>" + addr;
                tv.setText(Html.fromHtml(s));

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                lp.gravity = Gravity.CENTER;
                tv.setLayoutParams(lp);

                tv.setTextSize(15);
                tv.setPadding(5, 6, 5, 6);

                llDestinos.addView(tv);

                nDestino++;
            }
        }
    }

    //Se guardan los valores inputeados en Sharedpreferences
    private void savePreferences(int flag){//0-save, 1-delete
        mPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor ed = mPrefs.edit();

        if(flag == 0){

            ed.putString("empresa", empresa);
            ed.putString("responsable", responsable);
            ed.putString("nombre1", nombre1);
            ed.putString("nombre2", nombre2);
            ed.putString("rut1", rut1);
            ed.putString("rut2", rut2);
            ed.putString("valor", valor);

            ed.putBoolean("moto", moto);
            ed.putBoolean("camioneta", camioneta);
            ed.putBoolean("gestionP", gestionP);
            ed.putBoolean("gestionE", gestionE);

            ed.putString("destinos", destinos);

            ed.commit();


        }else if(flag == 1){

            ed.putString("empresa", "");
            ed.putString("responsable", "");
            ed.putString("nombre1", "");
            ed.putString("nombre2", "");
            ed.putString("rut1", "");
            ed.putString("rut2", "");
            ed.putString("valor", "");

            ed.putBoolean("moto", false);
            ed.putBoolean("camioneta", false);
            ed.putBoolean("gestionP", false);
            ed.putBoolean("gestionE", false);

            ed.putString("destinos", "");

            ed.commit();

        }
    }
}
