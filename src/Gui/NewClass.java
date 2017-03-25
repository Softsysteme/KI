/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Gui;

import SGA.BayesFactory;
import SGA.BayesFitnessEvaluator;
import SGA.Bayescrossover;
import SGA.Geneticsimulation;
import SGA.Mutation;
import SGA.Terminason;
import SGA.TunrnierSelektion;
import Graphs.Graph;
import Graphs.GraphMitAngabeVernetzungsgrad;

import Netze.BayesNetz;
import classifiers.ComplexClassifierZufall;
import classifiers.Simpleclassifier;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFileChooser;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;

/**
 *
 * @author Thierry
 */
public class NewClass {

    public static void main(String[] args) throws FileNotFoundException, IOException, Exception {
        Graph bestemodel = null;
        int anz;
        int anziter;
        List<BayesNetz> results;
        results = new ArrayList<BayesNetz>();
        int[] tab;
        double maxFitness;
        String verfahren = null;
        ArrayList<ComplexClassifierZufall> Complexnetze = new ArrayList<>();
        Simpleclassifier Simplenetz = null;
        BayesNetz K2_Netz;
        System.out.println("Datei im ARFF-Format auswählen");
        JFileChooser dialogue = new JFileChooser();

        dialogue.showOpenDialog(null);

        // selektierte File
        String Name = dialogue.getSelectedFile().getName();
        System.out.println("Ausgewählte Datensatz:" + " " + Name);
        if (Name.contains(".arff") && (dialogue.getSelectedFile().isFile())) {
            BufferedReader reader = new BufferedReader(new FileReader(dialogue.getSelectedFile()));

            ArffReader arff = new ArffReader(reader, 1000);
            Instances data = arff.getStructure();
            data.setClassIndex(data.numAttributes() - 1);//
            Instance inst;
            while ((inst = arff.readInstance(data)) != null) {
                data.add(inst);
            }
            System.out.println("bitte geben sie die  Groeße der Startpopulation  ein (die Zahl muss groeßer 1 sein!!)");

            Scanner s = new Scanner(System.in);
            if (s == null) {
                System.out.println("keine Zahl ausgewählt");
                throw new Exception();
            }

            anziter = s.nextInt();
            if (anziter <= 1) {
                System.out.println("Sie haben eine Zahl eigegeben, die kleiner oder gleich 1 ist. Dies ist nicht zulaessig. Die Startpopulationsgroeße muss>1 sein!");
                throw new Exception();

            }

            System.out.println("bitte geben sie die Anzahl Iterationen ein");
            Scanner sc = new Scanner(System.in);
            Collection<BayesNetz> dieNetze = new ArrayList<BayesNetz>();
            anz = sc.nextInt();
            System.out.println("lädt bitte warten...");
            /* BayesNetz struct=new BayesNetz(data,1);
             
             struct.BerechneFitness();
              
             struct.getGraph().ToString();
             ;
             tab = new int[anz];*/

            int i = 0;
            long anfangszeit_MTA = 0;

            while (i < anziter) {
                ComplexClassifierZufall cc = new ComplexClassifierZufall(data, anziter);
                Complexnetze.add(cc);

                dieNetze.add(new BayesNetz(cc.getinst(), cc.Model));

                i++;
            }

            for (ComplexClassifierZufall c : Complexnetze) {
                anfangszeit_MTA = System.currentTimeMillis();
                c.BauClassifier();
                // System.out.println("Ergebnisse mit Complexclassifier");
                //System.out.println("********************************");
                c.BewertunginProzent();
                // System.out.println("-----------------------------------------------------------------------------------------------------------------------------");
                // System.out.println("                                    ");

            }

            double max = Complexnetze.get(0).Mittlerevalidierungsquote;
            double zeitmin = Complexnetze.get(0).Mittlerezeit;
            int indexbeste = 0;

            long endzeit_MTA = 0;

            for (ComplexClassifierZufall c : Complexnetze) {

                if (max > c.Mittlerevalidierungsquote) {
                    max = c.Mittlerevalidierungsquote;
                    bestemodel = c.Model;
                    indexbeste = Complexnetze.indexOf(c);
                } else {
                    if (max == c.Mittlerevalidierungsquote) {
                        if (zeitmin >= c.Mittlerezeit) {
                            max = c.Mittlerevalidierungsquote;
                            zeitmin = c.Mittlerezeit;
                            bestemodel = c.Model;
                            indexbeste = Complexnetze.indexOf(c);
                        }
                    }
                }

            }

            if (Complexnetze.get(indexbeste).Model.getAnzahlkanten() != 0) {
                System.out.println("-----------------------------------------------------------------------------------------------------");
                System.out.println();
                System.out.println("Ergebnisse mit MTA_learning ");
                System.out.println("Fitness:" + ""
                        + (double) (int) (Complexnetze.get(indexbeste).Mittlerevalidierungsquote * 100) / 100
                        + "(±" + (double) (int) (Complexnetze.get(indexbeste).stadartdeviationvalidierung * 100) / 100 + ")" + "%" + " " + "Zeit:" + "" + (int) Complexnetze.get(indexbeste).Mittlerezeit + "(±" + (int) Complexnetze.get(indexbeste).standartdeviationtime + ")" + "ms");
                System.out.println("Vernetzungsprozent:" + "" + Complexnetze.get(indexbeste).vernetzung + "(" + (Complexnetze.get(indexbeste).Model.getMoeglischeAnzahlKanten() * Complexnetze.get(indexbeste).vernetzung) / Complexnetze.get(indexbeste).Model.getAnzahlkanten() + ")" + "%" + "    " + "max Anzahlkanten:"
                        + Complexnetze.get(indexbeste).Model.getAnzahlkanten() + "(" + Complexnetze.get(indexbeste).Model.getMoeglischeAnzahlKanten() + " )");
            } else {
                System.out.println("-----------------------------------------------------------------------------------------------------");
                System.out.println();
                System.out.println("Ergebnisse mit MTA_learning ");
                System.out.println(
                        "Fitness:" + ""
                        + (double) (int) (Complexnetze.get(indexbeste).Mittlerevalidierungsquote * 100) / 100
                        + "(±" + (double) (int) (Complexnetze.get(indexbeste).stadartdeviationvalidierung * 100) / 100 + ")" + " " + "Zeit:" + "" + (int) Complexnetze.get(indexbeste).Mittlerezeit + "(±" + (int) Complexnetze.get(indexbeste).standartdeviationtime + ")" + "ms");
                System.out.println("Vernetzungsprozent:" + "" + Complexnetze.get(indexbeste).vernetzung + "(" + (Complexnetze.get(indexbeste).Model.getMoeglischeAnzahlKanten() * Complexnetze.get(indexbeste).vernetzung) + ")" + "%" + "    " + "max Anzahlkanten:"
                        + Complexnetze.get(indexbeste).Model.getAnzahlkanten() + "(" + Complexnetze.get(indexbeste).Model.getMoeglischeAnzahlKanten() + " )");
            }

            Complexnetze.get(indexbeste).Model.ToString();
            endzeit_MTA = System.currentTimeMillis();
            System.out.println("Gesamte Ausführungszet MTA_learning:" + (endzeit_MTA - anfangszeit_MTA) + " " + "ms");
            results.add(Complexnetze.get(indexbeste).getStruct());
            maxFitness = ((Complexnetze.get(indexbeste).Mittlerevalidierungsquote * 100) / 100);
            verfahren = "MTA_learning";

            System.out.println("---------------------------------------------------------------------------------------------------------------------");

            System.out.println();
            long anfangszeit_NB = System.currentTimeMillis();
            long endzeit_NB = 0;

            Simplenetz = new Simpleclassifier(new BayesNetz(data, 0), anz);

            Simplenetz.BauClassifier();
            System.out.println("Ergebnisse mit NaiveBayes");

            System.out.println("Vernetzungsprozent:" + "" + (100 * (Simplenetz.getinst().numAttributes() - 1)) / Complexnetze.get(indexbeste).Model.getMaxAnzahlkanten() + "%");
            Simplenetz.BewertunginProzent();

            System.out.println("Fitness NaiveBayes:" + ""
                    + (double) (int) (Simplenetz.Mittlerevalidierungsquote * 100) / 100
                    + "(±" + (double) (int) (Simplenetz.stadartdeviationvalidierung * 100) / 100 + ")" + " " + "Zeit:" + "" + (int) Simplenetz.Mittlerezeit + "(±" + (int) Simplenetz.standartdeviationtime + ")" + "ms");

            Simplenetz.Model.ToString();
            double[] er = new double[3];
            er = Simplenetz.getStruct().getErgebnisse();
            endzeit_NB = System.currentTimeMillis();
            System.out.println("Gesamte Ausführungszet NaiveBayes:" + (endzeit_NB - anfangszeit_NB) + " " + "ms");

            results.add(Simplenetz.getStruct());

            if (maxFitness > ((Simplenetz.Mittlerevalidierungsquote * 100) / 100)) {
                maxFitness = (Simplenetz.Mittlerevalidierungsquote * 100) / 100;
                verfahren = "NaiveBayes";
            }
            /* for(int l=0;l<er.length;l++)
             System.out.println(er[l]);
             /* Simpleclassifier c = new Simpleclassifier(data, 5);
             c.BauClassifier();
             c.BewertunginProzent();// Get a DescriptiveStatistics instance*/

            System.out.println("---------------------------------------------------------------------------------------------------------------------");
            System.out.println();
            System.out.println("Ergebnisse mit K2:");
            System.out.println();
            long anfangszeit_K2 = System.currentTimeMillis();
            long endzeit_K2 = 0;
            K2_Netz = new BayesNetz(new BayesNetz(data, 1));
            er = K2_Netz.getErgebnisse();
            endzeit_K2 = System.currentTimeMillis();
            double f1 = K2_Netz.getFitness();
            System.out.println("Vernetzung:" + " " + K2_Netz.getVernetzung() + "%" + "    " + "Fitness :" + "" + f1 + "(±" + K2_Netz.geterb(3) + ")" + " " + "Zeit:" + (int) K2_Netz.getZeit() + "(±" + K2_Netz.geterb(4) + ")" + "" + "ms");
            K2_Netz.getGraph().ToString();
            System.out.println("Gesamte Ausführungszet K2:" + (endzeit_K2 - anfangszeit_K2) + " " + "ms");

            results.add(K2_Netz);
            if (maxFitness > f1) {
                maxFitness = f1;
                verfahren = "K2";
            }

            System.out.println("---------------------------------------------------------------------------------------------------------------------");

            //  System.out.println();
            // System.out.println("Ergebnisse mit Ci:");
            // System.out.println();
            //BayesNetz Ci_Netz=new BayesNetz(new BayesNetz(data,2));
            //System.out.println("Vernetzung:"+Ci_Netz.getVernetzung()+"%"+"    "+"Fitness :" + ""+Ci_Netz.getFitness());
            //Ci_Netz.getGraph().ToString();
            //System.out.println("---------------------------------------------------------------------------------------------------------------------");
            Random r;
            r = new Random();
            long anfangszeit_SGA = System.currentTimeMillis();

            AbstractCandidateFactory Factory = new BayesFactory<BayesNetz>();

            List<EvolutionaryOperator<BayesNetz>> operators = new LinkedList<EvolutionaryOperator<BayesNetz>>();
            operators.add(new Bayescrossover());
            operators.add(new Mutation(new ConstantGenerator<Probability>(new Probability(0.02)), new ConstantGenerator(10)));
            EvolutionaryOperator<BayesNetz> pipeline = new EvolutionPipeline<BayesNetz>(operators);
            FitnessEvaluator<BayesNetz> fitness = new BayesFitnessEvaluator();
            SelectionStrategy<BayesNetz> select = new TunrnierSelektion(new Probability(0.6));
            System.out.println("please wait while simulating  evolution...");
            System.out.println();

            long endzeit_SGA = 0;

            EvolutionEngine<BayesNetz> engine = new Geneticsimulation((BayesFactory) Factory, pipeline, fitness, select, r);

            engine.addEvolutionObserver(new EvolutionObserver<BayesNetz>() {

                @Override
                public void populationUpdate(PopulationData<? extends BayesNetz> data) {
                    System.out.println("Generation" + " " + (data.getGenerationNumber() + 1) + ":");
                    System.out.println("Fitness:" + " " + data.getBestCandidate().getFitness() + "," + " " + "Dauer:" + " " + data.getBestCandidate().geterb(3) + " " + "ms");
             ///System.out.println("Beste Netz:");

                    //data.getBestCandidate().getGraph().ToString();
                    System.out.println("-------------------------------------------------------------------------------");
                }

            });
            BayesNetz result = new BayesNetz(engine.evolve(dieNetze.size(), 1, dieNetze, new Terminason(anz)));
            endzeit_SGA = System.currentTimeMillis();
            System.out.println("Ergebnisse der Evolution:");
            double f = result.getFitness();
            if (maxFitness > f) {
                maxFitness = f;
                verfahren = "SGA";
            }

            er = result.getErgebnisse();

            System.out.println("Vernetzung:" + " " + result.getVernetzung() + "%" + "  " + "Fittness:" + " " + f + "(±" + result.geterb(3) + ")" + " " + "Zeit:" + " " + (int) result.getZeit() + "(±" + result.geterb(4) + ")" + " " + "ms");
            result.getGraph().ToString();
            System.out.println("Gesamte Dauer von SGA:" + " " + (endzeit_SGA - anfangszeit_SGA) + " " + "ms");
            results.add(result);

            System.out.println("---------------------------------------------------------------------------------------------------------------------");

        } else {
            System.out.println("ungültige Dateiformat");

        }

        switch (verfahren) {
            case "MTA_learning": {
                System.out.println("Bester Verfahren:" + " " + verfahren);
                System.out.println("gelernte Struktur: (" + results.get(0).getVernetzung() + "% Vernetzung)" + ":");
                results.get(0).getGraph().ToString();

                break;
            }
            case "NaiveBayes": {
                System.out.println("Bester Verfahren:" + " " + verfahren);
                System.out.println("gelernte Struktur: (" + results.get(1).getVernetzung() + "% Vernetzung)" + " " + verfahren + ":");
                results.get(1).getGraph().ToString();
                break;
            }

            case "K2": {
                System.out.println("Bester Verfahren:" + " " + verfahren);
                System.out.println("gelernte Struktur: (" + results.get(2).getVernetzung() + "% Vernetzung)" + " " + verfahren + ":");
                results.get(2).getGraph().ToString();
                break;
            }

            case "SGA": {
                System.out.println("Bester Verfahren:" + " " + verfahren);
                System.out.println("gelernte Struktur: (" + results.get(3).getVernetzung() + "% Vernetzung)" + " " + verfahren + ":");
                results.get(3).getGraph().ToString();
                break;
            }
            default:
                System.out.println("weiss nicht");
        }

    }
}
