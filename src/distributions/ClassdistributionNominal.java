/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distributions;

import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author Thierry
 */

/**
*diese Klasse wird für die Berechnung der Warscheinlichkeit eine Nominale Variable
* in Abhängigkeit einer bestimmte Klassenwert verwendet 
* Diese Klasse benutz das Interface Distribution
* parameter: Inst: die Trainingsmenge
* classID: der Index der Klassenwert; conditionalprobs:Array zum speichern der bedingte Warscheinlichkeiten
* attindexen: Array zur Speicherung der Attributindexen der Trainingsmenge
* kID:index des Attributs
 */


public class ClassdistributionNominal implements Distribution {

    private Instances inst;

    /** den ClaassenattributIndex*/
    int classID;

    /*das elternid*/
    private int kID;

    /*table von bedigte Wahrscheinlichkeiten*/
    private double[] conditionalprobs;

    /*die attributeindexen*/
    private int[] attindexen;
    
    
/**
Konstruktor 
     * @param inst
     * @param classID
     * @param kID
*/
    public ClassdistributionNominal(Instances inst, int classID, int kID) {
        this.inst = new Instances(inst);
        this.classID = classID;
        this.kID = kID;
        conditionalprobs = new double[inst.attribute(kID).numValues()];
        attindexen = new int[inst.attribute(kID).numValues()];
        for (int i = 0; i < attindexen.length; i++) {
            attindexen[i] = i;
        }
       

    }

    /**
     * Procedur zur Berechnung der Bedingtewahrscheinlichkeiten
     */
    @Override
    
    
   
    public void calculateProbs() {
        for (int i = 0; i < inst.numInstances(); i++) {
            
                for (int k = 0; k < attindexen.length; k++) {
                    
                        if (inst.instance(i).classValue() == classID) {
                            if ((inst.instance(i).value(kID)) == (attindexen[k])) {
                                conditionalprobs[k]++;
                            }
                        }
                    }
                }
            
        

        int count =0;
        for (int j = 0; j < inst.numInstances(); j++) {
           if (inst.instance(j).classValue() == classID) {
                    count++;
                }
            }
        
        for (int i = 0; i < conditionalprobs.length; i++) {
            
                if (count!=0);
                conditionalprobs[i] /= count;
            }
        }
    
    /**
     *
     * @param <Object>
     * @param insta
     * @return
     *  Rückgabe der berechnete Bedingtewahrscheinlichkeiten
     */
    @Override
    
    
   
    public <Object> double getProbs(Object insta) {
         calculateProbs();
        int a = (int) ((Instance) insta).value(kID);
       

        return conditionalprobs[a];

    }

    @Override
    public double[][] numberOfcounts() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

  

   
}