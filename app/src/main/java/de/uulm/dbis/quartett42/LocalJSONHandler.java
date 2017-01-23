package de.uulm.dbis.quartett42;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import de.uulm.dbis.quartett42.data.Card;
import de.uulm.dbis.quartett42.data.Deck;
import de.uulm.dbis.quartett42.data.ImageCard;
import de.uulm.dbis.quartett42.data.Property;

/**
 * Acts like a DB manager for the json file
 * Created by Luis on 12.01.2017.
 */
public class LocalJSONHandler {
    public static final String JSON_FILENAME_ASSETS = "jsonexample.json";
    public static final String JSON_FILENAME_INTERNAL_STORAGE = "quartett.json";

    public static final int JSON_MODE_ASSETS = 1;
    public static final int JSON_MODE_INTERNAL_STORAGE = 2;
    public static final int JSON_MODE_BOTH = 3;


    private Context context;
    private int jsonMode;

    public LocalJSONHandler(Context context) {
        // default constructor is for assets (may be changed later)
        this(context, JSON_MODE_ASSETS);
    }

    public LocalJSONHandler(Context context, int jsonMode) {
        this.context = context;
        this.jsonMode = jsonMode;
    }


    /**
     * Get a list of every deck <b>containing only name and one imagecard for each deck</b>.
     * Used for showing a deck overview. The file location depends on the specified jsonMode:
     *  <ul>
     *      <li>
     *          ASSETS: all decks from the assets json file
     *      </li>
     *      <li>
     *          INTERNAL_STORAGE: all decks from internal storage
     *      </li>
     *      <li>
     *          BOTH: all decks from assets and internal storage.
     *          If one file returns no decks, the deck list only
     *          contains the decks from the other file.
     *      </li>
     *  </ul>
     * @return a list of decks as an overview
     */
    public ArrayList<Deck> getDecksOverview() {
        ArrayList<Deck> deckList = new ArrayList<Deck>();
        JSONObject jsonObj;
        JSONArray decks;

        try {

            jsonObj = readJSONFromFile(jsonMode);
            decks = jsonObj.getJSONArray("decks");


            for (int i = 0; i < decks.length(); i++) {
                JSONObject tmpDeck = decks.getJSONObject(i);
                String deckName = tmpDeck.getString("name");
                String deckDescription = tmpDeck.getString("description");
                String deckImageUri = tmpDeck.getString("image");
                //Cards und Properties sind erst mal egal fuer die Deckuebersicht

                ImageCard newImage = new ImageCard(deckImageUri, deckDescription);
                // set srcMode to assets or internal
                int srcMode = Deck.SRC_MODE_NONE;
                if (jsonMode == JSON_MODE_ASSETS) {
                    srcMode = Deck.SRC_MODE_ASSETS;
                }
                else if (jsonMode == JSON_MODE_INTERNAL_STORAGE) {
                    srcMode = Deck.SRC_MODE_INTERNAL_STORAGE;
                }

                Deck newDeck = new Deck(deckName, newImage, null, null, srcMode);
                deckList.add(newDeck);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return deckList;
    }


    /**
     * Get the whole content of the json (all decks with full information).<br>
     * If you only need the deck overview, go for getDecksOverview().<br>
     * <b>ATTENTION: THIS READS THE WHOLE JSON (all decks with all cards).</b>
     * @return a list of complete deck objects
     */
    public ArrayList<Deck> getAllDecksDetailed() {
        ArrayList<Deck> deckList = new ArrayList<Deck>();
        JSONObject jsonObj;
        JSONArray decks;

        try {

            jsonObj = readJSONFromFile(jsonMode);
            decks = jsonObj.getJSONArray("decks");


            for (int i = 0; i < decks.length(); i++) {
                JSONObject tmpDeck = decks.getJSONObject(i);

                //Alle Daten fuer das Deck auslesen:
                String deckName = tmpDeck.getString("name");
                String deckDescription = tmpDeck.getString("description");
                String deckImageUri = tmpDeck.getString("image");
                ImageCard deckImage = new ImageCard(deckImageUri, deckDescription);
                //Properties auslesen:
                ArrayList<Property> propertyList = new ArrayList<Property>();
                JSONArray properties = tmpDeck.getJSONArray("properties");
                for(int p = 0; p < properties.length(); p++){
                    JSONObject tmpProperty = properties.getJSONObject(p);
                    String pName = tmpProperty.getString("name");
                    String pUnit = tmpProperty.getString("unit");
                    boolean pMaxwinner = tmpProperty.getBoolean("maxwinner");
                    Property property = new Property(pName, pUnit, pMaxwinner);
                    propertyList.add(property);
                }
                //Alle Karten auslesen:
                ArrayList<Card> cardList = new ArrayList<Card>();
                JSONArray cards = tmpDeck.getJSONArray("cards");
                for(int c = 0; c < cards.length(); c++){
                    JSONObject tmpCard = cards.getJSONObject(c);
                    String cName = tmpCard.getString("name");
                    int cId = tmpCard.getInt("id");
                    //Bilder durchlaufen:
                    ArrayList<ImageCard> imageList = new ArrayList<ImageCard>();
                    JSONArray images = tmpCard.getJSONArray("images");
                    for(int j = 0; j < images.length(); j++){
                        JSONObject tmpImage = images.getJSONObject(j);
                        String uri = tmpImage.getString("URI");
                        String iDescription = tmpImage.getString("description");
                        ImageCard cardImage = new ImageCard(uri, iDescription);
                        imageList.add(cardImage);
                    }
                    //Values durchlaufen:
                    HashMap<String, Double> attributeMap = new HashMap<String, Double>();
                    JSONObject values = tmpCard.getJSONObject("values");
                    for(Property px : propertyList){
                        //System.out.println(px.getName()+": "+values.getDouble(px.getName()));
                        attributeMap.put(px.getName(), values.getDouble(px.getName()));
                    }
                    //Karte erstellen
                    Card newCard = new Card(cName, cId, imageList, attributeMap);
                    cardList.add(newCard);
                }
                // set srcMode to assets or internal
                int srcMode = Deck.SRC_MODE_NONE;
                if (jsonMode != JSON_MODE_ASSETS) {
                    srcMode = Deck.SRC_MODE_ASSETS;
                }
                else if (jsonMode == JSON_MODE_INTERNAL_STORAGE) {
                    srcMode = Deck.SRC_MODE_INTERNAL_STORAGE;
                }

                //return the deck
                Deck newDeck = new Deck(deckName, deckImage, propertyList, cardList, srcMode);

                deckList.add(newDeck);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return deckList;
    }


    /**
     * Loads a single deck. The location where this method looks for the deck depends on
     * the jsonMode set in the constructor (assets/internal/both).
     *
     *
     * @param chosenDeckName the name of the deck
     * @return the deck with the specified name, or null if no deck was found
     */
    public Deck getDeck(String chosenDeckName){
        try {
            JSONObject jsonObj = readJSONFromFile(jsonMode);
            JSONArray decks = jsonObj.getJSONArray("decks");
            //Nach passendem Deck im JSON-Array suchen:
            for (int i = 0; i < decks.length(); i++) {
                JSONObject tmpDeck = decks.getJSONObject(i);
                String deckName = tmpDeck.getString("name");
                if(deckName.equals(chosenDeckName)){
                    //Alle Daten fuer das Deck auslesen:
                    String deckDescription = tmpDeck.getString("description");
                    String deckImageUri = tmpDeck.getString("image");
                    ImageCard deckImage = new ImageCard(deckImageUri, deckDescription);
                    //Properties auslesen:
                    ArrayList<Property> propertyList = new ArrayList<Property>();
                    JSONArray properties = tmpDeck.getJSONArray("properties");
                    for(int p = 0; p < properties.length(); p++){
                        JSONObject tmpProperty = properties.getJSONObject(p);
                        String pName = tmpProperty.getString("name");
                        String pUnit = tmpProperty.getString("unit");
                        boolean pMaxwinner = tmpProperty.getBoolean("maxwinner");
                        Property property = new Property(pName, pUnit, pMaxwinner);
                        propertyList.add(property);
                    }
                    //Alle Karten auslesen:
                    ArrayList<Card> cardList = new ArrayList<Card>();
                    JSONArray cards = tmpDeck.getJSONArray("cards");
                    for(int c = 0; c < cards.length(); c++){
                        JSONObject tmpCard = cards.getJSONObject(c);
                        String cName = tmpCard.getString("name");
                        int cId = tmpCard.getInt("id");
                        //Bilder durchlaufen:
                        ArrayList<ImageCard> imageList = new ArrayList<ImageCard>();
                        JSONArray images = tmpCard.getJSONArray("images");
                        for(int j = 0; j < images.length(); j++){
                            JSONObject tmpImage = images.getJSONObject(j);
                            String uri = tmpImage.getString("URI");
                            String iDescription = tmpImage.getString("description");
                            ImageCard cardImage = new ImageCard(uri, iDescription);
                            imageList.add(cardImage);
                        }
                        //Values durchlaufen:
                        HashMap<String, Double> attributeMap = new HashMap<String, Double>();
                        JSONObject values = tmpCard.getJSONObject("values");
                        for(Property px : propertyList){
                            //System.out.println(px.getName()+": "+values.getDouble(px.getName()));
                            attributeMap.put(px.getName(), values.getDouble(px.getName()));
                        }
                        //Karte erstellen
                        Card newCard = new Card(cName, cId, imageList, attributeMap);
                        cardList.add(newCard);
                    }
                    // set srcMode to assets or internal
                    int srcMode = Deck.SRC_MODE_NONE;
                    if (jsonMode != JSON_MODE_ASSETS) {
                        srcMode = Deck.SRC_MODE_ASSETS;
                    }
                    else if (jsonMode == JSON_MODE_INTERNAL_STORAGE) {
                        srcMode = Deck.SRC_MODE_INTERNAL_STORAGE;
                    }

                    //return the deck
                    return new Deck(deckName, deckImage, propertyList, cardList, srcMode);
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        // no deck was found
        return null;
    }


    /**
     * saves a list of decks to the json file in internal storage
     * with the file name {@value JSON_FILENAME_INTERNAL_STORAGE}.
     * <b>This overwrites the old file!</b>
     * @param decks the list of decks to be saved
     */
    public void saveAllDecks(ArrayList<Deck> decks) {
        try {
            JSONObject jsonObj = new JSONObject();
            JSONArray jsonDecks = new JSONArray();
            for (Deck d : decks) {
                jsonDecks.put(d.toJSON());
            }
            jsonObj.put("decks", jsonDecks);
            // save the created json object
            saveJSONToFile(jsonObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    ///////////////////
    // FILE HANDLING //
    ///////////////////

    /**
     * reads the json object from internal storage or from assets (depending on jsonMode)
     * @param jsonMode the mode (assets / internal storage), see the constants in LocalJSONHandler
     * @return the root json object
     */
    public JSONObject readJSONFromFile(int jsonMode) {

        switch (jsonMode) {
            case JSON_MODE_ASSETS:
                // read the json object from the assets
                try {
                    InputStream in = context.getAssets().open(JSON_FILENAME_ASSETS);
                    int size = in.available();
                    byte[] buffer = new byte[size];
                    in.read(buffer);
                    in.close();
                    String jsonString = new String(buffer, "UTF-8");
                    // Getting JSON Array node
                    return new JSONObject(jsonString);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    return null;
                }

            case JSON_MODE_INTERNAL_STORAGE:
                // read the json object from the internal storage
                try {
                    FileInputStream fin = context.openFileInput(
                            JSON_FILENAME_INTERNAL_STORAGE);
                    BufferedInputStream in = new BufferedInputStream(fin);

                    String jsonString = "";
                    byte[] buffer = new byte[1024];
                    int bytesRead = 0;
                    while((bytesRead = in.read(buffer)) != -1) {
                        jsonString += new String(buffer, 0, bytesRead);
                    }

                    return new JSONObject(jsonString);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            case JSON_MODE_BOTH:
                System.err.println("ERROR: Using JSON_MODE_BOTH does not work yet properly" +
                        "with the deck images (see Deck.srcMode). Pleas use a dedicated json mode");
                try {
                    // merge deck list from both files
                    JSONObject jsonObjAssets = readJSONFromFile(JSON_MODE_ASSETS);
                    JSONObject jsonObjInternal = readJSONFromFile(JSON_MODE_INTERNAL_STORAGE);

                    JSONArray decks = new JSONArray();

                    // hash map of names for checking for duplicates
                    HashSet<String> usedNames = new HashSet<>();

                    if (jsonObjAssets != null) {
                        // put all the decks from assets into list
                        decks = jsonObjAssets.getJSONArray("decks");
                        for (int i = 0; i < decks.length(); i++) {
                            // add the name to the hash set
                            usedNames.add(decks.getJSONObject(i).getString("name"));
                        }
                    }
                    JSONArray decksInternal;
                    if (jsonObjInternal != null) {
                        // add the decks from internal storage (except duplicates)
                        decksInternal = jsonObjInternal.getJSONArray("decks");
                        for (int i = 0; i < decksInternal.length(); i++) {
                            // only add deck if its name is not already in the deck list
                            if (!usedNames.contains(
                                    decksInternal.getJSONObject(i).getString("name"))) {
                                decks.put(decksInternal.getJSONObject(i));
                            }
                        }
                    }

                    JSONObject jsonObjBoth = new JSONObject();
                    jsonObjBoth.put("decks", decks);
                    return jsonObjBoth;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
        }

        // if everyting failed
        return null;
    }

    /**
     * saves a json object to a file in internal storage, overwriting any older json file
     * with the filename {@value JSON_FILENAME_INTERNAL_STORAGE}.
     * @param jsonObject the json object to save
     */
    public void saveJSONToFile(JSONObject jsonObject) {
        FileOutputStream outputStream;
        try {
            String jsonString = jsonObject.toString(4);

            outputStream = context.openFileOutput(
                    JSON_FILENAME_INTERNAL_STORAGE, Context.MODE_PRIVATE);
            outputStream.write(jsonString.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Method deletes the old JSON Array (for testing only)
     *
     */
    public void deleteJSONFile(){
        String dir = context.getFilesDir().getAbsolutePath();
        File file = new File(dir, JSON_FILENAME_INTERNAL_STORAGE);
        file.delete();
    }

}
