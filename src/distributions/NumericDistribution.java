/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distributions;

import mathematik.Discretisierer;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author Thierry
 */
public class NumericDistribution extends Discretisierer implements Distribution {

    protected Instances inst;
    protected int kID;
    protected int pID;
    protected double[] attwerten;
    private double[] probs;

    public NumericDistribution(Instances inst, int kID) {

        this.inst = new Instances(inst);
        this.kID = kID;
        attwerten = new double[inst.numAttributes()];
        for(int i=0,j=0;i<attwerten.length&&j<inst.numInstances();i++,j++){
            attwerten[i] = inst.instance(j).value(kID);
        }
        
        attwerten=super.EliminiereDopelt(attwerten);
       /* for (int k = 0; k < inst.numInstances(); k++) {
            attwerten[(int) inst.instance(k).value(kID)] = inst.instance(k).value(kID);
        }*/

        probs = new double[attwerten.length];

    }

    @Override
    public void calculateProbs() {

        for (int j = 0; j < attwerten.length; j++) {
            for (int i = 0; i < inst.numInstances(); i++) {
                if ((inst.instance(i).value(kID)) == (attwerten[j])) {
                    probs[j]++;
                }
            }
        }
        for (int i = 0; i < probs.length; i++) {
            if (inst.numInstances() != 0) {
                probs[i] /= inst.numInstances();
            }
        }

    }

    @Override
    public <Object> double getProbs(Object insta) {
        calculateProbs();
        int a = (int) ((Instance) insta).value(kID);
        if(a<attwerten.length)

        { return probs[a];}
        return 0;

    }

    @Override
    public double[][]numberOfcounts() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
