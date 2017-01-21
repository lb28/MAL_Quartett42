package de.uulm.dbis.quartett42;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

import de.uulm.dbis.quartett42.data.Deck;


public class LoadOnlineDecksActivity extends AppCompatActivity {

    // https://dhc.restlet.com/ for testing

    ArrayList<Deck> deckList;
    ContentLoadingProgressBar spinner; //Spinner fuer Ladezeiten
    GridView gridView;
    GridViewAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_online_decks);

        deckList = new ArrayList<Deck>();
        spinner = (ContentLoadingProgressBar) findViewById(R.id.progressBar1);
        spinner.show();


        // TODO make all this AsynchTask
        new Thread(new Runnable() {
            public void run() {
                ServerJSONHandler jsonHandler = new ServerJSONHandler();
                deckList = jsonHandler.getDecksOverview();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run(){
                        makeGridView(deckList);
                        spinner.hide();
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
                    final Deck deck = (Deck) parent.getItemAtPosition(position);

                    // show confirmation dialog before downloading
                    new AlertDialog.Builder(LoadOnlineDecksActivity.this)
                            .setIcon(R.drawable.ic_warning_black_24dp)
                            .setTitle("Deck Herunterladen")
                            .setMessage("Wollen Sie Deck " + deck.getName() + " herunterladen?")
                            .setPositiveButton("Ja", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // download the deck
                                    spinner.show();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            downloadDeck(deck.getID());
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    spinner.hide();
                                                }
                                            });
                                        }
                                    }).start();
                                }

                            })
                            .setNegativeButton("Nein", null)
                            .show();

                    // Einzelansicht des Decks aufrufen

                }

            });

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * downloads a single deck, saves it in internal storage and then redirects to the gallery
     * @param deckID
     */
    private void downloadDeck(int deckID) {
        ServerJSONHandler serverJSONHandler = new ServerJSONHandler();

        // TODO implement below comments

        // get the deck object from the server
        Deck deck = serverJSONHandler.getDeck(deckID);
        System.out.println(deck.toString()); //for testing

        // download the deck image and save it in internal storage (same file structure as assets)
        String deckImgUrl = deck.getImage().getUri();


        // replace the image uri with the local uri

        // download all the card images and save them in internal storage

        // replace all the image uris with the local uris (string.replace() in the json string?)

        // save the deck as json (deck.toJSON())
    }

}
