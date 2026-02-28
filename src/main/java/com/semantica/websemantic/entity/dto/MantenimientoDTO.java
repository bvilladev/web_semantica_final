package com.semantica.websemantic.entity.dto;


import com.semantica.websemantic.enums.TipoMantenimiento;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MantenimientoDTO {
    private Long id;
    private LocalDate fecha;
    private TipoMantenimiento tipoMantenimiento;
    private Double costoTotal;
    private String observaciones;
    private VehiculoDTO vehiculo;
    private MecanicoDTO mecanico;
    List<ServicioDTO> servicios;
}
