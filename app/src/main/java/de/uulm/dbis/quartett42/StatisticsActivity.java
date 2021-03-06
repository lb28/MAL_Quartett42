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
import com.github.mikephil.charting.components.Legend;
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

    PieChart pieChart, pieChart2, pieChart3, pieChart4;
    PieDataSet pieDataSet, pieDataSet2, pieDataSet3, pieDataSet4, pieDataSet5;
    PieData pieData, pieData2, pieData3, pieData4, pieData5;

    private final String[] xValues = {"Wins", "Losses"};

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
        pieChart3 = (PieChart) findViewById(R.id.pieChart3);
        pieChart4 = (PieChart) findViewById(R.id.pieChart4);

        setDataForPieChart();

        pieChart.invalidate();
        pieChart2.invalidate();
        pieChart3.invalidate();
        pieChart4.invalidate();
    }

    public void setDataForPieChart(){

        int spieleGesamt = sharedPref.getInt("spieleGesamt", 0);
        int spieleGesamtGewonnen = sharedPref.getInt("spieleGesamtGewonnen", 0);
        int spieleGesamtVerloren = spieleGesamt - spieleGesamtGewonnen;

        int normalespiele = sharedPref.getInt("normaleSpiele", 0);
        int normalespielegewonnen = sharedPref.getInt("normaleSpieleGewonnen", 0);
        int normalespieleverloren = normalespiele - normalespielegewonnen;

        int insanespiele = sharedPref.getInt("insaneSpiele", 0);
        int insanespielegewonnen = sharedPref.getInt("insaneSpieleGewonnen", 0);
        int insanespieleverloren = insanespiele - insanespielegewonnen;

        int expertspiele = sharedPref.getInt("expertSpiele", 0);
        int expertspielegewonnen = sharedPref.getInt("expertSpieleGewonnen", 0);
        int expertspieleverloren = expertspiele - expertspielegewonnen;

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(20,205,50));
        colors.add(Color.rgb(255, 20, 0));


        //----------------- spiele gesamt -------------------------

        ArrayList<PieEntry> yVals = new ArrayList<>();
        yVals.add(new PieEntry(spieleGesamtGewonnen, xValues[0]));
        yVals.add(new PieEntry(spieleGesamtVerloren, xValues[1]));

        //create pieDataSet
        pieDataSet =  new PieDataSet(yVals, "");
        pieDataSet.setSliceSpace(3);
        pieDataSet.setSelectionShift(5);
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setValueTextColor(Color.BLACK);

        //create pie data object and set xValues and yValues and set it to the pie chart
        pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());

        pieChart.setData(pieData);
        pieChart.setDescription(null);
        pieChart.setCenterText("Spiele gesamt: " + spieleGesamt);

        if (spieleGesamt == 0) {
            Legend l = pieChart.getLegend();
            l.setEnabled(false);
        }


        //----------------- normale spiele -------------------------

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
        pieData2.setValueFormatter(new PercentFormatter());

        pieChart2.setData(pieData2);
        pieChart2.setDescription(null);
        pieChart2.setCenterText("Normale Spiele: " + normalespiele);

        if (normalespiele == 0) {
            Legend l = pieChart2.getLegend();
            l.setEnabled(false);
        }


        //----------------- insane spiele -------------------------

        ArrayList<PieEntry> yVals3 = new ArrayList<>();
        yVals3.add(new PieEntry(insanespielegewonnen, xValues[0]));
        yVals3.add(new PieEntry(insanespieleverloren, xValues[1]));

        //create pieDataSet
        pieDataSet3 =  new PieDataSet(yVals3, "");
        pieDataSet3.setSliceSpace(3);
        pieDataSet3.setSelectionShift(5);
        pieDataSet3.setColors(colors);
        pieDataSet3.setValueTextSize(12f);


        //create pie data object and set xValues and yValues and set it to the pie chart
        pieData3 = new PieData(pieDataSet3);
        pieData3.setValueFormatter(new PercentFormatter());

        pieChart3.setData(pieData3);
        pieChart3.setDescription(null);
        pieChart3.setCenterText("Insane Spiele: " + insanespiele);

        if (insanespiele == 0) {
            Legend l = pieChart3.getLegend();
            l.setEnabled(false);
        }


        //----------------- expert spiele -------------------------

        ArrayList<PieEntry> yVals4 = new ArrayList<>();
        yVals4.add(new PieEntry(expertspielegewonnen, xValues[0]));
        yVals4.add(new PieEntry(expertspieleverloren, xValues[1]));

        //create pieDataSet
        pieDataSet4 =  new PieDataSet(yVals4, "");
        pieDataSet4.setSliceSpace(3);
        pieDataSet4.setSelectionShift(5);
        pieDataSet4.setColors(colors);
        pieDataSet4.setValueTextSize(12f);

        //create pie data object and set xValues and yValues and set it to the pie chart
        pieData4 = new PieData(pieDataSet4);
        pieData4.setValueFormatter(new PercentFormatter());

        pieChart4.setData(pieData4);
        pieChart4.setDescription(null);
        pieChart4.setCenterText("Expert Spiele: " + expertspiele);

        if (expertspiele == 0) {
            Legend l = pieChart4.getLegend();
            l.setEnabled(false);
        }

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

        /*
        //setze alle Felder auf die Defaults
        Spielegesamt.setText("" + 0);
        Spielegesamtgewonnen.setText("" + 0 + " (" + 0 + " %)");

        normaleSpiele.setText("" + 0);
        normaleSpielegewonnen.setText("" + 0 + " (" + 0 + " %)");

        insaneSpiele.setText("" + 0);
        insaneSpielegewonnen.setText("" + 0 + " (" + 0 + " %)");

        expertSpiele.setText("" + 0);
        expertSpielegewonnen.setText("" + 0 + " (" + 0 + " %)");
        */

        ArrayList<PieEntry> yVals5 = new ArrayList<>();
        yVals5.add(new PieEntry(0, xValues[0]));
        yVals5.add(new PieEntry(0, xValues[1]));

        //create pieDataSet
        pieDataSet5 =  new PieDataSet(yVals5, "");
        pieDataSet5.setSliceSpace(3);
        pieDataSet5.setSelectionShift(5);
        //pieDataSet5.setColors(colors);
        pieDataSet5.setValueTextSize(12f);


        //create pie data object and set xValues and yValues and set it to the pie chart
        pieData5 = new PieData(pieDataSet5);
        pieData5.setValueFormatter(new PercentFormatter());

        pieChart.setData(pieData5);
        pieChart.setDescription(null);
        pieChart.setCenterText("Spiele gesamt: " + 0);
        pieChart.invalidate();

        pieChart2.setData(pieData5);
        pieChart2.setDescription(null);
        pieChart2.setCenterText("Normale Spiele: " + 0);
        pieChart2.invalidate();

        pieChart3.setData(pieData5);
        pieChart3.setDescription(null);
        pieChart3.setCenterText("Insane Spiele: " + 0);
        pieChart3.invalidate();

        pieChart4.setData(pieData5);
        pieChart4.setDescription(null);
        pieChart4.setCenterText("Expert Spiele: " + 0);
        pieChart4.invalidate();

        Legend l = pieChart.getLegend();
        l.setEnabled(false);

        Legend l2 = pieChart2.getLegend();
        l.setEnabled(false);

        Legend l3 = pieChart3.getLegend();
        l.setEnabled(false);

        Legend l4 = pieChart4.getLegend();
        l.setEnabled(false);

        finish();

    }





}
