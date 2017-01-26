package de.uulm.dbis.quartett42;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class CreateDeckActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_deck);
    }

    @Override
    public boolean onSupportNavigateUp() {
        // TODO AlertDialog: "deck verwerfen / speichern / abbrechen"
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        onSupportNavigateUp();
    }


}
