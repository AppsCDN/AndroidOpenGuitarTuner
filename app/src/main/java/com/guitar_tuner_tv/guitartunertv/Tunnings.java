package com.guitar_tuner_tv.guitartunertv;

import java.util.ArrayList;
import java.util.List;

/**
 * Class with some of the most common tunings.
 */
public class Tunnings {

    public static final List<TuningType> getTuningTypes() {

        List<TuningType> tuningTypes = new ArrayList<>();

        tuningTypes.add(new TuningType("Afinación estándar",
                new Note[]{
                        new Note(82.41F, "E", 6),
                        new Note(110.00F, "A", 5),
                        new Note(146.83F, "D", 4),
                        new Note(196.00F, "G", 3),
                        new Note(246.94F, "B", 2),
                        new Note(329.63F, "E", 1),
                }));

        tuningTypes.add(new TuningType("Drop D",
                new Note[]{
                        new Note(73.42F, "D", 6),
                        new Note(110.00F, "A", 5),
                        new Note(146.83F, "D", 4),
                        new Note(196.00F, "G", 3),
                        new Note(246.94F, "B", 2),
                        new Note(329.63F, "E", 1),
                }));

        tuningTypes.add(new TuningType("Open D",
                new Note[]{
                        new Note(73.42F, "D", 6),
                        new Note(110.00F, "A", 5),
                        new Note(146.83F, "D", 4),
                        new Note(185.00F, "F", 3),
                        new Note(246.94F, "B", 2),
                        new Note(329.63F, "E", 1),
                }));

        return tuningTypes;
    }
}
