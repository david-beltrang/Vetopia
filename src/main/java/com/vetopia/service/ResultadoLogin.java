package com.vetopia.service;

public class ResultadoLogin {
    private Integer id;
    private String rol;
    private String correo;
    private String contrasena;

    public ResultadoLogin(Integer id, String rol, String correo, String contrasena) {
        this.id = id;
        this.rol = rol;
        this.correo = correo;
        this.contrasena = contrasena;
    }

    // Identificador del usuario autenticado (veterinario, administrador o dueño).
    public Integer getId() {
        return id;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
    
}
