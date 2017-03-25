/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SGA;

import Netze.BayesNetz;
import java.util.List;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

/**
 *
 * @author Thierry
 */
public class BayesFitnessEvaluator implements FitnessEvaluator<BayesNetz> {

    @Override
    public double getFitness(BayesNetz candidate, List<? extends BayesNetz> population) {
        return candidate.getFitness();
    }

    @Override

    public boolean isNatural() {
        return false;

    }
}
