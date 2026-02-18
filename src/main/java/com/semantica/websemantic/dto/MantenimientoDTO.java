package com.semantica.websemantic.dto;


import lombok.Data;

@Data
public class MantenimientoDTO {
    private Long id;
    private String descripcion;
    private VehiculoDTO vehiculo;
    private MecanicoDTO mecanico;
}
