package de.uulm.dbis.quartett42;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.uulm.dbis.quartett42.data.Property;

/**
 * Created by Luis on 11.01.2017.
 */
public class AttributeItemAdapter extends ArrayAdapter<Property> {
    public AttributeItemAdapter(Context context, int resource, List<Property> attributes) {
        super(context, resource, attributes);
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

        // Lookup view for data population
        TextView textViewAttrName = (TextView) convertView.findViewById(R.id.textViewAttrName);
        TextView textViewAttrValue = (TextView) convertView.findViewById(R.id.textViewAttrValue);
        TextView textViewAttrUnit = (TextView) convertView.findViewById(R.id.textViewAttrUnit);

        assert property != null;
        // Populate the data into the template view using the data object
        String nameAndMaxWinner = property.getName();
        if (property.getMaxwinner()) {
            nameAndMaxWinner += " (\u2191)"; // upwards arrow
        } else {
            nameAndMaxWinner += " (\u2193)"; // downwards arrow
        }
        textViewAttrName.setText(nameAndMaxWinner);
        textViewAttrValue.setText(String.valueOf(property.getValue()));
        textViewAttrUnit.setText(property.getUnit());

        // Return the completed view to render on screen
        return convertView;
    }
}
