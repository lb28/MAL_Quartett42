package de.uulm.dbis.quartett42;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import de.uulm.dbis.quartett42.data.Deck;

public class HighscoreActivity extends AppCompatActivity implements GestureDetector.OnGestureListener{

    // the default string to display when there is no entry (both for name and points)
    private static final String DEFAULT_NAME_STRING = "";

    // the default points stored in sharedPreferences
    private static final int DEFAULT_POINTS_INT = -1;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    Context context;

    LinearLayout linearLayout;

    TextView ersterName, ersterPunkte, zweiterName, zweiterPunkte, dritterName, dritterPunkte;
    TextView vierterName, vierterPunkte, fuenfterName, fuenfterPunkte;

    Button rundenButton, punkteButton, zeitButton;

    String ersterNameRunden, zweiterNameRunden, dritterNameRunden, vierterNameRunden, fuenfterNameRunden;
    String ersterNamePunkte, zweiterNamePunkte, dritterNamePunkte, vierterNamePunkte, fuenfterNamePunkte;
    String ersterNameZeit, zweiterNameZeit, dritterNameZeit, vierterNameZeit, fuenfterNameZeit;

    int ersterPunkteRunden, zweiterPunkteRunden, dritterPunkteRunden, vierterPunkteRunden, fuenfterPunkteRunden;
    int ersterPunktePunkte, zweiterPunktePunkte, dritterPunktePunkte, vierterPunktePunkte, fuenfterPunktePunkte;
    int ersterPunkteZeit, zweiterPunkteZeit, dritterPunkteZeit, vierterPunkteZeit, fuenfterPunkteZeit;

    GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        punkteButton = (Button) findViewById(R.id.pointsButton);

        context = this;

        gestureDetector = new GestureDetector(HighscoreActivity.this, HighscoreActivity.this);

        linearLayout = (LinearLayout) findViewById(R.id.linearLayoutSwipe);

        updateGUI();

        // show rounds highscore first
        clickRoundsButtonHighscoreFunction(null);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent1, MotionEvent motionEvent2, float X, float Y) {

        if(motionEvent1.getY() - motionEvent2.getY() > 50){

            Toast.makeText(HighscoreActivity.this , " Swipe Up " , Toast.LENGTH_LONG).show();

            return true;
        }

        if(motionEvent2.getY() - motionEvent1.getY() > 50){

            Toast.makeText(HighscoreActivity.this , " Swipe Down " , Toast.LENGTH_LONG).show();

            return true;
        }

        if(motionEvent1.getX() - motionEvent2.getX() > 50){

            Toast.makeText(HighscoreActivity.this , " Swipe Left " , Toast.LENGTH_LONG).show();

            return true;
        }

        if(motionEvent2.getX() - motionEvent1.getX() > 50) {

            Toast.makeText(HighscoreActivity.this, " Swipe Right ", Toast.LENGTH_LONG).show();

            return true;
        }
        else {

            return true ;
        }
    }

    //Button Methoden
    public void clickPointsButtonHighscoreFunction(View view){

        punkteButton.setBackgroundColor(Color.GRAY);
        rundenButton.setBackgroundColor(Color.WHITE);
        zeitButton.setBackgroundColor(Color.WHITE);



        ersterName.setText(ersterNamePunkte);
        zweiterName.setText(zweiterNamePunkte);
        dritterName.setText(dritterNamePunkte);
        vierterName.setText(vierterNamePunkte);
        fuenfterName.setText(fuenfterNamePunkte);

        ersterPunkte.setText(ersterPunktePunkte == DEFAULT_POINTS_INT ?
                DEFAULT_NAME_STRING : "" + ersterPunktePunkte);
        zweiterPunkte.setText(zweiterPunktePunkte == DEFAULT_POINTS_INT ?
                DEFAULT_NAME_STRING : "" + zweiterPunktePunkte);
        dritterPunkte.setText(dritterPunktePunkte == DEFAULT_POINTS_INT ?
                DEFAULT_NAME_STRING : "" + dritterPunktePunkte);
        vierterPunkte.setText(vierterPunktePunkte == DEFAULT_POINTS_INT ?
                DEFAULT_NAME_STRING : "" + vierterPunktePunkte);
        fuenfterPunkte.setText(fuenfterPunktePunkte == DEFAULT_POINTS_INT ?
                DEFAULT_NAME_STRING : "" + fuenfterPunktePunkte);

    }

    public void clickRoundsButtonHighscoreFunction(View view){

        punkteButton.setBackgroundColor(Color.WHITE);
        rundenButton.setBackgroundColor(Color.GRAY);
        zeitButton.setBackgroundColor(Color.WHITE);

        ersterName.setText(ersterNameRunden);
        zweiterName.setText(zweiterNameRunden);
        dritterName.setText(dritterNameRunden);
        vierterName.setText(vierterNameRunden);
        fuenfterName.setText(fuenfterNameRunden);

        ersterPunkte.setText(ersterPunkteRunden == DEFAULT_POINTS_INT ?
                DEFAULT_NAME_STRING : "" + ersterPunkteRunden);
        zweiterPunkte.setText(zweiterPunkteRunden == DEFAULT_POINTS_INT ?
                DEFAULT_NAME_STRING : "" + zweiterPunkteRunden);
        dritterPunkte.setText(dritterPunkteRunden == DEFAULT_POINTS_INT ?
                DEFAULT_NAME_STRING : "" + dritterPunkteRunden);
        vierterPunkte.setText(vierterPunkteRunden == DEFAULT_POINTS_INT ?
                DEFAULT_NAME_STRING : "" + vierterPunkteRunden);
        fuenfterPunkte.setText(fuenfterPunkteRunden == DEFAULT_POINTS_INT ?
                DEFAULT_NAME_STRING : "" + fuenfterPunkteRunden);

    }

    public void clickTimeButtonHighscoreFunction(View view){

        punkteButton.setBackgroundColor(Color.WHITE);
        rundenButton.setBackgroundColor(Color.WHITE);
        zeitButton.setBackgroundColor(Color.GRAY);

        ersterName.setText(ersterNameZeit);
        zweiterName.setText(zweiterNameZeit);
        dritterName.setText(dritterNameZeit);
        vierterName.setText(vierterNameZeit);
        fuenfterName.setText(fuenfterNameZeit);

        ersterPunkte.setText(ersterPunkteZeit == DEFAULT_POINTS_INT ?
                DEFAULT_NAME_STRING : "" + ersterPunkteZeit);
        zweiterPunkte.setText(zweiterPunkteZeit == DEFAULT_POINTS_INT ?
                DEFAULT_NAME_STRING : "" + zweiterPunkteZeit);
        dritterPunkte.setText(dritterPunkteZeit == DEFAULT_POINTS_INT ?
                DEFAULT_NAME_STRING : "" + dritterPunkteZeit);
        vierterPunkte.setText(vierterPunkteZeit == DEFAULT_POINTS_INT ?
                DEFAULT_NAME_STRING : "" + vierterPunkteZeit);
        fuenfterPunkte.setText(fuenfterPunkteZeit == DEFAULT_POINTS_INT ?
                DEFAULT_NAME_STRING : "" + fuenfterPunkteZeit);

    }

    public void resetHighscoresFunction(View view){

        editor = sharedPref.edit();

        editor.putInt("ersterPunktePunkte", DEFAULT_POINTS_INT);
        editor.putInt("zweiterPunktePunkte", DEFAULT_POINTS_INT);
        editor.putInt("dritterPunktePunkte", DEFAULT_POINTS_INT);
        editor.putInt("vierterPunktePunkte", DEFAULT_POINTS_INT);
        editor.putInt("fuenfterPunktePunkte", DEFAULT_POINTS_INT);

        editor.putString("ersterNamePunkte", DEFAULT_NAME_STRING);
        editor.putString("zweiterNamePunkte", DEFAULT_NAME_STRING);
        editor.putString("dritterNamePunkte", DEFAULT_NAME_STRING);
        editor.putString("vierterNamePunkte", DEFAULT_NAME_STRING);
        editor.putString("fuenfterNamePunkte", DEFAULT_NAME_STRING);

        editor.putInt("ersterPunkteRunden", DEFAULT_POINTS_INT);
        editor.putInt("zweiterPunkteRunden", DEFAULT_POINTS_INT);
        editor.putInt("dritterPunkteRunden", DEFAULT_POINTS_INT);
        editor.putInt("vierterPunkteRunden", DEFAULT_POINTS_INT);
        editor.putInt("fuenfterPunkteRunden", DEFAULT_POINTS_INT);

        editor.putString("ersterNameRunden", DEFAULT_NAME_STRING);
        editor.putString("zweiterNameRunden", DEFAULT_NAME_STRING);
        editor.putString("dritterNameRunden", DEFAULT_NAME_STRING);
        editor.putString("vierterNameRunden", DEFAULT_NAME_STRING);
        editor.putString("fuenfterNameRunden", DEFAULT_NAME_STRING);

        editor.putInt("ersterPunkteZeit", DEFAULT_POINTS_INT);
        editor.putInt("zweiterPunkteZeit", DEFAULT_POINTS_INT);
        editor.putInt("dritterPunkteZeit", DEFAULT_POINTS_INT);
        editor.putInt("vierterPunkteZeit", DEFAULT_POINTS_INT);
        editor.putInt("fuenfterPunkteZeit", DEFAULT_POINTS_INT);

        editor.putString("ersterNameZeit", DEFAULT_NAME_STRING);
        editor.putString("zweiterNameZeit", DEFAULT_NAME_STRING);
        editor.putString("dritterNameZeit", DEFAULT_NAME_STRING);
        editor.putString("vierterNameZeit", DEFAULT_NAME_STRING);
        editor.putString("fuenfterNameZeit", DEFAULT_NAME_STRING);

        editor.apply();

        // restart the activity
        updateGUI();
        finish();
    }

    private void updateGUI() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);


        ersterName = (TextView) findViewById(R.id.NamePosEins);
        zweiterName = (TextView) findViewById(R.id.NamePosZwei);
        dritterName = (TextView) findViewById(R.id.NamePosDrei);
        vierterName = (TextView) findViewById(R.id.NamePosVier);
        fuenfterName = (TextView) findViewById(R.id.NamePosFuenf);

        ersterPunkte = (TextView) findViewById(R.id.PunktePosEins);
        zweiterPunkte = (TextView) findViewById(R.id.PunktePosZwei);
        dritterPunkte = (TextView) findViewById(R.id.PunktePosDrei);
        vierterPunkte = (TextView) findViewById(R.id.PunktePosVier);
        fuenfterPunkte = (TextView) findViewById(R.id.PunktePosFuenf);

        rundenButton = (Button) findViewById(R.id.roundsButton);
        punkteButton = (Button) findViewById(R.id.pointsButton);
        zeitButton = (Button) findViewById(R.id.timeButton);

        ersterNameRunden = sharedPref.getString("ersterNameRunden", DEFAULT_NAME_STRING);
        zweiterNameRunden = sharedPref.getString("zweiterNameRunden", DEFAULT_NAME_STRING);
        dritterNameRunden = sharedPref.getString("dritterNameRunden", DEFAULT_NAME_STRING);
        vierterNameRunden = sharedPref.getString("vierterNameRunden", DEFAULT_NAME_STRING);
        fuenfterNameRunden = sharedPref.getString("fuenfterNameRunden", DEFAULT_NAME_STRING);
        ersterNamePunkte = sharedPref.getString("ersterNamePunkte", DEFAULT_NAME_STRING);
        zweiterNamePunkte = sharedPref.getString("zweiterNamePunkte", DEFAULT_NAME_STRING);
        dritterNamePunkte = sharedPref.getString("dritterNamePunkte", DEFAULT_NAME_STRING);
        vierterNamePunkte = sharedPref.getString("vierterNamePunkte", DEFAULT_NAME_STRING);
        fuenfterNamePunkte = sharedPref.getString("fuenfterNamePunkte", DEFAULT_NAME_STRING);
        ersterNameZeit = sharedPref.getString("ersterNameZeit", DEFAULT_NAME_STRING);
        zweiterNameZeit = sharedPref.getString("zweiterNameZeit", DEFAULT_NAME_STRING);
        dritterNameZeit = sharedPref.getString("dritterNameZeit", DEFAULT_NAME_STRING);
        vierterNameZeit = sharedPref.getString("vierterNameZeit", DEFAULT_NAME_STRING);
        fuenfterNameZeit = sharedPref.getString("fuenfterNameZeit", DEFAULT_NAME_STRING);

        ersterPunkteRunden = sharedPref.getInt("ersterPunkteRunden", DEFAULT_POINTS_INT);
        zweiterPunkteRunden = sharedPref.getInt("zweiterPunkteRunden", DEFAULT_POINTS_INT);
        dritterPunkteRunden = sharedPref.getInt("dritterPunkteRunden", DEFAULT_POINTS_INT);
        vierterPunkteRunden = sharedPref.getInt("vierterPunkteRunden", DEFAULT_POINTS_INT);
        fuenfterPunkteRunden = sharedPref.getInt("fuenfterPunkteRunden", DEFAULT_POINTS_INT);
        ersterPunktePunkte = sharedPref.getInt("ersterPunktePunkte", DEFAULT_POINTS_INT);
        zweiterPunktePunkte = sharedPref.getInt("zweiterPunktePunkte", DEFAULT_POINTS_INT);
        dritterPunktePunkte = sharedPref.getInt("dritterPunktePunkte", DEFAULT_POINTS_INT);
        vierterPunktePunkte = sharedPref.getInt("vierterPunktePunkte", DEFAULT_POINTS_INT);
        fuenfterPunktePunkte = sharedPref.getInt("fuenfterPunktePunkte", DEFAULT_POINTS_INT);
        ersterPunkteZeit = sharedPref.getInt("ersterPunkteZeit", DEFAULT_POINTS_INT);
        zweiterPunkteZeit = sharedPref.getInt("zweiterPunkteZeit", DEFAULT_POINTS_INT);
        dritterPunkteZeit = sharedPref.getInt("dritterPunkteZeit", DEFAULT_POINTS_INT);
        vierterPunkteZeit = sharedPref.getInt("vierterPunkteZeit", DEFAULT_POINTS_INT);
        fuenfterPunkteZeit = sharedPref.getInt("fuenfterPunkteZeit", DEFAULT_POINTS_INT);
    }

    /*
    public void clickUploadButtonFunction(View view){

        final ServerUploadJSONHandler suh = new ServerUploadJSONHandler(this);

        new Thread(new Runnable() {
            public void run() {
                // TODO make AsynchTask calling LocalJSONHandler.getDecks()
                boolean uploadSuccessful = suh.uploadDeck("TestDeckHochladen", Deck.SRC_MODE_ASSETS);

                if (uploadSuccessful == false){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Upload erfolgreich", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Upload fehlgeschlagen", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

    }
    */


}

class OnSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;

    public OnSwipeTouchListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public void onSwipeLeft() {
    }

    public void onSwipeRight() {
    }

    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_DISTANCE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceX > 0)
                    onSwipeRight();
                else
                    onSwipeLeft();
                return true;
            }
            return false;
        }
    }
}