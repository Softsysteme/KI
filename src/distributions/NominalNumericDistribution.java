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
public class NominalNumericDistribution extends Discretisierer implements Distribution {

    protected int[] attwertindexen;
    double[] attwerten;
    protected double[][] probs;
    int pID;
    int kID;
    Instances inst;
    double zaelt[][];

    public NominalNumericDistribution(Instances insta, int kID, int pID) {
        super(insta);
        this.inst = new Instances(insta);
        this.kID = kID;

        attwerten = new double[inst.numInstances()];
        for (int i = 0; i < attwerten.length; i++) {
            attwerten[i] = inst.instance(i).value(pID);
        }
        attwerten=super.EliminiereDopelt(attwerten);
        attwertindexen = new int[this.inst.attribute(kID).numValues()];
        probs = new double[this.inst.attribute(kID).numValues()][attwerten.length];
        zaelt = new double[this.inst.attribute(kID).numValues()][attwerten.length];
        for (int i = 0; i < attwertindexen.length; i++) {
            attwertindexen[i] = i;
        }
       /* for (int i = 0; i < inst.numInstances(); i++) {
            attwerten[(int) inst.instance(i).value(pID)] = inst.instance(i).value(pID);
        }*/

    }

    @Override
    public void calculateProbs() {

        for (int j = 0; j < attwerten.length; j++) {
            for (int k = 0; k < attwertindexen.length; k++) {
                for (int i = 0; i < inst.numInstances(); i++) {
                    if ((inst.instance(i).value(pID) == attwerten[j] && (inst.instance(i).value(kID)) == k)) {

                        probs[k][j]++;
                        zaelt[k][j]++;

                    }
                }

            }
        }
        int[] tab = new int[attwerten.length];

        for (int i = 0; i < attwerten.length; i++) {
            for (int j = 0; j < inst.numInstances(); j++) {
                if (inst.instance(j).value(pID) == attwerten[i]) {
                    tab[i]++;
                }
            }
        }
        for (int i = 0; i < probs.length; i++) {
            for (int j = 0; j < probs[i].length; j++) {
                if (tab[i] != 0) {
                    probs[i][j] /= tab[i];
                }
            }
        }
    }

    @Override
    public <Object> double getProbs(Object insta) {
        calculateProbs();
        int a=(int) ((Instance) insta).value(pID);
        if(a<attwerten.length){
        return probs[(int) ((Instance) insta).value(kID)][a];}
        return 0;
    }

    @Override
    public double[][] numberOfcounts() {
        return zaelt;
    }

}
