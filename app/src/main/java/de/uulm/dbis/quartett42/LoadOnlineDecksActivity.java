package de.uulm.dbis.quartett42;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import de.uulm.dbis.quartett42.data.Deck;
import de.uulm.dbis.quartett42.data.ImageCard;


public class LoadOnlineDecksActivity extends AppCompatActivity {

    // https://dhc.restlet.com/ for testing

    URL url;
    ArrayList<Deck> deckList;

    GridView gridView;
    GridViewAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_online_decks);

        deckList = new ArrayList<Deck>();

        try{
            url = new URL("http://quartett.af-mba.dbis.info/decks/");
        }catch(MalformedURLException e){
            e.printStackTrace();
        }

        // TODO make all this AsynchTask
        new Thread(new Runnable() {
            public void run() {
                String content = loadOnlineData(url);
                System.out.println(content);
                deckList = getDeckListFromOnlineData(content);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run(){
                        makeGridView(deckList);
                    }
                });
            }
        }).start();



    }

    //TODO mglw. auslagern in HTTP Klasse:
    //Method to load online data
    public String loadOnlineData(URL url){
        String content = "not started";
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Authorization", "Basic YWRtaW46ZGIxJGFkbWlu");
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            content="";
            int data = in.read();
            while (data != -1) {
                char current = (char) data;
                data = in.read();
                content += current;
            }
        } catch(IOException e){
            e.printStackTrace();
            content = "error";
        } finally {
            urlConnection.disconnect();
        }

        return content;
    }

    //TODO Auslagern in neue JSON Klasse:
    //Get Deck ArrayList from JSON String
    public ArrayList<Deck> getDeckListFromOnlineData(String jsonString){
        ArrayList<Deck> onlineDecks = new ArrayList<Deck>();

        try{
            JSONArray onlineDeckList = new JSONArray(jsonString);
            for(int i = 0; i < onlineDeckList.length(); i++){
                JSONObject onlineDeck = onlineDeckList.getJSONObject(i);
                int deckID = onlineDeck.getInt("id");
                String deckName = onlineDeck.getString("name");
                String deckDescription = onlineDeck.getString("description");

                //TODO load picture or save picture and then load the URL...
                //therefore probably a second adapter is necessary which shows pictures
                //given by an online URL instead of one that loads Bitmaps from the assets

                ImageCard newImage = new ImageCard("none", deckDescription);
                Deck newDeck = new Deck(deckName, newImage, null, null);
                newDeck.setID(deckID);
                onlineDecks.add(newDeck);
            }

        }catch(JSONException ej){
            ej.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

        return onlineDecks;
    }

    //Method to put this all into the Gridview:
    public void makeGridView(ArrayList<Deck> deckList){
        try{
            //Als Grid-Layout setzen:
            gridView = (GridView) findViewById(R.id.galleryGridView);
            gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, deckList);
            gridView.setAdapter(gridAdapter);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
