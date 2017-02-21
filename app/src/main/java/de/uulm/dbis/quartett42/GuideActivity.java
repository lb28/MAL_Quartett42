package de.uulm.dbis.quartett42;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class GuideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        TextView guideTextView = (TextView) findViewById(R.id.guideTextView);
        guideTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
