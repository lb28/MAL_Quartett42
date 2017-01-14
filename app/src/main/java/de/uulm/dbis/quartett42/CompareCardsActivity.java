package de.uulm.dbis.quartett42;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class CompareCardsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_cards);
    }

    public void continueGame(View view) {
        // TODO if gameOver --> send to GameOverActivity
        finish();
    }
}
