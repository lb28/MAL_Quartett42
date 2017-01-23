package de.uulm.dbis.quartett42;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;

import java.util.ArrayList;

import de.uulm.dbis.quartett42.data.Deck;

import static de.uulm.dbis.quartett42.LocalJSONHandler.JSON_MODE_INTERNAL_STORAGE;

public class GalleryActivity extends AppCompatActivity {
    String jsonString = "";
    ArrayList<Deck> deckList;
    GridView gridView;
    GridViewAdapter gridAdapter;
    ContentLoadingProgressBar spinner; //Spinner fuer Ladezeiten
    ImageButton imagebutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        spinner = (ContentLoadingProgressBar) findViewById(R.id.progressBar1);
        spinner.show();

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

        spinner.hide();
    }

    //Methoden der Activity:

    //Decks laden:
    public void loadData(){
        //ArrayList aller Decks aus JSON erstellen
        //LocalJSONHandler jsonParser = new LocalJSONHandler(this);
        LocalJSONHandler jsonParser = new LocalJSONHandler(this, JSON_MODE_INTERNAL_STORAGE); //TODO Rest der Galerie anpassen (Einzelansicht, Spiel, ...
        deckList = jsonParser.getDecksOverview();

        // update the view
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    //Als Grid-Layout setzen:
                    gridView = (GridView) findViewById(R.id.galleryGridView);
                    gridAdapter = new GridViewAdapter(GalleryActivity.this, R.layout.grid_item_layout, deckList);
                    gridView.setAdapter(gridAdapter);
                }catch(Exception e){
                    e.printStackTrace();
                }

                //On-Item-Click-Listener fuer einzelne Decks:
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        Deck item = (Deck) parent.getItemAtPosition(position);

                        // Einzelansicht des Decks aufrufen
                        spinner.show();
                        Intent intent = new Intent(GalleryActivity.this, ViewDeckActivity.class);
                        intent.putExtra("chosen_deck", item.getName());
                        intent.putExtra("json_string", jsonString);
                        startActivity(intent);
                    }

                });
            }
        });

    }

    //Button-Klick-Methoden:
    public void clickCreateDecksFunction(View view){
        //Toast.makeText(getApplicationContext(), "Klick", Toast.LENGTH_SHORT).show();
        spinner.show();
        Intent intent = new Intent(GalleryActivity.this, LoadOnlineDecksActivity.class);
        startActivity(intent);
    }

}
