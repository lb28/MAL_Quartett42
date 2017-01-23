package de.uulm.dbis.quartett42;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import de.uulm.dbis.quartett42.data.ImageCard;

import static de.uulm.dbis.quartett42.data.Deck.SRC_MODE_ASSETS;

/**
 * A simple pager adapter that shows images in sequence
 */
class ImageSlidePagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "ImageSlidePagerAdapter";

    private ArrayList<ImageCard> imageCards;
    private String deckName;
    private int srcMode;

    ImageSlidePagerAdapter(FragmentManager fm, ArrayList<ImageCard> imageCards, String deckName, int srcMode) {
        super(fm);
        this.imageCards = imageCards;
        this.deckName = deckName;
        this.srcMode = srcMode;
    }

    @Override
    public Fragment getItem(int position) {

        // create card image fragment
        CardImageFragment cardimageFragment = new CardImageFragment();

        // get the currently selected ImageCard from the currently selected card
        ImageCard currentImageCard = imageCards.get(position);

        // Attach data to it that we'll use to populate our fragment layouts
        Bundle args = new Bundle();

        // Set the arguments on the fragment that will be fetched in onCreateView
        String imageUri = currentImageCard.getUri();
        if (srcMode == SRC_MODE_ASSETS) {
            imageUri = deckName + "/" + imageUri;
        }
        args.putString("imageUri", imageUri);
        args.putString("imageDesc", currentImageCard.getDescription());
        args.putInt("srcMode", srcMode);

        cardimageFragment.setArguments(args);

        return cardimageFragment;
    }

    @Override
    public int getCount() {
        // the size of the selected card's image list
        return imageCards.size();
    }
}
