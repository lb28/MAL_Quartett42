package de.uulm.dbis.quartett42;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import de.uulm.dbis.quartett42.data.Card;
import de.uulm.dbis.quartett42.data.Deck;
import de.uulm.dbis.quartett42.data.ImageCard;
import de.uulm.dbis.quartett42.data.Property;

public class ViewDeckActivity extends AppCompatActivity {
    String jsonString = "";
    String chosenDeck = "";
    int currentCardIndex = 0;
    Deck deck = null;

    ProgressBar spinner; //Spinner fuer Ladezeiten
    ViewPager viewPager;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_deck);

        viewPager = (ViewPager) findViewById(R.id.cardImageViewPager);

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

    public void updateView() {
        // get all the view elements
        ImageView cardImageView = (ImageView) findViewById(R.id.cardImageView);
        TextView cardTitleTextView = (TextView) findViewById(R.id.cardTitleTextView);
        ListView cardAttributeListView = (ListView) findViewById(R.id.cardAttributeListView);

        // get the current card
        Card card = deck.getCardList().get(currentCardIndex);

        ArrayList<Property> attrList = buildAttrList(card);

        // update the image viewPager
        pagerAdapter = new ImageSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

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

    /**
     * A simple pager adapter that shows images in sequence
     */
    private class ImageSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ImageSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            // create card image fragment
            CardImageFragment cardimageFragment = new CardImageFragment();

            // get the currently selected ImageCard from the currently selected card
            ImageCard currentImageCard = deck.getCardList().get(currentCardIndex)
                    .getImageList().get(position);

            // Attach some data to it that we'll
            // use to populate our fragment layouts
            Bundle args = new Bundle();

            // Set the arguments on the fragment
            // that will be fetched in DemoFragment@onCreateView
            String imageUri = deck.getName()+"/"+currentImageCard.getUri();
            args.putString("imageUri", imageUri);
            args.putString("imageDesc", currentImageCard.getDescription());
            cardimageFragment.setArguments(args);

            return cardimageFragment;
        }

        @Override
        public int getCount() {
            // the size of the selected card's image list
            return deck.getCardList().get(currentCardIndex).getImageList().size();
        }
    }

}
