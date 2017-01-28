package de.uulm.dbis.quartett42;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import de.uulm.dbis.quartett42.data.Card;
import de.uulm.dbis.quartett42.data.Deck;
import de.uulm.dbis.quartett42.data.ImageCard;
import de.uulm.dbis.quartett42.data.Property;

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
    private ArrayList<Property> propertyList;
    private HashMap<String, Double> hashMap;
    private ArrayList<ImageCard> imageList;


    public ServerUploadJSONHandler(Context context){
        this.context = context;
    }

    /**
     * lädt ein deck hoch
     * erst der deckname nach:
     * http://quartett.af-mba.dbis.info/decks/
     * holt id des decks
     * für alle karten:
     * name der karte hochladen nach:
     * http://quartett.af-mba.dbis.info/decks/{deck_id}/cards
     * holt karten id
     * alle attribute der karte mit werten hochladen nach:
     * http://quartett.af-mba.dbis.info/decks/{deck_id}/cards/{card_id}/attributes
     * alle bilder der karte hochladen nach:
     * http://quartett.af-mba.dbis.info/decks/{deck_id}/cards/{card_id}/images
     *
     * @param deckname
     */
    public boolean uploadDeck(String deckname){

        //TODO bisher wird nur in den Assets geschaut weil der Konstruktor kein src_mode hat
        LocalJSONHandler ljh = new LocalJSONHandler(context, Deck.SRC_MODE_ASSETS);
        deckToUpload = ljh.getDeck(deckname);

        //deckToUpload might be null (if the handler does not find it)
        if (deckToUpload == null){
            return false;
        }

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
            postData.put("filename", "" + deckToUpload.getName() + "_thumbnail"); //TODO über fileendung nachdenken
            postData.put("image_base64", "" /* TODO bild als base64-string*/);

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
                //if an error occured return false
                Log.i("response", "false: " + responseCode);
                return false;
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

        propertyList = deckToUpload.getPropertyList();

        for (Card c : cardsToUpload){

            try {
                //name der karte hochladen
                url = new URL(URL_DECKS + deckToUploadId + "/cards/");
                Log.i("karte hochladen url", "" + url);

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
                    //if an error ocurred return false
                    Log.i("response Card name", "false: " + responseCode);
                    return false;
                }



                // id der karte holen
                int tmpCardID = 0;

                String jsonStringCards = sjh.loadOnlineData(url);
                JSONArray cardsArray = new JSONArray(jsonStringCards);

                for(int i = 0; i < cardsArray.length(); i++) {
                    JSONObject tmpIDCard = cardsArray.getJSONObject(i);
                    tmpCardID = tmpIDCard.getInt("id");
                }
                Log.i("card id error", "" + tmpCardID);



                //attribute hochladen
                url = new URL(URL_DECKS + deckToUploadId + "/cards/" + tmpCardID + "/attributes/");
                Log.i("url attribute hochladen", "" + url);

                hashMap = c.getAttributeMap();

                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObjectAttribute = new JSONObject();
                //hole alle Name-Werte-Paare und pack sie in den post request
                for (Property p : propertyList){
                    String higher_wins = "lower_wins";
                    Boolean higherWins = p.isMaxWinner();
                    if (higherWins == true){
                        higher_wins = "higher_wins";
                    }
                    String nameProperty = p.getName();

                    jsonObjectAttribute.put("name", nameProperty);
                    jsonObjectAttribute.put("value", hashMap.get(nameProperty));
                    jsonObjectAttribute.put("unit", p.getUnit());
                    jsonObjectAttribute.put("order", 0);
                    jsonObjectAttribute.put("what_wins", higher_wins);

                    jsonArray.put(jsonObjectAttribute);
                }

                //connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Authorization", URL_AUTHORIZATION);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);

                //write
                OutputStream ost = urlConnection.getOutputStream();
                BufferedWriter bwriter = new BufferedWriter(new OutputStreamWriter(ost, "UTF-8"));
                bwriter.write(jsonArray.toString());
                bwriter.flush();
                bwriter.close();
                ost.close();

                int responseCode2 = urlConnection.getResponseCode();

                //response
                if (responseCode2 == HttpURLConnection.HTTP_OK || responseCode2 == HttpURLConnection.HTTP_CREATED){
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(urlConnection.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null){
                        sb.append(line);
                        break;
                    }

                    in.close();
                    Log.i("response Card attr", sb.toString());
                } else{
                    //if an error ocurred return false
                    Log.i("response Card attr", "false: " + responseCode);
                    return false;
                }

                //bilder jeder karte hochladen
                url = new URL(URL_DECKS + deckToUploadId + "/cards/" + tmpCardID + "/images/");
                Log.i("url attribute hochladen", "" + url);

                imageList = c.getImageList();

                JSONArray jsonArrayImages = new JSONArray();
                JSONObject jsonObjectImages = new JSONObject();

                for (int i = 0; i < imageList.size(); i++){

                    ImageCard imageCard = imageList.get(i);

                    jsonObjectImages.put("description", imageCard.getDescription());
                    jsonObjectImages.put("order", 0);
                    jsonObjectImages.put("filename", "" + c.getName() + "_" + i /*TODO fileendung*/);
                    jsonObjectImages.put("image_base64", ""); //TODO base64 string aus uri erstellen

                    jsonArrayImages.put(jsonObjectImages);
                }

                //connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Authorization", URL_AUTHORIZATION);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);

                //write
                OutputStream ostr = urlConnection.getOutputStream();
                BufferedWriter buwriter = new BufferedWriter(new OutputStreamWriter(ostr, "UTF-8"));
                buwriter.write(jsonArray.toString());
                buwriter.flush();
                buwriter.close();
                ostr.close();

                int responseCode3 = urlConnection.getResponseCode();

                //response
                if (responseCode3 == HttpURLConnection.HTTP_OK || responseCode3 == HttpURLConnection.HTTP_CREATED){
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(urlConnection.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null){
                        sb.append(line);
                        break;
                    }

                    in.close();
                    Log.i("response Card images", sb.toString());
                } else{
                    //if an error ocurred return false
                    Log.i("response Card images", "false: " + responseCode);
                    return false;
                }



            } catch (MalformedURLException m){
                m.printStackTrace();
            } catch (IOException ioe){
                ioe.printStackTrace();
            } catch (JSONException je){
                je.printStackTrace();
            }


        }

        return true;

    }

    /*
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

    */

}
