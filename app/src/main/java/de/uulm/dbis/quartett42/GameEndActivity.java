package de.uulm.dbis.quartett42;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import de.uulm.dbis.quartett42.data.Game;

public class GameEndActivity extends AppCompatActivity {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    TextView winnerText, inDieTop5;
    Button rangliste;
    EditText nameEintragen;

    private int winner;

    //Elemente:

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_end);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();

        winnerText = (TextView) findViewById(R.id.matchAusgang);
        inDieTop5 = (TextView) findViewById(R.id.inDieTop5);
        rangliste = (Button) findViewById(R.id.inRangliste);
        nameEintragen = (EditText) findViewById(R.id.nameEintragen);

        // Intent holen
        Intent intent = getIntent();

        //Gewonnen/Verloren anzeigen
        int pointsPlayer = intent.getIntExtra("pointsPlayer", -1);
        int pointsComputer = intent.getIntExtra("pointsComputer", -1);

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
            //da nicht gewonnen wird die Ranglistenfunktion nicht angezeigt
            inDieTop5.setVisibility(View.INVISIBLE);
            rangliste.setVisibility(View.INVISIBLE);
            nameEintragen.setVisibility(View.INVISIBLE);
        }

        //statistik updaten
        updateStatistics();

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

        //
        boolean higherThanFifthPlace = newHighscore(pointsPlayer, spielmodus);



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


        //GesamtSpielzahl immer updaten
        int temp = sharedPref.getInt("spieleGesamt", 0);
        editor.putInt("spieleGesamt", temp+1);

        //und je nach mode
        //Gesamtspielzahl für insane
        if (insaneMode){
            int temp2 = sharedPref.getInt("insaneSpiele", 0);
            editor.putInt("insaneSpiele", temp2+1);

        }
        //Gesamtspielzahl für normal
        else {

            int temp3 = sharedPref.getInt("normaleSpiele", 0);
            editor.putInt("normaleSpiele", temp3+1);

        }

        //nur bei Gewinn wird die gewonnen-Statistik hochgezählt
        if (winner == Game.WINNER_PLAYER){
            //Gewonnen gesamt hochzählen
            int temp4 = sharedPref.getInt("spieleGesamtGewonnen", 0);
            editor.putInt("spieleGesamtGewonnen", temp4+1);

            //GewonnenesSpiel für insane
            if (insaneMode){
                int temp5 = sharedPref.getInt("insaneSpieleGewonnen", 0);
                editor.putInt("insaneSpieleGewonnen", temp5+1);

            }
            //Gewonnenes Spiel für normal
            else {

                int temp6 = sharedPref.getInt("normaleSpieleGewonnen", 0);
                editor.putInt("normaleSpieleGewonnen", temp6+1);

            }
        }


        editor.apply();

    }

    public boolean newHighscore(int pointsPlayer, int gamemode){

        int punktevergleich;
        int vergleich;

        if (gamemode == 1){

            vergleich = sharedPref.getInt("fuenfterPunkteRunden", -1);

        } else if (gamemode == 2){

            vergleich = sharedPref.getInt("fuenfterPunkteZeit", -1);

        } else{

            vergleich = sharedPref.getInt("fuenfterPunktePunkte", -1);
        }

        //die erreichten Punkte des Spielers sind kleiner als der unterste Wert in den Highscores
        // -> in die rangliste eintragen nicht möglich
        if (vergleich > pointsPlayer){
            inDieTop5.setVisibility(View.INVISIBLE);
            rangliste.setVisibility(View.INVISIBLE);
            nameEintragen.setVisibility(View.INVISIBLE);

            return false;
        }

        return true;
    }

    public void goToMainMenu(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
