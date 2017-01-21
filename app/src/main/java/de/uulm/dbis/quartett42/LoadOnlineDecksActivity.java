package de.uulm.dbis.quartett42;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import java.util.ArrayList;

import de.uulm.dbis.quartett42.data.Deck;


public class LoadOnlineDecksActivity extends AppCompatActivity {

    // https://dhc.restlet.com/ for testing

    ArrayList<Deck> deckList;

    GridView gridView;
    GridViewAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_online_decks);

        deckList = new ArrayList<Deck>();


        // TODO make all this AsynchTask
        new Thread(new Runnable() {
            public void run() {
                ServerJSONHandler jsonHandler = new ServerJSONHandler();
                deckList = jsonHandler.getDecksOverview();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run(){
                        makeGridView(deckList);
                    }
                });
            }
        }).start();



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
