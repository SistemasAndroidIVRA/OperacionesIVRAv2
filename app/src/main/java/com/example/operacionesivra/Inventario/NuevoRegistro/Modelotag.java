package com.example.operacionesivra.Inventario.NuevoRegistro;

import android.os.Parcel;
import android.os.Parcelable;

public class Modelotag implements Parcelable {
    String codigo, contenido, ubicaión, asignada;

    public Modelotag(String codigo, String contenido, String ubicaión, String asignada) {
        this.codigo = codigo;
        this.contenido = contenido;
        this.ubicaión = ubicaión;
        this.asignada = asignada;
    }

    protected Modelotag(Parcel in) {
        codigo = in.readString();
        contenido = in.readString();
        ubicaión = in.readString();
        asignada = in.readString();
    }

    public static final Creator<Modelotag> CREATOR = new Creator<Modelotag>() {
        @Override
        public Modelotag createFromParcel(Parcel in) {
            return new Modelotag(in);
        }

        @Override
        public Modelotag[] newArray(int size) {
            return new Modelotag[size];
        }
    };

    public String getCodigo() {
        return codigo;
    }

    public String getContenido() {
        return contenido;
    }

    public String getUbicaión() {
        return ubicaión;
    }

    public String getAsignada() {
        return asignada;
    }

    public void setAsignada(String asignada) {
        this.asignada = asignada;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(codigo);
        dest.writeString(contenido);
        dest.writeString(ubicaión);
        dest.writeString(asignada);
    }
}
