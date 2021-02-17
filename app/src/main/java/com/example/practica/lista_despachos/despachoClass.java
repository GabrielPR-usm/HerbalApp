package com.example.practica.lista_despachos;

public class despachoClass {

    private String id;
    private String title;
    private String label1;
    private String label2;

    public despachoClass(){

    }

    public despachoClass(String title, String label1, String label2) {
        this.title = title;
        this.label1 = label1;
        this.label2 = label2;
    }

    public despachoClass(String id, String title, String label1, String label2) {
        this.id = id;
        this.title = title;
        this.label1 = label1;
        this.label2 = label2;
    }

    //GETTERS
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getLabel1() { return label1; }
    public String getLabel2() { return label2; }

    //SETTERS
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setLabel1(String label1) { this.label1 = label1; }
    public void setLabel2(String label2) { this.label2 = label2; }

}

