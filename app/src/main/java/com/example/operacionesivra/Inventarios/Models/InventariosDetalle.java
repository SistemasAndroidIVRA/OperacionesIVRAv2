package com.example.operacionesivra.Inventarios.Models;

public class InventariosDetalle {
    String folio, registroID, cantidad, longitud, ubicacion, incidencia, estado, ubicacionId, productoID, codigoViejo, codigoNuevo;
    int isSelected, estadoEtiqueta, status;

    //ConstructorPrincipal
    public InventariosDetalle(String folio, String cantidad, String longitud, String ubicacion, String incidencia, String estado, String ubicacionId, String productoID, String codigoViejo, String codigoNuevo, int isSelected, int estadoEtiqueta) {
        this.folio = folio;
        this.cantidad = cantidad;
        this.longitud = longitud;
        this.ubicacion = ubicacion;
        this.incidencia = incidencia;
        this.estado = estado;
        this.ubicacionId = ubicacionId;
        this.productoID = productoID;
        this.codigoViejo = codigoViejo;
        this.codigoNuevo = codigoNuevo;
        this.isSelected = isSelected;
        this.estadoEtiqueta = estadoEtiqueta;
    }

    //Constructor editar
    public InventariosDetalle(String folio, String registroID, String cantidad, String longitud, String ubicacion, String incidencia, String estado, String ubicacionId, String productoID, String codigoViejo, String codigoNuevo, int status, int isSelected, int estadoEtiqueta) {
        this.folio = folio;
        this.registroID = registroID;
        this.cantidad = cantidad;
        this.longitud = longitud;
        this.ubicacion = ubicacion;
        this.incidencia = incidencia;
        this.estado = estado;
        this.ubicacionId = ubicacionId;
        this.productoID = productoID;
        this.codigoViejo = codigoViejo;
        this.codigoNuevo = codigoNuevo;
        this.isSelected = isSelected;
        this.estadoEtiqueta = estadoEtiqueta;
        this.status = status;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getRegistroID() {
        return registroID;
    }

    public void setRegistroID(String registroID) {
        this.registroID = registroID;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getIncidencia() {
        return incidencia;
    }

    public void setIncidencia(String incidencia) {
        this.incidencia = incidencia;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUbicacionId() {
        return ubicacionId;
    }

    public void setUbicacionId(String ubicacionId) {
        this.ubicacionId = ubicacionId;
    }

    public String getProductoID() {
        return productoID;
    }

    public void setProductoID(String productoID) {
        this.productoID = productoID;
    }

    public int getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(int isSelected) {
        this.isSelected = isSelected;
    }

    public int getEstadoEtiqueta() {
        return estadoEtiqueta;
    }

    public void setEstadoEtiqueta(int estadoEtiqueta) {
        this.estadoEtiqueta = estadoEtiqueta;
    }

    public String getCodigoViejo() {
        return codigoViejo;
    }

    public void setCodigoViejo(String codigoViejo) {
        this.codigoViejo = codigoViejo;
    }

    public String getCodigoNuevo() {
        return codigoNuevo;
    }

    public void setCodigoNuevo(String codigoNuevo) {
        this.codigoNuevo = codigoNuevo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
