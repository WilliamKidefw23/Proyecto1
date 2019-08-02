package com.proyecto1.william.proyecto1.Entidad;

/**
 * Created by Usuario on 19/02/2018.
 */

public class Usuario {

    private String idUsuario;
    private String nombres;
    private String apellidop;
    private String apellidom;
    private String login;
    private String password;
    private String creado;

    public Usuario(String idUsuario,String login) {
        this.idUsuario = idUsuario;
        this.login = login;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidop() {
        return apellidop;
    }

    public void setApellidop(String apellidop) {
        this.apellidop = apellidop;
    }

    public String getApellidom() {
        return apellidom;
    }

    public void setApellidom(String apellidom) {
        this.apellidom = apellidom;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreado() {
        return creado;
    }

    public void setCreado(String creado) {
        this.creado = creado;
    }
}
