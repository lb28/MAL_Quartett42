package de.uulm.dbis.quartett42;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import de.uulm.dbis.quartett42.data.Card;
import de.uulm.dbis.quartett42.data.Deck;
import de.uulm.dbis.quartett42.data.ImageCard;
import de.uulm.dbis.quartett42.data.Property;

import static de.uulm.dbis.quartett42.MainActivity.PERMISSION_REQUEST_READ_EXTERNAL_STORAGE;

public class CreateDeckActivity extends AppCompatActivity {
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int PICK_IMAGE_REQUEST = 2;

    private static final String TAG = "CreateDeckActivity";

    // the Deck we are building
    private Deck newDeck;

    // the attribute list
    private ArrayList<Property> deckAttrList;
    // the deck image
    private Bitmap deckImage;

    // for copying an existing Deck
    private String copyFromDeckName;

    // the views
    private EditText editTextDeckName;
    private EditText editTextDeckDescr;
    private ImageView deckImgBtn;
    private ListView addDeckAttrListView;
    private View footerView;

    private CreateDeckItemAdapter createDeckItemAdapter;

    // the target for the picasso loader
    private Target deckImgBtnTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            deckImage = bitmap;
            deckImgBtn.setImageBitmap(deckImage);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Log.w(TAG, "onBitmapFailed: Bitmap could not be loaded");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_deck);

        // get all the views
        addDeckAttrListView = (ListView) findViewById(R.id.addDeckAttrListView);
        editTextDeckName = (EditText) findViewById(R.id.editTextDeckName);
        editTextDeckDescr = (EditText) findViewById(R.id.editTextDeckDescr);
        deckImgBtn = (ImageView) findViewById(R.id.deckImgBtn);

        // if we were given a deck name, it means we are going to change an existing deck
        copyFromDeckName = getIntent().getStringExtra("deckName");
        if (copyFromDeckName != null) {
            // existing deck (from internal storage only)
            LocalJSONHandler localJSONHandler =
                    new LocalJSONHandler(this, Deck.SRC_MODE_INTERNAL_STORAGE);
            newDeck = localJSONHandler.getDeck(copyFromDeckName);
            // fill the activity with the decks data
            deckAttrList = newDeck.getPropertyList();
            editTextDeckName.setText(newDeck.getName() + " - Kopie");
            editTextDeckDescr.setText(newDeck.getImage().getDescription());
            File imgFile = new File(getFilesDir() + "/" + newDeck.getImage().getUri());
            // load image into imageBtn
            Picasso.with(this)
                    .load(imgFile)
                    .resize(500,500)
                    .centerInside()
                    .into(deckImgBtnTarget);
        } else {
            // new deck
            deckAttrList = new ArrayList<>();
            // add one property for convenience
            deckAttrList.add(new Property());
        }

        // add the footer (for adding items to the list)
        footerView = getLayoutInflater().inflate(R.layout.add_attribute_footer, null, false);
        addDeckAttrListView.addFooterView(footerView);

        // set the adapter
        createDeckItemAdapter = new CreateDeckItemAdapter(this, R.layout.create_deck_attr, deckAttrList);
        addDeckAttrListView.setAdapter(createDeckItemAdapter);

        // change the color of the add button when the list changes
        createDeckItemAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                int position = deckAttrList.size(); // footer is always after the last list element
                footerView.setBackgroundColor(position % 2 == 0 ?
                                Color.argb(50, 150, 200, 210) : Color.argb(50, 60, 160, 190));
                super.onChanged();
            }
        });

        createDeckItemAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        // AlertDialog: "deck verwerfen / speichern / abbrechen"
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setTitle("Deck verwerfen")
                .setMessage("Das Deck hat noch keine Karten. Deck verwerfen?")
                .setPositiveButton("Verwerfen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // leave without saving
                        CreateDeckActivity.super.onSupportNavigateUp();
                    }
                })
                .setNeutralButton("Abbrechen", null)
                .show();
        return false;
    }

    @Override
    public void onBackPressed() {
        onSupportNavigateUp();
    }

    public void clickAddCardsBtn(View view) {
        String newDeckName = editTextDeckName.getText().toString();
        if (saveDeck(newDeckName)) {
            // go to EditCardsActivity (send deck name in intent)
            Intent intent = new Intent(this, EditCardsActivity.class);
            intent.putExtra("deckName", newDeckName);
            intent.putExtra("copyFromDeckName", copyFromDeckName);
            intent.putExtra("editMode", "createDeck");
            startActivity(intent);
        }
    }

    private boolean saveDeck(String newDeckName) {
        String deckDescr = editTextDeckDescr.getText().toString();

        // get all the properties from the listview
        for (int i = 0; i < deckAttrList.size(); i++) {
            deckAttrList.set(i, createDeckItemAdapter.getItem(i));
        }

        // check for invalid input (name collision, etc...)
        LocalJSONHandler handler = new LocalJSONHandler(this, Deck.SRC_MODE_INTERNAL_STORAGE);
        if (handler.hasDeckNameCollision(newDeckName)) {
            Toast.makeText(this,
                    "Ein Deck mit Namen \"" + newDeckName + "\" ist schon vorhanden",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (newDeckName.isEmpty()) {
            Toast.makeText(this,
                    "Deckname darf nicht leer sein",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (deckAttrList.isEmpty()) {
            Toast.makeText(this,
                    "Gib mindestens ein Attribut an",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (hasEmptyName(deckAttrList)) {
            Toast.makeText(this,
                    "Jedes Attribut muss einen Namen haben",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (hasDuplicates(deckAttrList)) {
            Toast.makeText(this,
                    "Die Attribute müssen eindeutige Namen haben",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        // TODO maybe check for further invalid user input


        String imageUri = newDeckName+"_deckimage.jpg";

        // create the ImageCard (this saves the new picture)
        LocalJSONHandler jsonHandler = new LocalJSONHandler(this, Deck.SRC_MODE_INTERNAL_STORAGE);
        ImageCard deckImageCard = jsonHandler.createImageCard(imageUri, deckImage, deckDescr);

        // create the deck
        try {
            newDeck = new Deck(
                    newDeckName,
                    deckImageCard,
                    deckAttrList,
                    new ArrayList<Card>(),
                    Deck.SRC_MODE_INTERNAL_STORAGE);

            // save the deck json (with empty card list)
            LocalJSONHandler localJSONHandler =
                    new LocalJSONHandler(this, Deck.SRC_MODE_INTERNAL_STORAGE);
            localJSONHandler.saveDeck(newDeck);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Eingaben sind ungültig!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    public void addAttribute(View view) {
        deckAttrList.add(new Property());
        createDeckItemAdapter.notifyDataSetChanged();
    }

    public void changeDecPic(View view) {
        String[] options = {"Foto aufnehmen", "Aus Galerie wählen"};

        AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Bild ändern")
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
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user
                Toast.makeText(this, "Berechtigung zum Lesen der Bilder benötigt",
                        Toast.LENGTH_SHORT).show();

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);

            }

            return;
        }

        Intent intent;
        intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                // update the field and the view

                Bundle extras = data.getExtras();
                deckImage = (Bitmap) extras.get("data");
                deckImgBtn.setImageBitmap(deckImage);
            }
        } else if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();

                // load image into imageBtn
                Picasso.with(this)
                        .load(uri)
                        .resize(500,500)
                        .centerInside()
                        .into(deckImgBtnTarget);
            }
        }
    }

    private boolean hasDuplicates(ArrayList<Property> deckAttrList) {
        HashSet<String> attrNames = new HashSet<>();
        for (Property p : deckAttrList) {
            attrNames.add(p.getName());
        }
        return attrNames.size() < deckAttrList.size();
    }

    private boolean hasEmptyName(ArrayList<Property> deckAttrList) {
        for (Property p : deckAttrList) {
            if (p.getName().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MainActivity.PERMISSION_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    Intent intent;
                    intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");

                    startActivityForResult(intent, PICK_IMAGE_REQUEST);


                }
            }
        }
    }
}
