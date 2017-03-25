/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SuchAlgorihtmen;

import Netze.BayesNetz;
import weka.core.Instances;

/**
 *
 * @author Thierry
 */
public interface Search {

    public void suche();

    public void buildstruktur();

    public void repair();
    
    public int[][] getMatrix();
    public int getvernet();
}
