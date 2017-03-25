/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GenetischeAlgorithmen;

import java.util.Random;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import Netze.BayesNetz;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Mutation implements EvolutionaryOperator<BayesNetz> {

    private final NumberGenerator<Probability> mutationProbability;
    private final NumberGenerator<Integer> mutationCount;
    BayesNetz b;
    private BayesNetz p1;

    int[] mat1;

    public Mutation(NumberGenerator<Probability> mutationProbability,
            NumberGenerator<Integer> mutationCount) {
        this.mutationProbability = mutationProbability;
        this.mutationCount = mutationCount;
    }

    public BayesNetz mutate(BayesNetz selected, Random rnd) {

       

        if (mutationProbability.nextValue().nextEvent(rnd)) {
            int[] mutated = new int[selected.getGraph().getLinearrepresentation().length];
            mutated = selected.getGraph().getLinearrepresentation().clone();
            int mutations = mutationCount.nextValue();
            for (int i = 0; i < mutations; i++) {
                InvertBit(mutated, rnd.nextInt(mutated.length));
            }

            selected.setMatrix(selected.getGraph().restructruriereGraph(mutated));

            return selected;
        }
        return selected;
    }

    public void InvertBit(int[] tab, int i) {
        if (i < tab.length) {
            tab[i] = (tab[i] == 0 ? 1 : 0);
        }
    }

    @Override
    public List<BayesNetz> apply(List<BayesNetz> selectedCandidates, Random rng) {
        List<BayesNetz> mutatedPopulation = new ArrayList<BayesNetz>(selectedCandidates.size());
        Iterator<BayesNetz> it = selectedCandidates.iterator();
        while (it.hasNext()) {
            mutatedPopulation.add(mutate(it.next(),new Random()));
        }
        return mutatedPopulation;

    }

}
