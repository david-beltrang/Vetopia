package com.vetopia.service.droga;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vetopia.entities.Droga;
import com.vetopia.exception.ParametroInvalidoException;
import com.vetopia.exception.RecursoNoEncontradoException;
import com.vetopia.repository.DrogaRepository;

// Esta clase implementa la interfaz DrogaService y define la lógica de negocio relacionada con drogas/medicamentos. Es el único punto al que los controladores acceden para obtener datos; a su vez, delega el acceso a datos en la capa repository.
@Service
public class DrogaServiceImpl implements DrogaService {

    //Inyección de dependencias
    @Autowired
    private DrogaRepository drogaRepository;

    @Override
    public List<Droga> listarDrogas() {
        return drogaRepository.findAll();
    }

    // Obtiene una droga validando previamente el identificador
    @Override
    public Droga obtenerDrogaPorId(Long id) {
        // Validamos que el id no sea nulo antes de buscar la droga en el repositorio
        if (id == null) {
            throw new ParametroInvalidoException("No se especificó el identificador de la droga.");
        }
        return drogaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró ninguna droga con el identificador " + id + "."));
    }

    // Guarda (registra) una droga nueva. El id lo asigna la base de datos
    @Override
    public void guardar(Droga droga) {
        if (droga == null) {
            throw new ParametroInvalidoException("No se especificó la droga a guardar.");
        }
        drogaRepository.save(droga);
    }

    // Descuenta unidades del inventario de la droga por esto va en @Transactional
    @Override
    @Transactional
    public void descontarUnidades(Long drogaId, Integer cantidad) {
        if (drogaId == null || cantidad == null) {
            throw new ParametroInvalidoException("Debe indicar la droga y la cantidad a descontar.");
        }
        Droga droga = drogaRepository.findById(drogaId).orElse(null);
        if (droga == null || cantidad <= 0) {
            return;
        }
        if (droga.getUnidadesDisponibles() != null) {
            droga.setUnidadesDisponibles(Math.max(0, droga.getUnidadesDisponibles() - cantidad));
        }
        if (droga.getUnidadesVendidas() != null) {
            droga.setUnidadesVendidas(droga.getUnidadesVendidas() + cantidad);
        }
        drogaRepository.save(droga);
    }

    // Elimina una droga validando que exista.
    @Override
    @Transactional
    public void eliminarDroga(Long id) {
        obtenerDrogaPorId(id);
        drogaRepository.deleteById(id);
    }
}