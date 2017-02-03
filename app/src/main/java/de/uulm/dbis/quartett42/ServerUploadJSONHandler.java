package de.uulm.dbis.quartett42;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import de.uulm.dbis.quartett42.data.Card;
import de.uulm.dbis.quartett42.data.Deck;
import de.uulm.dbis.quartett42.data.ImageCard;
import de.uulm.dbis.quartett42.data.Property;

/**
 * Created by Tim on 25.01.17.
 */

public class ServerUploadJSONHandler {

private Context context;

    public ServerUploadJSONHandler(Context context){
        this.context = context;
    }

    //TODO Klasse kann wahrscheinlich gelöscht werden...

    /*
    public String getPostDataString(JSONObject params) throws Exception{
        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key = itr.next();
            Object value = params.get(key);

            if (first) {
                first = false;
            } else{
                result.append("&");
            }

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }

        Log.i("result.toString", result.toString());
        return result.toString();
    }

    protected String openImageInAssets(String imageName){
        String encodedImageBase64 = "";
        AssetManager assetManager = context.getAssets();
        InputStream fileStream = null;
        try {
            fileStream = assetManager.open(imageName);
            if(fileStream != null){
                //                  BitmapFactory.Options bfo = new BitmapFactory.Options();
                //                  bfo.inPreferredConfig = Bitmap.Config.ARGB_8888;
                //                  Bitmap bitmap = BitmapFactory.decodeStream(fileStream, null, bfo);

                Bitmap bitmap = BitmapFactory.decodeStream(fileStream);
                // Convert bitmap to Base64 encoded image for web
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                // to get image extension file name split the received
                int fileExtensionPosition = imageName.lastIndexOf('.');
                String fileExtension = imageName.substring(fileExtensionPosition+1);
                //                  Log.d(IConstants.TAG,"fileExtension: " + fileExtension);

                if(fileExtension.equalsIgnoreCase("png")){
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    //                      Log.d(IConstants.TAG,"fileExtension is PNG");
                }else if(fileExtension.equalsIgnoreCase("jpg") || fileExtension.equalsIgnoreCase("jpeg")){
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    //                      Log.d(TAG,"fileExtension is JPG");
                }

                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String imgageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                encodedImageBase64 = "data:image/png;base64," + imgageBase64;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return encodedImageBase64="";
        }
        finally {
            //Always clear and close
            try {
                if(fileStream != null) {
                    fileStream.close();
                    fileStream = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//      Log.d(TAG,"encodedImageBase64: " + encodedImageBase64);
        return encodedImageBase64;
    }

    */

}
