package de.uulm.dbis.quartett42;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import de.uulm.dbis.quartett42.data.ImageCard;

/**
 * A simple pager adapter that shows images in sequence
 */
class ImageSlidePagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<ImageCard> imageCards;
    private String deckName;

    ImageSlidePagerAdapter(FragmentManager fm, ArrayList<ImageCard> imageCards, String deckName) {
        super(fm);
        this.imageCards = imageCards;
        this.deckName = deckName;
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
        String imageUri = deckName+"/"+currentImageCard.getUri();
        args.putString("imageUri", imageUri);
        args.putString("imageDesc", currentImageCard.getDescription());
        cardimageFragment.setArguments(args);

        return cardimageFragment;
    }

    @Override
    public int getCount() {
        // the size of the selected card's image list
        return imageCards.size();
    }
}
