package de.uulm.dbis.quartett42.data;

/**
 * Created by Fischbach on 21.12.2016.
 */

import org.json.JSONException;
import org.json.JSONObject;

/** Nicht Image wegen Namensgleichheit der Java Klasse Image
 *
 */
public class ImageCard {
    /**
     * Bildname (irgendwas.jpg)
     * (Used for local URI aswell as online URL)
     */
    private String uri;
    private String description;

    /** Konstruktor
     *
     * @param uri
     * @param description
     */
    public ImageCard(String uri, String description) {
        this.uri = uri;
        this.description = description;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /** Zum Testen
     *
     * @return String
     */
    @Override
    public String toString() {
        return "ImageCard{" +
                "uri='" + uri + '\'' +
                ", description='" + description + "\'}";
    }

    public JSONObject toJSON() {
        JSONObject image = new JSONObject();
        try {
            image.put("description", description);
            image.put("URI", uri);
            return image;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
