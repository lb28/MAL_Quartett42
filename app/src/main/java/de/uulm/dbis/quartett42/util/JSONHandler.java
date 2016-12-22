package de.uulm.dbis.quartett42.util;

import java.util.HashMap;
import java.util.Set;

import de.uulm.dbis.quartett42.data.ImageCard;

/**
 * Created by Fischbach on 21.12.2016.
 */

public class JSONHandler {

    //TODO: alles

    /** Main Methode zum Testen:
     *
     * @param args
     */
    public static void main(String[] args){
        System.out.println("test method");

        HashMap<String, Double> test = new HashMap<String, Double>();
        test.put("eins", 1.0);
        test.put("zwei", 2.0);
        test.put("drei", 3.0);
        Set<String> testset = test.keySet();
        String[] testArray = testset.toArray(new String[testset.size()]);
        System.out.println(testArray[0]+" "+testArray[1]+" "+testArray[2]);
    }

}
