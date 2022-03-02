package com.example.operacionesivra.Vistas.Inventario;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.stream.Stream;

public class ModeloDetallesItem extends ArrayList<ModeloDetallesItem> implements Parcelable {
    String cantidad, longitud, ubicacion, estado, materialregistrado, folioadd, incidencia, ubicacionId;
    int imagen;
    int isSelected;
    public ModeloDetallesItem(String cantidad, String longitud, String materialregistrado, String ubicacion, String estado, int imagen, String incidencia, String folioadd, String ubicacionId, int isSelected) {
        this.cantidad = cantidad;
        this.longitud = longitud;
        this.ubicacion = ubicacion;
        this.estado = estado;
        this.imagen = imagen;
        this.materialregistrado = materialregistrado;
        this.folioadd = folioadd;
        this.incidencia = incidencia;
        this.ubicacionId = ubicacionId;
        this.isSelected = isSelected;
    }

    protected ModeloDetallesItem(Parcel in) {
        cantidad = in.readString();
        longitud = in.readString();
        ubicacion = in.readString();
        estado = in.readString();
        materialregistrado = in.readString();
        imagen = in.readInt();
        folioadd = in.readString();
        incidencia = in.readString();
    }

    public static final Creator<ModeloDetallesItem> CREATOR = new Creator<ModeloDetallesItem>() {
        @Override
        public ModeloDetallesItem createFromParcel(Parcel in) {
            return new ModeloDetallesItem(in);
        }

        @Override
        public ModeloDetallesItem[] newArray(int size) {
            return new ModeloDetallesItem[size];
        }
    };

    public String getMaterialregistrado() {
        return materialregistrado;
    }

    public int getImagen() {
        return imagen;
    }

    public String getEstado() {
        return estado;
    }

    public String getCantidad() {
        return cantidad;
    }

    public String getLongitud() {
        return longitud;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getFolioadd() {
        return folioadd;
    }

    public String getIncidencia() {
        return incidencia;
    }

    public String getUbicacionId(){
        return ubicacionId;
    }

    public int getIsSelected(){
        return isSelected;
    }

    public void setIsSelected(int isSelected){
        this.isSelected = isSelected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cantidad);
        dest.writeString(longitud);
        dest.writeString(ubicacion);
        dest.writeString(estado);
        dest.writeString(materialregistrado);
        dest.writeInt(imagen);
        dest.writeString(folioadd);
        dest.writeString(incidencia);
    }

    @NonNull
    @Override
    public Stream<ModeloDetallesItem> stream() {
        return null;
    }
}
