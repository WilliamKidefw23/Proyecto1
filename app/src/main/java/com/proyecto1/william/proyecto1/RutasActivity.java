package com.proyecto1.william.proyecto1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.proyecto1.william.proyecto1.Adapter.RutasAdapter;
import com.proyecto1.william.proyecto1.Entidad.Rutas;
import com.proyecto1.william.proyecto1.Settings.SettingsActivity;

public class RutasActivity extends AppCompatActivity{

    private static final String TAG = RutasActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<Rutas,RutasAdapter.ViewHolder> adaptador;
    private RutasAdapter adapter;
    private FirebaseRecyclerOptions<Rutas> options;
    private Query query;
    private int cantidadRutas;
    //Datos Usuario
    String UID,usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.i(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_rutas);

        PreferenceManager.setDefaultValues(this, R.xml.settings_gen, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        cantidadRutas = Integer.parseInt(sharedPref.getString("cantidadRutas", "4"));
        //Log.d(TAG,"onCreate "+cantidadRutas);
        recyclerView = (RecyclerView) findViewById(R.id.recycleviewLista);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        LlamarUsuario();
        CargaQuery();

    }

    public void LlamarUsuario(){
        //SharedPreferences preferences = getSharedPreferences("LoginActivityPreferencia",MODE_PRIVATE);
        UID = getIntent().getStringExtra("UID");
        //UID = preferences.getString("UID","");
        //user = preferences.getString("Usuario","");
    }

    public void CargaQuery(){
        //Query del child Rutas
        query = FirebaseDatabase.getInstance().
                getReference().child("rutas").child(UID).limitToFirst(cantidadRutas);

        options = new FirebaseRecyclerOptions.Builder<Rutas>()
                .setLifecycleOwner(this)
                .setQuery(query, Rutas.class)
                .build();
    }

    public void CargaAdaptador(){

        adaptador = new FirebaseRecyclerAdapter<Rutas, RutasAdapter.ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RutasAdapter.ViewHolder holder, int position, @NonNull final Rutas model) {
                //Log.d(TAG,model.getDescripcionIni().toString());
                //Log.d(TAG,model.getDescripcionFin().toString());
                holder.descripcionIni.setText(model.getDescripcionIni());
                //holder.latitudIni.setText(String.valueOf(model.getLatitudIni()));
                //holder.longitudIni.setText(String.valueOf(model.getLongitudIni()));
                holder.descripcionFin.setText(model.getDescripcionFin());
                //holder.latitudFin.setText(String.valueOf(model.getLatitudFin()));
                //holder.longitudFin.setText(String.valueOf(model.getLongitudFin()));
                holder.fecha.setText(model.getFecha());
                holder.ruta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Log.i(TAG,"onClick");
                        //Toast.makeText(view.getContext(),"Probando : " + holder.descripcionIni.getText(),Toast.LENGTH_SHORT).show();
                        //LatLng inicio = new LatLng(model.getLatitudIni(),model.getLongitudIni());
                        //LatLng fin = new LatLng(model.getLatitudFin(),model.getLongitudFin());

                        Intent databack = new Intent();
                        databack.putExtra("ruta_inicio_lat",model.getLatitudIni());
                        databack.putExtra("ruta_inicio_long",model.getLongitudIni());
                        databack.putExtra("ruta_fin_lat",model.getLatitudFin());
                        databack.putExtra("ruta_fin_long",model.getLongitudFin());
                        setResult(RESULT_OK,databack);
                        finish();
                    }
                });
            }

            @Override
            public RutasAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listado_rutas, parent, false);
                RutasAdapter.ViewHolder viewHolder = new RutasAdapter.ViewHolder(view);
                return viewHolder;
            }
        };

        recyclerView.setAdapter(adaptador);
    }

    @Override
    protected void onStart() {
        //Log.i(TAG,"onStart");
        super.onStart();
        CargaAdaptador();
        //adaptador.startListening();
    }

    @Override
    protected void onResume() {
        //Log.i(TAG,"onResume");
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        cantidadRutas = Integer.parseInt(sharedPref.getString("cantidadRutas", "4"));
        //Log.d(TAG,"onResume "+cantidadRutas);
        CargaQuery();
        CargaAdaptador();
        //updateAdapter(ConjuntoListas.randomList(cantidadItems));

    }

    @Override
    protected void onPause() {
        //Log.i(TAG,"onPause");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        //Log.i(TAG,"onStop");
        super.onStop();
        //adaptador.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ruta_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
