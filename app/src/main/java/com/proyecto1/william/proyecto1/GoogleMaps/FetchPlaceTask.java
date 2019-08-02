package com.proyecto1.william.proyecto1.GoogleMaps;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.proyecto1.william.proyecto1.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Usuario on 27/03/2018.
 */

public class FetchPlaceTask extends AsyncTask<Location, Void, String> {

    private final String TAG = FetchPlaceTask.class.getSimpleName();
    private Context mContext;
    private OnTaskCompleted mListener;


    public FetchPlaceTask(Context applicationContext , OnTaskCompleted listener) {
        mContext = applicationContext;
        mListener=listener;
    }

    public interface OnTaskCompleted {
        void onTaskCompleted(String result);
    }


    @Override
    protected String doInBackground(Location... params) {

        Geocoder geocoder = new Geocoder(mContext,
                Locale.getDefault());
        Location location = params[0];
        List<Address> addresses = null;
        String resultMessage = "";

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems
            resultMessage = mContext.getString(R.string.serviceFalse);
            Toast.makeText(mContext,resultMessage,Toast.LENGTH_LONG).show();
            //Log.e(TAG, resultMessage, ioException);
        }catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values
            resultMessage = mContext.getString(R.string.geocoderFalse);
            Toast.makeText(mContext,resultMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(),Toast.LENGTH_LONG).show();
            /*Log.e(TAG, resultMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);*/
        }

        if (addresses == null || addresses.size() == 0) {
            if (resultMessage.isEmpty()) {
                resultMessage = mContext.getString(R.string.addressesFalse);
                //Log.d(TAG, resultMessage);
                Toast.makeText(mContext,resultMessage,Toast.LENGTH_LONG).show();
            }
        }else {
            // If an address is found, read it into resultMessage
            Address address = addresses.get(0);
            ArrayList<String> addressParts = new ArrayList<>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressParts.add(address.getAddressLine(i));
            }

            //resultMessage = TextUtils.join("\n", addressParts);
            resultMessage = TextUtils.join("", addressParts);
        }

        return resultMessage;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        mListener.onTaskCompleted(result);
    }
}
