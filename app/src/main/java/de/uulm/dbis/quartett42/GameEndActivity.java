package de.uulm.dbis.quartett42;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import de.uulm.dbis.quartett42.data.Game;

public class GameEndActivity extends AppCompatActivity {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Intent intent;

    private int winner;

    //Elemente:

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_end);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();

        //statistik updaten
        updateStatistics();

        // Intent holen
        Intent intent = getIntent();

        //Gewonnen/Verloren anzeigen
        int pointsPlayer = intent.getIntExtra("pointsPlayer", -1);
        int pointsComputer = intent.getIntExtra("pointsComputer", -1);
        TextView winnerText = (TextView) findViewById(R.id.matchAusgang);

        winner = Game.WINNER_DRAW;
        if (pointsPlayer > pointsComputer) {
            winner = Game.WINNER_PLAYER;
            winnerText.setText("Gewonnen!");
            winnerText.setTextColor(Color.GREEN);
        }
        else if (pointsPlayer < pointsComputer) {
            winner = Game.WINNER_COMPUTER;
            winnerText.setText("Verloren...");
            winnerText.setTextColor(Color.RED);
        }

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

        //TODO gesamtGewonnen updaten


        if (insaneMode){
            int temp2 = sharedPref.getInt("insaneSpiele", 0);
            editor.putInt("insaneSpiele", temp2+1);

            //TODO wenn gewonnen insane gewonnen updaten

        } else {

            int temp3 = sharedPref.getInt("normaleSpiele", 0);
            editor.putInt("normaleSpiele", temp3+1);

            //TODO wenn gewonnen normale gewonnen updaten
        }

        editor.apply();

    }

    public boolean newHighscore(){

        return true;
    }

    public void goToMainMenu(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
