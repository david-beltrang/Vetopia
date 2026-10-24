package com.vetopia.dto;

/**
 * Credenciales que envía Angular en el cuerpo del login. Es un DTO y no la
 * entidad porque al iniciar sesión no llega un usuario completo, solo el
 * correo y la contraseña. El servicio de login tampoco estaba pensado para
 * recibir un objeto, así que aquí se reúne lo mínimo que necesita.
 */
public class LoginRequestDTO {

    private String correo;
    private String contrasena;

    // Constructor vacío y setters: Jackson los necesita para armar el objeto
    // a partir del JSON que llega en el @RequestBody.
    public LoginRequestDTO() {
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
