package com.example.operacionesivra.Reportes.Inventario;

public class ModeloItemsInventarioReporte {
    String total,stocktotal,material,fecha,folio,horainicio,horafin;

    public ModeloItemsInventarioReporte(String total, String stocktotal, String material, String fecha, String folio, String horainicio, String horafin) {
        this.total = total;
        this.stocktotal = stocktotal;
        this.material = material;
        this.fecha = fecha;
        this.folio = folio;
        this.horainicio = horainicio;
        this.horafin = horafin;
    }

    public String getTotal() {
        return total;
    }

    public String getStocktotal() {
        return stocktotal;
    }

    public String getMaterial() {
        return material;
    }

    public String getFecha() {
        return fecha;
    }

    public String getFolio() {
        return folio;
    }

    public String getHorainicio() {
        return horainicio;
    }

    public String getHorafin() {
        return horafin;
    }
}
