package de.uulm.dbis.quartett42;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import de.uulm.dbis.quartett42.data.Card;
import de.uulm.dbis.quartett42.data.Deck;
import de.uulm.dbis.quartett42.data.Property;

public class CreateCardActivity extends AppCompatActivity {
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int PICK_IMAGE_REQUEST = 2;

    private ArrayList<Bitmap> cardImages;
    private ArrayList<String> imgDescriptions;

    private Deck newDeck;
    private int currentCardIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_card);

        cardImages = new ArrayList<>();
        currentCardIndex = 0;

        String deckName = getIntent().getStringExtra("newDeckName");
        // TODO figure out how we can use the attibutes of the old deck if the attributes
        // were edited in CreateDeckActivity (only relevant for editing existing decks)
        // (we can reuse the name, imageCards, and those attributes that weren't changed)
        String copyFromDeckName = getIntent().getStringExtra("copyFromDeckName");

        // get the newly created deck which has no cards yet
        LocalJSONHandler jsonHandler = new LocalJSONHandler(this, Deck.SRC_MODE_INTERNAL_STORAGE);
        newDeck = jsonHandler.getDeck(deckName);

        updateView();
    }

    public void showPreviousCard(View view) {
    }

    /**
     * called when user is done adding cards and wants to save the deck
     */
    public void saveNewDeck(View view) {

    }

    /**
     * called by clicking the "add another card (plus)" button on the right
     * @param view
     */
    public void showNextCard(View view) {

    }

    /**
     * called by clicking the "show previous card" button on the left
     * @param view
     */
    public void addAttribute(View view) {

    }

    public void updateView() {
        // get all the views
        EditText editTextCardName = (EditText) findViewById(R.id.editTextCardName);
        LinearLayout cardImgContainer = (LinearLayout) findViewById(R.id.createCardImgLinearLayout);
        ListView createCardAttrListView = (ListView) findViewById(R.id.createCardAttrListView);
        ImageButton btnRight = (ImageButton) findViewById(R.id.createCardButtonRight);
        ImageButton btnLeft = (ImageButton) findViewById(R.id.createCardButtonLeft);

        // are we behind the last card?
        if (currentCardIndex == newDeck.getCardList().size()) {
            // we want to create a new card
            newDeck.getCardList().add(new Card(currentCardIndex));
        } else if (currentCardIndex < 0) {
            // safety mechanism (wrap around to last card is done in the onClick method)
            currentCardIndex = 0;
        }

        // are we currently viewing the last card?
        if (currentCardIndex == newDeck.getCardList().size()-1) {
            btnRight.setImageResource(R.drawable.ic_add_black_24dp);
        } else {
            btnRight.setImageResource(R.drawable.ic_keyboard_arrow_right_black_24dp);
        }

        // are we currently viewing the first card?
        if (currentCardIndex == 0) {
            // prevent the user from going back further (or wrapping around to the last card)
            btnLeft.setEnabled(false);
        }

        Card card = newDeck.getCardList().get(currentCardIndex);

        // populate the views:

        // set attribute list
        ArrayList<Property> attrList = Util.buildAttrList(newDeck.getPropertyList(), card);
        CreateCardItemAdapter itemAdapter = new CreateCardItemAdapter(
                this, R.layout.create_card_attr, attrList);
        createCardAttrListView.setAdapter(itemAdapter);

        // refresh the image container
        cardImgContainer.removeAllViews();
        for (int i = 0; i < cardImages.size(); i++) {
            // TODO add an imageview using makeImageCard()
        }

        cardImgContainer.addView(makeAddPhotoImgView(cardImages.size()));

        // set card name
        editTextCardName.setText(card.getName());

    }

    private ImageView makeAddPhotoImgView(int id) {
        ImageView imageView = new ImageView(this);
        imageView.setId(id);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        imageView.setMinimumWidth(150);
        imageView.setImageResource(R.drawable.ic_add_a_photo_black_24dp);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("call image picker");
                // TODO call image picker dialog and
                // TODO in OnActivityResult save image in the last element of the container
            }
        });
        return imageView;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {

                Bundle extras = data.getExtras();
                cardImages.add((Bitmap) extras.get("data"));

                updateView();
            }
        } else if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();

                Target deckImgBtnTarget = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        cardImages.add(bitmap);
                        updateView();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };

                // load image into imageBtn
                Picasso.with(this)
                        .load(uri)
                        .resize(300,300)
                        .onlyScaleDown()
                        .centerInside()
                        .into(deckImgBtnTarget);

            }
        }
    }


    /**
     * creates a imagecard (imageview with descr button) for the specified position
     * (cardImages and imgDescriptions must contain elements at the specified index)
     * @param position
     * @return
     */
    private RelativeLayout makeImageCard(final int position) {
        try{
            cardImages.get(position);
            imgDescriptions.get(position);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }

        RelativeLayout rootView = (RelativeLayout) getLayoutInflater().inflate(
                R.layout.card_image_fragment, null, false);

        ImageView cardImageView = (ImageView) rootView.findViewById(R.id.cardImageView);
        ImageButton imageDescBtn = (ImageButton) rootView.findViewById(R.id.imageDescBtn);

        // set bitmap
        cardImageView.setImageBitmap(cardImages.get(position));

        final EditText imgDescrEditText = (EditText) getLayoutInflater().inflate(R.layout.edit_descr_view,null);

        // set click listener for description button
        imageDescBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog descriptionAlertDialog = new AlertDialog.Builder(CreateCardActivity.this)
                        .setIcon(R.drawable.ic_info_black_24dp)
                        .setMessage(imgDescriptions.get(position))
                        .setTitle("Beschreibung")
                        .setView(imgDescrEditText)
                        .setPositiveButton("Speichern", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get the new description and save it
                                String newDescr = imgDescrEditText.getText().toString();
                                // replace the old description if there was any
                                imgDescriptions.set(position, newDescr);
                                dialog.dismiss();
                            }
                        })
                        .setNeutralButton("Abbrechen", new DialogInterface.OnClickListener() {
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
