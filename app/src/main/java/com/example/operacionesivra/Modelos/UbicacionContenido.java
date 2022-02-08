package com.example.operacionesivra.Modelos;

public class UbicacionContenido {
    String UbicacionID, Ubicacion, Contenido, ProductoID, codigoViejo, codigoNuevo;

    public UbicacionContenido(String UbicacionID, String Ubicacion, String Contenido, String ProductoID){
        this.UbicacionID = UbicacionID;
        this.Ubicacion = Ubicacion;
        this.Contenido = Contenido;
        this.ProductoID = ProductoID;
    }

    public UbicacionContenido(String UbicacionID, String Ubicacion, String Contenido, String ProductoID, String codigoViejo, String codigoNuevo){
        this.UbicacionID = UbicacionID;
        this.Ubicacion = Ubicacion;
        this.Contenido = Contenido;
        this.ProductoID = ProductoID;
        this.codigoViejo = codigoViejo;
        this.codigoNuevo = codigoNuevo;
    }

    public String getUbicacionID() {
        return UbicacionID;
    }

    public void setUbicacionID(String ubicacionID) {
        UbicacionID = ubicacionID;
    }

    public String getUbicacion() {
        return Ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        Ubicacion = ubicacion;
    }

    public String getContenido() {
        return Contenido;
    }

    public void setContenido(String contenido) {
        Contenido = contenido;
    }

    public String getProductoID() {
        return ProductoID;
    }

    public void setProductoID(String ProductoID) {
        ProductoID = ProductoID;
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
}
