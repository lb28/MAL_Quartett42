package de.uulm.dbis.quartett42;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import de.uulm.dbis.quartett42.data.Card;
import de.uulm.dbis.quartett42.data.Deck;
import de.uulm.dbis.quartett42.data.Property;

public class ViewDeckActivity extends AppCompatActivity {
    String jsonString = "";
    String chosenDeck = "";
    int currentCardIndex = 0;
    Deck deck = null;

    ProgressBar spinner; //Spinner fuer Ladezeiten
    ViewPager viewPager;

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
        currentCardIndex = (currentCardIndex +1) % deck.getCardList().size();  // wrap around positives
        updateView();
    }

    public void showPreviousCard(View view) {
        int deckSize = deck.getCardList().size();
        currentCardIndex = ((currentCardIndex -1) % deckSize + deckSize) % deckSize;   // wrap around negatives
        updateView();
    }

    public void goToNewGame(View view) {
        Intent intent = new Intent(this, NewGameActivity.class);
        intent.putExtra("chosen_deck", chosenDeck);
        intent.putExtra("json_string", jsonString);
        intent.putExtra("new_game_source", "view_deck_activity");
        startActivity(intent);
    }

    public void updateView() {
        // get all the view elements
        TextView cardTitleTextView = (TextView) findViewById(R.id.cardTitleTextView);
        ListView cardAttributeListView = (ListView) findViewById(R.id.cardAttributeListView);
        viewPager = (ViewPager) findViewById(R.id.cardImageViewPager);

        // get the current card
        Card card = deck.getCardList().get(currentCardIndex);

        ArrayList<Property> attrList = Util.buildAttrList(deck, card);
        // feed attribute list to array adapter
        ArrayAdapter<Property> attrListAdapter = new AttributeItemAdapter(
                this,
                R.layout.attr_list_item,
                attrList
        );
        cardAttributeListView.setAdapter(attrListAdapter);

        // update the image viewPager
        PagerAdapter pagerAdapter =
                new ImageSlidePagerAdapter(getSupportFragmentManager(), deck, card);

        viewPager.setAdapter(pagerAdapter);

        // set card name
        cardTitleTextView.setText(card.getName());

    }

    private class LoadDeckTask extends AsyncTask<Void, Void, Deck> {

        @Override
        protected Deck doInBackground(Void... voids) {
            JSONParser jsonParser = new JSONParser();
            return jsonParser.getDeck(ViewDeckActivity.this, chosenDeck);
        }

        @Override
        protected void onPostExecute(Deck deck) {
            ViewDeckActivity.this.deck = deck; // is this ok?
            updateView();
            spinner.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

}
