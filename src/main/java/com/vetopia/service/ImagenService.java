package com.vetopia.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Servicio de persistencia de imágenes de mascotas en el sistema de archivos.
 */
public interface ImagenService {

    /**
     * Guarda la foto subida en el directorio de imágenes de mascotas y
     * devuelve la ruta pública para servirla. Si no hay foto, devuelve null.
     */
    String guardarFoto(MultipartFile foto);
}