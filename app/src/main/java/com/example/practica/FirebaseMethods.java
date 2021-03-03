package com.example.practica;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.practica.lista_despachos.despacho;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirebaseMethods {

    static FirebaseAuth mAuth;
    static FirebaseAuth.AuthStateListener mAuthStateListener;
    static DatabaseReference mDataBase;

    public static boolean registerUser(final String email, final String pass, final String name){

        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance().getReference();
        final boolean[] taskDone = {false};

        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull final Task<AuthResult> task) { //tarea es crear usuario
                if (task.isSuccessful()){ //almacenar datos en la base de datos

                    Map<String, Object> map = new HashMap<>();
                    map.put("name", name);
                    map.put("email", email);
                    map.put("password", pass);

                    String id = mAuth.getCurrentUser().getUid(); //obtener id del usuario nuevo

                    mDataBase.child("Usuarios").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if(task2.isSuccessful()){ //tarea ahora es crear datos en la bd
                                taskDone[0] = true;
                                //Toast.makeText(ActivityRegister.this, "Has entrado como paciente", Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(ActivityRegister.this, MainActivity2.class));
                                //finish();
                            }
                            else{
                                Log.d("REGISTER", "TASK2 ELSE");
                                //System.out.println("salleee");
                                //Toast.makeText(ActivityRegister.this, "No se pudieron crear los datos", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Log.d("REGISTER", String.valueOf(task.getResult()));
                    //Toast.makeText((), "No se pudo registrar este usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return taskDone[0];
    }

    public static boolean addDispatch(final LatLng ubicacion, final String descripcionCarga, final String address, final String city){

        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance().getReference();
        boolean taskDone = false;

        if(mAuth.getCurrentUser() != null){

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            Date datetime = new Date();
            String fecha = dateFormat.format(datetime);
            String hora = timeFormat.format(datetime);

            String id = mAuth.getCurrentUser().getUid();

            Map<String, Object> map = new HashMap<>();
            map.put("descripcion", descripcionCarga);
            map.put("address", address);
            map.put("ciudad", city);
            map.put("fecha", fecha);
            map.put("hora", hora);
            map.put("latitude", ubicacion.latitude);
            map.put("longitude", ubicacion.longitude);

            mDataBase.child("Despachos").child("Pendientes").child(id).push().setValue(map);
            taskDone = true;
        }
        return taskDone;
    }

    public static String getUserId(){
        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance().getReference();
        String id = mAuth.getCurrentUser().getUid();
        return id;
    }

    public static void updateDespacho(String despachoId){

        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance().getReference();

        String id = mAuth.getCurrentUser().getUid();

        DatabaseReference from = mDataBase.child("Despachos").child("Pendientes").child(id).child(despachoId);
        DatabaseReference to = mDataBase.child("Despachos").child("Completados").child(id).child(despachoId);

        moveFirebaseRecord(from, to);
    }

    public static void moveFirebaseRecord(final DatabaseReference fromPath, final DatabaseReference toPath)
    {
        fromPath.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener()
                {
                    @Override
                    public void onComplete(DatabaseError error, DatabaseReference firebase)
                    {
                        if (error != null)
                        {
                            System.out.println("Copy failed");
                        }
                        else
                        {
                            fromPath.removeValue();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Copy failed");
            }
        });
    }

    /*
    public static ArrayList<despacho> getUbicaciones(String id){
        Log.d("loading data", "getUbicaciones");
        final ArrayList<despacho> data = new ArrayList<>();
        final int[] i = {1};

        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance().getReference();

        mDataBase.child("Despachos").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    for (DataSnapshot ds1: ds.getChildren()){

                        String address = ds1.child("address").getValue().toString();

                        data.add(new despachoClass("Despacho " + i[0], address, ""));

                        Log.d("loading data", "despacho " + i[0]);

                        i[0]++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAGCITO", "ERROR");
            }
        });

        return data;
    }

     */

}
