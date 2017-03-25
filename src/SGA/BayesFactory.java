/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SGA;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

/**
 *
 * @author Thierry
 */
public class BayesFactory<BayesNetz> extends  AbstractCandidateFactory<BayesNetz>{
    
 
 
 
 
 @Override
 public List<BayesNetz> generateInitialPopulation(int size, Collection<BayesNetz> pop, Random rng)
 {
    
      return super.generateInitialPopulation(size, pop, rng);
 
 
 }

    @Override
    public List<BayesNetz> generateInitialPopulation(int populationSize, Random rng) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   

    @Override
    public BayesNetz generateRandomCandidate(Random rng) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
 

    
}
