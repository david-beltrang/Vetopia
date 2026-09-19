package com.vetopia.service.login_temp;

//Por ahora se realiza una autenticación simple.
public interface LoginService {
    // Retorna un objeto ResultadoLogin que contiene el rol, correo y contraseña del usuario autenticado.
    ResultadoLogin autenticar(String correo, String contrasena);
}
