package com.proyecto1.william.proyecto1.GoogleMaps;

import com.proyecto1.william.proyecto1.Entidad.Rutas;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Usuario on 17/02/2018.
 * Interfaz creado para interatuar entre clases para la busqueda de listado_rutas
 */

public interface RoutingListener {

    void onRoutingStart();
    void onRoutingFailure(RouteExcepcion e);
    void onRoutingSuccess(List<Rutas> lists);
    void onRoutingCancelled();
}
