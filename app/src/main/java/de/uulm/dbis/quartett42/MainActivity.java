package de.uulm.dbis.quartett42;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public final static int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 42;

    public String jsonString = "";
    SharedPreferences sharedPref;
    ContentLoadingProgressBar spinner; //Spinner fuer Ladezeiten

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        spinner = (ContentLoadingProgressBar) findViewById(R.id.progressBar1);
        spinner.bringToFront();
        spinner.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                makeNotification();
            }
        }).start();

    }

    //add menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        spinner.hide();
    }

    //Methoden fuer Button-Klicks:

    //Galerie Button:
    public void clickGalleryButtonFunction(View view){
        spinner.show();
        Intent intent = new Intent(this, GalleryActivity.class);
        intent.putExtra("json_string", jsonString);
        startActivity(intent);
    }

    //Settings Button:
    public void clickSettingsButtonFunction(View view){
        spinner.show();
        Intent intent = new Intent(this, SettingActivity.class);
        intent.putExtra("setting_source", "main");
        startActivity(intent);
    }

    //NewGame Button:
    public void clickNewGameButtonFunction(View view){
        //Gucken, ob ein laufendes Spiel vorhanden:
        //runningGame Value lesen, 1 falls Spiel pausiert, 0 wenn nicht:
        if(sharedPref.getInt("runningGame", 0) == 1){
            //Fragen, ob vorhandenes Spiel fortgesetzt werden soll oder neues begonnen werden soll
            String[] aktionen = {"Ja, Spiel fortsetzen", "Nein, Neues Spiel starten"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Soll ein pausiertes Spiel fortgesetzt werden?")
                    .setItems(aktionen, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position of the selected item
                            if(which == 0){
                                // vorhandenes Spiel laden:
                                loadGame();
                            }else if(which == 1){
                                //alte gespeichertes Spiel loeschen
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putInt("runningGame", 0);
                                editor.putInt("currentRoundsLeft", 0);
                                editor.putInt("currentPointsPlayer", 0);
                                editor.putInt("currentPointsComputer", 0);
                                editor.putString("currentCardsPlayer", "");
                                editor.putString("currentCardsComputer", "");
                                editor.commit();
                                //Zu NewGameActivity weiter leiten
                                startNewGame();
                            }
                        }
                    });
            AlertDialog alert =  builder.create();
            alert.show();
        }else{
            //Zu NewGameActivity weiter leiten
            startNewGame();
        }

    }

    //Statistics Button:
    public void clickStatisticsButtonFunction(View view){
        spinner.show();
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }

    //Highscore Button:
    public void clickHighscoreButtonFunction(View view){
        spinner.show();
        Intent intent = new Intent(this, HighscoreActivity.class);
        startActivity(intent);
    }


    //Menue-Items:
    public void clickGuideFunction(MenuItem item){
        if(item.getTitle().equals("Anleitung")){
            spinner.show();
            Intent intent = new Intent(this, GuideActivity.class);
            startActivity(intent);
        }else if(item.getTitle().equals("Info")){
            spinner.show();
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
        }
    }

    //Methoden der Activity:

    //Neues Spiel starten:
    public void startNewGame(){
        spinner.show();
        Intent intent = new Intent(this, NewGameActivity.class);
        intent.putExtra("new_game_source", "main_activity");
        startActivity(intent);
    }

    public void loadGame(){
        spinner.show();
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("chosen_deck", sharedPref.getString("currentChosenDeck", "Sesamstrasse"));
        intent.putExtra("srcMode", sharedPref.getInt("srcMode", -1));
        startActivity(intent);
    }

    //Makes a notification if new games are uploaded into the online store
    public void makeNotification(){
        try{
            ServerJSONHandler serverJsonHandler = new ServerJSONHandler(this);
            final int availableDecks = serverJsonHandler.getDecksOverview(true).size();
            if(availableDecks > sharedPref.getInt("new_online_decks", 0)){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int newDecks = availableDecks - sharedPref.getInt("new_online_decks", 0);

                        Intent notificationIntent = new Intent(MainActivity.this,
                                LoadOnlineDecksActivity.class);
                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        PendingIntent intent = PendingIntent.getActivity(MainActivity.this, 0,
                                notificationIntent, 0);

                        NotificationManager notif=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                        Notification notify=new Notification.Builder
                                (getApplicationContext()).setContentTitle("Quartett42").setContentText("Es " +
                                "sind "+newDecks+" neue Decks im Store erhältlich").
                                setContentTitle("Quartett42").setSmallIcon(R.drawable.menu_image_cut)
                                .setContentIntent(intent)
                                .build();


                        notify.flags |= Notification.FLAG_AUTO_CANCEL;
                        notif.notify(0, notify);

                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt("new_online_decks", availableDecks);
                        editor.apply();
                    }
                });
            }else if(availableDecks < sharedPref.getInt("new_online_decks", 0)){
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("new_online_decks", availableDecks);
                editor.apply();
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
