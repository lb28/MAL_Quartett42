package de.uulm.dbis.quartett42;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GameEndActivity extends AppCompatActivity {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    //Elemente:

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_end);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();

        //statistik updaten
        updateStatistics();

        //Gewonnen/Verloren anzeigen
        //TODO gewonnen/verloren holen und Textfeld setzen


        //Spielmodus holen
        int spielmodus = sharedPref.getInt("mode", 0);
        int schwierigkeitTemp = sharedPref.getInt("difficulty", 0);
        double schwierigkeit;
        if (schwierigkeitTemp==1){
            schwierigkeit = 1.0;
        } else if (schwierigkeitTemp == 2){
            schwierigkeit = 1.25;
        } else {
            schwierigkeit = 1.5;
        }

        /*
        METRIK FÜR HIGHSCORES:
        Rundenmodus: Runden * Punkte * Schwierigkeit * (2 für Expertmodus)
        Zeitmodus: Spielzeit * Punkte * Schwierigkeit * (2 für Expertmodus)
        Punktemodus: Punkte * Schwierigkeit * (2 für Expertmodus)
         */

        //ergebnis in top5?
        if (spielmodus == 1){
            int platz5 = sharedPref.getInt("fuenfterPunkteRunden", -1);
            //TODO if erreichte Punkte * Anzahl Runden * Schwierigkeit * (2) > platz5, dann neuer highscore
        }
        else if (spielmodus == 2){
            int platz5 = sharedPref.getInt("fuenfterPunkteZeit", -1);
            //TODO

        } else {
            int platz5 = sharedPref.getInt("fuenfterPunktePunkte", -1);
            //TODO
        }

    }

    public void updateStatistics(){

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();

        //normaleMode/insaneMode? -> Statistik updaten
        Boolean insaneMode = sharedPref.getBoolean("insaneModus", false);
        //bisher noch nicht gebraucht
        //Boolean expertMode = sharedPref.getBoolean("expertModus", false);

        //TODO: Daten holen für gewonnen/verloren

        //GesamtSpielzahl updaten
        int temp = sharedPref.getInt("spieleGesamt", 0);
        editor.putInt("spieleGesamt", temp+1);
        editor.commit();

        //TODO gesamtGewonnen updaten


        if (insaneMode == true){
            int temp2 = sharedPref.getInt("insaneSpiele", 0);
            editor.putInt("insaneSpiele", temp2+1);
            editor.commit();

            //TODO wenn gewonnen insane gewonnen updaten

        } else {

            int temp3 = sharedPref.getInt("normaleSpiele", 0);
            editor.putInt("normaleSpiele", temp3+1);
            editor.commit();

            //TODO wenn gewonnen normale gewonnen updaten
        }

    }

    public boolean newHighscore(){

        return true;
    }



}
