package de.uulm.dbis.quartett42;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import de.uulm.dbis.quartett42.data.Deck;

import static de.uulm.dbis.quartett42.LocalJSONHandler.JSON_MODE_INTERNAL_STORAGE;


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

                                            //TODO check if deck with this name already exists in the internal storage
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

                }

            });

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * downloads a single deck, saves it in internal storage and then redirects to the gallery
     *
     * Saving all pictures in the same folder and no subfolders for every deck!!!
     *
     * @param deckID deckID
     */
    private void downloadDeck(int deckID) {
        System.out.println("starting download...");

        ServerJSONHandler serverJSONHandler = new ServerJSONHandler();

        // get the deck object from the server
        Deck deck = serverJSONHandler.getDeck(deckID);

        // download the deck image and save it in internal storage (same file structure as assets)
        FileOutputStream fos;
        try {
            String deckImgUrl = deck.getImage().getUri();
            Bitmap deckImageBitmap = Util.downloadBitmap(deckImgUrl);

            fos = openFileOutput(deck.getName()+"_deckimage.jpg", Context.MODE_PRIVATE);

            // Writing the bitmap to the output stream
            deckImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            // replace the image uri with the local uri
            deck.getImage().setUri(deck.getName()+"_deckimage.jpg");

        } catch (Exception e) {
            e.printStackTrace();
        }

        for(int i = 0; i < deck.getCardList().size(); i++){
            for(int j = 0; j < deck.getCardList().get(i).getImageList().size(); j++){
                try {
                    String cardImgUrl = deck.getCardList().get(i).getImageList().get(j).getUri();
                    Bitmap cardImageBitmap = Util.downloadBitmap(cardImgUrl);

                    fos = openFileOutput(deck.getName()+i+"_"+j+".jpg", Context.MODE_PRIVATE);
                    // Writing the bitmap to the output stream
                    cardImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();

                    // replace the image uri with the local uri
                    deck.getCardList().get(i).getImageList().get(j).setUri(deck.getName()+i+"_"+j+".jpg");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // save the deck as json
        JSONObject newJsonDeck = deck.toJSON();
        LocalJSONHandler localJsonHandler = new LocalJSONHandler(this, JSON_MODE_INTERNAL_STORAGE );

        //localJsonHandler.deleteJSONFile(); //for testing only

        JSONObject oldJsonDeckList = localJsonHandler.readJSONFromFile(JSON_MODE_INTERNAL_STORAGE);
        if(oldJsonDeckList != null){
            try {
                JSONArray oldDeckArray = oldJsonDeckList.getJSONArray("decks");
                oldDeckArray.put(newJsonDeck);

                JSONObject newDeckList = new JSONObject();
                newDeckList.put("decks", oldDeckArray);

                localJsonHandler.saveJSONToFile(newDeckList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            try {
                //1tes neues Deck, File nicht vorhanden
                oldJsonDeckList = new JSONObject();
                JSONArray newDeckArray = new JSONArray();
                newDeckArray.put(newJsonDeck);
                oldJsonDeckList.put("decks", newDeckArray);

                localJsonHandler.saveJSONToFile(oldJsonDeckList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //for testing:
        ArrayList<Deck> testDeckArray = localJsonHandler.getAllDecksDetailed();
        System.out.println(testDeckArray.toString());

        //Toast.makeText(getApplicationContext(), "Deck "+deck.getName()+" erfolgreich runter geladen", Toast.LENGTH_SHORT).show();
        finish();

    }

}
