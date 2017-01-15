package de.uulm.dbis.quartett42;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.uulm.dbis.quartett42.data.Game;

public class GameEndActivity extends AppCompatActivity {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    TextView winnerText, inDieTop5, endstand;
    Button rangliste;
    EditText nameEintragen;

    private int winner;
    private int schwierigkeit;
    private int erreichtePunkteSpieler;
    private int pointsPlayer, pointsComputer;

    private String nameFürRangliste = "";

    List<String> highscorenamen;
    List<Integer> highscorepunkte;

    /*
        METRIK FÜR HIGHSCORES:
        Rundenmodus: Runden * Punkte * Schwierigkeit * (2 für Expertmodus)
        Zeitmodus: Spielzeit * Punkte * Schwierigkeit * (2 für Expertmodus)
        Punktemodus: Punkte (der Einstellungen) * erreichte Punkte * Schwierigkeit * (2 für Expertmodus)
    */

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
        endstand = (TextView) findViewById(R.id.endPunkteStand);

        highscorenamen = new ArrayList<>();
        highscorepunkte = new ArrayList<>();

        // Intent holen
        Intent intent = getIntent();

        //Gewonnen/Verloren anzeigen
        pointsPlayer = intent.getIntExtra("pointsPlayer", -1);
        pointsComputer = intent.getIntExtra("pointsComputer", -1);

        endstand.setText("Endstand: "+pointsPlayer+" : "+pointsComputer);

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

        boolean higherThanFifthPlace = false;
        if(winner == Game.WINNER_PLAYER){
            higherThanFifthPlace = newHighscore(pointsPlayer, spielmodus);
        }


        //falls der wert in den top5 ist
        if (higherThanFifthPlace == true){

            inDieTop5.setVisibility(View.VISIBLE);
            rangliste.setVisibility(View.VISIBLE);
            nameEintragen.setVisibility(View.VISIBLE);

        }

    }


    //update die statistiken
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


    //sind die erspielten punkte in den top5?
    //true -> ja
    //false -> nein
    public boolean newHighscore(int pointsPlayer, int gamemode){

        int punktevergleich;
        int vergleich;
        int expert = 1;
        boolean tmp = sharedPref.getBoolean("expertModus", false);

        if (tmp == true){
            expert = 2;
        }

        if (gamemode == 1){

            int runden = sharedPref.getInt("roundsLeft", 1);

            vergleich = sharedPref.getInt("fuenfterPunkteRunden", -1);
            punktevergleich = runden * pointsPlayer * schwierigkeit * expert;

        } else if (gamemode == 2){

            int zeit = sharedPref.getInt("roundsLeft", 10);

            vergleich = sharedPref.getInt("fuenfterPunkteZeit", -1);
            punktevergleich = zeit * pointsPlayer * schwierigkeit * expert;

        } else{

            int punkte = sharedPref.getInt("pointsLeft", 1000);

            vergleich = sharedPref.getInt("fuenfterPunktePunkte", -1);
            punktevergleich = punkte * pointsPlayer * schwierigkeit * expert;
        }

        //die erreichten Punkte des Spielers sind kleiner als der unterste Wert in den Highscores
        // -> in die rangliste eintragen nicht möglich
        if (vergleich > punktevergleich){
            inDieTop5.setVisibility(View.INVISIBLE);
            rangliste.setVisibility(View.INVISIBLE);
            nameEintragen.setVisibility(View.INVISIBLE);

            return false;
        }

        return true;
    }


    //die erspielten punkte und den eingegebenen namen in die highscores einfügen
    public void clickInsertHighscoreFunction(View view){

        int spielmodus = sharedPref.getInt("mode", 0);
        schwierigkeit = sharedPref.getInt("difficulty", 0);
        boolean tmp = sharedPref.getBoolean("expertModus", false);
        int expert = 1;
        if (tmp == true){
            expert = 2;
        }
        System.out.println("expert= " + expert);

        nameFürRangliste = nameEintragen.getText().toString();
        System.out.println("nameFürRangliste= " + nameFürRangliste);

        if (spielmodus == 1){

            //punkte gesamt berechnen
            erreichtePunkteSpieler = sharedPref.getInt("roundsLeft", 1) * pointsPlayer * schwierigkeit * expert;

            //alle highscorewerte und -namen holen und in zwei listen schreiben
            highscorenamen.add(sharedPref.getString("ersterNameRunden", "Default Name"));
            highscorenamen.add(sharedPref.getString("zweiterNameRunden", "Default Name"));
            highscorenamen.add(sharedPref.getString("dritterNameRunden", "Default Name"));
            highscorenamen.add(sharedPref.getString("vierterNameRunden", "Default Name"));
            highscorenamen.add(sharedPref.getString("fuenfterNameRunden", "Default Name"));

            highscorepunkte.add(sharedPref.getInt("ersterPunkteRunden", -1));
            highscorepunkte.add(sharedPref.getInt("zweiterPunkteRunden", -1));
            highscorepunkte.add(sharedPref.getInt("dritterPunkteRunden", -1));
            highscorepunkte.add(sharedPref.getInt("vierterPunkteRunden", -1));
            highscorepunkte.add(sharedPref.getInt("fuenfterPunkteRunden", -1));

            //name und punkte an der richtigen stelle einsortieren und 6. stelle löschen
            if (erreichtePunkteSpieler > highscorepunkte.get(0)){
                highscorepunkte.add(0, erreichtePunkteSpieler);
                highscorepunkte.remove(5);
                highscorenamen.add(0, nameEintragen.getText().toString());
                highscorenamen.remove(5);

            } else if (erreichtePunkteSpieler < highscorepunkte.get(0) && pointsPlayer > highscorepunkte.get(1)){
                highscorepunkte.add(1, erreichtePunkteSpieler);
                highscorepunkte.remove(5);
                highscorenamen.add(1, nameEintragen.getText().toString());
                highscorenamen.remove(5);

            } else if (erreichtePunkteSpieler < highscorepunkte.get(1) && pointsPlayer > highscorepunkte.get(2)){
                highscorepunkte.add(2, erreichtePunkteSpieler);
                highscorepunkte.remove(5);
                highscorenamen.add(2, nameEintragen.getText().toString());
                highscorenamen.remove(5);

            } else if (erreichtePunkteSpieler < highscorepunkte.get(2) && pointsPlayer > highscorepunkte.get(3)){
                highscorepunkte.add(3, erreichtePunkteSpieler);
                highscorepunkte.remove(5);
                highscorenamen.add(3, nameEintragen.getText().toString());
                highscorenamen.remove(5);

            } else{
                highscorepunkte.add(4, erreichtePunkteSpieler);
                highscorepunkte.remove(5);
                highscorenamen.add(4, nameEintragen.getText().toString());
                highscorenamen.remove(5);

            }

            //sharedpreferences updaten
            editor.putInt("ersterPunkteRunden", highscorepunkte.get(0));
            editor.putInt("zweiterPunkteRunden", highscorepunkte.get(1));
            editor.putInt("dritterPunkteRunden", highscorepunkte.get(2));
            editor.putInt("vierterPunkteRunden", highscorepunkte.get(3));
            editor.putInt("fuenfterPunkteRunden", highscorepunkte.get(4));

            editor.putString("ersterNameRunden", highscorenamen.get(0));
            editor.putString("zweiterNameRunden", highscorenamen.get(1));
            editor.putString("dritterNameRunden", highscorenamen.get(2));
            editor.putString("vierterNameRunden", highscorenamen.get(3));
            editor.putString("fuenfterNameRunden", highscorenamen.get(4));

            editor.apply();

            Toast.makeText(getApplicationContext(), "In Rangliste eingetragen", Toast.LENGTH_SHORT).show();

            rangliste.setClickable(false);


        } else if (spielmodus == 2){

            //punkte gesamt berechnen
            erreichtePunkteSpieler = sharedPref.getInt("roundsLeft", 1) * pointsPlayer * schwierigkeit * expert;

            //alle highscorewerte und -namen holen und in zwei listen schreiben
            highscorenamen.add(sharedPref.getString("ersterNameZeit", "Default Name"));
            highscorenamen.add(sharedPref.getString("zweiterNameZeit", "Default Name"));
            highscorenamen.add(sharedPref.getString("dritterNameZeit", "Default Name"));
            highscorenamen.add(sharedPref.getString("vierterNameZeit", "Default Name"));
            highscorenamen.add(sharedPref.getString("fuenfterNameZeit", "Default Name"));

            highscorepunkte.add(sharedPref.getInt("ersterPunkteZeit", -1));
            highscorepunkte.add(sharedPref.getInt("zweiterPunkteZeit", -1));
            highscorepunkte.add(sharedPref.getInt("dritterPunkteZeit", -1));
            highscorepunkte.add(sharedPref.getInt("vierterPunkteZeit", -1));
            highscorepunkte.add(sharedPref.getInt("fuenfterPunkteZeit", -1));

            //name und punkte an der richtigen stelle einsortieren und 6. stelle löschen
            if (erreichtePunkteSpieler > highscorepunkte.get(0)){
                highscorepunkte.add(0, erreichtePunkteSpieler);
                highscorepunkte.remove(5);
                highscorenamen.add(0, nameEintragen.getText().toString());
                highscorenamen.remove(5);

            } else if (erreichtePunkteSpieler < highscorepunkte.get(0) && pointsPlayer > highscorepunkte.get(1)){
                highscorepunkte.add(1, erreichtePunkteSpieler);
                highscorepunkte.remove(5);
                highscorenamen.add(1, nameEintragen.getText().toString());
                highscorenamen.remove(5);

            } else if (erreichtePunkteSpieler < highscorepunkte.get(1) && pointsPlayer > highscorepunkte.get(2)){
                highscorepunkte.add(2, erreichtePunkteSpieler);
                highscorepunkte.remove(5);
                highscorenamen.add(2, nameEintragen.getText().toString());
                highscorenamen.remove(5);

            } else if (erreichtePunkteSpieler < highscorepunkte.get(2) && pointsPlayer > highscorepunkte.get(3)){
                highscorepunkte.add(3, erreichtePunkteSpieler);
                highscorepunkte.remove(5);
                highscorenamen.add(3, nameEintragen.getText().toString());
                highscorenamen.remove(5);

            } else{
                highscorepunkte.add(4, erreichtePunkteSpieler);
                highscorepunkte.remove(5);
                highscorenamen.add(4, nameEintragen.getText().toString());
                highscorenamen.remove(5);

            }

            //sharedpreferences updaten
            editor.putInt("ersterPunkteZeit", highscorepunkte.get(0));
            editor.putInt("zweiterPunkteZeit", highscorepunkte.get(1));
            editor.putInt("dritterPunkteZeit", highscorepunkte.get(2));
            editor.putInt("vierterPunkteZeit", highscorepunkte.get(3));
            editor.putInt("fuenfterPunkteZeit", highscorepunkte.get(4));

            editor.putString("ersterNameZeit", highscorenamen.get(0));
            editor.putString("zweiterNameZeit", highscorenamen.get(1));
            editor.putString("dritterNameZeit", highscorenamen.get(2));
            editor.putString("vierterNameZeit", highscorenamen.get(3));
            editor.putString("fuenfterNameZeit", highscorenamen.get(4));

            editor.apply();

            Toast.makeText(getApplicationContext(), "In Rangliste eingetragen", Toast.LENGTH_SHORT).show();

            rangliste.setClickable(false);

        } else{

            Log.i("My App", "ICH BIN IM PUNKTEMODUS");

            //punkte gesamt berechnen
            erreichtePunkteSpieler = sharedPref.getInt("pointsLeft", 1) * pointsPlayer * schwierigkeit * expert;
            Log.i("My App","erreichtePunkteSpie= " + erreichtePunkteSpieler);

            //alle highscorewerte und -namen holen und in zwei listen schreiben
            highscorenamen.add(0,sharedPref.getString("ersterNamePunkte", "Default Name"));
            highscorenamen.add(1,sharedPref.getString("zweiterNamePunkte", "Default Name"));
            highscorenamen.add(2,sharedPref.getString("dritterNamePunkte", "Default Name"));
            highscorenamen.add(3,sharedPref.getString("vierterNamePunkte", "Default Name"));
            highscorenamen.add(4,sharedPref.getString("fuenfterNamePunkte", "Default Name"));

            Log.i("My App","highscorenamen vor einfügen");
            for (int i = 0; i<highscorenamen.size(); i++){
                Log.i("My App",highscorenamen.get(i));
            }

            highscorepunkte.add(0,sharedPref.getInt("ersterPunktePunkte", -1));
            highscorepunkte.add(1,sharedPref.getInt("zweiterPunktePunkte", -1));
            highscorepunkte.add(2,sharedPref.getInt("dritterPunktePunkte", -1));
            highscorepunkte.add(3,sharedPref.getInt("vierterPunktePunkte", -1));
            highscorepunkte.add(4,sharedPref.getInt("fuenfterPunktePunkte", -1));

            Log.i("My App","highscorepunkte vor einfügen");
            for (int i = 0; i<highscorepunkte.size(); i++){
                Log.i("My App", highscorepunkte.get(i).toString());
            }

            //name und punkte an der richtigen stelle einsortieren und 6. stelle löschen
            if (erreichtePunkteSpieler > highscorepunkte.get(0)){
                highscorepunkte.add(0, erreichtePunkteSpieler);
                highscorepunkte.remove(5);
                highscorenamen.add(0, nameEintragen.getText().toString());
                highscorenamen.remove(5);

            } else if (erreichtePunkteSpieler <= highscorepunkte.get(0) && pointsPlayer > highscorepunkte.get(1)){
                highscorepunkte.add(1, erreichtePunkteSpieler);
                highscorepunkte.remove(5);
                highscorenamen.add(1, nameEintragen.getText().toString());
                highscorenamen.remove(5);

            } else if (erreichtePunkteSpieler <= highscorepunkte.get(1) && pointsPlayer > highscorepunkte.get(2)){
                highscorepunkte.add(2, erreichtePunkteSpieler);
                highscorepunkte.remove(5);
                highscorenamen.add(2, nameEintragen.getText().toString());
                highscorenamen.remove(5);

            } else if (erreichtePunkteSpieler <= highscorepunkte.get(2) && pointsPlayer > highscorepunkte.get(3)){
                highscorepunkte.add(3, erreichtePunkteSpieler);
                highscorepunkte.remove(5);
                highscorenamen.add(3, nameEintragen.getText().toString());
                highscorenamen.remove(5);

            } else{
                highscorepunkte.add(4, erreichtePunkteSpieler);
                highscorepunkte.remove(5);
                highscorenamen.add(4, nameEintragen.getText().toString());
                highscorenamen.remove(5);

            }

            Log.i("My App","highscorenamen nach einfügen");
            for (int i = 0; i<highscorenamen.size(); i++){
                Log.i("My App",highscorenamen.get(i));
            }

            Log.i("My App","highscorepunkte nach einfügen");
            for (int i = 0; i<highscorepunkte.size(); i++){
                Log.i("My App",highscorepunkte.get(i).toString());
            }

            //sharedpreferences updaten
            editor.putInt("ersterPunktePunkte", highscorepunkte.get(0));
            editor.putInt("zweiterPunktePunkte", highscorepunkte.get(1));
            editor.putInt("dritterPunktePunkte", highscorepunkte.get(2));
            editor.putInt("vierterPunktePunkte", highscorepunkte.get(3));
            editor.putInt("fuenfterPunktePunkte", highscorepunkte.get(4));

            editor.putString("ersterNamePunkte", highscorenamen.get(0));
            editor.putString("zweiterNamePunkte", highscorenamen.get(1));
            editor.putString("dritterNamePunkte", highscorenamen.get(2));
            editor.putString("vierterNamePunkte", highscorenamen.get(3));
            editor.putString("fuenfterNamePunkte", highscorenamen.get(4));

            editor.apply();

            Toast.makeText(getApplicationContext(), "In Rangliste eingetragen", Toast.LENGTH_SHORT).show();

            rangliste.setClickable(false);

        }
    }

    public void goToHighscores(View view){
        Intent intent = new Intent(this, HighscoreActivity.class);
        startActivity(intent);
    }



    public void goToMainMenu(View view) {
        finish();
    }


}
