/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SuchAlgorihtmen;


import Graphs.Graph;
import Netze.BayesNetz;
import Netze.Node;
import weka.core.Instances;

/**
 *
 * @author Thierry
 */
public class Ci implements Search {

    private BayesNetz struct;
    private Instances inst;
    int maxAnzeltern;
    int[][] matrix;
    Node[] n;

    public Ci(BayesNetz struct) {
        this.struct = struct;
        this.inst = inst;
       
        n = struct.getNodeListe();

        matrix = new int[inst.numAttributes()][inst.numAttributes()];

        maxAnzeltern = (int) (Math.random() * inst.numAttributes() - 1);
        
        buildstruktur();
        suche();

    }

    @Override
    public void suche() {
        struct.setGraph(new Graph(inst));
        Node[] hilf = new Node[inst.numAttributes()];
        hilf = struct.getNodeListe();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j]==1) {
                    struct.addElter(i, j);

                }
            }
        }
    }

    @Override
    public void buildstruktur() {
        for (int i = 0; i < inst.numAttributes(); i++) {
            for (int j = 0; j < inst.numAttributes(); j++) {
                if (!isConditionalIndependent(i, j)) {
                    matrix[i][j] = 1;
                }
            }
        }

        struct.setMatrix(matrix);

    }

    @Override
    public void repair() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * IsConditionalIndependent tests whether two nodes X and Y are independent
     * given a set of variables Z. The test compares the score of the Bayes
     * network with and without arrow Y->X where all nodes in Z are parents of
     * X.
     *
     * @param iAttributeX - index of attribute representing variable X
     * @param iAttributeY - index of attribute representing variable Y
     *
     * @return true if X and Y conditionally independent given Z
     */
    protected boolean isConditionalIndependent(
            int iAttributeX,
            int iAttributeY) {

        // insert parents in iAttributeZ
        for (int Z = 0; Z < inst.numAttributes(); Z++) {
            if (Z != iAttributeY) {
                n[iAttributeX].addEltern(new Node(Z,inst));
            }
        }

        double fScoreZ = struct.berechneLokalScore(iAttributeX);
        if (!n[iAttributeX].isEltern(new Node(iAttributeY,inst))) {
            n[iAttributeX].addEltern(new Node(iAttributeY,inst));
        }

        double fScoreZY = struct.berechneLokalScore(iAttributeX);
        if (fScoreZY <= fScoreZ) {
			// the score does not improve by adding Y to the parent set of X
            // so we conclude that nodes X and Y are conditionally independent
            // given the set of variables Z

            for (int Z = 0; Z < inst.numAttributes(); Z++) {
                if (Z != iAttributeY) {
                    n[iAttributeX].removeEltern(new Node(Z,inst));
                }
            }

            n[iAttributeX].removeEltern(new Node(iAttributeY,inst));
            return true;
        }

        for (int Z = 0; Z < inst.numAttributes(); Z++) {
            if (Z != iAttributeY) {
                n[iAttributeX].removeEltern(new Node(Z,inst));
            }
        }
        n[iAttributeX].removeEltern(new Node(iAttributeY,inst));

        return false;
    } // IsConditionalIndependent

    @Override
    public int[][] getMatrix() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getvernet() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
