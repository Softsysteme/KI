/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distributions;

import mathematik.Discretisierer;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author Thierry
 */
public class ClassdistributionNumeric extends Discretisierer implements Distribution {

    private double[] probs;
    int classID;
    Attribute at;
    int kID;
    Instances inst;
    double[] attwerten;

    /**
     *
     * @param inst
     * @param classID
     * @param kID
     */
    public ClassdistributionNumeric(Instances inst, int classID, int kID) {

        this.inst = new Instances(inst);

        this.classID = classID;
        this.kID = kID;

        attwerten = new double[inst.numInstances()];
        for (int k = 0; k < inst.numInstances(); k++) {
            attwerten[k] = inst.instance(k).value(kID);
        }

        attwerten = super.EliminiereDopelt(attwerten);
        /*for(int j=0;j<attwerten.length;j++){
         for (int i = 0; i < inst.numInstances(); i++) {
           
            
         attwerten[(int) inst.instance(i).value(kID)] = inst.instance(i).value(kID);
         }
        
         }*/

        probs = new double[attwerten.length];

    }

    /**
     *
     */
    @Override

    public void calculateProbs() {
        for (int i = 0; i < inst.numInstances(); i++) {

            for (int k = 0; k < attwerten.length; k++) {

                if (inst.instance(i).classValue() == classID) {
                    if ((inst.instance(i).value(kID)) == (attwerten[k])) {

                        probs[k]++;
                    }
                }

            }
        }

        int count = 0;
        for (int j = 0; j < inst.numInstances(); j++) {
            if (inst.instance(j).classValue() == classID) {
                count++;
            }
        }

        for (int i = 0; i < probs.length; i++) {

            if (count != 0);
            probs[i] /= count;
        }
    }

    /**
     *
     * @param <Object>
     * @param insta
     * @return 
     */
    @Override
    public <Object> double getProbs(Object insta) {
        calculateProbs();
        int a = (int) ((Instance) insta).value(kID);
        if (a < probs.length) {

            return probs[a];
        }
        return 0;

    }

    @Override
    public double[][] numberOfcounts() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
