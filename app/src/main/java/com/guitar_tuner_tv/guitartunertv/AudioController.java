/*
 * Copyright 2016 andryr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.guitar_tuner_tv.guitartunertv;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchProcessor;

import static be.tarsos.dsp.io.android.AudioDispatcherFactory.fromDefaultMicrophone;
import static be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm.YIN;

/**
 * Gets the mic input and the current frequency.
 * Created by sbarjola on 18/08/2018.
 */
public class AudioController extends AsyncTask<Float, Float, Float> {

    private static final int SAMPLE_RATE = 44100;
    private static final int BUFFER_SIZE = 4096;
    private static final int RECORD_OVERLAPS = 3072;
    private static final int MIN_SIMILAR_TAKES = 20;
    private static final float FREQ_TOLERANCE = 80F;

    private static List<Float> recordedFrequencies = new ArrayList<>();
    private static PublisherResults resultsPublisher;

    public AudioController(TuningType tuningType, Activity mActivity) {
        resultsPublisher = (PublisherResults) mActivity;
    }

    interface PublisherResults {
        void onFrequenceTranslated(Float frecuencia);
    }

    static boolean isMicRecording;
    private AudioDispatcher audioDispatcher;

    @Override
    protected Float doInBackground(Float... params) {

        PitchDetectionHandler handlerProcesadorFrecuencia = (pitchDetectionResult, audioEvent) -> {

            if (isCancelled()) {
                stopAudioDispatcher();
                return;
            }

            if (!isMicRecording) {
                isMicRecording = true;
                publishProgress();
            }

            final float frecuenciaActual = pitchDetectionResult.getPitch();
            Log.e("FRECUENCIA TEST:", String.valueOf(frecuenciaActual));

            if (frecuenciaActual != -1) {

                recordedFrequencies.add(frecuenciaActual);

                if (recordedFrequencies.size() >= MIN_SIMILAR_TAKES) {
                    final Float frecuenciaMediana = processAudioCaptured(recordedFrequencies);
                    publishProgress(frecuenciaMediana);
                    recordedFrequencies.clear();

                    Log.e("FRECUENCIA GOOD TEST:", String.valueOf(frecuenciaMediana));
                }
            }
        };

        PitchProcessor pitchProcessor = new PitchProcessor(YIN, SAMPLE_RATE, BUFFER_SIZE, handlerProcesadorFrecuencia);

        audioDispatcher = fromDefaultMicrophone(SAMPLE_RATE, BUFFER_SIZE, RECORD_OVERLAPS);
        audioDispatcher.addAudioProcessor(pitchProcessor);
        audioDispatcher.run();

        return null;
    }

    @Override
    protected void onCancelled(Float result) {
        stopAudioDispatcher();
    }

    @Override
    protected void onProgressUpdate(Float... frequencies) {
        if (resultsPublisher != null) {
            if (frequencies.length > 0) {
                resultsPublisher.onFrequenceTranslated(frequencies[0]);
            } else {
                resultsPublisher.onFrequenceTranslated(null);
            }
        }
    }

    private void stopAudioDispatcher() {
        if (audioDispatcher != null && !audioDispatcher.isStopped()) {
            audioDispatcher.stop();
            isMicRecording = false;
        }
    }

    /**
     * Calcula la frecuencia mediana de las tomas obtenidas de audio.
     *
     * @param tomasAudio frecuencias obtenidas.
     * @return mediana de ellas.
     */
    private static Float processAudioCaptured(List<Float> tomasAudio) {

        // Pasamos la list a array
        float[] arrayValores = floatListToArray(tomasAudio);
        float medianValue = calculateArrayMedian(arrayValores);

        // Volvemos a hacer otra pasada por el array, pero eliminando
        // los valores que consideramos ruido y escapan de nuestra tolerancia
        for (int iterIndex = 0; iterIndex < tomasAudio.size(); iterIndex++) {
            final Float currentFloat = tomasAudio.get(iterIndex);
            if (currentFloat < medianValue - FREQ_TOLERANCE || currentFloat > medianValue + FREQ_TOLERANCE) {
                tomasAudio.remove(iterIndex);
            }
        }

        arrayValores = floatListToArray(tomasAudio);
        medianValue = calculateArrayMedian(arrayValores);

        return medianValue;
    }

    /**
     * Metodo que transforma una lista de Floats a un array de Floats.
     *
     * @param floatList lista
     * @return array de floats
     */
    private static float[] floatListToArray(List<Float> floatList) {

        float[] arrayValores = new float[floatList.size()];

        int i = 0;

        for (Float f : floatList) {
            arrayValores[i++] = (f != null ? f : Float.NaN);
        }

        return arrayValores;
    }

    /**
     * Calcula la mediana de los valores de un array de Floats.
     *
     * @param floatArray array de Floats
     * @return mediana resultante
     */
    private static Float calculateArrayMedian(float[] floatArray) {

        float medianValue = 0;

        // Ordenamos
        Arrays.sort(floatArray);

        // Calculamos la mediana
        int median = floatArray.length / 2;

        if (floatArray.length % 2 == 1) {
            medianValue = floatArray[median];
        } else {
            medianValue = (floatArray[median - 1] + floatArray[median]) / 2;
        }

        return medianValue;
    }

    public static void setIsMicRecording(boolean isMicRecording) {
        AudioController.isMicRecording = isMicRecording;
    }
}