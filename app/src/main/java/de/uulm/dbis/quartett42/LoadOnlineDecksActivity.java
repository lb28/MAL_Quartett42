package de.uulm.dbis.quartett42;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import de.uulm.dbis.quartett42.data.Deck;


public class LoadOnlineDecksActivity extends AppCompatActivity {

    // https://dhc.restlet.com/ for testing

    ArrayList<Deck> deckList;

    GridView gridView;
    GridViewAdapter gridAdapter;

    ProgressBar spinner; //Spinner fuer Ladezeiten

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_online_decks);

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);

        deckList = new ArrayList<Deck>();

        new Thread(new Runnable() {
            public void run() {
                final ServerJSONHandler serverJsonHandler = new ServerJSONHandler();
                deckList = serverJsonHandler.getDecksOverview();

                //Next two lines only to test:
                Deck testDeck = serverJsonHandler.getDeck(10);
                System.out.println(testDeck.toString());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run(){
                        makeGridView(deckList);
                    }
                });
            }
        }).start();


    }

    //Method to put this all into the Gridview:
    public void makeGridView(ArrayList<Deck> deckList){
        try{
            //Als Grid-Layout setzen:
            gridView = (GridView) findViewById(R.id.galleryGridView);
            gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, deckList);
            gridView.setAdapter(gridAdapter);

            //On-Item-Click-Listener fuer einzelne Decks:
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Deck item = (Deck) parent.getItemAtPosition(position);
                    int chosenDeck = item.getID();
                    Toast.makeText(getApplicationContext(), "TODO: Download "+chosenDeck, Toast.LENGTH_SHORT).show();

                    //TODO:
                    // By clicking on a Deck check, if a deck with this name already exists in the internal-storage
                    // If not, start new activity, download the deck with getDeck(deckID) of ServerJSONHandler
                    // and finally save the Deck object by using the toJson() method of the deck
                    // as well as downloading all images from the deck to the internal storage
                }

            });

            spinner.setVisibility(View.GONE);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
