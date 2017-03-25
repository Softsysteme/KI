/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Graphs;

/**
 *
 * @author Thierry
 */
public class kante 
{
public int src;
public int dest;
public String srclabel,destlabel;
int type;

public kante(int src, int dest,int type)
{
this.src=src;
this.dest=dest;
this.type=type;
}

public kante(int src, int dest,int type,String l1, String l2)
{
this.src=src;
this.dest=dest;
this.type=type;
this.srclabel=l1; this.destlabel=l2;
}


}
