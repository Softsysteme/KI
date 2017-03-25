/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Netze;

import distributions.Distribution;
import distributions.NominalNominalDistribution;
import distributions.NominalNumericDistribution;
import distributions.NumericNominalDistribution;
import distributions.NumericNumericDistribution;
import java.util.ArrayList;
import java.util.List;
import mathematik.Discretisierer;
import weka.core.Attribute;
import weka.core.Instances;

/**
 *
 * @author Thierry
 */
public class Node extends Discretisierer {
    /*
     * To change this license header, choose License Headers in Project Properties.
     * To change this template file, choose Tools | Templates
     * and open the template in the editor.
     */

    private int knotenID;
    public int label;
    public List Eltern;
    int[] ElternIDs;

    int anzeltern;
    int Elterncardinality;
    Instances inst;

    private String Farbe;

    public Node(int id, Instances inst) {
        this.knotenID = id;

        Eltern = new ArrayList<Node>();

        anzeltern = 0;
        Elterncardinality = 0;
        this.inst = new Instances(inst);

        //discretisiere();
        becomeWhite();
    }

    public void discretisiere() {

        if (inst.attribute(knotenID).type() == Attribute.NUMERIC) {
            changeIndiscretvalues(knotenID);
        }
    }

    public Distribution[][] berechneNodeParameter() {

        Distribution[][] Nodeparam = new Distribution[1][Eltern.size()];

        for (int i = 0; i < Eltern.size(); i++) {

            switch (inst.attribute(knotenID).type()) {
                case Attribute.NUMERIC: {

                    Attribute a = inst.attribute(getEltern(i).getID());
                    int c = a.index();
                    switch (a.type()) {
                        case Attribute.NUMERIC:
                            Nodeparam[0][Eltern.indexOf(getEltern(i))] = new NumericNumericDistribution(inst, knotenID, getEltern(i).getID());
                        case (Attribute.NOMINAL):
                            Nodeparam[0][Eltern.indexOf(getEltern(i))] = new NumericNominalDistribution(inst, knotenID, getEltern(i).getID());
                            break;
                        case (Attribute.STRING):
                            Nodeparam[0][Eltern.indexOf(getEltern(i))] = new NumericNominalDistribution(inst, knotenID, getEltern(i).getID());
                            break;

                    }
                }

                break;
                case Attribute.NOMINAL: {

                    Attribute a = inst.attribute(getEltern(i).getID());

                    int c = a.index();

                    switch (a.type()) {

                        case Attribute.NUMERIC:
                            Nodeparam[0][Eltern.indexOf(getEltern(i))] = new NominalNumericDistribution(inst, knotenID, getEltern(i).getID());
                            break;
                        case (Attribute.NOMINAL):
                            Nodeparam[0][Eltern.indexOf(getEltern(i))] = new NominalNominalDistribution(inst, knotenID, getEltern(i).getID());
                            break;
                        case (Attribute.STRING):
                            Nodeparam[0][Eltern.indexOf(getEltern(i))] = new NominalNominalDistribution(inst, knotenID, getEltern(i).getID());
                            break;

                    }
                }

            }
        }

        return Nodeparam;
    }

    public int getID() {
        return this.knotenID;
    }

    public void addEltern(Node i) {
        Eltern.add(i);
        anzeltern++;

    }

    public int anzahlEltern() {
        return Eltern.size();
    }

    public void removeEltern(Node i) {

        Eltern.remove(i);
    }

    public Node getEltern(int i) {
        return (Node) Eltern.get(i);
    }

    public Node getElternliste() {
        return (Node) Eltern;
    }

    public boolean isEltern(Node id) {
        return Eltern.contains(id);
    }

    public void becomeWhite() {
        Farbe = "Weiss";
    }

    public void becomeGrey() {
        Farbe = "grau";
    }

    public void becomeBlack() {
        Farbe = "schwarz";
    }

}
