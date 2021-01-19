package com.example.operacionesivra.Monitoreo.Detalles;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.operacionesivra.R;
import com.example.operacionesivra.Services.ConexionMonitoreo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DetallesMonitoreo extends FragmentActivity implements OnMapReadyCallback {
    public GoogleMap mMap;
    LinearLayout mapa;
    List<ModeloDetallesMonitoreo> detallesMonitoreos = new ArrayList<>();
    RecyclerView movimientos;
    AdapterDetallesMonitoreo adapter;


    private void requestPermission(String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    32);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitoreo_detalles_monitoreo);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        mostrarocultarmapa();
        movimientos = findViewById(R.id.recyclermovimientos);
        movimientos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdapterDetallesMonitoreo(cargardetalles(getIntent().getStringExtra("IDUsuario")));
        movimientos.setAdapter(adapter);

    }

    public void mostrarocultarmapa(){
        mapa = findViewById(R.id.mapadetalles);
        findViewById(R.id.vermapadetalles).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapa.getVisibility()==View.GONE){
                    mapa.setVisibility(View.VISIBLE);
                }else{
                    mapa.setVisibility(View.GONE);
                }
            }
        });
    }

    public List<ModeloDetallesMonitoreo> cargardetalles(String id){
        ConexionMonitoreo conexion = new ConexionMonitoreo(this);
        try {
            Statement s = conexion.conexiondbImplementacion().createStatement();
            ResultSet r = s.executeQuery("Select * from Ubicacion where IDUsuario='"+id+"' order by fechahora ");
            while(r.next()){
                detallesMonitoreos.add(new ModeloDetallesMonitoreo(r.getString("IDUbicacion"),r.getString("IDUsuario")
                ,r.getString("Latitud"),r.getString("Longitud"),r.getString("FechaHora")));
            }
        }catch (Exception e){
            Toast.makeText(this, "Error: "+e, Toast.LENGTH_LONG).show();
        }
        return detallesMonitoreos;
    }

    private String buildRequestUrl(LatLng origin, LatLng destination) {
        String strOrigin = "origin=" + origin.latitude + "," + origin.longitude;
        String strDestination = "destination=" + destination.latitude + "," + destination.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";

        String param = strOrigin + "&" + strDestination + "&" + sensor + "&" + mode;
        String output = "json";
        String APIKEY = getResources().getString(R.string.google_maps_key);

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param + "&key="+APIKEY;
        Log.d("TAG", url);
        return url;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng mOrigin = new LatLng(41.3949,2.0086);
        LatLng mDestination = new LatLng(41.1258,1.2035);
        //Google map setup
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Show marker on the screen and adjust the zoom level
        mMap.addMarker(new MarkerOptions().position(mOrigin).title("Origin"));
        mMap.addMarker(new MarkerOptions().position(mDestination).title("Destination"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mOrigin,8f));
        new TaskDirectionRequest().execute(buildRequestUrl(mOrigin,mDestination));
    }


    private String requestDirection(String requestedUrl) {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(requestedUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            responseString = stringBuffer.toString();
            bufferedReader.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        httpURLConnection.disconnect();
        return responseString;
    }

    //-------------------------------Direction

    //Get JSON data from Google Direction
    public class TaskDirectionRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String responseString) {
            super.onPostExecute(responseString);
            //Json object parsing
            TaskParseDirection parseResult = new TaskParseDirection();
            parseResult.execute(responseString);
        }
    }


    //Parse JSON Object from Google Direction API & display it on Map
    public class TaskParseDirection extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonString) {
            List<List<HashMap<String, String>>> routes = null;
            JSONObject jsonObject = null;

            try {
                jsonObject = new JSONObject(jsonString[0]);
                DirectionParser parser = new DirectionParser();
                routes = parser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            super.onPostExecute(lists);
            ArrayList points = null;
            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lng"));

                    points.add(new LatLng(lat, lon));
                }
                polylineOptions.addAll(points);
                polylineOptions.width(15f);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }
            if (polylineOptions != null) {
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found", Toast.LENGTH_LONG).show();
            }
        }
    }
}