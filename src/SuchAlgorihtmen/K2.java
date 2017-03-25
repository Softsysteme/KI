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
public class K2 implements Search {

    private BayesNetz struct;
    private Instances inst;
    int[] reihenfolge;
    int maxAnzeltern;
    int[][] matrix;
    int vernet;

    public K2(BayesNetz struct) {
         this.struct = struct;
        this.inst = struct.getInst();
  
        reihenfolge = new int[inst.numAttributes()];
        for (int i = 0; i < reihenfolge.length; i++) {

            int k = (int) (Math.random() * inst.numAttributes()+1);
            while (containst(k, reihenfolge)) {
                k = (int) (Math.random() * inst.numAttributes() + 1);
            }
            reihenfolge[i] = k;

        }

        matrix = new int[inst.numAttributes()][inst.numAttributes()];

        maxAnzeltern = inst.numAttributes() - 1;
       

    }

    @Override
    public void suche() {
        boolean weiter;

        for (int i = 0; i < inst.numAttributes(); i++) {
            int[] predi = new int[inst.numAttributes()];
            for (int l = 0; l < predi.length; l++) {
                predi[l] = -1;
            }

            for (int j = 0; j < reihenfolge.length; j++) {
                if (reihenfolge[j] < reihenfolge[i] && i != j) {
                    predi[j] = j;

                }
            }

            int[] prediohnediEltern = new int[predi.length];
            for (int c = 0; c < prediohnediEltern.length; c++) {
                prediohnediEltern[c] = -1;
            }

            for (int k = 0; k < predi.length; k++) {
                if (!struct.isEltern(k, i) && predi[k] != -1) {
                    prediohnediEltern[k] = predi[k];

                }
            }

            weiter = true;
            while (weiter && struct.getAnzEltern(i) < maxAnzeltern) {
                double scoreold = struct.berechneLokalScore(i);
               

                int merke = -1;
                double max = 0;

                for (int s = 0; s < prediohnediEltern.length; s++) {

                    if (prediohnediEltern[s] != -1) {
                        
                        double Sc=struct.berechneLokalScoreWithExtraParent(i, s);
                  
                      

                     

                        if (max <= Sc) {
                           
                         

                            max = Sc;

                            merke = prediohnediEltern[s];

                            struct.remooveEltern(i, merke);

                        } else {
                            
                            struct.remooveEltern(i, s);
                        }
                    }
                }

                if (merke != -1) {

                    struct.addElter(i, merke);
                    
                    prediohnediEltern[merke] = -1;
                    scoreold=max;
                } else {
                    weiter = false;
                }

            }
        }
    }

    @Override
    public void buildstruktur() {

       
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (struct.isEltern(i,j)) {
                    matrix[i][j] = 1;
                    vernet++;
                }
            }
        }
        
        
    }

    public boolean containst(int i, int[] t) {
        int k = 0;
        while (k < t.length && t[k] != i) {
            k++;
        }
        return k < t.length;
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
          return vernet;
    }
}
