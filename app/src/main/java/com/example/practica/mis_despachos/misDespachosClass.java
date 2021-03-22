package com.example.practica.mis_despachos;

public class misDespachosClass {

    private String id;
    private String id_creador;
    private String ciudad;
    private String fecha;
    private String hora;
    private String empresa;
    private String responsable;
    private String nombre1;
    private String nombre2;
    private String rut1;
    private String rut2;
    private String valor;
    private Boolean boolMoto;
    private Boolean boolCamioneta;
    private Boolean boolGestionP;
    private Boolean boolGestionE;
    private String destinos;
    private String ubicacionActual;
    private String recorrido;

    public misDespachosClass(){

    }

    public misDespachosClass(String id, String id_creador, String ciudad, String fecha, String hora, String empresa, String responsable, String nombre1, String nombre2, String rut1, String rut2, String valor, Boolean boolMoto, Boolean boolCamioneta, Boolean boolGestionP, Boolean boolGestionE, String destinos) {
        this.id = id;
        this.id_creador = id_creador;
        this.ciudad = ciudad;
        this.fecha = fecha;
        this.hora = hora;
        this.empresa = empresa;
        this.responsable = responsable;
        this.nombre1 = nombre1;
        this.nombre2 = nombre2;
        this.rut1 = rut1;
        this.rut2 = rut2;
        this.valor = valor;
        this.boolMoto = boolMoto;
        this.boolCamioneta = boolCamioneta;
        this.boolGestionP = boolGestionP;
        this.boolGestionE = boolGestionE;
        this.destinos = destinos;
    }

    public misDespachosClass(String id, String id_creador, String ciudad, String fecha, String hora, String empresa, String responsable, String nombre1, String nombre2, String rut1, String rut2, String valor, Boolean boolMoto, Boolean boolCamioneta, Boolean boolGestionP, Boolean boolGestionE, String destinos, String ubicacionActual, String recorrido) {
        this.id = id;
        this.id_creador = id_creador;
        this.ciudad = ciudad;
        this.fecha = fecha;
        this.hora = hora;
        this.empresa = empresa;
        this.responsable = responsable;
        this.nombre1 = nombre1;
        this.nombre2 = nombre2;
        this.rut1 = rut1;
        this.rut2 = rut2;
        this.valor = valor;
        this.boolMoto = boolMoto;
        this.boolCamioneta = boolCamioneta;
        this.boolGestionP = boolGestionP;
        this.boolGestionE = boolGestionE;
        this.destinos = destinos;
        this.ubicacionActual = ubicacionActual;
        this.recorrido = recorrido;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_creador() {
        return id_creador;
    }

    public void setId_creador(String id_creador) {
        this.id_creador = id_creador;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
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

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getNombre1() {
        return nombre1;
    }

    public void setNombre1(String nombre1) {
        this.nombre1 = nombre1;
    }

    public String getNombre2() {
        return nombre2;
    }

    public void setNombre2(String nombre2) {
        this.nombre2 = nombre2;
    }

    public String getRut1() {
        return rut1;
    }

    public void setRut1(String rut1) {
        this.rut1 = rut1;
    }

    public String getRut2() {
        return rut2;
    }

    public void setRut2(String rut2) {
        this.rut2 = rut2;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Boolean getboolMoto() {
        return boolMoto;
    }

    public void setboolMoto(Boolean boolMoto) {
        this.boolMoto = boolMoto;
    }

    public Boolean getboolCamioneta() {
        return boolCamioneta;
    }

    public void setboolCamioneta(Boolean boolCamioneta) {
        this.boolCamioneta = boolCamioneta;
    }

    public Boolean getboolGestionP() {
        return boolGestionP;
    }

    public void setboolGestionP(Boolean boolGestionP) {
        this.boolGestionP = boolGestionP;
    }

    public Boolean getboolGestionE() {
        return boolGestionE;
    }

    public void setboolGestionE(Boolean boolGestionE) {
        this.boolGestionE = boolGestionE;
    }

    public String getDestinos() {
        return destinos;
    }

    public void setDestinos(String destinos) {
        this.destinos = destinos;
    }

    public String getUbicacionActual() {
        return ubicacionActual;
    }

    public void setUbicacionActual(String ubicacionActual) {
        this.ubicacionActual = ubicacionActual;
    }

    public String getRecorrido() {
        return recorrido;
    }

    public void setRecorrido(String recorrido) {
        this.recorrido = recorrido;
    }
}
