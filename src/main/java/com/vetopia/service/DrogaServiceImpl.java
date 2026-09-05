package com.vetopia.service;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Droga;
import com.vetopia.repository.DrogaRepository;

// Esta clase implementa la interfaz DrogaService y define la lógica de negocio relacionada con drogas/medicamentos. Es el único punto al que los controladores acceden para obtener datos; a su vez, delega el acceso a datos en la capa repository.
@Service
public class DrogaServiceImpl implements DrogaService {

    //Inyección de dependencias
    @Autowired
    private DrogaRepository drogaRepository;

    @Override
    public List<Droga> listarDrogas() {
        Collection<Droga> drogas = drogaRepository.searchAll();
        return drogas == null ? List.of() : List.copyOf(drogas);
    }

    // Obtiene una droga validando previamente el identificador
    @Override
    public Droga obtenerDrogaPorId(Integer id) {
        // Validamos que el id no sea nulo antes de buscar la droga en el repositorio
        if (id == null) {
            throw new IllegalArgumentException("No se especificó el identificador de la droga.");
        }
        Droga droga = drogaRepository.searchById(id);
        if (droga == null) {
            throw new IllegalArgumentException("No se encontró ninguna droga con el identificador " + id + ".");
        }
        return droga;
    }

    // Guarda (registra) una droga nueva. El id lo asigna el repositorio
    @Override
    public void guardar(Droga droga) {
        if (droga == null) {
            throw new IllegalArgumentException("No se especificó la droga a guardar.");
        }
        drogaRepository.save(droga);
    }

    // Descuenta unidades del inventario de la droga (al asignar un tratamiento)
    @Override
    public void descontarUnidades(Integer drogaId, Integer cantidad) {
        if (drogaId == null || cantidad == null) {
            throw new IllegalArgumentException("Debe indicar la droga y la cantidad a descontar.");
        }
        drogaRepository.descontarUnidades(drogaId, cantidad);
    }
}
