package de.uulm.dbis.quartett42;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {

    SharedPreferences sharedPref;

    Context context;

    //Elemente:
    TextView Spielegesamt;
    TextView Spielegesamtgewonnen;
    TextView normaleSpiele;
    TextView normaleSpielegewonnen;
    TextView insaneSpiele;
    TextView insaneSpielegewonnen;
    TextView expertSpiele;
    TextView expertSpielegewonnen;

    ImageView imageView1;

    PieChart pieChart, pieChart2;
    PieDataSet pieDataSet, pieDataSet2;
    PieData pieData, pieData2;

    private final String[] xValues = {"Gewonnen", "Verloren"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        //------------------test----------------------------
/*
        final SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt("spieleGesamt", 100);
        editor.putInt("spieleGesamtGewonnen", 50);

        editor.putInt("normaleSpiele", 50);
        editor.putInt("normaleSpieleGewonnen", 30);

        editor.putInt("insaneSpiele", 50);
        editor.putInt("insaneSpieleGewonnen", 20);

        editor.commit();
*/
        //--------------------------------------------------

        /*
        Spielegesamt = (TextView) findViewById(R.id.Spielegesamt);
        Spielegesamtgewonnen = (TextView) findViewById(R.id.Spielegesamtgewonnen);
        normaleSpiele = (TextView) findViewById(R.id.normaleSpiele);
        normaleSpielegewonnen = (TextView) findViewById(R.id.normaleSpielegewonnen);
        insaneSpiele = (TextView) findViewById(R.id.insaneSpiele);
        insaneSpielegewonnen = (TextView) findViewById(R.id.insaneSpielegewonnen);
        expertSpiele = (TextView) findViewById(R.id.expertSpiele);
        expertSpielegewonnen = (TextView) findViewById(R.id.expertSpielegewonnen);



        //Werte aus sharedPreferences holen und Texte setzen:

        //Spiele gesamt
        int spieleGesamt = sharedPref.getInt("spieleGesamt", 0);
        int spieleGesamtGewonnen = sharedPref.getInt("spieleGesamtGewonnen", 0);
        double spieleGesamtGewonnenProzent ;

        if (spieleGesamt <= 0){
            spieleGesamtGewonnenProzent = 0;
        } else {
            spieleGesamtGewonnenProzent = (double)(spieleGesamtGewonnen*100/spieleGesamt);
        }

        Spielegesamt.setText("" + spieleGesamt);
        Spielegesamtgewonnen.setText("" + spieleGesamtGewonnen + " (" + spieleGesamtGewonnenProzent + " %)");

        //normale Spiele
        int normalespiele = sharedPref.getInt("normaleSpiele", 0);
        int normalespielegewonnen = sharedPref.getInt("normaleSpieleGewonnen", 0);
        double normaleSpieleGewonnenProzent;

        if (normalespiele <= 0){
            normaleSpieleGewonnenProzent = 0;
        } else {
            normaleSpieleGewonnenProzent = (double) (normalespielegewonnen*100/normalespiele);
        }

        normaleSpiele.setText("" + normalespiele);
        normaleSpielegewonnen.setText("" + normalespielegewonnen + " (" + normaleSpieleGewonnenProzent + " %)");

        //insane Spiele
        int insanespiele = sharedPref.getInt("insaneSpiele", 0);
        int insanespielegewonnen = sharedPref.getInt("insaneSpieleGewonnen", 0);
        double insaneSpieleGewonnenProzent;
        if (insanespiele <= 0){
            insaneSpieleGewonnenProzent = 0;
        } else {
            insaneSpieleGewonnenProzent = (double) (insanespielegewonnen*100/insanespiele);
        }

        insaneSpiele.setText("" + insanespiele);
        insaneSpielegewonnen.setText("" + insanespielegewonnen + " (" + insaneSpieleGewonnenProzent + " %)");

        //expert Spiele
        int expertspiele = sharedPref.getInt("expertSpiele", 0);
        int expertspielegewonnen = sharedPref.getInt("expertSpieleGewonnen", 0);
        double expertSpieleGewonnenProzent;
        if (expertspiele <= 0){
            expertSpieleGewonnenProzent = 0;
        } else {
            expertSpieleGewonnenProzent = (double) (expertspielegewonnen*100/expertspiele);
        }

        expertSpiele.setText("" + expertspiele);
        expertSpielegewonnen.setText("" + expertspielegewonnen + " (" + expertSpieleGewonnenProzent + " %)");
        */

        //pie chart
        pieChart = (PieChart) findViewById(R.id.pieChart);
        pieChart2 = (PieChart) findViewById(R.id.pieChart2);

        //imageView1 = (ImageView) findViewById(R.id.pieChart1);

        setDataForPieChart();

        //Uri uri = Uri.parse(getFilesDir() + "/Spielegesamt.jpg");
        //imageView1.setImageURI(uri);

        pieChart.invalidate();
        pieChart2.invalidate();


    }

    public void setDataForPieChart(){

        //context = this.getApplicationContext();

        int spieleGesamt = sharedPref.getInt("spieleGesamt", 0);
        int spieleGesamtGewonnen = sharedPref.getInt("spieleGesamtGewonnen", 0);
        int spieleGesamtVerloren = spieleGesamt-spieleGesamtGewonnen;

        int normalespiele = sharedPref.getInt("normaleSpiele", 0);
        int normalespielegewonnen = sharedPref.getInt("normaleSpieleGewonnen", 0);
        int normalespieleverloren = normalespiele-normalespielegewonnen;

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.GREEN);
        colors.add(Color.RED);

        ArrayList<PieEntry> yVals = new ArrayList<>();
        yVals.add(new PieEntry(spieleGesamtGewonnen, xValues[0]));
        yVals.add(new PieEntry(spieleGesamtVerloren, xValues[1]));

        //create pieDataSet
        pieDataSet =  new PieDataSet(yVals, "");
        pieDataSet.setSliceSpace(3);
        pieDataSet.setSelectionShift(5);
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextSize(12f);

        //create pie data object and set xValues and yValues and set it to the pie chart
        pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.setDescription(null);
        pieChart.setCenterText("Spiele gesamt: " + spieleGesamt);

        //pieChart.saveToPath("Spielegesamt", context.getFilesDir() + "/");

        //----------------------------

        ArrayList<PieEntry> yVals2 = new ArrayList<>();
        yVals2.add(new PieEntry(normalespielegewonnen, xValues[0]));
        yVals2.add(new PieEntry(normalespieleverloren, xValues[1]));

        //create pieDataSet
        pieDataSet2 =  new PieDataSet(yVals2, "");
        pieDataSet2.setSliceSpace(3);
        pieDataSet2.setSelectionShift(5);
        pieDataSet2.setColors(colors);
        pieDataSet2.setValueTextSize(12f);


        //create pie data object and set xValues and yValues and set it to the pie chart
        pieData2 = new PieData(pieDataSet2);

        pieChart2.setData(pieData2);
        pieChart2.setDescription(null);
        pieChart2.setCenterText("Normale Spiele: " + normalespiele);






    }




    public void resetStatistics(View view){
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt("spieleGesamt", 0);
        editor.putInt("spieleGesamtGewonnen", 0);

        editor.putInt("normaleSpiele", 0);
        editor.putInt("normaleSpieleGewonnen", 0);

        editor.putInt("insaneSpiele", 0);
        editor.putInt("insaneSpieleGewonnen", 0);

        editor.putInt("expertSpiele", 0);
        editor.putInt("expertSpieleGewonnen", 0);

        editor.commit();

        Toast.makeText(getApplicationContext(), "Statistik zurückgesetzt", Toast.LENGTH_SHORT).show();

        //setze alle Felder auf die Defaults
        Spielegesamt.setText("" + 0);
        Spielegesamtgewonnen.setText("" + 0 + " (" + 0 + " %)");

        normaleSpiele.setText("" + 0);
        normaleSpielegewonnen.setText("" + 0 + " (" + 0 + " %)");

        insaneSpiele.setText("" + 0);
        insaneSpielegewonnen.setText("" + 0 + " (" + 0 + " %)");

        expertSpiele.setText("" + 0);
        expertSpielegewonnen.setText("" + 0 + " (" + 0 + " %)");

        finish();

    }





}
