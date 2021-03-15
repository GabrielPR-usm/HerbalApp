package com.example.practica;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    //layout_login
    private EditText etEmailLogin, etPassLogin; //datos que se pide

    private Button btnIngresar; //para logearse
    private TextView tvRegister; //para ir a pantalla registrarse

    //datos que se llenan
    private String email = "";
    private String pass = "";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_login);

        etEmailLogin = (EditText) findViewById(R.id.editTextEmail);
        etPassLogin = (EditText) findViewById(R.id.editTextPassword);
        btnIngresar = (Button) findViewById(R.id.cirLoginButton);
        tvRegister = (TextView) findViewById(R.id.tvRegister);

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent registerActivityIntent = new Intent(v.getContext(), RegisterActivity.class);
                startActivity(registerActivityIntent);
            }
        });

        btnIngresar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                email = etEmailLogin.getText().toString();
                pass = etPassLogin.getText().toString();

                if (!email.isEmpty() && !pass.isEmpty()){
                    FirebaseMethods.signIn(email, pass, LoginActivity.this);
                    startActivity(new Intent(LoginActivity.this, ListaDespachosActivity.class));
                    finish(); //para prohibir que se pueda volver a esa vista
                }
                else{
                    Toast.makeText(LoginActivity.this, "Debes rellenar todos los campos", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Login: ", "start");
        MapsActivity.checkLocationPermission(this, this);
        MapsActivity.enableUbication(this);
        if (mAuth.getCurrentUser() != null){ //con esto se puede cerrar la app y aun asi la sesion sigue iniciada
            startActivity(new Intent(LoginActivity.this, ListaDespachosActivity.class));
            finish(); //para prohibir que se pueda volver a esa vista
        }
    }
}
