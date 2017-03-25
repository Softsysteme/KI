/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classifiers;

import Graphs.Graph;
import Graphs.GraphMitAngabeVernetzungsgrad;
import Graphs.Knoten;
import Netze.BayesNetz;
import distributions.Distribution;
import distributions.NumericNominalDistribution;
import distributions.NominalDistribution;
import distributions.NominalNominalDistribution;
import distributions.NumericDistribution;
import distributions.NumericNumericDistribution;
import java.util.ArrayList;
import java.util.Enumeration;
import mathematik.Discretisierer;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * Test#######################
 *
 * @author Thierry
 */
public class ComplexClassifierZufall extends Discretisierer implements Classifiers {

    private Instances traindaten;
    private Instances testdaten;
    Instances Datenbank;
    private ArrayList<Distribution> list;
    private double[] Classparam;
    private int[] trainingsetindexen;
    private int[] testsetindexen;
    public GraphMitAngabeVernetzungsgrad Model;
    public int vernetzung;
    public int[] Modelsindexen;
    public Instances Modelmenge;
    public int[] validierungsindexen;
    public Instances validierungsmenge;
    int anzahldurchlauf;
    double[][] trainergebnisse;
    double[][] testergebnisse;
    double[][] Modelergebnisse;
    double[][] validierungsergebnisse;
    GraphMitAngabeVernetzungsgrad  bestemodel;
    public double Mittlerevalidierungsquote;
    public double Mittlerezeit;
    public double standartdeviationtime;
    public double stadartdeviationvalidierung;
    BayesNetz struct;
    

    public ComplexClassifierZufall(Instances inst, int anzahl) {
        super(inst);
        Datenbank = new Instances(super.getinst());
        this.vernetzung = (int) (Math.random() * 101);
        Model = new GraphMitAngabeVernetzungsgrad(inst, vernetzung);
        Model.strukturiereGraph();
       
        list = new ArrayList<>();
        Classparam = new double[inst.numInstances()];
        this.anzahldurchlauf = anzahl;
        trainergebnisse = new double[anzahldurchlauf][2];
        testergebnisse = new double[anzahldurchlauf][2];
        Modelergebnisse = new double[1][2];
        validierungsergebnisse = new double[1][2];
        struct=new BayesNetz(inst, Model);

    }

    @Override
    public void BauClassifier() {

        bootstrapvalidierungsmenge(Datenbank);
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

    @Override
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
        while (j < tab.length && tab[j] != i) {
            j++;
        }
        return j < tab.length;
    }

    @Override
    public void train(Instances inst) throws Exception {

        Knoten[] k = Model.getDieknoten();
        Enumeration<Attribute> enu = inst.enumerateAttributes();
        int attindex = 0;
        while (enu.hasMoreElements()) {
            Attribute att = enu.nextElement();
            if (k[attindex].hatEltern()) {

                switch (att.type()) {
                    case Attribute.NUMERIC: {

                        for (int i = 0; i < k[attindex].anzahlEltern(); i++) {
                            Attribute a = inst.attribute(k[attindex].getEltern(i).getID());
                            int c = a.index();
                            switch (a.type()) {
                                case Attribute.NUMERIC:
                                    list.add(attindex, (new NumericNumericDistribution(inst, attindex, c)));
                                    break;
                                case (Attribute.NOMINAL):
                                    list.add(attindex, new NumericNominalDistribution(inst, attindex, c));
                                    break;
                                case (Attribute.STRING):
                                    list.add(attindex, new NumericNominalDistribution(inst, attindex, c));
                                    break;
                                default:
                                    throw new Exception("Attributetype unbekannt");
                            }
                        }

                    }
                    break;
                    case Attribute.NOMINAL: {
                        for (int i = 0; i < k[attindex].anzahlEltern(); i++) {

                            Attribute a = inst.attribute(k[attindex].getEltern(i).getID());

                            int c = a.index();

                            switch (a.type()) {

                                case Attribute.NUMERIC:
                                    list.add(attindex, new NumericNominalDistribution(inst, attindex, c));
                                    break;
                                case (Attribute.NOMINAL):
                                    list.add(attindex, new NominalNominalDistribution(inst, attindex, c));
                                    break;
                                case (Attribute.STRING):
                                    list.add(attindex, new NominalNominalDistribution(inst, attindex, c));
                                    break;
                                default: {
                                    throw new Exception("Attributetype unbekannt");
                                }
                            }
                        }
                    }
                    break;
                }
            } else {

                switch (att.type()) {
                    case Attribute.NUMERIC:

                        list.add(attindex, new NumericDistribution(inst, attindex));
                        break;
                    case Attribute.NOMINAL:

                        list.add(attindex, new NominalDistribution(inst, attindex));
                        break;
                    case Attribute.STRING:
                        list.add(attindex, new NominalDistribution(inst, attindex));
                        break;
                    default:
                        throw new Exception("Attributetype unbekannt");

                }
            }
            attindex++;
        }

        for (int i = 0; i < inst.numClasses(); i++) {
            for (int j = 0; j < inst.numInstances(); j++) {
                if (inst.instance(j).classValue() == i) {
                    Classparam[i]++;
                }

            }

        }

        for (int i = 0; i < inst.numClasses(); i++) {
            Classparam[i] /= inst.numInstances();
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

                if (att.index() < list.size()) {
                    probabilities[att.index()][i] = list.get(att.index()).getProbs(in);
                }

            }

            attindex++;
        }

        for (int i = 0; i < Modelmenge.numClasses(); i++) {
            for (int j = 0; j < Modelmenge.numAttributes(); j++) {
                Classparam[i] *= probabilities[j][i];
            }
        }

        return (Maxindex(Classparam) == in.classValue());
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
    @SuppressWarnings("empty-statement")
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
                } else {
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
      /*  if (Model.getAnzahlkanten() != 0) {
            System.out.println("Parameter:" + "Vernetzungsprozent:" + this.vernetzung + "(" + (Model.getMoeglischeAnzahlKanten() * vernetzung) / Model.getAnzahlkanten()
                    + ")," + " " + "Anzahldurchlauf:" + this.anzahldurchlauf + "    " + "max Anzahlkanten:" + Model.getAnzahlkanten() + "(" + Model.getMoeglischeAnzahlKanten() + " )");
        } else {
            System.out.println("Parameter:" + "Vernetzungsprozent:" + this.vernetzung + "(" + (Model.getMoeglischeAnzahlKanten() * vernetzung)
                    + ")," + " " + "Anzahldurchlauf:" + this.anzahldurchlauf + "    " + "max Anzahlkanten:" + Model.getAnzahlkanten() + "(" + Model.getMoeglischeAnzahlKanten() + " )");
        }
        System.out.println("----------------------------------------------------------------------------------------");*/

        double count = 0;
        double[][] bestergeb = new double[1][2];
        double max = 100;
        double[][] hilf;
        double[][] hilf2;

        double[] erg = new double[5];

        for (int i = 0; i < anzahldurchlauf; i++) {

            Bootstrap(Modelmenge);
            train(this.traindaten);
           // System.out.println("training NR" + " " + i + ":");
            // System.out.println("trainingsdeaten:");
            hilf = new double[1][2];
            hilf2 = new double[1][2];

            hilf = test(traindaten);

            this.trainergebnisse[i][0] = hilf[0][0];
            this.trainergebnisse[i][1] = hilf[0][1];
           // System.out.println("Fehlerquote TrainingNR" + " " + i + ":" + " " + (double) (int) (this.trainergebnisse[i][0] * 100) / 100 + "%" + "  " + "Dauer:" + " " + (int) (this.trainergebnisse[i][1]) + "ms");

            hilf2 = test(testdaten);

            this.testergebnisse[i][0] = hilf2[0][0];
            this.testergebnisse[i][1] = hilf2[0][1];
            /* if(testergebnisse[i][0]<=max)
             {
             max=testergebnisse[i][0];
             bestemodel=Model;
             bestergeb[0][0]=testergebnisse[i][0];
             bestergeb[0][1]=testergebnisse[i][1];
            
             }*/

            //System.out.println("Validierung NR" + " " + i + ":");
            //System.out.println("Validierungsngsdaten:");

           // System.out.println("Fehlerquote Validierungs NR" + " " + i + ":" + " " + (double) (int) (this.testergebnisse[i][0] * 100) / 100 + "%" + "  " + "Dauer:" + " " + (int) (this.testergebnisse[i][1]) + "ms");
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

       // System.out.println("Mittlere Felehrquote des Tainings:" + " " + (double) (int) (mean1 * 100) / 100 + "%" + "(±" + (double) (int) ((std1 / Math.sqrt(anzahldurchlauf)) * 100) / 100 + "%)");
        //System.out.println("Mittlere Dauer des trainings:" + " " + (int) (meanzeit1) + " " + "ms" + "(±" + (int) ((stdzeit1 / Math.sqrt(anzahldurchlauf))) + "ms)");
        //System.out.println("--------------------------------------------------------------------------------------");

        DescriptiveStatistics stats = new DescriptiveStatistics();
        DescriptiveStatistics stat = new DescriptiveStatistics();

// Add the data from the array
        for (int i = 0; i < testergebnisse.length; i++) {

            stats.addValue(testergebnisse[i][0]);
            stat.addValue(testergebnisse[i][1]);
        }

        this.Mittlerevalidierungsquote = stats.getMean();

        this.stadartdeviationvalidierung = (int) (stats.getStandardDeviation() / Math.sqrt(anzahldurchlauf));
        this.Mittlerezeit = stat.getMean();
        this.standartdeviationtime = (int) (stat.getStandardDeviation() / Math.sqrt(anzahldurchlauf));

       // System.out.println("Mittlere Fehlerquote der Validierungsmengen:" + " " + (double) (int) (Mittlerevalidierungsquote * 100) / 100 + "%" + "(±" + (double) (int) ((stadartdeviationvalidierung / Math.sqrt(anzahldurchlauf)) * 100) / 100 + "%)");
        //System.out.println("Mittlere Dauer der Validierung :" + " " + (int) (Mittlerezeit) + " " + "ms" + "(±" + (int) ((standartdeviationtime / Math.sqrt(anzahldurchlauf))) + "ms)");
        erg[0] = vernetzung;
        erg[1] = (double) (int) (Mittlerevalidierungsquote * 100) / 100;
        erg[2] = Mittlerezeit;
        erg[3]=(double) (int) ((stadartdeviationvalidierung / Math.sqrt(anzahldurchlauf)) * 100) / 100; 
        erg[4]=(int) ((standartdeviationtime / Math.sqrt(anzahldurchlauf)));
  
        train(this.Modelmenge);
        hilf = test(Modelmenge);
        this.Modelergebnisse[0][0] = hilf[0][0];
        this.Modelergebnisse[0][1] = hilf[0][1];
        hilf = test(validierungsmenge);
        validierungsergebnisse[0][0] = hilf[0][0];
        validierungsergebnisse[0][1] = hilf[0][1];
        struct.setErgebnisse(erg);
        /* System.out.println("---------------------------------------------------------------------------------------");*/

        ///System.out.println("Fehlerquote der  training auf dem Datensatz:" + "  " + (double) (int) (Modelergebnisse[0][0] * 100) / 100 + "%");
        //System.out.println("Zeit des trainings (Datensatz):" + " " + (int) (Modelergebnisse[0][1]) + " " + "ms");
        //System.out.println("---------------------------------------------------------------------------------------");
        //System.out.println("Fehlerquote der Test:" + "  " + (double) (int) (validierungsergebnisse[0][0] * 100) / 100 + "%");
       // System.out.println("Zeit der Test:" + " " + (int) (validierungsergebnisse[0][1]) + " " + "ms");
       /* System.out.println();
         System.out.println("Beste Struktur:"+"  "+"Validierung:"+" "+(int)bestergeb[0][0]+"%"+"  "+"Zeit:"+" "+(int)bestergeb[0][1]+"ms");
         System.out.println("---------------------------------------------------------------------------------------");
         bestemodel=Model; if(bestemodel!=null) bestemodel.ToString();*/

    }
    
    public BayesNetz getStruct()
    {
    return struct;
    }

 
    

}
