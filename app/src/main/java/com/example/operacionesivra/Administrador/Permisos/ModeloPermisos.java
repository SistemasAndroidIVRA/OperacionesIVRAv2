package com.example.operacionesivra.Administrador.Permisos;

public class ModeloPermisos {
    String area,nombre,idpermiso,descripcion,usuario;
    boolean check;

    public ModeloPermisos(String area, String nombre, String idpermiso, String descripcion,String usuario, boolean check) {
        this.area = area;
        this.nombre = nombre;
        this.idpermiso = idpermiso;
        this.descripcion = descripcion;
        this.usuario = usuario;
        this.check = check;
    }

    public String getArea() {
        return area;
    }

    public String getNombre() {
        return nombre;
    }

    public String getIdpermiso() {
        return idpermiso;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getUsuario(){
        return usuario;
    }

    public boolean isCheck(){
        return check;
    }
}
