package de.uulm.dbis.quartett42;

import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Luis on 12.01.2017.
 */
public class CardImageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.card_image_fragment, container, false);

        // Get the arguments that was supplied when the fragment was instantiated by the adapter
        Bundle args = getArguments();
        String imageUri = args.getString("imageUri");
        String imageDesc = args.getString("imageDesc");
        assert imageUri != null;
        assert imageDesc != null;
        ImageView cardImageView = (ImageView) rootView.findViewById(R.id.cardImageView);
        ImageButton imageDescBtn = (ImageButton) rootView.findViewById(R.id.imageDescBtn);

        // set bitmap for fragment
        AssetManager assetManager = rootView.getContext().getAssets();
        InputStream is;
        try {
            is = assetManager.open(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            cardImageView.setImageBitmap(bitmap);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(imageDesc)
                .setTitle("Beschreibung")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        final AlertDialog descriptionAlertDialog = builder.create();

        imageDescBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                descriptionAlertDialog.show();
            }
        });

        return rootView;
    }
}