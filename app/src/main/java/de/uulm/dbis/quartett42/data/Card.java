package de.uulm.dbis.quartett42.data;

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

    //int points; //wird wahrscheinlich nicht gebraucht sondern on the fly berechnet?

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
}
