package com.semantica.websemantic.service;

import com.semantica.websemantic.dto.MantenimientoDTO;
import com.semantica.websemantic.dto.MecanicoDTO;
import com.semantica.websemantic.dto.ServicioDTO;
import com.semantica.websemantic.dto.VehiculoDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;

@Service
public class TallerClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "http://localhost:8080/api/v1"; // URL de tu Taller original

    public List<VehiculoDTO> obtenerTodosLosVehiculos() {
        // Llama a tu endpoint GET existente [Línea 17 de controllers.txt]
        VehiculoDTO[] response = restTemplate.getForObject(BASE_URL + "/vehiculos", VehiculoDTO[].class);
        return Arrays.asList(response);
    }

    public List<MecanicoDTO> obtenerTodosLosMecanicos() {
        // Llama a tu endpoint GET existente [Línea 9 de controllers.txt]
        MecanicoDTO[] response = restTemplate.getForObject(BASE_URL + "/mecanicos", MecanicoDTO[].class);
        return Arrays.asList(response);
    }

    public List<MantenimientoDTO> obtenerTodosLosMantenimientos() {
        // Llama a tu endpoint GET existente [Línea 4 de controllers.txt]
        MantenimientoDTO[] response = restTemplate.getForObject(BASE_URL + "/mantenimiento", MantenimientoDTO[].class);
        return Arrays.asList(response);
    }
    public List<ServicioDTO> obtenerTodosLosServicios() {
        // Llama al endpoint GET /api/v1/servicio de tu proyecto original
        ServicioDTO[] response = restTemplate.getForObject(BASE_URL + "/servicio", ServicioDTO[].class);
        return Arrays.asList(response);
    }
}