/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ugr.evitataboo;

/**
 *
 * @author ferguson
 */
public interface Move {

    /**
     * Performs the movement
     * @param solution The solution to perform the movement
     */
    public void operateOn(Solution solution);

    /**
     * Univoque identificator of the move
     * @return An integer to identify the move
     */
    public Integer getHash();
    
    public Object clone();
    
    public void undo(Solution solution);
}
