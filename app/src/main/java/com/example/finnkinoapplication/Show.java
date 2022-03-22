package com.example.finnkinoapplication;

public class Show {

    private String show_title;
    private String[] show_start;
    private String startTime;
    private String[] show_end;
    private String endTime;
    private String show_auditorium;

    public Show(String title, String start, String end, String auditorium){
        this.show_auditorium = auditorium;
        this.show_end = end.split("T");
        this.show_end = this.show_end[1].split(":");
        this.endTime = this.show_end[0] + ":" + this.show_end[1];
        this.show_start = start.split("T");
        this.show_start = this.show_start[1].split(":");
        this.startTime = this.show_start[0] + ":" + this.show_start[1];
        this.show_title = title;
    }

    @Override
    public String toString() {
        String print = this.show_title + ", " + this.startTime + "-" + this.endTime;
        return print;
    }

    public String getStartTime(){
        return this.startTime;
    }

}
