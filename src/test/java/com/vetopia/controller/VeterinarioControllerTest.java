package com.vetopia.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetopia.entities.Veterinario;
import com.vetopia.entities.mascota.dominio_attr.DominioEstado;
import com.vetopia.rest.VeterinarioRestController;
import com.vetopia.service.veterinario.VeterinarioService;

@WebMvcTest(VeterinarioRestController.class)
@ActiveProfiles("test")
public class VeterinarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VeterinarioService veterinarioService;

    // GET (listar todos) 
    @Test
    public void VeterinarioController_listarVeterinarios_ReturnsList() throws Exception {
        Veterinario veterinario1 = new Veterinario(
            "1234567890", "vet123", "doctor@vetopia.com", "Medicina general",
            0, "Carlos Pérez", DominioEstado.ACTIVO, "https://example.com/foto.jpg");
        veterinario1.setId(1L);

        Veterinario veterinario2 = new Veterinario(
            "0987654321", "vet456", "doctora@vetopia.com", "Cirugía",
            0, "Laura Gómez", DominioEstado.ACTIVO, "https://example.com/foto2.jpg");
        veterinario2.setId(2L);

        when(veterinarioService.listarVeterinarios()).thenReturn(List.of(veterinario1, veterinario2));

        ResultActions response = mockMvc.perform(
            get("/veterinario/all")
            .contentType("application/json"));

        response.andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].nombre").value(veterinario1.getNombre()))
        .andExpect(jsonPath("$[1].nombre").value(veterinario2.getNombre()));
    }

    // GET (buscar por id) 
    @Test
    public void VeterinarioController_buscarPorId_Veterinario() throws Exception {
        Veterinario veterinario = new Veterinario(
            "1234567890", "vet123", "doctor@vetopia.com", "Medicina general",
            0, "Carlos Pérez", DominioEstado.ACTIVO, "https://example.com/foto.jpg");
        veterinario.setId(1L);

        when(veterinarioService.obtenerVeterinarioPorId(1L)).thenReturn(veterinario);

        ResultActions response = mockMvc.perform(
            get("/veterinario/{id}", 1L)
            .contentType("application/json"));

        response.andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.nombre").value(veterinario.getNombre()))
        .andExpect(jsonPath("$.cedula").value(veterinario.getCedula()))
        .andExpect(jsonPath("$.correo").value(veterinario.getCorreo()));
    }

    // POST (agregar) 
    @Test
    public void VeterinarioController_agregarVeterinario_Veterinario() throws Exception {
        Veterinario veterinario = new Veterinario(
            "1234567890", "vet123", "doctor@vetopia.com", "Medicina general",
            0, "Carlos Pérez", DominioEstado.ACTIVO, "https://example.com/foto.jpg");

        // guardar() es void, por eso se usa doNothing() en lugar de when(...).thenReturn(...)
        doNothing().when(veterinarioService).guardar(Mockito.any());

        ResultActions response = mockMvc.perform(
            post("/veterinario")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(veterinario)));

        response.andExpect(status().isCreated())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.nombre").value(veterinario.getNombre()))
        .andExpect(jsonPath("$.cedula").value(veterinario.getCedula()))
        .andExpect(jsonPath("$.correo").value(veterinario.getCorreo()));
    }

    // PUT (actualizar) 
    @Test
    public void VeterinarioController_actualizarVeterinario_Veterinario() throws Exception {
        Veterinario existente = new Veterinario(
            "1234567890", "vet123", "doctor@vetopia.com", "Medicina general",
            0, "Carlos Pérez", DominioEstado.ACTIVO, "https://example.com/foto.jpg");
        existente.setId(1L);

        Veterinario cambios = new Veterinario(
            "1234567890", "vet123", "doctor@vetopia.com", "Medicina general",
            0, "Carlos Pérez Modificado", DominioEstado.ACTIVO, "https://example.com/foto.jpg");

        when(veterinarioService.obtenerVeterinarioPorId(1L)).thenReturn(existente);
        doNothing().when(veterinarioService).guardar(Mockito.any());

        ResultActions response = mockMvc.perform(
            put("/veterinario/{id}", 1L)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(cambios)));

        // El controlador fuerza el id de la ruta sobre el objeto recibido
        response.andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.nombre").value(cambios.getNombre()))
        .andExpect(jsonPath("$.cedula").value(cambios.getCedula()));
    }

    // DELETE (eliminar) 
    @Test
    public void VeterinarioController_eliminarVeterinario_NoContent() throws Exception {
        doNothing().when(veterinarioService).eliminarVeterinario(1L);

        ResultActions response = mockMvc.perform(
            delete("/veterinario/{id}", 1L)
            .contentType("application/json"));

        response.andExpect(status().isNoContent());
    }

    // GET (contar activos) 
    @Test
    public void VeterinarioController_contarActivos_ReturnsCount() throws Exception {
        when(veterinarioService.contarVeterinariosActivos()).thenReturn(5L);

        ResultActions response = mockMvc.perform(
            get("/veterinario/activos/count")
            .contentType("application/json"));

        response.andExpect(status().isOk())
        .andExpect(content().string("5"));
    }
}