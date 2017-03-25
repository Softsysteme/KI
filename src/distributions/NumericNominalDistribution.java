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
public class NumericNominalDistribution extends Discretisierer implements Distribution {

    protected double[] attwertindexen;
    protected double[] attwertindexen2;
    protected double[][] probs;
    int pID;
    Instances inst;
    int kID;
   double zaelt[][];

    public NumericNominalDistribution(Instances insta, int kID, int pID) {

        this.inst = new Instances(insta);
        this.pID = pID;
        this.kID = kID;

        attwertindexen = new double[inst.attribute(pID).numValues()];
        attwertindexen2 = new double[inst.numInstances()];
        for (int k = 0; k < attwertindexen2.length; k++) {
            attwertindexen2[k] =inst.instance(k).value(kID);
        }

        /*for (int i = 0; i < inst.numInstances(); i++) {
            attwertindexen2[(int) inst.instance(i).value(kID)] = inst.instance(i).value(kID);*/
        attwertindexen2=super.EliminiereDopelt(attwertindexen2);
        
        probs = new double[attwertindexen2.length][inst.attribute(pID).numValues()];
        zaelt=new double[attwertindexen2.length][inst.attribute(pID).numValues()];
        for (int i = 0; i < attwertindexen.length; i++) {
            attwertindexen[i] = i;
        }

    }

    @Override
    public void calculateProbs() {

        for (int j = 0; j < inst.attribute(pID).numValues(); j++) {
            for (int k = 0; k < attwertindexen2.length; k++) {
                for (int i = 0; i < this.inst.numInstances(); i++) {
                    if (inst.instance(i).value(pID) == j && (inst.instance(i).value(kID)) == attwertindexen2[k]) {

                        probs[k][j]++;
                        zaelt[k][j]++;

                    }
                }

            }
        }

        for (int j = 0; j < inst.numInstances(); j++) {
            for (int i = 0; i < this.inst.attribute(pID).numValues(); i++) {
                if (inst.instance(j).value(pID) == attwertindexen[i]) {
                    attwertindexen[i]++;
                }
            }
        }
        for (int i = 0; i < attwertindexen2.length; i++) {
            for (int j = 0; j < this.inst.attribute(pID).numValues(); j++) {
                probs[i][j] /= attwertindexen[j];
            }
        }
    }

    @Override
    public <Object> double getProbs(Object insta) {
        calculateProbs();
        int a=(int) ((Instance) insta).value(kID);
        int b=(int) ((Instance) insta).value(pID);
        if(a<attwertindexen2.length&&b<attwertindexen.length){
        return probs[a][b];}
        return 0;
    }

    @Override
    public double[][] numberOfcounts() {
        return zaelt;
    }
    

}
