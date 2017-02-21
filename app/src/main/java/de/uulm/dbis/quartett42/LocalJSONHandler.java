package de.uulm.dbis.quartett42;

import android.content.Context;
import android.graphics.Bitmap;

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

import de.uulm.dbis.quartett42.data.Card;
import de.uulm.dbis.quartett42.data.Deck;
import de.uulm.dbis.quartett42.data.ImageCard;
import de.uulm.dbis.quartett42.data.Property;

import static de.uulm.dbis.quartett42.data.Deck.SRC_MODE_ASSETS;
import static de.uulm.dbis.quartett42.data.Deck.SRC_MODE_INTERNAL_STORAGE;

/**
 * Acts like a DB manager for the json file
 * Created by Luis on 12.01.2017.
 */
public class LocalJSONHandler {
    public static final String JSON_FILENAME_ASSETS = "jsonexample.json";
    public static final String JSON_FILENAME_INTERNAL_STORAGE = "quartett.json";

    // mode BOTH is broken
//    public static final int JSON_MODE_BOTH = 3;


    private Context context;
    private int jsonMode;

    /**
     * default constructor using internal storage
     * @param context
     */
    public LocalJSONHandler(Context context) {
        this(context, SRC_MODE_INTERNAL_STORAGE);
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

            // if json was empty, return the empty deck list
            if (jsonObj == null) return deckList;
            
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
                if (jsonMode == SRC_MODE_ASSETS) {
                    srcMode = SRC_MODE_ASSETS;
                }
                else if (jsonMode == SRC_MODE_INTERNAL_STORAGE) {
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
                if (jsonMode == SRC_MODE_ASSETS) {
                    srcMode = SRC_MODE_ASSETS;
                }
                else if (jsonMode == SRC_MODE_INTERNAL_STORAGE) {
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
                    if (jsonMode == SRC_MODE_ASSETS) {
                        srcMode = SRC_MODE_ASSETS;
                    }
                    else if (jsonMode == SRC_MODE_INTERNAL_STORAGE) {
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

    public boolean removeDeck(String deleteDeckName) {
        boolean success = false;

        JSONObject oldJsonObj = readJSONFromFile(SRC_MODE_INTERNAL_STORAGE);
        JSONObject newJsonObj = new JSONObject();
        try {
            JSONArray oldDecks = oldJsonObj.getJSONArray("decks");
            JSONArray newDecks = new JSONArray();
            for (int i = 0; i < oldDecks.length(); i++) {
                JSONObject deck = oldDecks.getJSONObject(i);
                if (deck.get("name").equals(deleteDeckName)) {
                    //Delete all images
                    deleteImage(deck.getString("image"));
                    JSONArray cardArray = deck.getJSONArray("cards");
                    for(int c = 0; c < cardArray.length(); c++){
                        JSONObject card = cardArray.getJSONObject(c);
                        JSONArray imageArray = card.getJSONArray("images");
                        for(int j = 0; j < imageArray.length(); j++){
                            JSONObject image = imageArray.getJSONObject(j);
                            deleteImage(image.getString("URI"));
                        }
                    }
                }else{
                    //Write old JSON object into new JSON object
                    newDecks.put(deck);
                }
            }
            newJsonObj.put("decks", newDecks);
            saveJSONToFile(newJsonObj);
            success = true;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return success;
    }


    /**
     * removes a card from a deck (also deletes the images from internal storage)
     * @param deckName the name of the deck containing the card
     * @param cardName the name of the card to be deleted
     * @return true if the deletion was successful
     */
    public boolean removeCard(String deckName, String cardName) {
        JSONObject jsonObj = readJSONFromFile(SRC_MODE_INTERNAL_STORAGE);
        if (jsonObj == null) { return false; }
        try {
            // find the deck
            JSONArray decks = jsonObj.getJSONArray("decks");
            for (int i = 0; i < decks.length(); i++) {
                JSONObject deck = decks.getJSONObject(i);
                if (deck.get("name").equals(deckName)) {
                    // find the card to delete
                    JSONArray cards = deck.getJSONArray("cards");
                    JSONArray newCards = new JSONArray();
                    for (int j = 0; j < cards.length(); j++) {
                        JSONObject card = cards.getJSONObject(j);
                        if (card.get("name").equals(cardName)) {
                            // delete the card images
                            JSONArray imageArray = card.getJSONArray("images");
                            for(int k = 0; k < imageArray.length(); k++){
                                JSONObject image = imageArray.getJSONObject(k);
                                if(!deleteImage(image.getString("URI"))) {
                                    return false;
                                }
                            }
                        } else {
                            // put the card into the copied cards array
                            newCards.put(card);
                        }
                    }
                    deck.remove("cards");
                    deck.put("cards", newCards);
                    saveJSONToFile(jsonObj);
                    return true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        // we did not find the card (or deck)
        return false;
    }

    /**
     * removes a card from a deck (also deletes the images from internal storage)
     * @param deckName the name of the deck containing the card
     * @param cardIndex the index (in the deck's card list) of the card to be deleted
     * @return true if the deletion was successful
     */
    public boolean removeCard(String deckName, int cardIndex) {
        JSONObject jsonObj = readJSONFromFile(SRC_MODE_INTERNAL_STORAGE);
        if (jsonObj == null) { return false; }
        try {
            // find the deck
            JSONArray decks = jsonObj.getJSONArray("decks");
            for (int i = 0; i < decks.length(); i++) {
                JSONObject deck = decks.getJSONObject(i);
                if (deck.get("name").equals(deckName)) {
                    // find the card to delete
                    JSONArray cards = deck.getJSONArray("cards");
                    JSONArray newCards = new JSONArray();
                    for (int j = 0; j < cards.length(); j++) {
                        JSONObject card = cards.getJSONObject(j);
                        if (j == cardIndex) {
                            // delete the card images
                            JSONArray imageArray = card.getJSONArray("images");
                            for(int k = 0; k < imageArray.length(); k++){
                                JSONObject image = imageArray.getJSONObject(k);
                                if(!context.deleteFile(image.getString("URI"))) {
                                    return false;
                                }
                            }
                        } else {
                            // put the card into the copied cards array
                            newCards.put(card);
                        }
                    }
                    deck.remove("cards");
                    deck.put("cards", newCards);
                    saveJSONToFile(jsonObj);
                    return true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        // we did not find the card (or deck)
        return false;
    }

    /**
     * replaces a card in a deck
     * (also deletes the old images from internal storage and saves the new ones)
     * @param deckName the name of the deck containing the card
     * @param cardIndex the index (in the deck's card list) of the card to be deleted
     * @param newCard the new card that replaces the old one
     * @return true if the replacement was successful
     */
    public boolean replaceCard(String deckName, int cardIndex, Card newCard) {
        JSONObject newJsonCard = newCard.toJSON();

        JSONObject jsonObj = readJSONFromFile(SRC_MODE_INTERNAL_STORAGE);
        if (jsonObj == null) { return false; }
        try {
            // find the deck
            JSONArray decks = jsonObj.getJSONArray("decks");
            for (int i = 0; i < decks.length(); i++) {
                JSONObject deck = decks.getJSONObject(i);
                if (deck.get("name").equals(deckName)) {
                    // find the card to delete
                    JSONArray cards = deck.getJSONArray("cards");
                    JSONObject card = cards.getJSONObject(cardIndex);

                    // delete the card's images:
                    JSONArray imageArray = card.getJSONArray("images");
                    for(int k = 0; k < imageArray.length(); k++){
                        // delete the image
                        JSONObject image = imageArray.getJSONObject(k);
                        if(!context.deleteFile(image.getString("URI"))) {
                            return false;
                        }
                    }

                    // replace the json card with the new one
                    cards.put(cardIndex, newJsonCard);

                    saveJSONToFile(jsonObj);
                    return true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        // we did not find the card (or deck)
        return false;
    }

    /**
     * checks if a deck contains the specified card name
     * @param deckName
     * @param cardName
     * @return the index of the card in the deck, or -1 if there is no card with that name
     */
    public int getCardIndex(String deckName, String cardName) {
        JSONObject jsonObj = readJSONFromFile(SRC_MODE_INTERNAL_STORAGE);
        if (jsonObj == null) { return -1; }
        try {
            // find the dec to delete
            JSONArray decks = jsonObj.getJSONArray("decks");
            for (int i = 0; i < decks.length(); i++) {
                JSONObject deck = decks.getJSONObject(i);
                if (deck.get("name").equals(deckName)) {
                    // find the card
                    JSONArray cards = deck.getJSONArray("cards");
                    for (int j = 0; j < cards.length(); j++) {
                        JSONObject card = cards.getJSONObject(j);
                        if (card.get("name").equals(cardName)) {
                            return j;
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }

        // we did not find the card (or deck)
        return -1;
    }

    /**
     * adds a deck to the internal storage json file (without overwriting)
     * @param deck
     * @throws JSONException
     */
    public void saveDeck(Deck deck) throws JSONException {
        JSONObject newJsonDeck = deck.toJSON();
        JSONObject oldJsonDeckList = readJSONFromFile(SRC_MODE_INTERNAL_STORAGE);

        if (oldJsonDeckList != null) {
            JSONArray oldDeckArray = oldJsonDeckList.getJSONArray("decks");
            oldDeckArray.put(newJsonDeck);

            saveJSONToFile(oldJsonDeckList);

        } else {
            //1tes neues Deck, File nicht vorhanden
            oldJsonDeckList = new JSONObject();
            JSONArray newDeckArray = new JSONArray();
            newDeckArray.put(newJsonDeck);
            oldJsonDeckList.put("decks", newDeckArray);

            saveJSONToFile(oldJsonDeckList);
        }
    }

    /**
     * adds a card object to a deck without overwriting (in internal storage).
     * The images have to be saved and the correct uris set already
     * @param deckName the name of the deck the card should be inserted in
     * @param card the card to insert
     * @throws JSONException
     */
    public boolean saveCard(String deckName, Card card) throws JSONException {
        // convert the card into json format
        JSONObject newJsonCard = card.toJSON();

        // get the old card list from the deck
        JSONObject jsonObj = readJSONFromFile(SRC_MODE_INTERNAL_STORAGE);
        JSONArray jsonDecks = jsonObj.getJSONArray("decks");

        // look for the deck with the name
        for (int i = 0; i < jsonDecks.length(); i++) {
            JSONObject jsonDeck = jsonDecks.getJSONObject(i);
            if (jsonDeck.get("name").equals(deckName)) {
                JSONArray jsonCardArray = jsonDeck.getJSONArray("cards");

                // append the new card to the card list
                jsonCardArray.put(newJsonCard);

                // TODO not sure if we have to update the jsonObj explicitly

                // save the new jsonObj
                saveJSONToFile(jsonObj);

                return true;
            }
        }

        return false;
    }

    /**
     * creates an imagecard with the specified picture and description and stores the picture
     * in internal storage with the specified name
     *
     * @param fileName the image Uri as it should appear in the json file
     * @param bitmap the image
     * @param descr the image description
     * @return the imagecard containing the uri for the json file
     */
    public ImageCard createImageCard(String fileName, Bitmap bitmap, String descr) {
        String imageUri = "NO_DECK_IMAGE"; // inconsistency --> TODO put this into constant
        if (saveBitmap(fileName, bitmap)) {
            imageUri = fileName;
        }
        return new ImageCard(imageUri, descr);
    }

    /**
     * Renames a deck (internal storage only)
     * @param oldDeckName the current name
     * @param newDeckName the new deck name
     */
    public boolean renameDeck(String oldDeckName, String newDeckName) {
        JSONObject jsonObj;
        JSONArray decks;

        try {
            jsonObj = readJSONFromFile(SRC_MODE_INTERNAL_STORAGE);

            // if json was empty, return the empty deck list
            if (jsonObj == null) return false;

            decks = jsonObj.getJSONArray("decks");

            // first go through the decks and add all the names to a list
            int deckIndex = -1;
            for (int i = 0; i < decks.length(); i++) {
                JSONObject deck = decks.getJSONObject(i);
                String dName = deck.getString("name");
                if (dName.equals(newDeckName)) {
                    return false; // deckname already exists
                }
                if (dName.equals(oldDeckName)) {
                    deckIndex = i;
                }
            }

            if (deckIndex == -1) {
                return false; // deck not found
            }

            JSONObject deck = decks.getJSONObject(deckIndex);
            deck.remove("name");
            deck.put("name", newDeckName);

            saveJSONToFile(jsonObj);
            return true;

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasDeckNameCollision(String newDeckName) {
        for (Deck d : getDecksOverview()) {
            if (d.getName().equals(newDeckName)) {
                return true;
            }
        }
        return false;
    }

    ///////////////////
    // FILE HANDLING //
    ///////////////////

    /**
     * writes a bitmap to internal storage under the specified file name
     * @param fileName the name of the file (e.g. deckName0_1)
     * @param bitmap
     * @return
     */
    public boolean saveBitmap(String fileName, Bitmap bitmap) {
        if (bitmap != null) {
            FileOutputStream fos;
            try {
                fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                // Writing the bitmap to the output stream
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }

    /**
     * reads the json object from internal storage or from assets (depending on jsonMode)
     * @param jsonMode the mode (assets / internal storage), see the constants in LocalJSONHandler
     * @return the root json object
     */
    public JSONObject readJSONFromFile(int jsonMode) {

        switch (jsonMode) {
            case SRC_MODE_ASSETS:
                // read the json object from the assets
                try {
                    InputStream in = context.getAssets().open(JSON_FILENAME_ASSETS);
                    int size = in.available();
                    byte[] buffer = new byte[size];
                    in.read(buffer);
                    in.close();
                    String jsonString = new String(buffer, "UTF-8");
                    if (jsonString.isEmpty()) return null;
                    return new JSONObject(jsonString);
                } catch (IOException | JSONException e) {
                    //e.printStackTrace();
                    return null;
                }

            case SRC_MODE_INTERNAL_STORAGE:
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
                    if (jsonString.isEmpty()) return null;
                    return new JSONObject(jsonString);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            /*
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
                */
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
    public boolean deleteJSONFile(){
        return deleteImage(JSON_FILENAME_INTERNAL_STORAGE);
    }


    /**
     * deletes an image from internal storage with the given uri
     * @param imageUri the uri of the image to be deleted
     * @return true if the deletion was successful
     */
    private boolean deleteImage(String imageUri) {
        String dir = context.getFilesDir().getAbsolutePath();
        File file = new File(dir, imageUri);
        return file.delete();
    }
}
