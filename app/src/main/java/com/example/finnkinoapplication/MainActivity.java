package com.example.finnkinoapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    MainActivity context = null;
    TheatreData theatreData = TheatreData.getInstance();

    // Defining all the arrays
    ArrayList<String> date_array = new ArrayList<>();
    ArrayList<Theatre> theatre_array = theatreData.getTheatreArray();
    ArrayList<Show> show_array = new ArrayList<>();
    ArrayList<String> times_array = new ArrayList<>();

    // Defining the elements on the UI
    private Spinner theatreSpinner;
    private Spinner dateSpinner;
    private Spinner startTimeSpinner;
    private Spinner endTimeSpinner;
    private ListView showList;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        context = MainActivity.this;
        readAreaXML();
        initializeTheatreSpinner();
        initializeDateSpinner();
        initializeTimesSpinner();
    }

    // Method for setting up the dropdown menu for the desired theatre/area
    private void initializeTheatreSpinner(){
        theatreSpinner = findViewById(R.id.theatreSpinner);

        ArrayAdapter<Theatre> theatreAdapter = new ArrayAdapter<Theatre>(this,android.R.layout.simple_spinner_item, theatre_array);
        theatreSpinner.setAdapter(theatreAdapter);
    }

    // Method for setting up the dropdown menu for the desired date
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initializeDateSpinner(){
        dateSpinner = findViewById(R.id.dateSpinner);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDateTime now = LocalDateTime.now();
        // Loop for creating available dates, starting from the current date
        for (int i = 0; i < 11; i++) {
            date_array.add(dtf.format(now.plusDays(i)));
        }

        ArrayAdapter<String> dateAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, date_array);
        dateSpinner.setAdapter(dateAdapter);
    }

    // Method for setting up the list of shows
    private void initializeShowList(){
        showList = findViewById(R.id.showListView);
        ArrayAdapter<Show> showListAdapter = new ArrayAdapter<Show>(getApplicationContext(), android.R.layout.simple_spinner_item, show_array);
        showList.setAdapter(showListAdapter);
    }

    // Method for setting up the dropdown menus for choosing a specific start time
    private void initializeTimesSpinner(){
        endTimeSpinner = findViewById(R.id.endTimeSpinner);
        startTimeSpinner = findViewById(R.id.startTimeSpinner);

        // Loop that creates time values from 00:00 to 23:50
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 6; j++) {
                if (i < 10) {
                    times_array.add("0" + i + ":" + j + "0");
                }
                else {
                    times_array.add(i + ":" + j + "0");
                }
            }
        }

        ArrayAdapter<String> timesAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, times_array);
        startTimeSpinner.setAdapter(timesAdapter);
        endTimeSpinner.setAdapter(timesAdapter);
        startTimeSpinner.setSelection(90); // default value at 15:00
        endTimeSpinner.setSelection(120); // default value at 20:00
    }

    // Method for reading the date of the available theatres
    private void readAreaXML(){
        DocumentBuilder builder = null;
        try {

            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            String theatreURL = "https://www.finnkino.fi/xml/TheatreAreas/";
            Document doc = builder.parse(theatreURL);
            doc.getDocumentElement().normalize();
            // Getting data from all the theatres/areas
            NodeList nodeList = doc.getDocumentElement().getElementsByTagName("TheatreArea");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                // Condition for storing name and id for each theatre/area
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    theatreData.getTheatreArray().add(new Theatre(element.getElementsByTagName("Name").item(0).getTextContent(), element.getElementsByTagName("ID").item(0).getTextContent()));
                }

            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    // Method for reading the data of the specific theatre for a specific date
    private void readShowXML(String id, String date) {
        show_array.clear(); // clears the list
        DocumentBuilder builder = null;
        try {

            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            String dateURL = "https://www.finnkino.fi/xml/Schedule/?area=" + id + "&dt=" + date;
            Document doc = builder.parse(dateURL);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getDocumentElement().getElementsByTagName("Show");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                // Condition for storing all the shows in the specific theatre/area on the spesific date
                // Data stored is the title of the movie, starting and ending times and the auditorium
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    show_array.add(new Show(
                        element.getElementsByTagName("Title").item(0).getTextContent(),
                        element.getElementsByTagName("dttmShowStart").item(0).getTextContent(),
                        element.getElementsByTagName("dttmShowEnd").item(0).getTextContent(),
                        element.getElementsByTagName("TheatreAuditorium").item(0).getTextContent()));
                    // Condition for checking if the show starts on the desired time window (that the user has chosen)
                    // If so, the show is removed from the array
                    if (show_array.get(show_array.size()-1).getStartTime().compareTo(startTimeSpinner.getSelectedItem().toString()) < 0
                            || show_array.get(show_array.size()-1).getStartTime().compareTo(endTimeSpinner.getSelectedItem().toString()) > 0) {
                        show_array.remove(show_array.size()-1);
                    }
                }
            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    // Method for updating the list of shows to the UI
    public void getShows(View v) {
        int selectedTheatreIndex = theatreSpinner.getSelectedItemPosition();
        int selectedDateIndex = dateSpinner.getSelectedItemPosition();
        String selectedTheatreId = theatre_array.get(selectedTheatreIndex).getId();
        String selectedDate = date_array.get(selectedDateIndex).toString();
        readShowXML(selectedTheatreId, selectedDate);
        initializeShowList();
    }
}