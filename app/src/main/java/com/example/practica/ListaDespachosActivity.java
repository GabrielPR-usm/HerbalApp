package com.example.practica;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practica.lista_despachos.FirebaseDespachoViewHolder;
import com.example.practica.lista_despachos.despacho;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class ListaDespachosActivity extends AppCompatActivity {

    private RecyclerView rvDespachos;

    private FloatingActionButton add_despacho; //para agregar despachos
    private FirebaseAuth mAuth;

    ArrayList<despacho> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_despachos);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        add_despacho = (FloatingActionButton) findViewById(R.id.btnAddDespacho); //agregar despachos

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Despachos").child(FirebaseMethods.getUserId());
        Query query = databaseReference;

        if (user == null){
            boolean flag = FirebaseMethods.registerUser("gabopezoa97@gmail.com", "12345a", "Gabriel Pezoa");
        }

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

        setUpFirebaseAdapter(query);
        Log.d("after setup", "data: " + String.valueOf(data));

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    private void setUpFirebaseAdapter(Query query) {

        FirebaseRecyclerAdapter mFirebaseAdapter = new FirebaseRecyclerAdapter<despacho, FirebaseDespachoViewHolder>
                (despacho.class, R.layout.content_despachos_fragment, FirebaseDespachoViewHolder.class, query) {
            @Override
            protected void populateViewHolder(FirebaseDespachoViewHolder firebaseDespachoViewHolder, despacho despacho, int i) {
                data.add(despacho);
                Log.d("populateView", "data: " + String.valueOf(data));
                Log.d("populateView", "i: " + i);
                firebaseDespachoViewHolder.bindUser(despacho, i+1);
            }
        };

        rvDespachos.setHasFixedSize(true);
        rvDespachos.setLayoutManager(new LinearLayoutManager(this));
        rvDespachos.setAdapter(mFirebaseAdapter);

    }
}
