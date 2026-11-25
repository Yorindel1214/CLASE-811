package co.com.crediya.model.autenticacion.gateways;

import co.com.crediya.model.autenticacion.Sesion;
import co.com.crediya.model.usuario.Usuario;

/**
 * Puerto de salida: crear el token (JWT) con el que el usuario demuestra quién es y qué rol tiene.
 */
public interface GeneradorToken {
    Sesion generar(Usuario usuario);
}
