package com.proyecto1.william.proyecto1.Settings;

import android.app.FragmentTransaction;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;

import com.proyecto1.william.proyecto1.R;

import java.util.List;

public class SettingsActivity extends PreferenceActivity {

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(android.R.id.content, new SettingsFragment());
        ft.commit();
    }*/

    @Override
    protected boolean isValidFragment(String fragmentName) {
        // Comprobar que el fragmento esté relacionado con la actividad
        return SettingsFragment.class.getName().equals(fragmentName);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    @Override
    public boolean onIsMultiPane() {
        // Determinar que siempre sera multipanel
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        return ((float)metrics.densityDpi / (float)metrics.widthPixels) < 0.30;
    }
}
