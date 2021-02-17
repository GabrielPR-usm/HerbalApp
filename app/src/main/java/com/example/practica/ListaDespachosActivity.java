package com.example.practica;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practica.lista_despachos.DespachosAdapter;
import com.example.practica.lista_despachos.despachoClass;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ListaDespachosActivity extends AppCompatActivity {

    private RecyclerView rvDespachos;
    private GridLayoutManager glm;
    private DespachosAdapter adapter;

    private FloatingActionButton add_despacho; //para agregar despachos
    AlertDialog.Builder builder;

    ArrayList<despachoClass> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_despachos);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        add_despacho = (FloatingActionButton) findViewById(R.id.btnAddDespacho); //agregar despachos

        final AlertDialog alertD = new AlertDialog.Builder(this).create();

        add_despacho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get the layout inflater
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.pop_up, null);

                Button btnDestino = (Button) popupView.findViewById(R.id.btnMapaDestino);
                Button btnOrigen = (Button) popupView.findViewById(R.id.btnMapaOrigen);

                btnOrigen.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         Log.d("TAGCITO", "boton origen");
                         alertD.cancel();
                         startActivity(new Intent(ListaDespachosActivity.this, MapsActivity.class));
                         finish();
                     }
                 });

                btnDestino.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("TAGCITO", "boton destino");
                    }
                });

                alertD.setView(popupView);
                alertD.show();
            }
        });

        rvDespachos = (RecyclerView) findViewById(R.id.rvDespachos);
        adapter = new DespachosAdapter(dataSet());
        glm = new GridLayoutManager(this, 1);
        rvDespachos.setLayoutManager(glm);
        rvDespachos.setAdapter(adapter);

    }

    private ArrayList<despachoClass> dataSet() {
        data = new ArrayList<>();
        data.add(new despachoClass("Despacho 1", "Paine 2345", "Las Condes 2345"));
        data.add(new despachoClass("Despacho 2", "Renca 2345", "Santiago 2345"));
        data.add(new despachoClass("Despacho 3", "Ñuñoa 2345", "San joaquin 2345"));
        data.add(new despachoClass("Despacho 4", "La Reina 2345", "Buin 2345"));
        data.add(new despachoClass("Despacho 1", "Paine 2345", "Las Condes 2345"));
        data.add(new despachoClass("Despacho 2", "Renca 2345", "Santiago 2345"));
        data.add(new despachoClass("Despacho 3", "Ñuñoa 2345", "San joaquin 2345"));
        data.add(new despachoClass("Despacho 4", "La Reina 2345", "Buin 2345"));
        data.add(new despachoClass("Despacho 1", "Paine 2345", "Las Condes 2345"));
        data.add(new despachoClass("Despacho 2", "Renca 2345", "Santiago 2345"));
        data.add(new despachoClass("Despacho 3", "Ñuñoa 2345", "San joaquin 2345"));
        data.add(new despachoClass("Despacho 4", "La Reina 2345", "Buin 2345"));


        return data;
    }
}
