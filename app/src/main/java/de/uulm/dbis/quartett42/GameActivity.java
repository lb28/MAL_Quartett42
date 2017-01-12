package de.uulm.dbis.quartett42;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import de.uulm.dbis.quartett42.data.Game;
import de.uulm.dbis.quartett42.data.Property;

public class GameActivity extends AppCompatActivity {
    /**
     * The time (in milliseconds) the computer waits before selecting a card
     */
    public static final int COMPUTER_WAIT_MILLIS = 3000;
    String chosenDeck = "";
    Game game;

    SharedPreferences sharedPref;

    ProgressBar spinner; //Spinner fuer Ladezeiten

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        //JSON-String auslesen:
        Intent intent = getIntent();
        chosenDeck = intent.getStringExtra("chosen_deck");
        //System.out.println(jsonString);

        // use AsyncTask to load Deck from JSON
        new LoadDeckTask().execute();

    }

    @Override
    public boolean onSupportNavigateUp() {
        //TODO Dialog "wollen Sie das Spiel beenden?"
        return true;
    }

    @Override
    public void onBackPressed() {
        onSupportNavigateUp();
    }

    //Methoden der Activity:

    private class LoadDeckTask extends AsyncTask<Void, Void, Deck> {

        @Override
        protected Deck doInBackground(Void... voids) {
            // load deck with JSON parser
            JSONParser jsonParser = new JSONParser();
            return jsonParser.getDeck(GameActivity.this, chosenDeck);
        }

        @Override
        protected void onPostExecute(Deck deck) {
            // create a game
            makeGame(deck); // maybe also asynchronous?
           
            spinner.setVisibility(View.GONE);
            
            // call nextRound to start the game
            nextRound();
        }
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
            //TODO neues Game erstellen auf Basis der alten Daten
        }
    }

    /**
     * determines wether it is the player's or the computer's turn
     * and then updates the view accordingly
     */
    public void nextRound(){
        updateView();

        TextView instructionTextView = (TextView) findViewById(R.id.instructionTextView);
        ListView cardAttributeListView = (ListView) findViewById(R.id.cardAttributeListView);
        if (game.isNextPlayer()) {
            // let the user select an attribute for comparison
            cardAttributeListView.setEnabled(true);
            instructionTextView.setText("Wähle ein Attribut");
        } else {
            // disable the attribute list so the user cannot select an attribute
            cardAttributeListView.setEnabled(false);

            // update the view to show the computer is "thinking"
            instructionTextView.setText("Warte auf den Zug des Gegners...");

            // wait COMPUTER_WAIT_MILLIS milliseconds (async)
            // and then let the computer select an attribute
            new LoadDeckTask().execute();

        }

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

    public void updateView() {
        // get all the view elements
        TextView cardTitleTextView = (TextView) findViewById(R.id.cardTitleTextView);
        ListView cardAttributeListView = (ListView) findViewById(R.id.cardAttributeListView);
        ViewPager viewPager = (ViewPager) findViewById(R.id.cardImageViewPager);

        // get the current (the topmost) card of the player
        Card card = game.getDeck().getCardList().get(game.getCardsPlayer().get(0));

        ArrayList<Property> attributeList = Util.buildAttrList(game.getDeck(), card);
        // feed attribute list to array adapter
        ArrayAdapter<Property> attrListAdapter = new AttributeItemAdapter(
                this,
                R.layout.attr_list_item,
                attributeList
        );
        cardAttributeListView.setAdapter(attrListAdapter);

        // TODO set a click listener for the ListView which plays the cards based on the choice

        // update the image viewPager
        PagerAdapter pagerAdapter =
                new ImageSlidePagerAdapter(getSupportFragmentManager(), game.getDeck(), card);

        viewPager.setAdapter(pagerAdapter);

        // set card name
        cardTitleTextView.setText(card.getName());

    }

    private class WaitTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // first wait for the specified amount of time
                wait(COMPUTER_WAIT_MILLIS);

                // then let the computer choose a card
                String chosenAttribute = game.computerCardChoice();
                System.out.println(game.playCards(chosenAttribute));

                // TODO startActivityForResult(CompareCardsActivity)
                // ( first call startActivityForResult() --> continue to next round in onActivityResult() )
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }

}
