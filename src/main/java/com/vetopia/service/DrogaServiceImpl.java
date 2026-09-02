package com.vetopia.service;

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
        return List.copyOf(drogaRepository.searchAll());
    }

    @Override
    public Droga obtenerDrogaPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("No se especificó el identificador de la droga.");
        }
        return drogaRepository.searchById(id);
    }

    @Override
    public void guardar(Droga droga) {
        drogaRepository.save(droga);
    }

    @Override
    public void descontarUnidades(Integer drogaId, Integer cantidad) {
        drogaRepository.descontarUnidades(drogaId, cantidad);
    }
}
