package com.semantica.websemantic.dto;

import lombok.Data;

@Data
public class VehiculoDTO {
    private Long id;
    private String marca;
    private String modelo;
    private String color;
    private String placa;
    private String nombrePropietario;
}