package com.semantica.websemantic.dto;

import lombok.Data;

@Data
public class ServicioDTO {
    private Long id;

    private String nombre;
    private Double costo;
    private Double duracion;
    private String observacion;
}
