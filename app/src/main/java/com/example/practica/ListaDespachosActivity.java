package com.example.practica;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.practica.lista_despachos.FirebaseDespachoViewHolder;
import com.example.practica.lista_despachos.classDespacho;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ListaDespachosActivity extends AppCompatActivity {

    Location currentLocation;

    Query query;

    private RecyclerView rvDespachos;
    FirebaseRecyclerAdapter mFirebaseAdapter;

    private FloatingActionButton add_despacho; //para agregar despachos
    private FloatingActionButton startDriving;
    private Button createDespacho;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;

    ArrayList<classDespacho> data = new ArrayList<>();

    Context mContext;
    static Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_despachos);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // [START auth_state_listener] ,this method execute as soon as there is a change in Auth status , such as user sign in or sign out.
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    //redirect
                    Log.d("user ", user.getUid());
                    query = FirebaseDatabase.getInstance().getReference()
                            .child("Despachos")
                            .child("UNASIGNED")
                            .orderByChild("ciudad");
                    setUpFirebaseAdapter(query);
                    Log.d("adapter ", "set");

                } else {
                    // User is signed out
                    Log.d("onCreate", "onAuthStateChanged:signed_out");
                    setUpFirebaseAdapter(null);
                }

            }
        };

        setupUI();

        currentLocation = getLastKnownLocation(this);

        mContext = this;
        mActivity = this;

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(mAuthListener);
        //mFirebaseAdapter.startListening();
        user = mAuth.getCurrentUser();

    }

    @Override
    protected void onResume() {
        super.onResume();

        Location current = getLastKnownLocation( this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        mFirebaseAdapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                mAuth.signOut();
                startActivity(new Intent(ListaDespachosActivity.this, LoginActivity.class));
                //finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Se instancian los elementos de la vista y sus controladores
    private void setupUI(){

        rvDespachos = (RecyclerView) findViewById(R.id.rvDespachos);
        createDespacho = (Button) findViewById(R.id.btnCrearDespacho);

        add_despacho = (FloatingActionButton) findViewById(R.id.btnAddDespacho);
        startDriving = (FloatingActionButton) findViewById(R.id.btnStartDespacho);
        add_despacho.hide();
        startDriving.hide();

        createDespacho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(ListaDespachosActivity.this, CrearDespachoActivity.class));
            }
        });
    }

    //Adapter para poblar la vista de recyclerview. Muestra los datos pedidos en la query
    private void setUpFirebaseAdapter(Query query) {

        if(query == null){

            return;

        }

        Log.d("adapter", "begining");

        FirebaseRecyclerOptions<classDespacho> options =
                new FirebaseRecyclerOptions.Builder<classDespacho>()
                        .setQuery(query, classDespacho.class)
                        .build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<classDespacho, FirebaseDespachoViewHolder> (options) {

            @NonNull
            @Override
            public FirebaseDespachoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.content_despachos_fragment, parent, false);

                Log.d("adapter", "onCreateView");

                return new FirebaseDespachoViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FirebaseDespachoViewHolder firebaseDespachoViewHolder, int i, @NonNull classDespacho despacho) {
                String key = getRef(i).getKey();
                despacho.setId(key);
                data.add(despacho);
                firebaseDespachoViewHolder.bindUser(despacho, i + 1);
            }
        };

        rvDespachos.setHasFixedSize(true);
        rvDespachos.setLayoutManager(new LinearLayoutManager(this));
        rvDespachos.setAdapter(mFirebaseAdapter);
        mFirebaseAdapter.startListening();

        Log.d("adapter", "before helper");

        //Helper para poder hacer draggin de los items del recyclerview
        createHelper();
    }

    //Funcion para poder draggear los elementos de la vista
    private void createHelper() {
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                Log.d("helper", "onMove");
                int position_dragged = viewHolder.getAdapterPosition();
                int position_target = target.getAdapterPosition();

                Collections.swap(data, position_dragged, position_target);

                mFirebaseAdapter.notifyItemMoved(position_dragged, position_target);

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });

        helper.attachToRecyclerView(rvDespachos);
    }

    //En base a los destinos, genera una URL para poder llamar a Google Directions con el recorrido
    public static String getDirectionsUrl(String destinos, Context context) {

        String navUrl = null;

        // Origin of route
        Location origin = getLastKnownLocation(context);

        if(origin != null) {
            String str_origin = "origin=" + origin.getLatitude() + "," + origin.getLongitude();

            // Destination of route
            String str_dest = "";
            String wp = "";

            String[] destinosArray = destinos.split("<");
            int nDestinos = destinosArray.length;
            int i = 1;
            int j = 0;

            for (String dest : destinosArray) {
                String[] latLng = dest.split(";");
                Double lat = Double.valueOf(latLng[0]);
                Double lng = Double.valueOf(latLng[1]);

                if (i == nDestinos - 1) {
                    //se agrega destino final
                    str_dest = "destination=" + lat + "," + lng;
                } else if (i < nDestinos - 1) {
                    //se agregan waypoints
                    if (j == 0) {
                        wp = "waypoints=";
                    }
                    if (j == nDestinos - 1) {
                        wp += lat.toString() + "," + lng.toString();
                    } else {
                        wp += lat.toString() + "," + lng.toString() + "%7C";
                    }
                    j++;
                }

                i++;
            }

            // Para lanzar navigation
            String mode = "travelmode=driving&";
            navUrl = "https://www.google.com/maps/dir/?api=1&" + str_origin + "&" + str_dest + "&" + mode + wp;

        }else{
            Toast.makeText(context, "No se pudo obtener su ubicacion", Toast.LENGTH_SHORT).show();

            MapsActivity.enableUbication(mActivity);

        }

        return navUrl;
    }

    //Se obtiene la ubicacion actual
    private static Location getLastKnownLocation(Context context) {
        Location l=null;
        LocationManager mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if(ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
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

}
