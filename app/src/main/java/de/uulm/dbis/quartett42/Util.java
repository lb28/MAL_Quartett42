package de.uulm.dbis.quartett42;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import de.uulm.dbis.quartett42.data.Card;
import de.uulm.dbis.quartett42.data.ImageCard;
import de.uulm.dbis.quartett42.data.Property;

/**
 * Utilities class (only static methods)
 *
 * Created by Luis on 12.01.2017.
 */
public class Util {
    private static final String TAG = "Util";

    /**
     * for the list view we need to create an arraylist of strings containing
     * - name
     * - maxwinner
     * - value
     * - unit
     * @param card the currently selected card
     * @return the list of attributes formatted for display
     */
    public static ArrayList<Property> buildAttrList(ArrayList<Property> propList, Card card) {
        ArrayList<Property> attrList = new ArrayList<Property>();

        // loop through each property
        for (Property p: propList) {
            // get the cards value
            double attrValue = card.getAttributeMap().get(p.getName());
            // put it inside the property for the adapter
            Property cardAttr = new Property(p.getName(), p.getUnit(), p.isMaxWinner(), attrValue);
            attrList.add(cardAttr);
        }

        return attrList;
    }

    public static Card buildCardFromRaw(
            ArrayList<String> imgUriList,
            ArrayList<String> imgDescList,
            String attrName,
            double attrValue,
            String cardName) {

        // get the data into the right format
        ArrayList<ImageCard> imageCards = new ArrayList<>();
        for (int i = 0; i < imgUriList.size(); i++) {
            imageCards.add(new ImageCard(imgUriList.get(i), imgDescList.get(i)));
        }

        HashMap<String, Double> attributeMap = new HashMap<>();
        attributeMap.put(attrName, attrValue);
        return new Card(
                cardName,
                0, // id does not matter here
                imageCards,
                attributeMap
        );
    }

    public static Bitmap downloadBitmap(String imageUri) {
        Log.i(TAG, "downloadBitmap: Trying to download " + imageUri);

        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(imageUri);
            urlConnection = (HttpURLConnection) uri.openConnection();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                Bitmap result = BitmapFactory.decodeStream(inputStream);
                //If Image is too big do not use it
                // (Wenn manche meinen sie müssen riesen grosse Bilder hochladen
                // nehmen wir ihre Decks einfach nicht an)
                //System.out.println("groesse "+result.getByteCount());
                if (result.getByteCount() >= 1000000){
                    return null;
                }else{
                    return result;
                }
            }
        } catch (Exception e) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            Log.w("downloadBitmap()", "Error downloading image from " + imageUri);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    /**
     * stores an image at a subfolder "/deckName/fileName" in internal storage.
     * @param image
     * @param deckName
     * @param fileName
     * @return true if the image was successfully saved, false otherwise
     */
    public static boolean storeImage(Bitmap image, String deckName, String fileName) {
        // TODO store image at subfolder specified by the string
        return false;
    }

    /** Checks if a String doesn not contain any illegal Characters for saving it into a json file.
     *
     * @param inputString the String to check
     * @return true if valid, false if invalid
     */
    public static boolean checkString(String inputString){
        if(inputString.indexOf(';') != -1) return false;
        if(inputString.indexOf(',') != -1) return false;
        if(inputString.indexOf('<') != -1) return false;
        if(inputString.indexOf('>') != -1) return false;
        if(inputString.indexOf('{') != -1) return false;
        if(inputString.indexOf('}') != -1) return false;
        if(inputString.indexOf('[') != -1) return false;
        if(inputString.indexOf(']') != -1) return false;
        if(inputString.indexOf('(') != -1) return false;
        if(inputString.indexOf(')') != -1) return false;
        if(inputString.indexOf('"') != -1) return false;
        if(inputString.indexOf('\'') != -1) return false;
        if(inputString.indexOf('=') != -1) return false;

        return true;
    }
}
