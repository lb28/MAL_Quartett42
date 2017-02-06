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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import de.uulm.dbis.quartett42.data.Card;
import de.uulm.dbis.quartett42.data.Deck;
import de.uulm.dbis.quartett42.data.ImageCard;
import de.uulm.dbis.quartett42.data.Property;

import static de.uulm.dbis.quartett42.data.Deck.SRC_MODE_ASSETS;
import static de.uulm.dbis.quartett42.data.Deck.SRC_MODE_INTERNAL_STORAGE;

public class GalleryActivity extends AppCompatActivity {
    private static final String TAG = "GalleryActivity";

 //   String jsonString = "";
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
   //     jsonString = intent.getStringExtra("json_string");

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
                                        barProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                        barProgressDialog.setProgress(0);
                                        barProgressDialog.setMax(100);
                                        barProgressDialog.show();
                                    }
                                });

                                boolean success = uploadDeck(deckName, source_mode);
                                if(success){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            barProgressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Deck erfolgreich hochgeladen", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else{
                                    barProgressDialog.dismiss();
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



    //Sehr lange Deckupload-Methode:
    /**
     * lädt ein deck hoch
     * erst der deckname nach:
     * http://quartett.af-mba.dbis.info/decks/
     * holt id des decks
     * für alle karten:
     * name der karte hochladen nach:
     * http://quartett.af-mba.dbis.info/decks/{deck_id}/cards
     * holt karten id
     * alle attribute der karte mit werten hochladen nach:
     * http://quartett.af-mba.dbis.info/decks/{deck_id}/cards/{card_id}/attributes
     * alle bilder der karte hochladen nach:
     * http://quartett.af-mba.dbis.info/decks/{deck_id}/cards/{card_id}/images
     *
     * @param deckname of deck
     */
    public boolean uploadDeck(String deckname, int source_mode){
        final String URL_AUTHORIZATION = "Basic YWRtaW46ZGIxJGFkbWlu";
        final String URL_DECKS = "http://quartett.af-mba.dbis.info/decks/";

        HttpURLConnection urlConnection = null;
        URL url;
        Deck deckToUpload, tempDeck = null;
        int deckToUploadId;
        ArrayList<Deck> deckOverviewList;
        ArrayList<Card> cardsToUpload;
        ArrayList<Property> propertyList;
        HashMap<String, Double> hashMap;
        ArrayList<ImageCard> imageList;


        LocalJSONHandler ljh = new LocalJSONHandler(GalleryActivity.this, source_mode);
        deckToUpload = ljh.getDeck(deckname);
        Log.i("deckToUpload", deckToUpload.getName());

        //deckToUpload might be null or invalid (if the handler does not find it)
        if (deckToUpload == null
                || deckToUpload.getCardList().size() < 2
                || deckToUpload.getPropertyList().size() < 4
                || deckToUpload.getPropertyList().size() > 10
                || deckToUpload.getCardList().get(0).getAttributeMap().size() < 4
                || deckToUpload.getCardList().get(0).getAttributeMap().size() > 10){
            Log.i("deckToUpload", "Deck invalid");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    barProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Deck konnte nicht hochgeladen werden, " +
                            "da es invalide ist!", Toast.LENGTH_LONG).show();
                }
            });
            return false;
        }
        //check if Deck is already uploaded
        ServerJSONHandler serverJsonHandler = new ServerJSONHandler(GalleryActivity.this);
        ArrayList<Deck> testList = serverJsonHandler.getDecksOverview(false);
        for(Deck d : testList){
            if(d.getName().equals(deckname)){
                Log.i("deckToUpload", "Deckname bereits hochgeladen");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        barProgressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Deck konnte nicht hochgeladen werden, " +
                                "da es schon vorhanden ist!", Toast.LENGTH_LONG).show();
                    }
                });
                return false;
            }
        }

        barProgressDialog.setProgress(10);

        Random r = new Random();

        //testen ob hochladen wegen name möglich
        // /decks
        //wenn ja name hochladen -> deck bekommt id

        try {
            url = new URL(URL_DECKS);

            //json objekt erstellen
            JSONObject postData = new JSONObject();
            postData.put("name", deckToUpload.getName());
            postData.put("description", deckToUpload.getImage().getDescription());
            postData.put("misc", "");
            postData.put("misc_version", "1");
            postData.put("filename", ""+r.nextInt(100000)+deckToUpload.getImage().getUri());
            //Fileendung Teil der Image Url, Random damit nicht zwei gleich heissen auf seinem Server
            String deckImageUrl = "";
            //Image Uri wie in GridViewAdapter zusammenbauen
            if(source_mode == Deck.SRC_MODE_ASSETS){
                //deckImageUrl = "file:///android_asset/" + deckToUpload.getName() + "/" + deckToUpload.getImage().getUri();
                deckImageUrl = deckToUpload.getName() + "/" + deckToUpload.getImage().getUri();
            }else if(source_mode == Deck.SRC_MODE_INTERNAL_STORAGE){
                deckImageUrl = getFilesDir() + "/" + deckToUpload.getImage().getUri();
            }else{
                //error
                return false;
            }
            Log.i("FILENAME upload", deckImageUrl);
            postData.put("image_base64", Util.urlToBase64(deckImageUrl, source_mode, GalleryActivity.this));

            //connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Authorization", URL_AUTHORIZATION);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);

            //write
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            //writer.write(getPostDataString(postData));
            writer.write(postData.toString());
            writer.flush();
            writer.close();
            os.close();

            int responseCode = urlConnection.getResponseCode();

            //response
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED){
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null){
                    sb.append(line);
                    break;
                }

                in.close();
                Log.i("response", sb.toString());
            } else{
                //if an error occured return false
                Log.i("response", "false: " + responseCode);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        barProgressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Deck konnte nicht hochgeladen werden!", Toast.LENGTH_LONG).show();
                    }
                });
                return false;
            }

        } catch(IOException e){
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    barProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Deck konnte nicht hochgeladen werden!", Toast.LENGTH_LONG).show();
                }
            });
            return false;
        } catch(JSONException j){
            j.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    barProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Deck konnte nicht hochgeladen werden!", Toast.LENGTH_LONG).show();
                }
            });
            return false;
        } catch(Exception e1){
            e1.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    barProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Deck konnte nicht hochgeladen werden!", Toast.LENGTH_LONG).show();
                }
            });
            return false;
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        barProgressDialog.setProgress(15);

        //id holen über name
        // /decks
        ServerJSONHandler sjh = new ServerJSONHandler(GalleryActivity.this);
        deckOverviewList = sjh.getDecksOverview(false);

        for (Deck d : deckOverviewList){
            if (d.getName().equals(deckToUpload.getName())){
                tempDeck = d;
            }
        }

        deckToUploadId = tempDeck.getID();

        //karten hochladen (id?, name)
        // /decks/{deck_id}/cards
        cardsToUpload = deckToUpload.getCardList();

        //für jede Karte name hochladen
        //dann id holen
        //dann attribute hochladen
        //dann bilder

        propertyList = deckToUpload.getPropertyList();

        barProgressDialog.setProgress(20);
        int leftProgress = (80/cardsToUpload.size())/3;

        for (Card c : cardsToUpload){

            try {
                //name der karte hochladen
                url = new URL(URL_DECKS + deckToUploadId + "/cards/");
                Log.i("karte hochladen url", "" + url);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", c.getName());

                //connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Authorization", URL_AUTHORIZATION);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);

                //write
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();
                os.close();

                int responseCode = urlConnection.getResponseCode();

                //response
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED){
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(urlConnection.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null){
                        sb.append(line);
                        break;
                    }

                    in.close();
                    Log.i("response Card name", sb.toString());
                } else{
                    //if an error ocurred return false
                    Log.i("response Card name", "false: " + responseCode);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            barProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Deck konnte nicht hochgeladen werden!", Toast.LENGTH_LONG).show();
                        }
                    });
                    return false;
                }

                // id der karte holen
                int tmpCardID = 0;

                String jsonStringCards = sjh.loadOnlineData(url);
                JSONArray cardsArray = new JSONArray(jsonStringCards);

                for(int i = 0; i < cardsArray.length(); i++) {
                    JSONObject tmpIDCard = cardsArray.getJSONObject(i);
                    tmpCardID = tmpIDCard.getInt("id");
                }
                Log.i("card id", "" + tmpCardID);

                barProgressDialog.setProgress(barProgressDialog.getProgress()+leftProgress);

                //attribute hochladen
                url = new URL(URL_DECKS + deckToUploadId + "/cards/" + tmpCardID + "/attributes/");
                Log.i("url attribute hochladen", "" + url);

                hashMap = c.getAttributeMap();

                JSONObject jsonObjectAttribute = new JSONObject();
                //hole alle Name-Werte-Paare und pack sie in den post request
                int order = 0;
                for (Property p : propertyList){
                    String higher_wins = "lower_wins";
                    Boolean higherWins = p.isMaxWinner();
                    if (higherWins == true){
                        higher_wins = "higher_wins";
                    }
                    String nameProperty = p.getName();

                    jsonObjectAttribute.put("name", nameProperty);
                    jsonObjectAttribute.put("value", hashMap.get(nameProperty).toString());
                    jsonObjectAttribute.put("unit", p.getUnit());
                    jsonObjectAttribute.put("order", order);
                    jsonObjectAttribute.put("what_wins", higher_wins);

                    order++;

                    //connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Authorization", URL_AUTHORIZATION);
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestProperty("Accept", "application/json");
                    urlConnection.setDoOutput(true);

                    //write
                    OutputStream ost = urlConnection.getOutputStream();
                    BufferedWriter bwriter = new BufferedWriter(new OutputStreamWriter(ost, "UTF-8"));
                    bwriter.write(jsonObjectAttribute.toString());
                    bwriter.flush();
                    bwriter.close();
                    ost.close();

                    int responseCode2 = urlConnection.getResponseCode();

                    //response
                    if (responseCode2 == HttpURLConnection.HTTP_OK || responseCode2 == HttpURLConnection.HTTP_CREATED){
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(urlConnection.getInputStream()));
                        StringBuffer sb = new StringBuffer("");
                        String line = "";

                        while ((line = in.readLine()) != null){
                            sb.append(line);
                            break;
                        }

                        in.close();
                        Log.i("response Card attr", sb.toString());
                    } else{
                        //if an error ocurred return false
                        Log.i("response Card attr", "false: " + responseCode2);
                    }
                }

                barProgressDialog.setProgress(barProgressDialog.getProgress()+leftProgress);

                //bilder jeder karte hochladen
                url = new URL(URL_DECKS + deckToUploadId + "/cards/" + tmpCardID + "/images/");
                Log.i("url attribute hochladen", "" + url);

                imageList = c.getImageList();

                JSONObject jsonObjectImages = new JSONObject();

                for (int i = 0; i < imageList.size(); i++){

                    ImageCard imageCard = imageList.get(i);

                    jsonObjectImages.put("description", imageCard.getDescription());
                    jsonObjectImages.put("order", i);
                    jsonObjectImages.put("filename", ""+r.nextInt(100000)+imageList.get(i).getUri());
                    //Fileendung Teil der Image Url, Random damit nicht zwei gleich heissen auf seinem Server
                    String cardImageUri = "";
                    //Image Uri wie in GridViewAdapter zusammenbauen
                    if(source_mode == Deck.SRC_MODE_ASSETS){
                        //cardImageUri = "file:///android_asset/" + deckToUpload.getName() + "/" + imageList.get(i).getUri();
                        cardImageUri = deckToUpload.getName() + "/" + imageList.get(i).getUri();
                    }else if(source_mode == Deck.SRC_MODE_INTERNAL_STORAGE){
                        cardImageUri = getFilesDir() + "/" + imageList.get(i).getUri();
                    }else{
                        //error
                        return false;
                    }
                    jsonObjectImages.put("image_base64", Util.urlToBase64(cardImageUri, source_mode, GalleryActivity.this));

                    //connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Authorization", URL_AUTHORIZATION);
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestProperty("Accept", "application/json");
                    urlConnection.setDoOutput(true);

                    //write
                    OutputStream ostr = urlConnection.getOutputStream();
                    BufferedWriter buwriter = new BufferedWriter(new OutputStreamWriter(ostr, "UTF-8"));
                    buwriter.write(jsonObjectImages.toString());
                    buwriter.flush();
                    buwriter.close();
                    ostr.close();

                    int responseCode3 = urlConnection.getResponseCode();

                    //response
                    if (responseCode3 == HttpURLConnection.HTTP_OK || responseCode3 == HttpURLConnection.HTTP_CREATED){
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(urlConnection.getInputStream()));
                        StringBuffer sb = new StringBuffer("");
                        String line = "";

                        while ((line = in.readLine()) != null){
                            sb.append(line);
                            break;
                        }

                        in.close();
                        Log.i("response Card images", sb.toString());
                    } else{
                        //if an error ocurred return false
                        Log.i("response Card images", "false: " + responseCode3);
                    }

                }
                barProgressDialog.setProgress(barProgressDialog.getProgress()+leftProgress);


            } catch (MalformedURLException m){
                m.printStackTrace();
            } catch (IOException ioe){
                ioe.printStackTrace();
            } catch (JSONException je){
                je.printStackTrace();
            }
        }
        barProgressDialog.setProgress(100);

        return true;

    }


}
