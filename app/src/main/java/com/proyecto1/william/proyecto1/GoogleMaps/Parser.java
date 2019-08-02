package com.proyecto1.william.proyecto1.GoogleMaps;

import com.proyecto1.william.proyecto1.Entidad.Rutas;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Usuario on 17/02/2018.
 */

public interface Parser {
    List<Rutas> parse() throws RouteExcepcion;
}
