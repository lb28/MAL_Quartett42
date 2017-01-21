package de.uulm.dbis.quartett42;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import java.util.ArrayList;

import de.uulm.dbis.quartett42.data.Deck;

public class GalleryActivity extends AppCompatActivity {
    String jsonString = "";
    ArrayList<Deck> deckList;
    GridView gridView;
    GridViewAdapter gridAdapter;
    ProgressBar spinner; //Spinner fuer Ladezeiten
    ImageButton imagebutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);

        imagebutton = (ImageButton)findViewById(R.id.createDecksImageButton);
        imagebutton.bringToFront();

        //JSON-String auslesen:
        Intent intent = getIntent();
        jsonString = intent.getStringExtra("json_string");

        //Decks laden:
        new Thread(new Runnable() {
            public void run() {
                // TODO make AsynchTask calling LocalJSONHandler.getDecks()
                loadData();
            }
        }).start();


    }

    protected void onResume() {
        super.onResume();

        spinner.setVisibility(View.GONE);
    }

    //Methoden der Activity:

    //Decks laden:
    public void loadData(){
        //ArrayList aller Decks aus JSON erstellen
        LocalJSONHandler jsonParser = new LocalJSONHandler(this);
        deckList = jsonParser.getDecksOverview();


        /*
        // BEGIN TEST (checks if internal storage json produces same objects as assets)
        System.out.println("beginning test");

        ArrayList<Deck> correctDeckList = jsonParser.getAllDecksDetailed();

        System.out.println(new Date());

        jsonParser = new LocalJSONHandler(this, LocalJSONHandler.JSON_MODE_ASSETS);
        ArrayList<Deck> testDeckList = jsonParser.getAllDecksDetailed();
        System.out.println("1: " + correctDeckList.toString().equals(testDeckList.toString()));

        System.out.println(new Date());

        jsonParser = new LocalJSONHandler(this, LocalJSONHandler.JSON_MODE_INTERNAL_STORAGE);
        testDeckList = jsonParser.getAllDecksDetailed();
        System.out.println(testDeckList);
        System.out.println("2: " + correctDeckList.toString().equals(testDeckList.toString()));

        System.out.println(new Date());

        jsonParser = new LocalJSONHandler(this, LocalJSONHandler.JSON_MODE_BOTH);
        testDeckList = jsonParser.getAllDecksDetailed();
        System.out.println("3: " + correctDeckList.toString().equals(testDeckList.toString()));

        System.out.println(new Date());

        System.out.println("end of test");
        // END TEST
        */

        try{
            //Als Grid-Layout setzen:
            gridView = (GridView) findViewById(R.id.galleryGridView);
            gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, deckList);
            gridView.setAdapter(gridAdapter);
        }catch(Exception e){
            e.printStackTrace();
        }

        //On-Item-Click-Listener fuer einzelne Decks:
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Deck item = (Deck) parent.getItemAtPosition(position);

                // Einzelansicht des Decks aufrufen
                spinner.setVisibility(View.VISIBLE);
                Intent intent = new Intent(GalleryActivity.this, ViewDeckActivity.class);
                intent.putExtra("chosen_deck", item.getName());
                intent.putExtra("json_string", jsonString);
                startActivity(intent);
            }

        });
    }

    //Button-Klick-Methoden:
    public void clickCreateDecksFunction(View view){
        //Toast.makeText(getApplicationContext(), "Klick", Toast.LENGTH_SHORT).show();
        spinner.setVisibility(View.VISIBLE);
        Intent intent = new Intent(GalleryActivity.this, LoadOnlineDecksActivity.class);
        startActivity(intent);
    }

}
