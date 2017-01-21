package de.uulm.dbis.quartett42;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * Created by Luis on 21.01.2017.
 */

public class ImageLoaderAssetsTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private Context context;

    public ImageLoaderAssetsTask(ImageView imageView, Context context) {
        imageViewReference = new WeakReference<ImageView>(imageView);
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        // image uri points to a local file
        String imageUri = params[0];

        InputStream is = null;
        AssetManager assetManager = context.getAssets();
        try {
            is = assetManager.open(imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(is);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        ImageView imageView = imageViewReference.get();
        if (imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
    }
}