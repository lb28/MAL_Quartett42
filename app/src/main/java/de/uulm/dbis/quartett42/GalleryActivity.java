package de.uulm.dbis.quartett42;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.HashSet;

import de.uulm.dbis.quartett42.data.Deck;

import static de.uulm.dbis.quartett42.data.Deck.SRC_MODE_ASSETS;
import static de.uulm.dbis.quartett42.data.Deck.SRC_MODE_INTERNAL_STORAGE;

public class GalleryActivity extends AppCompatActivity {
    private static final String TAG = "GalleryActivity";

    String jsonString = "";
    ArrayList<Deck> deckList;
    GridView gridView;
    GridViewAdapter gridAdapter;
    ContentLoadingProgressBar spinner; //Spinner fuer Ladezeiten
    SharedPreferences sharedPref;
    ProgressDialog barProgressDialog;

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
        LocalJSONHandler jsonParserAssets = new LocalJSONHandler(this, SRC_MODE_ASSETS);
        deckList = jsonParserAssets.getDecksOverview();

        // put all the names of the deckList in our set
        for (Deck d :
                deckList) {
            deckNames.add(d.getName());
        }

        // add internal storage decks only if they are not already in the set of names
        LocalJSONHandler jsonParserInternal = new LocalJSONHandler(this, SRC_MODE_INTERNAL_STORAGE);
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
                        final String deckName = deck.getName();

                        String[] menuOptions = {"Kopie bearbeiten", "Hochladen", "Löschen"};
                        android.app.AlertDialog.Builder builder =
                                new android.app.AlertDialog.Builder(GalleryActivity.this);
                        builder.setTitle("Deck \"" + deckName +"\"")
                                .setItems(menuOptions, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case 0: // edit a copy of the deck
                                                Intent intent = new Intent(GalleryActivity.this,
                                                        CreateDeckActivity.class);
                                                intent.putExtra("deckName", deckName);
                                                startActivity(intent);
                                                break;
                                            case 1: // upload deck

                                                showUploadDialog(deckName, deck.getSrcMode());
                                                 break;
                                            case 2: // delete deck
                                                showDeleteDialog(deckName);
                                                break;
                                        }
                                    }
                                });
                        Dialog d = builder.create();
                        d.show();


                        // returning true to prevent the normal click listener from firing
                        return true;
                    }
                });
            }
        });

    }

    private void showDeleteDialog(final String deckName) {
        // show confirmation dialog before deleting
        new AlertDialog.Builder(GalleryActivity.this)
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setTitle("Deck löschen")
                .setMessage("Wollen Sie Deck \"" + deckName + "\" wirklich löschen?")
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
                                                SRC_MODE_INTERNAL_STORAGE);

                                if (!localJSONHandler.removeDeck(deckName)) {
                                    System.err.println("Deck " + deckName
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
                                        Toast.makeText(getApplicationContext(), "Deck gelöscht", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).start();
                    }

                })
                .setNegativeButton("Nein", null)
                .show();

    }

    private void showUploadDialog(final String deckName, final int source_mode) {
        // show confirmation dialog before deleting
        new AlertDialog.Builder(GalleryActivity.this)
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setTitle("Deck hochladen")
                .setMessage("Wollen Sie Deck \"" + deckName + "\" wirklich hochladen?")
                .setPositiveButton("Ja", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // delete the deck
                        spinner.show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        barProgressDialog = new ProgressDialog(GalleryActivity.this);
                                        barProgressDialog.setTitle("Deck wird hochgeladen...");
                                        barProgressDialog.setMessage("Bitte warten");
                                        barProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); //Progressbar ist glaube ich nicht möglich...
                                                                                                        //ausser wir kopieren die Methoden in die Activity hier rein...
                                        barProgressDialog.show();
                                    }
                                });

                                ServerUploadJSONHandler suh = new ServerUploadJSONHandler(GalleryActivity.this);
                                boolean success = suh.uploadDeck(deckName, source_mode);
                                if(success){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            barProgressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Deck erfolgreich hochgeladen", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            barProgressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Deck konnte nicht hochgeladen werden, " +
                                                    "da es inalide oder schon vorhanden ist!", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }

                            }
                        }).start();
                    }

                })
                .setNegativeButton("Nein", null)
                .show();

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
