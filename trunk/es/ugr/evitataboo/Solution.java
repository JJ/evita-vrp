/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.ugr.evitataboo;

/**
 *
 * @author ferguson
 */
public interface Solution {
    public Object clone();
    
    public double getCost();

    public void setCost(double cost);


}
