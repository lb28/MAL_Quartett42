package de.uulm.dbis.quartett42.data;

/**
 * Created by Fischbach on 21.12.2016.
 */

public class Property {
    private String name;

    private String unit;

    /**
     * True if maximum wins, false if minimum wins
     */
    private Boolean maxwinner;

    //int id; //wird wahrscheinlich nicht gebraucht

    /** Konstruktor
     *
     * @param name
     * @param unit
     * @param maxwinner
     */
    public Property(String name, String unit, Boolean maxwinner) {
        this.name = name;
        this.unit = unit;
        this.maxwinner = maxwinner;
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

    public Boolean getMaxwinner() {
        return maxwinner;
    }

    public void setMaxwinner(Boolean maxwinner) {
        this.maxwinner = maxwinner;
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
                ", maxwinner=" + maxwinner +
                '}';
    }
}
