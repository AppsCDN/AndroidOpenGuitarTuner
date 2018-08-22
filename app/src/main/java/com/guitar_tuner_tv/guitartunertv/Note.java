package com.guitar_tuner_tv.guitartunertv;

/**
 * Created by sbarjola on 18/08/2018.
 */
public class Note {

    private float noteFrequency;
    private String noteName;
    private int stringNumber;

    protected Note(float noteFrequency, String noteName, int stringNumber) {
        this.noteFrequency = noteFrequency;
        this.noteName = noteName;
        this.stringNumber = stringNumber;
    }

    // Getters and setters

    public float getNoteFrequency() {
        return noteFrequency;
    }

    public String getNoteName() {
        return noteName;
    }

    public int getStringNumber() {
        return stringNumber;
    }

    public void setStringNumber(int stringNumber) {
        this.stringNumber = stringNumber;
    }
}
