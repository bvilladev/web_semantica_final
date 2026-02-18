package com.semantica.websemantic.dto;

import lombok.Data;

@Data
public class VehiculoDTO {
    private Long id; // Importante para la URI
    private String placa;
    private String marca;
    private String modelo;
    // Getters y Setters
}