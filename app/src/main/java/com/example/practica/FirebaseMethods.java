package com.example.practica;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    public static void signIn(final String email, String password, final Activity activity) {
        Log.d("signIn: ", email);

        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("signIn: ", " Verification : signIn With Email:onComplete:" + task.isSuccessful());
                //  If sign in succeeds i.e if task.isSuccessful(); returns true then the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.


                // If sign in fails, display a message to the user.
                if (!task.isSuccessful()) {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        Log.d("signIn: ", "Invalid Emaild Id");

                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        Log.d("signIn: ", "invalid pass");

                    } catch (FirebaseNetworkException e) {
                        Log.d("signIn: ", "no Network");

                    } catch (Exception e) {
                        Log.d("signIn: ", e.getMessage());
                    }
                    Log.d("signIn: ", String.valueOf(task.getException()));
                    Toast.makeText(activity, R.string.Error, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public static boolean addDispatch(final String empresa,
                                      final String responsable,
                                      final String nombre1,
                                      final String nombre2,
                                      final String rut1,
                                      final String rut2,
                                      final String valor,
                                      final String city,
                                      final Boolean boolMoto,
                                      final Boolean boolCamioneta,
                                      final Boolean boolGestionP,
                                      final Boolean boolGestionE,
                                      final String destinos
                                      ){

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
            map.put("id_creador", id);

            map.put("ciudad", city);
            map.put("fecha", fecha);
            map.put("hora", hora);

            map.put("empresa", empresa);
            map.put("responsable", responsable);
            map.put("nombre1", nombre1);
            map.put("nombre2", nombre2);
            map.put("rut1", rut1);
            map.put("rut2", rut2);
            map.put("valor", valor);
            map.put("boolMoto", boolMoto);
            map.put("boolCamioneta", boolCamioneta);
            map.put("boolGestionP", boolGestionP);
            map.put("boolGestionE", boolGestionE);

            map.put("destinos", destinos);

            mDataBase.child("Despachos").child("UNASIGNED").push().setValue(map);
            taskDone = true;
        }
        return taskDone;
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

    public static void uploadImage(Uri filePath, final Context context) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
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
