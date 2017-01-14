package de.uulm.dbis.quartett42;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
    Switch expertSwitch;
    Switch soundSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        //Falls gerade ein Spiel am laufen ist, duerfen die Einstellungen nicht geaendert werden
        if(sharedPref.getInt("runningGame", 0) == 1){
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder info = new AlertDialog.Builder(this);
            info.setTitle("Hinweis");
            info.setMessage("Einstellungen aendern nicht moeglich waehrend eines laufenden Spiels!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            AlertDialog infoAlert =  info.create();
            infoAlert.show();
            //finish();
        }else{

            //Schauen, von welcher Activity man kommt
            //Linken Oberen Zurueck-Button deaktivieren:
            //JSON-String auslesen:
            Intent intent = getIntent();
            if(intent.getStringExtra("setting_source").equals("new_game")){
                //Falls von NewGameActivity kommt Linken Oberen Zurueck-Button entfernen:
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }

            //alle UI-Elemente suchen:
            buttonGroup = (RadioGroup)findViewById(R.id.varianteGroup);
            rundenButton = (RadioButton)findViewById(R.id.rundenRadioButton);
            zeitButton = (RadioButton)findViewById(R.id.zeitRadioButton);
            punkteButton = (RadioButton)findViewById(R.id.punkteRadioButton);
            anzahlTextView = (TextView)findViewById(R.id.anzahlTextView);
            anzahlEdit = (EditText)findViewById(R.id.anzahlEditText);
            schwierigkeitsPicker = (SeekBar)findViewById(R.id.schwierigkeitsgradPicker);
            insaneSwitch = (Switch)findViewById(R.id.insaneSwitch);
            expertSwitch = (Switch)findViewById(R.id.expertSwitch);
            soundSwitch = (Switch)findViewById(R.id.soundSwitch);

            //Alle Werte aus DefaultSharedPreferences lesen und UI setzen:
            int mode = sharedPref.getInt("mode", 1);
            if(mode == 1){
                rundenButton.setChecked(true);
                anzahlTextView.setText("Anzahl Runden:");
                anzahlEdit.setText(""+sharedPref.getInt("roundsLeft", 10));
            }else if(mode == 2){
                zeitButton.setChecked(true);
                anzahlTextView.setText("Spielminuten:");
                anzahlEdit.setText(""+sharedPref.getInt("roundsLeft", 10));
            }else{
                punkteButton.setChecked(true);
                anzahlTextView.setText("Punktelimit:");
                anzahlEdit.setText(""+sharedPref.getInt("pointsLeft", 1000));
            }

            schwierigkeitsPicker.setProgress(sharedPref.getInt("difficulty", 2)-1);
            insaneSwitch.setChecked(sharedPref.getBoolean("insaneModus", false));
            expertSwitch.setChecked(sharedPref.getBoolean("expertModus", false));
            soundSwitch.setChecked(sharedPref.getBoolean("soundModus", true));

            buttonGroup.setOnCheckedChangeListener(new myCheckBoxChangeClicker());
        }

    }


    //Neue Werte speichern bei Button-Klick:
    public void saveButtonFunction(View view){
        if(sharedPref.getInt("runningGame", 0) == 0) {
            SharedPreferences.Editor editor = sharedPref.edit();
            if (rundenButton.isChecked()) {
                editor.putInt("mode", 1);
                try {
                    int tmpValue = Integer.parseInt(anzahlEdit.getText().toString());
                    if (tmpValue > 0 && tmpValue < 9999) {
                        editor.putInt("roundsLeft", tmpValue);
                    } else {
                        editor.putInt("roundsLeft", 10);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    editor.putInt("roundsLeft", 10);
                }
            } else if (zeitButton.isChecked()) {
                editor.putInt("mode", 2);
                try {
                    int tmpValue = Integer.parseInt(anzahlEdit.getText().toString());
                    if (tmpValue > 0 && tmpValue < 9999) {
                        editor.putInt("roundsLeft", tmpValue);
                    } else {
                        editor.putInt("roundsLeft", 10);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    editor.putInt("roundsLeft", 10);
                }
            } else {
                editor.putInt("mode", 3);
                try {
                    int tmpValue = Integer.parseInt(anzahlEdit.getText().toString());
                    if (tmpValue > 0 && tmpValue < 999999) {
                        editor.putInt("pointsLeft", tmpValue);
                    } else {
                        editor.putInt("pointsLeft", 1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    editor.putInt("pointsLeft", 1000);
                }
            }

            editor.putInt("difficulty", schwierigkeitsPicker.getProgress() + 1);
            editor.putBoolean("insaneModus", insaneSwitch.isChecked());
            editor.putBoolean("expertModus", expertSwitch.isChecked());
            editor.putBoolean("soundModus", soundSwitch.isChecked());

            editor.apply();

            Toast.makeText(getApplicationContext(), "Einstellungen gespeichert", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "Aktion untersagt", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    //Innere Klasse fuer Textfeldaenderung:
    class myCheckBoxChangeClicker implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup buttonView,
                                     int checkedId) {

                if(checkedId == R.id.rundenRadioButton) {
                    anzahlTextView.setText("Anzahl Runden:");
                    anzahlEdit.setText(""+sharedPref.getInt("roundsLeft", 10));
                }else if(checkedId == R.id.zeitRadioButton) {
                    anzahlTextView.setText("Spielminuten:");
                    anzahlEdit.setText(""+sharedPref.getInt("roundsLeft", 10));
                }else{
                    anzahlTextView.setText("Punktelimit:");
                    anzahlEdit.setText(""+sharedPref.getInt("pointsLeft", 1000));
                }
            }
    }
}
