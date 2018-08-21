package com.guitar_tuner_tv.guitartunertv.Intro;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;
import com.guitar_tuner_tv.guitartunertv.MainActivity;

/**
 * Just a welcome activity. WIP. Uses AppIntro library: https://github.com/apl-devs/AppIntro
 */
public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        //addSlide(firstFragment);

        // Instead of fragments, you can also use our default slide.
        // Just create a `SliderPage` and provide title, description, background and image.
        // AppIntro will do the rest.
        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle("Guitar Tuner");
        sliderPage.setDescription("The First Android TV Tuner");
        //sliderPage.setImageDrawable(image);ç
        sliderPage.setBgColor(Color.rgb(167, 94, 101));
        addSlide(AppIntroFragment.newInstance(sliderPage));

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle("How to use:");
        sliderPage2.setDescription("1 - Put your phone close to your guitar");
        //sliderPage.setImageDrawable(image);
        sliderPage2.setBgColor(Color.rgb(167, 94, 101));
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
        showSkipButton(false);
        setProgressButtonEnabled(true);

        // Asks for the MIC AUDIO permissions
        askForPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}