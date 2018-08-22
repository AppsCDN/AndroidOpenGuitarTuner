package com.guitar_tuner_tv.guitartunertv;

/**
 * Created by sbarjola on 18/08/2018.
 */
public class Note {

    private float frecuencia;
    private String nombre;
    private int stringNumber;

    protected Note(float frecuencia, String nombre, int stringNumber) {
        this.frecuencia = frecuencia;
        this.nombre = nombre;
        this.stringNumber = stringNumber;
    }

    // Getters and setters

    public float getFrecuencia() {
        return frecuencia;
    }

    public String getNombre() {
        return nombre;
    }

    public int getStringNumber() {
        return stringNumber;
    }

    public void setStringNumber(int stringNumber) {
        this.stringNumber = stringNumber;
    }
}
