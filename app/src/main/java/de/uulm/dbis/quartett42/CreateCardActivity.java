package de.uulm.dbis.quartett42;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;

import de.uulm.dbis.quartett42.data.Card;
import de.uulm.dbis.quartett42.data.Deck;
import de.uulm.dbis.quartett42.data.ImageCard;
import de.uulm.dbis.quartett42.data.Property;

public class CreateCardActivity extends AppCompatActivity {
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int PICK_IMAGE_REQUEST = 2;

    private CreateCardItemAdapter itemAdapter;

    private ArrayList<Bitmap> cardImages;
    private ArrayList<String> imgDescriptions;

    private Deck newDeck;
    private int currentCardIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_card);

        cardImages = new ArrayList<>();
        imgDescriptions = new ArrayList<>();
        currentCardIndex = 0;

        String deckName = getIntent().getStringExtra("newDeckName");
        // TODO figure out how we can use the attibutes of the old deck if the attributes
        // were edited in CreateDeckActivity (only relevant for editing existing decks)
        // (we can reuse the name, imageCards, and those attributes that weren't changed)
        String copyFromDeckName = getIntent().getStringExtra("copyFromDeckName");

        // get the newly created deck which has no cards yet
        LocalJSONHandler jsonHandler = new LocalJSONHandler(this, Deck.SRC_MODE_INTERNAL_STORAGE);
        newDeck = jsonHandler.getDeck(deckName);

        updateViewFromCard();
    }

    /**
     * called by clicking the "show previous card" button on the left
     * @param view
     */
    public void showPreviousCard(View view) {
    }

    /**
     * called when user is done adding cards and wants to save the deck
     */
    public void saveNewDeck(View view) {
        saveCard();

        // TODO redirect to GalleryActivity, but first get rid of the json string
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * called by clicking the "add another card (plus)" button on the right
     * @param view
     */
    public void showNextCard(View view) {
        // try to save the current card
        if (!saveCard()) {
            Toast.makeText(this, "Karte konnte nicht gespeichert werden", Toast.LENGTH_SHORT).show();
        } else {
            // go to the next card
            currentCardIndex++;
            updateViewFromCard();
        }
    }

    /**
     * saves the current card into the existing deck, i.e.: <br/>
     * - saves the pictures of the card <br/>
     * - adds the card in the json file
     * - empties the imageCard and imgDescriptions lists
     */
    private boolean saveCard() {
        LocalJSONHandler jsonHandler = new LocalJSONHandler(this, Deck.SRC_MODE_INTERNAL_STORAGE);
        ArrayList<ImageCard> imageCards = new ArrayList<>();
        EditText editTextCardName = (EditText) findViewById(R.id.editTextCardName);

        try {
            // save each picture with corresponding description
            for (int i = 0; i < cardImages.size(); i++) {
                String imageUri = newDeck.getName() + currentCardIndex + "_" + i + ".jpg";
                ImageCard imageCard = jsonHandler.createImageCard(
                        imageUri, cardImages.get(i), imgDescriptions.get(i));
                imageCards.add(imageCard);
            }

            HashMap<String, Double> attributeMap = new HashMap<>();

            // put all the values of the properties into a hashmap with the name as key
            for (int i = 0; i < newDeck.getPropertyList().size(); i++) {
                Property p = itemAdapter.getItem(i);
                attributeMap.put(p.getName(), p.getValue());
            }

            String cardName = editTextCardName.getText().toString();

            Card newCard = new Card(cardName, currentCardIndex, imageCards, attributeMap);
            newDeck.getCardList().set(currentCardIndex, newCard);

            // save the card in the json file
            jsonHandler.saveCard(newDeck.getName(), newCard);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * updates the view to show the current content of the model
     * TODO text from the textfields is lost during the update
     */
    public void updateViewFromCard() {
        // show the spinner
        ContentLoadingProgressBar spinner =
                (ContentLoadingProgressBar) findViewById(R.id.progressBar1);
        spinner.show();

        // get all the views
        EditText editTextCardName = (EditText) findViewById(R.id.editTextCardName);
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

        // set card name
        editTextCardName.setText(card.getName());

        // set attribute list
        ArrayList<Property> attrList = Util.buildAttrList(newDeck.getPropertyList(), card);
        itemAdapter = new CreateCardItemAdapter(
                this, R.layout.create_card_attr, attrList);
        createCardAttrListView.setAdapter(itemAdapter);

        updateImageContainer();

        // hide the spinner when done
        spinner.hide();
    }

    private void updateImageContainer() {
        LinearLayout cardImgContainer = (LinearLayout) findViewById(R.id.createCardImgLinearLayout);

        // empty the image container
        cardImgContainer.removeAllViews();

        // add all the bitmaps (and descriptions) to the container
        for (int i = 0; i < cardImages.size(); i++) {
            cardImgContainer.addView(makeImageCard(i));
        }

        // add the "add photo" button behind the last element
        cardImgContainer.addView(makeAddPhotoImgView(cardImages.size()));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                addCardPic((Bitmap) extras.get("data"));
            }
        } else if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();

                Target deckImgBtnTarget = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        addCardPic(bitmap);
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
     * adds a bitmap to the list of pictures, adds an empty description to the list of descriptions,
     * then updates the view
     */
    private void addCardPic(Bitmap bitmap){
        cardImages.add(bitmap);
        imgDescriptions.add("");
        updateImageContainer();
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
        imageView.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray_bg));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPicDialog();
            }
        });
        return imageView;
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

        rootView.setPadding(0,0,10,0);

        final ImageView cardImageView = (ImageView) rootView.findViewById(R.id.cardImageView);
        ImageButton imageDescBtn = (ImageButton) rootView.findViewById(R.id.imageDescBtn);

        // set bitmap
        cardImageView.setImageBitmap(cardImages.get(position));


        cardImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                cardImages.remove(position);
                imgDescriptions.remove(position);
                updateImageContainer();
                return true;
            }
        });

        final EditText imgDescrEditText = (EditText) getLayoutInflater().inflate(R.layout.edit_descr_view,null);
        imgDescrEditText.setText(imgDescriptions.get(position));
        // set click listener for description button
        imageDescBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(CreateCardActivity.this)
                        .setIcon(R.drawable.ic_info_black_24dp)
                        .setTitle("Beschreibung")
                        .setView(imgDescrEditText)
                        .setPositiveButton("Speichern", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get the new description and save it
                                String newDescr = imgDescrEditText.getText().toString();
                                // replace the old description if there was any
                                imgDescriptions.set(position, newDescr);
                                dialog.dismiss();

                                updateImageContainer();
                            }
                        })
                        .setNeutralButton("Abbrechen", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();

                                updateImageContainer();
                            }
                        })
                        .show();
            }
        });

        return rootView;
    }

    public void addPicDialog() {
        String[] options = {"Foto aufnehmen", "Aus Galerie wählen"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Bild hinzufügen")
                .setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            takePicture();
                        } else if (which == 1) {
                            pickImage();
                        }
                    }
                });
        Dialog d = builder.create();
        d.show();
    }

    public void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void pickImage() {
        Intent intent;
        intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }
}
