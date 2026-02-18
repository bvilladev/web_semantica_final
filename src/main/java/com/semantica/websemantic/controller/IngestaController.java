package com.semantica.websemantic.controller;


import com.semantica.websemantic.dto.MantenimientoDTO;
import com.semantica.websemantic.dto.MecanicoDTO;
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
@RequestMapping("/api/semantica/ingesta") // Prefijo base DIFERENTE o específico
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

    // Endpoint principal para cargar todo desde MySQL
    @PostMapping("/cargar-todo")
    public ResponseEntity<String> cargarDatosDesdeBaseDeDatos() {
        // 1. Traer datos
        List<VehiculoDTO> vehiculos = tallerClient.obtenerTodosLosVehiculos();
        List<MecanicoDTO> mecanicos = tallerClient.obtenerTodosLosMecanicos();
        List<MantenimientoDTO> mantenimientos = tallerClient.obtenerTodosLosMantenimientos();


        log.error("Datos obtenidos:{}", vehiculos.get(2).getPlaca());

        // 2. Procesar
        for (VehiculoDTO v : vehiculos) semanticService.procesarVehiculo(v);
        for (MecanicoDTO m : mecanicos) semanticService.procesarMecanico(m);
        for (MantenimientoDTO mant : mantenimientos) semanticService.procesarMantenimiento(mant);

        return ResponseEntity.ok("Carga completa.");
    }
}