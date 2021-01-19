package com.example.operacionesivra.Inventario.ConteosPausa;

public class Modelo_conteos_pausa {
    String fecha, bloqueado,material,folio,usuario,stocktotal, almacen;
    int contador;

    public Modelo_conteos_pausa(int contador, String fecha, String bloqueado, String material, String folio, String usuario, String stocktotal,String almacen) {
        this.fecha = fecha;
        this.bloqueado = bloqueado;
        this.material = material;
        this.folio = folio;
        this.usuario = usuario;
        this.contador = contador;
        this.stocktotal = stocktotal;
        this.almacen = almacen;
    }

    public int getContador(){
        return contador;
    }

    public String getFecha() {
        return fecha;
    }

    public String getBloqueado() {
        return bloqueado;
    }

    public String getMaterial() {
        return material;
    }

    public String getFolio() {
        return folio;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getStocktotal(){
        return stocktotal;
    }

    public String getAlmacen(){
        return almacen;
    }
}
