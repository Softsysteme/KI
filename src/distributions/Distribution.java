/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Thierry
 */

/* Interface zur Berechnung der Parameters des Model  die wird durch die Klassen NumericDistribution( für Numerische Attributen)
 NominalDistribution(für Nominale oder String Attributen) und ClassDistribuion(für KlassenAttributen) */
package distributions;

public interface Distribution {

    /* gibt die Wahrscheinlichkeit eines Attributen zurück */
    public <Object> double getProbs(Object t);

    public void calculateProbs();
// public <T> double getProbs(T t);
    public double[][] numberOfcounts();

}
