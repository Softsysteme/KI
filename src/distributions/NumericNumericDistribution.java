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
public class NumericNumericDistribution extends Discretisierer implements Distribution {

    private int kID;
    private int pID;
    private double[] attwerten1;
    private double[] attwerten2;
    private Instances inst;
    private double[][] probs;
    double zaelt[][];

    public NumericNumericDistribution(Instances inst, int kID, int pID) {

        this.inst = new Instances(inst);
        attwerten1 = new double[inst.numInstances()];
        attwerten2 = new double[inst.numInstances()];
        for (int k = 0, j = 0; k < attwerten1.length && j < attwerten2.length; k++, j++) {
            attwerten2[k] = inst.instance(k).value(pID);
            attwerten1[j] = inst.instance(j).value(kID);
        }
       /* for (int i = 0; i < inst.numInstances(); i++) {

            attwerten1[(int) inst.instance(i).value(kID)] = inst.instance(i).value(kID);
            
    
            attwerten2[(int) inst.instance(i).value(pID)] = inst.instance(i).value(pID);

        }*/
        
       this. attwerten1=super.EliminiereDopelt(this.attwerten1);
        this.attwerten2=super.EliminiereDopelt(this.attwerten2);
       this. probs = new double[attwerten1.length][attwerten2.length];
       zaelt=new double[attwerten1.length][attwerten2.length];

    }

    @Override
    public void calculateProbs() {

        for (int j = 0; j < attwerten2.length; j++) {
            for (int k = 0; k < attwerten1.length; k++) {
                for (int i = 0; i < inst.numInstances(); i++) {
                    if (inst.instance(i).value(pID) == attwerten2[j] && (inst.instance(i).value(kID)) == attwerten1[k]) {

                        probs[k][j]++;
                        zaelt[k][j]++;

                    }
                }

            }
        }
        int[] tab = new int[attwerten2.length];
        for (int j = 0; j < inst.numInstances(); j++) {
            for (int i = 0; i < tab.length; i++) {
                if (inst.instance(j).value(pID) == attwerten2[i]) {
                    tab[i]++;
                }
            }
        }
        for (int i = 0; i < attwerten1.length; i++) {
            for (int j = 0; j < attwerten2.length; j++) {
                if (tab[j] != 0) {
                    probs[i][j] /= tab[j];
                }
            }
        }
    }

    @Override
    public <Object> double getProbs(Object insta) {
        calculateProbs();
        int a=(int) ((Instance) insta).value(kID);
        int b=(int) ((Instance) insta).value(pID);
        if(a<attwerten1.length&&b<attwerten2.length){
        return probs[a][b];}
        return 0;
    }

    @Override
    public double[][] numberOfcounts() {
        return zaelt;
    }

}
