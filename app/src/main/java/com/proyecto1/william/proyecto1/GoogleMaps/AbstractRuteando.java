package com.proyecto1.william.proyecto1.GoogleMaps;

import android.os.AsyncTask;
import android.util.Log;
import com.proyecto1.william.proyecto1.Entidad.Rutas;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Usuario on 19/03/2018.
 */

public abstract class AbstractRuteando extends AsyncTask<Void,Void,List<Rutas>>{

    protected List<RoutingListener> listeners;

    protected static final String DIRECTIONS_API_URL = "https://maps.googleapis.com/maps/api/directions/json?";

    private RouteExcepcion mException = null;

    private static final String TAG="AbstractRuteando";

    public enum TravelMode {
        BIKING("bicycling"),
        DRIVING("driving"),
        WALKING("walking"),
        TRANSIT("transit");

        protected String valor;

        TravelMode(String Valor) {
            this.valor = Valor;
        }

        protected String getValor() {
            return valor;
        }
    }

    public enum AvoidKind {
        TOLLS (1, "tolls"),
        HIGHWAYS (1 << 1, "highways"),
        FERRIES (1 << 2, "ferries");

        private final String parametro;
        private final int valor;

        AvoidKind(int valor, String parametro) {
            this.valor = valor;
            this.parametro = parametro;
        }

        protected int getValor() {
            return valor;
        }

        protected static String getParametro (int val) {
            StringBuilder stringBuilder = new StringBuilder();
            for (AvoidKind aux : AvoidKind.values()) {
                if ((val & aux.getValor()) == aux.valor) {
                    stringBuilder.append(aux.parametro).append('|');
                }
            }
            return stringBuilder.toString();
        }
    }

    protected abstract String obtenerURL();

    protected AbstractRuteando (RoutingListener listener) {
        this.listeners = new ArrayList<RoutingListener>();
        registerListener(listener);
    }

    public void registerListener(RoutingListener mListener) {
        if (mListener != null) {
            listeners.add(mListener);
        }
    }

    protected void dispatchOnStart() {
        for (RoutingListener mListener : listeners) {
            mListener.onRoutingStart();
        }
    }

    protected void dispatchOnFailure(RouteExcepcion exception) {
        for (RoutingListener mListener : listeners) {
            mListener.onRoutingFailure(exception);
        }
    }

    protected void dispatchOnSuccess(List<Rutas> route) {
        for (RoutingListener mListener : listeners) {
            mListener.onRoutingSuccess(route);
        }
    }

    private void dispatchOnCancelled() {
        for (RoutingListener mListener : listeners) {
            mListener.onRoutingCancelled();
        }
    }

    @Override
    protected List<Rutas> doInBackground(Void... voids) {
        List<Rutas> result = new ArrayList<Rutas>();
        try {
            result = new GoogleParser(obtenerURL()).parse();
        }catch(RouteExcepcion e){
            mException = e;
        }
        return result;
    }

    @Override
    protected void onPreExecute() {
        //Log.i(TAG,"onPreExecute");
        dispatchOnStart();
    }

    @Override
    protected void onPostExecute(List<Rutas> rutas) {
        //Log.i(TAG,"onPostExecute");
        if (!rutas.isEmpty()) {
            /*int shortestRouteIndex = 0;
            int minDistance = Integer.MIN_VALUE;

            for (int i = 0; i < listado_rutas.size(); i++) {
                PolylineOptions mOptions = new PolylineOptions();
                Rutas route = listado_rutas.get(i);

                //Find the shortest route index
                if (listado_rutas.getLength() < minDistance) {
                    shortestRouteIndex = i;
                    minDistance = route.getLength();
                }

                for (LatLng point : route.getPoints()) {
                    mOptions.add(point);
                }
                listado_rutas.get(i).setPolyOptions(mOptions);
            }*/
            dispatchOnSuccess(rutas);
        } else {
            dispatchOnFailure(mException);
        }
    }

    @Override
    protected void onCancelled() {
        dispatchOnCancelled();
    }
}
