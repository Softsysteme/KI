/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Graphs;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Thierry
 */
public class Knoten {

    private String knotenID;
    public String label;
    public  List<Knoten> Kinder;
    private String Farbe;
    public  List<Knoten> Eltern;
    public int[][] kanten;
    int[] ElternIDs;

    int MaxKinder;
    public Knoten(String id, String l) {
        this.knotenID = id;
        this.label = l;
        Kinder = new ArrayList<>();
        Eltern = new ArrayList<>();
      this.Farbe="Weiss";
    }

    public String getID() {
        return this.knotenID;
    }

    public String getColor() {
        return Farbe;
    }

    public void addKinder(Knoten id) {
        if(!isKind(id));
        Kinder.add(id);
        //  System.out.println("je suis"+" "+this.getID()+" "+"jai ajoute "+"  "+id.getID());
    }

    public void addEltern(Knoten id) {
        if(!isParent(id))
        Eltern.add(id);
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

    public int anzahlKinder() {
        return Kinder.size();
    }

    public void removeKind(Knoten i) {
        if (Kinder.contains(i)) {
            Kinder.remove(i);
        }
        //  System.out.println("je suis"+" "+this.getID()+" "+"jai enleve "+"  "+i);
    }

    public int anzahlEltern() {
        return Eltern.size();
    }

    public void removeEltern(Knoten i) {
        Eltern.remove(i);
    }

    public boolean isKind(Knoten id) {
        return this.Kinder.contains(id);
        
    }
    
    
    public boolean isParent(Knoten i)
    {
      return this.Eltern.contains(i);  
    }

    public boolean hatEltern() {
        return (!this.Eltern.isEmpty());
    }

    public Knoten getEltern(int i) {
        return Eltern.get(i);
    }

    public Knoten getKind(int i) {
        return Kinder.get(i);
    }
    
    
    public void setMaxKinder(int max)
    {
    MaxKinder=max;
    }

    public String DisplayKinder() {
        String s = this.knotenID + "->{";
        for (int i = 0; i < Kinder.size()-1; i++) {
            s += this.Kinder.get(i).knotenID + ",";
        }
        s += this.Kinder.get(Kinder.size()-1).getID()+"}";
        return s;

    }
}
