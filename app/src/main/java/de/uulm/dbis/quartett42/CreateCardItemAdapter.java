package de.uulm.dbis.quartett42;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import de.uulm.dbis.quartett42.data.Deck;
import de.uulm.dbis.quartett42.data.Property;

/**
 * Created by Luis on 11.01.2017.
 */
public class CreateCardItemAdapter extends ArrayAdapter<Property> {
    private static final String TAG = "CreateCardItemAdapter";

    private List<Property> attrList;

    public CreateCardItemAdapter(Context context, int resource, List<Property> attrList) {
        super(context, resource, attrList);
        this.attrList = attrList;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        // Get the Property item for this position
        final Property property = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.create_card_attr, parent, false);
        }

        // Lookup view for data population
        TextView textViewAttrName = (TextView) convertView.findViewById(
                R.id.textViewCreateCardAttrName);
        TextView textViewAttrUnit = (TextView) convertView.findViewById(
                R.id.textViewCreateCardAttrUnit);
        final EditText editTextAttrValue = (EditText) convertView.findViewById(
                R.id.editTextcreateCardAttrValue);

        // Populate the template view with the data
        if (property != null) {
            if (property.getName() != null || property.getName().isEmpty()) {
                String nameAndMaxWinner = property.getName();
                if (property.isMaxWinner()) {
                    nameAndMaxWinner += " "+ Deck.UNICODE_MAX_WINNER; // upwards arrow
                } else {
                    nameAndMaxWinner += " "+ Deck.UNICODE_MIN_WINNER; // downwards arrow
                }
                textViewAttrName.setText(nameAndMaxWinner);
            }

            editTextAttrValue.setText(String.valueOf(property.getValue()));

            if (property.getUnit() != null || property.getUnit().isEmpty()) {
                textViewAttrUnit.setText(property.getUnit());
            }
        }

        editTextAttrValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    try{
                        Property p = attrList.get(position);

                        Log.i(TAG, "onFocusChange: updating attribute \"" + p.getName() + "\"");

                        // update the name of the property
                        double newVal = 0;
                        if (!editTextAttrValue.getText().toString().isEmpty()) {
                            try {
                                newVal = Double.parseDouble(editTextAttrValue.getText().toString());
                            } catch (NumberFormatException nfe) {
                                nfe.printStackTrace();
                                Toast.makeText(getContext(), "Ungültiger Wert", Toast.LENGTH_SHORT)
                                        .show();
                                newVal = 0;
                            }
                        }
                        p.setValue(newVal);
                        editTextAttrValue.setText(String.valueOf(newVal));
                        attrList.set(position, p);
                    } catch (IndexOutOfBoundsException e) {/* row was already deleted */}
                }
            }
        });

        //Set two different background colors:
        convertView.setBackgroundColor(position % 2 == 0 ? Color.argb(50, 150, 200, 210) : Color.argb(50, 60, 160, 190));

        // Return the completed view to render on screen
        return convertView;
    }
}
