package com.guitar_tuner_tv.guitartunertv;

/**
 * Created by sbarjola on 18/08/2018.
 */
public class Note {

    private float frecuencia;
    private String nombre;
    private int index;

    protected Note(float frecuencia, String nombre) {
        this.frecuencia = frecuencia;
        this.nombre = nombre;
    }

    // Getters and setters

    public float getFrecuencia() {
        return frecuencia;
    }

    public String getNombre() {
        return nombre;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
