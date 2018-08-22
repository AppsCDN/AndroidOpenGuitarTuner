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
    private TextView tuningName;
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
    protected void onCreate(Bundle savedInstanceState) {
        // After showing the splash screen we load the real app theme.
        setTheme(R.style.NoActionBar);

        super.onCreate(savedInstanceState);

        // Checks if it's the first time the APP is opened. If it's it redirects to the intro activities.
        SharedPreferences sp = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        if (!sp.getBoolean("first", false)) {
            // Stops recording
            setIsMicRecording(false);

            // Intent to the welcome activities
            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
            finish();
        } else {
            //Inflate the view
            setContentView(R.layout.activity_main);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            // Set the current tuning
            setTunningType(new TuningType("Afinación estándar",
                    new Note[]{
                            new Note(82.41F, "E", 6),
                            new Note(110.00F, "A", 5),
                            new Note(146.83F, "D", 4),
                            new Note(196.00F, "G", 3),
                            new Note(246.94F, "B", 2),
                            new Note(329.63F, "E", 1),
                    }));

            // View references
            viewMarker = findViewById(R.id.aguja);
            vFrequencyText = findViewById(R.id.frequency_view);

            // Needle set up and UI adjustments
            viewMarker.setTickLabel(-1.0F, "-100c");
            viewMarker.setTickLabel(0.0F, String.format("%.02fHz", selectedTuningType.getTuningNotes()[0].getNoteFrequency()));
            viewMarker.setTickLabel(1.0F, "+100c");
            vFrequencyText.setText(String.format("%.02fHz", selectedTuningType.getTuningNotes()[0].getNoteFrequency()));

            // Starts the AsyncTask to capture audio
            new AudioController(selectedTuningType, this).execute();
        }
    }

    /**
     * Sets the current tuning to the APP.
     * @param tt tuning type to set.
     */
    private void setTunningType(final TuningType tt) {

        this.selectedTuningType = tt;

        // View references
        stringOne = findViewById(R.id.stringOne);
        stringTwo = findViewById(R.id.stringTwo);
        stringThree = findViewById(R.id.stringThree);
        stringFour = findViewById(R.id.stringFour);
        stringFive = findViewById(R.id.stringFive);
        stringSix = findViewById(R.id.stringSix);
        tuningName = findViewById(R.id.tuningName);

        // Sets the current tuning notes to the strings
        stringSix.setText(tt.getTuningNotes()[0].getNoteName());
        stringFive.setText(tt.getTuningNotes()[1].getNoteName());
        stringFour.setText(tt.getTuningNotes()[2].getNoteName());
        stringThree.setText(tt.getTuningNotes()[3].getNoteName());
        stringTwo.setText(tt.getTuningNotes()[4].getNoteName());
        stringOne.setText(tt.getTuningNotes()[5].getNoteName());

        // Sets the current tuning name
        tuningName.setText(tt.getTuningName());
    }

    @Override
    public void onFrequenceTranslated(final Float frequence) {
        if (frequence != null) {
            rotateMarker(frequence);
        }
    }

    /**
     * Rotates the marker to the given frequency.
     * @param reportedFrequency
     */
    private void rotateMarker(final float reportedFrequency) {

        Log.e("NOTA DETECTADA: ", String.valueOf(reportedFrequency));

        // Look for the closest/more similar note to the current frequency
        final Note nota = selectedTuningType.getClosestAlternative(reportedFrequency);
        // Frequency range we are going to work with
        double frequencyRange = 1200 * Math.log(reportedFrequency / nota.getNoteFrequency()) / Math.log(2);
        // Marker final position
        final float markerPos = (float) (frequencyRange / 100);
        // We consider it good tuned if the played note is close enough
        final boolean isGoodTuned = Math.abs(frequencyRange) < 5.0;

        runOnUiThread(() -> {
            // Set the ideal frequency
            viewMarker.setTickLabel(0.0F, String.format("%.02fHz", nota.getNoteFrequency()));
            // Set the current frequency
            vFrequencyText.setText(String.format("%.02fHz", reportedFrequency));
            // Turn on on UI the played string
            enlightString(nota.getStringNumber());
            // Move the marker
            viewMarker.animateTip(markerPos);

            if (isGoodTuned) {
                Toast.makeText(getApplicationContext(), "¡Bien afinado!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Sets the current played string to green.
     * @param string played string.
     */
    private void enlightString(int string) {

        turnOffAllStrings();

        switch (string) {
            case 1:
                turnOnString(stringOne);
                break;
            case 2:
                turnOnString(stringTwo);
                break;
            case 3:
                turnOnString(stringThree);
                break;
            case 4:
                turnOnString(stringFour);
                break;
            case 5:
                turnOnString(stringFive);
                break;
            case 6:
                turnOnString(stringSix);
                break;
        }
    }

    /**
     * Turns on a the given string.
     * @param stringPlayed string number.
     */
    private void turnOnString(TextView stringPlayed){
        stringPlayed.setTextColor(Color.GREEN);
        stringPlayed.setTypeface(stringPlayed.getTypeface(), Typeface.BOLD);
    }

    /**
     * Sets all the strings to the regular non-highlight state.
     */
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
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}


