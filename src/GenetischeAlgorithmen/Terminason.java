/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GenetischeAlgorithmen;

import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.TerminationCondition;

/**
 *
 * @author Thierry
 */
public class Terminason implements TerminationCondition{
    
    
    
     private final int generationCount;

    /**
     * @param generationCount The maximum number of generations that the
     * evolutionary algorithm will permit before terminating.
     */
    public Terminason(int generationCount)
    {
        if (generationCount <= 0)
        {
            throw new IllegalArgumentException("Generation count must be positive.");
        }
        this.generationCount = generationCount;
    }

    /**
     * {@inheritDoc}
     */
     @Override
    public boolean shouldTerminate(PopulationData<?> populationData)
    {
        return populationData.getGenerationNumber() + 1 >= generationCount;
    }
}

    

