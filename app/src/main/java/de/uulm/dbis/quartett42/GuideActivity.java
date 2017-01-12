package de.uulm.dbis.quartett42;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class GuideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        Toast.makeText(getApplicationContext(), "TODO: Gute Anleitung schreiben", Toast.LENGTH_SHORT).show();
    }
}
