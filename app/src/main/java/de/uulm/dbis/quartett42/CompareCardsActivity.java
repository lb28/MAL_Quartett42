package de.uulm.dbis.quartett42;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import de.uulm.dbis.quartett42.data.Card;
import de.uulm.dbis.quartett42.data.Game;
import de.uulm.dbis.quartett42.data.Property;


public class CompareCardsActivity extends AppCompatActivity {
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_cards);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        loadGUI();

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
                        System.out.println("------------ SPEICHERE SPIEL AUF 1");
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt("runningGame", 1);
                        editor.apply();

                        CompareCardsActivity.super.onSupportNavigateUp();
                        setResult(RESULT_CANCELED);
                        finish();
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



    private void loadGUI() {
        // get all the Views
        View computerParent = findViewById(R.id.includeDeckComputer);
        View playerParent = findViewById(R.id.includeDeckPlayer);

        ViewPager viewPagerComputer =
                (ViewPager) computerParent.findViewById(R.id.cardImageViewPager);
        TextView cardNameTextViewComputer =
                (TextView) computerParent.findViewById(R.id.cardTitleTextView);
        ListView listViewComputer =
                (ListView) computerParent.findViewById(R.id.cardAttributeListView);

        ViewPager viewPagerPlayer =
                (ViewPager) playerParent.findViewById(R.id.cardImageViewPager);
        TextView cardNameTextViewPlayer =
                (TextView) playerParent.findViewById(R.id.cardTitleTextView);
        ListView listViewPlayer =
                (ListView) playerParent.findViewById(R.id.cardAttributeListView);


        // get all the stuff from the intent
        Intent intent = getIntent();

        String deckName = intent.getStringExtra("deckName");
        String cardNamePlayer = intent.getStringExtra("cardNamePlayer");
        String cardNameComputer = intent.getStringExtra("cardNameComputer");
        ArrayList<String> imgUriListPlayer = intent.getStringArrayListExtra("imgUriListPlayer");
        ArrayList<String> imgDescListPlayer = intent.getStringArrayListExtra("imgDescListPlayer");
        ArrayList<String> imgUriListComputer = intent.getStringArrayListExtra("imgUriListComputer");
        ArrayList<String> imgDescListComputer = intent.getStringArrayListExtra("imgDescListComputer");
        boolean maxWinner = intent.getBooleanExtra("maxWinner", false);
        String attrName = intent.getStringExtra("attrName");
        String attrUnit = intent.getStringExtra("attrUnit");
        double attrValuePlayer = intent.getDoubleExtra("attrValuePlayer", -1);
        double attrValueComputer = intent.getDoubleExtra("attrValueComputer", -1);
        int roundWinner = intent.getIntExtra("roundWinner", -1);


        // bring all the data in the right format
        Card playerCard = Util.buildCardFromRaw(
                imgUriListPlayer,
                imgDescListPlayer,
                attrName,
                attrValuePlayer,
                cardNamePlayer
        );
        Card computerCard = Util.buildCardFromRaw(
                imgUriListComputer,
                imgDescListComputer,
                attrName,
                attrValueComputer,
                cardNameComputer
        );

        ArrayList<Property> propPlayerAsList = new ArrayList<>();
        propPlayerAsList.add(new Property(attrName, attrUnit, maxWinner));
        ArrayList<Property> propListPlayer = Util.buildAttrList(
                propPlayerAsList, playerCard);

        ArrayList<Property> propComputerAsList = new ArrayList<>();
        propComputerAsList.add(new Property(attrName, attrUnit, maxWinner));
        ArrayList<Property> propListComputer = Util.buildAttrList(
                propComputerAsList, computerCard);


        //////////////////////////////
        // update all the GUI Elements

        // player card
        ArrayAdapter<Property> attrListPlayerAdapter = new AttributeItemAdapter(
                false,
                false,
                sharedPref.getBoolean("insaneModus", false),
                this,
                R.layout.attr_list_item,
                propListPlayer
        );
        listViewPlayer.setAdapter(attrListPlayerAdapter);
        cardNameTextViewPlayer.setText(cardNamePlayer);
        viewPagerPlayer.setAdapter(new ImageSlidePagerAdapter(
                getSupportFragmentManager(),
                playerCard.getImageList(),
                deckName
        ));


        // computer card
        ArrayAdapter<Property> attrListComputerAdapter = new AttributeItemAdapter(
                false,
                false,
                sharedPref.getBoolean("insaneModus", false),
                this,
                R.layout.attr_list_item,
                propListComputer
        );
        listViewComputer.setAdapter(attrListComputerAdapter);
        cardNameTextViewComputer.setText(cardNameComputer);
        viewPagerComputer.setAdapter(new ImageSlidePagerAdapter(
                getSupportFragmentManager(),
                computerCard.getImageList(),
                deckName
        ));

        // round winner
        TextView roundWinnerTextView = (TextView) findViewById(R.id.roundWinnerTextView);
        switch (roundWinner) {
            case Game.WINNER_PLAYER:
                roundWinnerTextView.setText("Du gewinnst!");
                roundWinnerTextView.setTextColor(Color.GREEN);
                break;
            case Game.WINNER_COMPUTER:
                roundWinnerTextView.setText("Gegner gewinnt...");
                roundWinnerTextView.setTextColor(Color.RED);
                break;
            case Game.WINNER_DRAW:
                roundWinnerTextView.setText("unentschieden");
                break;
        }


        // debug

    }

    /**
     * gets called on button click "continue"
     * @param view
     */
    public void continueGame(View view) {
        setResult(RESULT_OK);
        finish();
    }
}
