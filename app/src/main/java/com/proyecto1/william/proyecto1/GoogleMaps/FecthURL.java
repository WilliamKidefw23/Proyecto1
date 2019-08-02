package com.proyecto1.william.proyecto1.GoogleMaps;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.proyecto1.william.proyecto1.Entidad.Rutas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Usuario on 17/02/2018.
 */

public class FecthURL extends AsyncTask<String, Void, String> {

    protected List<RoutingListener> alisteners;
    private RouteExcepcion mException = null;
    private Context context;

    @Override
    protected String doInBackground(String... url) {
        String data = "";

        try {
            // Fetching the data from web service
            data = downloadUrl(url[0]);
            //Log.d("Background Task data", data.toString());
        } catch (Exception e) {
            //Log.e("Background Task", e.toString());
        }
        return data;
    }

    @Override
    protected void onPostExecute(String data) {
        super.onPostExecute(data);
        List<Rutas> result = new ArrayList<>();

        try {
            result = new ParserTask().parse();
            //Log.d("Probando",result.toString());
            dispatchOnSuccess(result);
        }catch(RouteExcepcion e){
            dispatchOnFailure(mException);
            mException = e;
        }

    }

    @Override
    protected void onPreExecute() {
        dispatchOnStart();
    }

    @Override
    protected void onCancelled() {
        dispatchOnCancelled();
    }

    public FecthURL(RoutingListener listener) {
        this.alisteners = new ArrayList<RoutingListener>();
        registerListener(listener);
    }

    public void registerListener(RoutingListener mListener) {
        if (mListener != null) {
            alisteners.add(mListener);
        }
    }

    protected void dispatchOnSuccess(List<Rutas> route) {
        for (RoutingListener mListener : alisteners) {
            mListener.onRoutingSuccess(route);
        }
    }

    protected void dispatchOnFailure(RouteExcepcion exception) {
        for (RoutingListener mListener : alisteners) {
            mListener.onRoutingFailure(exception);
        }
    }

    protected void dispatchOnStart() {
        for (RoutingListener mListener : alisteners) {
            mListener.onRoutingStart();
        }
    }

    private void dispatchOnCancelled() {
        for (RoutingListener mListener : alisteners) {
            mListener.onRoutingCancelled();
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            //Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            //Log.e("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

}
