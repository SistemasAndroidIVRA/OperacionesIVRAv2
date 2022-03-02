package com.example.operacionesivra.Vistas.Inventario.InventariosHistoricos;

public class ModeloInventariosHistoricos {
    String usuario;
    String fecha;
    String material;
    String folio;
    String almacen;
    String totalregistrado;
    String diferencia;
    String horaInicio;
    String horaFin;
    String entradas;

    public ModeloInventariosHistoricos(String usuario, String fecha, String material, String folio, String almacen, String totalregistrado, String diferencia, String horaInicio, String horaFin, String entradas) {
        this.usuario = usuario;
        this.fecha = fecha;
        this.material = material;
        this.folio = folio;
        this.almacen = almacen;
        this.totalregistrado = totalregistrado;
        this.diferencia = diferencia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.entradas = entradas;
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

    public String getTotalRegistrado(){ return totalregistrado;}

    public String getDiferencia() {
        return diferencia;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public String getEntradas() {
        return entradas;
    }

}
