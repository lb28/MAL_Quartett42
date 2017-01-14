package de.uulm.dbis.quartett42;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class StatisticsActivity extends AppCompatActivity {

    SharedPreferences sharedPref;

    //Elemente:
    TextView Spielegesamt;
    TextView Spielegesamtgewonnen;
    TextView normaleSpiele;
    TextView normaleSpielegewonnen;
    TextView insaneSpiele;
    TextView insaneSpielegewonnen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        //------------------test----------------------------
/*
        final SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt("spieleGesamt", 100);
        editor.putInt("spieleGesamtGewonnen", 50);

        editor.putInt("normaleSpiele", 50);
        editor.putInt("normaleSpieleGewonnen", 30);

        editor.putInt("insaneSpiele", 50);
        editor.putInt("insaneSpieleGewonnen", 20);

        editor.commit();
*/
        //--------------------------------------------------

        Spielegesamt = (TextView) findViewById(R.id.Spielegesamt);
        Spielegesamtgewonnen = (TextView) findViewById(R.id.Spielegesamtgewonnen);
        normaleSpiele = (TextView) findViewById(R.id.normaleSpiele);
        normaleSpielegewonnen = (TextView) findViewById(R.id.normaleSpielegewonnen);
        insaneSpiele = (TextView) findViewById(R.id.insaneSpiele);
        insaneSpielegewonnen = (TextView) findViewById(R.id.insaneSpielegewonnen);



        //Werte aus sharedPreferences holen und Texte setzen:

        //Spiele gesamt
        int spieleGesamt = sharedPref.getInt("spieleGesamt", 0);
        int spieleGesamtGewonnen = sharedPref.getInt("spieleGesamtGewonnen", 0);
        double spieleGesamtGewonnenProzent ;

        if (spieleGesamt <= 0){
            spieleGesamtGewonnenProzent = 0;
        } else {
            spieleGesamtGewonnenProzent = (double)(spieleGesamtGewonnen*100/spieleGesamt);
        }

        Spielegesamt.setText("" + spieleGesamt);
        Spielegesamtgewonnen.setText("" + spieleGesamtGewonnen + " (" + spieleGesamtGewonnenProzent + " %)");

        //normale Spiele
        int normalespiele = sharedPref.getInt("normaleSpiele", 0);
        int normalespielegewonnen = sharedPref.getInt("normaleSpieleGewonnen", 0);
        double normaleSpieleGewonnenProzent;

        if (normalespiele <= 0){
            normaleSpieleGewonnenProzent = 0;
        } else {
            normaleSpieleGewonnenProzent = (double) (normalespielegewonnen*100/normalespiele);
        }

        normaleSpiele.setText("" + normalespiele);
        normaleSpielegewonnen.setText("" + normalespielegewonnen + " (" + normaleSpieleGewonnenProzent + " %)");

        //insane Spiele
        int insanespiele = sharedPref.getInt("insaneSpiele", 0);
        int insanespielegewonnen = sharedPref.getInt("insaneSpieleGewonnen", 0);
        double insaneSpieleGewonnenProzent;
        if (insanespiele <= 0){
            insaneSpieleGewonnenProzent = 0;
        } else {
            insaneSpieleGewonnenProzent = (double) (insanespielegewonnen*100/insanespiele);
        }

        insaneSpiele.setText("" + insanespiele);
        insaneSpielegewonnen.setText("" + insanespielegewonnen + " (" + insaneSpieleGewonnenProzent + " %)");

    }

    public void resetStatistics(View view){
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt("spieleGesamt", 0);
        editor.putInt("spieleGesamtGewonnen", 0);

        editor.putInt("normaleSpiele", 0);
        editor.putInt("normaleSpieleGewonnen", 0);

        editor.putInt("insaneSpiele", 0);
        editor.putInt("insaneSpieleGewonnen", 0);

        editor.commit();

        Toast.makeText(getApplicationContext(), "Statistik zurückgesetzt", Toast.LENGTH_SHORT).show();

        //entwerder das, aber lädt die activity neu
        /*
        Intent intent = getIntent();
        finish();
        startActivity(intent);
        */
        //oder das hier (ist besser)
        Spielegesamt.setText("" + 0);
        Spielegesamtgewonnen.setText("" + 0 + " (" + 0 + " %)");

        normaleSpiele.setText("" + 0);
        normaleSpielegewonnen.setText("" + 0 + " (" + 0 + " %)");

        insaneSpiele.setText("" + 0);
        insaneSpielegewonnen.setText("" + 0 + " (" + 0 + " %)");

        finish();

    }





}
