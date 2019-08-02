package com.proyecto1.william.proyecto1.GoogleMaps;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Usuario on 17/02/2018.
 */

public class RouteExcepcion extends Exception{

    private static final String TAG = "RouteException";
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "error_message";

    private String statusCode;
    private String message;

    public RouteExcepcion(JSONObject json){
        if(json == null){
            statusCode = "";
            message = "Parsing error";
            return;
        }
        try {
            statusCode = json.getString(KEY_STATUS);
            message = json.getString(KEY_MESSAGE);
        } catch (JSONException e) {
            //Log.e(TAG, "JSONException while parsing RouteException argument. Msg: " + e.getMessage());
        }
    }

    public RouteExcepcion(String msg){
        message = msg;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getStatusCode() {
        return statusCode;
    }
}
