package com.proyecto1.william.proyecto1.GoogleMaps;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.proyecto1.william.proyecto1.Entidad.Rutas;
import com.proyecto1.william.proyecto1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GoogleParser extends XMLParser implements Parser {

    private static final String VALUE = "value";
    private static final String DISTANCE = "distance";
    private static final String DURATION= "duration";
    private static Context context;
    private int distance;

    /* Status code returned when the request succeeded */
    private static final String OK = "OK";

    public GoogleParser(String feedUrl) {
        super(feedUrl);
    }

    public final List<Rutas> parse() throws RouteExcepcion {
        List<Rutas> routes = new ArrayList<>();

        // turn the stream into a string
        final String result = convertStreamToString(this.getInputStream());
        if (result == null) {
            throw new RouteExcepcion(context.getString(R.string.parserFalse));
        }

        //Log.d("GoogleParser",result);

        try {
            //Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            //Get the route object

            if(!json.getString("status").equals(OK)){
                throw new RouteExcepcion(json);
            }

            JSONArray jsonRoutes = json.getJSONArray("routes");
            //Log.d("Routes",String.valueOf(jsonRoutes.length()));

            for (int i = 0; i < jsonRoutes.length(); i++) {
                Rutas ruta = new Rutas();
                //Create an empty segment
                Segment segment = new Segment();

                JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
                //Get the bounds - northeast and southwest
                final JSONObject jsonBounds = jsonRoute.getJSONObject("bounds");
                final JSONObject jsonNortheast = jsonBounds.getJSONObject("northeast");
                final JSONObject jsonSouthwest = jsonBounds.getJSONObject("southwest");

                //route.setLatLgnBounds(new LatLng(jsonNortheast.getDouble("lat"), jsonNortheast.getDouble("lng")), new LatLng(jsonSouthwest.getDouble("lat"), jsonSouthwest.getDouble("lng")));

                //Get the leg, only one leg as we don't support waypoints
                final JSONObject leg = jsonRoute.getJSONArray("legs").getJSONObject(0);
                //Get the steps for this leg
                final JSONArray steps = leg.getJSONArray("steps");
                //Number of steps for use in for loop
                final int numSteps = steps.length();
                //Set the name of this route using the start & end addresses
                //route.setName(leg.getString("start_address") + " to " + leg.getString("end_address"));
                //Get google's copyright notice (tos requirement)
                //route.setCopyright(jsonRoute.getString("copyrights"));
                //Get distance and time estimation
                ruta.setDuracion(leg.getJSONObject(DURATION).getString("text"));
                ruta.setDuracionvalor(leg.getJSONObject(DURATION).getInt(VALUE));
                ruta.setDistancia(leg.getJSONObject(DISTANCE).getString("text"));
                ruta.setDistanciavalor(leg.getJSONObject(DISTANCE).getInt(VALUE));
                //route.setEndAddressText(leg.getString("end_address"));
                //Get the total length of the route.
                //route.setLength(leg.getJSONObject(DISTANCE).getInt(VALUE));
                //Get any warnings provided (tos requirement)
                if (!jsonRoute.getJSONArray("warnings").isNull(0)) {
                    //route.setWarning(jsonRoute.getJSONArray("warnings").getString(0));
                }

                /* Loop through the steps, creating a segment for each one and
                 * decoding any polylines found as we go to add to the route object's
                 * map array. Using an explicit for loop because it is faster!
                 */
                for (int y = 0; y < numSteps; y++) {
                    //Get the individual step
                    final JSONObject step = steps.getJSONObject(y);
                    //Get the start position for this step and set it on the segment
                    final JSONObject start = step.getJSONObject("start_location");
                    final LatLng position = new LatLng(start.getDouble("lat"),
                            start.getDouble("lng"));
                    segment.setPoint(position);

                    //travel mode
                    ruta.setTravelmode(step.getString("travel_mode"));
                    //Set the length of this segment in metres
                    final int length = step.getJSONObject(DISTANCE).getInt(VALUE);
                    distance += length;
                    segment.setLength(length);
                    segment.setDistance((double)distance / (double)1000);
                    //Strip html from google directions and set as turn instruction
                    segment.setInstruction(step.getString("html_instructions").replaceAll("<(.*?)*>", ""));
                    
                    if(step.has("maneuver"))
                        segment.setManeuver(step.getString("maneuver"));
                    
                    //Retrieve & decode this segment's polyline and add it to the route.
                    ruta.addRutas(decodePolyLine(step.getJSONObject("polyline").getString("points")));
                    //Push a copy of the segment to the route
                    //route.addSegment(segment.copy());
                }
                routes.add(ruta);
            }

        } catch (JSONException e) {
            throw new RouteExcepcion("JSONException. Msg: "+e.getMessage());
        }
        return routes;
    }

    private static String convertStreamToString(final InputStream input) {
        if (input == null) return null;

        final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        final StringBuilder sBuf = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sBuf.append(line);
            }
        } catch (IOException e) {
            //Log.e("Routing Error", e.getMessage());
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();

        } finally {
            try {
                input.close();
                reader.close();
            } catch (IOException e) {
                //Log.e("Routing Error", e.getMessage());
                Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
        return sBuf.toString();
    }

    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }

}
