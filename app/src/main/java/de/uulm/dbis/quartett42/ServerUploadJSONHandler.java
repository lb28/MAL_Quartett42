package de.uulm.dbis.quartett42;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

import de.uulm.dbis.quartett42.data.Card;
import de.uulm.dbis.quartett42.data.Deck;

/**
 * Created by Tim on 25.01.17.
 */

public class ServerUploadJSONHandler {

    private static final String URL_AUTHORIZATION = "Basic YWRtaW46ZGIxJGFkbWlu";
    private static final String URL_DECKS = "http://quartett.af-mba.dbis.info/decks/";

    private Context context;
    private HttpURLConnection urlConnection = null;
    private URL url;
    private Deck deckToUpload, tempDeck;
    private int deckToUploadId;
    private ArrayList<Deck> deckOverviewList;
    private ArrayList<Card> cardsToUpload;

    public ServerUploadJSONHandler(Context context){
        this.context = context;
    }

    public void uploadDeck(String deckname){

        //TODO bisher wird nur in den Assets geschaut weil der Konstruktor kein src_mode hat
        LocalJSONHandler ljh = new LocalJSONHandler(context);
        deckToUpload = ljh.getDeck(deckname);

        //testen ob hochladen wegen name möglich
        // /decks
        //wenn ja name hochladen -> deck bekommt id

        try {
            url = new URL(URL_DECKS);

            //json objekt erstellen
            JSONObject postData = new JSONObject();
            postData.put("name", deckToUpload.getName());
            postData.put("description", deckToUpload.getImage().getDescription());
            postData.put("misc", "");
            postData.put("misc_version", "1");
            postData.put("filename", ""); //TODO filename suchen
            postData.put("image_base64", url /* + name des bildes*/);

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
            //writer.write(getPostDataString(postData));
            writer.write(postData.toString());
            writer.flush();
            writer.close();
            os.close();

            int responseCode = urlConnection.getResponseCode();

            //response
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED){
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
                //TODO bei fehler vielleicht false als return
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
        ServerJSONHandler sjh = new ServerJSONHandler(context);
        deckOverviewList = sjh.getDecksOverview(false);

        for (Deck d : deckOverviewList){
            if (d.getName().equals(deckToUpload.getName())){
                tempDeck = d;
            }
        }

        deckToUploadId = tempDeck.getID();


        //karten hochladen (id?, name)
        // /decks/{deck_id}/cards
        cardsToUpload = deckToUpload.getCardList();

        //für jede Karte name hochladen
        //dann id holen
        //dann attribute hochladen
        //dann bilder
        for (Card c : cardsToUpload){

            try {
                //name der karte hochladen
                url = new URL(URL_DECKS + deckToUploadId + "/cards/");

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", c.getName());

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
                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();
                os.close();

                int responseCode = urlConnection.getResponseCode();

                //response
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED){
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(urlConnection.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null){
                        sb.append(line);
                        break;
                    }

                    in.close();
                    Log.i("response Card name", sb.toString());
                } else{
                    //TODO bei fehler vielleicht false als return
                    Log.i("response Card name", "false: " + responseCode);
                }

                // id der karte holen




            } catch (MalformedURLException m){
                m.printStackTrace();
            } catch (IOException ioe){
                ioe.printStackTrace();
            } catch (JSONException je){
                je.printStackTrace();
            }


        }





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

    protected String openImageInAssets(String imageName){
        String encodedImageBase64 = "";
        AssetManager assetManager = context.getAssets();
        InputStream fileStream = null;
        try {
            fileStream = assetManager.open(imageName);
            if(fileStream != null){
                //                  BitmapFactory.Options bfo = new BitmapFactory.Options();
                //                  bfo.inPreferredConfig = Bitmap.Config.ARGB_8888;
                //                  Bitmap bitmap = BitmapFactory.decodeStream(fileStream, null, bfo);

                Bitmap bitmap = BitmapFactory.decodeStream(fileStream);
                // Convert bitmap to Base64 encoded image for web
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                // to get image extension file name split the received
                int fileExtensionPosition = imageName.lastIndexOf('.');
                String fileExtension = imageName.substring(fileExtensionPosition+1);
                //                  Log.d(IConstants.TAG,"fileExtension: " + fileExtension);

                if(fileExtension.equalsIgnoreCase("png")){
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    //                      Log.d(IConstants.TAG,"fileExtension is PNG");
                }else if(fileExtension.equalsIgnoreCase("jpg") || fileExtension.equalsIgnoreCase("jpeg")){
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    //                      Log.d(TAG,"fileExtension is JPG");
                }

                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String imgageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                encodedImageBase64 = "data:image/png;base64," + imgageBase64;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return encodedImageBase64="";
        }
        finally {
            //Always clear and close
            try {
                if(fileStream != null) {
                    fileStream.close();
                    fileStream = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//      Log.d(TAG,"encodedImageBase64: " + encodedImageBase64);
        return encodedImageBase64;
    }






}
