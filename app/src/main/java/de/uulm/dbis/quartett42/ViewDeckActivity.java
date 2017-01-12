package de.uulm.dbis.quartett42;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import de.uulm.dbis.quartett42.data.Card;
import de.uulm.dbis.quartett42.data.Deck;
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

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);


        //JSON-String auslesen:
        Intent intent = getIntent();
        jsonString = intent.getStringExtra("json_string");
        chosenDeck = intent.getStringExtra("chosen_deck");

        // use AsyncTask to load Deck from JSON
        new LoadDeckTask().execute();
    }

    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, GalleryActivity.class);
        intent.putExtra("json_string", jsonString);
        startActivity(intent);
        return true;
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

        ArrayList<Property> attrList = buildAttrList(card);

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
        ArrayAdapter<Property> attrListAdapter = new AttributeItemAdapter(
                this,
                R.layout.attr_list_item,
                attrList
                );

        cardAttributeListView.setAdapter(attrListAdapter);

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
    private ArrayList<Property> buildAttrList(Card card) {
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

    private class LoadDeckTask extends AsyncTask<Void, Void, Deck> {

        @Override
        protected Deck doInBackground(Void... voids) {
            JSONParser jsonParser = new JSONParser();
            try { synchronized (this) {
                this.wait(1000);
            }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return jsonParser.getDeck(ViewDeckActivity.this, chosenDeck);
        }

        @Override
        protected void onPostExecute(Deck deck) {
            ViewDeckActivity.this.deck = deck; // is this ok?
            updateView();
            spinner.setVisibility(View.GONE);
        }
    }

}
