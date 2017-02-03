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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import de.uulm.dbis.quartett42.data.Card;
import de.uulm.dbis.quartett42.data.Deck;
import de.uulm.dbis.quartett42.data.ImageCard;
import de.uulm.dbis.quartett42.data.Property;

public class CreateCardActivity extends AppCompatActivity {
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int PICK_IMAGE_REQUEST = 2;
    private static final String TAG = "CreateCardActivity";

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

        // get the newly created deck which has no cards yet
        LocalJSONHandler jsonHandler = new LocalJSONHandler(this, Deck.SRC_MODE_INTERNAL_STORAGE);
        newDeck = jsonHandler.getDeck(deckName);

        updateViewFromCard();
    }

    @Override
    public boolean onSupportNavigateUp() {
        // AlertDialog: "deck verwerfen / speichern / abbrechen"
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setTitle("Neues Deck")
                .setNegativeButton("Verwerfen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // delete the deck and then leave
                        LocalJSONHandler handler = new LocalJSONHandler(
                                CreateCardActivity.this, Deck.SRC_MODE_INTERNAL_STORAGE);
                        if (handler.removeDeck(newDeck.getName())) {
                            CreateCardActivity.super.onSupportNavigateUp();
                        } else {
                            Toast.makeText(CreateCardActivity.this,
                                    "Deck konnte nicht entfernt werden", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNeutralButton("Abbrechen", null);

        if (newDeck.getCardList().size() < 2) {
            builder.setMessage("Das Deck hat weniger als 2 Karten. Deck verwerfen?");
        } else {
            builder.setMessage("Deck speichern?");
            builder.setPositiveButton("Speichern", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(saveCard()) {
                        CreateCardActivity.super.onSupportNavigateUp();
                    }
                }
            });
        }

        // display the dialog
        builder.show();

        return false;
    }

    @Override
    public void onBackPressed() {
        onSupportNavigateUp();
    }

    /**
     * called by clicking the "show previous card" button on the left
     * @param view
     */
    public void showPreviousCard(View view) {
        // try to save the current card
        if (saveCard()) {
            if (currentCardIndex > 0){
                // go to the previous card
                currentCardIndex--;
                updateViewFromCard();
            }
        }
    }

    /**
     * called when user is done adding cards and wants to save the deck
     */
    public void saveNewDeck(View view) {
        // try to save the current card
        if (newDeck.getCardList().size() < 2) {
            // not enough cards
            new android.app.AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_warning_black_24dp)
                    .setTitle("Neues Deck")
                    .setMessage("Das Deck hat weniger als 2 Karten. Deck verwerfen?")
                    .setNegativeButton("Verwerfen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // delete the deck and then leave
                            LocalJSONHandler handler = new LocalJSONHandler(
                                    CreateCardActivity.this, Deck.SRC_MODE_INTERNAL_STORAGE);
                            if (handler.removeDeck(newDeck.getName())) {
                                CreateCardActivity.super.onSupportNavigateUp();
                            } else {
                                Toast.makeText(CreateCardActivity.this,
                                        "Deck konnte nicht entfernt werden", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNeutralButton("Abbrechen", null)
                    .show();

        } else if (saveCard()) {
            // TODO redirect to GalleryActivity, but first get rid of the json string
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    /**
     * called by clicking the "add another card (plus)" button on the right
     * @param view
     */
    public void showNextCard(View view) {
        // try to save the current card
        if (saveCard()) {
            // go to the next card
            currentCardIndex++;
            updateViewFromCard();
        }
    }

    public void deleteCurrentCard(View view) {
        // are we on the first card
        if (newDeck.getCardList().size() <= 2) {
            Toast.makeText(this, "Deck muss mindestens zwei Karten enthalten", Toast.LENGTH_SHORT).show();
            return;
        }

        LocalJSONHandler handler = new LocalJSONHandler(this, Deck.SRC_MODE_INTERNAL_STORAGE);
        int cardIndex = handler.getCardIndex(newDeck.getName(), newDeck.getCardList().get(currentCardIndex).getName());

        // if the card is not yet in the json file
        if (cardIndex == -1) {
            // remove card from card list and decrement the current card index
            newDeck.getCardList().remove(currentCardIndex);
            currentCardIndex--;
        }
        // the card is not the last card and has to be deleted
        else {
            // delete the card from the json and delete the pictures
            if(handler.removeCard(newDeck.getName(),
                    newDeck.getCardList().get(currentCardIndex).getName())) {
                // delete card from card list
                newDeck.getCardList().remove(currentCardIndex);
            } else {
                Toast.makeText(
                        this, "Karte konnte nicht entfernt werden", Toast.LENGTH_SHORT).show();
            }

        }

        updateViewFromCard();


    }

    /**
     * saves the current card into the existing deck, i.e.: <br/>
     * - saves the pictures of the card <br/>
     * - replaces the card in the json file<br/>
     * - empties the imageCard and imgDescriptions lists<br/>
     * Also shows error messages if something goes wrong (e.g. card name collision)
     *
     * @return true if the card was saved successfully
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

            if (hasCardNameCollision(cardName)) {
                Toast.makeText(this,
                        "Kartenname ist im Deck schon vorhanden",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            if (cardName.isEmpty()) {
                Toast.makeText(this,
                        "Gib einen Namen an",
                        Toast.LENGTH_SHORT).show();
                return false;
            }

            Card newCard = new Card(cardName, currentCardIndex, imageCards, attributeMap);
            newDeck.getCardList().set(currentCardIndex, newCard);

            Deck storedDeck = jsonHandler.getDeck(newDeck.getName());
            // do we have a new card that is not already stored? and are we on the last card?
            if (storedDeck.getCardList().size() < newDeck.getCardList().size()
                    && currentCardIndex == newDeck.getCardList().size()-1) {
                return jsonHandler.saveCard(newDeck.getName(), newCard);
            } else {
                return jsonHandler.replaceCard(newDeck.getName(), currentCardIndex, newCard);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Karte konnte nicht gespeichert werden", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * changes an existing card: inserts the values from the user input into
     * the card at the specified position
     * @return true if the change was successful, false otherwise (or if the card was not found)
     */
    private boolean saveChangedCurrentCard() {
        LocalJSONHandler jsonHandler = new LocalJSONHandler(this, Deck.SRC_MODE_INTERNAL_STORAGE);
        Deck storedDeck = jsonHandler.getDeck(newDeck.getName());
        // do we have a new card that is not already stored? and are we on the last card?
        if (storedDeck.getCardList().size() < newDeck.getCardList().size()
                && currentCardIndex == newDeck.getCardList().size()-1) {
            boolean test = saveCard();
            return test;
        } else {
            if (jsonHandler.removeCard(newDeck.getName(), currentCardIndex)) {
                return saveCard();
            }
        }

        // failed:
        return false;
    }

    /**
     * updates the view to show the current content of the model
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
        ImageButton btnDelete = (ImageButton) findViewById(R.id.deletCardBtn);

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
            btnLeft.setVisibility(View.INVISIBLE);
        } else {
            btnLeft.setVisibility(View.VISIBLE);
        }

/*
        if (newDeck.getCardList().size() <= 2) {
            btnDelete.setVisibility(View.GONE);
        } else {
            btnDelete.setVisibility(View.VISIBLE);
        }
*/
        // empty the images and descriptions lists
        cardImages = new ArrayList<>();
        imgDescriptions = new ArrayList<>();

        Card card = newDeck.getCardList().get(currentCardIndex);

        for (ImageCard imageCard : card.getImageList()) {
            imgDescriptions.add(imageCard.getDescription());

            Target imageTarget = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    cardImages.add(bitmap);
                    updateImageContainer();
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            String imageUri = imageCard.getUri();
            File imgFile = new File(getFilesDir() + "/" + imageUri);
            Picasso.with(this)
                    .load(imgFile)
                    .resize(300, 300)
                    .centerInside()
                    .onlyScaleDown()
                    .placeholder(R.drawable.menu_image)
                    .into(imageTarget);
        }

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
                new AlertDialog.Builder(CreateCardActivity.this)
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .setTitle("Bild löschen")
                        .setMessage("Wollen Sie das Bild löschen?")
                        .setPositiveButton("Ja", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cardImages.remove(position);
                                imgDescriptions.remove(position);
                                updateImageContainer();
                            }

                        })
                        .setNegativeButton("Nein", null)
                        .show();
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

    /**
     * checks if the specified card name equals any of the card names in the deck
     * @return true if there was a collision, false if the name is safe
     */
    private boolean hasCardNameCollision(String cardName) {
        for(Card c : newDeck.getCardList()) {
            if (cardName.equals(c.getName()) && newDeck.getCardList().indexOf(c) != currentCardIndex) {
                return true;
            }
        }
        return false;
    }
}
