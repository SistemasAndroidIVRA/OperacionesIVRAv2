package com.example.operacionesivra.Monitoreo;

public class ModeloTipoUsuario {
    String tipo,idusuario;

    public ModeloTipoUsuario(String tipo, String idusuario) {
        this.tipo = tipo;
        this.idusuario = idusuario;
    }

    public String getTipo() {
        return tipo;
    }

    public String getIdusuario() {
        return idusuario;
    }
}
