package de.uulm.dbis.quartett42;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    public String jsonString = "";
    SharedPreferences sharedPref;
    ProgressBar spinner; //Spinner fuer Ladezeiten

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.bringToFront();
        spinner.setVisibility(View.VISIBLE);

        //Daten laden, runOnUiThread weil UI angepasst wird, keine Ahnung ob es was besseres gibt
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadData();
            }});
    }

    //add menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        spinner.setVisibility(View.GONE);
    }

    //Methoden fuer Button-Klicks:

    //Galerie Button:
    public void clickGalleryButtonFunction(View view){
        spinner.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, GalleryActivity.class);
        intent.putExtra("json_string", jsonString);
        startActivity(intent);
    }

    //Settings Button:
    public void clickSettingsButtonFunction(View view){
        spinner.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, SettingActivity.class);
        intent.putExtra("setting_source", "main");
        startActivity(intent);
    }

    //NewGame Button:
    public void clickNewGameButtonFunction(View view){
        //Gucken, ob ein laufendes Spiel vorhanden:
        //runningGame Value lesen, 1 falls Spiel pausiert, 0 wenn nicht:
        System.out.println("laufendes Spiel --------- "+sharedPref.getInt("runningGame", 500));
        if(sharedPref.getInt("runningGame", 0) == 1){
            //Fragen, ob vorhandenes Spiel fortgesetzt werden soll oder neues begonnen werden soll
            String[] aktionen = {"Ja, Spiel fortsetzen", "Nein, Neues Spiel starten"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Soll ein pausiertes Spiel fortgesetzt werden?")
                    .setItems(aktionen, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position of the selected item
                            if(which == 0){
                                // vorhandenes Spiel laden:
                                loadGame();
                            }else if(which == 1){
                                //alte gespeichertes Spiel loeschen
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putInt("runningGame", 0);
                                editor.putInt("currentRoundsLeft", 0);
                                editor.putInt("currentPointsPlayer", 0);
                                editor.putInt("currentPointsComputer", 0);
                                editor.putString("currentCardsPlayer", "");
                                editor.putString("currentCardsComputer", "");
                                editor.commit();
                                //Zu NewGameActivity weiter leiten
                                startNewGame();
                            }
                        }
                    });
            AlertDialog alert =  builder.create();
            alert.show();
        }else{
            //Zu NewGameActivity weiter leiten
            startNewGame();
        }

    }

    //Statistics Button:
    public void clickStatisticsButtonFunction(View view){
        spinner.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }

    //Highscore Button:
    public void clickHighscoreButtonFunction(View view){
        spinner.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, HighscoreActivity.class);
        startActivity(intent);
    }


    //Menue-Items:
    public void clickGuideFunction(MenuItem item){
        if(item.getTitle().equals("Anleitung")){
            spinner.setVisibility(View.VISIBLE);
            Intent intent = new Intent(this, GuideActivity.class);
            startActivity(intent);
        }else if(item.getTitle().equals("Info")){
            spinner.setVisibility(View.VISIBLE);
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
        }
    }

    //Methoden der Activity:

    //Lade-Methode fuer Start-Daten und JSON-Datei:
    public void loadData(){
        //Test: Willkommen Text laden:
        TextView welcomeText = (TextView)findViewById(R.id.welcomeText);
        try {
            InputStream json = getAssets().open("welcometext.txt");
            BufferedReader in=
                    new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String welcomeMessage = "";
            String str = "";
            while ((str = in.readLine()) != null) {
                welcomeMessage = welcomeMessage+str;
            }
            welcomeText.setText(welcomeMessage);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

/*
        //Test: Startbild laden:
        AssetManager assetManager = getAssets();
        InputStream is = null;
        ImageView welcomeImage = (ImageView)findViewById(R.id.welcomeImage);
        try {
            is = assetManager.open("menu_image_cut.gif");
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            welcomeImage.setImageBitmap(bitmap);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
*/

        //JSON-String lesesn, damit er nicht jedes mal neu gelesen werden muss:
        try {
            InputStream in = getAssets().open("jsonexample.json");
            int size = in.available();
            byte[] buffer = new byte[size];
            System.out.println(in.read(buffer));
            in.close();
            jsonString = new String(buffer, "UTF-8");
            //System.out.println(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    //Neues Spiel starten:
    public void startNewGame(){
        spinner.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, NewGameActivity.class);
        intent.putExtra("json_string", jsonString);
        intent.putExtra("new_game_source", "main_activity");
        startActivity(intent);
    }

    public void loadGame(){
        spinner.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("chosen_deck", sharedPref.getString("currentChosenDeck", "Sesamstrasse"));
        intent.putExtra("json_string", jsonString);
        startActivity(intent);
    }

}
