package de.uulm.dbis.quartett42.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Fischbach on 21.12.2016.
 */

public class Deck {
    /*
     * source modes specify it this deck is in assets, internal storage, or online
     * necessary for the pictures
     */
    public static final int SRC_MODE_ASSETS = 1;
    public static final int SRC_MODE_INTERNAL_STORAGE = 2;
    public static final int SRC_MODE_SERVER = 3;
    public static final int SRC_MODE_NONE = -1;

    public static final String UNICODE_MAX_WINNER = "\u25B2";
    public static final String UNICODE_MIN_WINNER = "\u25BC";

    private String name;

    /** Enthaelt auch gleich die Beschreibung somit kein String description mehr noetig.
     *
     */
    private ImageCard image;

    private ArrayList<Property> propertyList;

    private ArrayList<Card> cardList;

    private int srcMode;

    /** Erweitert um ID, die beim Download benoetigt wird, aber ansonsten nicht
     *
     */
    private int ID;

    //int id; //wird wahrscheinlich nicht gebraucht

    /** Konstruktor, Bild und Beschreibung als ImageCard-Object
     *
     * @param name
     * @param image
     * @param propertyList
     * @param cardList
     * @param srcMode
     */
    public Deck(String name, ImageCard image, ArrayList<Property> propertyList, ArrayList<Card> cardList, int srcMode) {
        this.name = name;
        this.image = image;
        this.propertyList = propertyList;
        this.cardList = cardList;
        this.srcMode = srcMode;
    }

    /** Konstruktor, falls kein Bild vorhanden
     *
     * @param name
     * @param description
     * @param propertyList
     * @param cardList
     */
    public Deck(String name, String description, ArrayList<Property> propertyList, ArrayList<Card> cardList) {
        this(name, new ImageCard(null, description), propertyList, cardList, SRC_MODE_NONE);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ImageCard getImage() {
        return image;
    }

    public void setImage(ImageCard image) {
        this.image = image;
    }

    public ArrayList<Property> getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(ArrayList<Property> propertyList) {
        this.propertyList = propertyList;
    }

    public ArrayList<Card> getCardList() {
        return cardList;
    }

    public void setCardList(ArrayList<Card> cardList) {
        this.cardList = cardList;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getSrcMode() {
        return srcMode;
    }

    public void setSrcMode(int srcMode) {
        this.srcMode = srcMode;
    }


    /** Zum Testen
     *
     * @return
     */
    @Override
    public String toString() {
        return "Deck{" +
                "name='" + name + '\'' +
                ", image=" + image.toString() +
                ", propertyList=" + propertyList.toString() +
                ", cardList=" + cardList.toString() +
                '}';
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("description", image.getDescription());
            jsonObject.put("image", image.getUri());
            // add the properties as json array
            JSONArray properties = new JSONArray();
            for (Property p : propertyList) {
                properties.put(p.toJSON());
            }
            jsonObject.put("properties", properties);
            // add the card list as json array
            JSONArray cards = new JSONArray();
            for (Card c : cardList) {
                cards.put(c.toJSON());
            }
            jsonObject.put("cards", cards);
            return jsonObject;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
