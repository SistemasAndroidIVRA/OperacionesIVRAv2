package com.example.operacionesivra.Vistas.Inventario.InventariosCerrados;

public class ModeloInventariosCerrados {

    String usuario, fecha, material, folio, almacen, fisico, sistema, diferencia;
    int estadoIncidencia;

    public ModeloInventariosCerrados(String fehca, String usuario, String material, String fisico, String sistema, String diferencia, String folio, String almacen, int estadoIncidencia) {
        this.usuario = usuario;
        this.fecha = fehca;
        this.material = material;
        this.fisico = fisico;
        this.sistema = sistema;
        this.diferencia = diferencia;
        this.folio = folio;
        this.almacen = almacen;
        this.estadoIncidencia = estadoIncidencia;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getFecha() {
        return fecha;
    }

    public String getMaterial() {
        return material;
    }

    public String getFolio() {
        return folio;
    }

    public String getAlmacen() {
        return almacen;
    }

    public String getFisico() {
        return fisico;
    }

    public String getSistema() {
        return sistema;
    }

    public String getDiferencia() {
        return diferencia;
    }

    public int getEstadoIncidencia(){return estadoIncidencia;}
}