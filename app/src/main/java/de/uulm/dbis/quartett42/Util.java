package de.uulm.dbis.quartett42;

import java.util.ArrayList;

import de.uulm.dbis.quartett42.data.Card;
import de.uulm.dbis.quartett42.data.Deck;
import de.uulm.dbis.quartett42.data.Property;

/**
 * Utilities class (only static methods)
 *
 * Created by Luis on 12.01.2017.
 */
public class Util {

    /**
     * for the list view we need to create an arraylist of strings containing
     * - name
     * - maxwinner
     * - value
     * - unit
     * @param card the currently selected card
     * @return the list of attributes formatted for display
     */
    public static ArrayList<Property> buildAttrList(Deck deck, Card card) {
        ArrayList<Property> attrList = new ArrayList<Property>();

        // loop through each property
        for (Property p: deck.getPropertyList()) {
            // get the cards value
            double attrValue = card.getAttributeMap().get(p.getName());
            // put it inside the property for the adapter
            Property cardAttr = new Property(p.getName(), p.getUnit(), p.getMaxwinner(), attrValue);
            attrList.add(cardAttr);
        }

        return attrList;
    }
}
