package com.guitar_tuner_tv.guitartunertv;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.guitar_tuner_tv.guitartunertv.Intro.IntroActivity;

import static com.guitar_tuner_tv.guitartunertv.AudioController.setIsMicRecording;

/*
 * MainActivity class.
 */
public class MainActivity extends AppCompatActivity implements AudioController.PublisherResults {

    // Aux constant
    public static final String MARKER_POSITION = "";
    public static final String TUNING_INDX = "";

    // Current tuning type
    private TuningType selectedTuningType;

    // View objects
    private MarkerView viewMarker;
    private TuningView viewTuner;
    private TextView vFrequencyText;

    @Override
    protected void onStart() {
        super.onStart();

        PackageManager pm = getPackageManager();
        boolean micPresent = pm.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
        Toast.makeText(getApplicationContext(), "Tiene microfono:" + micPresent,
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checks if it's the first time the APP is opened
        SharedPreferences sp = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        if (!sp.getBoolean("first", false)) {
            setIsMicRecording(false);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("first", true);
            editor.apply();
            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_main);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            // Afinación actual
            selectedTuningType = new TuningType("Afinación estándar",
                    new Note[]{
                            new Note(82.41F, "E"),
                            new Note(110.00F, "A"),
                            new Note(146.83F, "D"),
                            new Note(196.00F, "G"),
                            new Note(246.94F, "B"),
                            new Note(329.63F, "E"),
                    });

            viewTuner = findViewById(R.id.vistanotas);
            viewTuner.setTuningType(selectedTuningType);

            // UI adjustments
            viewMarker = findViewById(R.id.aguja);
            viewMarker.setTickLabel(-1.0F, "-100c");
            viewMarker.setTickLabel(0.0F, String.format("%.02fHz", selectedTuningType.getNotasAfinacion()[0].getFrecuencia()));
            viewMarker.setTickLabel(1.0F, "+100c");

            vFrequencyText = findViewById(R.id.frequency_view);
            vFrequencyText.setText(String.format("%.02fHz", selectedTuningType.getNotasAfinacion()[0].getFrecuencia()));

            // Start the AsyncTask
            new AudioController(selectedTuningType, this).execute();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        viewMarker.setTipPos(savedInstanceState.getFloat(MARKER_POSITION));
        int pitchIndex = savedInstanceState.getInt(TUNING_INDX);
        viewMarker.setTickLabel(0.0F, String.format("%.02fHz", selectedTuningType.getNotasAfinacion()[pitchIndex].getFrecuencia()));
        viewTuner.setSelectedIndex(pitchIndex);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onFrequenceTranslated(Float frecuencia) {
        if (frecuencia != null) {
            moverAguja(frecuencia);
        }
    }

    private void moverAguja(final float frecuenciaDetectada) {

        Log.e("NOTA DETECTADA: ", String.valueOf(frecuenciaDetectada));

        // Buscamos la nota de afinación más cercana
        final Note nota = selectedTuningType.notaMasParecida(frecuenciaDetectada);
        // Intervalo de frecuencia en el que vamos a trabajar
        double rangoFrecuencia = 1200 * Math.log(frecuenciaDetectada / nota.getFrecuencia()) / Math.log(2);
        // Posición de la aguja
        final float posicionAguja = (float) (rangoFrecuencia / 100);
        // Es una buena afinación si está cerca de la frecuencia correcta
        final boolean esNotaBuena = Math.abs(rangoFrecuencia) < 5.0;

        runOnUiThread(() -> {

            viewTuner.setSelectedIndex(nota.getIndex(), true);
            viewMarker.setTickLabel(0.0F, String.format("%.02fHz", nota.getFrecuencia()));
            viewMarker.animateTip(posicionAguja);
            vFrequencyText.setText(String.format("%.02fHz", frecuenciaDetectada));

            if (esNotaBuena) {
                Toast.makeText(getApplicationContext(), "¡Bien afinado!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}


