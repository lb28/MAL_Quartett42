package de.uulm.dbis.quartett42;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import de.uulm.dbis.quartett42.data.Card;
import de.uulm.dbis.quartett42.data.Deck;
import de.uulm.dbis.quartett42.data.ImageCard;
import de.uulm.dbis.quartett42.data.Property;

public class CreateDeckActivity extends AppCompatActivity {
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int PICK_IMAGE_REQUEST = 2;

    private static final String TAG = "CreateDeckActivity";

    // the Deck we are building
    private Deck newDeck;
    // the attribute list
    private ArrayList<Property> deckAttrList = new ArrayList<>();
    // the deck image
    private Bitmap deckImage;

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
            deckImage = BitmapFactory.decodeResource(
                    getResources(), R.drawable.menu_image);
            Log.i(TAG, "onBitmapFailed: ");
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

        // default deck image
        deckImage = BitmapFactory.decodeResource(getResources(), R.drawable.menu_image);
        // add the footer (for adding items to the list)
        footerView = getLayoutInflater().inflate(R.layout.decklist_footer, null, false);
        addDeckAttrListView.addFooterView(footerView);

        // set the adapter
        createDeckItemAdapter = new CreateDeckItemAdapter(this, R.layout.decklist_new_attr, deckAttrList);
        addDeckAttrListView.setAdapter(createDeckItemAdapter);

        // add one property for convenience
        deckAttrList.add(new Property());

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
        // TODO AlertDialog: "deck verwerfen / speichern / abbrechen"
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        onSupportNavigateUp();
    }

    public void clickAddCardsBtn(View view) {
        String deckName = editTextDeckName.getText().toString();
        String deckDescr = editTextDeckDescr.getText().toString();
        String imageUri = deckName+"_deckimage.jpg";

        // get all the properties from the listview
        for (int i = 0; i < deckAttrList.size(); i++) {
            deckAttrList.set(i, createDeckItemAdapter.getItem(i));
        }

        // TODO maybe check for invalid user input

        // save the deck image in internal storage
        FileOutputStream fos;
        try {
            fos = openFileOutput(imageUri, Context.MODE_PRIVATE);
            // Writing the bitmap to the output stream
            deckImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // create ImageCard
        ImageCard deckImageCard = new ImageCard(imageUri, deckDescr);

        // create the deck
        try {
            newDeck = new Deck(
                    deckName,
                    deckImageCard,
                    deckAttrList,
                    new ArrayList<Card>(),
                    Deck.SRC_MODE_INTERNAL_STORAGE);


            // test
            System.out.println(newDeck.toJSON().toString(4));
            LocalJSONHandler localJSONHandler =
                    new LocalJSONHandler(this, Deck.SRC_MODE_INTERNAL_STORAGE);
            localJSONHandler.saveDeck(newDeck);

            // TODO go to AddCardsActivity (send deck name in intent)

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Eingaben sind ungültig!", Toast.LENGTH_SHORT).show();
        }

    }

    public void addDeckAttribute(View view) {
        deckAttrList.add(new Property());
        createDeckItemAdapter.notifyDataSetChanged();
    }

    public  void changeDecPic(View view) {
        String[] options = {"Take Photo", "Choose From Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Thumbnail")
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
}
