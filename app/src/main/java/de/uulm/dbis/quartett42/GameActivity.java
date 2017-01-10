package de.uulm.dbis.quartett42;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.uulm.dbis.quartett42.data.Card;
import de.uulm.dbis.quartett42.data.Deck;
import de.uulm.dbis.quartett42.data.Game;
import de.uulm.dbis.quartett42.data.ImageCard;
import de.uulm.dbis.quartett42.data.Property;

public class GameActivity extends AppCompatActivity {
    String jsonString = "";
    String chosenDeck = "";
    Game game;

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Toast.makeText(getApplicationContext(), "TODO Darstellung und Spielablauf", Toast.LENGTH_SHORT).show();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        //JSON-String auslesen:
        Intent intent = getIntent();
        jsonString = intent.getStringExtra("json_string");
        chosenDeck = intent.getStringExtra("chosen_deck");
        //System.out.println(jsonString);

        //JSON String auslesen und Game erstellen
        new Thread(new Runnable() {
            public void run() {
                loadData();
            }
        }).start();

    }


    //Methoden der Activity:

    //JSON Daten laden und Game erstellen:
    public void loadData(){
        try {
            // Getting JSON Array node
            JSONObject jsonObj = new JSONObject(jsonString);
            JSONArray decks = jsonObj.getJSONArray("decks");
            //Nach passendem Deck im JSON-Array suchen:
            for (int i = 0; i < decks.length(); i++) {
                JSONObject tmpDeck = decks.getJSONObject(i);
                String deckName = tmpDeck.getString("name");
                if(deckName.equals(chosenDeck)){
                    //Alle Daten fuer das Deck auslesen:
                    String deckDescription = tmpDeck.getString("description");
                    String deckImageUri = tmpDeck.getString("image");
                    ImageCard deckImage = new ImageCard(deckImageUri, deckDescription);
                    //Properties auslesen:
                    ArrayList<Property> propertyList = new ArrayList<Property>();
                    JSONArray properties = tmpDeck.getJSONArray("properties");
                    for(int p = 0; p < properties.length(); p++){
                        JSONObject tmpProperty = properties.getJSONObject(p);
                        String pName = tmpProperty.getString("name");
                        String pUnit = tmpProperty.getString("unit");
                        boolean pMaxwinner = tmpProperty.getBoolean("maxwinner");
                        Property property = new Property(pName, pUnit, pMaxwinner);
                        propertyList.add(property);
                    }
                    //Alle Karten auslesen:
                    ArrayList<Card> cardList = new ArrayList<Card>();
                    JSONArray cards = tmpDeck.getJSONArray("cards");
                    for(int c = 0; c < cards.length(); c++){
                        JSONObject tmpCard = cards.getJSONObject(c);
                        String cName = tmpCard.getString("name");
                        int cId = tmpCard.getInt("id");
                        //Bilder durchlaufen:
                        ArrayList<ImageCard> imageList = new ArrayList<ImageCard>();
                        JSONArray images = tmpCard.getJSONArray("images");
                        for(int j = 0; j < images.length(); j++){
                            JSONObject tmpImage = images.getJSONObject(j);
                            String uri = tmpImage.getString("URI");
                            String iDescription = tmpImage.getString("description");
                            ImageCard cardImage = new ImageCard(uri, iDescription);
                            imageList.add(cardImage);
                        }
                        //Values durchlaufen:
                        HashMap<String, Double> attributeMap = new HashMap<String, Double>();
                        JSONObject values = tmpCard.getJSONObject("values");
                        for(Property px : propertyList){
                            //System.out.println(px.getName()+": "+values.getDouble(px.getName()));
                            attributeMap.put(px.getName(), values.getDouble(px.getName()));
                        }
                        //Karte erstellen
                        Card newCard = new Card(cName, cId, imageList, attributeMap);
                        cardList.add(newCard);
                    }
                    //Deck erstellen:
                    Deck newDeck = new Deck(chosenDeck, deckImage, propertyList, cardList);

                    //Game erstellen:
                    makeGame(newDeck);

                    i = decks.length();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
            //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //Test Ausgabe des Games:
        System.out.println(game.toString());

        playGame();

    }

    //Game erstellen anhand von Deck und Zustand:
    public void makeGame(Deck newDeck){
        //Daten fuer Game aus DefaultSharedPreferences lesen:
        int difficulty = sharedPref.getInt("difficulty", 2);
        int mode = sharedPref.getInt("mode", 1);
        boolean insaneModus = sharedPref.getBoolean("insaneModus", false);
        int roundsLeft = 10;
        if(mode == 1 || mode == 2){
            roundsLeft = sharedPref.getInt("roundsLeft", 10);
        }else{
            roundsLeft = sharedPref.getInt("pointsLeft", 1000);
        }


        //Andere Behandlung je nachdem ob ein neues Spiel gespielt wird oder ein altes geladen wird:
        if(sharedPref.getInt("runningGame", 0) == 0){
            game = new Game(newDeck, difficulty, mode, insaneModus, roundsLeft);
        }else{
            //neues Game erstellen auf Basis der alten Daten:
            //TODO
        }
    }

    //Game durchfuehren (Runden spielen und Anzeige aktualisieren
    public void playGame(){
        //TODO
        //Test-Aufruf der Methoden:
        System.out.println(game.returnCardOfID(game.getCardsPlayer().get(0)));
        System.out.println(game.returnCardOfID(game.getCardsComputer().get(0)));
        System.out.println(game.computerCardChoice());
        System.out.println(game.playCards(game.computerCardChoice()));
        System.out.println(game.toString());

        System.out.println(game.returnCardOfID(game.getCardsPlayer().get(0)));
        System.out.println(game.returnCardOfID(game.getCardsComputer().get(0)));
        System.out.println(game.computerCardChoice());
        System.out.println(game.playCards(game.computerCardChoice()));
        System.out.println(game.toString());

    }

}
