package de.uulm.dbis.quartett42;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.List;

import de.uulm.dbis.quartett42.data.Deck;
import de.uulm.dbis.quartett42.data.Property;

/**
 * Created by Luis on 11.01.2017.
 */
public class CreateDeckItemAdapter extends ArrayAdapter<Property> {

    private List<Property> attrList;

    public CreateDeckItemAdapter(Context context, int resource, List<Property> attrList) {
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
                    R.layout.create_deck_attr, parent, false);
        }

        // Lookup view for data population
        final EditText editTextAttrName = (EditText) convertView.findViewById(R.id.editTextAttrName);
        Button maxWinnerBtn = (Button) convertView.findViewById(R.id.maxWinnerBtn);
        final EditText editTextAttrUnit = (EditText) convertView.findViewById(R.id.editTextAttrUnit);
        ImageButton deleteRowBtn = (ImageButton) convertView.findViewById(R.id.deleteRowBtn);

        // Populate the template view with the data
        if (property != null) {
            if (property.getName() != null || property.getName().isEmpty()) {
                editTextAttrName.setText(property.getName());
            }
            if (property.getUnit() != null || property.getUnit().isEmpty()) {
                editTextAttrUnit.setText(property.getUnit());
            }
            if (property.isMaxWinner()) {
                maxWinnerBtn.setText(Deck.UNICODE_MAX_WINNER);
            } else {
                maxWinnerBtn.setText(Deck.UNICODE_MIN_WINNER);
            }
        }

        maxWinnerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button maxWinnerBtn = (Button) view;
                // swap the up / down arrow
                String newBtnText = maxWinnerBtn.getText().equals(Deck.UNICODE_MAX_WINNER) ?
                        Deck.UNICODE_MIN_WINNER : Deck.UNICODE_MAX_WINNER;
                maxWinnerBtn.setText(newBtnText);

                Property p = attrList.get(position);
                // update the maxWinner field of the property
                p.setMaxWinner(maxWinnerBtn.getText().equals(Deck.UNICODE_MAX_WINNER));
                attrList.set(position, p);
            }
        });
        deleteRowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remove(property);
            }
        });

        editTextAttrName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    try{
                        Property p = attrList.get(position);
                        // update the name of the property
                        p.setName(editTextAttrName.getText().toString());
                        attrList.set(position, p);
                    } catch (IndexOutOfBoundsException e) {/* row was already deleted */}
                }
            }
        });
        editTextAttrUnit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    try{
                        Property p = attrList.get(position);
                        // update the unit of the property
                        p.setUnit(editTextAttrUnit.getText().toString());
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
