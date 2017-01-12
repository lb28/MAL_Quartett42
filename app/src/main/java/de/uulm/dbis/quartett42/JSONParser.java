package de.uulm.dbis.quartett42;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

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
    private String jsonFileName;

    public JSONParser() {
        // use default / example json
        this("jsonexample.json");
    }

    public JSONParser(String jsonFileName) {
        this.jsonFileName = jsonFileName;
    }

    // TODO implement getAllDecks, getCard, getGame, etc... (like a DB handler)

    /**
     * Loads a single deck
     *
     * @param context
     * @param chosenDeckName
     * @return
     */
    public Deck getDeck(Context context, String chosenDeckName){
        try {
            InputStream in = context.getAssets().open(jsonFileName);
            int size = in.available();
            byte[] buffer = new byte[size];
            in.read(buffer);
            in.close();
            String jsonString = new String(buffer, "UTF-8");

            // Getting JSON Array node
            JSONObject jsonObj = new JSONObject(jsonString);
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
