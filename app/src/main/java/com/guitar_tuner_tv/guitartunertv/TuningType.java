package com.guitar_tuner_tv.guitartunertv;

/**
 * Created by sbarjola 19/08/2018
 */
public class TuningType {

    private Note[] tuningNotes;
    private String tuningName;

    public TuningType(String tuningName, Note[] tuningNotes) {
        this.tuningName = tuningName;
        this.tuningNotes = tuningNotes;
    }

    public Note notaMasParecida(float frecuencia) {

        float limiteDerecho = Float.POSITIVE_INFINITY;
        Note notaFinal = null;

        for (Note notaIterada: tuningNotes) {
            final float diferenciaFrecuencia = Math.abs(frecuencia - notaIterada.getNoteFrequency());
            if (diferenciaFrecuencia < limiteDerecho) {
                notaFinal = notaIterada;
                limiteDerecho = diferenciaFrecuencia;
            }
            else{
                break;
            }
        }

        return notaFinal;
    }

    public Note getClosestAlternative(float x) {

        int low = 0;
        int high = tuningNotes.length - 1;

        while (low < high) {

            final int mid = (low + high) / 2;

            assert(mid < high);

            final float d1 = Math.abs(tuningNotes[mid  ].getNoteFrequency() - x);
            final float d2 = Math.abs(tuningNotes[mid+1].getNoteFrequency() - x);

            if (d2 <= d1) {
                low = mid+1;
            }
            else {
                high = mid;
            }
        }
        return tuningNotes[high];
    }


    // Getters

    public Note[] getTuningNotes() {
        return tuningNotes;
    }

    public String getTuningName() {
        return tuningName;
    }
}
