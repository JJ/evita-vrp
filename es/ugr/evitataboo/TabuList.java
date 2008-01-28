/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.ugr.evitataboo;

/**
 *
 * @author ferguson
 */
public interface TabuList {
    
    /**
     * Returns if a move is tabu
     * @param m The move to check
     * @return True if the movement is tabu, false if not.
     */
    public boolean isTabu(Move m);
    /**
     * Inserts the movement in the tabu list
     * @param m The move to insert
     */
    public void addMove(Move m);
    /**
     * Updates the tabu tenure. Use it at the end of each iteration
     */
    public void updateTenure();

}
