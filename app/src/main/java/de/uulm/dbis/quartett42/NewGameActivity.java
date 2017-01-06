package de.uulm.dbis.quartett42;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.uulm.dbis.quartett42.data.Deck;
import de.uulm.dbis.quartett42.data.ImageCard;

public class NewGameActivity extends AppCompatActivity {
    String jsonString = "";
    String chosenDeck = "";
    ArrayList<Deck> deckList;
    GridView gridView;
    GridViewAdapter gridAdapter;

    TextView deckGameText;
    TextView modeGameText;
    TextView roundsLeftGameText;
    TextView difficultyGameText;
    TextView insaneGameText;
    TextView soundGameText;

    SharedPreferences sharedPref;
    ProgressBar spinner; //Spinner fuer Ladezeiten

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        //JSON-String auslesen:
        Intent intent = getIntent();
        jsonString = intent.getStringExtra("json_string");
        //System.out.println(jsonString);

        deckGameText = (TextView)findViewById(R.id.deckGameText);
        modeGameText = (TextView)findViewById(R.id.varianteGameText);
        roundsLeftGameText = (TextView)findViewById(R.id.anzahlGameText);
        difficultyGameText = (TextView)findViewById(R.id.schwierigkeitGameText);
        insaneGameText = (TextView)findViewById(R.id.insaneGameTExt);
        soundGameText = (TextView)findViewById(R.id.soundGameText);

        //Decks laden:
        new Thread(new Runnable() {
            public void run() {
                loadData();
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Einstellungen aus DefaultSharedPreferences laden:
        deckGameText.setText("Deck: "+ chosenDeck);

        int mode = sharedPref.getInt("mode", 1);
        if(mode == 1){
            modeGameText.setText("Spielmodus: rundenbasiert");
            roundsLeftGameText.setText("Anzahl Runden: "+sharedPref.getInt("roundsLeft", 10));
        }else if(mode == 2){
            modeGameText.setText("Spielmodus: zeitbasiert");
            roundsLeftGameText.setText("Spielminuten: "+sharedPref.getInt("roundsLeft", 10));
        }else{
            modeGameText.setText("Spielmodus: punktebasiert");
            roundsLeftGameText.setText("Punktelimit: "+sharedPref.getInt("roundsLeft", 10));
        }

        int difficulty = sharedPref.getInt("difficulty", 2);
        if(difficulty == 1){
            difficultyGameText.setText("Schwierigkeit: leicht");
        }else if(difficulty == 2){
            difficultyGameText.setText("Schwierigkeit: mittel");
        }else{
            difficultyGameText.setText("Schwierigkeit: schwer");
        }

        if(sharedPref.getBoolean("insaneModus", false)){
            insaneGameText.setText("Insane Modus: ein");
        }else{
            insaneGameText.setText("Insane Modus: aus");
        }

        if(sharedPref.getBoolean("soundModus", true)){
            soundGameText.setText("Sound: ein");
        }else{
            soundGameText.setText("Sound: aus");
        }

        spinner.setVisibility(View.GONE);
    }

    //Button Klick Methoden:

    //Einstellungen anpassen:
    public void clickChangeSettingsButtonFunction(View view){
        spinner.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, SettingActivity.class);
        intent.putExtra("setting_soucre", "new_game");
        startActivity(intent);
    }

    //Spiel starten:
    public void clickStartGameButtonFunction(View view){
        //Ueberpruefen, ob ein Deck gewaehlt wurde:
        if(chosenDeck.equals("")){
            Toast.makeText(getApplicationContext(), "Bitte zuerst ein Deck aussuchen!", Toast.LENGTH_SHORT).show();
        }else{
            //Spiel starten und ausgesuchtes Deck sowie JSON-String mitgeben
            //TODO
            Toast.makeText(getApplicationContext(), "TODO", Toast.LENGTH_SHORT).show();
        }
    }

    //Methoden der Activity:

    //Decks laden:
    public void loadData(){
        //ArrayList aller Decks aus JSON erstellen
        deckList = new ArrayList<Deck>();
        try {
            // Getting JSON Array node
            JSONObject jsonObj = new JSONObject(jsonString);
            JSONArray decks = jsonObj.getJSONArray("decks");
            for (int i = 0; i < decks.length(); i++) {
                JSONObject tmpDeck = decks.getJSONObject(i);
                String deckName = tmpDeck.getString("name");
                String deckDescription = tmpDeck.getString("description");
                String deckImageUri = tmpDeck.getString("image");
                //Cards und Properties sind erst mal egal fuer die Deckuebersicht

                ImageCard newImage = new ImageCard(deckImageUri, deckDescription);
                Deck newDeck = new Deck(deckName, newImage, null, null);
                deckList.add(newDeck);
            }

            //Test-Ausgabe der Daten:
            for(int i = 0; i < deckList.size(); i++){
                System.out.println("Deck "+deckList.get(i).getName()+": "+deckList.get(i).getImage().getDescription());
            }

            try{
                //Als Grid-Layout setzen:
                gridView = (GridView) findViewById(R.id.galleryGridView);
                gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, deckList);
                gridView.setAdapter(gridAdapter);
            }catch(Exception e){
                e.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //On-Item-Click-Listener fuer einzelne Decks:
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Deck item = (Deck) parent.getItemAtPosition(position);
                chosenDeck = item.getName();
                deckGameText.setText("Deck: "+ chosenDeck);
            }

        });
    }
}
