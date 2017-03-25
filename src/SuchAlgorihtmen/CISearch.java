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
public class CISearch implements Search {

    private BayesNetz struct;
    private Instances inst;
    int maxAnzeltern;
    int[][] matrix;
    Node[] n;
    int vernet;

    public CISearch(BayesNetz struct) {
        this.struct = struct;
        this.inst = new Instances(struct.getInst());
       
        n = struct.getNodeListe();

        matrix = new int[inst.numAttributes()][inst.numAttributes()];

        maxAnzeltern = (int) (Math.random() * inst.numAttributes() - 1);
        
        

    }

    @Override
    public void suche() {
        struct.setGraph(new Graph(inst));
        Node[] hilf = new Node[inst.numAttributes()];
        hilf = struct.getNodeListe();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j]==1) {
                  //  struct.addElter(i, j);

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
                    vernet++;
                }
            }
        }

       

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
        int[] tab;
        tab=new int[struct.getInst().numAttributes()];
        for(int i=0;i<tab.length;i++)
            tab[i]=1;
        for(int i=0; i<tab.length;i++)
        {
        
        if(struct.isEltern(iAttributeX,i ))
            tab[i]=i;
        
        
        }
        
        //remoove all Parent von iAttributeX
        
        for(int i=0; i<struct.getInst().numAttributes();i++)
        {
        
        if(struct.isEltern( iAttributeX,i ))
            struct.remooveEltern( iAttributeX,i);
        
        }
        
        for (int Z = 0; Z < inst.numAttributes(); Z++) {
            if (Z != iAttributeY) {
                struct.addElter(iAttributeX, Z);
            }
        }

        double fScoreZ = struct.berechneLokalScore(iAttributeX);
        

        double fScoreZY = struct.berechneLokalScoreWithExtraParent(iAttributeX,iAttributeY);
        if (fScoreZY <= fScoreZ) {
			// the score does not improve by adding Y to the parent set of X
            // so we conclude that nodes X and Y are conditionally independent
            // given the set of variables Z

            for (int Z = 0; Z < inst.numAttributes(); Z++) {
              
                    struct.remooveEltern(iAttributeX, Z);
                    if(tab[Z]!=-1)
                        struct.addElter(iAttributeX, Z);
            }
                    return true;
                
            

        }
        
       

        for (int Z = 0; Z < inst.numAttributes(); Z++) {
            
                struct.remooveEltern(iAttributeX, Z);
                if(tab[Z]!=-1)
                        struct.addElter(iAttributeX, Z);
                
                //struct.addElter(iAttributeX,iAttributeY);
            
        }
      

        return false;
    } // IsConditionalIndependent

    @Override
    public int[][] getMatrix() {
       buildstruktur();
        suche();
        return matrix;
    }

    @Override
    public int getvernet() {
       return vernet;
    }

}
