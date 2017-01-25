package de.uulm.dbis.quartett42;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import de.uulm.dbis.quartett42.data.Card;
import de.uulm.dbis.quartett42.data.Deck;
import de.uulm.dbis.quartett42.data.ImageCard;
import de.uulm.dbis.quartett42.data.Property;

import static de.uulm.dbis.quartett42.data.Deck.SRC_MODE_SERVER;

/**
 * a JSON Handler for the Server
 * Created by Luis on 12.01.2017.
 */
public class ServerJSONHandler {
    private static final String TAG = "ServerJSONHandler";

    private static final String URL_DECKS_OVERVIEW = "http://quartett.af-mba.dbis.info/decks/";
    private static final String URL_AUTHORIZATION = "Basic YWRtaW46ZGIxJGFkbWlu";

    private Context context;

    public ServerJSONHandler(Context context) {
        this.context = context;
    }

    /**
     * Get a list of every deck from the server,
     * <b>each deck containing only name and one imagecard</b>.
     * Used for showing a deck overview.
     * @param hideDownloadedDecks specifies if the list should contain the decks that the user
     *                            already downloaded (i.e. decks that are in internal storage)
     * @return a list of decks as an overview
     */
    public ArrayList<Deck> getDecksOverview(boolean hideDownloadedDecks) {
        ArrayList<Deck> onlineDecks = new ArrayList<Deck>();

        // get hashSet of names to be hidden
        HashSet<String> hiddenNames = new HashSet<>();
        LocalJSONHandler localJSONHandler = new LocalJSONHandler(
                context, LocalJSONHandler.JSON_MODE_INTERNAL_STORAGE);
        for (Deck deck : localJSONHandler.getDecksOverview()) {
            hiddenNames.add(deck.getName());
        }

        try{
            String jsonString = loadOnlineData(new URL(URL_DECKS_OVERVIEW));
            JSONArray onlineDeckList = new JSONArray(jsonString);
            for(int i = 0; i < onlineDeckList.length(); i++){
                JSONObject onlineDeck = onlineDeckList.getJSONObject(i);
                String deckName = onlineDeck.getString("name");
                // if hideDownloadedDecks is true, then
                // only add deck to list if it is not in the hidden decks list
                if (!(hideDownloadedDecks && hiddenNames.contains(deckName))) {
                    int deckID = onlineDeck.getInt("id");
                    String deckDescription = onlineDeck.getString("description");
                    String imageUri = onlineDeck.getString("image");

                    ImageCard newImage = new ImageCard(imageUri, deckDescription);
                    Deck newDeck = new Deck(deckName, newImage, null, null, SRC_MODE_SERVER);
                    newDeck.setID(deckID);
                    onlineDecks.add(newDeck);
                }
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
     * The image of the deck and the cards is still the online URL
     * the picture has to be downloaded and saved when parsing the data object to JSON
     *
     * @param chosenDeckID the id of the deck which wants to be downloaded
     * @return the deck with the specified name, or null if no deck was found
     */

    public Deck getDeck(int chosenDeckID){
        Deck resultDeck = null;
        try {
            //1. Load Deck information:
            String deckURL = URL_DECKS_OVERVIEW+chosenDeckID+"/";
            String jsonStringDeck = loadOnlineData(new URL(deckURL));
            if(jsonStringDeck.equals("error")) return null;

            JSONObject onlineDeck = new JSONObject(jsonStringDeck);
            String deckName = onlineDeck.getString("name");
            if(!Util.checkString(deckName)) return null;
            String deckDescription = onlineDeck.getString("description");
            if(!Util.checkString(deckDescription)) return null;
            String deckImageURL = onlineDeck.getString("image");
            ImageCard deckImage = new ImageCard(deckImageURL, deckDescription);

            //2. Load List of all cards:
            String deckCardsURL = deckURL+"cards/";
            String jsonStringCards = loadOnlineData(new URL(deckCardsURL));
            if(jsonStringCards.equals("error")) return null;

            ArrayList<Property> propertyArrayList = new ArrayList<Property>();
            ArrayList<Card> cardArrayList = new ArrayList<Card>();

            JSONArray onlineCardsArray = new JSONArray(jsonStringCards);
            for(int i = 0; i < onlineCardsArray.length(); i++){
                JSONObject tmpIDCard = onlineCardsArray.getJSONObject(i);
                int onlineCardID = tmpIDCard.getInt("id");
                //Name kann weggelassen werden... kommt nochmal


                //3. Load Data for single Card:
                String singleCardURL = deckCardsURL+onlineCardID+"/";
                String jsonStringSingleCard = loadOnlineData(new URL(singleCardURL));
                if(jsonStringSingleCard.equals("error")) return null;

                JSONObject onlineSingleCard = new JSONObject(jsonStringSingleCard);
                String cardName = onlineSingleCard.getString("name");
                if(!Util.checkString(cardName)) return null;

                HashMap<String, Double> tmpAttributeHashmap = new HashMap<String, Double>();


                //4. Load Properties
                String propertiesURL = singleCardURL+"attributes/";
                String jsonStringProperties = loadOnlineData(new URL(propertiesURL));
                if(jsonStringProperties.equals("error")) return null;

                JSONArray attributeArrayList = new JSONArray(jsonStringProperties);
                if(i == 0){ //do this only once
                    for(int p = 0; p < attributeArrayList.length(); p++){
                        JSONObject onlineProperty = attributeArrayList.getJSONObject(p);
                        String propertyName = onlineProperty.getString("name");
                        if(!Util.checkString(propertyName)) return null;
                        String propertyUnit = onlineProperty.getString("unit");
                        if(!Util.checkString(propertyUnit)) return null;
                        boolean maxWinner = onlineProperty.getString("what_wins").equals("higher_wins");

                        Property tmpProperty = new Property(propertyName, propertyUnit, maxWinner);
                        propertyArrayList.add(tmpProperty);
                    }
                }


                //5. Load Attributes for every Card
                for(int p = 0; p < attributeArrayList.length(); p++){
                    JSONObject onlineProperty = attributeArrayList.getJSONObject(p);
                    String propertyName = onlineProperty.getString("name");
                    if(!Util.checkString(propertyName)) return null;
                    Double propertyValue = onlineProperty.getDouble("value");
                    tmpAttributeHashmap.put(propertyName, propertyValue);
                }

                //6. Load Images and Descriptions
                String cardImagesURL = singleCardURL+"images/";
                String jsonCardImages = loadOnlineData(new URL(cardImagesURL));
                if(jsonCardImages.equals("error")) return null;

                ArrayList<ImageCard> tmpImageList = new ArrayList<ImageCard>();
                JSONArray cardImagesArray = new JSONArray(jsonCardImages);
                for(int l = 0; l < cardImagesArray.length(); l++){
                    JSONObject onlineCardImage = cardImagesArray.getJSONObject(l);
                    String cardImageURL = onlineCardImage.getString("image");
                    String cardImageDescription = onlineCardImage.getString("description");
                    if(!Util.checkString(cardImageDescription)) return null;

                    ImageCard tmpCardImage = new ImageCard(cardImageURL, cardImageDescription);
                    tmpImageList.add(tmpCardImage);
                }

                //7. Built Card together and add it to the ArrayList
                Card tmpCard = new Card(cardName, i, tmpImageList, tmpAttributeHashmap);
                cardArrayList.add(tmpCard);

            }

            //8. Built deck together:
            resultDeck = new Deck(deckName, deckImage, propertyArrayList, cardArrayList, SRC_MODE_SERVER);

        } catch (Exception e){
            e.printStackTrace();
        }

        return resultDeck;
    }



    ///////////////////////////////
    // COMMUNICATION WITH SERVER //
    ///////////////////////////////


    /**
     * loads "raw" json data from the server as string.
     * @param url the url that is used in the request (see slides)
     * @return the string containing the json data
     */
    public String loadOnlineData(URL url){
        Log.i(TAG, "loadOnlineData: Trying to download " + url.toString());
        String jsonString = "error";
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Authorization", URL_AUTHORIZATION);
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
