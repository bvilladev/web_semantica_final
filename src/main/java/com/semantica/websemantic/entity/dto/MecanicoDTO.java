package com.semantica.websemantic.entity.dto;


import lombok.Data;


@Data
public class MecanicoDTO {
    private Long id;

    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private String especialidad;

}