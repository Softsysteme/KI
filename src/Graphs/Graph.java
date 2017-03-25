/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Graphs;

import Netze.BayesNetz;
import weka.core.Instances;

/**
 *
 * @author Thierry
 */
public class Graph {

    protected int[][] adjMatrix;
    protected Knoten[] dieknoten;
    protected int[] linearerepresentation;

    /*Feld der beinhaltet ids von knoten,die überhaupt in eine verbindung sind*/
    protected int[] k;
    protected boolean hatZyklen = true;
    protected Instances inst;
    protected int[][] matrix;
    BayesNetz struct;
    int vernetzung;

    public Graph(Instances inst) {
        this.inst = new Instances(inst);
        dieknoten = new Knoten[inst.numAttributes()];
        for (int i = 0; i < dieknoten.length; i++) {
            dieknoten[i] = new Knoten(inst.attribute(i).name(), inst.attribute(i).name());
            dieknoten[i].setMaxKinder(inst.numAttributes() - 1);
        }
        adjMatrix = new int[inst.numAttributes()][inst.numAttributes()];
        matrix = new int[inst.numAttributes()][inst.numAttributes()];
        linearerepresentation = new int[inst.numAttributes() * inst.numAttributes()];
    }

    public Graph() {

    }

    public Graph(BayesNetz struct) {
        this.struct = struct;

        this.inst = new Instances(struct.getInst());
        dieknoten = new Knoten[inst.numAttributes()];
        for (int i = 0; i < dieknoten.length; i++) {
            dieknoten[i] = new Knoten(inst.attribute(i).name(), inst.attribute(i).name());
        }
        matrix = new int[inst.numAttributes()][inst.numAttributes()];
        this.matrix = struct.getMatrix();

        linearerepresentation = new int[inst.numAttributes() * inst.numAttributes()];
        suche();
        connectGraph();

    }

    public void suche() {

        while (hatWeiss()) {
            int i = (int) (Math.random() * dieknoten.length);

            while (!"Weiss".equals(dieknoten[i].getColor())) {
                i = (int) (Math.random() * dieknoten.length);

            }
            int count = 0;
            for (int k = 0; k < matrix.length; k++) {
                if (matrix[i][k] == 1) {
                    count++;
                }
            }
            if (count == 0) {
                dieknoten[i].becomeBlack();

            } else {

                sucheundEliminiereZyklen(dieknoten[i]);
            }

        }

    }

    public void sucheundEliminiereZyklen(Knoten k) {
        k.becomeGrey();
        boolean[][] schoninvertiert = new boolean[matrix.length][matrix.length];

        //   if (k.anzahlKinder() != 0) {
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i][inst.attribute(k.getID()).index()] == 1) {

                if (!"Weiss".equals(dieknoten[i].getColor()) && (schoninvertiert[i][inst.attribute(k.getID()).index()] == false)) {

                    matrix[i][inst.attribute(k.getID()).index()] = 0;
                    matrix[inst.attribute(k.getID()).index()][i] = 1;
                    schoninvertiert[i][inst.attribute(k.getID()).index()] = true;
                    schoninvertiert[inst.attribute(k.getID()).index()][i] = true;
                    k.becomeWhite();
                    sucheundEliminiereZyklen(k);

                } else {
                    if (schoninvertiert[i][inst.attribute(k.getID()).index()] == true) {
                        matrix[i][inst.attribute(k.getID()).index()] = 0;
                        matrix[inst.attribute(k.getID()).index()][i] = 0;

                    } else {
                        sucheundEliminiereZyklen(dieknoten[i]);
                    }

                    //k.becomeWhite();
                }

            }
            // }
        }
        k.becomeBlack();
    }

    public double Fakulteat(double k) {
        if (k == 0) {
            return 1;
        } else {
            return k * Fakulteat(k - 1);
        }
    }

    public double binomischeFormel(double k, double n) {
        return (Fakulteat(k) / (Fakulteat(n) * Fakulteat(k - n)));
    }

    public int Maxindex(int[] tab) {
        int max = tab[0];
        int j = 0;
        int i = 0;
        while (i < tab.length) {
            if (max < tab[i]) {
                max = tab[i];
                j = i++;
            } else {
                i++;
            }
        }
        return max;
    }

    public int Maxindex(int i, int[][] tab, int[] tab1) {
        int max = 0;
        int k = 0;
        int[] t = null;

        for (int j = 0; j < tab[i].length; j++) {
            if (tab[i][j] == 1 && tab[j][i] == 1) {
                if (max < tab1[j]) {
                    max = tab1[j];

                }

            }
        }
        int count = 0;
        for (int j = 0; j < tab[i].length; j++) {
            if (tab[i][j] == 1 && tab[j][i] == 1) {
                if (tab1[j] == max) {
                    count++;
                }
            }

        }
        if (count != 0) {
            t = new int[count];
        }
        else {t=new int[1];}
        for (int c = 0; c < t.length; c++) {
            for (int j = 0; j < tab[i].length; j++) {
                if (tab[i][j] == 1 && tab[j][i] == 1) {
                    if (tab1[j] == max) {
                        t[c] = j;
                    }
                }
            }
        }

        return t[(int) (Math.random() * t.length)];
    }

    public Knoten[] getDieknoten() {
        return this.dieknoten;
    }

    public void ToString() {
        for (Knoten dieknoten1 : dieknoten) {
            if (dieknoten1.anzahlKinder() != 0) {
                System.out.println(dieknoten1.DisplayKinder());
            } else {
                System.out.println("{" + dieknoten1.getID() + "}");
            }
        }
    }

    public boolean hatWeiss() {

        int i = 0;
        while (i < dieknoten.length) {
            if ("Weiss".equals(dieknoten[i].getColor())) {
                return true;
            } else {
                i++;
            }

        }
        return (i < dieknoten.length);
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(int[][] m) {
        this.matrix = m;
        suche();
        doLinearerepresentation();
        connectGraph();

    }

    public void connectGraph() {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == 1) {
                    dieknoten[i].addEltern(new Knoten(inst.attribute(j).name(), inst.attribute(j).name()));
                    dieknoten[j].addKinder(new Knoten(inst.attribute(i).name(), inst.attribute(i).name()));

                }
            }
        }
        for (Knoten dieknoten1 : dieknoten) {
            dieknoten1.kanten = new int[dieknoten1.anzahlKinder() + dieknoten1.anzahlEltern()][2];
            for (int b = 0; b < dieknoten1.anzahlKinder(); b++) {
                dieknoten1.kanten[b][0] = inst.attribute(dieknoten1.getKind(b).getID()).index();
            }
            for (int j = 0; j < dieknoten1.anzahlEltern(); j++) {
                dieknoten1.kanten[j][1] = inst.attribute(dieknoten1.getEltern(j).getID()).index();
            }
        }
    }

    public void doLinearerepresentation() {
        int k = 0;

        while (k < linearerepresentation.length) {
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    linearerepresentation[k++] = matrix[i][j];

                }
            }
        }

    }

    public int[] getLinearrepresentation() {
        doLinearerepresentation();
        return linearerepresentation;
    }

    public int[][] restructruriereGraph(int[] tab) {

        if (tab.length > this.linearerepresentation.length) {
            System.out.println("Error");
        }

        int c = 0;

        while (c < linearerepresentation.length) {
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    matrix[i][j] = linearerepresentation[c++];

                }
            }
        }

        suche();
        doLinearerepresentation();
        connectGraph();

        return matrix;

    }

}
