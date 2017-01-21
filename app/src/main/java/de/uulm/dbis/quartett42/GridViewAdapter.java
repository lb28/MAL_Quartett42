package de.uulm.dbis.quartett42;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.uulm.dbis.quartett42.data.Deck;

public class GridViewAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private ArrayList<Deck> data = new ArrayList<Deck>();

    public GridViewAdapter(Context context, int layoutResourceId, ArrayList data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        // TODO add the info button that displays the deck description

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) row.findViewById(R.id.galleryListName);
            holder.image = (ImageView) row.findViewById(R.id.galleryListImage);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Deck singleDeck = data.get(position);
        holder.imageTitle.setText(singleDeck.getName());

        String imageUri = singleDeck.getImage().getUri();

        // call one of three different tasks for the three srcModes
        switch (singleDeck.getSrcMode()) {
            case Deck.SRC_MODE_SERVER:
                new ImageLoaderServerTask(holder.image).execute(imageUri);
            case Deck.SRC_MODE_ASSETS:
                imageUri = singleDeck.getName() + "/" + imageUri;
                new ImageLoaderAssetsTask(holder.image, context).execute(imageUri);
                break;
            case Deck.SRC_MODE_INTERNAL_STORAGE:
                imageUri = singleDeck.getName() + "/" + imageUri;
                new ImageLoaderInternalStorageTask(holder.image, context).execute(imageUri);
                break;
        }

        return row;
    }

    private static class ViewHolder {
        TextView imageTitle;
        ImageView image;
    }
}