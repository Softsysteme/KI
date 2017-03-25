/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Graphs;

import weka.core.Instances;

/**
 *
 * @author Thierry
 */
public class GraphMitAngabeVernetzungsgrad extends Graph {

    double prozent;//vernetzungsprozent
/*Feld der beinhaltet ids von knoten,die überhaupt in eine verbindung sind*/
    int maxanzahkkanten;
    int anzahlkanten;
    int moeglischeanzkanten;

    public GraphMitAngabeVernetzungsgrad(Instances inst, int prozent) {
        this.inst = new Instances(inst);
        dieknoten = new Knoten[inst.numAttributes()];
        for (int i = 0; i < dieknoten.length; i++) {
            dieknoten[i] = new Knoten(inst.attribute(i).name(), inst.attribute(i).name());
        }
        this.prozent = prozent;
        adjMatrix = new int[inst.numAttributes()][inst.numAttributes()];
        matrix = new int[inst.numAttributes()][inst.numAttributes()];
        maxanzahkkanten = (int) Math.ceil(binomischeFormel(inst.numAttributes(), 2) * 2) / 2;
        for (int i = 0; i < adjMatrix.length; i++) {
            for (int j = 0; j < adjMatrix[i].length; j++) {
                if (i == j) {
                    adjMatrix[i][j] = 0;
                    adjMatrix[j][i] = 0;
                } else {
                    adjMatrix[i][j] = 1;
                }
            }
        }
        linearerepresentation = new int[inst.numAttributes() * inst.numAttributes()];

    }

    public boolean gibMin2False(boolean[] tab) {
        int i = 0;
        int count = 0;
        while (i < tab.length) {
            if (tab[i] == false) {
                count++;
                i++;
            } else {
                i++;
            }
        }

        return count >= 2;
    }

    public boolean gib1False(boolean[] tab) {
        int i = 0;
        int count = 0;
        while (i < tab.length) {
            if (tab[i] == false) {
                count++;
                i++;
            } else {
                i++;
            }
        }

        return count == 1;
    }

    public void VernetzungsProzent(double prozent) {
        this.anzahlkanten = (int) (Math.ceil(((this.maxanzahkkanten * prozent) / 100) * 2) / 2);
        int zuloeschen = this.maxanzahkkanten - this.anzahlkanten;
        int[] hilf = new int[matrix.length];
        for (int i = 0; i < hilf.length; i++) {
            hilf[i] = matrix.length - 1;
        }

        boolean[] kanteentfernt = new boolean[matrix.length];
        for (int i = 0; i < kanteentfernt.length; i++) {
            kanteentfernt[i] = false;
        }
        int k = 0;
        int j = 0;
        int i = 0;

        while (k < zuloeschen) {

            if (gibMin2False(kanteentfernt)) {

                while ((kanteentfernt[i] == true) || (hilf[i] < Maxindex(hilf))) {

                    i = (int) (Math.random() * matrix.length);
                }

                j = Maxindex(i, this.adjMatrix, hilf);
                /*  while ((kanteentfernt[j] == true)) {
                 System.out.println("www");
                 j = Maxindex(i, this.adjMatrix, hilf);
                 }*/

                kanteentfernt[i] = true;
                kanteentfernt[j] = true;
                adjMatrix[i][j] = 0;
                adjMatrix[j][i] = 0;
                hilf[i] -= 1;
                hilf[j] -= 1;

                k++;

            } else {

                if (this.gib1False(kanteentfernt)) {
                    int h = 0;
                    while (h < kanteentfernt.length && kanteentfernt[h] == true) {
                        h++;
                    }

                    i = h;

                    j = Maxindex(i, this.adjMatrix, hilf);

                    if (adjMatrix[i][j] == 1 && adjMatrix[j][i] == 1) {

                        adjMatrix[i][j] = 0;
                        adjMatrix[j][i] = 0;
                        hilf[i] -= 1;
                        hilf[j] -= 1;
                        k++;
                        for (int d = 0; d < kanteentfernt.length; d++) {
                            kanteentfernt[d] = false;
                        }

                    }
                } else {
                    for (int d = 0; d < kanteentfernt.length; d++) {
                        kanteentfernt[d] = false;
                    }
                }
            }
        }

        for (int t = 0;
                t < adjMatrix.length;
                t++) {
            for (int z = 0; z < adjMatrix[t].length; z++) {
                if (adjMatrix[t][z] == 1 && adjMatrix[z][t] == 1) {
                    int zufall = (int) (Math.random() * 2);
                    if (zufall == 0) {
                        matrix[t][z] = 1;
                        matrix[z][t] = 0;
                    } else {
                        matrix[t][z] = 0;
                        matrix[z][t] = 1;
                    }
                }
            }
        }
    }

    @Override
    public void suche() {
        this.moeglischeanzkanten = this.anzahlkanten;
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

    @Override
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
                        this.moeglischeanzkanten--;

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
    /* struckuriert den GraphMitAngabeVernetzungsgrad*/

    public void strukturiereGraph() {

        VernetzungsProzent(prozent);
        this.suche();
        doLinearerepresentation();
        this.connectGraph();
       

    }

    public int getMoeglischeAnzahlKanten() {
        return this.moeglischeanzkanten;
    }

    public int getAnzahlkanten() {
        return this.anzahlkanten;
    }

    public int getMaxAnzahlkanten() {
        return this.maxanzahkkanten;
    }

    /* public void VernetzungsProzent(double prozent) {
     double l;
     l = Math.ceil((((inst.numAttributes()) * prozent) / 100)*2)/2;
     k = new int[(int) l];
     for (int i = 0; i < k.length; i++) {
     k[i] = -1;
     }

     for (int i = 0; i < k.length; i++) {
     int c = (int) (Math.random() * inst.numAttributes());

     int j = 0;
     while (j <= i) {
     if (k[j] != c) {
     j++;
     } else {
     c = (int) (Math.random() * inst.numAttributes());
     j = 0;
     }

     }
     k[i] = c;

     }
     }

     public void fuelleadjMatrixaus() {

     for (int i = 0; i < adjMatrix.length; i++) {
     for (int j = 0; j < adjMatrix[i].length; j++) {

     if (!inTable(i) || !inTable(j)) {
     adjMatrix[i][j] = 0;
     adjMatrix[j][i] = 0;
     } else {

     adjMatrix[i][j] = 1;

               
     //(int) (Math.random() * 2);
     }

     }

     }

     for (int i = 0; i < adjMatrix.length; i++) {
     for (int j = 0; j < adjMatrix[i].length; j++) {
     if (adjMatrix[i][j] == 1 && adjMatrix[j][i] == 1) {
     adjMatrix[j][i] = 0;
     }
     }

     }
     }

     //prüfe ob i in der tabele enthalten ist
     public boolean inTable(int i) {
     int j = 0;
     while (j < k.length && k[j] != i) {
     j++;
     }
     return j < k.length;
     }

     public boolean hatKeinnachfolger(int i) {
     int count = 0;
     for (int j = 0; j < adjMatrix[i].length; j++) {
     if (adjMatrix[j][i] == 1) {
     count++;
     }
     }
     return count == 0;
     }*/
}
