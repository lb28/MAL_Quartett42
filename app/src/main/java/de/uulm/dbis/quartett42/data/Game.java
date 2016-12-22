package de.uulm.dbis.quartett42.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Fischbach on 21.12.2016.
 */

public class Game {
    private Deck deck;

    /** Schwierigkeitsstufe
     *
     */
    private int difficulty;

    /** Spielmodus: 1 = rundenbasiert, 2 = zeitbasiert, 3 = punktebasiert
     *
     */
    private int mode;

    /** true, falls im Insanemodus gespielt werden soll
     *
     */
    private boolean insaneModus = false;

    /** Atrribut kann fuer verbleibende Runden bzw. Zeit bzw. Punkte genutzt werden
     *
     */
    private int roundsLeft;

    /** Anzahl Punkte oder Karten des Spielers
     *
     */
    private int pointsPlayer;

    /** Anzahl Punkte oder Karten des Computers
     *
     */
    private int pointsComputer;

    /** true, falls Player als naechstes Karte auswaehlen darf, false falls Computer
     *
     */
    private boolean nextPlayer;

    /** Speichert alle IDs in der Reihenfolge, wie sie der Player auf seinem Stapel hat
     *
     */
    private ArrayList<Integer> cardsPlayer;

    /** Speichert alle IDs in der Reihenfolge, wie sie der Computer auf seinem Stapel hat
     *
     */
    private ArrayList<Integer> cardsComputer;

    /** HashMap speichert als Key-Value Paare <Name, Wert> die Durchschnittswerte fuer alle Attribute
     *
     */
    private HashMap<String, Double> averageValues;

    /** Wird true, wenn Spiel zu Ende
     *
     */
    private Boolean gameOver = false;

    /** Konstruktor fuer Spielstart
     *
     * @param deck
     * @param difficulty
     * @param mode
     * @param insaneModus
     * @param roundsLeft
     */
    public Game(Deck deck, int difficulty, int mode, boolean insaneModus, int roundsLeft) {
        this.deck = deck;
        this.difficulty = difficulty;
        this.mode = mode;
        this.insaneModus = insaneModus;
        this.roundsLeft = roundsLeft;
        this.pointsPlayer = 0;
        this.pointsComputer = 0;
        this.nextPlayer = r.nextBoolean();
        this.averageValues = returnAverage();
        this.cardsPlayer = new ArrayList<Integer>();
        this.cardsComputer = new ArrayList<Integer>();
        shuffleCards();
    }

    /** Konstruktor, falls Spiel pausiert wurde und fortgefuehrt werden muss:
     *
     * @param deck
     * @param difficulty
     * @param mode
     * @param insaneModus
     * @param roundsLeft
     * @param pointsPlayer
     * @param pointsComputer
     * @param nextPlayer
     * @param cardsPlayer
     * @param cardsComputer
     */
    public Game(Deck deck, int difficulty, int mode, boolean insaneModus, int roundsLeft,
                int pointsPlayer, int pointsComputer, boolean nextPlayer,
                ArrayList<Integer> cardsPlayer, ArrayList<Integer> cardsComputer) {
        this.deck = deck;
        this.difficulty = difficulty;
        this.mode = mode;
        this.insaneModus = insaneModus;
        this.roundsLeft = roundsLeft;
        this.pointsPlayer = pointsPlayer;
        this.pointsComputer = pointsComputer;
        this.nextPlayer = nextPlayer;
        this.cardsPlayer = cardsPlayer;
        this.cardsComputer = cardsComputer;
        this.averageValues = returnAverage();
    }


    //Alle Game-Methoden
    private Random r = new Random();

    /** Methode ermittelt alle Durchschnittswerte und speichert sie ab
     *
     * @return HashMap<String, Double> mit allen average-Werten
     */
    private HashMap<String, Double> returnAverage(){
        HashMap<String, Double> tmpMap = new HashMap<String, Double>();
        for(Property tmpProperty : deck.getPropertyList()){
            String propertyName = tmpProperty.getName();
            Double propertyValue = 0.0;
            for(Card tmpCard : deck.getCardList()){
                propertyValue = propertyValue + tmpCard.getAttributeMap().get(propertyName);
            }
            propertyValue = propertyValue/deck.getCardList().size();
            tmpMap.put(propertyName, propertyValue);
        }
        return tmpMap;
    }

    /** Methode verteilt die Karten zufaellig auf Spieler und Computer
     *
     */
    private void shuffleCards(){
        int cardNumbers = deck.getCardList().size()/2;
        for(int i = 0; i < deck.getCardList().size(); i++){
            if(r.nextBoolean() && cardsPlayer.size() <= cardNumbers){
                cardsPlayer.add(i);
            }else if(cardsComputer.size() <= cardNumbers){
                cardsComputer.add(i);
            }else if(cardsPlayer.size() <= cardNumbers){
                cardsPlayer.add(i);
            }
        }

    }

    /** Gibt zu einer Karten ID die passende Karte zurueck
     *
     * @param cardID
     * @return Card with given ID
     */
    public Card returnCardOfID(int cardID){
        Card resCard = null;
        for(int i = 0; i < deck.getCardList().size(); i++){
            if(deck.getCardList().get(i).getId() == cardID){
                resCard = deck.getCardList().get(i);
                i = deck.getCardList().size();
            }
        }
        return resCard;
    }

    //TODO: Methode, die Vergleich von zwei gewaehlten Attributen der obersten Karte durchfuehrt, Gewinner bestimmt, abhaengige Attribute anpasst und ggf. Spielende ermittelt

    //TODO: Methode, die Computer in Abhaengigkeit vom Schwierigkeitsgrad und gegebener Karte ein Attribut waehlen laesst



    //alle Getter und Setter:

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public boolean isInsaneModus() {
        return insaneModus;
    }

    public void setInsaneModus(boolean insaneModus) {
        this.insaneModus = insaneModus;
    }

    public int getRoundsLeft() {
        return roundsLeft;
    }

    public void setRoundsLeft(int roundsLeft) {
        this.roundsLeft = roundsLeft;
    }

    public int getPointsPlayer() {
        return pointsPlayer;
    }

    public void setPointsPlayer(int pointsPlayer) {
        this.pointsPlayer = pointsPlayer;
    }

    public int getPointsComputer() {
        return pointsComputer;
    }

    public void setPointsComputer(int pointsComputer) {
        this.pointsComputer = pointsComputer;
    }

    public boolean isNextPlayer() {
        return nextPlayer;
    }

    public void setNextPlayer(boolean nextPlayer) {
        this.nextPlayer = nextPlayer;
    }

    public ArrayList<Integer> getCardsPlayer() {
        return cardsPlayer;
    }

    public void setCardsPlayer(ArrayList<Integer> cardsPlayer) {
        this.cardsPlayer = cardsPlayer;
    }

    public ArrayList<Integer> getCardsComputer() {
        return cardsComputer;
    }

    public void setCardsComputer(ArrayList<Integer> cardsComputer) {
        this.cardsComputer = cardsComputer;
    }

    public HashMap<String, Double> getAverageValues() {
        return averageValues;
    }

    public Boolean getGameOver() {
        return gameOver;
    }

    public void setGameOver(Boolean gameOver) {
        this.gameOver = gameOver;
    }

    /**
     * Zum Testen
     *
     * @return
     */
    @Override
    public String toString() {
        return "Game{" +
                "deck=" + deck.toString() +
                ", difficulty=" + difficulty +
                ", mode=" + mode +
                ", insaneModus=" + insaneModus +
                ", roundsLeft=" + roundsLeft +
                ", pointsPlayer=" + pointsPlayer +
                ", pointsComputer=" + pointsComputer +
                ", nextPlayer=" + nextPlayer +
                ", cardsPlayer=" + cardsPlayer.toString() +
                ", cardsComputer=" + cardsComputer.toString() +
                ", averageValues=" + averageValues.toString() +
                ", gameOver=" +gameOver +
                '}';
    }
}
