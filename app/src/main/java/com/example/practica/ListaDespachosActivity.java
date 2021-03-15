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

    int MAX_POINTS = 8;
    Location currentLocation;

    private String MY_KEY = "AIzaSyCXBbshQNc65iXw86KBT0r3QrvX0fHI-vc";

    Query query;

    private RecyclerView rvDespachos;
    FirebaseRecyclerAdapter mFirebaseAdapter;

    private FloatingActionButton add_despacho; //para agregar despachos
    private FloatingActionButton startDriving;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;

    ArrayList<classDespacho> data = new ArrayList<>();
    ArrayList<classDespacho> sortedData;

    Integer distanceMatrix[][];

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

        currentLocation = getLocation(this, this);

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

        Location current = getLocation(this, this);

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
                View popupView = inflater.inflate(R.layout.popup_add_despacho, null);

                Button btnDestino = (Button) popupView.findViewById(R.id.btnMapaDestino);
                Button btnOrigen = (Button) popupView.findViewById(R.id.btnMapaOrigen);
                TextView tvX = popupView.findViewById(R.id.tvX);

                btnOrigen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("TAGCITO", "boton origen");
                        alertD.cancel();
                        startActivity(new Intent(ListaDespachosActivity.this, CrearDespachoActivity.class));
                    }
                });

                btnDestino.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("TAGCITO", "boton destino");
                        alertD.cancel();
                        /*
                        Intent i = new Intent(ListaDespachosActivity.this, FolioActivity.class);
                        String despachoId = data.get(0).getDespachoId();
                        Log.d("despachoId", despachoId);
                        i.putExtra("despachoId", despachoId);
                        startActivity(i);

                         */
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

        startDriving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //launchNavigation();
                String url = getDirectionsUrl(data.get(0).getDestinos(), mContext);
                if(url != null){
                    Intent b = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(b);
                }
            }
        });
    }

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

    public static Location getLocation(Activity activity, Context context) {

        final Location[] loc = new Location[1];

        FusedLocationProviderClient client =
                LocationServices.getFusedLocationProviderClient(activity);

        // Get the last known location
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return loc[0];
        }
        client.getLastLocation()
                .addOnCompleteListener(activity, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        loc[0] = task.getResult();
                    }
                });

        Log.d("getLocation ", String.valueOf(loc[0]));
        return loc[0];
    }
/*
    private void launchNavigation(){

        Double lat = data.get(0).getLatitude();
        Double lng = data.get(0).getLongitude();

        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lng);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

 */

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
/*
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

 */
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
