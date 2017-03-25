/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SuchAlgorihtmen;

import Netze.BayesNetz;
import Netze.Node;
import weka.core.Instances;

/**
 *
 * @author Thierry
 */
public class NaiveBayes implements Search {

    private BayesNetz struct;
    private Instances inst;

    int[][] matrix;

    public NaiveBayes(BayesNetz struct) {
        this.struct = struct;
        this.inst = struct.getInst();

        matrix = new int[inst.numAttributes()][inst.numAttributes()];


    }

    @Override
    public void suche() {

        for (int i = 0; i < inst.numAttributes(); i++) {
            if (inst.classIndex() != i) {
                struct.addElter(i, inst.classIndex());

            }
        }

    }

    @Override
    public void buildstruktur() {

        for (int i = 0; i < matrix.length; i++) {

            if (i != inst.classIndex()) {
                matrix[i][inst.classIndex()] = 1;

            }
        }

        //struct.setMatrix(matrix);
    }

    @Override
    public void repair() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int[][] getMatrix() {
       
        suche();
        buildstruktur();
        return matrix;
    }

    @Override
    public int getvernet() {
       return 0;
    }

}
