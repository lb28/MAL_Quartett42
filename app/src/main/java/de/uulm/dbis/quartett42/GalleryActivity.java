package de.uulm.dbis.quartett42;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.HashSet;

import de.uulm.dbis.quartett42.data.Deck;

import static de.uulm.dbis.quartett42.LocalJSONHandler.JSON_MODE_ASSETS;
import static de.uulm.dbis.quartett42.LocalJSONHandler.JSON_MODE_INTERNAL_STORAGE;

public class GalleryActivity extends AppCompatActivity {
    private static final String TAG = "GalleryActivity";

    String jsonString = "";
    ArrayList<Deck> deckList;
    GridView gridView;
    GridViewAdapter gridAdapter;
    ContentLoadingProgressBar spinner; //Spinner fuer Ladezeiten
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        spinner = (ContentLoadingProgressBar) findViewById(R.id.progressBar1);
        spinner.show();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

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

    @Override
    protected void onResume() {
        super.onResume();

        spinner.hide();
    }

    @Override
    public void onBackPressed() {
        super.onSupportNavigateUp();
    }

    //Methoden der Activity:

    //Decks laden:
    public void loadData(){
        //ArrayList aller Decks aus JSON erstellen

        // Set of deckNames that we loaded so far (for eliminating duplicates)
        HashSet<String> deckNames = new HashSet<>();

        // load asset decks
        LocalJSONHandler jsonParserAssets = new LocalJSONHandler(this, JSON_MODE_ASSETS);
        deckList = jsonParserAssets.getDecksOverview();

        // put all the names of the deckList in our set
        for (Deck d :
                deckList) {
            deckNames.add(d.getName());
        }

        // add internal storage decks only if they are not already in the set of names
        //TODO Rest der Galerie anpassen (Einzelansicht, Spiel, ...
        LocalJSONHandler jsonParserInternal = new LocalJSONHandler(this, JSON_MODE_INTERNAL_STORAGE);
        for (Deck d :
                jsonParserInternal.getDecksOverview()) {
            if (!deckNames.contains(d.getName())) {
                deckList.add(d);
            }
        }

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
                        intent.putExtra("srcMode", item.getSrcMode());
                        intent.putExtra("json_string", jsonString);
                        ((FloatingActionMenu) findViewById(R.id.createDecksFAM)).close(true);
                        startActivity(intent);

                    }

                });

                gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                        final Deck deck = (Deck) parent.getItemAtPosition(position);

                        // show confirmation dialog before downloading
                        new AlertDialog.Builder(GalleryActivity.this)
                                .setIcon(R.drawable.ic_warning_black_24dp)
                                .setTitle("Deck löschen")
                                .setMessage("Wollen Sie Deck " + deck.getName() + " wirklich löschen?")
                                .setPositiveButton("Ja", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // delete the deck
                                        spinner.show();
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {

                                                LocalJSONHandler localJSONHandler =
                                                        new LocalJSONHandler(GalleryActivity.this,
                                                                JSON_MODE_INTERNAL_STORAGE);

                                                if (!localJSONHandler.removeDeck(deck.getName())) {
                                                    System.err.println("Deck " + deck.getName()
                                                    + " could not be removed!");
                                                }else{
                                                    //Laufende Spielvariable auf 0 setzen, falls dieses Deck gerade in der Pause ist
                                                    if(sharedPref.getInt("runningGame", 0) == 1){
                                                        SharedPreferences.Editor editor = sharedPref.edit();
                                                        editor.putInt("runningGame", 0);
                                                        editor.putInt("currentRoundsLeft", 0);
                                                        editor.putInt("currentPointsPlayer", 0);
                                                        editor.putInt("currentPointsComputer", 0);
                                                        editor.putString("currentCardsPlayer", "");
                                                        editor.putString("currentCardsComputer", "");
                                                        editor.apply();
                                                    }
                                                }

                                                // refresh the activity
                                                Intent intent = getIntent();
                                                finish();
                                                startActivity(intent);

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

                        // returning true to prevent the normal click listener from firing
                        return true;
                    }
                });
            }
        });

    }

    //Button-Klick-Methoden:
    public void clickGoToShopButton(View view){
        spinner.show();
        ((FloatingActionMenu) findViewById(R.id.createDecksFAM)).close(true);
        Intent intent = new Intent(GalleryActivity.this, LoadOnlineDecksActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void clickCreateDeckButton(View view) {
        spinner.show();
        ((FloatingActionMenu) findViewById(R.id.createDecksFAM)).close(true);
        Intent intent = new Intent(GalleryActivity.this, CreateDeckActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


}
