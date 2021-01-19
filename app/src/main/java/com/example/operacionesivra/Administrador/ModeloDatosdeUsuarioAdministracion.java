package com.example.operacionesivra.Administrador;

import com.example.operacionesivra.Monitoreo.ModeloDatosUsuario;

public class ModeloDatosdeUsuarioAdministracion {
    String nombre,usuario,password,area,ubicacion,idusuario;

    public ModeloDatosdeUsuarioAdministracion(String nombre, String usuario,String password, String area, String ubicacion, String idusuario) {
        this.nombre = nombre;
        this.usuario = usuario;
        this.password = password;
        this.area = area;
        this.ubicacion = ubicacion;
        this.idusuario = idusuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getPassword(){
        return password;
    }

    public String getArea() {
        return area;
    }

    public String getIdusuario(){
        return idusuario;
    }

    public String getUbicacion() {
        return ubicacion;
    }
}
