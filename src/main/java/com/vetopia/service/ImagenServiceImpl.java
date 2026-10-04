package com.vetopia.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/*
 * Implementa el guardado de fotos de mascotas en el directorio
 * src/main/resources/static/images/mascotas
 */
@Service
public class ImagenServiceImpl implements ImagenService {

    private static final String DIRECTORIO_IMAGENES = "src/main/resources/static/images/mascotas";

    // Guarda la foto subida en el directorio de imágenes de mascotas y 
    // devuelve la ruta pública para servirla. Si no hay foto, devuelve null
    @Override
    public String guardarFoto(MultipartFile foto) {
        // Validamos que la foto no sea nula ni esté vacía antes de intentar guardarla
        if (foto == null || foto.isEmpty()) {
            return null;
        }
        try {
            String extension = "";
            String nombreOriginal = foto.getOriginalFilename();
            // Extraemos la extensión del archivo original si existe
            if (nombreOriginal != null && nombreOriginal.contains(".")) {
                extension = nombreOriginal.substring(nombreOriginal.lastIndexOf('.'));
            }
            String nombreArchivo = System.currentTimeMillis() + extension;
            Path directorio = Paths.get(DIRECTORIO_IMAGENES);
            Files.createDirectories(directorio);
            Files.copy(foto.getInputStream(), directorio.resolve(nombreArchivo),
                    StandardCopyOption.REPLACE_EXISTING);
            return "/images/mascotas/" + nombreArchivo;
        } catch (IOException excepcion) {
            return null;
        }
    }
}