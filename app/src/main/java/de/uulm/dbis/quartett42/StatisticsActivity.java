package de.uulm.dbis.quartett42;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class StatisticsActivity extends AppCompatActivity {

    SharedPreferences sharedPref;

    //Elemente:
    TextView Spielegesamt;
    TextView Spielegesamtgewonnen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        Spielegesamt = (TextView) findViewById(R.id.Spielegesamt);
        Spielegesamtgewonnen = (TextView) findViewById(R.id.Spielegesamtgewonnen);


        //Werte aus sharedPreferences holen und Texte setzen:

        //Spiele gesamt
        int spieleGesamt = sharedPref.getInt("spieleGesamt", 0);
        int spieleGesamtGewonnen = sharedPref.getInt("spieleGesamtGewonnen", 0);
        double spieleGesamtGewonnenProzent;

        if (spieleGesamt <= 0){
            spieleGesamtGewonnenProzent = 0;
        } else {
            spieleGesamtGewonnenProzent = spieleGesamtGewonnen/spieleGesamt * 100;
        }

        Spielegesamt.setText("" + spieleGesamt);
        Spielegesamtgewonnen.setText("" + spieleGesamtGewonnen + " (" + spieleGesamtGewonnenProzent + " %)");



    }
}
