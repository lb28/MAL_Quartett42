package de.uulm.dbis.quartett42;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

import de.uulm.dbis.quartett42.data.Deck;
import de.uulm.dbis.quartett42.data.ImageCard;

public class GalleryActivity extends AppCompatActivity {
    String jsonString = "";
    ArrayList<Deck> deckList;
    GridView gridView;
    GridViewAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        //Testen der DefaultSharedPreferences:
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //Value lesen:
        int testValue = sharedPref.getInt("testValue", -1);
        Toast.makeText(getApplicationContext(), "Shared Preference Value: "+testValue, Toast.LENGTH_SHORT).show();

        //JSON-String auslesen:
        Intent intent = getIntent();
        jsonString = intent.getStringExtra("json_string");
        //System.out.println(jsonString);

        //ArrayList aller Decks aus JSON erstellen ... kann spaeter in asynchrone Methode ausgelagert werden
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
        //TODO: Einzelansicht des Decks
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Deck item = (Deck) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), "Deck "+item.getName()+": "+item.getImage().getDescription(), Toast.LENGTH_SHORT).show();
            }

         });


    }

}
