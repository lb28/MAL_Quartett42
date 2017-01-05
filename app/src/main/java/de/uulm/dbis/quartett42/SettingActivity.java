package de.uulm.dbis.quartett42;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {
    SharedPreferences sharedPref;

    //Alle Buttons:
    RadioGroup buttonGroup;
    RadioButton rundenButton;
    RadioButton zeitButton;
    RadioButton punkteButton;
    TextView anzahlTextView;
    EditText anzahlEdit;
    SeekBar schwierigkeitsPicker;
    Switch insaneSwitch;
    Switch soundSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        //Falls gerade ein Spiel am laufen ist, duerfen die Einstellungen nicht geaendert werden
        if(sharedPref.getInt("runningGame", 0) == 1){
            Toast.makeText(getApplicationContext(), "Einstellungen aendern nicht moeglich waehrend eines laufenden 'Spiels!", Toast.LENGTH_LONG).show();
            finish();
        }else{
            //alle UI-Elemente suchen:
            buttonGroup = (RadioGroup)findViewById(R.id.varianteGroup);
            rundenButton = (RadioButton)findViewById(R.id.rundenRadioButton);
            zeitButton = (RadioButton)findViewById(R.id.zeitRadioButton);
            punkteButton = (RadioButton)findViewById(R.id.punkteRadioButton);
            anzahlTextView = (TextView)findViewById(R.id.anzahlTextView);
            anzahlEdit = (EditText)findViewById(R.id.anzahlEditText);
            schwierigkeitsPicker = (SeekBar)findViewById(R.id.schwierigkeitsgradPicker);
            insaneSwitch = (Switch)findViewById(R.id.insaneSwitch);
            soundSwitch = (Switch)findViewById(R.id.soundSwitch);

            //Alle Werte aus DefaultSharedPreferences lesen und UI setzen:
            int mode = sharedPref.getInt("mode", 1);
            if(mode == 1){
                rundenButton.setChecked(true);
                anzahlTextView.setText("Anzahl Runden:");
            }else if(mode == 2){
                zeitButton.setChecked(true);
                anzahlTextView.setText("Spielminuten:");
            }else{
                punkteButton.setChecked(true);
                anzahlTextView.setText("Punktelimit:");
            }

            anzahlEdit.setText(""+sharedPref.getInt("roundsLeft", 10));
            schwierigkeitsPicker.setProgress(sharedPref.getInt("difficulty", 2)-1);
            insaneSwitch.setChecked(sharedPref.getBoolean("insaneModus", false));
            soundSwitch.setChecked(sharedPref.getBoolean("soundModus", true));

            buttonGroup.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        }

    }


    //Neue Werte speichern bei Button-Klick:
    public void saveButtonFunction(View view){
        SharedPreferences.Editor editor = sharedPref.edit();

        if(rundenButton.isChecked()){
            editor.putInt("mode", 1);
        }else if(zeitButton.isChecked()){
            editor.putInt("mode", 2);
        }else{
            editor.putInt("mode", 3);
        }
        try{
            int tmpValue = Integer.parseInt(anzahlEdit.getText().toString());
            if(tmpValue > 0 && tmpValue < 9999){
                editor.putInt("roundsLeft", tmpValue);
            }else{
                editor.putInt("roundsLeft", 10);
            }
        }catch(Exception e){
            e.printStackTrace();
            editor.putInt("roundsLeft", 10);
        }
        editor.putInt("difficulty", schwierigkeitsPicker.getProgress()+1);
        editor.putBoolean("insaneModus", insaneSwitch.isChecked());
        editor.putBoolean("soundModus", soundSwitch.isChecked());

        editor.commit();

        Toast.makeText(getApplicationContext(), "Einstellungen gespeichert", Toast.LENGTH_SHORT).show();
        finish();
    }

    //Innere Klasse fuer Textfeldaenderung:
    class myCheckBoxChnageClicker implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup buttonView,
                                     int checkedId) {

                if(checkedId == R.id.rundenRadioButton) {
                    anzahlTextView.setText("Anzahl Runden:");
                }else if(checkedId == R.id.zeitRadioButton) {
                    anzahlTextView.setText("Spielminuten:");
                }else{
                    anzahlTextView.setText("Punktelimit:");
                }
            }
    }
}
