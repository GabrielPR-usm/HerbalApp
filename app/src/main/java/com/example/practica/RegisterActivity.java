package com.example.practica;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPass;
    private Button btnRegister;
    private TextView tvIngresar;

    private String name = "";
    private String email = "";
    private String pass = "";

    FirebaseAuth mAuth;
    DatabaseReference mDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance().getReference();

        etName = (EditText) findViewById(R.id.editTextName);
        etEmail = (EditText) findViewById(R.id.editTextEmail);
        etPass = (EditText) findViewById(R.id.editTextPassword);

        btnRegister = (Button) findViewById(R.id.cirLoginButton);
        tvIngresar = (TextView) findViewById(R.id.tvIngresar);

        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                name = etName.getText().toString();
                email = etEmail.getText().toString();
                pass = etPass.getText().toString();

                if (!name.isEmpty() && !email.isEmpty() && !pass.isEmpty()){

                    if(pass.length() >= 6) {

                        Boolean flag = FirebaseMethods.registerUser(email, pass, name);

                        if(flag){
                            startActivity(new Intent(RegisterActivity.this, ListaDespachosActivity.class));
                            finish();
                        }
                    }
                    else{
                        Toast.makeText(RegisterActivity.this, "El password debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(RegisterActivity.this, "Debes completar todos los datos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent loginActivityIntent = new Intent(v.getContext(), LoginActivity.class);
                startActivity(loginActivityIntent);
            }
        });

    }

    /*@Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }/

    /*@Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null){
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }*/


}