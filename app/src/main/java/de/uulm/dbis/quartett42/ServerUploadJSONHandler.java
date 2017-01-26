package de.uulm.dbis.quartett42;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import de.uulm.dbis.quartett42.data.Deck;

/**
 * Created by Tim on 25.01.17.
 */

public class ServerUploadJSONHandler {

    private static final String URL_AUTHORIZATION = "Basic YWRtaW46ZGIxJGFkbWlu";

    String deckToUpload;

    private Context context;

    public ServerUploadJSONHandler(Context context){
        this.context = context;
    }

    public void uploadDeck(String deckname){

        URL url;
        deckToUpload = "Test Deck";

        //testen ob hochladen wegen name möglich
        // /decks
        //wenn ja name hochladen -> deck bekommt id
        Log.i("upload", "test");
        HttpURLConnection urlConnection = null;

        try {
            url = new URL("http://quartett.af-mba.dbis.info/decks/");

            //test erstellen
            JSONObject postData = new JSONObject();
            postData.put("name", deckToUpload);
            postData.put("description", "Testdeck42");
            postData.put("misc", "");
            postData.put("misc_version", "1");
            postData.put("image", "");
            Log.i("parameter", postData.toString());

            //connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Authorization", URL_AUTHORIZATION);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);

            //write
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postData));
            writer.flush();
            writer.close();
            os.close();

            int responseCode = urlConnection.getResponseCode();

            //response
            if (responseCode == HttpURLConnection.HTTP_OK){
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null){
                    sb.append(line);
                    break;
                }

                in.close();
                Log.i("response", sb.toString());
            } else{
                Log.i("response", "false: " + responseCode);
            }

        } catch(IOException e){
            e.printStackTrace();
        } catch(JSONException j){
            j.printStackTrace();
        } catch(Exception e1){
            e1.printStackTrace();
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }


        //id holen über name
        // /decks

        //mit id detailliertes deck (id?, name, beschreibung, deckbild) hochladen
        // /decks/{deck_id}

        //karten hochladen (id?, name)
        // /decks/{deck_id}/cards

        //detaillierte karten (id?, deck, name, order) hochladen
        // /decks/{deck_id}/cards/{card_id}

        //attribute jeder karte (id, card, name, value, unit, what_wins, image="") hochladen
        // /decks/{deck_id}/cards/{card_id}/attributes

        //bilder (id, card, order, description, image) hochladen
        // /decks/{deck_id}/cards/{card_id}/images



    }

    public String getPostDataString(JSONObject params) throws Exception{
        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key = itr.next();
            Object value = params.get(key);

            if (first) {
                first = false;
            } else{
                result.append("&");
            }

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }

        Log.i("result.toString", result.toString());
        return result.toString();
    }






}
