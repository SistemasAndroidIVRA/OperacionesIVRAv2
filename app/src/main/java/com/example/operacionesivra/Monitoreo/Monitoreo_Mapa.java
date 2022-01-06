package com.example.operacionesivra.Monitoreo;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.operacionesivra.MainActivity.MainActivity;
import com.example.operacionesivra.Monitoreo.Detalles.DetallesMonitoreo;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Services.Conexion;
import com.example.operacionesivra.Services.ConexionMonitoreo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Monitoreo_Mapa extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    Context context;
    ArrayList<ModeloDatosUsuario> lista = new ArrayList<>();
    ArrayList<ModeloClienteUbicacion> clientes = new ArrayList<>();
    LocationManager locationManager;
    String idusuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitoreo__mapa);
        context = this;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        localizaciontiemporeal();

    }

    //Obtiene las credenciales del ususario
    public String obtenercredenciales() {
        String id;
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        idusuario = preferences.getString("iduser", "Vacio");
        id = preferences.getString("iduser", "Vacio");
        return id;
    }

    //Comprueba en la base de datos los permisos actuales existentes
    public List<ModeloTipoUsuario> obtenertipoUsuario() {
        List<ModeloTipoUsuario> tipoUsuarios = new ArrayList<>();
        ConexionMonitoreo c = new ConexionMonitoreo(this);
        try {
            Statement s = c.conexiondbImplementacion().createStatement();
            ResultSet r = s.executeQuery("Select * from TipoUsuario");
            while (r.next()) {
                tipoUsuarios.add(new ModeloTipoUsuario(r.getString("Tipo"), r.getString("IDUsuario")));
            }
        } catch (Exception e) {
            new MaterialAlertDialogBuilder(context)
                    .setCancelable(false)
                    .setTitle("Error")
                    .setMessage("Imposible cargar usuarios, porfavor intentalo mas tarde")
                    .setPositiveButton("Regresar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(context, MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                    })
                    .show();
        }
        return tipoUsuarios;
    }

    //Verifica el tipo de usuario en sesion
    public String asignarPermiso(List<ModeloTipoUsuario> tipo) {
        String tipousuario = "";
        ConexionMonitoreo c = new ConexionMonitoreo(this);
        //Conexion c = new Conexion(this);
        try {
            Statement s = c.conexiondbImplementacion().createStatement();
            ResultSet r = s.executeQuery("Select * from Usuarios where IDUsuario='" + obtenercredenciales() + "'");
            while (r.next()) {
                for (int i = 0; i < tipo.size(); i++) {
                    if (r.getString("TipoUsuario").equals(tipo.get(i).getIdusuario())) {
                        tipousuario = tipo.get(i).getTipo();
                    }
                }
            }
        } catch (Exception e) {
            new MaterialAlertDialogBuilder(context)
                    .setCancelable(false)
                    .setTitle("Error al comprobar")
                    .setMessage("Error al cargar permisos de usuario, intentelo mas tarde: "+e.getMessage())
                    .setPositiveButton("Regresar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(context, MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                    })
                    .show();
        }
        return tipousuario;
    }

    //Actualiza la localización del usuario en tiempo real segun el parametro asignado de tiempo y distancia recorrida
    private void localizaciontiemporeal() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }

        }
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                100000, 30, location);
    }

    //Carga los usuarios activos de la base de datos
    public List<ModeloDatosUsuario> obtenerUsuarios() {
        ConexionMonitoreo conexion = new ConexionMonitoreo(this);
        try {
            Statement s = conexion.conexiondbImplementacion().createStatement();
            ResultSet r = s.executeQuery("Select * from Usuarios where longitud is not null");
            while (r.next()) {
                lista.add(new ModeloDatosUsuario(r.getString("Nombre_Completo"), r.getFloat("Latitud"), r.getFloat("Longitud"), r.getString("IDUsuario")));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return lista;
    }

    //Override de cargar mapa
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng shimaco = new LatLng(21.088395, -101.621771);
        mMap.addMarker(new MarkerOptions().position(shimaco).title("Shimaco Group")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ubicacion_empresa)));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(shimaco));
        mMap.setTrafficEnabled(true);
        if (asignarPermiso(obtenertipoUsuario()).equals("Administrador")) {
            llenarusuarioactivos();
            crearGeocercas();
            crearPoligono();

        }
        //crearGeocerca();
        //marcarClientes();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    //Crea el area que tendrá la Geo Cerca
    public void crearGeocercas() {
        LatLng shimaco = new LatLng(21.088395, -101.621771);
        CircleOptions circleOptions = new CircleOptions()
                .center(shimaco)
                .strokeColor(Color.argb(50, 70, 70, 70))
                .fillColor(Color.argb(100, 150, 150, 150))
                .radius(180);
        mMap.addCircle(circleOptions);
    }

    //Coloca puntos en la ultima ubicación del usuario
    public void llenarusuarioactivos() {
        obtenerUsuarios();

        for (int i = 0; i < lista.size(); i++) {
            LatLng usuario = new LatLng(lista.get(i).getLatitud(), lista.get(i).getLongitud());
            mMap.addMarker(new MarkerOptions().position(usuario).title(lista.get(i).getNombre())
                    //Cambia las propiedades del icono mistrado
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ubicacion_suario))

            );
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    mensajedetalles(marker.getTitle());
                    return false;
                }
            });
        }
    }

    //Confirma ver los detalles de alguno de los puntos marcados
    public void mensajedetalles(final String nombre) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Ver detalles")
                .setMessage("Quieres ver el historial de movimientos de:" + nombre)
                .setPositiveButton("Ver", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(context, DetallesMonitoreo.class);
                        i.putExtra("IDUsuario", verid(nombre));
                        startActivity(i);
                        finish();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    //Muestra el ID de la persona seleccionada
    public String verid(String nombre) {
        String id = "";
        for (int i = 0; i < lista.size(); i++) {
            if (nombre.equals(lista.get(i).getNombre())) {
                id = lista.get(i).getIdusuario();
            }
        }
        return id;
    }

    //GPS
    //Actualiza
    LocationListener location = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Toast.makeText(context, "Hay movimiento", Toast.LENGTH_SHORT).show();
            Toast.makeText(context, location.getLatitude() + "", Toast.LENGTH_SHORT).show();
            Toast.makeText(context, location.getLongitude() + "", Toast.LENGTH_SHORT).show();
            Toast.makeText(context, location.getSpeed() + "", Toast.LENGTH_SHORT).show();
            //Cuando se ective se estará enviando la informacion al servidor
            //actualizarubicacionDB(location.getLatitude()+"",location.getLongitude()+"",idusuario);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    //Actualiza la ubicacioin del usuario en la base de datos
    public void actualizarubicacionDB(String latitud, String longitud, String id) {
        ConexionMonitoreo c = new ConexionMonitoreo(this);
        try (PreparedStatement p = c.conexiondbImplementacion().prepareCall("Execute UpdateUbicacion ?,?,?")) {
            p.setString(1, latitud);
            p.setString(2, longitud);
            p.setString(3, id);
            p.execute();
        } catch (SQLException e) {
            System.out.println("error" + e);
        }

    }

    //Calcular distancia entre dos puntos
    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = (double) (earthRadius * c);

        return dist;
    }

    //Recibe coordenadas, regresa direccion
    public String direccion(Location loc) {
        String direccion = "Direccion no disponible";
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> list = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            if (!list.isEmpty()) {
                Address DirCalle = list.get(0);
                String tempo = DirCalle.getAddressLine(0);
                direccion = tempo;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return direccion;
    }

    //Recibe una direccion, regresa coordenadas
    public LatLng coordenadas(String direccion) {
        LatLng localizacioncliente = null;
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> list = geocoder.getFromLocationName(direccion, 1);
            if (!list.isEmpty()) {
                localizacioncliente = new LatLng(list.get(0).getLatitude(), list.get(0).getLongitude());
            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
        return localizacioncliente;
    }

    //Marca las localizaciones de los clientes
    public void marcarClientes() {
        obtenerClientesDB();
        for (int i = 0; i < clientes.size(); i++) {
            try {
                LatLng usuario = clientes.get(i).getUbicacion();
                mMap.addMarker(new MarkerOptions().position(usuario).title(clientes.get(i).getNombre()));
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        mensajedetalles(marker.getTitle());
                        return false;
                    }
                });
            } catch (Exception e) {
                System.out.println("Error " + e);
            }
        }
    }

    //Obtiene la lista de clientes en el sistema
    public List<ModeloClienteUbicacion> obtenerClientesDB() {
        Conexion conexion = new Conexion(this);
        try {
            Statement s = conexion.conexiondbImplementacion().createStatement();
            ResultSet r = s.executeQuery("Execute PMovil_VerDireccionesClientes 'PAQ COECILLO 1'");
            while (r.next()) {
                String direccion = r.getString("Direccion") + ", " + r.getString("ClaveCodPostal");
                System.out.println(r.getString("Cliente") + "\n" + direccion);
                clientes.add(new ModeloClienteUbicacion(r.getString("Cliente"), coordenadas(direccion)));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return clientes;
    }

    /*
    public Geofence crearGeocerca(){
        Geofence geofence = new Geofence.Builder()
                .setRequestId("Radio Shimaco") // Geofence ID
                .setCircularRegion( 21.085666, 101.6208686, 100) // defining fence region
                .setExpirationDuration( 60 * 60 * 1000 )// Transition types that it should look for
                .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT )
                .build();
        return geofence;
    }
     */

    //Crea un poligo visual en el mapa apartir de 4 coordenadas
    public void crearPoligono() {
        Polygon polygon1 = mMap.addPolygon(new PolygonOptions()
                .strokeWidth(2)
                .add(
                        new LatLng(21.087945, -101.619635),
                        new LatLng(21.087049, -101.620475),
                        new LatLng(21.088654, -101.622470),
                        new LatLng(21.089555, -101.621636))
                .fillColor(R.drawable.colores_prueba)
                .clickable(true));
        polygon1.setTag("alpha");
    }


}