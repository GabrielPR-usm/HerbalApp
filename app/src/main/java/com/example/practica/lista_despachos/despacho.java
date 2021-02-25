package com.example.practica.lista_despachos;

import com.google.android.gms.maps.model.LatLng;

public class despacho {

    private String responsableId;
    private String despachoId;
    private String fecha;
    private String hora;
    private String address;
    private String descripcion;
    private String estado;
    private Double latitude;
    private Double longitude;

    public despacho(String responsableId, String despachoId, String fecha, String hora, String direccion, String descripcion, String estado, Double latitude, Double longitude) {
        this.responsableId = responsableId;
        this.despachoId = despachoId;
        this.fecha = fecha;
        this.hora = hora;
        this.address = direccion;
        this.descripcion = descripcion;
        this.estado = estado;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public despacho(){

    }

    public String getResponsableId() {
        return responsableId;
    }

    public void setResponsableId(String responsableId) {
        this.responsableId = responsableId;
    }

    public String getDespachoId() {
        return despachoId;
    }

    public void setDespachoId(String despachoId) {
        this.despachoId = despachoId;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
