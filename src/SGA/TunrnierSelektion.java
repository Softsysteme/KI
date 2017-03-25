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
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.SelectionStrategy;

/**
 *
 * @author Thierry
 */
public class TunrnierSelektion implements SelectionStrategy<BayesNetz> {

    private String description = "Tournament Selection";

    public TunrnierSelektion(Probability selectionProbability) {

        if (selectionProbability.doubleValue() <= 0.5) {
            throw new IllegalArgumentException("Selection threshold must be greater than 0.5.");
        }
        this.description = "Tournament Selection (p = " + selectionProbability.toString() + ')';

    }

    public <BayesNetz> List<BayesNetz> select(List<EvaluatedCandidate<BayesNetz>> population, int selectionSize) {
        List<BayesNetz> selection = new ArrayList<BayesNetz>(selectionSize);
        for (int i = 0; i < selectionSize; i++) {

            // Pick two candidates at random.
            EvaluatedCandidate<BayesNetz> candidate1 = population.get((int) ((Math.random() + 1) * population.size()));
            EvaluatedCandidate<BayesNetz> candidate2 = population.get((int) ((Math.random() + 1) * population.size()));
            selection.add(candidate2.getFitness() > candidate1.getFitness()
                    ? candidate2.getCandidate()
                    : candidate1.getCandidate());
        }

        return selection;
    }

    @Override
    public String toString() {
        return description;
    }

    @Override
    public <S extends BayesNetz> List<S> select(List<EvaluatedCandidate<S>> population, boolean naturalFitnessScores, int selectionSize, Random rng) {

        List<S> selection = new ArrayList<S>(selectionSize);
        for (int i = 0; i < selectionSize; i++) {
            // Pick two candidates at random.
            EvaluatedCandidate<S> candidate1 = population.get(rng.nextInt(population.size()));
            EvaluatedCandidate<S> candidate2 = population.get(rng.nextInt(population.size()));

            // Use a random value to decide wether to select the fitter individual or the weaker one.
            // Select the fitter candidate.
            selection.add(candidate2.getFitness() > candidate1.getFitness()
                    ? candidate2.getCandidate()
                    : candidate1.getCandidate());
        }

        return selection;
    }
}
