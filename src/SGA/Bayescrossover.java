/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SGA;

import Netze.BayesNetz;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.watchmaker.framework.operators.AbstractCrossover;

/**
 *
 * @author Thierry
 */
public class Bayescrossover extends AbstractCrossover<BayesNetz> {
    
   
   int[] mat1;
   int[] mat2;

    public Bayescrossover() {
        super(1);
    }

    @Override
    protected List<BayesNetz> mate(BayesNetz parent1,
            BayesNetz parent2,
            int numberOfCrossoverPoints,
            Random rng) {
        
        //p2=new BayesNetz(parent1);
        //p2=new BayesNetz(parent2);
        mat1=new int[parent1.getGraph().getLinearrepresentation().length];
        mat2=new int[parent2.getGraph().getLinearrepresentation().length];
        mat1=parent1.getGraph().getLinearrepresentation();
        
        mat2=parent2.getGraph().getLinearrepresentation();
       
        
        
        
        if (mat1.length != mat2.length) {
            throw new IllegalArgumentException("Cannot perform cross-over with different length parents.");
        }
        int[] offspring1 = new int[mat1.length];
        System.arraycopy(mat1, 0, offspring1, 0, mat1.length);
        int[] offspring2 = new int[mat2.length];
        System.arraycopy(mat2, 0, offspring2, 0, mat2.length);
        // Apply as many cross-overs as required.
        int[] temp = new int[mat1.length];
        for (int i = 0; i < numberOfCrossoverPoints; i++) {
            // Cross-over index is always greater than zero and less than
            // the length of the parent so that we always pick a point that
            // will result in a meaningful cross-over.
            int crossoverIndex = (1 + rng.nextInt(mat1.length - 1));
            System.arraycopy(offspring1, 0, temp, 0, crossoverIndex);
            System.arraycopy(offspring2, 0, offspring1, 0, crossoverIndex);
            System.arraycopy(temp, 0, offspring2, 0, crossoverIndex);
        }
        List<BayesNetz> result = new ArrayList<BayesNetz>(2);
        parent1.setMatrix(parent1.getGraph().restructruriereGraph(offspring1));
            parent2.setMatrix(parent2.getGraph().restructruriereGraph(offspring2));
        result.add(parent1);
        result.add(parent2);
        return result;
    }

    

  
}
