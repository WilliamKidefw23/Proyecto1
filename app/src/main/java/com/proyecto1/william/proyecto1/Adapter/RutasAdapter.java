package com.proyecto1.william.proyecto1.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.proyecto1.william.proyecto1.Entidad.Rutas;
import com.proyecto1.william.proyecto1.R;

import java.util.List;

/**
 * Created by Usuario on 21/02/2018.
 */

public class RutasAdapter extends RecyclerView.Adapter<RutasAdapter.ViewHolder>  {

    private List<Rutas> rutas;

    public  RutasAdapter(List<Rutas> rutas){
        this.rutas = rutas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listado_rutas, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.descripcionIni.setText(rutas.get(position).getDescripcionIni());
        //holder.latitudIni.setText(String.valueOf(model.getLatitudIni()));
        //holder.longitudIni.setText(String.valueOf(model.getLongitudIni()));
        holder.descripcionFin.setText(rutas.get(position).getDescripcionFin());
        //holder.latitudFin.setText(String.valueOf(model.getLatitudFin()));
        //holder.longitudFin.setText(String.valueOf(model.getLongitudFin()));
        holder.ruta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(view.getContext(),"Probando : " + holder.descripcionIni.getText(),Toast.LENGTH_SHORT).show();
                //LatLng inicio = new LatLng(model.getLatitudIni(),model.getLongitudIni());
                //LatLng fin = new LatLng(model.getLatitudFin(),model.getLongitudFin());

                Intent databack = new Intent();
                databack.putExtra("ruta_inicio_lat",rutas.get(position).getLatitudIni());
                databack.putExtra("ruta_inicio_long",rutas.get(position).getLongitudIni());
                databack.putExtra("ruta_fin_lat",rutas.get(position).getLatitudFin());
                databack.putExtra("ruta_fin_long",rutas.get(position).getLongitudFin());
                //setResult(RESULT_OK,databack);
                //finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return rutas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        public TextView descripcionIni,longitudIni,latitudIni;
        public TextView descripcionFin,longitudFin,latitudFin;
        public TextView fecha;
        public ImageView ruta;

        public ViewHolder(View view){
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardView);
            descripcionIni = (TextView) view.findViewById(R.id.descripcionIni);
            //longitudIni = (TextView) view.findViewById(R.id.longitudIni);
            //latitudIni = (TextView) view.findViewById(R.id.latitudIni);
            descripcionFin = (TextView) view.findViewById(R.id.descripcionFin);
            //longitudFin = (TextView) view.findViewById(R.id.longitudFin);
            //latitudFin = (TextView) view.findViewById(R.id.latitudFin);
            fecha = view.findViewById(R.id.fechaRuta);
            ruta = (ImageView) view.findViewById(R.id.ruta);
            Glide.with(view.getContext()).load(R.drawable.icon_ruta).into(ruta);
        }
    }
}
