package com.proyecto1.william.proyecto1;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.proyecto1.william.proyecto1.Entidad.Rutas;
import com.proyecto1.william.proyecto1.GoogleMaps.AbstractRuteando;
import com.proyecto1.william.proyecto1.GoogleMaps.FetchPlaceTask;
import com.proyecto1.william.proyecto1.GoogleMaps.PlaceAutoCompleteAdapter;
import com.proyecto1.william.proyecto1.GoogleMaps.RouteExcepcion;
import com.proyecto1.william.proyecto1.GoogleMaps.RoutingListener;
import com.proyecto1.william.proyecto1.GoogleMaps.Ruteando;
import com.proyecto1.william.proyecto1.GoogleMaps.Util;
import com.proyecto1.william.proyecto1.Map.MapFragment;
import com.proyecto1.william.proyecto1.Settings.SettingsActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements RoutingListener,OnMapReadyCallback, FetchPlaceTask.OnTaskCompleted {

    private MapFragment mMapFragment;
    private GoogleMap mMap;
    private AutoCompleteTextView rInicio, rFin;
    private static final String TAG = MainActivity.class.getSimpleName();;
    private PlaceAutoCompleteAdapter mAdapter;
    private GoogleApiClient mGoogleApiClient;
    private GeoDataClient mGoogleDataClient;
    private LatLng lInicio, lFin;
    private ImageView rEnvio;
    private ProgressDialog progressDialog;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLocation;
    private List<Polyline> polylines;
    private List<Marker> markers;
    //Firebase Database
    private DatabaseReference databaseReference;
    //Firebase Autorizacion
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    //Bounds Lima Adapter
    private static final LatLngBounds BOUNDS_LIMA = new LatLngBounds(new LatLng(-12.2532891, -76.78833970000001),
            new LatLng(-11.7999875, -77.18721859999999));
    //Por default locacion y zoom
    private final LatLng mDefaultLocation = new LatLng(-12.046374, -77.042793);
    private static final int DEFAULT_ZOOM = 15;
    //Colores de las listado_rutas
    private static final int[] COLORS = new int[]{R.color.color0001, R.color.color0003, R.color.color0004, R.color.color0005,R.color.color0002};
    //Datos Usuario
    private String UID,usuario,rutaInicio,rutaFin;
    private String fechabusqueda;
    private final static int RUTAS_REQUEST_CODE = 1;
    private boolean RUTAS_MENSAJE=false;
    //Preferences
    private String travelMode;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.i(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.settings_gen, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        travelMode = sharedPref.getString("travelModes", "driving");
        //Log.d(TAG,"onCreate "+travelMode);
        polylines = new ArrayList<>();
        markers = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // Usuario conectado
                    //Log.d(TAG, "onAuthStateChanged:conectado");
                    UID = user.getUid();
                    //usuario = user.getEmail();
                } else {
                    // Usuario desconectado
                    //Log.e(TAG, "onAuthStateChanged:desconectado");
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    finish();
                }
            }
        };

        //LlamarUsuario();
        //Inicializar
        rInicio = (AutoCompleteTextView) findViewById(R.id.txtInicio);
        rFin = (AutoCompleteTextView) findViewById(R.id.txtFin);
        rEnvio = (ImageView) findViewById(R.id.imgEnvio);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        mMapFragment = MapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.map_contenedor, mMapFragment)
                .commit();

        /*SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);*/
        //mapFragment.getMapAsync(this);
        mMapFragment.getMapAsync(this);

        //Acceso a GoogleApiClient
        /*mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();*/

        //Acceso a Google Data Client
        mGoogleDataClient = Places.getGeoDataClient(this, null);

        mAdapter = new PlaceAutoCompleteAdapter(this, mGoogleDataClient, BOUNDS_LIMA, null);

        rInicio.setAdapter(mAdapter);
        rFin.setAdapter(mAdapter);
        rFin.requestFocus();
        rInicio.setOnItemClickListener(rutaInicioListener);
        rFin.setOnItemClickListener(rutaFinListener);

        rInicio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int startNum, int before, int count) {
                //Log.d(TAG,"onTextChanged rInicio");
                if (lInicio != null) {
                    lInicio = null;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        rFin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Log.d(TAG,"onTextChanged rFin");
                if (lFin != null) {
                    lFin = null;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        rEnvio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Util.Operations.isOnline(MainActivity.this))
                {
                    route();
                }
                else
                {
                    Toast.makeText(MainActivity.this,getString(R.string.internetFalse),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public AdapterView.OnItemClickListener rutaInicioListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            AutocompletePrediction item = mAdapter.getItem(i);
            String placeId = String.valueOf(item.getPlaceId());
            //Log.d(TAG, "Autocomplete Item seleccionado rutaInicio: " + item.getFullText(null));

            Task<PlaceBufferResponse> placeResult = mGoogleDataClient.getPlaceById(placeId);
            placeResult.addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                    try {
                        PlaceBufferResponse places = task.getResult();
                        final Place place = places.get(0);
                        lInicio = place.getLatLng();
                        places.release();
                    } catch (RuntimeRemoteException e) {
                        Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        //Log.e(TAG, "Error", e);
                        return;
                    }catch (IllegalStateException e) {
                        Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        //Log.e(TAG, "Error", e);
                        return;
                    }
                }
            });
        }
    };

    public AdapterView.OnItemClickListener rutaFinListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            AutocompletePrediction item = mAdapter.getItem(i);
            String placeId = String.valueOf(item.getPlaceId());
            //Log.d(TAG, "Autocomplete Item seleccionado rutaFin: " + item.getFullText(null));

            Task<PlaceBufferResponse> placeResult = mGoogleDataClient.getPlaceById(placeId);
            placeResult.addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                    try {
                        PlaceBufferResponse places = task.getResult();
                        final Place place = places.get(0);
                        lFin = place.getLatLng();
                        places.release();
                    } catch (RuntimeRemoteException e) {
                        Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        //Log.e(TAG, "Error", e);
                        return;
                    }catch (IllegalStateException e) {
                        Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        //Log.e(TAG, "Error", e);
                        return;
                    }
                }
            });
        }
    };

    public void LlamarUsuario(){
        SharedPreferences preferences = getSharedPreferences("LoginActivityPreferencia",MODE_PRIVATE);
        UID = preferences.getString("UID","");
        usuario = preferences.getString("Usuario","");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Log.i(TAG,"onMapReady()");
        mMap = googleMap;

        /*if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(false);
        }*/
       //if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Log.d(TAG,"If permiso");
                obtenerPosicionDispositivo();
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(false);
            } else {
                //Log.e(TAG,"Else permiso");
                //Request Location Permission
                checkLocationPermission();
            }
        //}
        /*else {
            Log.d(TAG,"Else permiso");
            obtenerPosicionDispositivo();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(false);
        }*/

        //obtenerPosicionDispositivo();

        //Posicionamiento de la camara Lima
        /*CameraUpdate center = CameraUpdateFactory.newLatLng(mDefaultLocation);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);*/

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                mAdapter.setBounds(bounds);
            }
        });

    }

    private void checkLocationPermission() {
        //Log.i(TAG,"checkLocationPermission");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Necesita una Explicacion?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.permisosLocationTitulo))
                        .setMessage(getString(R.string.permisosLocationMensaje))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // Si no necesita explicacion, aplicar permisos
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Log.i(TAG,"onRequestPermissionsResult");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // Si se cancela la solicitud, el array esta vacio
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permisos Otorgados
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        obtenerPosicionDispositivo();
                        mMap.setMyLocationEnabled(true);
                        mMap.getUiSettings().setZoomControlsEnabled(false);
                    }

                } else {
                    // Permiso Denegado
                    Toast.makeText(this, getString(R.string.permisoFalse), Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void obtenerPosicionDispositivo() {
        //Log.i(TAG, "obtenerPosicionDispositivo");
        try {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLocation = task.getResult();
                            //Log.d(TAG,task.getResult().toString());

                            lInicio = new LatLng(mLocation.getLatitude(),
                                    mLocation.getLongitude());

                            new FetchPlaceTask(MainActivity.this,
                                    MainActivity.this).execute(mLocation);

                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLocation.getLatitude(),
                                                mLocation.getLongitude()), DEFAULT_ZOOM));

                            LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;

                            //Se actualice la busqueda de listado_rutas
                            mAdapter.setBounds(bounds);

                        } else {
                            //Log.e(TAG, "Ubicacion Actual es nula. Usando Defaults");
                            //Log.e(TAG, "Excepcion: ", task.getException());
                            Toast.makeText(MainActivity.this,getString(R.string.locationFalse),
                                    Toast.LENGTH_LONG).show();
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        }
                    }
                });
        } catch (SecurityException e)  {
            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
            //Log.e(TAG, e.getMessage());
        } catch (Exception ex){
            Toast.makeText(MainActivity.this,ex.getMessage(),Toast.LENGTH_LONG).show();
            //Log.e(TAG, ex.getMessage());
        }
    }

    private void obtenerPosicion() {
        try {
            Task locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        mLocation = task.getResult();

                        lInicio = new LatLng(mLocation.getLatitude(),
                                mLocation.getLongitude());

                        new FetchPlaceTask(MainActivity.this,
                                MainActivity.this).execute(mLocation);

                        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;

                        //Se actualice la busqueda de listado_rutas
                        mAdapter.setBounds(bounds);

                    } else {
                        Toast.makeText(MainActivity.this,getString(R.string.locationFalse),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (SecurityException e)  {
            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
        } catch (Exception ex){
            Toast.makeText(MainActivity.this,ex.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public void route() {

        if (lInicio == null || lFin == null) {
            if (lInicio == null) {
                if (rInicio.getText().length() > 0) {
                    rInicio.setError(getString(R.string.locationValidacion));
                } else {
                    Toast.makeText(this, getString(R.string.locationInicio), Toast.LENGTH_SHORT).show();
                }
            }
            if (lFin == null) {
                if (rFin.getText().length() > 0) {
                    rFin.setError(getString(R.string.locationValidacion));
                } else {
                    Toast.makeText(this, getString(R.string.locationFin), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            progressDialog = ProgressDialog.show(this, getString(R.string.dialogoTitulo),
                    getString(R.string.dialogoMensaje), true);

            // Getting URL to the Google Directions API
            /*String url = getUrl(lInicio, lFin);
            FecthURL FetchUrl = new FecthURL(this);

            // Start downloading json data from Google Directions API
            FetchUrl.execute(url);*/

            Ruteando routing = new Ruteando.Builder()
                    //.travelMode(AbstractRuteando.TravelMode.DRIVING)
                    .prueba(travelMode)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(lInicio, lFin)
                    .build();
            routing.execute();
        }
    }

    public Bitmap resizeBitmap(String drawableName, int width, int height) {
        Bitmap bitmap = null;
        try{
            Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(drawableName, "drawable", getPackageName()));
            bitmap= Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        }catch (Exception e){
            //Log.e(TAG,e.getMessage());
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return bitmap;
    }

    @Override
    public void onRoutingSuccess(List<Rutas> resultado) {
        //Log.i(TAG, "onRoutingSuccess");
        //mMap.clear();
        progressDialog.dismiss();

        //Moviendo la camara al marcador Inicio
        CameraUpdate center = CameraUpdateFactory.newLatLng(lInicio);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);

        PolylineOptions lineOptions = null;

        //Eliminando poligonos lineales
        if(polylines.size()>0) {
            for (Polyline pol : polylines){
                pol.remove();
            }
        }
        polylines = new ArrayList<>();

        //Eliminando markadores
        if(markers.size()>0) {
        for (Marker mar : markers){
            mar.remove();
        }}
        markers = new ArrayList<>();

        // Navegando por las listado_rutas
        for (int i = 0; i < resultado.size(); i++) {
            lineOptions = new PolylineOptions();
            int colorIndex = i % COLORS.length;
            // Agregar todos los puntos de la ruta
            lineOptions.color(getResources().getColor(COLORS[colorIndex]));
            lineOptions.width(10 + i * 3);
            lineOptions.addAll(resultado.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(lineOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(),"Rutas "+ resultado.get(i).getTravelmode()+"-"+
                    (i+1) +": distancia - "+ resultado.get(i).getDistanciavalor()+":" +
                    " duracion - "+ resultado.get(i).getDuracionvalor(),Toast.LENGTH_SHORT).show();
        }

        // Dibujar el marker en el mapa
        if (lineOptions != null) {

            MarkerOptions options = new MarkerOptions();
            options.position(lInicio);
            options.title(rutaInicio);
            options.snippet(String.valueOf(lInicio));
            options.icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap("start_blue", 50, 50)));
            Marker marker = mMap.addMarker(options);
            markers.add(marker);

            // End marker
            options = new MarkerOptions();
            options.position(lFin);
            options.title(rFin.getText().toString());
            options.snippet(String.valueOf(lFin));
            options.icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap("end_green", 50, 50)));
            marker= mMap.addMarker(options);
            markers.add(marker);

        } else {
            Toast.makeText(getApplicationContext(),getString(R.string.pointMarker),Toast.LENGTH_SHORT).show();
        }

        //Log.d(TAG,"Boolean"+RUTAS_MENSAJE);

        if(!RUTAS_MENSAJE){
            GuardarRutas();
        }else{
            reiniciarBusqueda();
        }
    }

    public void GuardarRutas(){
        //Log.d(TAG,"GuardarRutas");

        if(!TextUtils.isEmpty(rInicio.getText()))
            rutaInicio = rInicio.getText().toString();

        /*Query query = databaseReference.child("listado_rutas").child(UID)
                .orderByChild("id");*/

        final String coordenadas = lInicio+";"+lFin;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
        fechabusqueda = dateFormat.format(new Date());

        //Log.d(TAG,"GuardarRutas "+UID);
        Query query = databaseReference.child("rutas").child(UID)
                .orderByChild("coordenadas").equalTo(coordenadas);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    reiniciarBusqueda();
                    /*for (DataSnapshot data : dataSnapshot.getChildren()){
                        //Log.d(TAG,"DataSnapshot"+data.getValue().toString());
                        if(listado_rutas.getLatitudIni()!=lInicio.latitude && listado_rutas.getLongitudIni()!=lInicio.longitude
                                && listado_rutas.getLatitudFin()!=lFin.latitude && listado_rutas.getLongitudFin()!=lFin.longitude){
                            Log.d(TAG,"CRUCE "+listado_rutas.getId());
                            reiniciarBusqueda();
                        }else{
                            Log.d(TAG,"NOCRUCE "+listado_rutas.getId());
                        }
                    }*/
                }else{
                         Rutas rutas = new Rutas(coordenadas,
                                 rutaInicio,rFin.getText().toString(),
                                 lInicio.latitude,lInicio.longitude,
                                 lFin.latitude,lFin.longitude,fechabusqueda);

                         //push genera una clave automatica
                         //Guarda las listado_rutas
                         databaseReference.child("rutas").child(UID).push().setValue(rutas);
                         reiniciarBusqueda();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
                //Log.e(TAG,databaseError.getMessage());
            }
        });
    }

    public AlertDialog createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ruta")
                .setMessage("Desea guardar la ruta ?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       //Toast.makeText(getApplicationContext(),"Ok",Toast.LENGTH_LONG).show();
                        String puntos = lInicio+";"+lFin;
                        Rutas rutas = new Rutas(puntos,
                                rInicio.getText().toString(),rFin.getText().toString(),
                                lInicio.latitude,lInicio.longitude,
                                lFin.latitude,lFin.longitude,fechabusqueda);
                        //push genera una clave automatica
                        //Guarda las listado_rutas
                        databaseReference.child("listado_rutas").child(UID).push().setValue(rutas);
                        reiniciarBusqueda();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Toast.makeText(getApplicationContext(),"No",Toast.LENGTH_LONG).show();
                        reiniciarBusqueda();
                        dialogInterface.cancel();
                    }
                });
        return builder.create();
    }

    public void reiniciarBusqueda(){
        //Log.d(TAG,"reiniciarBusqueda");
        RUTAS_MENSAJE=false;
        rInicio.setText("");
        rFin.setText("");
        rFin.requestFocus();
        obtenerPosicion();
        //rInicio.requestFocus();
    }

    @Override
    public void onRoutingCancelled() {
        //Log.i(TAG, "onRoutingCancelled");
    }

    @Override
    public void onRoutingStart() {
        //Log.i(TAG, "onRoutingStart");
    }

    @Override
    protected void onResume() {
        //Log.i(TAG, "onResume");
        super.onResume();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        // Actualizar cantidad de items
        travelMode = sharedPref.getString("travelModes", "driving");
        //Log.d(TAG,"onResume "+travelMode);
        //Log.d(TAG,UID);
    }

    @Override
    protected void onPause() {
        //Log.i(TAG, "onPause");
        super.onPause();
        //Log.d(TAG,UID);
    }

    @Override
    protected void onStart() {
        //Log.i(TAG,"onStart");
        super.onStart();
        //Log.d(TAG,UID);
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        //Log.i(TAG,"onStop");
        super.onStop();
        //Log.d(TAG,UID);
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onRestart() {
        //Log.i(TAG,"onRestart");
        super.onRestart();
        //Log.d(TAG,"onRestart "+UID);
    }

    @Override
    protected void onDestroy() {
        //Log.i(TAG,"onDestroy");
        super.onDestroy();
        //mAuth.signOut();
    }

    @Override
    public void onRoutingFailure(RouteExcepcion e) {
        //Log.i(TAG, "onRoutingFailure");
        progressDialog.dismiss();
        if (e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getString(R.string.errorRuteando), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Log.i(TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Log.i(TAG, "onOptionsItemSelected");
        switch (item.getItemId()){
            case R.id.action_lista_rutas:
                Intent intent = new Intent(this,RutasActivity.class);
                intent.putExtra("UID",UID);
                startActivityForResult(intent,RUTAS_REQUEST_CODE);
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.action_desconectar:
                mAuth.signOut();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.i(TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RUTAS_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {

                double recp_inicio_lat = data.getDoubleExtra("ruta_inicio_lat",0);
                double recp_inicio_long = data.getDoubleExtra("ruta_inicio_long",0);
                double recp_fin_lat = data.getDoubleExtra("ruta_fin_lat",0);
                double recp_fin_long = data.getDoubleExtra("ruta_fin_long",0);

                lInicio = new LatLng(recp_inicio_lat,recp_fin_lat);
                lFin = new LatLng(recp_inicio_long,recp_fin_long);

                RUTAS_MENSAJE=true;

                progressDialog = ProgressDialog.show(this, getString(R.string.dialogoTitulo),
                        getString(R.string.dialogoMensaje), true);

                Ruteando routing = new Ruteando.Builder()
                        .travelMode(AbstractRuteando.TravelMode.DRIVING)
                        //.prueba(travelMode)
                        .withListener(this)
                        .alternativeRoutes(true)
                        .waypoints(lInicio, lFin)
                        .build();
                routing.execute();

            }else{
                //Toast.makeText(this,"Hubo Error al Traer la Ruta",Toast.LENGTH_LONG).show();
                RUTAS_MENSAJE=true;
            }
        }
    }

    @Override
    public void onBackPressed() {
        //Log.i(TAG, "onBackPressed");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.mensajeExit));
        builder.setCancelable(true);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                //mAuth.signOut();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onTaskCompleted(String result) {
        //Log.i(TAG,"LUGAR ELEGIDO "+result);
        rutaInicio = result;
    }
}
