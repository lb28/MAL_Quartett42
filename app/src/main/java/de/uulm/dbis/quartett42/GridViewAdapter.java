package de.uulm.dbis.quartett42;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
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
        ViewHolder holder = null;

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

        Deck item = data.get(position);
        holder.imageTitle.setText(item.getName());

        String imageUri = item.getName()+"/"+item.getImage().getUri();
        AssetManager assetManager = context.getAssets();
        InputStream is = null;
        try {
            is = assetManager.open(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            holder.image.setImageBitmap(bitmap);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            //Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return row;
    }

    static class ViewHolder {
        TextView imageTitle;
        ImageView image;
    }
}