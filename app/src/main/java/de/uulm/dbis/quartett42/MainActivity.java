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
    ProgressBar spinner; //Spinner fuer Ladezeiten

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);

        //Testen der DefaultSharedPreferences:
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        //Value schreiben:
        Random random = new Random();
        //SharedPreferences.Editor editor = sharedPref.edit();
        //editor.putInt("testValue", random.nextInt(9));
        //editor.commit();

        //Value lesen:
        int testValue = sharedPref.getInt("testValue", -1);
        //Toast.makeText(getApplicationContext(), "Shared Preference Value: "+testValue, Toast.LENGTH_SHORT).show();



        //Start Assets laden:... kann spaeter in asynchrone Methode ausgelagert werden

        //Text:
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

        //Bild:
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

    @Override
    protected void onResume() {
        super.onResume();
        spinner.setVisibility(View.GONE);
    }

    //Methoden fuer Button-Klicks:
    public void clickGalleryButtonFunction(View view){
        //Toast.makeText(getApplicationContext(), "Galerie Button geklickt", Toast.LENGTH_SHORT).show();
        spinner.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, GalleryActivity.class);
        intent.putExtra("json_string", jsonString);
        startActivity(intent);
    }

}
