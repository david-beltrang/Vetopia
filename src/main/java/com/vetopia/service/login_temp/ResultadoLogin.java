package com.vetopia.service.login_temp;

public class ResultadoLogin {
    private Long id;
    private String rol;
    private String correo;
    private String contrasena;

    // Constructor para inicializar los campos de la clase ResultadoLogin
    public ResultadoLogin(Long id, String rol, String correo, String contrasena) {
        this.id = id;
        this.rol = rol;
        this.correo = correo;
        this.contrasena = contrasena;
    }

    // Identificador del usuario autenticado (veterinario, administrador o dueño)
    public Long getId() {
        return id;
    }

    // Establece el identificador del usuario autenticado
    public String getRol() {
        return rol;
    }

    // Establece el rol del usuario autenticado (VETERINARIO, ADMINISTRADOR o CLIENTE)
    public void setRol(String rol) {
        this.rol = rol;
    }

    // Devuelve el correo del usuario autenticado
    public String getCorreo() {
        return correo;
    }

    //  Establece el correo del usuario autenticado
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    // Devuelve la contraseña del usuario autenticado
    public String getContrasena() {
        return contrasena;
    }

    // Establece la contraseña del usuario autenticado
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}