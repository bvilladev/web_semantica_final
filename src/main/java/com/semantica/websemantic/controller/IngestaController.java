package com.semantica.websemantic.controller;


import com.semantica.websemantic.dto.MantenimientoDTO;
import com.semantica.websemantic.dto.MecanicoDTO;
import com.semantica.websemantic.dto.ServicioDTO;
import com.semantica.websemantic.dto.VehiculoDTO;
import com.semantica.websemantic.service.SemanticService;
import com.semantica.websemantic.service.TallerClient;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/semantica/ingesta")
public class IngestaController {

    private final SemanticService semanticService;

    @Autowired
    private TallerClient tallerClient;

    public IngestaController(SemanticService semanticService) {
        this.semanticService = semanticService;
    }

    @GetMapping
    public String hola(){
        return "hola bnn";
    }

    @PostMapping("/cargar-todo")
    public ResponseEntity<String> cargarDatosDesdeBaseDeDatos() {
        // 1. Traer datos de todas las 4 tablas
        List<VehiculoDTO> vehiculos = tallerClient.obtenerTodosLosVehiculos();
        List<MecanicoDTO> mecanicos = tallerClient.obtenerTodosLosMecanicos();
        List<MantenimientoDTO> mantenimientos = tallerClient.obtenerTodosLosMantenimientos();

        // NUEVO: Traer los servicios
        List<ServicioDTO> servicios = tallerClient.obtenerTodosLosServicios();

        if(!vehiculos.isEmpty()) {
            log.info("Datos obtenidos de vehículo de prueba: {}", vehiculos.get(0).getPlaca());
        }

        // 2. Procesar (Convertir a Tripletas RDF)
        // IMPORTANTE: Primero procesamos las entidades maestras (Vehiculo, Mecanico, Servicio)
        for (VehiculoDTO v : vehiculos) semanticService.procesarVehiculo(v);
        for (MecanicoDTO m : mecanicos) semanticService.procesarMecanico(m);
        for (ServicioDTO s : servicios) semanticService.procesarServicio(s); // NUEVO

        // Al final procesamos el Mantenimiento, porque es el que conecta a los demás
        for (MantenimientoDTO mant : mantenimientos) semanticService.procesarMantenimiento(mant);

        return ResponseEntity.ok("Carga completa. Tablas procesadas: Vehículos, Mecánicos, Servicios y Mantenimientos.");
    }
}