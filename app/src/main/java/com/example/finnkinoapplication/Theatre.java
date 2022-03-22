package com.example.finnkinoapplication;

public class Theatre {

    private String theatre_name;
    private String theatre_id;

    public Theatre(String name, String id){
        this.theatre_id = id;
        this.theatre_name = name;
    }

    @Override
    public String toString() {
        String print = this.theatre_name;
        return print;
    }

}
