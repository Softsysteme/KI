/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distributions;

import weka.core.*;

/**
 *
 * @author Thierry
 */
public interface Classdistributions {

    public double getProbs(Instance inst, int i);

   
}
