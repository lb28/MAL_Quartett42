package de.uulm.dbis.quartett42;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HighscoreActivity extends AppCompatActivity {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    TextView ersterName, ersterPunkte, zweiterName, zweiterPunkte, dritterName, dritterPunkte;
    TextView vierterName, vierterPunkte, fuenfterName, fuenfterPunkte;

    Button rundenButton, punkteButton, zeitButton;

    String ersterNameRunden, zweiterNameRunden, dritterNameRunden, vierterNameRunden, fuenfterNameRunden;
    String ersterNamePunkte, zweiterNamePunkte, dritterNamePunkte, vierterNamePunkte, fuenfterNamePunkte;
    String ersterNameZeit, zweiterNameZeit, dritterNameZeit, vierterNameZeit, fuenfterNameZeit;

    int ersterPunkteRunden, zweiterPunkteRunden, dritterPunkteRunden, vierterPunkteRunden, fuenfterPunkteRunden;
    int ersterPunktePunkte, zweiterPunktePunkte, dritterPunktePunkte, vierterPunktePunkte, fuenfterPunktePunkte;
    int ersterPunkteZeit, zweiterPunkteZeit, dritterPunkteZeit, vierterPunkteZeit, fuenfterPunkteZeit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);


        ersterName = (TextView) findViewById(R.id.NamePosEins);
        zweiterName = (TextView) findViewById(R.id.NamePosZwei);
        dritterName = (TextView) findViewById(R.id.NamePosDrei);
        vierterName = (TextView) findViewById(R.id.NamePosVier);
        fuenfterName = (TextView) findViewById(R.id.NamePosFuenf);

        ersterPunkte = (TextView) findViewById(R.id.PunktePosEins);
        zweiterPunkte = (TextView) findViewById(R.id.PunktePosZwei);
        dritterPunkte = (TextView) findViewById(R.id.PunktePosDrei);
        vierterPunkte = (TextView) findViewById(R.id.PunktePosVier);
        fuenfterPunkte = (TextView) findViewById(R.id.PunktePosFuenf);

        rundenButton = (Button) findViewById(R.id.roundsButton);
        punkteButton = (Button) findViewById(R.id.pointsButton);
        zeitButton = (Button) findViewById(R.id.timeButton);

        ersterNameRunden = sharedPref.getString("ersterNameRunden", "Default Name");
        zweiterNameRunden = sharedPref.getString("zweiterNameRunden", "Default Name");
        dritterNameRunden = sharedPref.getString("dritterNameRunden", "Default Name");
        vierterNameRunden = sharedPref.getString("vierterNameRunden", "Default Name");
        fuenfterNameRunden = sharedPref.getString("fuenfterNameRunden", "Default Name");
        ersterNamePunkte = sharedPref.getString("ersterNamePunkte", "Default Name2");
        zweiterNamePunkte = sharedPref.getString("zweiterNamePunkte", "Default Name2");
        dritterNamePunkte = sharedPref.getString("dritterNamePunkte", "Default Name2");
        vierterNamePunkte = sharedPref.getString("vierterNamePunkte", "Default Name2");
        fuenfterNamePunkte = sharedPref.getString("fuenfterNamePunkte", "Default Name2");
        ersterNameZeit = sharedPref.getString("ersterNameZeit", "Default Name3");
        zweiterNameZeit = sharedPref.getString("zweiterNameZeit", "Default Name3");
        dritterNameZeit = sharedPref.getString("dritterNameZeit", "Default Name3");
        vierterNameZeit = sharedPref.getString("vierterNameZeit", "Default Name3");
        fuenfterNameZeit = sharedPref.getString("fuenfterNameZeit", "Default Name3");

        ersterPunkteRunden = sharedPref.getInt("ersterPunkteRunden", -1);
        zweiterPunkteRunden = sharedPref.getInt("zweiterPunkteRunden", -1);
        dritterPunkteRunden = sharedPref.getInt("dritterPunkteRunden", -1);
        vierterPunkteRunden = sharedPref.getInt("vierterPunkteRunden", -1);
        fuenfterPunkteRunden = sharedPref.getInt("fuenfterPunkteRunden", -1);
        ersterPunktePunkte = sharedPref.getInt("ersterPunktePunkte", -2);
        zweiterPunktePunkte = sharedPref.getInt("zweiterPunktePunkte", -2);
        dritterPunktePunkte = sharedPref.getInt("dritterPunktePunkte", -2);
        vierterPunktePunkte = sharedPref.getInt("vierterPunktePunkte", -2);
        fuenfterPunktePunkte = sharedPref.getInt("fuenfterPunktePunkte", -2);
        ersterPunkteZeit = sharedPref.getInt("ersterPunkteZeit", -3);
        zweiterPunkteZeit = sharedPref.getInt("zweiterPunkteZeit", -3);
        dritterPunkteZeit = sharedPref.getInt("dritterPunkteZeit", -3);
        vierterPunkteZeit = sharedPref.getInt("vierterPunkteZeit", -3);
        fuenfterPunkteZeit = sharedPref.getInt("fuenfterPunkteZeit", -3);

        rundenButton.setBackgroundColor(Color.GRAY);
        punkteButton.setBackgroundColor(Color.WHITE);
        zeitButton.setBackgroundColor(Color.WHITE);

        ersterName.setText(ersterNameRunden);
        zweiterName.setText(zweiterNameRunden);
        dritterName.setText(dritterNameRunden);
        vierterName.setText(vierterNameRunden);
        fuenfterName.setText(fuenfterNameRunden);

        ersterPunkte.setText("" + ersterPunkteRunden);
        zweiterPunkte.setText("" + zweiterPunkteRunden);
        dritterPunkte.setText("" + dritterPunkteRunden);
        vierterPunkte.setText("" + vierterPunkteRunden);
        fuenfterPunkte.setText("" + fuenfterPunkteRunden);
    }

    //Button Methoden
    public void clickPointsButtonHighscoreFunction(View view){

        punkteButton.setBackgroundColor(Color.GRAY);
        rundenButton.setBackgroundColor(Color.WHITE);
        zeitButton.setBackgroundColor(Color.WHITE);

        ersterName.setText(ersterNamePunkte);
        zweiterName.setText(zweiterNamePunkte);
        dritterName.setText(dritterNamePunkte);
        vierterName.setText(vierterNamePunkte);
        fuenfterName.setText(fuenfterNamePunkte);

        ersterPunkte.setText("" + ersterPunktePunkte);
        zweiterPunkte.setText("" + zweiterPunktePunkte);
        dritterPunkte.setText("" + dritterPunktePunkte);
        vierterPunkte.setText("" + vierterPunktePunkte);
        fuenfterPunkte.setText("" + fuenfterPunktePunkte);

    }

    public void clickRoundsButtonHighscoreFunction(View view){

        punkteButton.setBackgroundColor(Color.WHITE);
        rundenButton.setBackgroundColor(Color.GRAY);
        zeitButton.setBackgroundColor(Color.WHITE);

        ersterName.setText(ersterNameRunden);
        zweiterName.setText(zweiterNameRunden);
        dritterName.setText(dritterNameRunden);
        vierterName.setText(vierterNameRunden);
        fuenfterName.setText(fuenfterNameRunden);

        ersterPunkte.setText("" + ersterPunkteRunden);
        zweiterPunkte.setText("" + zweiterPunkteRunden);
        dritterPunkte.setText("" + dritterPunkteRunden);
        vierterPunkte.setText("" + vierterPunkteRunden);
        fuenfterPunkte.setText("" + fuenfterPunkteRunden);

    }

    public void clickTimeButtonHighscoreFunction(View view){

        punkteButton.setBackgroundColor(Color.WHITE);
        rundenButton.setBackgroundColor(Color.WHITE);
        zeitButton.setBackgroundColor(Color.GRAY);

        ersterName.setText(ersterNameZeit);
        zweiterName.setText(zweiterNameZeit);
        dritterName.setText(dritterNameZeit);
        vierterName.setText(vierterNameZeit);
        fuenfterName.setText(fuenfterNameZeit);

        ersterPunkte.setText("" + ersterPunkteZeit);
        zweiterPunkte.setText("" + zweiterPunkteZeit);
        dritterPunkte.setText("" + dritterPunkteZeit);
        vierterPunkte.setText("" + vierterPunkteZeit);
        fuenfterPunkte.setText("" + fuenfterPunkteZeit);

    }

    public void resetHighscoresFunction(View view){

        editor = sharedPref.edit();

        editor.putInt("ersterPunktePunkte", -1);
        editor.putInt("zweiterPunktePunkte", -1);
        editor.putInt("dritterPunktePunkte", -1);
        editor.putInt("vierterPunktePunkte", -1);
        editor.putInt("fuenfterPunktePunkte", -1);

        editor.putString("ersterNamePunkte", "Default");
        editor.putString("zweiterNamePunkte", "Default");
        editor.putString("dritterNamePunkte", "Default");
        editor.putString("vierterNamePunkte", "Default");
        editor.putString("fuenfterNamePunkte", "Default");

        editor.putInt("ersterPunkteRunden", -1);
        editor.putInt("zweiterPunkteRunden", -1);
        editor.putInt("dritterPunkteRunden", -1);
        editor.putInt("vierterPunkteRunden", -1);
        editor.putInt("fuenfterPunkteRunden", -1);

        editor.putString("ersterNameRunden", "Default");
        editor.putString("zweiterNameRunden", "Default");
        editor.putString("dritterNameRunden", "Default");
        editor.putString("vierterNameRunden", "Default");
        editor.putString("fuenfterNameRunden", "Default");

        editor.putInt("ersterPunkteZeit", -1);
        editor.putInt("zweiterPunkteZeit", -1);
        editor.putInt("dritterPunkteZeit", -1);
        editor.putInt("vierterPunkteZeit", -1);
        editor.putInt("fuenfterPunkteZeit", -1);

        editor.putString("ersterNameZeit", "Default");
        editor.putString("zweiterNameZeit", "Default");
        editor.putString("dritterNameZeit", "Default");
        editor.putString("vierterNameZeit", "Default");
        editor.putString("fuenfterNameZeit", "Default");

        editor.apply();

        finish();

    }



}
