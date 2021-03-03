package com.example.practica;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.practica.lista_despachos.FirebaseDespachoViewHolder;
import com.example.practica.lista_despachos.despacho;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ListaDespachosActivity extends AppCompatActivity {

    int MAX_POINTS = 8;
    Location currentLocation;

    private String MY_KEY = "AIzaSyCXBbshQNc65iXw86KBT0r3QrvX0fHI-vc";

    private RecyclerView rvDespachos;
    FirebaseRecyclerAdapter mFirebaseAdapter;

    private FloatingActionButton add_despacho; //para agregar despachos
    private FloatingActionButton startDriving;
    private FirebaseAuth mAuth;

    ArrayList<despacho> data = new ArrayList<>();
    ArrayList<despacho> sortedData;

    Integer distanceMatrix[][];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_despachos);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            MapsActivity.checkLocationPermission(this, this);
        }

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        Query query = FirebaseDatabase.getInstance().getReference().child("Despachos").child("Pendientes").child(FirebaseMethods.getUserId()).orderByChild("ciudad");

        if (user == null) {
            boolean flag = FirebaseMethods.registerUser("gabopezoa97@gmail.com", "12345a", "Gabriel Pezoa");
        }

        setupUI();

        setUpFirebaseAdapter(query);

        currentLocation = getLocation();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mFirebaseAdapter.startListening();
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    @Override
    protected void onResume() {
        super.onResume();

        Location current = getLocation();

        /*
        if(HaversineDistance(current.getLatitude(), current.getLongitude(), data.get(0).getLatitude(), data.get(0).getLongitude()) < 100){
            Intent i = new Intent(ListaDespachosActivity.this, FolioActivity.class);
            String despachoId = data.get(0).getDespachoId();
            Log.d("despachoId", despachoId);
            i.putExtra("despachoId", despachoId);
            startActivity(i);
            finish();
        }
         */
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAdapter.stopListening();
    }

    private void setupUI(){

        rvDespachos = (RecyclerView) findViewById(R.id.rvDespachos);
        add_despacho = (FloatingActionButton) findViewById(R.id.btnAddDespacho); //agregar despachos
        startDriving = (FloatingActionButton) findViewById(R.id.btnStartDespacho);

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
                        alertD.cancel();
                        Intent i = new Intent(ListaDespachosActivity.this, FolioActivity.class);
                        String despachoId = data.get(0).getDespachoId();
                        Log.d("despachoId", despachoId);
                        i.putExtra("despachoId", despachoId);
                        startActivity(i);
                        finish();
                        //launchNavigation();
                        //getDistanceInfo();
                        //Log.d("TAGCITO", MapsActivity.currentLocation.toString());
                        //sortDestinations();
                        //Intent b = new Intent(Intent.ACTION_VIEW, Uri.parse(getDirectionsUrl(MapsActivity.currentLocation, MapsActivity.currentLocation)));
                        //startActivity(b);
                    }
                });

                alertD.setView(popupView);
                alertD.show();
            }
        });

        startDriving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchNavigation();
            }
        });
    }

    private void setUpFirebaseAdapter(Query query) {

        FirebaseRecyclerOptions<despacho> options =
                new FirebaseRecyclerOptions.Builder<despacho>()
                        .setQuery(query, despacho.class)
                        .build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<despacho, FirebaseDespachoViewHolder> (options) {

            @NonNull
            @Override
            public FirebaseDespachoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.content_despachos_fragment, parent, false);

                return new FirebaseDespachoViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FirebaseDespachoViewHolder firebaseDespachoViewHolder, int i, @NonNull despacho despacho) {
                String key = getRef(i).getKey();
                despacho.setDespachoId(key);
                data.add(despacho);
                firebaseDespachoViewHolder.bindUser(despacho, i + 1);
            }
        };

        rvDespachos.setHasFixedSize(true);
        rvDespachos.setLayoutManager(new LinearLayoutManager(this));
        rvDespachos.setAdapter(mFirebaseAdapter);

        //Helper para poder hacer draggin de los items del recyclerview
        createHelper();
    }

    private void createHelper() {
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                Log.d("helper", "onMove");
                int position_dragged = viewHolder.getAdapterPosition();
                int position_target = target.getAdapterPosition();

                Collections.swap(data, position_dragged, position_target);

                mFirebaseAdapter.notifyItemMoved(position_dragged, position_target);

                printData();

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });

        helper.attachToRecyclerView(rvDespachos);
    }

    private void printData() {
        for (int i = 0; i < data.size(); i++)
            Log.d("CURRENT DATA: ", data.get(i).getAddress());
    }

    private Location getLocation() {

        final Location[] loc = new Location[1];

        FusedLocationProviderClient client =
                LocationServices.getFusedLocationProviderClient(this);

        // Get the last known location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return loc[0];
        }
        client.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        loc[0] = task.getResult();
                    }
                });

        return loc[0];
    }

    private void launchNavigation(){

        Double lat = data.get(0).getLatitude();
        Double lng = data.get(0).getLongitude();

        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lng);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Waypoints
        String wp = "";
        for (int i = 0; i < MAX_POINTS; i++) {

            Double lat = sortedData.get(i).getLatitude();
            Double lng = sortedData.get(i).getLongitude();

            if (i == 0){
                wp = "waypoints=";
            }

            if (i == MAX_POINTS - 1) {
                wp += lat.toString() + "," + lng.toString();
            } else {
                wp += lat.toString() + "," + lng.toString() + "%7C";
            }
        }

        // Para lanzar navigation
        String mode = "travelmode=driving&";
        String navUrl = "https://www.google.com/maps/dir/?api=1&" + str_origin + "&" + str_dest + "&" + mode + wp;

        Log.d("FINAL URL", navUrl);

        return navUrl;
    }
/*
    private void sortDestinations(){

        ArrayList<despacho> auxData = data;

        Double uLat = MapsActivity.currentLocation.latitude;
        Double uLng = MapsActivity.currentLocation.longitude;

        int nearestDistance = Integer.MAX_VALUE;
        int nearestDuration = Integer.MAX_VALUE;
        int dataPosition = 0;

        for(int j = 0; j < data.size(); j++) {
            for (int i = 0; i < auxData.size(); i++) {

                Double destLat = auxData.get(i).getLatitude();
                Double destLng = auxData.get(i).getLongitude();

                ArrayList<Integer> distanceInfo = null;

                if(j == 0)
                    distanceInfo = getDistanceInfo(uLat, uLng, destLat, destLng);
                else{
                    Double currentLat = sortedData.get(j-1).getLatitude();
                    Double currentLng = sortedData.get(j-1).getLongitude();
                    distanceInfo = getDistanceInfo(currentLat, currentLng, destLat, destLng);
                }

                int distance = distanceInfo.get(0);
                int duration = distanceInfo.get(1);

                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestDuration = duration;
                    dataPosition = i;
                }
            }

            sortedData.add(auxData.get(dataPosition));
            auxData.remove(dataPosition);
        }

    }

 */

    private void getDistanceInfo() {//realiza consulta a api de google y crea matriz de distancias

        final int nDespachos = data.size();
        distanceMatrix = new Integer [nDespachos][nDespachos];

        String places = "";

        for(int i = 0; i < nDespachos; i++){
            if(i == data.size()-1)
                places += data.get(i).getLatitude().toString() + "," + data.get(i).getLongitude().toString();
            else
                places += data.get(i).getLatitude().toString() + "," + data.get(i).getLongitude().toString() + "%7C";
        }

        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=" + places + "&destinations=" + places + "&key=" + MY_KEY;

        Log.d("GETDISTANCE", url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            JSONArray array = response.getJSONArray("rows");

                            for(int j = 0; j < nDespachos; j++){
                                JSONObject elements = array.getJSONObject(j);
                                JSONArray element = elements.getJSONArray("elements");

                                for(int k = 0; k < nDespachos; k++){
                                    JSONObject info = element.getJSONObject(k);
                                    JSONObject distance = info.getJSONObject("distance");
                                    int dist = distance.getInt("value");//metros

                                    distanceMatrix [j][k] = dist;
                                }
                            }

                            Log.d("distanceMatrix", Arrays.deepToString(distanceMatrix));

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Log.d("GETDIATANCE", "Primer catch");
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

    private static int HaversineDistance(double lat1, double lon1, double lat2, double lon2) {

        double earthRadius = 6371; // km

        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;

        double sinlat = Math.sin(dlat / 2);
        double sinlon = Math.sin(dlon / 2);

        double a = (sinlat * sinlat) + Math.cos(lat1)*Math.cos(lat2)*(sinlon*sinlon);
        double c = 2 * Math.asin (Math.min(1.0, Math.sqrt(a)));

        double distanceInMeters = earthRadius * c * 1000;

        return (int)distanceInMeters;

    }

}
