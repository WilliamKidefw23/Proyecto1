package com.proyecto1.william.proyecto1.Entidad;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Usuario on 20/02/2018.
 */

public class Rutas {

    private String coordenadas;
    private String descripcionIni,descripcionFin;
    private double latitudIni,latitudFin;
    private double longitudIni,longitudFin;
    private List<LatLng> rutas;
    private String duracion;
    private int duracionvalor;
    private String distancia;
    private int distanciavalor;
    private String fecha;
    private String travelmode;

    public Rutas(){
        rutas = new ArrayList<LatLng>();
    }

    public Rutas(String coordenadas,
                 String descripcionIni,
                 String descripcionFin,
                 double latitudIni,
                 double latitudFin,
                 double longitudIni,
                 double longitudFin,
                 String fecha) {
        this.coordenadas = coordenadas;
        this.descripcionIni = descripcionIni;
        this.descripcionFin = descripcionFin;
        this.latitudIni = latitudIni;
        this.latitudFin = latitudFin;
        this.longitudIni = longitudIni;
        this.longitudFin = longitudFin;
        this.fecha = fecha;
    }

    public String getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }

    public String getDescripcionIni() {
        return descripcionIni;
    }

    public void setDescripcionIni(String descripcionIni) {
        this.descripcionIni = descripcionIni;
    }

    public String getDescripcionFin() {
        return descripcionFin;
    }

    public void setDescripcionFin(String descripcionFin) {
        this.descripcionFin = descripcionFin;
    }

    public double getLatitudIni() {
        return latitudIni;
    }

    public void setLatitudIni(double latitudIni) {
        this.latitudIni = latitudIni;
    }

    public double getLatitudFin() {
        return latitudFin;
    }

    public void setLatitudFin(double latitudFin) {
        this.latitudFin = latitudFin;
    }

    public double getLongitudIni() {
        return longitudIni;
    }

    public void setLongitudIni(double longitudIni) {
        this.longitudIni = longitudIni;
    }

    public double getLongitudFin() {
        return longitudFin;
    }

    public void setLongitudFin(double longitudFin) {
        this.longitudFin = longitudFin;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public int getDuracionvalor() {
        return duracionvalor;
    }

    public void setDuracionvalor(int duracionvalor) {
        this.duracionvalor = duracionvalor;
    }

    public String getDistancia() {
        return distancia;
    }

    public void setDistancia(String distancia) {
        this.distancia = distancia;
    }

    public int getDistanciavalor() {
        return distanciavalor;
    }

    public void setDistanciavalor(int distanciavalor) {
        this.distanciavalor = distanciavalor;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTravelmode() {
        return travelmode;
    }

    public void setTravelmode(String travelmode) {
        this.travelmode = travelmode;
    }

    public void addRutas(List<LatLng> rutas) {
        this.rutas.addAll(rutas);
    }
    public List<LatLng> getPoints() {
        return rutas;
    }

}
