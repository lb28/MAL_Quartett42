package de.uulm.dbis.quartett42;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import de.uulm.dbis.quartett42.data.Deck;

/**
 * Created by Luis on 12.01.2017.
 */
public class CardImageFragment extends Fragment {
    public static final String TAG = "CardImageFragment";

    AlertDialog descriptionAlertDialog;

    @Override
    public void onPause() {
        super.onPause();
        if (descriptionAlertDialog != null) {
            descriptionAlertDialog.dismiss();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.card_image_fragment, container, false);

        // Get the arguments that were supplied when the fragment was instantiated by the adapter
        Bundle args = getArguments();

        // image uri will be in the right format when supplied by the pagerAdapter
        String imageUri = args.getString("imageUri");
        int srcMode = args.getInt("srcMode");
        final String imageDesc = args.getString("imageDesc");
        String deckName = args.getString("deckName");
        ImageView cardImageView = (ImageView) rootView.findViewById(R.id.cardImageView);
        ImageButton imageDescBtn = (ImageButton) rootView.findViewById(R.id.imageDescBtn);

        // set bitmap for fragment
        // call one of three different tasks for the three srcModes
        switch (srcMode) {
            case Deck.SRC_MODE_SERVER:
                if (imageUri != null && imageUri.isEmpty()) {
                    imageUri = null;
                }
                Picasso.with(getContext())
                        .load(imageUri)
                        .resize(500, 500)
                        .centerInside()
                        .onlyScaleDown()
                        .into(cardImageView);
                break;
            case Deck.SRC_MODE_ASSETS:
                imageUri = "file:///android_asset/" + deckName + "/" + imageUri;
                Picasso.with(getContext())
                        .load(imageUri)
                        .resize(500, 500)
                        .centerInside()
                        .onlyScaleDown()
                        .into(cardImageView);
                break;
            case Deck.SRC_MODE_INTERNAL_STORAGE:
                File imgFile = new File(getContext().getFilesDir() + "/" + imageUri);
                System.out.println(imgFile);
                Picasso.with(getContext())
                        .load(imgFile)
                        .resize(500, 500)
                        .centerInside()
                        .onlyScaleDown()
                        .into(cardImageView);
                break;
        }

        imageDescBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                descriptionAlertDialog = new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.ic_info_black_24dp)
                        .setMessage(imageDesc)
                        .setTitle("Beschreibung")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });



        return rootView;
    }
}
