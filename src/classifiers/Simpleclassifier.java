/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classifiers;


import Graphs.Graph;

import Netze.BayesNetz;
import distributions.ClassdistributionNominal;
import distributions.ClassdistributionNumeric;

import distributions.Distribution;
import weka.core.*;

import java.util.*;
import mathematik.Discretisierer;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author Thierry
 */
public class Simpleclassifier extends Discretisierer implements Classifiers {

    private Instances traindaten;
    private Instances testdaten;
    private Instances validierungsmenge;
    private Instances Modelmenge;
    Instances Datenbank;
    private Distribution[][] Modelparam;
    private double[] classparam;
    private int[] trainingsetindexen;
    private int[] testsetindexen;
    private int[] validierungsindexen;
    private int[] Modelsindexen;
    int anzahldurchlauf;
    double[][] trainergebnisse;
    double[][] testergebnisse;
    double[][] Modelergebnisse;
    double[][] validierungsergebnisse;
    public double Mittlerevalidierungsquote;
    public  double Mittlerezeit;
     public double standartdeviationtime;
     public double stadartdeviationvalidierung;
     BayesNetz struct;
     double[] ergb;
     public Graph Model;

    public Simpleclassifier(BayesNetz strct, int anzahl) {
        super(strct.getInst());

        Datenbank = new Instances(super.inst);
        this.struct=strct;

        this.anzahldurchlauf = anzahl;
        trainergebnisse = new double[anzahldurchlauf][2];
        testergebnisse = new double[anzahldurchlauf][2];
        Modelergebnisse = new double[1][2];
        validierungsergebnisse = new double[1][2];
        ergb=new double[5];
        Model= struct.getGraph();

    }

    @Override
    public void BauClassifier() {
        if (inst != null) {
            bootstrapvalidierungsmenge(Datenbank);
        }

    }

    @Override
    public void bootstrapvalidierungsmenge(Instances inst) {
        if (inst.numAttributes() != 0) {
            int[] hilf = new int[inst.numInstances()];

            for (int i = 0; i < inst.numInstances(); i++) {
                int a = ((int) (Math.random() * inst.numInstances()));

                hilf[i] = a;
            }

            Modelsindexen = EliminiereDopelt(hilf);
            Modelmenge = new Instances(inst, Modelsindexen.length);
            for (int i = 0; i < Modelsindexen.length; i++) {

                Modelmenge.add(new Instance(inst.instance(Modelsindexen[i])));
            }

            validierungsindexen = new int[inst.numInstances() - Modelsindexen.length];
            validierungsmenge = new Instances(Modelmenge, validierungsindexen.length);

            for (int i = 0, j = 0; i < inst.numInstances() && j < validierungsindexen.length; i++, j++) {
                if (!(HasSet(Modelsindexen, i))) {
                    validierungsindexen[j] = i;
                    validierungsmenge.add(inst.instance(validierungsindexen[j]));

                }
            }

        }

    }

    @Override
    public void Bootstrap(Instances inst) {
        if (Modelmenge.numAttributes() != 0) {
            int[] hilf = new int[Modelmenge.numInstances()];

            for (int i = 0; i < Modelmenge.numInstances(); i++) {
                int a = ((int) (Math.random() * Modelmenge.numInstances()));

                hilf[i] = a;
            }

            trainingsetindexen = EliminiereDopelt(hilf);
            traindaten = new Instances(Modelmenge, trainingsetindexen.length);
            for (int i = 0; i < trainingsetindexen.length; i++) {

                traindaten.add(new Instance(inst.instance(trainingsetindexen[i])));
            }

            testsetindexen = new int[Modelsindexen.length - trainingsetindexen.length];
            testdaten = new Instances(traindaten, testsetindexen.length);

            for (int i = 0, j = 0; i < Modelmenge.numInstances() && j < testsetindexen.length; i++, j++) {
                if (!(HasSet(trainingsetindexen, i))) {
                    testsetindexen[j] = i;
                    testdaten.add(inst.instance(testsetindexen[j]));

                }
            }

        }

    }

    /*Anzahl testdaten*/
    public int getAnzahldaten(int[] tab) {
        int i = 0;
        int count = 0;
        while (i < tab.length) {
            if (!HasSet(tab, i)) {
                count++;
            }
            i++;
        }
        return count;
    }

    public boolean HasSet(int[] tab, int i) {
        int j = 0;
        boolean ergebniss;
        while (j < tab.length && tab[j] != i) {
            j++;
        }
        if (j < tab.length) {
            ergebniss = true;
        } else {
            ergebniss = false;
        }
        return ergebniss;

    }

    public int[] EliminiereDopelt(int[] tab) {
        int[] hilf;
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

        hilf = new int[tab.length - count];
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

    @Override
    public void train(Instances inst) throws Exception {
        if (inst.numAttributes() != 0) {

            inst.setClassIndex(inst.numAttributes() - 1);
            Modelparam = new Distribution[inst.numAttributes()][inst.numClasses()];
            Enumeration<Attribute> enu = inst.enumerateAttributes();
            int attindex = 0;
            while (enu.hasMoreElements()) {
                Attribute att = enu.nextElement();
                for (int i = 0; i < inst.numClasses(); i++) {
                    switch (att.type()) {
                        case Attribute.NUMERIC:
                            Modelparam[attindex][i] = new ClassdistributionNumeric(inst, i, attindex);
                            break;

                        case Attribute.NOMINAL:
                            Modelparam[attindex][i] = new ClassdistributionNominal(inst, i, attindex);
                            break;

                        case (Attribute.STRING):
                            Modelparam[attindex][i] = new ClassdistributionNominal(inst, i, attindex);
                            break;

                        default: {
                           
                            throw new Exception("Attributetype unbekannt");
                        }

                    }

                }

                attindex++;

            }
            classparam = new double[inst.numClasses()];
            for (int i = 0; i < inst.numClasses(); i++) {
                for (int j = 0; j < inst.numInstances(); j++) {
                    if (inst.instance(j).classValue() == i) {
                        classparam[i]++;
                    }

                }

            }

            for (int i = 0; i < inst.numClasses(); i++) {
                classparam[i] /= inst.numInstances();
            }

        }
    }

    @Override
    public boolean Classify(Instance in) {

        double[][] probabilities = new double[Modelmenge.numAttributes()][Modelmenge.numClasses()];
        Enumeration<Attribute> enu = Modelmenge.enumerateAttributes();
        int attindex = 0;
        while (enu.hasMoreElements()) {
            Attribute att = enu.nextElement();

            for (int i = 0; i < Modelmenge.numClasses(); i++) {

                probabilities[att.index()][i] = Modelparam[att.index()][i].getProbs(in);

            }

            attindex++;
        }

        for (int i = 0; i < Modelmenge.numClasses(); i++) {
            for (int j = 0; j < Modelmenge.numAttributes(); j++) {
                classparam[i] *= probabilities[j][i];
            }
        }

        return (Maxindex(classparam) == in.classValue());
    }

    public int Maxindex(double[] tab) {
        double max = tab[0];
        int j = 0;
        int i = 1;
        while (i < tab.length) {
            if (max < tab[i]) {
                max = tab[i];
                j = i++;
            } else {
                i++;
            }
        }
        return j;
    }

    @Override
    public double[][] test(Instances testinst) {
        double count = 0;
        long anfangszeit = System.currentTimeMillis();;
        long endzeit;
        double[][] ausgabe = new double[1][2];
        if (testinst.numAttributes() != 0) {

            testinst.setClass(testinst.attribute(testinst.numAttributes() - 1));

            for (int i = 0; i < testinst.numInstances(); i++) {

                if (!Classify(testinst.instance(i))) {
                    count++;
                }

            }

            endzeit = System.currentTimeMillis();
            ausgabe[0][0] = (count / testinst.numInstances()) * 100;

            ausgabe[0][1] = ((endzeit - anfangszeit));
            // System.out.println(testinst);
            return ausgabe;
        } else {
            // System.out.println(testinst);
            return ausgabe;
        }

    }

    @Override
    public void BewertunginProzent() throws Exception {
         System.out.println("Parameter:" + "Anzahldurchlauf:"+this.anzahldurchlauf);
         //System.out.println("----------------------------------------------------------------------------------------");
        double count = 0;
        double max = 0;
        double[][] hilf = new double[1][2];
        double[][] hilf2 = new double[1][2];

        for (int i = 0; i < anzahldurchlauf; i++) {

            Bootstrap(Modelmenge);
            train(this.traindaten);
           // System.out.println("training NR" + " " + i + ":");
            // System.out.println("trainingsdeaten:");

            hilf = test(traindaten);

            this.trainergebnisse[i][0] = hilf[0][0];
            this.trainergebnisse[i][1] = hilf[0][1];
            //System.out.println("Fehlerquote TrainingNR" + " " + i + ":" + " " + (double)(int) (hilf[0][0]*100)/100 + "%" + "  " + "Dauer:" + " " + (int) hilf[0][1]  + "ms");

            hilf2 = test(testdaten);

            this.testergebnisse[i][0] = hilf2[0][0];
            this.testergebnisse[i][1] = hilf2[0][1];
           // System.out.println("Validierung NR" + " " + i + ":");
            //System.out.println("Validierungsngsdaten:");

           // System.out.println("Fehlerquote Validierungs NR" + " " + i + ":" + " " + (double)(int) (hilf2[0][0]*100)/100 + "%" + "  " + "Dauer:" + " " + (int) hilf2[0][1]  + "ms");
            //System.out.println("----------------------------------------------------------------------------------------");

        }

        DescriptiveStatistics stats1 = new DescriptiveStatistics();
        DescriptiveStatistics stat1 = new DescriptiveStatistics();

// Add the data from the array
        for (int i = 0; i < trainergebnisse.length; i++) {

            stats1.addValue(trainergebnisse[i][0]);
            stat1.addValue(trainergebnisse[i][1]);
        }

        double mean1 = stats1.getMean();
        double std1 = stats1.getStandardDeviation();
        double meanzeit1 = stat1.getMean();
        double stdzeit1 = stat1.getStandardDeviation();

       // System.out.println("Mittlere Felehrquote des Tainings:" + " " + (double)(int) (mean1*100)/100 + "%" + "(±" + (double)(int) ((std1 / Math.sqrt(anzahldurchlauf))*100)/100 + "%)");
        //System.out.println("Mittlere Dauer des trainings:" + " " + (int) meanzeit1  + " " + "ms" + "(±" + (int) ((stdzeit1 / Math.sqrt(anzahldurchlauf)) ) + "ms)");
        //System.out.println("--------------------------------------------------------------------------------------");

        DescriptiveStatistics stats = new DescriptiveStatistics();
        DescriptiveStatistics stat = new DescriptiveStatistics();

// Add the data from the array
        for (int i = 0; i < testergebnisse.length; i++) {

            stats.addValue(testergebnisse[i][0]);
            stat.addValue(testergebnisse[i][1]);
        }

        this.Mittlerevalidierungsquote = stats.getMean();
       this.stadartdeviationvalidierung = (int)(stats.getStandardDeviation()/Math.sqrt(anzahldurchlauf));
        this.Mittlerezeit = stat.getMean();
       this.standartdeviationtime = (int)(stat.getStandardDeviation()/Math.sqrt(anzahldurchlauf));
        ergb[1]=Mittlerevalidierungsquote;
        ergb[2]=Mittlerezeit;
        ergb[3]=(double) (int) ((stadartdeviationvalidierung / Math.sqrt(anzahldurchlauf)) * 100) / 100; 
        ergb[4]=(int) ((standartdeviationtime / Math.sqrt(anzahldurchlauf)));
  
        struct.setErgebnisse(ergb);
      /*  System.out.println("Durchnittliche Fehlerquote der Validierungsmengen:" + " " + (int) Mittlerevalidierungsquote + "%" + "(±" + (int) (stadartdeviationvalidierung / Math.sqrt(anzahldurchlauf)) + "%)");
        System.out.println("durchnittliche Dauer der Validierung :" + " " + (int) (Mittlerezeit)  + " " + "ms" + "(±" + (int) ((standartdeviationtime / Math.sqrt(anzahldurchlauf)) ) + "ms)");*/

        train(this.Modelmenge);
        hilf = test(Modelmenge);
        this.Modelergebnisse[0][0] = hilf[0][0];
        this.Modelergebnisse[0][1] = hilf[0][1];
        hilf = test(validierungsmenge);
        validierungsergebnisse[0][0] = hilf[0][0];
        validierungsergebnisse[0][1] = hilf[0][1];
       /* System.out.println("---------------------------------------------------------------------------------------");*/

       // System.out.println("Fehlerquote der  training auf dem Datensatz:" + "  " + (double)(int) (Modelergebnisse[0][0]*100)/100 + "%");
       // System.out.println("Zeit des trainings (Datensatz):" + " " + (int) (Modelergebnisse[0][1] ) + " " + "ms");
       // System.out.println("---------------------------------------------------------------------------------------");
       // System.out.println("Fehlerquote der Test:" + "  " + (double)(int) (validierungsergebnisse[0][0]*100)/100 + "%");
       // System.out.println("Zeit der Test:" + " " + (int) (validierungsergebnisse[0][1] ) + " " + "ms");

        /* Instances bestmodel=new Instances(Modelmenge,bestmodelindexen.length);
         double result;
         for(int i=0;i<bestmodelindexen.length;i++)
         {
         bestmodel.add(Modelmenge.instance(bestmodelindexen[i]));
         }
         train(bestmodel);
      
      
      
         result= test(validierungsmenge);
         System.out.println();
         System.out.println("der Beste Model  ist:");
         System.out.println("-----------------------------------------");
         System.out.println(bestmodel);
         System.out.println("mit eine Leistung von:"+"   "+result+"%");
           
           
           
           
           
         return result;

         }*/
    }
    
    public BayesNetz getStruct()
    {
     return struct;
    }
}
