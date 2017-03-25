/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GenetischeAlgorithmen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.uncommons.watchmaker.framework.AbstractEvolutionEngine;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.SelectionStrategy;

/**
 *
 * @author Thierry
 */
public class Geneticsimulation<BayesNetz> extends AbstractEvolutionEngine<BayesNetz> {
    

    private final EvolutionaryOperator<BayesNetz> evolutionScheme;
    private final FitnessEvaluator<? super BayesNetz> fitnessEvaluator;
    private final SelectionStrategy<? super BayesNetz> selectionStrategy;

    /**
     * Creates a new evolution engine by specifying the various components required by
     * a generational evolutionary algorithm.
     * @param candidateFactory Factory used to create the initial population that is
     * iteratively evolved.
     * @param evolutionScheme The combination of evolutionary operators used to evolve
     * the population at each generation.
     * @param fitnessEvaluator A function for assigning fitness scores to candidate
     * solutions.
     * @param selectionStrategy A strategy for selecting which candidates survive to
     * be evolved.
     * @param rng The source of randomness used by all stochastic processes (including
     * evolutionary operators and selection strategies).
     */
    public Geneticsimulation (BayesFactory<BayesNetz> candidateFactory,
                                       EvolutionaryOperator<BayesNetz> evolutionScheme,
                                       FitnessEvaluator<? super BayesNetz> fitnessEvaluator,
                                       SelectionStrategy<? super BayesNetz> selectionStrategy,
                                       Random rng)
    {
        super(candidateFactory, fitnessEvaluator, rng);
        this.evolutionScheme = evolutionScheme;
        this.fitnessEvaluator = fitnessEvaluator;
        this.selectionStrategy = selectionStrategy;
    }


   
    @Override
    
    protected List<EvaluatedCandidate<BayesNetz>> nextEvolutionStep(List<EvaluatedCandidate<BayesNetz>> evaluatedPopulation,
                                                            int eliteCount,
                                                            Random rng)
    {
        List<BayesNetz> population = new ArrayList<BayesNetz>(evaluatedPopulation.size());

        // First perform any elitist selection.
        List<BayesNetz> elite = new ArrayList<BayesNetz>(eliteCount);
        Iterator<EvaluatedCandidate<BayesNetz>> iterator = evaluatedPopulation.iterator();
        while (elite.size() < eliteCount)
        {
            elite.add(iterator.next().getCandidate());
        }
        // Then select candidates that will be operated on to create the evolved
        // portion of the next generation.
        population.addAll(selectionStrategy.select(evaluatedPopulation,
                                                   fitnessEvaluator.isNatural(),
                                                   evaluatedPopulation.size() - eliteCount,
                                                   rng));
        // Then evolve the population.
        population = evolutionScheme.apply(population, rng);
        // When the evolution is finished, add the elite to the population.
        population.addAll(elite);
        return evaluatePopulation(population);
    }

   
}