package de.uulm.dbis.quartett42;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import de.uulm.dbis.quartett42.data.Card;
import de.uulm.dbis.quartett42.data.Deck;
import de.uulm.dbis.quartett42.data.ImageCard;

/**
 * A simple pager adapter that shows images in sequence
 */
class ImageSlidePagerAdapter extends FragmentStatePagerAdapter {
    private Deck deck;
    private Card card;

    ImageSlidePagerAdapter(FragmentManager fm, Deck deck, Card card) {
        super(fm);
        this.deck = deck;
        this.card = card;
    }

    @Override
    public Fragment getItem(int position) {

        // create card image fragment
        CardImageFragment cardimageFragment = new CardImageFragment();

        // get the currently selected ImageCard from the currently selected card
        ImageCard currentImageCard = card.getImageList().get(position);

        // Attach data to it that we'll use to populate our fragment layouts
        Bundle args = new Bundle();

        // Set the arguments on the fragment that will be fetched in onCreateView
        String imageUri = deck.getName()+"/"+currentImageCard.getUri();
        args.putString("imageUri", imageUri);
        args.putString("imageDesc", currentImageCard.getDescription());
        cardimageFragment.setArguments(args);

        return cardimageFragment;
    }

    @Override
    public int getCount() {
        // the size of the selected card's image list
        return card.getImageList().size();
    }
}
