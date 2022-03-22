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

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    MainActivity context = null;
    TheatreData theatreData = TheatreData.getInstance();
    ArrayList<String> date_array = new ArrayList<>();
    ArrayList<Theatre> theatre_array = theatreData.getTheatreArray();
    ArrayList<Show> show_array = new ArrayList<>();

    private Spinner theatreSpinner;
    private Spinner dateSpinner;
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
    }

    private void initializeTheatreSpinner(){
        theatreSpinner = findViewById(R.id.theatreSpinner);

        ArrayAdapter<Theatre> theatreAdapter = new ArrayAdapter<Theatre>(getApplicationContext(),  androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, theatre_array);
        theatreAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);

        theatreSpinner.setAdapter(theatreAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initializeDateSpinner(){
        dateSpinner = findViewById(R.id.dateSpinner);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 11; i++) {
            date_array.add(dtf.format(now.plusDays(i)));
        }

        ArrayAdapter<String> dateAdapter = new ArrayAdapter<String>(getApplicationContext(),  androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, date_array);
        dateAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);

        dateSpinner.setAdapter(dateAdapter);
    }

    private void initializeShowList(){
        showList = findViewById(R.id.showListView);
        ArrayAdapter<Show> showListAdapter = new ArrayAdapter<Show>(getApplicationContext(), android.R.layout.simple_spinner_item, show_array);
        showList.setAdapter(showListAdapter);
    }

    private void readAreaXML(){
        DocumentBuilder builder = null;
        try {

            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            String theatreURL = "https://www.finnkino.fi/xml/TheatreAreas/";
            Document doc = builder.parse(theatreURL);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getDocumentElement().getElementsByTagName("TheatreArea");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    theatreData.getTheatreArray().add(new Theatre(element.getElementsByTagName("Name").item(0).getTextContent(), element.getElementsByTagName("ID").item(0).getTextContent()));
                }

            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    private void readShowXML(String id, String date) {
        show_array.clear();
        DocumentBuilder builder = null;
        try {

            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            String dateURL = "https://www.finnkino.fi/xml/Schedule/?area=" + id + "&dt=" + date;
            Document doc = builder.parse(dateURL);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getDocumentElement().getElementsByTagName("Show");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    show_array.add(new Show(
                            element.getElementsByTagName("Title").item(0).getTextContent(),
                            element.getElementsByTagName("dttmShowStart").item(0).getTextContent(),
                            element.getElementsByTagName("dttmShowEnd").item(0).getTextContent(),
                            element.getElementsByTagName("TheatreAuditorium").item(0).getTextContent()));
                    System.out.println(element.getElementsByTagName("Title").item(0).getTextContent());
                }

            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    public void getShows(View v) {
        int selectedTheatreIndex = theatreSpinner.getSelectedItemPosition();
        int selectedDateIndex = dateSpinner.getSelectedItemPosition();
        String selectedTheatreId = theatre_array.get(selectedTheatreIndex).getId();
        String selectedDate = date_array.get(selectedDateIndex).toString();
        readShowXML(selectedTheatreId, selectedDate);
        initializeShowList();
    }
}