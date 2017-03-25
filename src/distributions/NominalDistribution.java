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
public class NominalDistribution implements Distribution {

    private double[] probs;
    private int kID;
    private Instances inst;
    int[] attwerten1;

    public NominalDistribution(Instances inst, int kID) {
        this.inst = new Instances(inst);
        this.kID = kID;
        probs = new double[inst.attribute(kID).numValues()];
        attwerten1=new int[inst.attribute(kID).numValues()];
        for (int i = 0; i < attwerten1.length; i++) {
            attwerten1[i] = i;
        }
        

    }

    @Override
    public void calculateProbs() {
        for (int i = 0; i < inst.numInstances(); i++) {
            for (int j = 0; j < attwerten1.length; j++) {
                if ((inst.instance(i).value(kID)) == (attwerten1[j])) {
                    probs[j]++;
                }
            }
        }
        for (int i = 0; i < probs.length; i++) {
            probs[i] /= inst.numInstances();
        }
    }

    @Override
    public <Object> double getProbs(Object insta) {
        calculateProbs();
        int a = (int) ((Instance) insta).value(kID);

        return probs[a];

    }

    @Override
    public double[][] numberOfcounts() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    

}
