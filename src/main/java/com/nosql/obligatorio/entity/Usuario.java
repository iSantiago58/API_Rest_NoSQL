package com.nosql.obligatorio.entity;

// -- La libreria de lombok.Data genera los getters y setters automaticamente
import lombok.Data;

import java.util.ArrayList;

@Data
public class Usuario {
    private String correo;
    private String pass;
    private String nombre;
    private String apellido;
    private ArrayList<String> roles;

}
