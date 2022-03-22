package com.example.finnkinoapplication;

import java.util.ArrayList;

public class TheatreData {

    private static TheatreData theatreData;
    protected ArrayList<Theatre> theatre_array = new ArrayList<>();

    public static TheatreData getInstance() {
        if (theatreData == null) {
            theatreData = new TheatreData();
        }
        return theatreData;
    }

    private TheatreData (){}

    public ArrayList getTheatreArray () {
        return theatre_array;
    }

}
