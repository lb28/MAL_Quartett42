package de.uulm.dbis.quartett42.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Fischbach on 21.12.2016.
 */

public class Property {
    private String name;
    private String unit;

    /**
     * True if maximum wins, false if minimum wins
     */
    private boolean maxWinner;

    /**
     * optional value field so the value can be passed along with attribute name, unit, and maxWinner
     * (convenient for the adapter handling the display)
     * NOTE: The "actual" value (which is used during the game) is stored inside each card's attributeMap.
     * This only serves display purposes.
     */
    private double value;

    //int id; //wird wahrscheinlich nicht gebraucht

    /** Konstruktor
     *
     * @param name
     * @param unit
     * @param maxWinner
     */
    public Property(String name, String unit, boolean maxWinner) {
        this.name = name;
        this.unit = unit;
        this.maxWinner = maxWinner;
    }

    /**
     * DO NOT use this unless for display purposes (e.g. feeding adapters)!
     * (the value field is not meant to hold the actual values,
     * but is used when displaying the attributes)
     * @param name
     * @param unit
     * @param maxWinner
     * @param value
     */
    public Property(String name, String unit, boolean maxWinner, double value) {
        this(name, unit, maxWinner);
        this.value = value;
    }

    /**
     * Default constructor: creates property with empty name, unit, and maxWinner = false
     */
    public Property() {
        this("", "", false);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public boolean isMaxWinner() {
        return maxWinner;
    }

    public void setMaxWinner(boolean maxWinner) {
        this.maxWinner = maxWinner;
    }

    /**
     * DO NOT use this unless for display purposes (e.g. feeding adapters)
     */
    public double getValue() {
        return value;
    }

    /**
     * DO NOT use this unless for display purposes (e.g. feeding adapters)
     */
    public void setValue(double value) {
        this.value = value;
    }

    @Override
    /** Zum Testen
     *
     * @return String
     */
    public String toString() {
        return "Property{" +
                "name='" + name + '\'' +
                ", unit='" + unit + '\'' +
                ", maxWinner=" + maxWinner +
                '}';
    }

    public JSONObject toJSON() {
        JSONObject property = new JSONObject();
        try {
            property.put("name", name);
            property.put("unit", unit);
            property.put("maxwinner", maxWinner);
            return property;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
