package de.uulm.dbis.quartett42.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

/**
 * Created by Fischbach on 21.12.2016.
 */

public class Game {
    private Deck deck;

    /** Schwierigkeitsstufe: 1 = leicht, 2 = mittel, 3 = Profi
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

    /** Speichert alle IDs in der Reihenfolge, wie sie der Player auf seinem Stapel hat,
     * wobei an der Stelle 0 die "oberste Karte"/aktuelle Karte liegt
     *
     */
    private ArrayList<Integer> cardsPlayer;

    /** Speichert alle IDs in der Reihenfolge, wie sie der Computer auf seinem Stapel hat
     * wobei an der Stelle 0 die "oberste Karte"/aktuelle Karte liegt
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
            if(r.nextBoolean() && (cardsPlayer.size() < cardNumbers)){
                cardsPlayer.add(i);
            }else if(cardsComputer.size() < cardNumbers){
                cardsComputer.add(i);
            }else if(cardsPlayer.size() < cardNumbers){
                cardsPlayer.add(i);
            }
        }
        Collections.shuffle(cardsPlayer);
        Collections.shuffle(cardsComputer);

    }


    /** Methode, die den Zug fuer den Computer, je nach Schwierigkeitsgrad ausfuehrt.
     *
     * @return choosen Property Name (Key) of Compputer
     */
    public String computerCardChoice(){
        String chosenAttribute = "";
        Double chosenValue = 0.0;
        Card tmpCard = returnCardOfID(cardsComputer.get(0));
        int numberOfProperties = averageValues.size();
        Set<String> propertySet = averageValues.keySet();
        String[] propertyArray = propertySet.toArray(new String[numberOfProperties]);
        if(difficulty == 3){
            //Profi: Alle Werte durchlaufen
            //TODO umdrehen fuer Insane-Modus
            for(int i = 0; i < numberOfProperties; i++){
                boolean tmpMaxwinner = true;
                for(Property px : deck.getPropertyList()){
                    if(px.getName().equals(propertyArray[i])){
                        tmpMaxwinner = px.getMaxwinner();
                    }
                }
                if(tmpMaxwinner){
                    if((tmpCard.getAttributeMap().get(propertyArray[i]) / averageValues.get(propertyArray[i])) > chosenValue){
                        chosenValue = tmpCard.getAttributeMap().get(propertyArray[i])/averageValues.get(propertyArray[i]);
                        chosenAttribute = propertyArray[i];
                    }
                }else{
                    if((averageValues.get(propertyArray[i]) / tmpCard.getAttributeMap().get(propertyArray[i])) > chosenValue){
                        chosenValue = averageValues.get(propertyArray[i])/tmpCard.getAttributeMap().get(propertyArray[i]);
                        chosenAttribute = propertyArray[i];
                    }
                }
                System.out.println(chosenAttribute+": "+chosenValue);
            }
        }else if(difficulty == 2){
            //Mittel: Aus der Haelfte aller Werte zufaellige welche auswaehlen und vergleichen
            for(int i = 0; i < numberOfProperties+2; i++){
                int randomProperty = r.nextInt(numberOfProperties);
                boolean tmpMaxwinner = true;
                for(Property px : deck.getPropertyList()){
                    if(px.getName().equals(propertyArray[randomProperty])){
                        tmpMaxwinner = px.getMaxwinner();
                    }
                }
                if(tmpMaxwinner){
                    if((tmpCard.getAttributeMap().get(propertyArray[randomProperty]) / averageValues.get(propertyArray[randomProperty])) > chosenValue){
                        chosenValue = tmpCard.getAttributeMap().get(propertyArray[randomProperty])/averageValues.get(propertyArray[randomProperty]);
                        chosenAttribute = propertyArray[randomProperty];
                    }
                }else{
                    if((averageValues.get(propertyArray[randomProperty]) / tmpCard.getAttributeMap().get(propertyArray[randomProperty])) > chosenValue){
                        chosenValue = averageValues.get(propertyArray[randomProperty])/tmpCard.getAttributeMap().get(propertyArray[randomProperty]);
                        chosenAttribute = propertyArray[randomProperty];
                    }
                }
            }
        }else{
            //Leicht: Zufaelliger Wert auswaehlen:
            int randomProperty = r.nextInt(numberOfProperties);
            chosenAttribute = propertyArray[randomProperty];
        }

        return chosenAttribute;
    }

    /** Methode behandelt das Spielen zweier Karten: Sie bestimmt den Gewinner, zahelt die Punkte hoch
     * und die verbleibenden runter, packt Karten auf den richtigen Stapel
     * und bestimmt ggf. das Ende
     *
     * @param chosenAttribute
     * @return int winner: //0 = unentschieden, 1 = Spieler gewinnt, 2 = Computer gewinnt
     */
    public int playCards(String chosenAttribute){
        int winner = -1; //0 = unentschieden, 1 = Spieler gewinnt, 2 = Computer gewinnt
        Card cardPlayer = returnCardOfID(cardsPlayer.get(0));
        Card cardComputer = returnCardOfID(cardsComputer.get(0));
        boolean tmpMaxwinner = true;
        for(Property px : deck.getPropertyList()){
            if(px.getName().equals(chosenAttribute)){
                tmpMaxwinner = px.getMaxwinner();
            }
        }
        //Gewinner bestimmen: TODO: umdrehen fuer Insane-Modus
        if (tmpMaxwinner) {
            if(cardPlayer.getAttributeMap().get(chosenAttribute) > cardComputer.getAttributeMap().get(chosenAttribute)){
                //Player gewinnt:
                winner = 1;
                nextPlayer = true;
                //Punkte behandlen:
                pointsPlayer = pointsPlayer + calculatePoints(chosenAttribute, winner);
                //Bei runden- oder punkte-basiert die Azahl runter zaehlen
                if(mode == 1 || mode == 3){
                    roundsLeft = roundsLeft - calculatePoints(chosenAttribute, winner);
                }
                //Beide Karten vorne wegnehmen und hinten auf den Stapel des Players legen
                cardsPlayer.remove(0);
                cardsComputer.remove(0);
                cardsPlayer.add(cardComputer.getId());
                cardsPlayer.add(cardPlayer.getId());
            }else if(cardPlayer.getAttributeMap().get(chosenAttribute) < cardComputer.getAttributeMap().get(chosenAttribute)){
                //Computer gewinnt:
                //Player gewinnt:
                winner = 2;
                nextPlayer = false;
                //Punkte behandeln:
                pointsComputer = pointsComputer + calculatePoints(chosenAttribute, winner);
                //Bei runden- oder punkte-basiert die Azahl runter zaehlen
                if(mode == 1 || mode == 3){
                    roundsLeft = roundsLeft - calculatePoints(chosenAttribute, winner);
                }
                //Beide Karten vorne wegnehmen und hinten auf den Stapel des Computers legen
                cardsPlayer.remove(0);
                cardsComputer.remove(0);
                cardsComputer.add(cardPlayer.getId());
                cardsComputer.add(cardComputer.getId());
            }else{
                //unentschieden:
                winner = 0;
                //Jeder haengt seine Karte hinten an:
                cardsPlayer.remove(0);
                cardsComputer.remove(0);
                cardsPlayer.add(cardPlayer.getId());
                cardsComputer.add(cardComputer.getId());
            }
        }else{
            if(cardPlayer.getAttributeMap().get(chosenAttribute) < cardComputer.getAttributeMap().get(chosenAttribute)){
                //Player gewinnt:
                winner = 1;
                nextPlayer = true;
                //Punkte behandlen:
                pointsPlayer = pointsPlayer + calculatePoints(chosenAttribute, winner);
                //Bei runden- oder punkte-basiert die Azahl runter zaehlen
                if(mode == 1 || mode == 3){
                    roundsLeft = roundsLeft - calculatePoints(chosenAttribute, winner);
                }
                //Beide Karten vorne wegnehmen und hinten auf den Stapel des Players legen
                cardsPlayer.remove(0);
                cardsComputer.remove(0);
                cardsPlayer.add(cardComputer.getId());
                cardsPlayer.add(cardPlayer.getId());
            }else if(cardPlayer.getAttributeMap().get(chosenAttribute) > cardComputer.getAttributeMap().get(chosenAttribute)){
                //Computer gewinnt:
                //Player gewinnt:
                winner = 2;
                nextPlayer = false;
                //Punkte behandeln:
                pointsComputer = pointsComputer + calculatePoints(chosenAttribute, winner);
                //Bei runden- oder punkte-basiert die Azahl runter zaehlen
                if(mode == 1 || mode == 3){
                    roundsLeft = roundsLeft - calculatePoints(chosenAttribute, winner);
                }
                //Beide Karten vorne wegnehmen und hinten auf den Stapel des Computers legen
                cardsPlayer.remove(0);
                cardsComputer.remove(0);
                cardsComputer.add(cardPlayer.getId());
                cardsComputer.add(cardComputer.getId());
            }else{
                //unentschieden:
                winner = 0;
                //Jeder haengt seine Karte hinten an:
                cardsPlayer.remove(0);
                cardsComputer.remove(0);
                cardsPlayer.add(cardPlayer.getId());
                cardsComputer.add(cardComputer.getId());
            }
        }

        //Ende?:
        if(roundsLeft <= 0 && (mode == 1 || mode == 3)){
            gameOver = true;
        }

        return winner;
    }

    /** Methode berechnet Punkte, die der Gewinner bekommt
     *
     * @param chosenAttribute
     * @param winner
     * @return int points
     */
    private int calculatePoints(String chosenAttribute, int winner){
        if(mode == 1 || mode == 2){
            //Falls Runden oder Zeitbasiert, einfach 1 zurueck geben:
            return 1;
        }else{
            //Bei Punktebasiert irgendeinen fancy Algorithmus entwickeln, der Punkte aufsummiert
            //bisher als Erastz: Differenz beider Punkte mal 10
            Card cardPlayer = returnCardOfID(cardsPlayer.get(0));
            Card cardComputer = returnCardOfID(cardsComputer.get(0));
            if(cardPlayer.getAttributeMap().get(chosenAttribute) > cardComputer.getAttributeMap().get(chosenAttribute)){
                return (int) Math.round((cardPlayer.getAttributeMap().get(chosenAttribute) / cardComputer.getAttributeMap().get(chosenAttribute))*10);
            }else{
                return (int) Math.round((cardComputer.getAttributeMap().get(chosenAttribute) / cardPlayer.getAttributeMap().get(chosenAttribute))*10);
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
