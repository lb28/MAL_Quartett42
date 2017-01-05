package de.uulm.dbis.quartett42;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public String jsonString = "";
    SharedPreferences sharedPref;
    ProgressBar spinner; //Spinner fuer Ladezeiten

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);

        //Daten laden, runOnUiThread weil UI angepasst wird, keine Ahnung ob es was besseres gibt
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadData();
            }});
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Gucken, ob ein laufendes Spiel vorhanden:
        //runningGame Value lesen, 1 falls Spiel pausiert, 0 wenn nicht:
        Button newGameButton = (Button) findViewById(R.id.newGameButton);
        if(sharedPref.getInt("runningGame", 0) == 1){
            newGameButton.setText("SPIEL FORTSETZEN");
        }else{
            newGameButton.setText("NEUES SPIEL");
        }

        spinner.setVisibility(View.GONE);
    }

    //Methoden fuer Button-Klicks:

    //Galerie Button:
    public void clickGalleryButtonFunction(View view){
        //Toast.makeText(getApplicationContext(), "Galerie Button geklickt", Toast.LENGTH_SHORT).show();
        spinner.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, GalleryActivity.class);
        intent.putExtra("json_string", jsonString);
        startActivity(intent);
    }

    //Settings Button:
    public void clickSettingsButtonFunction(View view){
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    //Methoden der Activity:

    //Lade-Methode fuer Start-Daten und JSON-Datei:
    public void loadData(){
        //Test: Willkommen Text laden:
        TextView welcomeText = (TextView)findViewById(R.id.welcomeText);
        try {
            InputStream json = getAssets().open("welcometext.txt");
            BufferedReader in=
                    new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String welcomeMessage = "";
            String str = "";
            while ((str = in.readLine()) != null) {
                welcomeMessage = welcomeMessage+str;
            }
            welcomeText.setText(welcomeMessage);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //Test: Startbild laden:
        AssetManager assetManager = getAssets();
        InputStream is = null;
        ImageView welcomeImage = (ImageView)findViewById(R.id.welcomeImage);
        try {
            is = assetManager.open("startimage.gif");
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            welcomeImage.setImageBitmap(bitmap);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //JSON-String lesesn, damit er nicht jedes mal neu gelesen werden muss:
        try {
            InputStream in = getAssets().open("jsonexample.json");
            int size = in.available();
            byte[] buffer = new byte[size];
            in.read(buffer);
            in.close();
            jsonString = new String(buffer, "UTF-8");
            //System.out.println(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

}
