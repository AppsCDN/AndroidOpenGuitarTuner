package com.guitar_tuner_tv.guitartunertv.Intro;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;
import com.guitar_tuner_tv.guitartunertv.R;

public class WelcomeFragment extends Fragment implements ISlideBackgroundColorHolder {

    @Override
    public int getDefaultBackgroundColor() {
        final int colorOne = ContextCompat.getColor(getContext(), R.color.welcome_screen1_bg);
        return colorOne;
    }

    @Override
    public void setBackgroundColor(@ColorInt int backgroundColor) {

         ConstraintLayout layoutContainer = getView().findViewById(R.id.welcomeFragmentCl);

        // Set the background color of the view within your slide to which the transition should be applied.
        if (layoutContainer != null) {
            layoutContainer.setBackgroundColor(backgroundColor);
        }
    }

    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
    private int layoutResId;

    public static WelcomeFragment newInstance(int layoutResId) {
        WelcomeFragment sampleSlide = new WelcomeFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID)) {
            layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.welcome_fragment, null);
        return view;
    }
}