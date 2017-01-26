package de.uulm.dbis.quartett42;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.uulm.dbis.quartett42.data.Deck;
import de.uulm.dbis.quartett42.data.Property;

/**
 * Created by Luis on 11.01.2017.
 */
public class AttributeItemAdapter extends ArrayAdapter<Property> {
    private boolean isClickable;
    private boolean expertMode;
    private boolean insaneMode;

    public AttributeItemAdapter(boolean isClickable, boolean expertMode, boolean insaneMode, Context context, int resource, List<Property> attributes) {
        super(context, resource, attributes);
        this.isClickable = isClickable;
        this.expertMode = expertMode;
        this.insaneMode = insaneMode;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the Property item for this position
        Property property = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.attr_list_item, parent, false);
        }

        // negate the maxWinner if we are in insaneMode
        boolean maxWinner = property.isMaxWinner();
        if (insaneMode) {
            maxWinner = !maxWinner;
        }

        // Lookup view for data population
        TextView textViewAttrName = (TextView) convertView.findViewById(R.id.textViewAttrName);
        TextView textViewAttrValue = (TextView) convertView.findViewById(R.id.textViewAttrValue);
        TextView textViewAttrUnit = (TextView) convertView.findViewById(R.id.textViewAttrUnit);

        assert property != null;
        // Populate the data into the template view using the data object
        String nameAndMaxWinner = property.getName();
        if (maxWinner) {
            nameAndMaxWinner += " "+ Deck.UNICODE_MAX_WINNER; // upwards arrow
        } else {
            nameAndMaxWinner += " "+ Deck.UNICODE_MIN_WINNER; // downwards arrow
        }
        textViewAttrName.setText(nameAndMaxWinner);

        if (expertMode) {
            textViewAttrValue.setText("[?]");
        } else {
            textViewAttrValue.setText(String.valueOf(property.getValue()));
        }
        textViewAttrUnit.setText(property.getUnit());

        //Set two different background colors:
        convertView.setBackgroundColor(position % 2 == 0 ? Color.argb(50, 150, 200, 210) : Color.argb(50, 60, 160, 190));

        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        // return false if isClickable is set to false, else call overridden method
        return isClickable && super.isEnabled(position);
    }
}
