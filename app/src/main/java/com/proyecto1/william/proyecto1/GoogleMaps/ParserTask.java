package com.proyecto1.william.proyecto1.GoogleMaps;

import android.util.Log;

import com.proyecto1.william.proyecto1.Entidad.Rutas;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Usuario on 17/02/2018.
 */

public class ParserTask implements Parser {

    @Override
    public List<Rutas> parse() throws RouteExcepcion {

        JSONObject jObject;
        List<Rutas> routes = null;

        try {
            jObject = new JSONObject();
            //Log.d("ParserTask",jsonData.toString());
            DirectionsJSONParser parser = new DirectionsJSONParser();
            //Log.d("DataParser", parser.toString());

            // Starts parsing data
            routes = parser.parse(jObject);
            //Log.d("ParserTask routes","Executing routes");
            //Log.d("ParserTask routes",routes.toString());

        } catch (Exception e) {
            //Log.e("ParserTask",e.toString());
            e.printStackTrace();
        }
        return routes;
    }


}
