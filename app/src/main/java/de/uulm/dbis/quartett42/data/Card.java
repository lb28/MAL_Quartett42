package de.uulm.dbis.quartett42.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Fischbach on 21.12.2016.
 */

public class Card {
    private String name;

    private int id;

    private ArrayList<ImageCard> imageList;
    /** Key-Value-Paare der Attribute <Name, Wert>
     * Wichtig: Value ist vom Typ Double und nicht double
     *
     */

    private HashMap<String, Double> attributeMap;

    public Card(int id) {
        this("", id, new ArrayList<ImageCard>(), new HashMap<String, Double>());
    }

    /** Konstruktor
     *
     * @param name
     * @param id
     * @param imageList
     * @param attributeMap<String, Double>
     */
    public Card(String name, int id, ArrayList<ImageCard> imageList, HashMap<String, Double> attributeMap) {
        this.name = name;
        this.id = id;
        this.imageList = imageList;
        this.attributeMap = attributeMap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<ImageCard> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<ImageCard> imageList) {
        this.imageList = imageList;
    }

    public HashMap<String, Double> getAttributeMap() {
        return attributeMap;
    }

    public void setAttributeMap(HashMap<String, Double> attributeMap) {
        this.attributeMap = attributeMap;
    }

    /** Zum Testen
     *
     * @return
     */
    @Override
    public String toString() {
        return "Card{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", imageList=" + imageList.toString() +
                ", attributeMap=" + attributeMap.toString() +
                '}';
    }

    public JSONObject toJSON(){

        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("id", id);
            JSONArray images = new JSONArray();
            // add all the images to a jsonArray
            for (ImageCard imageCard : imageList) {
                images.put(imageCard.toJSON());
            }
            jsonObject.put("images", images);
            // add all the attributes to a jsonArray

            JSONObject values = new JSONObject();
            for (String attrName : attributeMap.keySet()) {
                values.put(attrName, attributeMap.get(attrName));
            }
            jsonObject.put("values", values);

            return jsonObject;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
}
