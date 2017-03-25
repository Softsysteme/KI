/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mathematik;

import java.util.Enumeration;
import weka.core.Attribute;
import weka.core.Instances;

/**
 *
 * @author Thierry
 */
public class Discretisierer {

    protected Instances inst;

    protected int[] attwerten;
    protected double[] attwerten2;

    protected int[] attwertenchanged;
    int id;
    int pID;

    public Discretisierer(Instances insta) {

        this.inst = new Instances(insta);

        attwerten = new int[insta.numInstances()];
        Enumeration<Attribute> enu = insta.enumerateAttributes();
        int attindex = 0;
        while (enu.hasMoreElements()) {
            Attribute att = enu.nextElement();

            if (att.type() == Attribute.NUMERIC) {

                {
                    for (int i = 0; i < attwerten.length; i++) {
                        attwerten[i] = ((int) inst.instance(i).value(attindex));
                    }

                }

                changeIndiscretvalues(attindex);

            }
            attindex++;
        }
    }

    public Discretisierer() {
    }

    //zählt die Anzahl der Intervallen
    public int numberOfIntervalls(int index) {
        return (int) Math.round(1 + 3.322 * Math.log10(inst.numDistinctValues(index)));
    }

    //berechnet den hoechsten Wert
    public int getHigvalue(int[] tab) {
        int max = 0;
        int j = 1;
        max = tab[0];

        while (j < tab.length) {
            if (tab[j] > max) {
                max = tab[j];
            }
            j++;

        }

        return max;
    }

    //berechnet niedrigten Wert
    public int getlowvalue(int[] tab) {
        int min = 0;
        int j = 1;
        min = tab[0];

        while (j < tab.length) {
            if (tab[j] < min) {
                min = tab[j];
            }
            j++;

        }

        return min;
    }

    //Umfang jedes Intervalls
    public int Intervallsamplitude(int index) {

        return (int) ((getHigvalue(attwerten) - getlowvalue(attwerten)) / numberOfIntervalls(index));

    }

    public double[] EliminiereDopelt(double[] tab) {
        double[] hilf;
        int count = 0;
        for (int i = 0; i < tab.length; i++) {
            int j = i + 1;
            while (j < tab.length) {
                if (tab[j] == tab[i] && tab[i] != -1) {
                    tab[j] = -1;
                    count++;
                    j++;
                } else {
                    j++;
                }
            }
        }

        hilf = new double[tab.length - count];
        int k = 0;
        int j = 0;
        while (k < hilf.length && j < tab.length) {
            if (tab[j] != -1) {
                hilf[k] = tab[j];
                j++;
                k++;
            } else {
                j++;
            }

        }
        return hilf;
    }

    public boolean missingcase(boolean[] tab) {
        int i = 0;
        while (i < tab.length) {
            if (tab[i] == false) {

                return true;
            } else {
                i++;
            }
        }

        return false;

    }

    public boolean inIntervall(int a, int b, int c) {

        return (c >= a && c < b);

    }

    //discretisieren
    public void changeIndiscretvalues(int att) {
        int n = 0;
        int a = ((int) this.Intervallsamplitude(att));
        if (a != 0) {
            if (a < 0) {
                a = a * (-1);
            }

            n = (int) ((this.getHigvalue(attwerten) - (this.getlowvalue(attwerten))) / a);
           

            
            int obereschranke = (int) this.getlowvalue(attwerten);
            int untereschranke;

            int k = 0;

            while (k <= n) {

                untereschranke = obereschranke;
                obereschranke = obereschranke + a;

                for (int i = 0; i < attwerten.length; i++) {

                    if (inIntervall(untereschranke, obereschranke, (int) attwerten[i])) {
                     
                        for (int j = 0; j < inst.numInstances(); j++) {

                            if ((int) (inst.instance(j).value(att)) == attwerten[i]) {
                                inst.instance(i).setValue(att, k);

                            }

                        }

                    }

                }
                k++;
            }
        } else {

            n = (int) this.numberOfIntervalls(att);
           
            int obereschranke = (int) this.getlowvalue(attwerten);
            int untereschranke;

            int k = 0;

            while (k <= n) {

                untereschranke = obereschranke;
                obereschranke = obereschranke + 1;

                for (int i = 0; i < attwerten.length; i++) {

                    if (inIntervall(untereschranke, obereschranke, (int) attwerten[i])) {
                        // attwertenchanged[i] = k;
                        for (int j = 0; j < inst.numInstances(); j++) {

                            if ((int) (inst.instance(j).value(att)) == attwerten[i]) {
                                inst.instance(i).setValue(att, k);

                            }

                        }

                    }

                }
                k++;

            }
        }
    }

    public Instances getinst() {
        return this.inst;
    }
}
