/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classifiers;

import weka.core.*;

/**
 *
 * @author Thierry
 */
public interface Classifiers {

    public abstract void train(Instances inst) throws Exception;

    public abstract double[][] test(Instances inst);
     public int[] EliminiereDopelt(int[] tab) ;

    public abstract void BewertunginProzent() throws Exception;

    public abstract boolean Classify(Instance inst);

    public abstract void Bootstrap(Instances inst);

    public abstract void BauClassifier();
    public void bootstrapvalidierungsmenge(Instances inst);

}
