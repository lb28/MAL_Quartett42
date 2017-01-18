package de.uulm.dbis.quartett42;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import de.uulm.dbis.quartett42.data.Card;
import de.uulm.dbis.quartett42.data.Deck;
import de.uulm.dbis.quartett42.data.ImageCard;
import de.uulm.dbis.quartett42.data.Property;

/**
 * Acts like a DB manager for the json file
 * Created by Luis on 12.01.2017.
 */
public class JSONParser {
    public static final String JSON_MODE_ASSETS = "jsonexample.json";
    public static final String JSON_MODE_INTERNAL_STORAGE = "quartett.json";
    //public static final String JSON_MODE_SERVER = "???";

    // one of the above; maybe as enum?
    private String jsonMode;
    private Context context;

    public JSONParser(Context context) {
        // default constructor from assets, may be changed later
        this(context, JSON_MODE_ASSETS);
    }

    public JSONParser(Context context, String jsonMode) {
        this.context = context;
        this.jsonMode = jsonMode;
    }

    private JSONObject readJSONObject() {
        switch (jsonMode) {
            case JSON_MODE_ASSETS:
                // read the json object from the assets
                try {
                    InputStream in = context.getAssets().open(JSON_MODE_ASSETS);
                    int size = in.available();
                    byte[] buffer = new byte[size];
                    in.read(buffer);
                    in.close();
                    String jsonString = new String(buffer, "UTF-8");
                    // Getting JSON Array node
                    return new JSONObject(jsonString);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            case JSON_MODE_INTERNAL_STORAGE:
                // read the json object from the internal storage
                break;
//            case JSON_MODE_SERVER:
//                break;
        }

        // if everyting failed
        return null;
    }


    // TODO implement getCard, getGame, etc... (like a DB handler)

    public ArrayList<Deck> getAllDecks() {
        ArrayList<Deck> deckList = new ArrayList<Deck>();
        try {
            JSONObject jsonObj = readJSONObject();

            JSONArray decks = jsonObj.getJSONArray("decks");
            for (int i = 0; i < decks.length(); i++) {
                JSONObject tmpDeck = decks.getJSONObject(i);
                String deckName = tmpDeck.getString("name");
                String deckDescription = tmpDeck.getString("description");
                String deckImageUri = tmpDeck.getString("image");
                //Cards und Properties sind erst mal egal fuer die Deckuebersicht

                ImageCard newImage = new ImageCard(deckImageUri, deckDescription);
                Deck newDeck = new Deck(deckName, newImage, null, null);
                deckList.add(newDeck);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return deckList;
    }

    /**
     * Loads a single deck
     *
     * @param chosenDeckName
     * @return
     */
    public Deck getDeck(String chosenDeckName){
        try {
            JSONObject jsonObj = readJSONObject();

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
                    //return the deck
                    return new Deck(chosenDeckName, deckImage, propertyList, cardList);
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        // no deck was found
        return null;
    }
}
