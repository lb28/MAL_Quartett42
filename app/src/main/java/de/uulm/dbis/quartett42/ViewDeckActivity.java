package de.uulm.dbis.quartett42;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import de.uulm.dbis.quartett42.data.Card;
import de.uulm.dbis.quartett42.data.Deck;
import de.uulm.dbis.quartett42.data.ImageCard;
import de.uulm.dbis.quartett42.data.Property;

public class ViewDeckActivity extends AppCompatActivity {
    String jsonString = "";
    String chosenDeck = "";
    int currentCard = 0;
    Deck deck = null;
    ProgressBar spinner; //Spinner fuer Ladezeiten

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_deck);


        // TODO
        // have list of all cards in the deck
        // feed list into custom adapter (PagerAdapter?)
        // the adapter uses cardViewFragment

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);


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

    protected void onResume() {
        super.onResume();

        spinner.setVisibility(View.GONE);
    }

    public void showNextCard(View view) {
        currentCard = (currentCard+1) % deck.getCardList().size();  // wrap around positives
        updateView();
    }

    public void showPreviousCard(View view) {
        int deckSize = deck.getCardList().size();
        currentCard = ((currentCard-1) % deckSize + deckSize) % deckSize;   // wrap around negatives
        updateView();
    }

    public void updateView() {
        // get all the view elements
        ImageView cardImageView = (ImageView) findViewById(R.id.cardImageView);
        TextView cardTitleTextView = (TextView) findViewById(R.id.cardTitleTextView);
        ListView cardAttributeListView = (ListView) findViewById(R.id.cardAttributeListView);

        // get the current card
        Card card = deck.getCardList().get(currentCard);

        ArrayList<String> attrList = buildAttrList(card);

        //  set image
        String imageUri = deck.getName()+"/"+card.getImageList().get(0).getUri();
        InputStream is = null;
        try {
            is = getAssets().open(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            cardImageView.setImageBitmap(bitmap);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // set card name
        cardTitleTextView.setText(card.getName());

        // feed attribute list to array adapter
        ArrayAdapter<String> simpleAdapter = new ArrayAdapter<String>(
                this,
                R.layout.simple_list_item,
                attrList
                );

        cardAttributeListView.setAdapter(simpleAdapter);


        //TextView cardContentText = (TextView)findViewById(R.id.cardContentText);
        //cardContentText.setText(deck.getCardList().get(currentCard).toString());
    }

    /**
     * for the list view we need to create an arraylist of strings containing
     * - name
     * - maxwinner
     * - value
     * - unit
     * @param card the currently selected card
     * @return the list of attributes formatted for display
     */
    private ArrayList<String> buildAttrList(Card card) {
        ArrayList<String> attrList = new ArrayList<String>();

        // loop through each property
        for (Property p: deck.getPropertyList()) {
            // this is a simple formatted string,
            // could be replaced e.g. by a LinearLayout (custom adapter needed)
            String maxWinnerString = p.getMaxwinner() ? "^" : "v";
            String attrString = String.format("%10s %-20s %-10s %s",
                    p.getName(),
                    maxWinnerString,
                    card.getAttributeMap().get(p.getName()),
                    p.getUnit());
            attrList.add(attrString);
        }

        return attrList;
    }

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
                    deck = new Deck(chosenDeck, deckImage, propertyList, cardList);

                    break;
                }
            }

            updateView();

        } catch (Exception e){
            e.printStackTrace();
        }


    }

}
