package de.uulm.dbis.quartett42;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.uulm.dbis.quartett42.data.Deck;

public class GridViewAdapter extends ArrayAdapter<Deck> {
    private static final String TAG = "GridViewAdapter";

    private Context context;
    private int layoutResourceId;

    public GridViewAdapter(Context context, int layoutResourceId, ArrayList<Deck> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
             LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) row.findViewById(R.id.galleryListName);
            holder.image = (ImageView) row.findViewById(R.id.galleryListImage);
            holder.descButtonView = (ImageView) row.findViewById(R.id.deckImgDescView);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final Deck singleDeck = getItem(position);

        holder.imageTitle.setText(singleDeck.getName());

        holder.descButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setIcon(R.drawable.ic_info_black_24dp)
                        .setMessage(singleDeck.getImage().getDescription())
                        .setTitle("Beschreibung")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        String imageUri = singleDeck.getImage().getUri();
        // call one of three different tasks for the three srcModes

        switch (singleDeck.getSrcMode()) {
            case Deck.SRC_MODE_SERVER:
                if (imageUri.isEmpty()) {
                    imageUri = null;
                }
                Picasso.with(context)
                        .load(imageUri)
                        .resize(500, 500)
                        .centerInside()
                        .onlyScaleDown()
                        .placeholder(R.drawable.menu_image)
                        .into(holder.image);
                break;
            case Deck.SRC_MODE_ASSETS:
                // TODO replace with picasso?
                imageUri = singleDeck.getName() + "/" + imageUri;
                new ImageLoaderAssetsTask(holder.image, context).execute(imageUri);
                break;
            case Deck.SRC_MODE_INTERNAL_STORAGE:
                // TODO replace with picasso?
                new ImageLoaderInternalStorageTask(holder.image, context).execute(imageUri);
                break;
        }

        return row;
    }

    private static class ViewHolder {
        TextView imageTitle;
        ImageView image;
        ImageView descButtonView;
    }
}