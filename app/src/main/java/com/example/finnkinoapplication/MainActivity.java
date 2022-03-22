package com.example.finnkinoapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    MainActivity context = null;
    TheatreData theatreData = TheatreData.getInstance();

    private Spinner theatreSpinner;
    private Spinner dateSpinner;
    private Spinner timeSpinner;
    private Spinner movieSpinner;
    private Spinner placeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        context = MainActivity.this;
        readXML();
        initializeSpinners();
    }

    private void initializeSpinners(){
        theatreSpinner = findViewById(R.id.theatreSpinner);
        dateSpinner = findViewById(R.id.dateSpinner);
        timeSpinner = findViewById(R.id.timeSpinner);
        movieSpinner = findViewById(R.id.movieSpinner);
        placeSpinner = findViewById(R.id.placeSpinner);

        ArrayAdapter<Theatre> adapter = new ArrayAdapter<Theatre>(getApplicationContext(),  androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, theatreData.getTheatreArray());
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);

        theatreSpinner.setAdapter(adapter);
    }

    private void readXML(){
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
}