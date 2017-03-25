/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Netze;

import Graphs.Graph;
import SuchAlgorihtmen.CISearch;
import SuchAlgorihtmen.K2;
import SuchAlgorihtmen.NaiveBayes;
import SuchAlgorihtmen.Search;
import classifiers.ComplexClassifier;
import distributions.Distribution;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.core.Instances;

/**
 *
 * @author Thierry
 */
public class BayesNetz {

    private Instances inst;
    private Node[] Nodes;
    private Distribution[][] Netzparam;
    private int[][] matrix;
    private Search algo;
    private Graph G;
    double[] ergebnisse;
    int algoArt;
    int vernetzung ;

    public BayesNetz(Instances insta, int art) {
        this.inst = new Instances(insta);
        algoArt = art;
        Nodes = new Node[inst.numAttributes()];
        for (int i = 0; i < Nodes.length; i++) {
            Nodes[i] = new Node(i, inst);
            Netzparam = new Distribution[inst.numAttributes()][inst.numAttributes()];
        }
        matrix = new int[inst.numAttributes()][inst.numAttributes()];

        G = new Graph(this.inst);
        searchalgo(art);
        
        matrix = algo.getMatrix();
         G.setMatrix(matrix);
        matrix=G.getMatrix();
        for(int i=0; i<matrix.length;i++)
        {
         for(int j=0; j<matrix[i].length;j++)
         {
          if(matrix[i][j]==1)   
              vernetzung++;
         }
        }
        vernetzung=((int)((vernetzung*100)/(G.binomischeFormel(inst.numAttributes(), 2))))*100;
       
       

        ergebnisse = new double[5];

    }

    public BayesNetz(BayesNetz struct) {
        this.inst = new Instances(struct.getInst());
        Nodes = new Node[inst.numAttributes()];
        for (int i = 0; i < Nodes.length; i++) {
            Nodes[i] = new Node(i, inst);
            Netzparam = new Distribution[inst.numAttributes()][inst.numAttributes()];
        }
        matrix = new int[inst.numAttributes()][inst.numAttributes()];
        this.G = new Graph(struct);
        matrix=G.getMatrix();
         for(int i=0; i<matrix.length;i++)
        {
         for(int j=0; j<matrix[i].length;j++)
         {
          if(matrix[i][j]==1)   
              vernetzung++;
         }
        }
        vernetzung=((int)((vernetzung*100)/(G.binomischeFormel(inst.numAttributes(), 2))));
      
        ergebnisse = new double[5];
    }

    public BayesNetz(Instances inst, Graph G) {
        this.inst = inst;
        Nodes = new Node[inst.numAttributes()];
        for (int i = 0; i < Nodes.length; i++) {
            Nodes[i] = new Node(i, inst);
            Netzparam = new Distribution[inst.numAttributes()][inst.numAttributes()];
        }

        this.G = G;
        matrix = G.getMatrix();
        ergebnisse = new double[5];
    }

    //berechnet den lokalen BDScore eines Knotensdes Graphen
    public double berechneLokalScore(int id) {
        if (Nodes[id].anzahlEltern() == 0) {

            return 0;
        }

        double score = 1;
        double sumcount = 0;
        double prodcont = 1;
        double sumprod = 1;

        Distribution[][] hilf;
        hilf= new Distribution[1][Nodes[id].anzahlEltern()];

        hilf = Nodes[id].berechneNodeParameter();
        if (hilf[0].length == 0) {
            return 0;
        }

        for (int j = 0; j < Nodes[id].anzahlEltern(); j++) {
            //System.out.println(isEltern(id, Nodes[id].getEltern(j).getID()));
            if (isEltern(id, Nodes[id].getEltern(j).getID())) {

                double[][] hilf2;
                hilf2= new double[inst.attribute(id).numValues()][inst.attribute(Nodes[id].getEltern(j).getID()).numValues()];
                if( hilf[0][j]!=null)
                hilf2 = hilf[0][j].numberOfcounts();
                for (int k = 0; k < hilf2.length; k++) {
                    for (int a = 0; a < hilf2[k].length; a++) {

                        sumcount = ((hilf2[k][a] == 0 ? 0 : Math.log10(hilf2[k][a])));
                        sumcount += ((inst.attribute(id).numValues() - 1) == 0 ? 0 : Math.log10(inst.attribute(id).numValues() - 1));
                        sumprod *= (sumcount);
                        double mult;

                        mult = ((hilf2[k][a] == 0 ? 1 : Math.log10(hilf2[k][a])));
                        prodcont *= mult;

                    }
                }
            }

        }
        //sumprod=(1/fakulteat(sumprod));
        // sumprod*=fakulteat(inst.attribute(id).numValues() - 1);
        //prodcont=fakulteat(prodcont);
        score = sumprod * prodcont;

        return Math.log10(score);

    }

    public double berechneLokalScoreWithExtraParent(int id, int p) {
        double result = 0;
        addElter(id, p);

        result = berechneLokalScore(id);
        remooveEltern(id, p);
        return result;
    }

    public double fakulteat(double k) {
        if (k == 0) {
            return 1;
        } else {
            return k * fakulteat(k - 1);
        }
    }

    public Node[] getNodeListe() {
        return Nodes;
    }

    public void searchalgo(int i) {
        switch (i) {
            case 0: {
                algo = new NaiveBayes(this);
                break;
            }

            case 1: {
                algo = new K2(this);
                break;
            }

            case 2: {
                algo = new CISearch(this);
                break;
            }

        }

    }

    public void BerechneFitness() throws Exception {
        ComplexClassifier c;

        c = new ComplexClassifier(this, 5);
        
        ergebnisse = c.getErgb();

    }

    public double getFitness() {
        try {
            BerechneFitness();
        } catch (Exception ex) {
            Logger.getLogger(BayesNetz.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ergebnisse[1];
    }
    
    public double getZeit()
    {
    return ergebnisse[2];
    }

    public int getAnzEltern(int i) {
        return Nodes[i].anzahlEltern();
    }

    public boolean isEltern(int i, int j) {
        return Nodes[i].isEltern(Nodes[j]);
    }

    public void addElter(int i, int j) {
        Nodes[i].addEltern(Nodes[j]);
    }

    public void remooveEltern(int i, int j) {
        Nodes[i].removeEltern(Nodes[j]);
    }

    public Instances getInst() {
        return inst;
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(int[][] m) {
        G.setMatrix(m);

    }

    public void setErgebnisse(double[] t) {
        ergebnisse = t;
    }

    public double[] getErgebnisse() {
        return ergebnisse;
    }

    public Graph getGraph() {
        return G;
    }

    public void setGraph(Graph g) {
        G = g;
    }
    
    
    public int getVernetzung()
    {
     return vernetzung;
    }
    
    public double geterb(int i)
    {
        return ergebnisse[i];
    }
    
    
   

  
}
