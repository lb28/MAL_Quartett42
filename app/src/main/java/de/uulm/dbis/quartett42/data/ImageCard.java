package de.uulm.dbis.quartett42.data;

/**
 * Created by Fischbach on 21.12.2016.
 */

/** Nicht Image wegen Namensgleichheit der Java Klasse Image
 *
 */
public class ImageCard {
    /**
     * Bildname (irgendwas.jpg)
     */
    private String uri; //String oder URI?

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
                ", description='" + description + '\'' +
                '}';
    }
}
