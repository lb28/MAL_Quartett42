package de.uulm.dbis.quartett42;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import de.uulm.dbis.quartett42.data.Card;
import de.uulm.dbis.quartett42.data.Deck;
import de.uulm.dbis.quartett42.data.Game;
import de.uulm.dbis.quartett42.data.ImageCard;
import de.uulm.dbis.quartett42.data.Property;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";

    /**
     * The time (in milliseconds) the computer waits before selecting a card
     */
    public static final int COMPUTER_WAIT_MILLIS = 3000;
    public static final int ROUND_WAIT_MILLIS = 30000; //30 Sekunden pro Runde??

    /**
     * request code for calling the CompareCardsActivity
     */
    private static final int REQUEST_COMPARE_CARDS = 1;

    String chosenDeck = "";
    Game game;

    SharedPreferences sharedPref;

    ProgressBar spinner; //Spinner fuer Ladezeiten

    TextView textViewRoundsRemaining;
    TextView textViewRoundTime;
    CountDownTimer countDownTimer;
    CountDownTimer roundTimer;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);
        findViewById(R.id.progressBarWait).setVisibility(View.GONE);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        //JSON-String auslesen:
        Intent intent = getIntent();
        chosenDeck = intent.getStringExtra("chosen_deck");
        //System.out.println(jsonString);

        textViewRoundsRemaining = (TextView) findViewById(R.id.textViewRoundsRemaining);
        textViewRoundTime = (TextView) findViewById(R.id.textViewRoundTimeLeft);

        mp = MediaPlayer.create(this, R.raw.timeout);

        // use AsyncTask to load Deck from JSON
        new LoadDeckTask().execute();

    }

    @Override
    public void onResume(){
        super.onResume();
        textViewRoundTime.setText("");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        if(game.getMode() == 2){
            countDownTimer.cancel();

            try{
                roundTimer.cancel();
            }catch(Exception e){
                //Falls Computer an der Reihe...
            }
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("currentChosenDeck", chosenDeck);
        editor.putInt("currentRoundsLeft", game.getRoundsLeft());
        editor.putInt("currentPointsPlayer", game.getPointsPlayer());
        editor.putInt("currentPointsComputer", game.getPointsComputer());
        editor.putBoolean("currentNextPlayer", game.isNextPlayer());
        StringBuilder sb1 = new StringBuilder();
        for (int i = 0; i < game.getCardsPlayer().size(); i++) {
            sb1.append(game.getCardsPlayer().get(i)).append(";");
        }
        editor.putString("currentCardsPlayer", sb1.toString());
        StringBuilder sb2 = new StringBuilder();
        for (int i = 0; i < game.getCardsComputer().size(); i++) {
            sb2.append(game.getCardsComputer().get(i)).append(";");
        }
        editor.putString("currentCardsComputer", sb2.toString());
        editor.commit();
    }


    @Override
    public boolean onSupportNavigateUp() {
        // Dialog "wollen Sie das Spiel beenden?"
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Spiel beenden")
                .setMessage("Spiel beenden und Spielstand speichern?")
                .setPositiveButton("Ja", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Alle aktuellen Daten fuer ein Spiel in SharedPreferences schreiben:
                        //in on Destroy-Methode, diese fängt auch das Schliessen der App durch
                        //Activity Manager von Android auf.

                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt("runningGame", 1);
                        editor.apply();

                        GameActivity.super.onSupportNavigateUp();
                    }

                })
                .setNegativeButton("Nein", null)
                .show();
        return false;
    }

    @Override
    public void onBackPressed() {
        onSupportNavigateUp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_COMPARE_CARDS && resultCode == RESULT_OK) {
            if (game.getGameOver()) {
                // the game is over
                endGame();

            } else {
                // the game is still going on
                nextRound();
            }
        } else if(requestCode == REQUEST_COMPARE_CARDS && resultCode == RESULT_CANCELED){
            //Save Game Data and finish
            GameActivity.super.onSupportNavigateUp();
        }else {
            Log.e(TAG, "onActivityResult: wrong requestCode / resultCode");
        }
    }


    ///////////////////////////
    // Methoden der Activity://
    ///////////////////////////

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
            makeGame(deck); // maybe also asynchronous

            //Falls im Zeitmodus, dann CountdownTimer mit der Zeit starten:
            if(game.getMode() == 2){
                countDownTimer = new CountDownTimer(game.getRoundsLeft(), 1000) {

                    public void onTick(long millisUntilFinished) {
                        //Zeit runter rechnen jede Sekunde:
                        game.setRoundsLeft(game.getRoundsLeft()-1000);
                        int seconds = (int) (game.getRoundsLeft() / 1000) % 60 ;
                        int minutes = (int) ((game.getRoundsLeft() / (1000*60)) % 60);
                        String sSeconds ="";
                        if(seconds < 10){
                            sSeconds = "0"+seconds;
                        }else{
                            sSeconds = ""+seconds;
                        }
                        String sMinutes ="";
                        if(minutes < 10){
                            sMinutes = "0"+minutes;
                        }else{
                            sMinutes = ""+minutes;
                        }
                        String timeLeft = sMinutes+":"+sSeconds+" übrig";
                        textViewRoundsRemaining.setText(timeLeft);
                    }

                    public void onFinish() {
                        //Spiel beenden:
                        endGame();
                    }
                };

                countDownTimer.start();
            }

           
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
        if(mode == 2){
            roundsLeft = roundsLeft*60000;
        }


        //Andere Behandlung je nachdem ob ein neues Spiel gespielt wird oder ein altes geladen wird:
        if(sharedPref.getInt("runningGame", 0) == 0){
            game = new Game(newDeck, difficulty, mode, insaneModus, roundsLeft);

        }else{
            int currentRoundsLeft = sharedPref.getInt("currentRoundsLeft", 10);
            int currentPointsPlayer = sharedPref.getInt("currentPointsPlayer", 0);
            int currentPointsComputer = sharedPref.getInt("currentPointsComputer", 0);
            boolean currentNextPlayer = sharedPref.getBoolean("currentNextPlayer", true);
            String[] playerCardsString = sharedPref.getString("currentCardsPlayer", "").split(";");
            ArrayList<Integer> currentCardsPlayer = new ArrayList<Integer>();
            for(int s = 0; s < playerCardsString.length; s++){
                currentCardsPlayer.add(Integer.parseInt(playerCardsString[s]));
            }
            String[] computerCardsString = sharedPref.getString("currentCardsComputer", "").split(";");
            ArrayList<Integer> currentCardsComputer = new ArrayList<Integer>();
            for(int s = 0; s < computerCardsString.length; s++){
                currentCardsComputer.add(Integer.parseInt(computerCardsString[s]));
            }

            game = new Game(newDeck, difficulty, mode, insaneModus, currentRoundsLeft,
                    currentPointsPlayer, currentPointsComputer, currentNextPlayer,
                    currentCardsPlayer, currentCardsComputer);
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
            instructionTextView.setText("Wähle ein Attribut");

            //start Extra Round Timer if time mode:
            if(game.getMode() == 2){
                roundTimer = new CountDownTimer(ROUND_WAIT_MILLIS, 1000) {

                    int roundLeftTime = ROUND_WAIT_MILLIS;

                    public void onTick(long millisUntilFinished) {
                        //Zeit runter rechnen jede Sekunde:
                        roundLeftTime = roundLeftTime -1000;
                        int seconds = (int) (roundLeftTime / 1000) % 60 ;
                        int minutes = (int) (roundLeftTime / (1000*60) % 60);
                        String sSeconds ="";
                        if(seconds < 10){
                            sSeconds = "0"+seconds;
                        }else{
                            sSeconds = ""+seconds;
                        }
                        String sMinutes ="";
                        if(minutes < 10){
                            sMinutes = "0"+minutes;
                        }else{
                            sMinutes = ""+minutes;
                        }
                        String timeLeft = "Rundenlimit: "+sMinutes+":"+sSeconds;
                        textViewRoundTime.setText(timeLeft);

                        //Last 10 Seconds get an extra Sound
                        if(seconds <= 10){
                            mp.start();
                        }
                    }

                    public void onFinish() {
                        //If time finished, let the computer choose an anntibute
                        // then let the computer choose a card
                        String chosenAttribute = game.computerCardChoice();
                        playCardsGUI(chosenAttribute);
                    }
                };

                roundTimer.start();
            }

        } else {
            // update the view to show the computer is "thinking"
            instructionTextView.setText("Warte auf den Zug des Gegners...");
            findViewById(R.id.progressBarWait).setVisibility(View.VISIBLE);

            // wait COMPUTER_WAIT_MILLIS milliseconds (async)
            // and then let the computer select an attribute
            new ComputerChoiceTask().execute();

        }

    }

    /**
     * updates the view based on the current status of 'game'
     */
    public void updateView() {
        // get all the view elements
        TextView textViewGameScore = (TextView) findViewById(R.id.textViewGameScore);
        ViewPager viewPager = (ViewPager) findViewById(R.id.cardImageViewPager);
        TextView cardTitleTextView = (TextView) findViewById(R.id.cardTitleTextView);
        ListView cardAttributeListView = (ListView) findViewById(R.id.cardAttributeListView);

        // Text Bsp: "(Du) 23 : 41 (Gegner)"
        if(game.getMode() == 3){
            textViewGameScore.setText("(Du) " + game.getPointsPlayer() + " : " + game.getPointsComputer() + " (Gegner)");
        }else{
            textViewGameScore.setText("(Du) " + game.getCardsPlayer().size() + " : " + game.getCardsComputer().size() + " (Gegner)");
        }

        // Text Bsp: "7 Runden übrig"
        String roundsRemainingString = "";
        switch (game.getMode()) {
            case 1:
                roundsRemainingString += game.getRoundsLeft() + " Runden übrig";
                break;
            case 2:
                //roundsRemainingString += "?Minuten?";
                break;
            case 3:
                roundsRemainingString += game.getRoundsLeft() + " Punkte übrig";
                break;
        }
        textViewRoundsRemaining.setText(roundsRemainingString);

        // get the current (the topmost) card of the player
        Card card = game.getDeck().getCardList().get(game.getCardsPlayer().get(0));

        ArrayList<Property> attributeList = Util.buildAttrList(game.getDeck().getPropertyList(), card);
        // feed attribute list to array adapter
        ArrayAdapter<Property> attrListAdapter = new AttributeItemAdapter(
                game.isNextPlayer(), // make list clickable only if it is player's turn
                sharedPref.getBoolean("expertModus", false),
                sharedPref.getBoolean("insaneModus", false),
                this,
                R.layout.attr_list_item,
                attributeList
        );
        cardAttributeListView.setAdapter(attrListAdapter);

        // set a click listener for the ListView which plays the cards based on the choice
        cardAttributeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int clickedPosition, long id) {
                ArrayList<Property> properties = game.getDeck().getPropertyList();

                // int clickedPosition contains the clicked list element (starting at 0)
                String chosenAttribute = properties.get(clickedPosition).getName();

                // play the cards
                playCardsGUI(chosenAttribute);
            }
        });

        // update the image viewPager
        PagerAdapter pagerAdapter =
                new ImageSlidePagerAdapter(
                        getSupportFragmentManager(), card.getImageList(), game.getDeck().getName());

        viewPager.setAdapter(pagerAdapter);

        // set card name
        cardTitleTextView.setText(card.getName());

    }

    private class ComputerChoiceTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // first wait for the specified amount of time
                Thread.sleep(COMPUTER_WAIT_MILLIS);

                // then let the computer choose a card
                String chosenAttribute = game.computerCardChoice();
                playCardsGUI(chosenAttribute);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            findViewById(R.id.progressBarWait).setVisibility(View.GONE);
        }
    }

    /**
     * (method name sucks... any idea?)
     * calls game.playCards() and makes the redirect to CompareCardsActivity.
     * Basically acts as the GUI counterpart to game.playCards()
     * @param chosenAttribute the name of the chosen property/attribute
     */
    private void playCardsGUI(String chosenAttribute) {
        if(game.getMode() == 2){
            try{
                roundTimer.cancel();
            }catch(Exception e){
                //Falls Computer an der Reihe...
            }
        }

        Intent intent = new Intent(this, CompareCardsActivity.class);

        Property chosenProp = null;
        // attribute name, maxwinner, unit and both values
        for (Property p : game.getDeck().getPropertyList()) {
            if (p.getName().equals(chosenAttribute)) {
                chosenProp = p;
                break;
            }
        }

        // get both cards BEFORE they are played
        Card cardPlayer = game.returnCardOfID(game.getCardsPlayer().get(0));
        Card cardComputer = game.returnCardOfID(game.getCardsComputer().get(0));

        // play the cards (i.e. update the model)
        int roundWinner = game.playCards(chosenAttribute);

        // get image URIs and descriptions of both cards and put them into the intent as lists
        ArrayList<String> imgUriListPlayer = new ArrayList<String>();
        ArrayList<String> imgDescListPlayer = new ArrayList<String>();
        for (ImageCard imgCard : cardPlayer.getImageList()) {
            imgUriListPlayer.add(imgCard.getUri());
            imgDescListPlayer.add(imgCard.getDescription());
        }

        ArrayList<String> imgUriListComputer = new ArrayList<String>();
        ArrayList<String> imgDescListComputer = new ArrayList<String>();
        for (ImageCard imgCard : cardComputer.getImageList()) {
            imgUriListComputer.add(imgCard.getUri());
            imgDescListComputer.add(imgCard.getDescription());
        }

        // put all the stuff we need into the intent:
        intent.putExtra("deckName", game.getDeck().getName());
        intent.putExtra("cardNamePlayer", cardPlayer.getName());
        intent.putExtra("cardNameComputer", cardComputer.getName());
        intent.putStringArrayListExtra("imgUriListPlayer", imgUriListPlayer);
        intent.putStringArrayListExtra("imgDescListPlayer", imgDescListPlayer);
        intent.putStringArrayListExtra("imgUriListComputer", imgUriListComputer);
        intent.putStringArrayListExtra("imgDescListComputer", imgDescListComputer);
        intent.putExtra("maxWinner", chosenProp.isMaxWinner());
        intent.putExtra("attrName", chosenProp.getName());
        intent.putExtra("attrUnit", chosenProp.getUnit());
        intent.putExtra("attrValuePlayer",
                cardPlayer.getAttributeMap().get(chosenAttribute).doubleValue());
        intent.putExtra("attrValueComputer",
                cardComputer.getAttributeMap().get(chosenAttribute).doubleValue());
        intent.putExtra("roundWinner", roundWinner);

        startActivityForResult(intent, REQUEST_COMPARE_CARDS);
    }

    public void endGame(){
        try{
            if(game.getMode() == 2){
                countDownTimer.cancel();
            }
        }catch(Exception e){
            //Countdown schon abgelaufen
        }

        //alte gespeichertes Spiel loeschen
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("runningGame", 0);
        editor.apply();


        Intent intent = new Intent(this, GameEndActivity.class);
        intent.putExtra("pointsPlayer", game.getPointsPlayer());
        intent.putExtra("pointsComputer", game.getPointsComputer());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
