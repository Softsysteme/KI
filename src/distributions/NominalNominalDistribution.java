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
public class NominalNominalDistribution implements Distribution {

    private int kID;
    private int pID;
    private int[] attwerten1;
    private int[] attwerten2;
    private Instances inst;
    private double[][] probs;
    private double[][] zaelt;

    public NominalNominalDistribution(Instances inst, int kID, int pID) {
        this.inst = new Instances(inst);
        this.kID = kID;
        this.pID = pID;
        attwerten1 = new int[inst.attribute(kID).numValues()];
        attwerten2 = new int[inst.attribute(pID).numValues()];
        for (int i = 0; i < attwerten1.length; i++) {
            attwerten1[i] = i;
        }
        for (int i = 0; i < attwerten2.length; i++) {
            attwerten2[i] = i;
        }
        probs = new double[inst.attribute(kID).numValues()][inst.attribute(pID).numValues()];
        zaelt=new double[this.inst.attribute(kID).numValues()][inst.attribute(pID).numValues()];
      

    }

    @Override
    public void calculateProbs() {
        int[] hilf = new int[attwerten2.length];
        for (int j = 0; j < attwerten2.length; j++) {
            for (int k = 0; k < attwerten1.length; k++) {
                for (int i = 0; i < inst.numInstances(); i++) {

                    if ((inst.instance(i).value(pID)) == (attwerten2[j]) && (inst.instance(i).value(kID) == attwerten1[k])) {

                        probs[k][j]++;
                        zaelt[k][j]++;
                    }
                }
            }
        }

        for (int i = 0; i < inst.numInstances(); i++) {
            for (int j = 0; j < attwerten2.length; j++) {
                if (inst.instance(i).value(pID) == attwerten2[j]) {
                    hilf[j]++;
                }
            }

        }

        for (int i = 0; i < probs.length; i++) {
            for (int j = 0; j < probs[i].length; j++) {
                probs[i][j] /= hilf[j];
            }
        }

    }

    @Override
    public <Object> double getProbs(Object insta) {
        calculateProbs();
            
       return probs[(int) ((Instance) insta).value(kID)][(int) ((Instance) insta).value(pID)];

    }

    @Override
    public double[][] numberOfcounts() {
         calculateProbs();
        return zaelt;
    }

}
