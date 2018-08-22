package com.guitar_tuner_tv.guitartunertv;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
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

    // Current tuning type
    private TuningType selectedTuningType;

    // View objects
    private MarkerView viewMarker;
    private TextView vFrequencyText;

    // Tuning and strings
    private TextView tnningName;
    private TextView stringOne;
    private TextView stringTwo;
    private TextView stringThree;
    private TextView stringFour;
    private TextView stringFive;
    private TextView stringSix;

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

            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_main);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            // Afinación actual
            setTunningType(new TuningType("Afinación estándar",
                    new Note[]{
                            new Note(82.41F, "E", 6),
                            new Note(110.00F, "A", 5),
                            new Note(146.83F, "D", 4),
                            new Note(196.00F, "G", 3),
                            new Note(246.94F, "B", 2),
                            new Note(329.63F, "E", 1),
                    }));

            // UI adjustments
            viewMarker = findViewById(R.id.aguja);
            vFrequencyText = findViewById(R.id.frequency_view);

            viewMarker.setTickLabel(-1.0F, "-100c");
            viewMarker.setTickLabel(0.0F, String.format("%.02fHz", selectedTuningType.getTuningNotes()[0].getFrecuencia()));
            viewMarker.setTickLabel(1.0F, "+100c");
            vFrequencyText.setText(String.format("%.02fHz", selectedTuningType.getTuningNotes()[0].getFrecuencia()));

            // Start the AsyncTask
            new AudioController(selectedTuningType, this).execute();
        }
    }

    private void setTunningType(final TuningType tt) {

        selectedTuningType = tt;

        stringOne = findViewById(R.id.stringOne);
        stringTwo = findViewById(R.id.stringTwo);
        stringThree = findViewById(R.id.stringThree);
        stringFour = findViewById(R.id.stringFour);
        stringFive = findViewById(R.id.stringFive);
        stringSix = findViewById(R.id.stringSix);
        tnningName = findViewById(R.id.tuningName);

        stringSix.setText(tt.getTuningNotes()[0].getNombre());
        stringFive.setText(tt.getTuningNotes()[1].getNombre());
        stringFour.setText(tt.getTuningNotes()[2].getNombre());
        stringThree.setText(tt.getTuningNotes()[3].getNombre());
        stringTwo.setText(tt.getTuningNotes()[4].getNombre());
        stringOne.setText(tt.getTuningNotes()[5].getNombre());
        tnningName.setText(tt.getTuningName());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
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

    private void moverAguja(final float reportedFrequency) {

        Log.e("NOTA DETECTADA: ", String.valueOf(reportedFrequency));

        // Buscamos la nota de afinación más cercana
        final Note nota = selectedTuningType.notaMasParecida(reportedFrequency);
        // Intervalo de frecuencia en el que vamos a trabajar
        double rangoFrecuencia = 1200 * Math.log(reportedFrequency / nota.getFrecuencia()) / Math.log(2);
        // Posición de la aguja
        final float posicionAguja = (float) (rangoFrecuencia / 100);
        // Es una buena afinación si está cerca de la frecuencia correcta
        final boolean esNotaBuena = Math.abs(rangoFrecuencia) < 5.0;

        runOnUiThread(() -> {
            viewMarker.setTickLabel(0.0F, String.format("%.02fHz", nota.getFrecuencia()));
            enlightString(nota.getStringNumber());
            viewMarker.animateTip(posicionAguja);
            vFrequencyText.setText(String.format("%.02fHz", reportedFrequency));


            if (esNotaBuena) {
                Toast.makeText(getApplicationContext(), "¡Bien afinado!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void enlightString(int string) {
        turnOffAllStrings();

        switch (string) {
            case 1:
                stringOne.setTextColor(Color.GREEN);
                stringOne.setTypeface(stringOne.getTypeface(), Typeface.BOLD);
                break;
            case 2:
                stringTwo.setTextColor(Color.GREEN);
                stringTwo.setTypeface(stringTwo.getTypeface(), Typeface.BOLD);
                break;
            case 3:
                stringThree.setTextColor(Color.GREEN);
                stringThree.setTypeface(stringThree.getTypeface(), Typeface.BOLD);
                break;
            case 4:
                stringFour.setTextColor(Color.GREEN);
                stringFour.setTypeface(stringFour.getTypeface(), Typeface.BOLD);
                break;
            case 5:
                stringFive.setTextColor(Color.GREEN);
                stringFive.setTypeface(stringFive.getTypeface(), Typeface.BOLD);
                break;
            case 6:
                stringSix.setTextColor(Color.GREEN);
                stringSix.setTypeface(stringSix.getTypeface(), Typeface.BOLD);
                break;
        }
    }

    private void turnOffAllStrings() {
        stringOne.setTextColor(Color.WHITE);
        stringTwo.setTextColor(Color.WHITE);
        stringThree.setTextColor(Color.WHITE);
        stringFour.setTextColor(Color.WHITE);
        stringFive.setTextColor(Color.WHITE);
        stringSix.setTextColor(Color.WHITE);

        stringOne.setTypeface(stringOne.getTypeface(), Typeface.NORMAL);
        stringTwo.setTypeface(stringTwo.getTypeface(), Typeface.NORMAL);
        stringThree.setTypeface(stringThree.getTypeface(), Typeface.NORMAL);
        stringFour.setTypeface(stringFour.getTypeface(), Typeface.NORMAL);
        stringFive.setTypeface(stringFive.getTypeface(), Typeface.NORMAL);
        stringSix.setTypeface(stringSix.getTypeface(), Typeface.NORMAL);

    }
}


