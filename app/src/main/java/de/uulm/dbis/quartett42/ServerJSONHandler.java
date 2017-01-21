package de.uulm.dbis.quartett42;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import de.uulm.dbis.quartett42.data.Deck;
import de.uulm.dbis.quartett42.data.ImageCard;

/**
 * a JSON Handler for the Server
 * Created by Luis on 12.01.2017.
 */
public class ServerJSONHandler {
    public static final String URL_DECKS_OVERVIEW = "http://quartett.af-mba.dbis.info/decks/";

    /**
     * Get a list of every deck from the server,
     * <b>each deck containing only name and one imagecard</b>.
     * Used for showing a deck overview.
     * @return a list of decks as an overview
     */
    public ArrayList<Deck> getDecksOverview() {
        ArrayList<Deck> onlineDecks = new ArrayList<Deck>();

        try{
            String jsonString = loadOnlineData(new URL(URL_DECKS_OVERVIEW));
            JSONArray onlineDeckList = new JSONArray(jsonString);
            for(int i = 0; i < onlineDeckList.length(); i++){
                JSONObject onlineDeck = onlineDeckList.getJSONObject(i);
                int deckID = onlineDeck.getInt("id");
                String deckName = onlineDeck.getString("name");
                String deckDescription = onlineDeck.getString("description");
                String imageUri = onlineDeck.getString("image");

                ImageCard newImage = new ImageCard(imageUri, deckDescription);
                Deck newDeck = new Deck(deckName, newImage, null, null, Deck.SRC_MODE_SERVER);
                newDeck.setID(deckID);
                onlineDecks.add(newDeck);
            }

        } catch(Exception e){
            e.printStackTrace();
        }

        return onlineDecks;
    }


    /**
     * Loads a single deck. The location where this method looks for the deck depends on
     * the jsonMode set in the constructor (assets/internal/both).
     *
     *
     * @param chosenDeckName the name of the deck
     * @return the deck with the specified name, or null if no deck was found
     */
/*
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
                    //return the deck
                    return new Deck(chosenDeckName, deckImage, propertyList, cardList, Deck.SRC_MODE_SERVER);
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        // no deck was found
        return null;
    }


*/



    ///////////////////////////////
    // COMMUNICATION WITH SERVER //
    ///////////////////////////////


    /**
     * loads "raw" json data from the server as string.
     * @param url the url that is used in the request (see slides)
     * @return the string containing the json data
     */
    public String loadOnlineData(URL url){
        String jsonString = "not started";
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Authorization", "Basic YWRtaW46ZGIxJGFkbWlu");
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            jsonString = "";
            byte[] buffer = new byte[1024];
            int bytesRead;
            while((bytesRead = in.read(buffer)) != -1) {
                jsonString += new String(buffer, 0, bytesRead);
            }

        } catch(IOException e){
            e.printStackTrace();
            jsonString = "error";
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return jsonString;
    }
}
