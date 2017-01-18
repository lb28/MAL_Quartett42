package de.uulm.dbis.quartett42;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.uulm.dbis.quartett42.data.Deck;
import de.uulm.dbis.quartett42.data.Game;

public class NewGameActivity extends AppCompatActivity {
    String jsonString = "";
    String chosenDeck = "";
    ArrayList<Deck> deckList;
    GridView gridView;
    GridViewAdapter gridAdapter;

    TextView deckGameText;
    TextView modeGameText;
    TextView roundsLeftGameText;
    TextView roundsLeftLabel;
    TextView difficultyGameText;
    TextView insaneGameText;
    TextView expertGameText;
    TextView soundGameText;

    Intent intent;

    SharedPreferences sharedPref;
    ProgressBar spinner; //Spinner fuer Ladezeiten

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.bringToFront();
        spinner.setVisibility(View.VISIBLE);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        //JSON-String auslesen:
        intent = getIntent();
        jsonString = intent.getStringExtra("json_string");

        deckGameText = (TextView)findViewById(R.id.deckGameText);
        modeGameText = (TextView)findViewById(R.id.modusGameText);
        roundsLeftGameText = (TextView)findViewById(R.id.anzahlGameText);
        roundsLeftLabel = (TextView)findViewById(R.id.anzahlRundenLabel);
        difficultyGameText = (TextView)findViewById(R.id.schwierigkeitGameText);
        insaneGameText = (TextView)findViewById(R.id.insaneGameText);
        expertGameText = (TextView)findViewById(R.id.expertGameText);
        soundGameText = (TextView)findViewById(R.id.soundGameText);

        //Decks laden (asynchron):
        loadData();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        //Einstellungen aus DefaultSharedPreferences laden:
        deckGameText.setText(chosenDeck);
        if (chosenDeck.equals("")) {
            deckGameText.setText("bitte wählen");
            deckGameText.setTextColor(Color.RED);
        }

        int mode = sharedPref.getInt("mode", 1);
        if(mode == Game.MODE_ROUNDS){
            modeGameText.setText("Rundenbasiert");
            roundsLeftLabel.setText("Rundenlimit:");
            roundsLeftGameText.setText(""+sharedPref.getInt("roundsLeft", 10));
        }else if(mode == Game.MODE_TIME){
            modeGameText.setText("Zeitbasiert");
            roundsLeftLabel.setText("Spielminuten:");
            //TODO is it correct to use "roundsLeft"?
            roundsLeftGameText.setText(""+sharedPref.getInt("roundsLeft", 10));
        }else{
            modeGameText.setText("Punktebasiert");
            roundsLeftLabel.setText("Punktelimit:");
            roundsLeftGameText.setText(""+sharedPref.getInt("pointsLeft", 1000));
        }

        int difficulty = sharedPref.getInt("difficulty", 2);
        if(difficulty == 1){
            difficultyGameText.setText("leicht");
        }else if(difficulty == 2){
            difficultyGameText.setText("mittel");
        }else{
            difficultyGameText.setText("schwer");
        }

        if(sharedPref.getBoolean("insaneModus", false)){
            insaneGameText.setText("ein");
        }else{
            insaneGameText.setText("aus");
        }

        if(sharedPref.getBoolean("expertModus", false)){
            expertGameText.setText("ein");
        }else{
            expertGameText.setText("aus");
        }

        if(sharedPref.getBoolean("soundModus", true)){
            soundGameText.setText("ein");
        }else{
            soundGameText.setText("aus");
        }

        spinner.setVisibility(View.GONE);
    }

    //Button Klick Methoden:

    //Einstellungen anpassen:
    public void clickChangeSettingsButtonFunction(View view){
        spinner.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, SettingActivity.class);
        intent.putExtra("setting_source", "new_game");
        startActivity(intent);
    }

    //Spiel starten:
    public void clickStartGameButtonFunction(View view){
        //Ueberpruefen, ob ein Deck gewaehlt wurde:
        if(chosenDeck.equals("")){
            Toast.makeText(getApplicationContext(), "Bitte zuerst ein Deck aussuchen!", Toast.LENGTH_SHORT).show();
        }else{
            //Spiel starten und ausgesuchtes Deck sowie JSON-String mitgeben
            spinner.setVisibility(View.VISIBLE);
            Intent intent = new Intent(this, GameActivity.class);
            //intent.putExtra("setting_source", "new_game");
            intent.putExtra("chosen_deck", chosenDeck);
            //intent.putExtra("json_string", jsonString);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    //Methoden der Activity:

    //Decks laden:
    public void loadData(){
        if(intent.getStringExtra("new_game_source").equals("main_activity")) {
            //Falls die Anfrage von der Main Activity aus gestartet wurde:
            //ArrayList aller Decks aus JSON erstellen

            new LoadDecksGridTask().execute();

        } else if(intent.getStringExtra("new_game_source").equals("view_deck_activity")) {
            //Falls von GalleryActivity kommt Linken Oberen Zurueck-Button entfernen:
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            //und Galerie nicht laden, dafuer gleich das uebergebene Deck setzen:
            chosenDeck = intent.getStringExtra("chosen_deck");
            deckGameText.setText(chosenDeck);
            deckGameText.setTextColor(Color.parseColor("#8a000000"));
        }

    }

    private class LoadDecksGridTask extends AsyncTask<Void, Void, ArrayList<Deck>> {

        @Override
        protected ArrayList<Deck> doInBackground(Void... voids) {
            JSONParser jsonParser = new JSONParser(NewGameActivity.this);
            return jsonParser.getAllDecks();
        }

        @Override
        protected void onPostExecute(ArrayList<Deck> deckList) {
            try {
                //Als Grid-Layout setzen:
                gridView = (GridView) findViewById(R.id.galleryGridView);
                gridAdapter = new GridViewAdapter(
                        NewGameActivity.this,
                        R.layout.grid_item_layout,
                        deckList);
                gridView.setAdapter(gridAdapter);

                //On-Item-Click-Listener fuer einzelne Decks:
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        Deck item = (Deck) parent.getItemAtPosition(position);
                        chosenDeck = item.getName();
                        deckGameText.setText(chosenDeck);

                        // reset color to default color
                        deckGameText.setTextColor(Color.parseColor("#8a000000"));
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (spinner != null) {
                spinner.setVisibility(View.GONE);
            }
        }
    }
}
